package com.melvin.android.base.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * @author Liu Congshan liucs@uucun.com
 *         <p/>
 *         A slower gallery
 */
public class SlowGallery extends Gallery {
    private OnDragLastListener mOnDragLastListener;
    private int position = 0;

    public SlowGallery(Context context) {
        super(context);
    }

    public SlowGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (mOnDragLastListener != null
                && velocityX < 0
                && position == getAdapter().getCount() - 1) {
            mOnDragLastListener.onDragLast();
        }
        position = getSelectedItemPosition();
        return super.onFling(e1, e2, velocityX / 10, velocityY);
    }

    public void setOnDragLastListener(OnDragLastListener l) {
        mOnDragLastListener = l;
    }

    public interface OnDragLastListener {
        void onDragLast();
    }
}
