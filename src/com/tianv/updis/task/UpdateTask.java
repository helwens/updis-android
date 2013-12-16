
package com.tianv.updis.task;

import android.content.Context;
import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: PostMessageTask.java
 * @Description: TODO
 * @Date 2013-4-9 下午1:54:45
 */
public class UpdateTask extends BaseTask<Void, Void, String> {

    /**
     * 参数表
     */
    private Map<String, String> paramsMap = null;

    public UpdateTask(Context context, TaskCallBack<Void, String> taskCallBack,
                      Map<String, String> params) {
        super(taskCallBack, context);
        this.paramsMap = params;
        // TODO Auto-generated constructor stub
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public String doInBackground(Void... params) {
        super.doInBackground(params);
        if (paramsMap == null) {
            return null;
        }
        try {
            return CollectResource.getInstance(context).updateApp(paramsMap);
        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("PostCommentTask", "" + s);
        }
        return null;
    }

    public void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void onCancelled() {
        super.onCancelled();
    }

    public void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
