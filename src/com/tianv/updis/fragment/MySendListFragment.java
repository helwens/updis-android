
package com.tianv.updis.fragment;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.melvin.android.base.fragment.BaseFragment;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

public class MySendListFragment extends BaseFragment implements OnClickListener,
        DialogInterface.OnClickListener, OnItemClickListener {

    private Map<String, String> hashMap = null;

    public MySendListFragment(Activity activity, String moduleCode) {
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

    public View setBodyView(Context context, LayoutInflater inflater, int index) {
        View view = inflater.inflate(R.layout.mysendlist_layout, null);

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

        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_query:
                // InputMethodManager imm = (InputMethodManager) mActivity
                // .getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(mQueryPost.getWindowToken(), 0);
                if (!isNetworkAvailable()) {
                    return;
                }
                // 登录

                if (!isInputAvailable()) {
                    return;
                }
                // query

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
