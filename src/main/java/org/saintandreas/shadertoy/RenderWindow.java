package org.saintandreas.shadertoy;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

public class RenderWindow {
  private boolean running = true;
  private GLContext glContext = new GLContext();
  private int width = 800, height = 600;
  private ContextAttribs contextAttributes = new ContextAttribs();
  private PixelFormat pixelFormat = new PixelFormat();

  public RenderWindow() {
    new Thread(() -> {
      run();
    }).start();;
  }

  public void stop() {
    running = false;
  }

  private void run() {
    try {
      Display.setDisplayMode(new DisplayMode(width, height));
      Display.setVSyncEnabled(true);
      Display.create(pixelFormat, contextAttributes);
      GLContext.useContext(glContext, false);
      Mouse.create();
      Keyboard.create();
      Controllers.create();
    } catch (LWJGLException e) {
      throw new RuntimeException(e);
    }
    onCreate();
    while (running) {
      if (Display.wasResized()) {
        onResize(Display.getWidth(), Display.getHeight());
      }
      drawFrame();
      finishFrame();
    }
    onDestroy();
    Display.destroy();
  }

  protected void finishFrame() {
    Display.update();
  }

  protected void drawFrame() {
  }

  protected void onDestroy() {
  }

  protected void onCreate() {
  }

  protected void onResize(int width, int height) {
    this.width = width;
    this.height = height;
  }
}
