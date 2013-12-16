
package com.tianv.updis.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.apkinfo.ApkUtils;
import com.uucun.android.utils.newstring.MD5Util;
import com.uucun.android.utils.newstring.StringUtils;
import com.uucun.android.utils.r.RFileUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DownloadManager {
    /**
     * Notification Id
     */
    public static final int COMMON_NOTICE_ID = 1000001;

    /**
     * all Download Task
     */
    private HashMap<String, ApplicationDownloadTask> DOWNLOAD_MAPS = new HashMap<String, ApplicationDownloadTask>();

    /**
     * downloading task
     */
    private ConcurrentLinkedQueue<String> DOWNLOADING_TASK = new ConcurrentLinkedQueue<String>();

    /**
     * un download task
     */
    private ConcurrentLinkedQueue<String> UN_DOWNLOAD_TASK = new ConcurrentLinkedQueue<String>();

    /**
     * download info cache in db,when start the task,get DownloadTable from
     * database
     * *
     */
    public static HashMap<String, DownloadModel> DOWNLOAD_SQLITE_MAPS = new HashMap<String, DownloadModel>();

    /**
     * silent install cache
     */
    public static HashMap<String, Boolean> SILENT_INSTALL_MAPS = new HashMap<String, Boolean>();

    /**
     * Context
     */
    public Context context = null;

    private static DownloadManager downloadManager = null;

    /**
     * download Listener cache *
     */
    private static HashMap<String, ApplicationDownloadTask.DownloadListener> DOWNLOADING_LISTENNER = new HashMap<String, ApplicationDownloadTask.DownloadListener>();

    private RFileUtil mRFileUtil;

    private SharedStore sharedStore = null;

    /**
     * Constructor
     *
     * @param context
     */
    private DownloadManager(Context context) {
        this.context = context;
        mRFileUtil = RFileUtil.getInstance(context);
        sharedStore = new SharedStore(context, null);
    }

    /**
     * TODO
     *
     * @param context
     * @return DownloadManager
     * @author zhangf
     * @date 2013-5-1
     */
    public synchronized static DownloadManager getInstance(Context context) {
        if (downloadManager == null)
            downloadManager = new DownloadManager(context);
        return downloadManager;
    }


    /**
     * TODO 产生Apk本地存储路径
     *
     * @param context
     * @param packageUrl 下载地址
     * @return String
     * @author Melvin
     * @date 2013-5-9
     */
    public static String createApkFilePath(Context context, String packageUrl) {
        return ApkUtils.getApkFile(context,
                StringUtils.generateFileName(MD5Util.getMD5String(packageUrl)) + ".apk")
                .getAbsolutePath();
    }

    /**
     * TODO 获取当前进度
     *
     * @return int
     * @author Melvin
     * @date 2013-5-13
     */
    public synchronized int getProgress(Context context, DownloadModel downloadModel) {
        int progress = 0;
        if (StringUtils.isNullAndBlank(downloadModel.downloadKey)) {
            downloadModel.downloadKey = MD5Util.getMD5String(downloadModel.apkUrl);
        }
        SharedStore sharedStore = new SharedStore(context, null);
        progress = sharedStore.getInt(downloadModel.downloadKey, 0);
        return progress;
    }


    public synchronized ApplicationDownloadTask addDownloadTask(DownloadModel downloadModel,
                                                                ApplicationDownloadTask.DownloadListener downloadListener) {
        if (downloadModel == null || StringUtils.isNullAndBlank(downloadModel.apkUrl)) {

            try {
                throw new Exception("YOU MUST SET PACKAGEURL,THE FILE DOWNLOAD URL");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (StringUtils.isNullAndBlank(downloadModel.downloadKey)) {
            downloadModel.downloadKey = MD5Util.getMD5String(downloadModel.apkUrl);
        }

        ApplicationDownloadTask downloadTask = null;
        downloadTask = get(downloadModel.downloadKey);
        downloadModel.filePath = createApkFilePath(context, downloadModel.apkUrl);
        /*** 如果内存中有这个downloadTask，就直接提示任务已经加载到内存中了 */
        if (downloadTask != null) {

        } else {
            downloadTask = new ApplicationDownloadTask(context, downloadModel);
            UN_DOWNLOAD_TASK.add(downloadModel.downloadKey);
            DOWNLOAD_MAPS.put(downloadModel.downloadKey, downloadTask);
        }
        if (downloadListener != null) {
            setDownloadListener(downloadModel.downloadKey, downloadListener);
        }
        detectTask();
        return downloadTask;
    }

    /**
     * detect task
     */
    private void detectTask() {
        checkTaskNotice();
        /** 在配置文件中配置下载的数量 **/
        int countDownloadConcurrent = 6;
        if (DOWNLOADING_TASK.size() >= countDownloadConcurrent) {
            return;
        }
        String packageKey = UN_DOWNLOAD_TASK.poll();
        ApplicationDownloadTask applicationDownloadTask = get(packageKey);
        if (applicationDownloadTask != null) {
            DOWNLOADING_TASK.add(packageKey);
            applicationDownloadTask.execute();
        }
    }

    /**
     * get unStart task size
     *
     * @return int
     */
    public int getUnStartTask() {
        return UN_DOWNLOAD_TASK.size();
    }

    /**
     * get downloading task size
     *
     * @return size
     */
    public int getDownloadingTask() {
        return DOWNLOADING_TASK.size();

    }

    /**
     * get all task size
     *
     * @return all task size
     */
    public int getAllTaskSize() {
        return DOWNLOAD_MAPS.size();
    }

    /**
     * TODO get download task
     *
     * @return ApplicationDownloadTask
     * @author zhangf
     * @date 2013-5-2
     */
    public ApplicationDownloadTask get(String packageKey) {
        if (TextUtils.isEmpty(packageKey)) {
            return null;
        }
        return DOWNLOAD_MAPS.get(packageKey);
    }

    /**
     * TODO 暂停下载
     *
     * @return void
     * @author Melvin
     * @date 2013-5-16
     */
    public synchronized void pasuseTask(DownloadModel downloadModel) {
        if (StringUtils.isNullAndBlank(downloadModel.downloadKey)) {
            downloadModel.downloadKey = MD5Util.getMD5String(downloadModel.apkUrl);
        }
        remove(downloadModel.downloadKey);
    }

    /**
     * TODO 删除一个下载任务,并删除相关文件
     *
     * @return void
     * @author Melvin
     * @date 2013-5-16
     */
    public synchronized void deleteTask(DownloadModel downloadModel) {
        if (StringUtils.isNullAndBlank(downloadModel.downloadKey)) {
            downloadModel.downloadKey = MD5Util.getMD5String(downloadModel.apkUrl);
        }
        remove(downloadModel.downloadKey);

        DownloadManager.DOWNLOAD_SQLITE_MAPS.remove(downloadModel.downloadKey);
        sharedStore.remove(downloadModel.downloadKey);
    }

    /**
     * Remove task when download is done.
     *
     * @param downloadKey
     */
    private synchronized void remove(String downloadKey) {
        if (downloadKey != null) {
            ApplicationDownloadTask downloadTask = null;
            downloadTask = get(downloadKey);
            if (downloadTask != null) {
                downloadTask.cancel(true);
                downloadTask.cancelNotice();
                if (downloadTask.downloadListener != null)
                    downloadTask.downloadListener.onDelete();
            }
            DOWNLOAD_MAPS.remove(downloadKey);
            UN_DOWNLOAD_TASK.remove(downloadKey);
            DOWNLOADING_TASK.remove(downloadKey);
        }
        detectTask();
    }

    /**
     * TODO delete
     *
     * @param packageKey
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public synchronized void removeSQLMap(String packageKey) {
        if (packageKey != null) {
            DOWNLOAD_SQLITE_MAPS.remove(packageKey);
        }
    }

    /**
     * TODO set downloadListener via packagekey
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public synchronized void setDownloadListener(String packageKey, ApplicationDownloadTask.DownloadListener listener) {
        if (packageKey != null) {
            ApplicationDownloadTask downloadTask = null;
            downloadTask = get(packageKey);
            if (downloadTask != null) {
                downloadTask.setDownloadListener(listener);
            }
        }
    }

    /**
     * TODO 获取监听器
     *
     * @return DownloadListener
     * @author Melvin
     * @date 2013-5-9
     */
    public synchronized ApplicationDownloadTask.DownloadListener getDownloadListener(String packageKey) {
        if (packageKey != null) {
            ApplicationDownloadTask downloadTask = null;
            downloadTask = get(packageKey);
            if (downloadTask != null && downloadTask.downloadListener != null) {
                return downloadTask.downloadListener;
            }
        }
        return null;
    }

    /**
     * TODO change downloadListener
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public synchronized void changeListener() {
        Set<String> set = DOWNLOADING_LISTENNER.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            ApplicationDownloadTask downloadTask = (ApplicationDownloadTask) DOWNLOAD_MAPS.get(key);
            if (downloadTask != null) {
                if (downloadTask.downloadListener != null) {
                    ApplicationDownloadTask.DownloadListener temp = DOWNLOADING_LISTENNER.get(key);
                    if (temp != null) {
                        downloadTask.setDownloadListener(temp);
                    }
                }
            }
        }
    }

    /**
     * TODO delete task call DownloadListener onDelete()
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public synchronized void deleteTask() {
        Set<String> set = DOWNLOADING_LISTENNER.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            ApplicationDownloadTask.DownloadListener temp = DOWNLOADING_LISTENNER.get(key);
            if (temp != null) {
                temp.onDelete();
            }
        }
    }

    /**
     * remove all task
     */
    public void removeAllTask() {
        Set<String> set = DOWNLOAD_MAPS.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            ApplicationDownloadTask downloadTask = (ApplicationDownloadTask) DOWNLOAD_MAPS.get(key);
            if (downloadTask != null) {
                downloadTask.cancel(true);
                downloadTask.cancelNotice();
            }
            iterator.remove();
            UN_DOWNLOAD_TASK.remove(key);
            DOWNLOADING_TASK.remove(key);
        }
        detectTask();
    }


    /**
     * check if the task is downloading
     *
     * @param packageKey downloading key
     * @return boolean
     */
    public boolean onDownloading(String packageKey) {
        if (TextUtils.isEmpty(packageKey)) {
            return false;
        }
        ApplicationDownloadTask task = get(packageKey);
        return task != null;
    }

    /**
     * TODO check task notice to show current downloading numbers and wait
     * download numbers
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    @SuppressWarnings("deprecation")
    public void checkTaskNotice() {
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notice = new Notification();
//        notice.icon = android.R.drawable.stat_sys_download;
//        notice.tickerText = "正在下载更新";
//        notice.flags |= Notification.FLAG_AUTO_CANCEL;
//        mNotificationManager.notify(COMMON_NOTICE_ID, notice);
    }

}
