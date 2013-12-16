
package com.tianv.updis.fragment;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melvin.android.base.fragment.BaseFragment;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * 修改密码
 *
 * @author Melvin
 */
public class ChangePasswordFragment extends BaseFragment implements OnClickListener,
        DialogInterface.OnClickListener, OnItemClickListener {

    private EditText mEditNowPwd = null;

    private EditText mEditNewPwd = null;

    private EditText mEditNewPwd2 = null;

    /**
     * 当前密码
     */
    private String nowPwd = null;

    /**
     * 新密码
     */
    private String newPwd = null;

    /**
     * 重复新密码
     */
    private String newPwd2 = null;

    private Button mQueryPost = null;

    private Map<String, String> hashMap = null;

    public ChangePasswordFragment(Activity activity, String moduleCode) {
        super(activity, moduleCode);
        initView();
    }

    private void initView() {
        hashMap = new HashMap<String, String>();

    }

    @Override
    public View setBtnLayout(Context context, LayoutInflater inflater) {
        return null;
    }

    public View setBodyView(Context context, LayoutInflater inflate, int index) {
        View view = inflate.inflate(R.layout.changewd_layout, null);

        mEditNowPwd = (EditText) view.findViewById(R.id.txt_pwd_now);
        mEditNewPwd = (EditText) view.findViewById(R.id.txt_pwd_new);
        mEditNewPwd2 = (EditText) view.findViewById(R.id.txt_pwd_new2);
        mQueryPost = (Button) view.findViewById(R.id.btn_query);
        mQueryPost.setOnClickListener(this);

        hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS, this.moduleCode);
        return view;
    }

    public void onChangeView(int index, View view) {
        onLoadingResource();
    }

    private void onLoadingResource() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            return;
        }
    }

    public void onDisplay() {
        super.onDisplay();
    }

    @Override
    public void onRetry() {
        onLoadingResource();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /**
     * 当无网络时展现提示信息
     */
    private boolean isNetworkAvailable() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            mDialog.showInfo(
                    mActivity.getResources().getString(R.string.updis_network_error_title),
                    mActivity.getResources().getString(R.string.updis_network_error_tip));
            return false;
        }
        return true;
    }

    /**
     * 检测文本输入
     */
    private boolean isInputAvailable() {
        nowPwd = mEditNowPwd.getText().toString();
        newPwd = mEditNewPwd.getText().toString();
        newPwd2 = mEditNewPwd2.getText().toString();

        if (nowPwd.equals("") || nowPwd == null) {
            UIUtilities.showCustomToast(mActivity, R.string.updis_changepwd_nowpwd_tips);
            return false;
        }

        if (newPwd.equals("") || newPwd == null) {
            UIUtilities.showCustomToast(mActivity, R.string.updis_changepwd_newpwd_tips);
            return false;
        }
        if (newPwd2.equals("") || newPwd2 == null) {
            UIUtilities.showCustomToast(mActivity, R.string.updis_changepwd_newpwd2_tips);
            return false;
        }
        if (!newPwd.equals(newPwd2)) {
            UIUtilities.showCustomToast(mActivity, R.string.updis_changepwd_newpwderror_tips);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_query:
                InputMethodManager imm = (InputMethodManager) mActivity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mQueryPost.getWindowToken(), 0);
                if (!isNetworkAvailable()) {
                    return;
                }
                // 登录

                if (!isInputAvailable()) {
                    return;
                }
                // query
                hashMap.put(Constant.UrlAlias.PARAMS_KEY_NOWPWD, nowPwd);
                hashMap.put(Constant.UrlAlias.PARAMS_KEY_NEWPWD, newPwd);

                break;
        }
    }

    /**
     * 查询结果回调
     *
     * @param currentActivity
     * @return
     */
    public TaskCallBack<Void, PersonModel> getQueryTaskCallBack(final Activity currentActivity) {
        return new TaskCallBack<Void, PersonModel>() {
            public void beforeDoingTask() {
                // showProgressDialog();
            }

            public void doingTask() {
            }

            public void onCancel() {

            }

            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(PersonModel eParam, AppException appException) {
                // TODO Auto-generated method stub
            }
        };
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        dialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

}
