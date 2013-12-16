package com.tianv.updis.task;

import java.util.Map;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Melvin
 * Date: 13-4-18
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public class CommonFetchDataTask extends BaseTask<Void, Void, Object> {

    /**
     * 参数表
     */
    private Map<String, String> paramsMap = null;

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param context
     * @param taskCallBack 任务回调函数
     * @param params       发送的参数
     */
    public CommonFetchDataTask(Context context, TaskCallBack<Void, Object> taskCallBack,
                               Map<String, String> params) {
        super(taskCallBack, context);
        this.paramsMap = params;
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public Object doInBackground(Void... params) {
        super.doInBackground(params);
        try {
            if (paramsMap == null) {
                return null;
            }
            return CollectResource.getInstance(context).fetchDataFromServer(paramsMap);

        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("ResourceDetailModel", "" + s);
        }
        return null;
    }

    public void onCancelled() {
        super.onCancelled();
    }

    public void onPostExecute(Object result) {
        super.onPostExecute(result);
    }
}
