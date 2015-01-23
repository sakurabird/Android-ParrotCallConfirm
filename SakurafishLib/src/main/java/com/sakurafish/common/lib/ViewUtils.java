package com.sakurafish.common.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import java.lang.reflect.Method;

/**
 * Created by sakura on 2014/12/28.
 */
public class ViewUtils {

    /**
     * ディスプレイ幅を返す
     *
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getWidth(final Activity activity) {
        Display display;
        int size = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            size = display.getWidth();
        } else {
            display = activity.getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            size = p.x;
        }
        return size;
    }

    /**
     * ディスプレイ高を返す
     *
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getHeight(final Activity activity) {
        Display display;
        int size = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            size = display.getHeight();
        } else {
            display = activity.getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            size = p.y;
        }
        return size;
    }

    /**
     * ディスプレイ密度を返す
     *
     * @return
     */
    public static float getDensity(final Activity activity) {
        float density = activity.getResources().getDisplayMetrics().density;
        return density;
    }

    public static void enableView(View v, boolean enable) {
        if (v.isEnabled() == enable) {
            return;
        }

        float from = enable ? .5f : 1.0f;
        float to = enable ? 1.0f : .5f;

        AlphaAnimation a = new AlphaAnimation(from, to);

        a.setDuration(0);
        a.setFillAfter(true);

        v.setEnabled(enable);
        v.startAnimation(a);
    }

    @SuppressWarnings("deprecation")
    public static void overrideGetSize(Display display, Point outSize) {
        try {
            @SuppressWarnings("rawtypes")
            Class pointClass = Class.forName("android.graphics.Point");
            Method newGetSize = Display.class.getMethod("getSize", new Class[]{
                    pointClass
            });

            newGetSize.invoke(display, outSize);
        } catch (Exception ex) {
            outSize.x = display.getWidth();
            outSize.y = display.getHeight();
        }
    }

    private ViewUtils(){}
}
