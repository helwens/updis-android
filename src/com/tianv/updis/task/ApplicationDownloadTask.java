
package com.tianv.updis.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.widget.Toast;
import com.melvin.android.base.task.AsyncMockTask;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.apkinfo.ApkUtils;
import com.uucun.android.utils.io.IOUtils;
import com.uucun.android.utils.newstring.StringUtils;
import com.uucun.android.uunetwork.httptools.CheckRequestState;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * download任务
 *
 * @author zhangf
 * @date 2013-5-1
 */
public class ApplicationDownloadTask extends AsyncMockTask<Void, Integer, Integer> {

    /**
     * downloadListener
     */
    public DownloadListener downloadListener = null;

    /**
     * DownloadModel error
     */
    public static final int DOWNLOAD_MODEL_ERROR = 1;

    /**
     * download url error
     */
    public static final int DOWNLOAD_URL_ERROR = 2;

    /**
     * read data error
     */
    public static final int READ_DATA_ERROR = 3;

    /**
     * SD full
     */
    public static final int SPACE_FULL_ERROR = 4;

    /**
     * no sd card error *
     */
    public static final int NO_SDCARD_ERROR = 5;

    /**
     * download success
     */
    public static final int DOWNLOAD_OK = 0;

    /**
     * delete download task
     */
    public static final int DELETE_DOWNLOAD_TASK = 6;

    /**
     * reconnection size
     */
    public static int RECONNECTION_TIME = 3;

    /**
     * current progress
     */
    private int mProgress;

    private Context mContext;

    private DownloadModel downloadModel;

    private SharedStore sharedStore;

    /**
     * Version Code
     */
    private int appVersionCode = 0;

    /**
     * start position
     */
    private long mStartPosition = 0;

    /**
     * download file size
     */
    private long downloadSize = 0;

    /**
     * length of the content
     */
    private long contentLength = 0;

    private HttpClient httpClient = null;

    private HttpResponse response = null;

    /**
     * APK file
     */
    private File apkFile = null;

    private String destFileName = null;

    private String apkPackageName = null;

    private String apkVersionCode = null;

    private Message mMessage = null;

    private String APK_URL_KEY = "apk_url";

    private DownloadManager downloadManager = null;

    /**
     * constructor 有安装的回调
     *
     * @param context
     * @param downloadModel
     */
    public ApplicationDownloadTask(Context context, DownloadModel downloadModel) {
        mContext = context;
        sharedStore = new SharedStore(mContext, null);
        this.downloadModel = downloadModel;
        appVersionCode = ApkUtils.getAppVersionCode(context, context.getPackageName());
        downloadManager = DownloadManager.getInstance(context);
    }


    public int getDownloadProgress() {
        return mProgress;
    }

    /**
     * TODO set download Listener
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void onPreExecute() {
        if (downloadListener != null)
            downloadListener.onStart(null);
        downloadManager.checkTaskNotice();
    }

    protected Integer doInBackground(Void... params) {
        if (downloadModel == null)
            return DOWNLOAD_MODEL_ERROR;
        try {
            if (!IOUtils.isExternalStorageAvailable()) {
                return NO_SDCARD_ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isNullAndBlank(downloadModel.apkUrl)) {
            return DOWNLOAD_MODEL_ERROR;
        }

        resetFile();

        int result = DOWNLOAD_OK;
        for (int i = 0; i < RECONNECTION_TIME; i++) {
            if (isCancelled()) {
                break;
            }
            if (fileExsitAndIsOk(mContext, apkFile)) {
                return DOWNLOAD_OK;
            }
            try {
                resetFile();
                if (connectApk(downloadModel.apkUrl, mStartPosition)) {
                    downloadFile();
                }
            } catch (IllegalStateException e) {
                result = DOWNLOAD_URL_ERROR;
            } catch (IOException e) {
                e.printStackTrace();
                result = READ_DATA_ERROR;
                if (e instanceof SpaceFullException || getSDCardLeftSize() == 0) {
                    result = SPACE_FULL_ERROR;
                    return result;
                }
            } finally {
                if (httpClient != null)
                    httpClient.getConnectionManager().shutdown();
            }
        }

        boolean succeed = fileExsitAndIsOk(mContext, apkFile);
        if (succeed) {
            return DOWNLOAD_OK;
        } else {
            result = READ_DATA_ERROR;
        }
        return result;
    }

    /**
     * TODO reset file
     *
     * @return void
     * @author user
     * @date 2013-5-2
     */
    private void resetFile() {
        apkPackageName = downloadModel.packageName;
        apkVersionCode = downloadModel.versionCode + "";
        destFileName = StringUtils.generateFileName(downloadModel.downloadKey) + ".apk";
        apkFile = ApkUtils.getApkFile(mContext, destFileName);
        if (apkFile.exists()) {
            mStartPosition = apkFile.length();
        }
        downloadSize = 0;
        contentLength = 0;
        downloadSize = mStartPosition;
    }

