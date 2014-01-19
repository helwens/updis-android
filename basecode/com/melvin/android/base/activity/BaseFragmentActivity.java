/**
 * @Title: BaseActivity.java
 * @Package com.uucun.android.cms.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author guojun
 * @date 2012-1-11 上午11:22:24
 * @version V1.0
 */

package com.melvin.android.base.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.melvin.android.base.adapter.UUFragmentPageAdapter;
import com.melvin.android.base.common.ui.PageIndicator;
import com.melvin.android.base.common.ui.TabPageIndicator;
import com.melvin.android.base.fragment.UUFragment;
import com.uucun.android.logger.Logger;

import cn.jpush.android.api.InstrumentedActivity;

public class BaseFragmentActivity extends InstrumentedActivity implements TabPageIndicator.CustClickListener {

    protected ViewPager mPager;

    protected PageIndicator mIndicator;

    protected UUFragmentPageAdapter mAdapter;

    @Override
    protected void onDestroy() {
        mPager = null;
        mIndicator = null;
        mAdapter = null;
        // 强制GC
        System.gc();
        super.onDestroy();
    }

    /**
     * 初始化页面更改的事件
     *
     * @Title: initPageChange
     */
    protected void initPageChange() {
        ((TabPageIndicator) mIndicator).setCustClickListener(this);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                changePage();
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        changePage();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null && mPager != null) {
            int index = mPager.getCurrentItem();
            if (mAdapter != null) {
                UUFragment fragment = mAdapter.getItem(index);
                if (fragment != null) {
                    fragment.onStop();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onStop();
        if (mAdapter != null && mPager != null) {
            int index = mPager.getCurrentItem();
            if (mAdapter != null) {
                UUFragment fragment = mAdapter.getItem(index);
                if (fragment != null) {
                    fragment.onPause();
                }
            }
        }
    }

    /**
     * 页面切换的时候会执行的操作
     */
    public void changePage() {
        if (mAdapter != null && mPager != null) {
            int index = mPager.getCurrentItem();
            Logger.i("changePage==========", String.valueOf(index));
            if (mAdapter != null) {
                UUFragment fragment = mAdapter.getItem(index);
                fragment.onDisplay();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAdapter != null && mPager != null) {
            int index = mPager.getCurrentItem();
            if (mAdapter != null) {
                UUFragment fragment = mAdapter.getItem(index);
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void clickTab(int index) {
        Logger.i("clickTab-----", String.valueOf(index));

        if (mAdapter != null) {
            UUFragment fragment = mAdapter.getItem(index);
            fragment.clickTab(index);
        }
    }
}
