
package com.tianv.updis.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.jpush.android.api.InstrumentedActivity;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.cache.FastBitmapDrawable;
import com.tianv.updis.cache.ImageManager;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: DefaultActivity.java
 * @Description: TODO
 * @Date 2013-3-24 下午4:54:03
 */
public class DefaultActivity extends InstrumentedActivity {

    /**
     * 检测网络失败
     */
    private static final int MESSAGE_POST_ERROR = 0x2;

    /**
     * 进入登录界面
     */
    private static final int MESSAGE_GO_LOGIN = 0x3;

    /**
     * 进入主界面
     */
    private static final int MESSAGE_GO_MAIN = 0x4;

    /**
     * 检测网路失败弹出的dialog
     */
    private static final int DIALOG_LOADING_ERROR = 3;

    private Dialog changeNetworkDialog = null;

    private SharedStore mSharedStore = null;

    private final Handler sHandler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case MESSAGE_GO_LOGIN:
                    intent = new Intent(DefaultActivity.this, LoginActivity.class);
                    startActivity(intent);
                    DefaultActivity.this.finish();
                    break;
                case MESSAGE_GO_MAIN:
                    intent = new Intent(DefaultActivity.this, MainActivityGroup.class);
                    startActivity(intent);
                    DefaultActivity.this.finish();
                    break;
                case MESSAGE_POST_ERROR:
                    if (!isFinishing()) {
                        if (changeNetworkDialog != null && changeNetworkDialog.isShowing()) {
                            changeNetworkDialog.dismiss();
                        }
                        showDialog(DIALOG_LOADING_ERROR);
                    }
                    break;
            }
        }
    };

    protected void onDestroy() {
        // GC
        System.gc();
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);
        setBootSurface();
        setContentView(R.layout.default_layout);
        ImageView loading = (ImageView) findViewById(R.id.splash_loading);
        loading.setBackgroundResource(R.anim.splash_animation);
        final AnimationDrawable anim = (AnimationDrawable) loading.getBackground();
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim.start();
            }
        }, 200);
        if (mSharedStore == null) {
            mSharedStore = new SharedStore(getApplicationContext(), null);
        }

        //清理自身通知栏
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detectNetwork();
    }

    /**
     * Set the boot surface, provided this functionality can be customized
     * start-up surface conveniently for user.
     */
    private void setBootSurface() {
        FastBitmapDrawable bootDrawable = ImageManager.getInstance(getApplicationContext())
                .getCachedBootImage();
        if (bootDrawable != null) {
            getWindow().setBackgroundDrawable(bootDrawable);
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.bgcover);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 检测当前的网络是否可用
     */
    private void detectNetwork() {
        Message message = null;
        new Thread() {
            public void run() {
                // 检测是否登录
                boolean isLogin = false;
                Message message = null;
                isLogin = mSharedStore.getBoolean(Constant.UPDIS_STORE_KEY_LOGINFLAG, false);
                if (!isLogin) {
                    // 未登录
                    if (!NetWorkInfo.isNetworkAvailable(getApplicationContext())) {
                        message = sHandler.obtainMessage(MESSAGE_POST_ERROR);
                    } else {
                        message = sHandler.obtainMessage(MESSAGE_GO_LOGIN);
                    }
                } else {
                    message = sHandler.obtainMessage(MESSAGE_GO_MAIN);
                }
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                message.sendToTarget();

            }
        }.start();

    }

    // 当网络不通时候弹出的对话框
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING_ERROR:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.updis_network_error_title);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage(R.string.updis_network_error_tip);
                builder.setPositiveButton(R.string.set_network,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NetWorkInfo.startWirelessSetting(getApplicationContext());
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                    }
                });
                builder.setCancelable(true);
                return builder.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
