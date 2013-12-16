package com.tianv.updis.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.melvin.android.base.common.ui.MessageDialog;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.apkinfo.ApkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Melvin
 * Date: 13-7-2
 * Time: 下午2:25
 * To change this template use File | Settings | File Templates.
 */
public class UpdateApp {

    private static int DEFAULT_UPDATE_HOUR = 1 * 24;
    /**
     * 一小时
     */
    public static long HOUR_TIME = 3600000;
    private static UpdateApp updateApp;

    private static UpdateTask updateTask;
    private static Context mContext;
    private static boolean isUpdating;
    private static SharedStore mSharedStore;
    private static MessageDialog mDialog;

    private static String apkUrl;
    private static String versionCode;

    private static boolean isFocus;
    private static ProgressDialog mProgressDialog;

    public UpdateApp(Context context) {
        mContext = context;
    }

    public synchronized static UpdateApp getInstance(Context context) {
        if (updateApp == null) {
            updateApp = new UpdateApp(context);
        }
        return updateApp;
    }


    private static void showProgressDialog() {
        //To change body of created methods use File | Settings | File Templates.
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setMessage("正在检查更新");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private static TaskCallBack<Void, String> getPostMessageTask() {
        TaskCallBack<Void, String> taskCallBask = new TaskCallBack<Void, String>() {
            @Override
            public void beforeDoingTask() {
                if (isFocus) {
                    showProgressDialog();
                }
            }

            @Override
            public void doingTask() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(String eParam, AppException appException) {

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                if (appException != null && eParam == null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时
                        ReLoginTask reLoginTask = new ReLoginTask(mContext);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                }
                if (eParam != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(eParam);
                        if (mSharedStore == null) {
                            mSharedStore = new SharedStore(mContext, null);
                        }
                        mSharedStore.putLong("check_update", System.currentTimeMillis());
                        int nowVersion = ApkUtils.getAppVersionCode(mContext, ApkUtils.getPackageName(mContext));
                        int buildVersion = jsonObject.getInt("buildVersion");
                        String releaseNote = "";
                        if (jsonObject.has("releaseNote")) {
                            releaseNote = jsonObject.getString("releaseNote");
                        }
                        if (buildVersion > nowVersion) {

                            new AlertDialog.Builder(mContext)
                                    .setTitle("更新")
                                    .setMessage(String.format("Updis %s 版本更新发布\n %s ", jsonObject.getString("releaseVersion"), releaseNote))
                                    .setPositiveButton("立即更新", onClickListener)
                                    .setNegativeButton("暂不升级", onClickListener)
                                    .show();

                            apkUrl = jsonObject.getString("downloadURL");
                            versionCode = jsonObject.getString("buildVersion");
                        } else {
                            if (isFocus)
                                new AlertDialog.Builder(mContext).setTitle("检查更新")
                                        .setMessage("已是最新版")
                                        .setNegativeButton("确定", onClickListener)
                                        .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isFocus)
                        new AlertDialog.Builder(mContext).setTitle("检查更新")
                                .setMessage("已是最新版")
                                .setNegativeButton("确定", onClickListener)
                                .show();
                }
            }
        };
        return taskCallBask;
    }

    static DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //更新
                DownloadModel downloadModel = new DownloadModel();
                downloadModel.apkUrl = apkUrl;
                downloadModel.packageName = ApkUtils.getPackageName(mContext);
                downloadModel.versionCode = versionCode;
                DownloadManager.getInstance(mContext).addDownloadTask(downloadModel, null);
            } else {
                //暂不更新
//
//                if (mSharedStore == null) {
//                    mSharedStore = new SharedStore(mContext, null);
//                }
//                mSharedStore.putBoolean("check_update_not_now", true);
            }
        }
    };

    public void update(boolean foucus) {
        boolean canUpdate = checkUpdate();
        isFocus = foucus;
        boolean notNow = mSharedStore.getBoolean("check_update_not_now", false);
        if (!canUpdate) {
            if (foucus)
                canUpdate = true;
        }
        if (canUpdate) {
            //发送消息

            if (updateTask == null
                    || updateTask.getStatus() == AsyncMockTask.Status.FINISHED) {
                /** 数据接口 */

                Map hashMap = new HashMap<String, String>();
                hashMap.put("clientType", "1");
                updateTask = new UpdateTask(mContext, getPostMessageTask()
                        , hashMap);
                updateTask.execute();
            }
        }
    }

    static ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
        }
    };

    private static boolean checkUpdate() {

        if (mSharedStore == null) {
            mSharedStore = new SharedStore(mContext, null);
        }
        long ctime = mSharedStore.getLong("check_update", 0);
        if (((System.currentTimeMillis() - ctime) > HOUR_TIME
                * DEFAULT_UPDATE_HOUR)) {
            return true;
        }
        return false;
    }
}
