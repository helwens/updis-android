package com.tianv.updis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.melvin.android.base.common.ui.MessageDialog;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.task.CommonFetchDataTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Melvin
 * Date: 13-8-13
 * Time: 上午11:29
 */
public class WebContentActivity extends Activity implements View.OnTouchListener,
        GestureDetector.OnGestureListener, View.OnClickListener {
    private SharedStore mSharedStore;
    private MessageDialog mDialog;
    private WebView webView;
    private ImageView mNavLeftButton;

    private CommonFetchDataTask fetchDataTask = null;

    private String fileName = null;
    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webcontent_layout);
        if (mSharedStore == null) {
            mSharedStore = new SharedStore(getApplicationContext(), null);
        }

        fileName = getIntent().getStringExtra(Constant.UPDIS_INTENT_KEY_FILENAME);
        if (mDialog == null) {
            mDialog = new MessageDialog(this);
        }
        initView();
    }


    private void loadData() {
        if (fetchDataTask == null || fetchDataTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            Map<String, String> params = new HashMap<String, String>();
            if (fileName.equals("about.html")) {
                params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                        Constant.INTERFACE_ABOUT);
            } else if (fileName.equals("version.html")) {
                params.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                        Constant.INTERFACE_CHECKVERSION);
            }
            fetchDataTask = new CommonFetchDataTask(this, getFetchTaskCallBack(WebContentActivity.this), params);

            fetchDataTask.execute();
        }
    }

    public TaskCallBack<Void, Object> getFetchTaskCallBack(final Activity currentActivity) {
        return new TaskCallBack<Void, Object>() {
            public void beforeDoingTask() {
                showProgressDialog();
            }

            public void doingTask() {
            }

            public void onCancel() {

            }

            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(Object object, AppException appException) {
                // TODO Auto-generated method stub


                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    setPageData(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        };
    }

    public void setPageData(JSONObject jsonObject) {
        String templateStr = null;
        try {
            InputStream in = getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            templateStr = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileName.equals("about.html")) {
            try {
                templateStr = templateStr.replace("$about", jsonObject.getString("aboutContent").toString());
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if (fileName.equals("version.html")) {
            try {
                templateStr = templateStr.replace("$version", jsonObject.getString("releaseVersion").toString());
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        Logger.i("temp", templateStr);
        ProxyBridge pBridge = new ProxyBridge();
        webView.addJavascriptInterface(pBridge, "ProxyBridge");
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });
            webView.loadDataWithBaseURL(null, templateStr, "text/html", "utf-8", null);
        }
    }

    private void showProgressDialog() {
        //To change body of created methods use File | Settings | File Templates.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setMessage("正在加载数据,请稍后...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void initView() {
        //To change body of created methods use File | Settings | File Templates.

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webView.setOnTouchListener(this);

        if (mNavLeftButton == null) {
            mNavLeftButton = (ImageView) findViewById(R.id.nav_left_image);
        }
        mNavLeftButton.setVisibility(View.VISIBLE);
        mNavLeftButton.setOnClickListener(this);

        loadData();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
        this.finish();
    }

    private class ProxyBridge {
        public void backList() {

        }
    }
}
