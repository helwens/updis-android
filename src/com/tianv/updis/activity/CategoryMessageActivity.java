
package com.tianv.updis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.melvin.android.base.activity.BaseFragmentActivity;
import com.melvin.android.base.common.ui.PageIndicator;
import com.tianv.updis.R;
import com.tianv.updis.adapter.CategoryFragmentAdapter;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CategoryMessageActivity.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:20:48
 */
public class CategoryMessageActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.common_viewpage_layout);
        String title[] = getResources().getStringArray(R.array.category_tab_text);
        mAdapter = new CategoryFragmentAdapter(this, title);
        mIndicator = (PageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
        initPageChange();
    }

    @Override
    protected void onNewIntent(Intent intent) {
    }
}
