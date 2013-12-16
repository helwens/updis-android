
package com.tianv.updis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import com.tianv.updis.activity.CommonResourceDetailActivity;
import com.tianv.updis.activity.DefaultActivity;
import com.tianv.updis.task.DownloadManager;
import com.tianv.updis.task.DownloadModel;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.apkinfo.ApkUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MyReceiver extends BroadcastReceiver {
    public static final int COMMON_NOTICE_ID = 3000000;

    private static final String TAG = "MyReceiver";

    private static final String UPDATE_RECEIVE = "updis_update";

    @Override
    public void onReceive(Context context, Intent rIntent) {
        Bundle bundle = rIntent.getExtras();
        Log.d(TAG, "onReceive - " + rIntent.getAction() + ", extras: " + printBundle(bundle));

        if (UPDATE_RECEIVE.equals(rIntent.getAction())) {
            //更新
            String apkUrl = bundle.getString("apkUrl");
            String versionCode = bundle.getString("versionCode");
            DownloadModel downloadModel = new DownloadModel();
            downloadModel.apkUrl = apkUrl;
            downloadModel.packageName = ApkUtils.getPackageName(context);
            downloadModel.versionCode = versionCode;
            DownloadManager.getInstance(context).addDownloadTask(downloadModel, null);
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(rIntent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            // send the Registration Id to your server...
        } else if (JPushInterface.ACTION_UNREGISTER.equals(rIntent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
            // send the UnRegistration Id to your server...
        } else {
            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(rIntent.getAction())) {

                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(extra);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (jsonObject != null) {
                    SharedStore mSharedStore = new SharedStore(context, null);
                    NotificationManager mNotificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notice = new Notification();
                    notice.flags |= Notification.FLAG_AUTO_CANCEL;
                    notice.icon = R.drawable.icon;
                    Intent intent = new Intent(context, CommonResourceDetailActivity.class);
                    String contentId = null;
                    String categoryType = null;
                    String notifyType = null;
                    boolean showNotice = true;
                    try {
                        notifyType = jsonObject.getString("notifyType");
                        if (notifyType.equals("2")) {
                            //版本更新消息
                            int buildVersion = jsonObject.getInt("buildVersion");
                            int nowVersion = ApkUtils.getAppVersionCode(context, ApkUtils.getPackageName(context));
                            if (buildVersion > nowVersion) {
                                //发现新版本
                                Intent update = new Intent(UPDATE_RECEIVE);
                                update.putExtra("apkUrl", jsonObject.getString("downloadURL"));
                                update.putExtra("versionCode", jsonObject.getString("buildVersion"));
                                PendingIntent defaultPendingIntent = PendingIntent.getActivity(context, 0,
                                        update, PendingIntent.FLAG_CANCEL_CURRENT);
                                notice.setLatestEventInfo(context, context.getText(R.string.app_name), "发现新版本,请及时更新",
                                        defaultPendingIntent);
                                mNotificationManager.notify(COMMON_NOTICE_ID, notice);
                            }

                        } else {
                            //ERP 消息
                            contentId = jsonObject.getString("messageId");
                            categoryType = jsonObject.getString("messageType");
                            switch (Integer.parseInt(categoryType)) {
                                case 1:
                                    showNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_NOTICE, true);
                                    break;
                                case 2:
                                    showNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_BIDDING, true);
                                    break;
                                case 3:
                                    showNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_TALK, true);
                                    break;
                                case 4:
                                    showNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_AMATEUR, true);
                                    break;
                                case 5:
                                    showNotice = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_PUSH_PROJECT, true);
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (showNotice) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.UPDIS_INTENT_KEY_CONTENTID, contentId);
                        intent.putExtra(Constant.UPDIS_INTENT_KEY_CATEGORYTYPE, categoryType);

                        PendingIntent defaultPendingIntent = PendingIntent.getActivity(context, 0,
                                intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        notice.setLatestEventInfo(context, context.getText(R.string.app_name), message,
                                defaultPendingIntent);
                        mNotificationManager.notify(COMMON_NOTICE_ID, notice);
                    }
                }

                BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
                builder.statusBarDrawable = R.drawable.icon;
                builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为自动消失
                builder.notificationDefaults = Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_VIBRATE; // 设置为铃声与震动都要
                JPushInterface.setPushNotificationBuilder(1, builder);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(rIntent.getAction())) {
                Log.d(TAG, "接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(rIntent.getAction())) {
                Log.d(TAG, "用户点击打开了通知");

                // 打开自定义的Activity
                Intent i = new Intent(context, DefaultActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            } else {
                Log.d(TAG, "Unhandled intent - " + rIntent.getAction());
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
