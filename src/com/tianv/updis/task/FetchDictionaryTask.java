
package com.tianv.updis.task;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.DictionaryModel;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: FetchDictionaryTask.java
 * @Description: TODO
 * @Date 2013-4-11 下午2:05:48
 */
public class FetchDictionaryTask extends BaseTask<Void, Void, DictionaryModel> {

    public FetchDictionaryTask(Context context, TaskCallBack<Void, DictionaryModel> taskCallBack) {
        super(taskCallBack, context);
    }

    public void onPreExecute() {
        super.onPreExecute();
    }

    public DictionaryModel doInBackground(Void... params) {
        super.doInBackground(params);
        try {
            return CollectResource.getInstance(context).fetchDictData();
        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("FetchDictionaryTask", "" + s);
        }
        return null;
    }

    public void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void onCancelled() {
        super.onCancelled();
    }

    public void onPostExecute(DictionaryModel result) {
        super.onPostExecute(result);
    }
}
