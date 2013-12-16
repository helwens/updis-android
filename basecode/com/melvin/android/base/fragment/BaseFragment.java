/**
 * @Title: BaseFragment.java
 * @Package com.uucun.android.cms.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author guojun
 * @date 2012-1-11 上午10:45:33
 * @version V1.0
 */

package com.melvin.android.base.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melvin.android.base.common.ui.MessageDialog;
import com.tianv.updis.AppException;
import com.tianv.updis.R;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * @author guojun
 * @ClassName: 含有btn切换界面的公共Fragment类
 * @Description:
 * @date 2012-1-11 上午10:45:33
 */

public abstract class BaseFragment extends UUFragment {

    /**
     * 用于判断btn的3个还是2个
     */
    protected int btnCount;

    /**
     * 动态定义btn的text
     */
    protected String[] btnText;

    /**
     * 本模块的代码标识
     */
    public String moduleCode = "";

    /**
     * 按钮布局
     */
    protected ViewGroup btnLayout = null;

    /**
     * 中间View布局
     */
    protected LinearLayout bodyLayout = null;

    /**
     * 当前选中的view索引
     */
    public int selectedViewIndex = 0;

    /**
     * View数组
     */
    protected List<View> childViews = null;

    /**
     * 按钮数组
     */
    protected List<Button> buttons = null;

    protected LayoutInflater inflater = null;

    /**
     * 用于控制按钮背景状态
     */
    protected boolean flag = false;

    protected MessageDialog mDialog = null;

    protected ProgressDialog mProgressDialog;

    /**
     * 基础构造方法
     *
     * @param activity
     * @param moduleCode 本模块代码
     */
    public BaseFragment(Activity activity, String moduleCode) {
        super(activity);
        this.moduleCode = moduleCode;
        initViewAndBtnNumber();
        inflater = LayoutInflater.from(activity);
        onCreateView();
        if (mDialog == null) {
            mDialog = new MessageDialog(activity);
        }
    }

    protected void initViewAndBtnNumber() {
        buttons = new ArrayList<Button>();
        childViews = new ArrayList<View>();
    }

