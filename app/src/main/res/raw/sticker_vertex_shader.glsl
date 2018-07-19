uniform mat4 uMatrix;
attribute vec4 aPosition;
attribute vec2 aTextureCoordinates;
varying vec2 vTextureCoordinates;

void main()
{
    gl_Position = uMatrix * aPosition;
    vTextureCoordinates = aTextureCoordinates;
}