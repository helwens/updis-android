package com.tianv.updis.task;

import android.content.Context;
import android.os.Message;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.model.LoginDataModel;
import com.uucun.android.sharedstore.SharedStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Melvin on 13-5-18.
 */
public class ReLoginTask {
    private Context mContext;
    private SharedStore sharedStore = null;
    private LoginTask loginTask = null;
    private ReLoginTaskListener reLoginTaskListener = null;

    public ReLoginTask(Context context) {
        mContext = context;
        sharedStore = new SharedStore(mContext, null);
    }

    public interface ReLoginTaskListener {
        public void loginOK();
    }

    public void setReLoginTaskListener(ReLoginTaskListener listener) {
        reLoginTaskListener = listener;
    }


    public void login() {
        String userName = sharedStore.getString(Constant.UPDIS_STORE_KEY_USERNAME, "");
        String userPwd = sharedStore.getString(Constant.UPDIS_STORE_KEY_USERPWD, "");
        String plainTextPassword = sharedStore.getString(Constant.UPDIS_STORE_KEY_PLAINTEXTPASSWORD, "");
        if (loginTask == null || loginTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            Map<String, String> params = new HashMap<String, String>();

            params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                    Constant.UrlAlias.LOGIN_USER_ALIAS);

            params.put(Constant.UrlAlias.PARAMS_KEY_USERNAME, userName);
            params.put(Constant.UrlAlias.PARAMS_KEY_USERPWD, userPwd);
            params.put(Constant.UrlAlias.PARAMS_KEY_PLAINTEXTPASSWORD, plainTextPassword);
            params.put(Constant.UrlAlias.PARAMS_KEY_VERCODE, "");
            params.put(Constant.UrlAlias.PARAMS_KEY_PHONENUM, "");
            loginTask = new LoginTask(mContext,
                    getLoginTaskCallBack(), params);

            loginTask.execute();
        }
    }

    /**
     * 用户登录回调
     *
     * @return
     */
    public TaskCallBack<Void, LoginDataModel> getLoginTaskCallBack() {
        return new TaskCallBack<Void, LoginDataModel>() {
            public void beforeDoingTask() {
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

                if ("1".equals(eParam.success)) {
                    if (reLoginTaskListener != null)
                        reLoginTaskListener.loginOK();
                }
            }
        };
    }
}
