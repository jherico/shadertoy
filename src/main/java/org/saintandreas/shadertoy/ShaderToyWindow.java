package org.saintandreas.shadertoy;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import org.saintandreas.gl.IndexedGeometry;
import org.saintandreas.gl.MatrixStack;
import org.saintandreas.gl.OpenGL;
import org.saintandreas.gl.shaders.Program;
import org.saintandreas.gl.shaders.Shader;
import org.saintandreas.gl.textures.Texture;
import org.saintandreas.math.Quaternion;
import org.saintandreas.math.Vector3f;
import org.saintandreas.resources.Images;
import org.saintandreas.resources.Resource;
import org.saintandreas.resources.ResourceManager;
import org.saintandreas.shadertoy.data.ChannelInput;
import org.saintandreas.shadertoy.data.Shaders;

import com.oculusvr.capi.Posef;

public class ShaderToyWindow extends RiftWindow {
  public static final String SHADER_HEADER = ""
      + "#version 330\n"
      + "uniform vec3      iResolution;           // viewport resolution (in pixels)\n"
      + "uniform float     iGlobalTime;           // shader playback time (in seconds)\n"
      + "uniform float     iChannelTime[4];       // channel playback time (in seconds)\n"
      + "uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)\n"
      + "uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click\n"
      + "uniform vec4      iDate;                 // (year, month, day, time in seconds)\n"
      + "uniform float     iSampleRate;           // sound sample rate (i.e., 44100)\n"
      + "uniform vec3      iPos; // Head position\n"
      + "uniform sampler2D iChannel0;\n"
      + "uniform sampler2D iChannel1;\n"
      + "uniform sampler2D iChannel2;\n"
      + "uniform sampler2D iChannel3;\n"
      + "\n"
      + "in vec3 iDir; // Direction from viewer\n"
      + "out vec4 FragColor;\n";

  public static final String UNIFORM_RESOLUTION = "iResolution";
  public static final String UNIFORM_GLOBALTIME = "iGlobalTime";
  public static final String UNIFORM_CHANNEL_TIME = "iChannelTime";
  public static final String UNIFORM_CHANNEL_RESOLUTION = "iChannelResolution";
  public static final String UNIFORM_MOUSE_COORDS = "iMouse";
  public static final String UNIFORM_DATE = "iDate";
  public static final String UNIFORM_SAMPLE_RATE = "iSampleRate";
  public static final String UNIFORM_POSITION = "iPos";

  private IndexedGeometry geometry;
  private Shader vertexShader;
  private Shader fragmentShader;
  private Program program;
  private org.saintandreas.gl.textures.Texture channels[] = new org.saintandreas.gl.textures.Texture[4];
  private Vector3f res;
  private long start;

  public ShaderToyWindow() {
    MatrixStack.MODELVIEW.lookat(new Vector3f(0, 0, -1), Vector3f.ZERO, Vector3f.UNIT_Y);
  }
    
  @Override
  protected void onCreate() {
    super.onCreate();
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_BLEND);
    glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    geometry = OpenGL.makeColorCube();
    vertexShader = new Shader(GL_VERTEX_SHADER,
        ResourceManager.getAsString(Shaders.VERTEX_SHADER));
    res = new Vector3f(eyeTextures[0].Header.TextureSize.w, eyeTextures[0].Header.TextureSize.h, 0);
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    glViewport(0, 0, width, height);
  }

  private static Map<Resource, Texture> TEXTURE_MAP = new HashMap<>();
  
  public static synchronized Texture getTexture2d(Resource resource) {
    if (!TEXTURE_MAP.containsKey(resource)) {
      Texture texture = new Texture(GL_TEXTURE_2D);
      texture.bind();
      texture.parameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
      texture.parameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      texture.parameter(GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
      texture.parameter(GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
      texture.parameter(GL_TEXTURE_WRAP_R, GL_MIRRORED_REPEAT);
      texture.loadImageData(Images.load(resource), GL_TEXTURE_2D);
      texture.unbind();
      TEXTURE_MAP.put(resource, texture);
    }
    return TEXTURE_MAP.get(resource);
  }
  
  public void setTextureSource(ChannelInput result, int index) {
    assert(index >= 0);
    assert(index < channels.length);
    switch (result.type) {
    case TEXTURE:
      channels[index] = getTexture2d(result.resource);
      break;

    case CUBEMAP:
    case VIDEO:
    case AUDIO:
    default:
      channels[index] = null;
    }
    assert(0 == glGetError());
    updateUniforms();
  }
  
  public void setFragmentSource(String newFragmentShaderSource) {
    try {
      newFragmentShaderSource = newFragmentShaderSource.replaceAll("\\bgl_FragColor\\b", "FragColor");
      newFragmentShaderSource = newFragmentShaderSource.replaceAll("\\btexture2D\\b", "texture");
      newFragmentShaderSource = SHADER_HEADER + newFragmentShaderSource; 
      Shader fs = new Shader(GL_FRAGMENT_SHADER, newFragmentShaderSource);
      fs.compile();

      Program p = new Program(vertexShader, fs);
      p.link();
      if (null != fragmentShader) {
      }
      fragmentShader = fs;
      program = p;
      start = System.currentTimeMillis();
    } catch (Exception e) {

    }
    updateUniforms();
  }
  
  private void updateUniforms() {
    if (null != program) {
      program.use();
      program.setUniform(UNIFORM_RESOLUTION, res);
      for (int i = 0; i < 4; ++i) {
        program.setUniform("iChannel" + i, i);
      }
      Program.clear();
    }
    assert(0 == glGetError());
  }

  @Override
  protected void renderScene(Posef pose) {
    glClear(GL_COLOR_BUFFER_BIT);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);
    if (null == program) {
      return;
    }

    long elapsed = System.currentTimeMillis() - start;
    float time = elapsed;
    time /= 1000.0f;
    program.use();
    OpenGL.bindProjection(program);
    program.setUniform(UNIFORM_GLOBALTIME, time);
    program.setUniform(UNIFORM_POSITION, RiftUtils.toVector3f(pose.Position));
    for (int i = 0; i < 4; ++i) {
      if (null != channels[i]) {
        glActiveTexture(GL_TEXTURE0 + i);
        channels[i].bind();
      }
    }

    MatrixStack mv = MatrixStack.MODELVIEW;
    mv.withPush(()->{
      Quaternion q = mv.getRotation();
      mv.identity().rotate(q);
      OpenGL.bindModelview(program);
    });
    geometry.bindVertexArray();
    geometry.draw();
  }
}
