
package com.tianv.updis.task;

import java.util.Map;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.ResourceDetailModel;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CommonDetailTask.java
 * @Description: TODO
 * @Date 2013-4-13 下午3:31:14
 */
public class CommonDetailTask extends BaseTask<Void, Void, ResourceDetailModel> {

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
     * @param taskCallBack    任务回调函数
     * @param pageFetcher     页码回调函数
     * @param resultFlagFetch 搜索标志位回调函数
     * @param params          发送的参数
     */
    public CommonDetailTask(Context context, TaskCallBack<Void, ResourceDetailModel> taskCallBack,
                            Map<String, String> params) {
        super(taskCallBack, context);
        this.paramsMap = params;
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public ResourceDetailModel doInBackground(Void... params) {
        super.doInBackground(params);
        try {
            if (paramsMap == null) {
                return null;
            }
            return CollectResource.getInstance(context).fetchResourceDetail(paramsMap);

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

    public void onPostExecute(ResourceDetailModel result) {
        super.onPostExecute(result);
    }

}
