
package com.tianv.updis.task;

import java.util.Map;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.LoginDataModel;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: LoginTask.java
 * @Description: TODO
 * @Date 2013-4-9 下午1:54:45
 */
public class LoginTask extends BaseTask<Void, Void, LoginDataModel> {

    /**
     * 参数表
     */
    private Map<String, String> paramsMap = null;

    public LoginTask(Context context, TaskCallBack<Void, LoginDataModel> taskCallBack,
                     Map<String, String> params) {
        super(taskCallBack, context);
        this.paramsMap = params;
        // TODO Auto-generated constructor stub
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public LoginDataModel doInBackground(Void... params) {
        super.doInBackground(params);
        if (paramsMap == null) {
            return null;
        }
        try {
            return CollectResource.getInstance(context).fetchLoginData(paramsMap);
        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("LoginTask", "" + s);
        }
        return null;
    }

    public void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void onCancelled() {
        super.onCancelled();
    }

    public void onPostExecute(LoginDataModel result) {
        super.onPostExecute(result);
    }
}
