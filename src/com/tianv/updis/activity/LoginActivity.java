
package com.tianv.updis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melvin.android.base.common.ui.MessageDialog;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.LoginDataModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.LoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.networkinfo.NetWorkInfo;
import com.uucun.android.utils.newstring.MD5Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: LoginActivity.java
 * @Description: TODO
 * @Date 2013-3-24 下午4:54:03
 */
public class LoginActivity extends Activity implements OnClickListener {

    private static final int LOGIN_MSG_LOGINOK = 0;

    private static final int LOGIN_MSG_UNREGDEVICE = 1;

    private static final int LOGIN_MSG_LOGINERROR = 2;


    private static final int LOGIN_MSG_UNREGAPP = 4;

    /**
     * 服务器连接出错,包含网络,JSON出错等等与服务端有关的异常
     */
    private static final int LOGIN_MSG_NETWORKERROR = 3;

    /** UI控件定义 */
    /**
     * 手机验证区域
     */
    private LinearLayout mPhoneVerify = null;

    /**
     * 手机号码
     */
    private TextView mTextPhoneNum = null;

    /**
     * 验证码
     */
    private EditText mEditCodeCheck = null;

    /**
     * 用户验证区域
     */
    private LinearLayout mUserVerify = null;

    /**
     * 用户名
     */
    private EditText mEditUserName = null;

    /**
     * 密码
     */
    private EditText mEditUserPwd = null;

    /**
     * 提交按纽
     */
    private Button mPostBtn = null;

    private LoginTask loginTask = null;

    private String userName = null;

    private String userPwd = null;

    private String verCode = null;

    private String phoneNum = null;

    private ProgressDialog mProgressDialog;

    private boolean checkPhoneNum = false;

    private SharedStore mSharedStore = null;

