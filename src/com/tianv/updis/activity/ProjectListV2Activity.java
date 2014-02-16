package com.tianv.updis.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.melvin.android.base.activity.BaseFragmentActivity;
import com.melvin.android.base.common.ui.PageIndicator;
import com.tianv.updis.R;
import com.tianv.updis.adapter.MyInformationAdapter;
import com.tianv.updis.adapter.ProjectListAdapter;

/**
 * Created by Wincent on 14-2-16.
 */
public class ProjectListV2Activity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.common_viewpage_layout);
        String title[] = getResources().getStringArray(R.array.projectlist_tab_text);
        mAdapter = new ProjectListAdapter(this, title);
        mIndicator = (PageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
        initPageChange();
    }
}
