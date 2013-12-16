package com.melvin.android.base.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * UUCUN MARKET自定义的Fragment
 *
 * @author Wang Baoxi
 */
public class UUFragment {

    public Activity mActivity;

    /**
     * 最外层的视图对象
     */
    public View view;

    /**
     * 构造函数
     *
     * @param activity
     */
    public UUFragment(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 当创建视图的时候回调
     *
     * @return
     */
    public View onCreateView() {
        return null;
    }

    /**
     * 当页面显示的时候调用，onResume和页面切换的时候 均会调用此方法
     */
    public void onDisplay() {
    }

    /**
     * 当使用startActivityForResult 启动一个Activity的时候，返回时会回调此方法
     *
     * @param requestCode 请求的CODE
     * @param resultCode  结果CODE
     * @param data        返回的INTENT DATA
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onStop() {

    }

    public void onPause() {

    }

    public void clickTab(int index) {

    }
}
