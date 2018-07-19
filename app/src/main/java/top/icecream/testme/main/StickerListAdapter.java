package top.icecream.testme.main;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;

import java.util.LinkedList;

import top.icecream.testme.R;
import top.icecream.testme.opengl.CameraRender;

/**
 * AUTHOR: 86417
 * DATE: 6/11/2017
 */

public class StickerListAdapter extends ListAdapter {

    public StickerListAdapter(Context context, CameraRender cameraRender, MainActivity.Callback callback) {
        super(context, cameraRender, callback, R.array.sticker);
        iconList = new LinkedList<>();
        iconList.add(null);
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.glasses, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.moustache, null));
        iconList.add(BitmapFactory.decodeResource(context.getResources(), R.raw.mask, null));
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        cameraRender.selectSticker(position);
        callback.listVanish();
    }
}


