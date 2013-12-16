package com.tianv.updis.network;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;

public class UUHttpClient /** extends DefaultHttpClient **/
{

    public static HttpParams createHttpParams(Context context) {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 15000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);
        ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean(20);
        connPerRouteBean.setDefaultMaxPerRoute(20);

        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRouteBean);
        ConnManagerParams.setTimeout(params, 15000);
        ConnManagerParams.setMaxTotalConnections(params, 1000);

        HttpHost proxy = CheckRequestState.checkHttpHost(context);
        HttpProtocolParams.setUserAgent(params, "Android Cms Client 2.0");
        if (proxy != null) {
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return params;
    }

    public static ClientConnectionManager createClientConnectionManager(
            HttpParams httpParams) {
        SchemeRegistry localSchemeRegistry = new SchemeRegistry();
        localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        localSchemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));
        return new ThreadSafeClientConnManager(httpParams, localSchemeRegistry);
    }

    public static HttpClient getInstance(Context mContext2) {
        return AndroidHttpClient.newInstance(mContext2);
    }

    public static void closeHttpClient(HttpClient httpClient) {
        if (httpClient != null) {
            ClientConnectionManager clientConnectionManager = httpClient
                    .getConnectionManager();
            if (clientConnectionManager != null)
                clientConnectionManager.shutdown();
        }
    }
}
