/**
 * @Title: BaseTask.java
 * @Package com.uucun.adsdk.task
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Wang Baoxi 
 * @date 2011-9-14 下午05:50:12
 * @version V1.0
 */

package com.melvin.android.base.task;

import android.content.Context;

import com.tianv.updis.AppException;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

public abstract class BaseTask<T, F, E> extends AsyncMockTask<T, F, E> {

    // private static final String CLASSTAG = BaseTask.class.getSimpleName();

    /**
     * 任务回调接口
     */
    private TaskCallBack<F, E> mTaskCallBack = null;

    /**
     * 异常信息
     */
    protected AppException appException = null;

    protected Context context = null;

    public BaseTask(TaskCallBack<F, E> taskCallBack, Context context) {
        this.mTaskCallBack = taskCallBack;
        this.context = context;
    }

    protected E doInBackground(T... params) {
        if (!NetWorkInfo.isNetworkAvailable(context)) {
            appException = new AppException(AppException.NO_NETWORK_ERROR_CODE, "");
        }

        if (mTaskCallBack != null)
            mTaskCallBack.doingTask();
        return null;
    }

    protected void onPreExecute() {
        if (mTaskCallBack != null)
            mTaskCallBack.beforeDoingTask();
    }

    protected void onProgressUpdate(F... values) {
        if (mTaskCallBack != null)
            mTaskCallBack.doingProgress(values);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: onCancelled
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @see android.os.AsyncTask#onCancelled()
     */
    protected void onCancelled() {
        if (mTaskCallBack != null)
            mTaskCallBack.onCancel();
    }

    protected void onPostExecute(E result) {
        if (mTaskCallBack != null)
            mTaskCallBack.endTask(result, appException);
        Runtime.getRuntime().gc();
    }

}
