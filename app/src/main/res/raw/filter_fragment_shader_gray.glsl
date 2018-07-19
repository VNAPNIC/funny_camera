#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES sTexture;
varying vec2 vTextureCoordinates;

void main()
{
  vec4 textureColor = texture2D(sTexture, vTextureCoordinates);
  vec3 weight = vec3(0.33, 0.33, 0.33);
  float step = dot(textureColor.rgb, weight);
  gl_FragColor = vec4(vec3(step), 1);
}