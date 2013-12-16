
package com.melvin.android.base.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.melvin.android.base.common.ui.TitleProvider;
import com.melvin.android.base.fragment.UUFragment;

/**
 * UU Fragment 自定义的adapter
 *
 * @author wangbx
 */
public abstract class UUFragmentPageAdapter extends PagerAdapter implements TitleProvider {

    protected Activity mActivity = null;

    /**
     * TAB标签text *
     */
    protected String[] mContent;

    private List<UUFragment> fragments = null;

    public UUFragmentPageAdapter(Activity activity, String[] content) {
        this.mActivity = activity;
        this.mContent = content;
        fragments = new ArrayList<UUFragment>();
    }

    public void add(UUFragment fragment) {
        fragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public UUFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        UUFragment fragment = getItem(position);
        View view = fragment.onCreateView();
        ((ViewPager) collection).addView(view, 0);
        return fragment;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView(getItem(position).onCreateView());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((UUFragment) object).onCreateView();
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    public String getTitle(int position) {
        if (mContent != null)
            return mContent[position];
        return null;
    }

}
