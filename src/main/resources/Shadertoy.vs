#version 330

uniform mat4 Projection = mat4(1);
uniform mat4 ModelView = mat4(1);

layout(location = 0) in vec3 Position;

void main() {
  gl_Position = vec4(Position, 1);
}
