package org.saintandreas.shadertoy;


public class Accumulator {
  private float value = 0;
  private int count = 0;
  
  public void add(float f) {
    value += f;
    ++count;
  }
  
  public void reset() {
    value = 0;
    count = 0;
  }
  
  public int count() {
    return count;
  }
  
  public float average() {
    if (0 != count) {
      return value / count;
    }
    return 0;
  }
}
