
package com.tianv.updis;

import java.util.HashSet;
import java.util.Set;

import android.app.Application;
import android.os.StrictMode;
import cn.jpush.android.api.JPushInterface;

import com.uucun.android.data.sqlhelper.UUSQLHelper;
import com.uucun.android.logger.Logger;

public class UUApplication extends Application {

    private boolean DEVELOPER_MODE = false;

    private boolean LOG_MODE = true;

    @Override
    public void onCreate() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                    .penaltyLog() // 打印logcat
                    .penaltyDeath().build());
        }
        super.onCreate();
        UUSQLHelper.initialize(this);
        Logger.setLogState(LOG_MODE);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        Set<String> tags = new HashSet<String>();
        tags.add("default");
        JPushInterface.setAliasAndTags(getApplicationContext(), "default", tags);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        UUSQLHelper.dispose();
    }

}
