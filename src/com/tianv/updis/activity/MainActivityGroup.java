/**
 * @Title: MainActivityGroup.java
 * @Package com.uucun.android.cms.activity
 * @author Wang Baoxi 
 * @date 2012-1-21 下午07:21:18
 * @version V1.0
 */

package com.tianv.updis.activity;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.task.UpdateApp;
import com.uucun.android.sharedstore.SharedStore;

/**
 * @author Wang Baoxi
 * @ClassName: MainActivityGroup
 * @date 2012-1-21 下午07:21:18
 */
@SuppressWarnings("deprecation")
public class MainActivityGroup extends ActivityGroup {
    // 150,173,178
    // 网络状态 无连接
    public static final int NO_NETWORK = 0;

    // 网络连接类型 wifi
    public static final int WIFI = 1;

    // 网络连接类型 wap
    public static final int WAP = 2;

    // 网络连接类型 gprs
    public static final int GPRS = 3;


    public static int aBottomNavIcons[] = {
            R.drawable.icon_1, R.drawable.icon_2, R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5
    };

    private static int aBottomNavHLIcons[] = {
            R.drawable.icon_1_h, R.drawable.icon_2_h, R.drawable.icon_3_h, R.drawable.icon_4_h, R.drawable.icon_5_h
    };

    @SuppressWarnings("rawtypes")
    public static Class mTabClassArray[] = {
            CategoryMessageActivity.class, PersonnelMessageActivity.class,
            MyInformationActivity.class, ProjectListV2Activity.class,SettingActivity.class
    };

    private final int NAVIGATOR_HOME = 0;

    private final int NAVIGATOR_CATEGORY = 1;

    private final int NAVIGATOR_RECOMMEND = 2;

    private static final int NAVIGATOR_PROJECT = 3;

    private final int NAVIGATOR_MANAGE = 4;

    /**
     * 装载内容的容器
     */
    private LinearLayout container;

    /**
     * 当前Tab的索引
     */
    private int currentTabIndex = -1;

    /**
     * pop window menu
     */
    private PopupWindow menuWindow;

    @SuppressWarnings("unused")
    private Context mContext;

    /**
     * bottom navigate layout
     */
    private TextView[] bottomNavigate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_nav_tab_layout);
        mContext = this;
        init();
    }

    /**
     * 初始化
     *
     * @Title: init
     */
    private void init() {
        container = (LinearLayout) findViewById(R.id.container_layout);
        bottomNavigate = new TextView[5];
        bottomNavigate[NAVIGATOR_HOME] = (TextView) findViewById(R.id.bottom_nav_home);
        bottomNavigate[NAVIGATOR_CATEGORY] = (TextView) findViewById(R.id.bottom_nav_category);
        bottomNavigate[NAVIGATOR_RECOMMEND] = (TextView) findViewById(R.id.bottom_nav_recommend);
        bottomNavigate[NAVIGATOR_PROJECT] = (TextView) findViewById(R.id.bottom_nav_project);
        bottomNavigate[NAVIGATOR_MANAGE] = (TextView) findViewById(R.id.bottom_nav_manage);
        View.OnClickListener navigateClickListener = getNavigateClickListener();
        for (TextView view : bottomNavigate) {
            view.setOnClickListener(navigateClickListener);
        }
        switchActivity(NAVIGATOR_HOME);

        SharedStore sharedStore = new SharedStore(mContext, null);
        Constant.COOKIES = sharedStore.getString("login_cookies", "");

        UpdateApp.getInstance(mContext).update(false);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int marginLeft = (int) (dm.widthPixels * 0.9 + 7 * dm.density);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginLeft, 0, 0, 0);
    }

    /**
     * {@link #switchActivity(int, int, Uri)}
     */
    private void switchActivity(int index) {
        switchActivity(index, 0, null);
    }

    /**
     * {@link #switchActivity(int, int, Uri)}
     */
    private void switchActivity(int index, int initPosition) {
        switchActivity(index, initPosition, null);
    }

    /**
     * {@link #switchActivity(int, int, Uri)}
     */
    @SuppressWarnings("unused")
    private void switchActivity(int index, Uri data) {
        switchActivity(index, 0, data);
    }

    /**
     * Switch to an Activity
     *
     * @param index        : index of activity
     * @param initPosition : initialize tab position
     * @param data         : uri data to activity
     */
    private void switchActivity(int index, int initPosition, Uri data) {
        if (index < 0 || index >= mTabClassArray.length) {
            return;
        }
        if (currentTabIndex != -1) {
            TextView currentTab = bottomNavigate[currentTabIndex];
            currentTab.setTextColor(getResources().getColor(R.color.bottom_nav_text));
            currentTab.setBackgroundResource(R.drawable.click_selector);
            Drawable drawableNormal = getResources().getDrawable(aBottomNavIcons[currentTabIndex]);
            currentTab.setCompoundDrawablesWithIntrinsicBounds(null, drawableNormal, null, null);
        }
        currentTabIndex = index;
        container.removeAllViews();
        TextView newTab = bottomNavigate[currentTabIndex];
        newTab.setTextColor(getResources().getColor(R.color.white));
        newTab.setBackgroundResource(R.drawable.menuh);
        Drawable drawableHl = getResources().getDrawable(aBottomNavHLIcons[currentTabIndex]);
        newTab.setCompoundDrawablesWithIntrinsicBounds(null, drawableHl, null, null);

        LinearLayout.LayoutParams childparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        Intent intent = new Intent(this, mTabClassArray[index]);

        if (null != data) {
            intent.setData(data);
        }
        String name = index + " subactivity";
        Window subActivity = getLocalActivityManager().startActivity(name, intent);
        container.addView(subActivity.getDecorView(), childparams);
    }

    private View.OnClickListener getNavigateClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.bottom_nav_home:
                        switchActivity(NAVIGATOR_HOME);
                        break;
                    case R.id.bottom_nav_category:
                        switchActivity(NAVIGATOR_CATEGORY);
                        break;
                    case R.id.bottom_nav_recommend:
                        switchActivity(NAVIGATOR_RECOMMEND);
                        break;
                    case R.id.bottom_nav_project:
                        switchActivity(NAVIGATOR_PROJECT);
                        break;
                    case R.id.bottom_nav_manage:
                        switchActivity(NAVIGATOR_MANAGE, 1);
                        break;
                }
            }
        };
        return clickListener;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // hide menu if menu is showing
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.dismiss();
            return;
        }
        exit();
    }

    private void exit() {
//        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // dispatch the BACK press event to function onBackPressed().
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBackPressed();
            return true;
        }
        // dispatch the Menu press event to function onKeyUp().
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
            onKeyUp(KeyEvent.KEYCODE_MENU, event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            // BUG 4.0 APP_MARKET ACTION跳转过来及退出。注释即可
            // exit();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        if (menuWindow != null) {
            menuWindow.dismiss();
            menuWindow = null;
        }
        super.onDestroy();
    }
}
