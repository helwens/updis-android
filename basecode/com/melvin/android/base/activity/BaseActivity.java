
package com.melvin.android.base.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.melvin.android.base.common.ui.MessageDialog;
import com.tianv.updis.AppException;
import com.tianv.updis.R;
import com.tianv.updis.model.UIUtilities;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.networkinfo.NetWorkInfo;
import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: BaseActivity.java
 * @Description: TODO
 * @Date 2013-4-13 上午10:03:20
 */
public abstract class BaseActivity extends Activity {
    /**
     * 页面弹出对话框
     */
    protected MessageDialog mDialog = null;

    /**
     * 页面loading
     */
    protected ProgressDialog mProgressDialog;

    /**
     * 文件存储
     */
    protected SharedStore mSharedStore = null;

    /**
     * 导航栏左侧按纽
     */
    protected ImageView mNavLeftButton = null;

    /**
     * 导航栏右侧按纽
     */
    protected ImageView mNavRightButton = null;

    protected Context mContext = null;

    /**
     * 错误提示view
     */
    protected View errorView;


    protected View commonWebView;

    /**
     * 中间View布局
     */
    protected LinearLayout bodyLayout = null;

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(0);
        String msg = getResources().getString(R.string.updis_login_progress_tips);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void initView() {
        if (mSharedStore == null) {
            mSharedStore = new SharedStore(getApplicationContext(), null);
        }
        if (mDialog == null) {
            mDialog = new MessageDialog(this);
        }
        if (mNavLeftButton == null) {
            mNavLeftButton = (ImageView) findViewById(R.id.nav_left_image);
        }
        if (mNavRightButton == null) {
            mNavRightButton = (ImageView) findViewById(R.id.nav_right_image);
        }
        mNavLeftButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                navLeftClick();
            }
        });
        mNavRightButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                navRightClick();
            }
        });
    }

    /**
     * 展示本地网页
     *
     * @param fileName
     */
    public void showWebView(String fileName) {
        bodyLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        commonWebView = LayoutInflater.from(mContext).inflate(R.layout.common_webview_layout, null);
        commonWebView.setLayoutParams(layoutParams);
        WebView webView = (WebView) commonWebView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);

        String templateStr = null;
        try {
            InputStream in = mContext.getResources().getAssets().open(fileName);
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

        Logger.i("temp",templateStr);
        ProxyBridge pBridge = new ProxyBridge();
        webView.addJavascriptInterface(pBridge, "ProxyBridge");
//        webView.loadUrl("http://google.com/ncr");
        webView.loadDataWithBaseURL(null, templateStr, "text/html", "utf-8", null);
        bodyLayout.addView(commonWebView, 0);
    }
    private class ProxyBridge {
        public void backList() {
            if (commonWebView != null) {
                bodyLayout.startLayoutAnimation();
                bodyLayout.removeView(commonWebView);
            }
        }
    }

    /**
     * 网络发生错误情况
     *
     * @param type
     */
    public void onError(int type) {
        bodyLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        errorView = LayoutInflater.from(mContext).inflate(R.layout.no_networking_status, null);
        errorView.setLayoutParams(layoutParams);
        ImageView img = (ImageView) errorView.findViewById(R.id.img_id);
        TextView to = (TextView) errorView.findViewById(R.id.text_one);
        TextView ts = (TextView) errorView.findViewById(R.id.text_second);
        TextView tt = (TextView) errorView.findViewById(R.id.text_three);
        Button tryBtn = (Button) errorView.findViewById(R.id.tryagain_btn_id);
        // bodyLayout.addView(errorView, layoutParams);
        bodyLayout.addView(errorView, 0);
        if (type == AppException.NO_NETWORK_ERROR_CODE) {
            img.setBackgroundResource(R.drawable.no_networking_icon);
            to.setText(R.string.no_networking_text);
            ts.setVisibility(View.VISIBLE);
            ts.setText(R.string.check_networking);
            tt.setText(R.string.click_try_again);
            tryBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!NetWorkInfo.isNetworkAvailable(mContext)) {
                        UIUtilities.showCustomToast(mContext, R.string.updis_network_error_tip);
                    } else {
                        hideErrorView();
                        loadResourceData();
                    }
                }
            });
        } else {
            showDataError(img, to, ts, tt, tryBtn);
        }
    }

    private void hideErrorView() {
        if (errorView != null) {
            bodyLayout.startLayoutAnimation();
            bodyLayout.removeView(errorView);
        }
    }

    /**
     * 除了无联网，其他错误状态下显示的界面
     */
    private void showDataError(ImageView img, TextView to, TextView ts, TextView tt, Button tryBtn) {
        img.setBackgroundResource(R.drawable.data_error_icon);
        to.setText(R.string.data_error);
        ts.setVisibility(View.GONE);
        tt.setText(R.string.data_get);
        tryBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                hideErrorView();
                loadResourceData();
            }
        });
    }

    public abstract void loadResourceData();

    public abstract void navLeftClick();

    public abstract void navRightClick();

}
