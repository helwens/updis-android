package com.tianv.updis.fragment;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tianv.updis.R;
import com.uucun.android.logger.Logger;

/**
 * Created by Melvin on 13-5-23.
 */
public class CommonWebView implements View.OnTouchListener,
        GestureDetector.OnGestureListener {

    GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;

    private WebView webView = null;
    private Context context;
    private View view = null;
    private CommonWebViewListener webViewListener = null;
    ProgressDialog dialog = null;

    public CommonWebView(Context context) {
        this.context = context;
        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflate.inflate(R.layout.common_webview_layout, null);
        mGestureDetector = new GestureDetector(this);
        layout.setOnTouchListener(this);
        layout.setLongClickable(true);
        
        view = layout;
        webView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webView.setOnTouchListener(this);
    }

    /**
     * 返回View
     *
     * @return
     */
    public View createView() {
        return view;

    }

    public void setUrl(String fileName) {
        String templateStr = null;
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
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
        templateStr = templateStr.replace("$about", "关于.");

        Logger.i("temp", templateStr);
        ProxyBridge pBridge = new ProxyBridge();
        webView.addJavascriptInterface(pBridge, "ProxyBridge");
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    dialog.dismiss();
                }
            });
            dialog = ProgressDialog.show(context,null,"正在加载数据,请稍后...");
            webView.loadDataWithBaseURL(null, templateStr, "text/html", "utf-8", null);
        }
    }

    public void setWebViewListener(CommonWebViewListener listener) {
        webViewListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling left
            Toast.makeText(context, "向左手势", Toast.LENGTH_SHORT).show();
        } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling right
            Toast.makeText(context, "向右手势", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public interface CommonWebViewListener {
        public void back();
    }

    private class ProxyBridge {
        public void backList() {
            Logger.i("melvin", "click back button");
            if (webViewListener != null)
                webViewListener.back();
        }
    }
}
