package org.saintandreas.shadertoy.data;

import org.saintandreas.resources.BasicResource;
import org.saintandreas.resources.Resource;

public enum ChannelInput {
  TEX00(Type.TEXTURE, new TextureResource("tex00.jpg")), //
  TEX01(Type.TEXTURE, new TextureResource("tex01.jpg")), //
  TEX02(Type.TEXTURE, new TextureResource("tex02.jpg")), //
  TEX03(Type.TEXTURE, new TextureResource("tex03.jpg")), //
  TEX04(Type.TEXTURE, new TextureResource("tex04.jpg")), //
  TEX05(Type.TEXTURE, new TextureResource("tex05.jpg")), //
  TEX06(Type.TEXTURE, new TextureResource("tex06.jpg")), //
  TEX07(Type.TEXTURE, new TextureResource("tex07.jpg")), //
  TEX08(Type.TEXTURE, new TextureResource("tex08.jpg")), //
  TEX09(Type.TEXTURE, new TextureResource("tex09.jpg")), //
  TEX10(Type.TEXTURE, new TextureResource("tex10.png")), //
  TEX11(Type.TEXTURE, new TextureResource("tex11.png")), //
  TEX12(Type.TEXTURE, new TextureResource("tex12.png")), //
  TEX14(Type.TEXTURE, new TextureResource("tex14.png")), //
  TEX15(Type.TEXTURE, new TextureResource("tex15.png")), //
  TEX16(Type.TEXTURE, new TextureResource("tex16.png")), //

  VID00(Type.VIDEO, new VideoResource("vid00.mp4"), new VideoResource("vid00.gif")), //
  VID01(Type.VIDEO, new VideoResource("vid01.mp4"), new VideoResource("vid01.gif")), //
  VID02(Type.VIDEO, new VideoResource("vid02.mp4"), new VideoResource("vid02.gif")), //

  CUBE00(Type.CUBEMAP, new CubemapResource("cube00_0.jpg")), //
  CUBE01(Type.CUBEMAP, new CubemapResource("cube01_0.png")), //
  CUBE02(Type.CUBEMAP, new CubemapResource("cube02_0.jpg")), //
  CUBE03(Type.CUBEMAP, new CubemapResource("cube03_0.png")), //
  CUBE04(Type.CUBEMAP, new CubemapResource("cube04_0.png")), //
  CUBE05(Type.CUBEMAP, new CubemapResource("cube05_0.png")), //

  AUD00(Type.AUDIO, new AudioResource("mzk00.mp3")), //
  AUD01(Type.AUDIO, new AudioResource("mzk01.mp3")), //
  AUD02(Type.AUDIO, new AudioResource("mzk02.mp3")), //
  AUD03(Type.AUDIO, new AudioResource("mzk03.mp3")), //
  AUD04(Type.AUDIO, new AudioResource("mzk04.mp3")), //
  AUD05(Type.AUDIO, new AudioResource("mzk05.mp3")), //
  AUD06(Type.AUDIO, new AudioResource("mzk06.mp3"));//

  public static final ChannelInput TEXTURES[] = { //
  TEX00,//
      TEX01,//
      TEX02,//
      TEX03,//
      TEX04,//
      TEX05,//
      TEX06,//
      TEX07,//
      TEX08,//
      TEX09,//
      TEX10,//
      TEX11,//
      TEX12,//
      TEX14,//
      TEX15,//
      TEX16,//
  };

  public static final ChannelInput CUBEMAPS[] = { //
  CUBE00, //
      CUBE01, //
      CUBE02, //
      CUBE03, //
      CUBE04, //
      CUBE05, //
  };

  public static final ChannelInput AUDIOS[] = { //
  AUD00, //
      AUD01, //
      AUD02, //
      AUD03, //
      AUD04, //
      AUD05, //
      AUD06, //
  };

  public static final ChannelInput VIDEOS[] = { //
  VID00, //
      VID01, //
      VID02, //
  };

  public enum Type {
    TEXTURE, CUBEMAP, AUDIO, VIDEO,
  };

  private static class TextureResource extends BasicResource {
    TextureResource(String name) {
      super("textures/" + name);
    }
  }

  private static class VideoResource extends BasicResource {
    VideoResource(String name) {
      super("videos/" + name);
    }
  }

  private static class CubemapResource extends BasicResource {
    CubemapResource(String name) {
      super("cubemaps/" + name);
    }
  }

  private static class AudioResource extends BasicResource {
    AudioResource(String name) {
      super("audio/" + name);
    }
  }

  public final Type type;
  public final Resource resource;
  public final Resource thumbnail;

  ChannelInput(Type type, Resource resource) {
    this(type, resource, resource);
  }

  ChannelInput(Type type, Resource resource, Resource thumbnail) {
    this.type = type;
    this.resource = resource;
    this.thumbnail = thumbnail;
  }

}
