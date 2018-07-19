#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES sTexture;
varying vec2 vTextureCoordinates;

void main()
{
    vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec2 TexSize = vec2(100.0, 100.0);
    vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);

    vec2 tex = vTextureCoordinates;
    vec2 upLeftUV = vec2(tex.x-1.0/TexSize.x, tex.y-1.0/TexSize.y);
    vec4 curColor = texture2D(sTexture, vTextureCoordinates);
    vec4 upLeftColor = texture2D(sTexture, upLeftUV);
    vec4 delColor = curColor - upLeftColor;
    float luminance = dot(delColor.rgb, W);
    gl_FragColor = vec4(vec3(luminance), 0.0) + bkColor;
}