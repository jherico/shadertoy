package org.saintandreas.shadertoy;

import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Orientation;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Position;
import static com.oculusvr.capi.OvrLibrary.ovrRenderAPIType.*;
import static com.oculusvr.capi.OvrLibrary.ovrDistortionCaps.*;

import org.lwjgl.opengl.Display;
import org.saintandreas.gl.FrameBuffer;
import org.saintandreas.gl.MatrixStack;
import org.saintandreas.math.Matrix4f;
import org.saintandreas.math.Quaternion;
import org.saintandreas.math.Vector3f;

import com.oculusvr.capi.EyeRenderDesc;
import com.oculusvr.capi.FovPort;
import com.oculusvr.capi.Hmd;
import com.oculusvr.capi.OvrLibrary;
import com.oculusvr.capi.OvrMatrix4f;
import com.oculusvr.capi.OvrQuaternionf;
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
  private final OvrVector3f eyeOffsets[] = (OvrVector3f[]) new OvrVector3f()
      .toArray(2);
  private final Texture eyeTextures[] = (Texture[]) new Texture().toArray(2);
  protected final Posef[] poses = (Posef[]) new Posef().toArray(2);
  private final FrameBuffer frameBuffers[] = new FrameBuffer[2];
  private final Matrix4f projections[] = new Matrix4f[2];
  private int frameCount = -1;

  public static Vector3f toVector3f(OvrVector3f v) {
    return new Vector3f(v.x, v.y, v.z);
  }

  public static Quaternion toQuaternion(OvrQuaternionf q) {
    return new Quaternion(q.x, q.y, q.z, q.w);
  }

  public static Matrix4f toMatrix4f(Posef p) {
    return new Matrix4f().rotate(toQuaternion(p.Orientation)).mult(
        new Matrix4f().translate(toVector3f(p.Position)));
  }

  public static Matrix4f toMatrix4f(OvrMatrix4f m) {
    return new org.saintandreas.math.Matrix4f(m.M).transpose();
  }

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
    hmd.configureTracking(ovrTrackingCap_Orientation | ovrTrackingCap_Position,
        0);

    for (int eye = 0; eye < 2; ++eye) {
      FovPort fov = hmd.DefaultEyeFov[eye];
      projections[eye] = toMatrix4f(Hmd.getPerspectiveProjection(
          hmd.DefaultEyeFov[eye], 0.1f, 1000000f, true));

      Texture texture = eyeTextures[eye];
      TextureHeader header = texture.Header;
      header.API = ovrRenderAPI_OpenGL;
      header.TextureSize = hmd.getFovTextureSize(eye, fov, 1.0f);
      header.RenderViewport.Size = header.TextureSize;
      header.RenderViewport.Pos = new OvrVector2i(0, 0);
    }
  }

  public void create() {
    if (debugRift) {
      create(hmd.Resolution.w, hmd.Resolution.h);
    } else {
      System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
      create(hmd.Resolution.w, hmd.Resolution.h, hmd.WindowsPos.x,
          hmd.WindowsPos.y);
    }
  }
  
  @Override
  protected void onCreate() {
    for (int eye = 0; eye < 2; ++eye) {
      TextureHeader eth = eyeTextures[eye].Header;
      frameBuffers[eye] = new FrameBuffer(
          eth.TextureSize.w, eth.TextureSize.h);
      eyeTextures[eye].TextureId = frameBuffers[eye].getTexture().id;
    }

    RenderAPIConfig rc = new RenderAPIConfig();
    rc.Header.RTSize = hmd.Resolution;
    rc.Header.Multisample = 1;

    int distortionCaps = 
      ovrDistortionCap_Chromatic |
      ovrDistortionCap_TimeWarp |
      ovrDistortionCap_Vignette;

    for (int i = 0; i < rc.PlatformData.length; ++i) {
      rc.PlatformData[i] = Pointer.createConstant(0);
    }

    EyeRenderDesc[] eyeRenderDescs = hmd.configureRendering(
        rc, distortionCaps, hmd.DefaultEyeFov);

    for (int eye = 0; eye < 2; ++eye) {
      this.eyeOffsets[eye].x = eyeRenderDescs[eye].HmdToEyeViewOffset.x;
      this.eyeOffsets[eye].y = eyeRenderDescs[eye].HmdToEyeViewOffset.y;
      this.eyeOffsets[eye].z = eyeRenderDescs[eye].HmdToEyeViewOffset.z;
    }
  }

  @Override
  protected final void drawFrame() {
    hmd.beginFrame(frameCount);
    Posef eyePoses[] = hmd.getEyePoses(frameCount, eyeOffsets);
    for (int i = 0; i < 2; ++i) {
      int eye = hmd.EyeRenderOrder[i];
      Posef pose = eyePoses[eye];
      MatrixStack.PROJECTION.set(projections[eye]);
      // This doesn't work as it breaks the contiguous nature of the array
      // FIXME there has to be a better way to do this
      poses[eye].Orientation = pose.Orientation;
      poses[eye].Position = pose.Position;

      MatrixStack mv = MatrixStack.MODELVIEW;
      mv.withPush(()->{
        mv.preTranslate(toVector3f(poses[eye].Position).mult(-1));
        mv.preRotate(toQuaternion(poses[eye].Orientation).inverse());
        frameBuffers[eye].activate();
        renderScene();
        frameBuffers[eye].deactivate();
      });
    }
    hmd.endFrame(poses, eyeTextures);
  }
  
  protected abstract void renderScene();

  @Override
  protected void finishFrame() {
    // Display update combines both input processing and
    // buffer swapping. We want only the input processing
    // so we have to call processMessages.
    Display.processMessages();
    // Display.update();
  }
}
