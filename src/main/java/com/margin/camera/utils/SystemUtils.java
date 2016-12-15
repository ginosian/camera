package com.margin.camera.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created on Jul 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class SystemUtils {

    /**
     * Hides the system bars.
     * Sets the IMMERSIVE flag.
     * Sets the content to appear under the system bars so that the content doesn't resize
     * when the system bars hide and show.
     */
    public static void hideSystemUI(Activity activity) {
        if (activity != null) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * Shows the system bars. It does this by removing all the flags
     * except for the ones that make the content appear under the system bars.
     */
    public static void showSystemUI(Activity activity) {
        if (activity != null) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * Returns status bar height in px
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
