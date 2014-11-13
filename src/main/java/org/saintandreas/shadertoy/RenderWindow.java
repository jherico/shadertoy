package org.saintandreas.shadertoy;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

public abstract class RenderWindow {
  private GLContext glContext = new GLContext();
  private ContextAttribs contextAttributes = new ContextAttribs(3, 2)
      .withProfileCore(true);
  private PixelFormat pixelFormat = new PixelFormat();
  private int frameCount = 0;
  private int lastFpsStartFrame = 0;
  private long lastFpsStartTime = -1;
  protected float fps = 0;

  public void create(int width, int height, int x, int y) {
    try {
      Display.setDisplayMode(new DisplayMode(width, height));
      Display.setVSyncEnabled(true);
      Display.create(pixelFormat, contextAttributes);
      if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
        Display.setLocation(x,  y);
      }
      glGetError();
      GLContext.useContext(glContext, false);
    } catch (LWJGLException ex) {
      throw new RuntimeException(ex);
    }
    onCreate();
    onResize(width, height);
  }

  public void create(int width, int height) {
    create(width, height, Integer.MIN_VALUE, Integer.MIN_VALUE);
  }

  public void onFrame() {
    ++frameCount;
    if (Display.wasResized()) {
      onResize(Display.getWidth(), Display.getHeight());
    }
    drawFrame();
    finishFrame();

    long now = System.currentTimeMillis();
    if (lastFpsStartTime < 0) {
      lastFpsStartTime = now;
    }
    long elapsed = now - lastFpsStartTime;
    if (elapsed >= 5000) {
      float frames = frameCount - lastFpsStartFrame;
      float elapsedSeconds = elapsed / 1000.0f;
      fps = frames / elapsedSeconds;
      lastFpsStartTime = now;
      lastFpsStartFrame = frameCount;
      logFps();
    }
  }

  protected void logFps() {
    System.out.println(String.format("FPS: %f", fps));
  }

  public void destroy() {
    onDestroy();
    Display.destroy();
  }

  protected void finishFrame() {
    Display.update();
  }

  protected abstract void drawFrame();

  protected void onDestroy() {
  }

  protected void onCreate() {
  }

  protected void onResize(int width, int height) {
  }
}
