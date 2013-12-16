
package com.tianv.updis.adapter;

import android.app.Activity;
import com.melvin.android.base.adapter.UUFragmentPageAdapter;
import com.tianv.updis.Constant;
import com.tianv.updis.fragment.CommonListFragment;
import com.uucun.android.sharedstore.SharedStore;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CategoryFragmentAdapter.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:36:30
 */
public class CategoryFragmentAdapter extends UUFragmentPageAdapter {
    public CategoryFragmentAdapter(Activity activity, String[] content) {
        super(activity, content);
        add(new CommonListFragment(activity, Constant.VIEW_HOME_NOTICE));
        add(new CommonListFragment(activity, Constant.VIEW_HOME_BIDDING));
        add(new CommonListFragment(activity, Constant.VIEW_HOME_TALK));
        add(new CommonListFragment(activity, Constant.VIEW_HOME_AMATEUR));


        SharedStore sharedStore = new SharedStore(activity, null);
        String isSpecailUser = sharedStore.getString(Constant.UPDIS_STORE_KEY_ISSPECIALUSER, "0");
        if (isSpecailUser.equals("1")) {
            add(new CommonListFragment(activity, Constant.VIEW_PROJECT));
        }
    }
}
