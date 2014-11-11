package org.saintandreas.shadertoy.data;

public enum ImageType {
  PNG("png"),
  JPG("jpg");
  
  public final String extension;
  
  ImageType(String extension) {
    this.extension = extension;
  }
}
