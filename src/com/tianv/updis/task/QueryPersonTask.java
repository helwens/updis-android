
package com.tianv.updis.task;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: QueryPersonTask.java
 * @Description: TODO
 * @Date 2013-4-11 下午4:11:34
 */
public class QueryPersonTask extends BaseTask<Void, Void, ArrayList<PersonModel>> {


    /**
     * 分页回调
     */
    private CollectResource.PageFetcher pageFetch;
    /**
     * 参数表
     */
    private Map<String, String> paramsMap = null;

    public QueryPersonTask(Context context, TaskCallBack<Void, ArrayList<PersonModel>> taskCallBack, CollectResource.PageFetcher pageFetch
            , Map<String, String> params) {
        super(taskCallBack, context);
        this.pageFetch = pageFetch;
        this.paramsMap = params;
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public ArrayList<PersonModel> doInBackground(Void... params) {
        super.doInBackground(params);
        if (paramsMap == null) {
            return null;
        }
        try {
            return CollectResource.getInstance(context).fetchPersonData(pageFetch, paramsMap);
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

    public void onPostExecute(ArrayList<PersonModel> result) {
        super.onPostExecute(result);
    }
}
