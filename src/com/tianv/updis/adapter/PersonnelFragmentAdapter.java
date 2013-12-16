
package com.tianv.updis.adapter;

import android.app.Activity;

import com.melvin.android.base.adapter.UUFragmentPageAdapter;
import com.tianv.updis.Constant;
import com.tianv.updis.fragment.PersonnelQueryFragment;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: PersonnelFragmentAdapter.java
 * @Description: TODO
 * @Date 2013-4-9 下午11:03:45
 */
public class PersonnelFragmentAdapter extends UUFragmentPageAdapter {
    public PersonnelFragmentAdapter(Activity activity, String[] content) {
        super(activity, content);
        add(new PersonnelQueryFragment(activity, Constant.VIEW_PERSONNEL_QUERY));
        add(new PersonnelQueryFragment(activity, Constant.VIEW_PERSONNEL_ADDRESS_BOOK));
    }
}
