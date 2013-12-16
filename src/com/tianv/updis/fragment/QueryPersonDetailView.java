
package com.tianv.updis.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.QueryPersonTask;
import com.tianv.updis.task.ReLoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.newstring.StringUtils;

public class QueryPersonDetailView {

    private Context mContext = null;

    private PersonModel personModel = null;

    private View view = null;

    private WebView webView;

    private DetailViewListener detailViewListener = null;

    private ProgressDialog dialog = null;

    private boolean isContacts = false;

    private QueryPersonTask queryPersonTask = null;

    private Map<String, String> hashMap = null;

    public QueryPersonDetailView(Context context) {
        // TODO Auto-generated constructor stub
        this.mContext = context;

        LayoutInflater inflate = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflate.inflate(R.layout.query_person_detail_layout, null);

        view = layout;
        webView = (WebView) view.findViewById(R.id.personContent);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
    }

    private class ProxyBridge {
        public void backList() {
            if (detailViewListener != null)
                detailViewListener.back();
        }
    }

    private void onLoadingResource() {
        if (hashMap == null)
            hashMap = new HashMap<String, String>();

        hashMap.put(Constant.UrlAlias.PARAMS_KEY_FLAG, "2");
        if (!UIUtilities.isNull(personModel.name))
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_NAME, personModel.name);
        if (!UIUtilities.isNull(personModel.dept))
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_DEPT, personModel.dept);
        if (!UIUtilities.isNull(personModel.specialty))
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_SUBJECT, personModel.specialty);

        if (!UIUtilities.isNull(personModel.userid))
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_USEID, personModel.userid);
        if (queryPersonTask == null
                || queryPersonTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            queryPersonTask = new QueryPersonTask(mContext,
                    getQueryTaskCallBack(mContext), null, hashMap);
            // query detail
            queryPersonTask.execute();
        }
    }

    ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
            onLoadingResource();
        }
    };

    /**
     * 查询结果回调
     *
     * @return
     */
    public TaskCallBack<Void, ArrayList<PersonModel>> getQueryTaskCallBack(final Context context) {
        return new TaskCallBack<Void, ArrayList<PersonModel>>() {
            public void beforeDoingTask() {
                dialog = ProgressDialog.show(mContext, null, "正在加载数据,请稍后...");
            }

            public void doingTask() {
            }

            public void onCancel() {

            }

            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(ArrayList<PersonModel> eParam, AppException appException) {
                // TODO Auto-generated method stub
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (appException != null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时
                        ReLoginTask reLoginTask = new ReLoginTask(mContext);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                }
                if (eParam != null) {
                    personModel = eParam.get(0);
                    initView();
                }

            }
        };
    }


    /**
     * 返回View
     *
     * @return
     */
    public View createView() {
        return view;

    }

    public PersonModel getPersonModel() {
        return personModel;
    }

    public void setPersonModel(PersonModel personModel, boolean contacts) {
        this.personModel = personModel;
        this.isContacts = contacts;
//        if (isContacts) {
        initView();
//        } else {
//            onLoadingResource();
//        }
    }

    public void setDetailViewListener(DetailViewListener listener) {
        this.detailViewListener = listener;
    }


    public interface DetailViewListener {
        public void back();
    }


    private void initView() {
        if (personModel != null) {
            if (webView != null) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        dialog.dismiss();
                    }

                });
                dialog = ProgressDialog.show(mContext, null, "正在加载数据,请稍后...");
                writeContent();
            }
        }
    }

    private String checkNull(String old) {
        return UIUtilities.isNull(old) ? "" : old;
    }


    private void writeContent() {
        String templateStr = null;
        try {
            InputStream in = mContext.getResources().getAssets().open(isContacts ? "Contacts.htm" : "member.htm");
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
        templateStr = templateStr.replace("$name", checkNull(personModel.name));
        templateStr = templateStr.replace("$dept", checkNull(personModel.dept));
        templateStr = templateStr.replace("$birthday", checkNull(personModel.birthday));
        templateStr = templateStr.replace("$gender", checkNull(personModel.gender));
        templateStr = templateStr.replace("$specialty", checkNull(personModel.specialty));
        templateStr = templateStr.replace("$educational", checkNull(personModel.educational));
        templateStr = templateStr.replace("$degree", checkNull(personModel.degree));
        templateStr = templateStr.replace("$graduationdate", checkNull(personModel.graduationdate));
        templateStr = templateStr.replace("$entrydate", checkNull(personModel.entrydate));
        templateStr = templateStr.replace("$rank", checkNull(personModel.rank));
        templateStr = templateStr.replace("$titles", checkNull(personModel.titles));

        templateStr = templateStr.replace("$mobilePhone", checkNull(personModel.mobilePhone));
        templateStr = templateStr.replace("$officePhone", checkNull(personModel.officePhone));
        templateStr = templateStr.replace("$homeNum", checkNull(personModel.homeNum));
        templateStr = templateStr.replace("$mail", checkNull(personModel.mail));
        if (checkNull(personModel.iconurl).equals("")) {
            templateStr = templateStr.replace("$iconurl", "");
        } else {
            String name = StringUtils.generateFileName(personModel.iconurl);
            String iconUrl = "file://" + Environment.getExternalStorageDirectory() + "/updis/image/" + name;
            Logger.i("iconurl:", iconUrl);
            templateStr = templateStr.replace("$iconurl", iconUrl);
        }
        templateStr = templateStr.replace("$resume", checkNull(personModel.resume));

        ProxyBridge pBridge = new ProxyBridge();
        webView.addJavascriptInterface(pBridge, "ProxyBridge");
        webView.loadDataWithBaseURL(null, templateStr, "text/html", "utf-8", null);
    }

}