    public View onCreateView() {
        /*** 使用老的View进行 **/
        if (view != null) {
            return view;
        }
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.common_linearlayout, null);
        btnLayout = (ViewGroup) layout.findViewById(R.id.btn_layout_id);
        bodyLayout = (LinearLayout) layout.findViewById(R.id.body_layout_id);
        View layoutbtn = null;
        /** 如果没有按钮 **/
        if (btnCount <= 0 || btnText == null || btnText.length <= 0) {
            btnLayout.setVisibility(View.GONE);
        } else {
            btnLayout.setVisibility(View.VISIBLE);
            layoutbtn = setBtnLayout(mActivity, inflater);
            if (layoutbtn != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                btnLayout.addView(layoutbtn, layoutParams);
            }
        }
        initNavBtn();
        view = layout;
        return layout;
    }

    /**
     * 初始化导航按钮
     *
     * @Title: initNavBtn
     */
    private void initNavBtn() {
        if (null != btnLayout) {
            if (btnCount == 2) {
                Button button1 = (Button) btnLayout.findViewById(R.id.btn_first_id);
                Button button2 = (Button) btnLayout.findViewById(R.id.btn_second_id);
                buttons.add(button1);
                buttons.add(button2);
            } else if (btnCount == 3) {
                Button button1 = (Button) btnLayout.findViewById(R.id.common_btn_first_id);
                Button button2 = (Button) btnLayout.findViewById(R.id.common_btn_second_id);
                Button button3 = (Button) btnLayout.findViewById(R.id.common_btn_three_id);
                buttons.add(button1);
                buttons.add(button2);
                buttons.add(button3);
            }

        }
        if (buttons == null)
            return;
        int length = buttons.size();
        for (int i = 0; i < length; i++) {
            final int index = i;
            Button button = buttons.get(i);
            button.setText(btnText[i]);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    changeToView(index);
                }
            });
        }

    }

    /**
     * 切换到哪个视图
     *
     * @param index
     * @Title: changeToView
     */
    public void changeToView(int index) {
        selectedViewIndex = index;
        changeToCurrentView();
        onChangeView(index, childViews.get(index));
    }

    /**
     * 切换到当前的视图
     */
    public void changeToCurrentView() {
        if (bodyLayout != null)
            bodyLayout.removeAllViews();

        /*** 生成新的body页面 **/
        if (childViews.size() <= selectedViewIndex) {
            View view = setBodyView(mActivity, inflater, selectedViewIndex);
            childViews.add(view);
        }
        /** 加入到body布局中 **/
        if (childViews.get(selectedViewIndex) != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            bodyLayout.addView(childViews.get(selectedViewIndex), layoutParams);
        }
        /** 处理按钮 **/
        if (buttons != null && selectedViewIndex > -1 && selectedViewIndex < buttons.size()) {
            /** 用于控制按钮选中获取焦点后的效果guojun */
            int count = buttons.size();
            for (int i = 0; i < count; i++) {
                Button btn1 = buttons.get(i);
                if (selectedViewIndex == i) {
                    btn1.requestFocus();
                    btn1.setBackgroundResource(R.drawable.left_btn_pressed);
                    btn1.setTextColor(mActivity.getResources().getColor(R.color.state_blue_color));
                    continue;
                }
                btn1.setBackgroundResource(R.drawable.left_btn_selector);
                btn1.setTextColor(mActivity.getResources().getColor(R.color.black));
            }
        }
    }

    /**
     * 更改View的切换，但是图进行切换的时候，就会执行此方法
     *
     * @param index 索引
     * @param view  视图
     * @Title: onChangeView
     */
    public abstract void onChangeView(int index, View view);

    /**
     * 设置Btn导航的布局
     *
     * @param context
     * @param inflater
     * @return
     * @Title: setBtnLayout
     */
    public View setBtnLayout(Context context, LayoutInflater inflater) {
        return null;
    }

    /**
     * 设置Body中的View，一个View只会执行一次
     *
     * @param context
     * @param inflater
     * @param index
     * @return
     * @Title: setBodyView
     */
    public abstract View setBodyView(Context context, LayoutInflater inflater, int index);

    /**
     * 显示回调
     *
     * @Title: onDisplay
     */
    public void onDisplay() {
        changeToView(selectedViewIndex);
        setCallback();
    }

    /**
     * 设置回调
     */
    public void setCallback() {

    }

    /**
     * 网络发生错误情况
     *
     * @param type
     */
    public void onError(int type) {
        bodyLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        View v = LayoutInflater.from(mActivity).inflate(R.layout.no_networking_status, null);
        ImageView img = (ImageView) v.findViewById(R.id.img_id);
        TextView to = (TextView) v.findViewById(R.id.text_one);
        TextView ts = (TextView) v.findViewById(R.id.text_second);
        TextView tt = (TextView) v.findViewById(R.id.text_three);
        Button tryBtn = (Button) v.findViewById(R.id.tryagain_btn_id);
        bodyLayout.addView(v, layoutParams);
        if (type == AppException.NO_NETWORK_ERROR_CODE) {
            img.setBackgroundResource(R.drawable.no_networking_icon);
            to.setText(R.string.no_networking_text);
            ts.setVisibility(View.VISIBLE);
            ts.setText(R.string.check_networking);
            tt.setText(R.string.click_try_again);
            tryBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
                        // UIUtilities.showCustomToast(mActivity,
                        // R.string.network_error_tip);
                    } else {
                        changeToCurrentView();
                        onRetry();
                    }
                }
            });
        } else {
            showDataError(img, to, ts, tt, tryBtn);
        }
    }

    /**
     * 除了无联网，其他错误状态下显示的界面
     *
     * @param type
     */
    private void showDataError(ImageView img, TextView to, TextView ts, TextView tt, Button tryBtn) {
        img.setBackgroundResource(R.drawable.data_error_icon);
        to.setText(R.string.data_error);
        ts.setVisibility(View.GONE);
        tt.setText(R.string.data_get);
        tryBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                changeToCurrentView();
                onRetry();
            }
        });
    }

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setProgressStyle(0);
        String msg = mActivity.getResources().getString(R.string.updis_login_progress_tips);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * 重试处理方法
     */
    public abstract void onRetry();

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
