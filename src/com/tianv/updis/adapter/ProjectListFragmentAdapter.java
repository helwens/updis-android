package com.tianv.updis.adapter;

import android.app.Activity;

import com.melvin.android.base.adapter.UUFragmentPageAdapter;
import com.tianv.updis.Constant;
import com.tianv.updis.fragment.ProjectListFragment;

/**
 * Created by Wincent on 14-2-16.
 */
public class ProjectListFragmentAdapter extends UUFragmentPageAdapter {
    public ProjectListFragmentAdapter(Activity activity, String[] title) {
        super(activity, title);
        add(new ProjectListFragment(activity, Constant.VIEW_PROJECT_LIST));
    }
}
