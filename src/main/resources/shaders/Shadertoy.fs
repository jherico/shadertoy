void main(void)
{
  vec2 uv = gl_FragCoord.xy / iResolution.xy;
  gl_FragColor = vec4(iDir,1.0);
}

