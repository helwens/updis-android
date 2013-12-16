package com.melvin.android.base.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author guojun
 * @ClassName: DotIndicatorLayout
 * @Description: 详情页面截图滚动底部的指示点
 * @date 2012-7-9 上午11:09:23
 */
public class DotIndicatorLayout extends LinearLayout {
    private int count = 0;
    private int selection = 0;
    private ImageView[] dotImgs;
    /**
     * 正常的图片
     */
    private int resDotNormal;
    /**
     * 选中后的图片
     */
    private int resDotHl;
    private Context mContext;

    public DotIndicatorLayout(Context context) {
        this(context, null);
    }

    public DotIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * @param resDotHl     : high light dot image
     * @param resDotNormal : normal dot image
     * @param count        : dot counts, MUST > 0
     */
    public void initLayout(int resDotHl, int resDotNormal, int count) {
        if (count < 1) {
            return;
        }

        this.resDotHl = resDotHl;
        this.resDotNormal = resDotNormal;
        this.count = count;

        dotImgs = new ImageView[count];
        for (int i = 0; i < count; i++) {
            dotImgs[i] = new ImageView(mContext);
            dotImgs[i].setImageResource(resDotNormal);
            addView(dotImgs[i]);
        }
    }

    public int getCount() {
        return count;
    }

    public void setSelection(int position) {
        int index = position % count;
        dotImgs[selection].setImageResource(resDotNormal);
        dotImgs[index].setImageResource(resDotHl);
        selection = index;
    }
}