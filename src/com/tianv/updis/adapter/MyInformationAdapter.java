
package com.tianv.updis.adapter;

import android.app.Activity;

import com.melvin.android.base.adapter.UUFragmentPageAdapter;
import com.tianv.updis.Constant;
import com.tianv.updis.fragment.ChangePasswordFragment;
import com.tianv.updis.fragment.MyInfoDetailFragment;
import com.tianv.updis.fragment.MySendListFragment;
import com.tianv.updis.fragment.SendMsgFragment;

public class MyInformationAdapter extends UUFragmentPageAdapter {
    public MyInformationAdapter(Activity activity, String[] content) {
        super(activity, content);
//        add(new ChangePasswordFragment(activity, Constant.VIEW_CHANGE_PWD));
//        add(new MyInfoDetailFragment(activity, Constant.VIEW_USER_INFO));
        add(new MySendListFragment(activity, Constant.VIEW_USER_SEND_LIST));
        add(new SendMsgFragment(activity, Constant.VIEW_USER_SEND_MSG));
    }
}
