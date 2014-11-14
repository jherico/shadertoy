package org.saintandreas.shadertoy;


public class RateCounter {
  private long start = -1;
  private int count = 0;
  
  public void increment() {
    if (-1 == start) {
      start = System.currentTimeMillis();
    }
    ++count;
  }
  
  public void reset() {
    start = System.currentTimeMillis();
    count = 0;
  }
  
  public int count() {
    return count;
  }
  
  public float duration() {
    return (System.currentTimeMillis() - start) / 1000.0f;
  }
  
  public float rate() {
    return count / duration();
  }
}
