
package com.tianv.updis.cache;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class FastBitmapDrawable extends Drawable {
    private final Bitmap mBitmap;

    public FastBitmapDrawable(Bitmap b) {
        mBitmap = b;
    }

    public void draw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
        }
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getIntrinsicWidth() {
        if (mBitmap == null) {
            return 0;
        }
        return mBitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        if (mBitmap == null) {
            return 0;
        }
        return mBitmap.getHeight();
    }

    public int getMinimumWidth() {
        if (mBitmap == null) {
            return 0;
        }
        return mBitmap.getWidth();
    }

    public int getMinimumHeight() {
        if (mBitmap == null) {
            return 0;
        }
        return mBitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
