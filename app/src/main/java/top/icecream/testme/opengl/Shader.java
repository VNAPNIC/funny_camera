package top.icecream.testme.opengl;

import android.content.Context;

import top.icecream.testme.opengl.utils.ResourceHelper;
import top.icecream.testme.opengl.utils.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

/**
 * AUTHOR: 86417
 * DATE: 5/3/2017
 */

public class Shader {

    protected static final String S_TEXTURE = "sTexture";

    protected static final String U_MATRIX = "uMatrix";
    protected static final String U_TEXTURE_UNIT = "uTextureUnit";

    protected static final String A_POSITION = "aPosition";
    protected static final String A_TEXTURE_COORDINATES = "aTextureCoordinates";

    protected final int program;

    protected float[] coordinates;

    protected Shader(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(
                ResourceHelper.readFile(context, vertexShaderResourceId),
                ResourceHelper.readFile(context, fragmentShaderResourceId)
        );
    }

    public void useProgram() {
        glUseProgram(program);
    }
    public float[] getCoordinates() {
        return coordinates;
    }
}
