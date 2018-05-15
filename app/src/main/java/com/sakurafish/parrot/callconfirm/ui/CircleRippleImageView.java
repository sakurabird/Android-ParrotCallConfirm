/**
 * Copyright 2015-present Yukari Sakurai
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sakurafish.parrot.callconfirm.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * CircleImageView with ripple effect
 */
public class CircleRippleImageView extends AppCompatImageView {
    private static final float rippleSpeed = 1.2f;
    private static final int circleMargin = 0;
    private int canvasSize;
    private int circleCenterX, circleCenterY;
    private Bitmap image;
    private Paint paint;
    private Integer rippleColor = Color.parseColor("#44FFFFFF");

    public CircleRippleImageView(Context context) {
        super(context, null);
    }

    public CircleRippleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public CircleRippleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() == 0 || getHeight() == 0) return;
        showCircleImage(canvas);
        showRippleEffect(canvas);
    }

    private void showCircleImage(Canvas canvas) {
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        if (drawable == null) return;
        Bitmap srcBmp = drawable.getBitmap();
        if (srcBmp == null) return;

        image = getSquareBitmap(srcBmp);

        // TODO
        canvasSize = canvas.getWidth() - circleMargin;
        if (canvas.getHeight() - circleMargin < canvasSize)
            canvasSize = canvas.getHeight() - circleMargin;

        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        circleCenterX = canvas.getWidth() / 2;
        circleCenterY = canvas.getHeight()  / 2;

        canvas.drawCircle(circleCenterX, circleCenterY, canvasSize / 2, paint);
        invalidate();
    }

    float x = -1, y = -1;
    float radius = -1;

    private void showRippleEffect(Canvas canvas) {
        if (x == -1) x = circleCenterX;
        if (y == -1) y = circleCenterY;
        if (radius == -1) radius = 1;

        Rect src = new Rect(0, 0, getWidth(), getHeight());
        Rect dst = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(makeCircle(), src, dst, null);
        invalidate();
    }

    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);
        canvas.drawCircle(x, y, radius, paint);
        if (radius < getHeight() / 2 - rippleSpeed) {
            radius += rippleSpeed;
        } else {
            //repeat
            radius = 1;
        }
        return output;
    }

    private Bitmap getSquareBitmap(Bitmap srcBmp) {
        if (srcBmp.getWidth() == srcBmp.getHeight()) return srcBmp;

        //Rectangle to square. Equivarent to ScaleType.CENTER_CROP
        int dim = Math.min(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        float left = srcBmp.getWidth() > dim ? (dim - srcBmp.getWidth()) / 2 : 0;
        float top = srcBmp.getHeight() > dim ? ((dim - srcBmp.getHeight()) / 2) : 0;
        canvas.drawBitmap(srcBmp, left, top, null);

        return dstBmp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else {
            result = canvasSize;
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else {
            result = canvasSize;
        }
        return (result + 2);
    }
}