package top.icecream.testme.opengl;

import top.icecream.testme.opengl.filter.FilterRender;
import top.icecream.testme.opengl.sticker.StickerRender;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static top.icecream.testme.opengl.Constant.BYTES_PER_FLOAT;

/**
 * AUTHOR: 86417
 * DATE: 5/4/2017
 */

public class Texture {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private VertexArray vertexArray;

    public Texture() {
    }

    public void bindData(Shader shader){
        int arrLocXY = 0;
        int arrLocST = 0;
        if (shader instanceof FilterRender) {
            arrLocXY = ((FilterRender) shader).getPositionAttributeLocation();
            arrLocST = ((FilterRender) shader).getTextureCoordinatesAttributeLocation();

        }else if(shader instanceof StickerRender) {
            arrLocXY = ((StickerRender) shader).getPositionAttributeLocation();
            arrLocST = ((StickerRender) shader).getTextureCoordinatesAttributeLocation();
        }

        vertexArray = new VertexArray(shader.getCoordinates());
        vertexArray.setVertexAttributePointer(
                0,
                arrLocXY,
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                arrLocST,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
