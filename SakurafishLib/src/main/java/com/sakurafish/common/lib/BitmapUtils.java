package com.sakurafish.common.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.InputStream;

/**
 * Created by sakura on 2014/12/28.
 */
public class BitmapUtils {

    public static Bitmap createStreamBitmap(InputStream stream, BitmapFactory.Options opts, int newWidth,
                                            int newHeight) {
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, opts);

        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        if (imageHeight > newHeight || imageWidth > newWidth) {
            if (imageWidth > imageHeight) {
                opts.inSampleSize = (int) Math.ceil((float) imageHeight
                        / (float) newHeight);
            } else {
                opts.inSampleSize = (int) Math.ceil((float) imageWidth
                        / (float) newWidth);
            }
        }
        opts.inJustDecodeBounds = false;
        Bitmap ret = BitmapFactory.decodeStream(stream, null, opts);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        if (oldWidth < newWidth && oldHeight < newHeight) {
            // 縦も横も指定サイズより小さい場合は何もしない
            return bitmap;
        }

        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix scale = new Matrix();
        scale.postScale(scaleFactor, scaleFactor);

        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false);
        bitmap.recycle();

        return resizeBitmap;
    }

    private BitmapUtils(){}
}
