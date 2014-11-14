package org.saintandreas.shadertoy;

import static com.oculusvr.capi.OvrLibrary.ovrDistortionCaps.*;
import static com.oculusvr.capi.OvrLibrary.ovrRenderAPIType.*;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.SharedDrawable;
import org.saintandreas.gl.FrameBuffer;
import org.saintandreas.gl.MatrixStack;
import org.saintandreas.math.Matrix4f;

import com.oculusvr.capi.EyeRenderDesc;
import com.oculusvr.capi.FovPort;
import com.oculusvr.capi.FrameTiming;
import com.oculusvr.capi.Hmd;
import com.oculusvr.capi.OvrLibrary;
import com.oculusvr.capi.OvrSizei;
import com.oculusvr.capi.OvrVector2i;
import com.oculusvr.capi.OvrVector3f;
import com.oculusvr.capi.Posef;
import com.oculusvr.capi.RenderAPIConfig;
import com.oculusvr.capi.Texture;
import com.oculusvr.capi.TextureHeader;
import com.sun.jna.Pointer;

public abstract class RiftWindow extends RenderWindow {
  protected Hmd hmd;
  private boolean debugRift = false;
  private final OvrVector3f eyeOffsets[] = (OvrVector3f[]) new OvrVector3f().toArray(2);
  protected volatile Texture eyeTextures[] = (Texture[]) new Texture().toArray(2);
  protected volatile Posef[] poses = (Posef[]) new Posef().toArray(2);

  private final FrameBuffer frameBuffers[][] = new FrameBuffer[2][2];
  private final Matrix4f projections[] = new Matrix4f[2];
  private int writeFramebufferIndex = 0;
  private int distortionFrameCount = -1;
  private boolean enableDynamicScaling = true;
  private boolean enableAsyncTimewarp = false;
  private float minDynamicScale = 0.25f;
  private float dynamicScale = 1.0f;
  private double frameBudget = 0;
  private Accumulator frameTimes = new Accumulator();
  private SharedDrawable renderContext;
  private Object lock = new Object();

  RiftWindow() {
    hmd = Hmd.create(0);
    if (null == hmd) {
      hmd = Hmd.createDebug(OvrLibrary.ovrHmdType.ovrHmd_DK1);
      debugRift = true;
    }
    if (null == hmd) {
      throw new IllegalStateException("Unable to initialize HMD");
    }

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
    }
    hmd.configureTracking(ovrTrackingCap_Orientation | ovrTrackingCap_Position, 0);

