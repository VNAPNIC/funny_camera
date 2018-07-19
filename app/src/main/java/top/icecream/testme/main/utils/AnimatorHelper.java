package top.icecream.testme.main.utils;

import android.view.View;


/**
 * AUTHOR: 86417
 * DATE: 5/11/2017
 */

public class AnimatorHelper {

    private static final int DURATION_LIST = 700;

    public static void buttonVanish(View... views){
        for(final View view:views){
            view.setEnabled(false);
            view.animate()
                    .scaleX(0)
                    .scaleY(0)
                    .setDuration(DURATION_LIST)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.INVISIBLE);
                        }
                    }).start();
        }
    }

    public static void buttonEmerge(View... views) {
        for (final View view : views) {
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(DURATION_LIST)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    })
                    .start();
        }
    }

    public static void listVanish(View... views) {
        for(final View view: views){
            view.animate()
                    .setDuration(DURATION_LIST)
                    .translationYBy(view.getHeight())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.INVISIBLE);
                            view.setY(view.getY()-view.getHeight());
                        }
                    })
                    .start();
        }
    }

    public static void listEmerge(View... views) {
        for(final View view: views){
            view.setY(view.getY()+view.getHeight());
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .setDuration(DURATION_LIST)
                    .translationYBy(-view.getHeight())
                    .start();
        }
    }

}
