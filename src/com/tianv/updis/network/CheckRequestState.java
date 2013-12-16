package com.tianv.updis.network;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Defines a request to abstract the target request URL and makes it adapt for
 * the mobile network, such as cmwap, ctwap, uniwap, 3gwap. Then using this URL
 * to fetch the resource from a remote server. 检测网络的状态，比如3G,WIFI等，重置一些Url信息
 *
 * @author wuss
 * @since 2011-09-14
 */
public class CheckRequestState {

    public boolean isWapState = false;

    public static final String GWAP = "3gwap";

    public static final String CMWAP = "cmwap";

    public static final String CTWAP = "ctwap";

    public static final String UNIWAP = "uniwap";

    public static final String PORT_TOKEN = ":";

    public static final String SCHEME = "http://";

    public static final String X_ONLINE_HOST = "X-Online-Host";

    public static final String X_OFFLINE_HOST = "http://10.0.0.172";

    public static final String DEFAULT_PROXY_HOST = "10.0.0.172";

    /**
     * 取得URL
     *
     * @param context
     * @param getRequest
     * @return request URL
     */
    public String getRequestUrl(Context context, HttpPost getRequest) {
        final StringBuilder urlBuilder = new StringBuilder();
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
        String extraInfo = null;
        if (networkinfo != null) {
            extraInfo = networkinfo.getExtraInfo();
        }
        if (extraInfo != null
                && (extraInfo.equals(CMWAP) || extraInfo.equals(CTWAP)
                || extraInfo.equals(UNIWAP) || extraInfo.equals(GWAP))) {
            urlBuilder.append(X_OFFLINE_HOST);
            isWapState = true;
        } else {
            urlBuilder.append(SCHEME);
            urlBuilder.append(getRequest.getURI().getHost());
            int port = getRequest.getURI().getPort();
            if (port != -1) {
                urlBuilder.append(PORT_TOKEN);
                urlBuilder.append(port);
            }
        }
        urlBuilder.append(getRequest.getURI().getPath());
        final String query = getRequest.getURI().getQuery();
        if (!TextUtils.isEmpty(query)) {
            urlBuilder.append("?");
            urlBuilder.append(query);
        }
        return urlBuilder.toString();
    }

    /**
     * @param context
     * @param getRequest
     * @return request URL
     */
    public static boolean isWap(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
        String extraInfo = null;
        if (networkinfo != null) {
            extraInfo = networkinfo.getExtraInfo();
        }
        if (extraInfo != null
                && (extraInfo.equals(CMWAP) || extraInfo.equals(UNIWAP) || extraInfo
                .equals(CTWAP))) {

            return true;
        }
        return false;

    }

    /**
     * 检测wap状态的httphost
     *
     * @param context
     * @return
     * @Title: checkHttpHost
     */
    @SuppressWarnings("deprecation")
    public static HttpHost checkHttpHost(Context context) {
        if (isWap(context)) {
            String h = android.net.Proxy.getHost(context);
            int port = android.net.Proxy.getPort(context);
            if (h == null || TextUtils.isEmpty(h.trim())) {
                h = CheckRequestState.DEFAULT_PROXY_HOST;
            }
            if (port == -1) {
                port = 80;
            }
            return new HttpHost(h, port);
        }
        return null;

    }

    /**
     * 检测wap状态 UrlConnection的代理
     *
     * @param context
     * @return
     * @Title: checkUrlConnectionProxy
     */
    @SuppressWarnings("deprecation")
    public static java.net.Proxy checkUrlConnectionProxy(Context context) {
        if (isWap(context)) {
            String h = android.net.Proxy.getHost(context);
            int port = android.net.Proxy.getPort(context);
            if (h == null || TextUtils.isEmpty(h.trim())) {
                h = CheckRequestState.DEFAULT_PROXY_HOST;
            }
            if (port == -1) {
                port = 80;
            }
            return new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(h,
                    port));
        }
        return null;
    }
}
