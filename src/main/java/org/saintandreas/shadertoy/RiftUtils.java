package org.saintandreas.shadertoy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.saintandreas.math.Matrix4f;
import org.saintandreas.math.Quaternion;
import org.saintandreas.math.Vector2f;
import org.saintandreas.math.Vector3f;

import com.oculusvr.capi.OvrMatrix4f;
import com.oculusvr.capi.OvrQuaternionf;
import com.oculusvr.capi.OvrSizei;
import com.oculusvr.capi.OvrVector2f;
import com.oculusvr.capi.OvrVector3f;
import com.oculusvr.capi.Posef;

public class RiftUtils {

  static long getNativeWindow() {
    long window = -1;
    try {
      Object displayImpl = null;
      Method[] displayMethods = Display.class.getDeclaredMethods();
      for (Method m : displayMethods) {
        if (m.getName().equals("getImplementation")) {
          m.setAccessible(true);
          displayImpl = m.invoke(null, (Object[]) null);
          break;
        }
      }
  
      String fieldName = null;
      switch (LWJGLUtil.getPlatform()) {
      case LWJGLUtil.PLATFORM_LINUX:
        fieldName = "current_window";
        break;
      case LWJGLUtil.PLATFORM_WINDOWS:
        fieldName = "hwnd";
        break;
      }
      if (null != fieldName) {
        Field[] windowsDisplayFields = displayImpl.getClass()
            .getDeclaredFields();
        for (Field f : windowsDisplayFields) {
          if (f.getName().equals(fieldName)) {
            f.setAccessible(true);
            window = (Long) f.get(displayImpl);
            continue;
          }
        }
      }
    } catch (IllegalArgumentException | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
    return window;
  }

  public static Matrix4f toMatrix4f(OvrMatrix4f m) {
    return new org.saintandreas.math.Matrix4f(m.M).transpose();
  }

  public static Matrix4f toMatrix4f(Posef p) {
    return new Matrix4f().rotate(toQuaternion(p.Orientation)).mult(
        new Matrix4f().translate(toVector3f(p.Position)));
  }

  public static Quaternion toQuaternion(OvrQuaternionf q) {
    return new Quaternion(q.x, q.y, q.z, q.w);
  }

  public static Vector2f toVector2f(OvrSizei v) {
    return new Vector2f(v.w, v.h);
  }

  public static Vector2f toVector2f(OvrVector2f v) {
    return new Vector2f(v.x, v.y);
  }

  public static Vector3f toVector3f(OvrVector3f v) {
    return new Vector3f(v.x, v.y, v.z);
  }

}