    /**
     * 进度条
     */
    public void onProgressUpdate(Integer... values) {
        if (null == mMessage)
            mMessage = new Message();
        mMessage.obj = downloadModel.downloadKey;
        if (downloadListener != null && isFileDownloading(mContext, downloadModel.downloadKey)) {
            downloadListener.onProgressUpdate(mMessage, values[0], values[1]);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    /**
     * TODO cancel notice
     *
     * @return void
     * @author zhangf
     * @date 2013-5-2
     */
    public void cancelNotice() {
    }

    public void onPostExecute(Integer result) {
        downloadManager.pasuseTask(downloadModel);
        downloadManager.checkTaskNotice();
        int resu = result.intValue();
        if (resu == DOWNLOAD_OK) {
            if (null == mMessage)
                mMessage = new Message();
            mMessage.obj = downloadModel.downloadKey;

            installApk();
            if (downloadListener != null) {
                Message msg = new Message();
                msg.obj = downloadModel;
                downloadListener.onSuccess(msg);
            }
            // 检测是否是APK文件
            if (ApkUtils.fileExsitAndIsOk(mContext, apkFile)) {
                installApk();
            }
        } else {
            if (downloadListener != null) {
                downloadListener.onError(null);
            }
            if (resu == DOWNLOAD_MODEL_ERROR) {
                Toast.makeText(mContext, "服务器数据读取失败", Toast.LENGTH_SHORT).show();
            } else if (resu == DOWNLOAD_URL_ERROR) {
                Toast.makeText(mContext, "服务器数据读取失败", Toast.LENGTH_SHORT).show();
            } else if (resu == READ_DATA_ERROR) {
                Toast.makeText(mContext, "服务器数据读取失败", Toast.LENGTH_SHORT).show();
            } else if (resu == SPACE_FULL_ERROR) {
                Toast.makeText(mContext, "服务器数据读取失败", Toast.LENGTH_SHORT).show();
            } else if (resu == NO_SDCARD_ERROR) {
                Toast.makeText(mContext, "SD卡出错", Toast.LENGTH_SHORT).show();
                if (downloadModel != null) {
                    downloadManager.deleteTask(downloadModel);
                }
            } else {
                Toast.makeText(mContext, "服务器数据读取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * TODO install apk
     *
     * @return void
     * @author user
     * @date 2013-5-2
     */
    private void installApk() {
        installDefault(mContext, apkFile.getAbsolutePath());
    }


    /**
     * TODO 使用系统界面安装
     *
     * @param path 安装
     * @return boolean
     * @author zhangf
     * @date 2013-4-24
     */
    public static boolean installDefault(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }


    /**
     * HttpClient
     *
     * @return
     * @Title: getHttpClient
     */
    private HttpClient getHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient(createHttpParams(mContext));
        return httpClient;
    }

    /**
     * TODO create HttpParams
     *
     * @return HttpParams
     * @author zhangf
     * @date 2013-5-2
     */
    public HttpParams createHttpParams(Context context) {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, true);
        HttpHost proxy = CheckRequestState.checkHttpHost(context);
        HttpProtocolParams.setUserAgent(params, "Android Cms Client downloader");
        if (proxy != null) {
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return params;
    }

    /**
     * @param apkUrl
     * @param startPosition
     * @return
     * @throws org.apache.http.client.ClientProtocolException
     *
     * @throws java.io.IOException
     * @Title: connectApk
     */
    private boolean connectApk(String apkUrl, long startPosition) throws ClientProtocolException,
            IOException {
        if (isCancelled() || null == apkUrl) {
            return false;
        }
        HttpGet getRequest = new HttpGet(apkUrl);
        if (httpClient != null)
            httpClient.getConnectionManager().shutdown();
        httpClient = getHttpClient();
        getRequest.addHeader("Range", "bytes=" + startPosition + "-");
        getRequest.addHeader("User-Agent", "Android_CMS Client " + appVersionCode);

        String firstCookie = sharedStore.getString("login_cookies", "");
        getRequest.addHeader("Cookie", firstCookie);

        response = httpClient.execute(getRequest);
        int code = response.getStatusLine().getStatusCode();
        if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
            return true;
        }
        return false;
    }

    /**
     * download file
     *
     * @throws IllegalStateException
     * @throws java.io.IOException
     * @Title: downloadFile
     */
    private void downloadFile() throws IllegalStateException, IOException {
        if (response.getEntity() == null) {
            return;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long leftSpace = 0;
        try {
            FileOutputStream fileOutputStream = null;
            if (IOUtils.isExternalStorageAvailable()) {
                fileOutputStream = new FileOutputStream(apkFile, true);
                leftSpace = getSDCardLeftSize();
            } else {
                /** 如果没有SD卡，就在ROM中建一个文件 */
                fileOutputStream = mContext.openFileOutput(destFileName, Context.MODE_APPEND
                        | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                leftSpace = getSystemSize();
            }
            contentLength = response.getEntity().getContentLength();
            if (contentLength > leftSpace) {
                if (httpClient != null)
                    httpClient.getConnectionManager().shutdown();
                throw new SpaceFullException("Space Full!!");
            }
            inputStream = response.getEntity().getContent();
            outputStream = new BufferedOutputStream(fileOutputStream);
            int read;
            /** 10K,10K的往里面写数据 */
            byte[] buff = new byte[10 * 1024];
            while ((read = inputStream.read(buff)) != -1) {
                if (isCancelled()) {
                    break;
                }
                outputStream.write(buff, 0, read);
                downloadSize += read;
                if (downloadSize > contentLength + mStartPosition) {
                    break;
                }
                int progress = (int) ((downloadSize * 100.0 / (contentLength + mStartPosition)));

                if (progress < 0) {
                    progress = 0;
                } else if (progress > 100) {
                    progress = 100;
                }
                mProgress = progress;
                sharedStore.putInt(downloadModel.downloadKey, progress);
                sharedStore.putLong(downloadModel.downloadKey + "totalsize", downloadSize);
                sharedStore.putFloat(downloadModel.downloadKey + "readsize",
                        (contentLength + mStartPosition));
                // sharedStore.commit();
                publishProgress(progress, (int) (contentLength + mStartPosition));
                outputStream.flush();
            }
        } finally {
            if (outputStream != null) {
                outputStream.flush();
            }
            IOUtils.closeStream(outputStream);
            if (httpClient != null)
                httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * TODO get SD card left size
     *
     * @return long
     * @author zhangf
     * @date 2013-5-2
     */
    private long getSDCardLeftSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            return blockSize * availCount;
        }
        return 0;
    }

    /**
     * TODO get system storage left size
     *
     * @return long
     * @author zhangf
     * @date 2013-5-2
     */
    private long getSystemSize() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return blockSize * availCount;
    }

    public class SpaceFullException extends IOException {
        private static final long serialVersionUID = 1L;

        public SpaceFullException(String msg) {
            super(msg);
        }
    }

    /**
     * TODO judge a task is downloading
     *
     * @param context
     * @param key     package key
     * @return boolean
     * @author zhangf
     * @date 2013-5-2
     */
    public static boolean isFileDownloading(Context context, String key) {
        DownloadManager downloadManager = DownloadManager.getInstance(context);
        ApplicationDownloadTask downloadTask = downloadManager.get(key);
        if (downloadTask != null)
            return true;
        return false;
    }

    public static interface DownloadListener {
        /**
         * start download
         */
        public void onStart(Message msg);

        /**
         * downloading
         */
        public void onProgressUpdate(Message msg, int progress, int totalSize);

        /**
         * download success
         */
        public void onSuccess(Message msg);

        /**
         * download error
         */
        public void onError(Message msg);

        /**
         * delete task
         */
        public void onDelete();
    }

    /**
     * TODO judge a file is ok
     *
     * @return boolean
     * @author zhangf
     * @date 2013-5-2
     */
    public static boolean fileExsitAndIsOk(Context context, File file) {
        if (!file.exists())
            return false;
        if (IOUtils.isExternalStorageAvailable()) {
            String s = ApkUtils.getUninstalledAppPackageName(context, file.getAbsolutePath());
            return file.exists() && !StringUtils.isNullAndBlank(s);
        } else {
            String s = ApkUtils.getUninstalledAppPackageName(context, file.getAbsolutePath());
            return file.exists() && !StringUtils.isNullAndBlank(s);
        }
    }
}
