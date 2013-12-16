
package com.tianv.updis.task;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.network.CollectResource;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: ResourcesLoadTask.java
 * @Description: TODO
 * @Date 2013-3-24 下午2:42:55
 */
public class ResourcesLoadTask extends BaseTask<String, ResourceModel, ArrayList<ResourceModel>> {

    /**
     * 分页回调
     */
    private PageFetcher pageFetch;

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
     * @param resultFlagFetch 搜索标志位回调函数
     * @param params          发送的参数
     */
    public ResourcesLoadTask(Context context,
                             TaskCallBack<ResourceModel, ArrayList<ResourceModel>> taskCallBack,
                             PageFetcher pageFetch, Map<String, String> params) {
        super(taskCallBack, context);
        this.pageFetch = pageFetch;
        this.paramsMap = params;
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public ArrayList<ResourceModel> doInBackground(String... params) {
        super.doInBackground(params);
        try {
            if (paramsMap == null) {
                return null;
            }
            return CollectResource.getInstance(context).fetchResource(pageFetch, paramsMap);

        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("ResourcesLoadTask", "" + s);
        }
        return null;
    }

    public void onCancelled() {
        super.onCancelled();
    }

    public void onPostExecute(ArrayList<ResourceModel> result) {
        super.onPostExecute(result);
    }

}
