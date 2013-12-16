
package com.tianv.updis.task;

import android.content.Context;
import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.network.CollectResource;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: ResourcesLoadTask.java
 * @Description: TODO
 * @Date 2013-3-24 下午2:42:55
 */
public class CommentListLoadTask extends BaseTask<String, CommentModel, ArrayList<CommentModel>> {

    /**
     * 分页回调
     */
    private PageFetcher pageFetch;

    /**
     * 参数表
     */
    private Map<String, String> paramsMap = null;

    /**
     *
     * @param context
     * @param taskCallBack
     * @param pageFetch
     * @param params
     */
    public CommentListLoadTask(Context context,
                               TaskCallBack<CommentModel, ArrayList<CommentModel>> taskCallBack,
                               PageFetcher pageFetch, Map<String, String> params) {
        super(taskCallBack, context);
        this.pageFetch = pageFetch;
        this.paramsMap = params;
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public ArrayList<CommentModel> doInBackground(String... params) {
        super.doInBackground(params);
        try {
            if (paramsMap == null) {
                return null;
            }
            return CollectResource.getInstance(context).fetchComment(pageFetch, paramsMap);

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

    public void onPostExecute(ArrayList<CommentModel> result) {
        super.onPostExecute(result);
    }

}
