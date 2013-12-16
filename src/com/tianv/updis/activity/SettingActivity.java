
package com.tianv.updis.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;
import com.melvin.android.base.activity.BaseActivity;
import com.melvin.android.base.common.ui.IMessageDialogListener;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.fragment.CommonWebView;
import com.tianv.updis.model.LoginDataModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.LoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.tianv.updis.task.UpdateApp;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.apkinfo.ApkUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: SettingActivity.java
 * @Description: TODO
 * @Date 2013-3-22 下午5:24:49
 */
public class SettingActivity extends BaseActivity implements OnClickListener,
        IMessageDialogListener, CommonWebView.CommonWebViewListener {
    private static final int VIEW_BACK_LIST = 1;
    private int LOGOU_CONFIRM = 10010;

    private CheckBox mCheckNotice = null;

    private CheckBox mCheckBidding = null;

    private CheckBox mCheckTalk = null;

    private CheckBox mCheckAmateur = null;

    private CheckBox mCheckProject = null;

    private RadioGroup mRadioGroup = null;

    private RadioButton mRadioTimeOpen = null;

    private RadioButton mRadioTimeNight = null;

    private RadioButton mRadioTimeClose = null;

    private LinearLayout mAboutView = null;

    private LinearLayout mFeedBackView = null;

    private LinearLayout mVersionView = null;

    private LinearLayout mCheckVersionView = null;

    private Button mLogout = null;

    private TextView mTextVersion = null;

    private LoginTask loginTask = null;

    private CommonWebView aboutView = null;

    private CommonWebView versionView = null;

    private LinearLayout settingPanel = null;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_layout);
        mContext = SettingActivity.this;
        bodyLayout = (LinearLayout) findViewById(R.id.bodyview);
        settingPanel = (LinearLayout) findViewById(R.id.setting_panel);
        initView();

        loadResourceData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        mCheckNotice = (CheckBox) findViewById(R.id.push_title_notice);
        mCheckBidding = (CheckBox) findViewById(R.id.push_title_bidding);
        mCheckTalk = (CheckBox) findViewById(R.id.push_title_talk);
        mCheckAmateur = (CheckBox) findViewById(R.id.push_title_amateur);
        mCheckProject = (CheckBox) findViewById(R.id.push_title_project);

        mCheckNotice.setOnCheckedChangeListener(mCheckedChange);
        mCheckBidding.setOnCheckedChangeListener(mCheckedChange);
        mCheckTalk.setOnCheckedChangeListener(mCheckedChange);
        mCheckAmateur.setOnCheckedChangeListener(mCheckedChange);
        mCheckProject.setOnCheckedChangeListener(mCheckedChange);

        SharedStore sharedStore = new SharedStore(this, null);
        String isSpecailUser = sharedStore.getString(Constant.UPDIS_STORE_KEY_ISSPECIALUSER, "0");
        if (isSpecailUser.equals("1")) {
            mCheckProject.setVisibility(View.VISIBLE);

        }

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group_push_time);
        mRadioGroup.setOnCheckedChangeListener(mChangeRadio);
        mRadioTimeOpen = (RadioButton) findViewById(R.id.push_time_open);
        mRadioTimeNight = (RadioButton) findViewById(R.id.push_time_night);
        mRadioTimeClose = (RadioButton) findViewById(R.id.push_time_close);

        mAboutView = (LinearLayout) findViewById(R.id.push_setting_about_line);
        mFeedBackView = (LinearLayout) findViewById(R.id.push_setting_feedback_line);
        mVersionView = (LinearLayout) findViewById(R.id.push_setting_version_line);
        mCheckVersionView = (LinearLayout) findViewById(R.id.push_setting_check_version_line);

        mLogout = (Button) findViewById(R.id.btn_logout);

        mTextVersion = (TextView) findViewById(R.id.txt_version);
        mTextVersion.setText(ApkUtils.getAppVersionName(getApplicationContext(), "1.0"));
        mLogout.setOnClickListener(this);
        mAboutView.setOnClickListener(this);
        mFeedBackView.setOnClickListener(this);
        mVersionView.setOnClickListener(this);
        mCheckVersionView.setOnClickListener(this);

        super.initView();
    }

    private OnCheckedChangeListener mCheckedChange = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.push_title_notice:
                    mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_PUSH_NOTICE, arg1);
                    break;
                case R.id.push_title_bidding:
                    mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_PUSH_BIDDING, arg1);
                    break;
                case R.id.push_title_talk:
                    mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_PUSH_TALK, arg1);
                    break;
                case R.id.push_title_amateur:
                    mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_PUSH_AMATEUR, arg1);
                    break;
                case R.id.push_title_project:
                    mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_PUSH_PROJECT, arg1);
                    break;

            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case R.id.push_time_open:
                    mSharedStore.putString(Constant.UPDIS_STORE_KEY_PUSH_TIME,
                            Constant.UPDIS_PUSH_OPEN);
                    JPushInterface.init(getApplicationContext());
                    JPushInterface.resumePush(getApplicationContext());
                    break;
                case R.id.push_time_close:
                    mSharedStore.putString(Constant.UPDIS_STORE_KEY_PUSH_TIME,
                            Constant.UPDIS_PUSH_CLOSE);
                    JPushInterface.stopPush(getApplicationContext());
                    break;
                case R.id.push_time_night:
                    mSharedStore.putString(Constant.UPDIS_STORE_KEY_PUSH_TIME,
                            Constant.UPDIS_PUSH_NIGHT);
                    Set<Integer> days = new HashSet<Integer>();
                    days.add(0);
                    days.add(1);
                    days.add(2);
                    days.add(3);
                    days.add(4);
                    days.add(5);
                    days.add(6);
                    JPushInterface.setPushTime(getApplicationContext(), days, 19, 23);
                    break;
            }
        }
    };

    @Override
    public void loadResourceData() {
        // TODO Auto-generated method stub
        String pushTimeValue = mSharedStore.getString(Constant.UPDIS_STORE_KEY_PUSH_TIME, "1");
        if (pushTimeValue == "1") {
            mRadioTimeOpen.setChecked(true);
        } else if (pushTimeValue == "0") {
            mRadioTimeClose.setChecked(true);
        } else {
            mRadioTimeNight.setChecked(true);
        }
        boolean pushNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_NOTICE, true);
        boolean pushBidding = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_BIDDING, true);
        boolean pushTalk = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_TALK, true);
        boolean pushAmateur = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_AMATEUR, true);
        boolean pushProject = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_PROJECT, true);

        mCheckNotice.setChecked(pushNotice);
        mCheckBidding.setChecked(pushBidding);
        mCheckTalk.setChecked(pushTalk);
        mCheckAmateur.setChecked(pushAmateur);
        mCheckProject.setChecked(pushProject);
    }

    @Override
    public void navLeftClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void navRightClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.push_setting_about_line:
