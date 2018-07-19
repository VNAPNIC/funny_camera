package top.icecream.testme.opengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import top.icecream.testme.camera.Camera;
import top.icecream.testme.opengl.Shader;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static top.icecream.testme.camera.Camera.BACK;

/**
 * AUTHOR: 86417
 * DATE: 5/4/2017
 */

public class FilterRender extends Shader {

    private final int uTextureUnitLocation;
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private float aspectRatio = 640.0f / 480.0f;

    public FilterRender(Context context, int vertexShader, int fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, S_TEXTURE);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        setFrontCoordinate();
    }

    public void bindTexture(int textureId, float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }


    private void setFrontCoordinate() {
        coordinates = new float[]{
                //order of coordinate:X,Y,S,T
                //Triangle fan
                //相机原始图像需要逆时针旋转90度
                0f, 0f, 0.5f, 0.5f,
                -1f, -aspectRatio, 0f, 1f,
                1f, -aspectRatio, 0f, 0f,
                1f, aspectRatio, 1f, 0f,
                -1f, aspectRatio, 1f, 1f,
                -1f, -aspectRatio, 0f, 1f
        };
    }

    private void setBackCoordinate() {
        coordinates = new float[]{
                //order of coordinate:X,Y,S,T
                //Triangle fan
                //相机原始图像需要顺时针旋转90度， 再镜像对称。或者沿y=x对称
                0f, 0f, 0.5f, 0.5f,
                -1f, -aspectRatio, 1f, 1f,
                1f, -aspectRatio, 1f, 0f,
                1f, aspectRatio, 0f, 0f,
                -1f, aspectRatio, 0f, 1f,
                -1f, -aspectRatio, 1f, 1f
        };
    }

    public void changeCameraDirection(Camera camera) {
        if (camera.getCameraId() == BACK) {
            setBackCoordinate();
        } else{
            setFrontCoordinate();
        }
    }
}