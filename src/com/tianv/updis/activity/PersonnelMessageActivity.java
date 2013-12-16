
package com.tianv.updis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.melvin.android.base.activity.BaseFragmentActivity;
import com.melvin.android.base.common.ui.PageIndicator;
import com.melvin.android.base.common.ui.TabPageIndicator;
import com.tianv.updis.R;
import com.tianv.updis.adapter.PersonnelFragmentAdapter;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: PersonnelMessageActivity.java
 * @Description: TODO
 * @Date 2013-4-9 下午10:58:39
 */
public class PersonnelMessageActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.common_viewpage_layout);
        String title[] = getResources().getStringArray(R.array.personnel_tab_text);
        mAdapter = new PersonnelFragmentAdapter(this, title);
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
