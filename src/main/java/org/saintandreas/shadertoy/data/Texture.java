package org.saintandreas.shadertoy.data;

import static org.saintandreas.shadertoy.data.ImageType.*;
public enum Texture {
  Tex00(PNG),
  Tex01(PNG),
  Tex02(PNG),
  Tex03(PNG),
  Tex04(PNG),
  Tex05(PNG),
  Tex06(PNG),
  Tex07(PNG),
  Tex08(PNG),
  Tex09(PNG),
  Tex10(JPG),
  Tex11(JPG),
  Tex12(JPG),
  Tex13(JPG),
  Tex14(JPG),
  Tex15(JPG),
  Tex16(JPG),
  ;

  public final String path;

  Texture(ImageType type) {
    path = name().toLowerCase() + "." + type.extension;
  }
}