    for (int eye = 0; eye < 2; ++eye) {
      FovPort fov = hmd.DefaultEyeFov[eye];
      projections[eye] = RiftUtils.toMatrix4f(Hmd
          .getPerspectiveProjection(hmd.DefaultEyeFov[eye], 0.1f, 1000000f, true));

      Texture texture = eyeTextures[eye];
      TextureHeader header = texture.Header;
      header.API = ovrRenderAPI_OpenGL;
      header.TextureSize = hmd.getFovTextureSize(eye, fov, 1.0f);
      header.RenderViewport.Size = new OvrSizei();
      header.RenderViewport.Pos = new OvrVector2i(0, 0);
    }
  }

  public void create() {
    if (debugRift) {
      create(hmd.Resolution.w, hmd.Resolution.h);
    } else {
      System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
      int posx = hmd.WindowsPos.x;
      int posy = hmd.WindowsPos.y;
      if ("MONOLITH8".equals(System.getenv("COMPUTERNAME"))) {
        posx = 0;
        posy = -1080;
      }
      create(hmd.Resolution.w, hmd.Resolution.h, posx, posy);
    }
  }

  @Override
  protected void onCreate() {
    long nativeWindow = RiftUtils.getNativeWindow();
    OvrLibrary.INSTANCE.ovrHmd_AttachToWindow(hmd, Pointer.createConstant(nativeWindow), null, null);

    RenderAPIConfig rc = new RenderAPIConfig();
    rc.Header.RTSize = hmd.Resolution;
    rc.Header.Multisample = 1;

    int distortionCaps = 0 //
        | ovrDistortionCap_Chromatic //
        | ovrDistortionCap_TimeWarp //
        | ovrDistortionCap_Vignette;

    for (int i = 0; i < rc.PlatformData.length; ++i) {
      rc.PlatformData[i] = Pointer.createConstant(0);
    }

    EyeRenderDesc[] eyeRenderDescs = hmd.configureRendering(rc, distortionCaps, hmd.DefaultEyeFov);

    if (enableAsyncTimewarp) {
      try {
        Drawable distortionContext = Display.getDrawable();
        distortionContext.releaseContext();
        renderContext = new SharedDrawable(distortionContext);
        renderContext.makeCurrent();
        new Thread(() -> {
          try {
            distortionContext.makeCurrent();
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
          distortionLoop();
        }).start();
      } catch (LWJGLException e) {
        throw new IllegalStateException(e);
      }
    }
    for (int eye = 0; eye < 2; ++eye) {
      this.eyeOffsets[eye].x = eyeRenderDescs[eye].HmdToEyeViewOffset.x;
      this.eyeOffsets[eye].y = eyeRenderDescs[eye].HmdToEyeViewOffset.y;
      this.eyeOffsets[eye].z = eyeRenderDescs[eye].HmdToEyeViewOffset.z;
      TextureHeader eth = eyeTextures[eye].Header;
      for (int i = 0; i < 2; ++i) {
        frameBuffers[eye][i] = new FrameBuffer(eth.TextureSize.w, eth.TextureSize.h);
      }
      eyeTextures[eye].TextureId = frameBuffers[eye][1].getTexture().id;
    }
  }

  private void distortionLoop() {
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    while (true) {
      FrameTiming frameTiming = hmd.beginFrame(distortionFrameCount);
      frameBudget = (frameTiming.NextFrameSeconds - frameTiming.ThisFrameSeconds);
      if (0 != frameTiming.TimewarpPointSeconds) {
        Hmd.waitTillTime(frameTiming.TimewarpPointSeconds - 0.005);
      }
      synchronized (lock) {
        hmd.endFrame(poses, eyeTextures);
      }
      ++distortionFrameCount;
    }
  }

  @Override
  protected final void drawFrame() {
    Posef renderPoses[] = hmd.getEyePoses(distortionFrameCount, eyeOffsets);
    if (!enableAsyncTimewarp) {
      FrameTiming frameTiming = hmd.beginFrame(distortionFrameCount);
      frameBudget = (frameTiming.NextFrameSeconds - frameTiming.ThisFrameSeconds);
    }
    double frameStartTime = Hmd.getTimeInSeconds();

    for (int i = 0; i < 2; ++i) {
      OvrSizei size = eyeTextures[i].Header.TextureSize;
      int eye = hmd.EyeRenderOrder[i];
      Posef pose = renderPoses[eye];
      MatrixStack.PROJECTION.set(projections[eye]);
      MatrixStack mv = MatrixStack.MODELVIEW;
      mv.withPush(() -> {
        mv.preTranslate(RiftUtils.toVector3f(pose.Position).mult(-1));
        mv.preRotate(RiftUtils.toQuaternion(pose.Orientation).inverse());
        frameBuffers[eye][writeFramebufferIndex].activate();
        glViewport(0, 0, (int) (size.w * dynamicScale), (int) (size.h * dynamicScale));
        renderScene(poses[eye]);
        frameBuffers[eye][writeFramebufferIndex].deactivate();
      });
    }
    glFinish();
    float frameActual = (float) (Hmd.getTimeInSeconds() - frameStartTime); 
    frameTimes.add(frameActual);

    synchronized (this) {
      for (int i = 0; i < 2; ++i) {
        // This doesn't work as it breaks the contiguous nature of the array
        // FIXME there has to be a better way to do this
        poses[i].Orientation = renderPoses[i].Orientation;
        poses[i].Position = renderPoses[i].Position;
        poses[i].write();
        OvrSizei size = eyeTextures[i].Header.TextureSize;
        eyeTextures[i].TextureId = frameBuffers[i][writeFramebufferIndex].getTexture().id;
        eyeTextures[i].Header.RenderViewport.Size.w = (int) (size.w * dynamicScale);
        eyeTextures[i].Header.RenderViewport.Size.h = (int) (size.h * dynamicScale);
        eyeTextures[i].write();
      }
    }

    if (!enableAsyncTimewarp) {
      hmd.endFrame(poses, eyeTextures);
    }

    writeFramebufferIndex = (0 == writeFramebufferIndex) ? 1 : 0;
    if (enableDynamicScaling && 0 != frameBudget && frameTimes.count() > 10) {
      float frameAverage = frameTimes.average();
      if (frameAverage > frameBudget) {
        dynamicScale = Math.max(minDynamicScale, dynamicScale * 0.8f);
      } else if ((frameAverage * 1.1) < frameBudget) {
        dynamicScale = Math.min(1.0f, dynamicScale * 1.05f);
      }
    }

    // Don't render at more than twice the refresh rate.
    if (enableAsyncTimewarp) {
      if (frameActual < frameBudget / 2.0) {
        Hmd.waitTillTime(Hmd.getTimeInSeconds() + frameBudget / 2.0);
      }
    }
  }

  @Override
  protected void logFps() {
    if (fps < 70) {
      dynamicScale = Math.max(minDynamicScale, dynamicScale * 0.8f);
    } 
    System.out.println(
        String.format("FPS: %f, Budget: %f, Average: %f, Scale: %f", 
            fps, (float) frameBudget, frameTimes.average(), dynamicScale));
    frameTimes.reset();
  }

  protected abstract void renderScene(Posef pose);

  @Override
  protected void finishFrame() {
    // Display update combines both input processing and
    // buffer swapping. We want only the input processing
    // so we have to call processMessages.
    // Display.processMessages();
    // Display.update();
  }
}