    private MessageDialog mDialog = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);
        if (mSharedStore == null) {
            mSharedStore = new SharedStore(getApplicationContext(), null);
        }
        if (mDialog == null) {
            mDialog = new MessageDialog(this);
        }
        initView();
    }

    /**
     * 初始化页面控件
     */
    private void initView() {
        mPhoneVerify = (LinearLayout) findViewById(R.id.login_phone_verify);
        mTextPhoneNum = (TextView) findViewById(R.id.txt_phonenum);
        mEditCodeCheck = (EditText) findViewById(R.id.txt_login_code_check);

        mUserVerify = (LinearLayout) findViewById(R.id.login_user_verify);
        mEditUserName = (EditText) findViewById(R.id.txt_login_username);
        mEditUserPwd = (EditText) findViewById(R.id.txt_login_userpwd);

        mPostBtn = (Button) findViewById(R.id.btn_login);
        mPostBtn.setOnClickListener(this);

//        mEditUserName.setText("phonetest");
//        mEditUserPwd.setText("123456");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        String msg = getResources().getString(R.string.updis_login_progress_tips);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);//123
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 当无网络时展现提示信息
     */
    private boolean isNetworkAvailable() {
        if (!NetWorkInfo.isNetworkAvailable(getApplicationContext())) {
            mDialog.showInfo(getResources().getString(R.string.updis_network_error_title),
                    getResources().getString(R.string.updis_network_error_tip));
            return false;
        }
        return true;
    }

    /**
     * 检测文本输入
     */
    private boolean isInputAvailable() {
        userName = mEditUserName.getText().toString();
        userPwd = mEditUserPwd.getText().toString();
        verCode = mEditCodeCheck.getText().toString();
        if (checkPhoneNum) {
            if (verCode.equals("") || verCode == null) {
                UIUtilities.showCustomToast(getApplicationContext(),
                        R.string.updis_login_tips_vercode);
                return false;
            }
        } else {
            if (userName.equals("") || userName == null) {
                UIUtilities.showCustomToast(getApplicationContext(),
                        R.string.updis_login_tips_username);
                return false;
            }
            if (userPwd.equals("") || userPwd == null) {
                UIUtilities.showCustomToast(getApplicationContext(),
                        R.string.updis_login_tips_userpwd);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_login:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPostBtn.getWindowToken(), 0);
                if (!isNetworkAvailable()) {
                    return;
                }
                // 登录
                if (!isInputAvailable()) {
                    return;
                }
                if (loginTask == null || loginTask.getStatus() == AsyncMockTask.Status.FINISHED) {
                    Map<String, String> params = new HashMap<String, String>();
                    if (checkPhoneNum) {
                        params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                                Constant.UrlAlias.LOGIN_PHONENUM_ALIAS);
                    } else {
                        params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                                Constant.UrlAlias.LOGIN_USER_ALIAS);
                    }
                    params.put(Constant.UrlAlias.PARAMS_KEY_USERNAME, userName);
                    params.put(Constant.UrlAlias.PARAMS_KEY_USERPWD, MD5Util.getMD5String(userPwd));
                    params.put(Constant.UrlAlias.PARAMS_KEY_PLAINTEXTPASSWORD, userPwd);
                    params.put(Constant.UrlAlias.PARAMS_KEY_VERCODE, verCode);
                    params.put(Constant.UrlAlias.PARAMS_KEY_PHONENUM, phoneNum);
                    loginTask = new LoginTask(getApplicationContext(),
                            getLoginTaskCallBack(LoginActivity.this), params);

                    loginTask.execute();
                }
                break;
        }
    }

    private final Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            LoginDataModel loginDataModel = (LoginDataModel) msg.obj;
            switch (msg.what) {
                case LOGIN_MSG_UNREGAPP:
                    mDialog.showInfo(getResources().getString(R.string.updis_login_logingerror_title),
                            getResources().getString(R.string.updis_need_reg));
                    break;
                case LOGIN_MSG_LOGINOK:
                    // 登录成功
                    Intent intent = new Intent(LoginActivity.this, MainActivityGroup.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_MSG_NETWORKERROR:
                    // 连接超时
                    mDialog.showInfo(getResources().getString(R.string.updis_service_error_title),
                            getResources().getString(R.string.updis_service_error_tip));
                    break;
                case LOGIN_MSG_UNREGDEVICE:
                    // 设备未注册 显示设备注册界面
                    Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_in);
                    Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_out);
                    mUserVerify.setVisibility(View.GONE);
                    mUserVerify.startAnimation(slideOut);
                    mPhoneVerify.setVisibility(View.VISIBLE);
                    mPhoneVerify.startAnimation(slideIn);
                    checkPhoneNum = true;
                    phoneNum = loginDataModel.phoneNum;
                    mTextPhoneNum.setText(phoneNum);
                    break;
                case LOGIN_MSG_LOGINERROR:
                    // 登录失败
                    mDialog.showInfo(
                            getResources().getString(R.string.updis_login_logingerror_title),
                            loginDataModel.msg);
                    break;
            }
        }
    };

    /**
     * 用户登录回调
     *
     * @param currentActivity
     * @return
     */
    public TaskCallBack<Void, LoginDataModel> getLoginTaskCallBack(final Activity currentActivity) {
        return new TaskCallBack<Void, LoginDataModel>() {
            public void beforeDoingTask() {
                showProgressDialog();
            }

            public void doingTask() {
            }

            public void onCancel() {

            }

            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(LoginDataModel eParam, AppException appException) {
                // TODO Auto-generated method stub
                Message message = null;
                if (appException != null || eParam == null) {
                    message = sHandler.obtainMessage(LOGIN_MSG_NETWORKERROR);
                    message.obj = eParam;
                    message.sendToTarget();
                    return;
                }

                if ("1".equals(eParam.success)) {
                    if ("0".equals(eParam.registered)) {
                        // 设备未注册
                        message = sHandler.obtainMessage(LOGIN_MSG_UNREGDEVICE);
                        message.obj = eParam;
                        message.sendToTarget();
                        return;
                    } else if ("99".equals(eParam.registered)) {
                        // 用户未注册使用手机
                        message = sHandler.obtainMessage(LOGIN_MSG_UNREGAPP);
                        message.obj = eParam;
                        message.sendToTarget();
                        return;
                    } else {
                        message = sHandler.obtainMessage(LOGIN_MSG_LOGINOK);
                        // 登录成功
                        mSharedStore.putBoolean(Constant.UPDIS_STORE_KEY_LOGINFLAG, true);
                        mSharedStore.putString(Constant.UPDIS_STORE_KEY_USERNAME,
                                userName);
                        mSharedStore.putString(Constant.UPDIS_STORE_KEY_USERPWD,
                                MD5Util.getMD5String(userPwd));
                        mSharedStore.putString(Constant.UPDIS_STORE_KEY_PLAINTEXTPASSWORD,
                                userPwd);
                        mSharedStore.putString(Constant.UPDIS_STORE_KEY_ISSPECIALUSER,
                                eParam.isSpecailUser);
                        message.obj = eParam;
                        message.sendToTarget();
                        return;
                    }
                } else {
                    message = sHandler.obtainMessage(LOGIN_MSG_LOGINERROR);
                    message.obj = eParam;
                    message.sendToTarget();
                    return;
                }
            }
        };
    }
}
