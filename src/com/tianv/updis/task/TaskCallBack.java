
package com.tianv.updis.task;

import com.tianv.updis.AppException;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: TaskCallBack.java
 * @Description: TODO
 * @Date 2013-4-11 下午4:11:48
 */
public interface TaskCallBack<F, E> {

    /**
     * @Title: beforeDoingTask
     * @Description: 任务开始回调
     */
    public void beforeDoingTask();

    /**
     * @Title: doingTask
     * @Description: 任务正在进行中回调
     */
    public void doingTask();

    /**
     * 取消的时候回调
     *
     * @Title: onCancel
     */
    public void onCancel();

    /**
     * @param fParam
     * @Title: doingProgress
     * @Description: 有进度更新回调
     */
    public void doingProgress(F... fParam);

    /**
     * @param eParam
     * @param AppException 程序错误的异常
     * @Title: endTask
     * @Description: 结束任务回调
     */
    public void endTask(E eParam, AppException appException);

}
