package org.saintandreas.shadertoy;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.io.File;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.saintandreas.gl.IndexedGeometry;
import org.saintandreas.gl.OpenGL;
import org.saintandreas.gl.app.LwjglApp;
import org.saintandreas.gl.shaders.Program;
import org.saintandreas.math.Vector3f;
import org.saintandreas.resources.BasicResource;
import org.saintandreas.resources.FilesystemResourceProvider;
import org.saintandreas.resources.ResourceManager;

public class ShaderToy extends LwjglApp {
  private static final Vector3f SIZE = new Vector3f(1000, 558, 0);
  public static final String SHADER_HEADER = 
      "uniform vec3      iResolution;           // viewport resolution (in pixels)\n" +
      "uniform float     iGlobalTime;           // shader playback time (in seconds)\n" +
      "uniform float     iChannelTime[4];       // channel playback time (in seconds)\n" +
      "uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)\n" +
      "uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click\n" +
      "uniform vec4      iDate;                 // (year, month, day, time in seconds)\n" +
      "uniform float     iSampleRate;           // sound sample rate (i.e., 44100)\n";

  IndexedGeometry mGeometry;
  Vector3f mRes = Vector3f.UNIT_XYZ;
  long mStart = System.currentTimeMillis();

  public ShaderToy() {
    ResourceManager.setProvider(new FilesystemResourceProvider(
        new File("C:\\Users\\bdavis\\Git\\shadertoy\\src\\main\\resources")));
  }
  
  @Override
  protected void setupDisplay() {
    setupDisplay(new Rectangle(1920 + 10, 200, (int)SIZE.x, (int)SIZE.y));
  }

  @Override
  protected void initGl() {
    super.initGl();
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_BLEND);
    mGeometry = OpenGL.makeTexturedQuad(1.0f, Measure.valueOf(2.0f, SI.METER));
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    glViewport(0, 0, width, height);
    mRes = new Vector3f(width, height, 0);
  }

  @Override
  protected void update() {
  }

//uniform vec3      iResolution;           // viewport resolution (in pixels)
//uniform float     iGlobalTime;           // shader playback time (in seconds)
//uniform float     iChannelTime[4];       // channel playback time (in seconds)
//uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)
//uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
//uniform samplerXX iChannel0..3;          // input channel. XX = 2D/Cube
//uniform vec4      iDate;                 // (year, month, day, time in seconds)
//uniform float     iSampleRate;           // sound sample rate (i.e., 44100)

  public static final String UNIFORM_RESOLUTION = "iResolution"; 
  public static final String UNIFORM_GLOBALTIME = "iGlobalTime"; 
  public static final String UNIFORM_CHANNEL_TIME = "iChannelTime"; 
  public static final String UNIFORM_CHANNEL_RESOLUTION = "iChannelResolution"; 
  public static final String UNIFORM_MOUSE_COORDS = "iMouse"; 
  public static final String UNIFORM_DATE = "iDate"; 
  public static final String UNIFORM_SAMPLE_RATE = "iSampleRate"; 

  @Override
  public void drawFrame() {
    OpenGL.checkError();
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    Program program = OpenGL.getProgram(
        new BasicResource("Shadertoy.vs"), 
        new BasicResource("Shadertoy.fs"));
    program.use();
    long elapsed = System.currentTimeMillis() - mStart; 
    float time = elapsed;
    time /= 1000.0f;
    program.setUniform(UNIFORM_RESOLUTION, mRes);
    program.setUniform(UNIFORM_GLOBALTIME, time);
    mGeometry.bind();
    mGeometry.draw();
  }


  public static void main(String[] args) {
    new ShaderToy().run();
  }

}
