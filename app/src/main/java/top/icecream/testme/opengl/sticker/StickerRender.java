package top.icecream.testme.opengl.sticker;

import android.content.Context;

import top.icecream.testme.R;
import top.icecream.testme.opengl.Shader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * AUTHOR: 86417
 * DATE: 5/3/2017
 */

public class StickerRender extends Shader {

    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public StickerRender(Context context) {
        super(context, R.raw.sticker_vertex_shader,R.raw.sticker_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        setPosition(new float[]{0f, 0f}, 0, 0);
    }

    public void setMatrix(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public void bindTexture() {

    }

    protected void bindTexture(int textureId) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

    public void setPosition(float[] center, float xRadius, float yRadius) {
        coordinates = new float[]{
                //order of coordinate:X,Y,S,T
                //Triangle fan
                center[0], center[1], 0.5f, 0.5f,
                center[0] - xRadius, center[1] - yRadius, 0f, 1f,
                center[0] + xRadius, center[1] - yRadius, 1f, 1f,
                center[0] + xRadius, center[1] + yRadius, 1f, 0f,
                center[0] - xRadius, center[1] + yRadius, 0f, 0f,
                center[0] - xRadius, center[1] - yRadius, 0f, 1f
        };
    }
}