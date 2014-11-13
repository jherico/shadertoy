package org.saintandreas.shadertoy.data;

import org.saintandreas.resources.Resource;

public class ChannelInput {

  public enum Type {
    TEXTURE,
    CUBEMAP,
    AUDIO,
    VIDEO,
  };
  
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
