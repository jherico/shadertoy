package org.saintandreas.shadertoy;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.saintandreas.gl.IndexedGeometry;
import org.saintandreas.gl.OpenGL;
import org.saintandreas.gl.shaders.Program;
import org.saintandreas.gl.shaders.Shader;
import org.saintandreas.math.Vector3f;
import org.saintandreas.resources.ResourceManager;
import org.saintandreas.shadertoy.data.Shaders;

public class ShaderToyWindow extends RenderWindow {
  public static final String SHADER_HEADER = ""
      + "#version 330\n"
      + "uniform vec3      iResolution;           // viewport resolution (in pixels)\n"
      + "uniform float     iGlobalTime;           // shader playback time (in seconds)\n"
      + "uniform float     iChannelTime[4];       // channel playback time (in seconds)\n"
      + "uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)\n"
      + "uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click\n"
      + "uniform vec4      iDate;                 // (year, month, day, time in seconds)\n"
      + "uniform float     iSampleRate;           // sound sample rate (i.e., 44100)\n\n"
      + "out vec4 FragColor;\n";

  public static final String UNIFORM_RESOLUTION = "iResolution";
  public static final String UNIFORM_GLOBALTIME = "iGlobalTime";
  public static final String UNIFORM_CHANNEL_TIME = "iChannelTime";
  public static final String UNIFORM_CHANNEL_RESOLUTION = "iChannelResolution";
  public static final String UNIFORM_MOUSE_COORDS = "iMouse";
  public static final String UNIFORM_DATE = "iDate";
  public static final String UNIFORM_SAMPLE_RATE = "iSampleRate";

  private IndexedGeometry geometry;
  private Shader vertexShader;
  private Shader fragmentShader;
  private Program program;
  private Vector3f res;
  private long start;

  @Override
  protected void onCreate() {
    super.onCreate();
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_BLEND);
    geometry = OpenGL.makeTexturedQuad(1.0f, Measure.valueOf(2.0f, SI.METER));
    vertexShader = new Shader(GL_VERTEX_SHADER,
        ResourceManager.getAsString(Shaders.VERTEX_SHADER));
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    glViewport(0, 0, width, height);
    res = new Vector3f(width, height, 0);
  }

  // uniform vec3 iResolution; // viewport resolution (in pixels)
  // uniform float iGlobalTime; // shader playback time (in seconds)
  // uniform float iChannelTime[4]; // channel playback time (in seconds)
  // uniform vec3 iChannelResolution[4]; // channel resolution (in pixels)
  // uniform vec4 iMouse; // mouse pixel coords. xy: current (if MLB down), zw:
  // click
  // uniform samplerXX iChannel0..3; // input channel. XX = 2D/Cube
  // uniform vec4 iDate; // (year, month, day, time in seconds)
  // uniform float iSampleRate; // sound sample rate (i.e., 44100)

  @Override
  public void drawFrame() {
    glClear(GL_COLOR_BUFFER_BIT);
    if (null == program) {
      return;
    }
    long elapsed = System.currentTimeMillis() - start;
    float time = elapsed;
    time /= 1000.0f;
    program.use();
    program.setUniform(UNIFORM_RESOLUTION, res);
    program.setUniform(UNIFORM_GLOBALTIME, time);
    geometry.bindVertexArray();
    geometry.draw();
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public void setFragmentSource(String newFragmentShaderSource) {
    try {
      newFragmentShaderSource = newFragmentShaderSource.replaceAll("\\bgl_FragColor\\b", "FragColor");
      Shader fs = new Shader(GL_FRAGMENT_SHADER, SHADER_HEADER + newFragmentShaderSource);
      fs.compile();
      Program p = new Program(vertexShader, fs);
      p.link();
      program = p;
      start = System.currentTimeMillis();
    } catch (Exception e) {
      
    }
  }
}
