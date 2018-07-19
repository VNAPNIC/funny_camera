package top.icecream.testme.opengl.sticker;

import android.content.Context;

import top.icecream.testme.R;
import top.icecream.testme.opengl.utils.TextureHelper;

/**
 * AUTHOR: 86417
 * DATE: 6/8/2017
 */

public class MoustacheStickerRender extends StickerRender{

    private final Context context;

    public MoustacheStickerRender(Context context) {
        super(context);
        this.context = context;
    }

    public void bindTexture() {
        int textureId = TextureHelper.loadTexture(context, R.raw.moustache);
        bindTexture(textureId);
    }
}