//                if (aboutView == null) {
//                    aboutView = new CommonWebView(mContext);
//                    aboutView.setWebViewListener(this);
//                }
//                aboutView.setUrl("about.html");
//                bodyLayout.removeAllViews();
//                bodyLayout.addView(aboutView.createView());
//                mNavLeftButton.setVisibility(View.VISIBLE);

                UIUtilities.showWebContent(this,"about.html");
                break;
            case R.id.push_setting_feedback_line:
                break;
            case R.id.push_setting_version_line:

                UIUtilities.showWebContent(this,"version.html");


//                if (aboutView == null) {
//                    aboutView = new CommonWebView(mContext);
//                    aboutView.setWebViewListener(this);
//                }
//                aboutView.setUrl("version.html");
//                bodyLayout.removeAllViews();
//                bodyLayout.addView(aboutView.createView());
//                mNavLeftButton.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_logout:
                mDialog.showConfirm(LOGOU_CONFIRM, getString(R.string.updis_logout),
                        getString(R.string.updis_logout_confirm), this);
            case R.id.push_setting_check_version_line:
                UpdateApp.getInstance(mContext).update(true);
                break;
        }
    }

    @Override
    public void onDialogClickOk(int requestCode) {
        // TODO Auto-generated method stub
        if (requestCode == LOGOU_CONFIRM) {
            mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_LOGINFLAG, false);

            if (loginTask == null || loginTask.getStatus() == AsyncMockTask.Status.FINISHED) {
                Map<String, String> params = new HashMap<String, String>();

                params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                        Constant.UrlAlias.LOGIN_USER_LOGOUT_ALIAS);

                loginTask = new LoginTask(getApplicationContext(),
                        new TaskCallBack<Void, LoginDataModel>() {

                            @Override
                            public void onCancel() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void endTask(LoginDataModel eParam, AppException appException) {
                                // TODO Auto-generated method stub
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }

                            @Override
                            public void doingTask() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void doingProgress(Void... fParam) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void beforeDoingTask() {
                                // TODO Auto-generated method stub

                            }
                        }, params);

                loginTask.execute();
            }

        }

    }

    @Override
    public void onDialogClickCancel(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDialogClickClose(int requestCode) {
        // TODO Auto-generated method stub

    }

    private final Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            Animation fade = AnimationUtils.loadAnimation(mContext,
                    R.anim.fade);
            switch (msg.what) {
                case VIEW_BACK_LIST:
                    bodyLayout.removeAllViews();
                    settingPanel.startAnimation(fade);
                    bodyLayout.addView(settingPanel);
                    break;
            }
        }
    };

    @Override
    public void back() {
        Message message = sHandler.obtainMessage(VIEW_BACK_LIST);
        message.sendToTarget();
    }
}
