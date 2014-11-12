package org.saintandreas.shadertoy.data;

import org.saintandreas.resources.BasicResource;
import org.saintandreas.resources.Resource;

public class Data {
  private static class TextureResource extends BasicResource {
    TextureResource(String name) {
      super("textures/" + name);
    }
  }

  public static Resource TEXTURES[] = new Resource[] {
    new TextureResource("tex00.jpg"),
    new TextureResource("tex01.jpg"),
    new TextureResource("tex02.jpg"),
    new TextureResource("tex03.jpg"),
    new TextureResource("tex04.jpg"),
    new TextureResource("tex05.jpg"),
    new TextureResource("tex06.jpg"),
    new TextureResource("tex07.jpg"),
    new TextureResource("tex08.jpg"),
    new TextureResource("tex09.jpg"),
    new TextureResource("tex10.png"),
    new TextureResource("tex11.png"),
    new TextureResource("tex12.png"),
    //new TextureResource("tex13.png"),
    new TextureResource("tex14.png"),
    new TextureResource("tex15.png"),
    new TextureResource("tex16.png"),
  };


  private static class VideoResource extends BasicResource {
    VideoResource(String name) {
      super("videos/" + name);
    }
  }
  
  public static Resource VIDEOS[] = new Resource[] {
    new VideoResource("vid00.mp4"),
    new VideoResource("vid01.mp4"),
    new VideoResource("vid02.mp4"),
  };

  private static class CubemapResource extends BasicResource {
    CubemapResource(String name) {
      super("cubemaps/" + name);
    }
  }

  public static Resource CUBEMAPS[] = new Resource[] {
    new CubemapResource("cube00_0.jpg"),
    new CubemapResource("cube01_0.png"),
    new CubemapResource("cube02_0.jpg"),
    new CubemapResource("cube03_0.png"),
    new CubemapResource("cube04_0.png"),
    new CubemapResource("cube05_0.png"),
  };

  private static class MusicResource extends BasicResource {
    MusicResource(String name) {
      super("audio/" + name);
    }
  }

  public static Resource MUSIC[] = new Resource[] {
    new MusicResource("mzk00.mp3"),
    new MusicResource("mzk01.mp3"),
    new MusicResource("mzk02.mp3"),
    new MusicResource("mzk03.mp3"),
    new MusicResource("mzk04.mp3"),
    new MusicResource("mzk05.mp3"),
    new MusicResource("mzk06.mp3"),
  };
}
