package top.icecream.testme.main;

import android.content.Context;
import android.view.View;

import java.util.LinkedList;

import top.icecream.testme.R;
import top.icecream.testme.opengl.CameraRender;

/**
 * AUTHOR: 86417
 * DATE: 6/11/2017
 */

public class FilterListAdapter extends ListAdapter {

    public FilterListAdapter(Context context, CameraRender cameraRender, MainActivity.Callback callback) {
        super(context, cameraRender, callback, R.array.filter);
        iconList = new LinkedList<>();
        iconList.add(null);
        iconList.add(null);
        iconList.add(null);
        iconList.add(null);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        cameraRender.selectFilter(position);
        callback.listVanish();
    }
}
