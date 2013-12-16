
package com.tianv.updis.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.melvin.android.base.common.ui.MTextView;
import com.melvin.android.base.fragment.BaseFragment;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.PostMessageTask;
import com.tianv.updis.task.ReLoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.networkinfo.NetWorkInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMsgFragment extends BaseFragment implements View.OnClickListener,
        DialogInterface.OnClickListener, OnItemClickListener {

    private Map<String, String> hashMap = null;
    private TextView mTextViewCategory = null;
    private EditText mTextViewTitle = null;
    private EditText mTextViewDept = null;
    private EditText mTextViewContent = null;
    private Button mBtnPost = null;
    private SharedStore sharedStore = null;
    private String[] categoryArr = null;

    private String category = null;
    private String dept = null;
    private String smsContent = null;
    private String title = null;
    private String content = null;

    private PostMessageTask postMessageTask = null;


    public SendMsgFragment(Activity activity, String moduleCode) {
        super(activity, moduleCode);
        initView();
        sharedStore = new SharedStore(activity, null);
        String isSpecailUser = sharedStore.getString(Constant.UPDIS_STORE_KEY_ISSPECIALUSER, "0");
        if (isSpecailUser.equals("0")) {
            categoryArr = new String[3];
        } else {
            categoryArr = new String[4];
            categoryArr[3] = "在谈项目";
        }
        categoryArr[0] = "通知";
        categoryArr[1] = "招投标信息";
        categoryArr[2] = "畅所欲言";
    }

    private void initView() {
        hashMap = new HashMap<String, String>();
    }

    @Override
    public View setBtnLayout(Context context, LayoutInflater inflater) {
        return null;
    }

    public View setBodyView(Context context, LayoutInflater inflater, int index) {
        View view = inflater.inflate(R.layout.send_msg_layout, null);
        mTextViewCategory = (TextView) view.findViewById(R.id.txt_query_category);
        mTextViewCategory.setOnClickListener(this);
        mTextViewDept = (EditText) view.findViewById(R.id.txt_query_dept);
        mTextViewTitle = (EditText) view.findViewById(R.id.txt_query_title);
        mTextViewContent = (EditText) view.findViewById(R.id.txt_query_content);
        mBtnPost = (Button) view.findViewById(R.id.btn_query);
        mBtnPost.setOnClickListener(this);

        //设置EditText的显示方式为多行文本输入
        mTextViewContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //文本显示的位置在EditText的最上方
        mTextViewContent.setGravity(Gravity.TOP);
        //改变默认的单行模式
        mTextViewContent.setSingleLine(false);
        //水平滚动设置为False
        mTextViewContent.setHorizontallyScrolling(false);

        hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS, this.moduleCode);
        return view;
    }

    public void onChangeView(int index, View view) {
//        onLoadingResource();
    }

    private void postMessage() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            return;
        }
        String categoryType = "1";
        if (category.equals("通知")) {
            categoryType = "1";
        }
        if (category.equals("招投标信息")) {
            categoryType = "2";
        }
        if (category.equals("畅所欲言")) {
            categoryType = "3";
        }
        if (category.equals("在谈项目")) {
            categoryType = "5";
        }
        hashMap.put(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE, categoryType);
        hashMap.put(Constant.UrlAlias.PARAMS_KEY_TITLE, title);
        hashMap.put(Constant.UrlAlias.PARAMS_KEY_CONTENT, content);
        if (categoryType.equals("1")) {
            //通知类消息
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_PUBLISHDEPT, dept);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_SMSCONTENT, smsContent);
        }
        //发送消息
        if (postMessageTask == null
                || postMessageTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            /** 数据接口 */
            postMessageTask = new PostMessageTask(mActivity, getPostMessageTask()
                    , hashMap);
            postMessageTask.execute();
        }
    }

    private TaskCallBack<Void, String> getPostMessageTask() {
        TaskCallBack<Void, String> taskCallBask = new TaskCallBack<Void, String>() {
            @Override
            public void beforeDoingTask() {
                showProgressDialog();
            }

            @Override
            public void doingTask() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(String eParam, AppException appException) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                if (appException != null && eParam == null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时
                        ReLoginTask reLoginTask = new ReLoginTask(mActivity);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                }
                if (eParam != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(eParam);
                        if (jsonObject.getString("success").equals("1")) {
                            UIUtilities.showToast(mActivity, "消息发送成功");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return taskCallBask;
    }

    ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
            postMessage();
        }
    };

    public void onDisplay() {
        super.onDisplay();
    }

    @Override
    public void onRetry() {
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
        category = mTextViewCategory.getText().toString();
        title = mTextViewTitle.getText().toString();
        content = mTextViewContent.getText().toString();

        if (UIUtilities.isNull(category)) {
            UIUtilities.showCustomToast(mActivity,
                    R.string.updis_sendmsg_tips_category);
            return false;
        }
        if (UIUtilities.isNull(title)) {
            UIUtilities.showCustomToast(mActivity,
                    R.string.updis_sendmsg_tips_title);
            return false;
        }
        if (UIUtilities.isNull(content)) {
            UIUtilities.showCustomToast(mActivity,
                    R.string.updis_sendmsg_tips_content);
            return false;
        }


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
                // post
                postMessage();
                break;
            case R.id.txt_query_category:
                mDialog.showInfo(0, "选择分类", categoryArr, null, categorySelect);
                break;
        }
    }

    DialogInterface.OnClickListener categorySelect = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Logger.i("which", String.valueOf(which));
            mTextViewCategory.setText(categoryArr[which]);
            dialog.dismiss();
        }
    };

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
