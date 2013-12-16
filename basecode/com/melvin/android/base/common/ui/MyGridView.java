package com.melvin.android.base.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author guojun
 * @ClassName:猜你喜欢 , 重写gridview，显示全部数据
 * @Description:
 * @date 2012-3-22 下午01:53:28
 */
public class MyGridView extends GridView {

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
