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
  TEX16(Type.TEXTURE, new TextureResource("tex16.png")); //

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
