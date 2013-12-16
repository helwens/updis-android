
package com.tianv.updis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import com.melvin.android.base.activity.BaseActivity;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.adapter.CommonListAdapter;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.ResourceDetailModel;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.task.*;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.newstring.StringUtils;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CommonDetailActivity.java
 * @Description: TODO
 * @Date 2013-4-13 上午10:02:29
 */
public class CommonResourceDetailActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 详情ID
     */
    private String mContentId = null;

    /**
     * 详情类别
     */
    private String mCategoryType = null;

    /**
     * 详情拉取task
     */
    private CommonDetailTask commonDetailTask = null;

    private View loadingLayout;

    private WebView contentView = null;

    private LinearLayout commentPanel = null;

    /**
     * 评论列表
     */
    private ListView commentList = null;

    private LinearLayout relatedPanel = null;

    /**
     * 相关阅读
     */
    private ListView relatedList = null;

    private CommonListAdapter commentListAdapter = null;

    private CommonListAdapter relatedListAdapter = null;

    private TextView txtShowAll = null;

    private EditText editTextComment;

    private Button btnPostComment;

    private Button btnShowPostPanel;

    private LinearLayout mPostPanel;

    private PostCommentTask postCommentTask;
    private CheckBox checkBoxAnonymous;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.resource_detail_layout);
        mContext = this;
        bodyLayout = (LinearLayout) findViewById(R.id.bodyview);
        initView();

        mContentId = getIntent().getStringExtra(Constant.UPDIS_INTENT_KEY_CONTENTID);
        mCategoryType = getIntent().getStringExtra(Constant.UPDIS_INTENT_KEY_CATEGORYTYPE);
        checkBoxAnonymous = (CheckBox) findViewById(R.id.comment_isanonymous);
        checkBoxAnonymous.setChecked(false);
        loadResourceData();
    }

    protected void initView() {
        contentView = (WebView) findViewById(R.id.webContent);
        commentList = (ListView) findViewById(R.id.comment_list_view);
        commentPanel = (LinearLayout) findViewById(R.id.comment_panel);
        relatedList = (ListView) findViewById(R.id.related_list_view);
        relatedPanel = (LinearLayout) findViewById(R.id.related_panel);
        commentListAdapter = new CommonListAdapter(mContext, null, commentList,
                Constant.VIEW_COMMENT_LIST);
        commentList.setAdapter(commentListAdapter);

        relatedListAdapter = new CommonListAdapter(mContext, null, relatedList,
                Constant.VIEW_COMMENT_LIST);
        relatedList.setAdapter(relatedListAdapter);
        txtShowAll = (TextView) findViewById(R.id.detail_comment_show_all);
        txtShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommonListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.UPDIS_INTENT_KEY_CONTENTID, mContentId);
                intent.putExtra(Constant.UPDIS_INTENT_KEY_CATEGORYTYPE, Constant.VIEW_COMMENT_LIST);
                mContext.startActivity(intent);
            }
        });

        btnShowPostPanel = (Button) findViewById(R.id.detail_common_list_button_write);
        btnShowPostPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostPanel.getVisibility() == View.VISIBLE) {
                    mPostPanel.setVisibility(View.GONE);
                } else {
                    mPostPanel.setVisibility(View.VISIBLE);
                    UIUtilities.spaceAnimation(mPostPanel, mContext);
                }
            }
        });
        mPostPanel = (LinearLayout) findViewById(R.id.post_comment_panel);
        btnPostComment = (Button) findViewById(R.id.btn_post_comment);
        btnPostComment.setOnClickListener(this);
        editTextComment = (EditText) findViewById(R.id.edit_comment);

        super.initView();
        mPostPanel.setVisibility(View.GONE);
        mNavLeftButton.setVisibility(View.VISIBLE);


        UpdateApp.getInstance(mContext).update(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void loadResourceData() {
        // TODO Auto-generated method stub
        if (commonDetailTask == null
                || commonDetailTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            Map<String, String> params = new HashMap<String, String>();

            params.put(Constant.UrlAlias.PARAMS_KEY_CONTENTID, mContentId);
            params.put(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE, mCategoryType);
            commonDetailTask = new CommonDetailTask(getApplicationContext(),
                    getDetailTaskCallBack(CommonResourceDetailActivity.this), params);

            commonDetailTask.execute();
        }
    }

    public TaskCallBack<Void, ResourceDetailModel> getDetailTaskCallBack(
            final Activity currentActivity) {
        return new TaskCallBack<Void, ResourceDetailModel>() {
            public void beforeDoingTask() {
                // showProgressDialog();
                ViewStub loading = (ViewStub) findViewById(R.id.resource_detail_loading);
                if (loading != null) {
                    loadingLayout = loading.inflate();
                }
                loadingLayout.setVisibility(View.VISIBLE);
            }

            public void doingTask() {

            }

            public void onCancel() {

            }

            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(ResourceDetailModel resourceDetailModel, AppException appException) {
                // TODO Auto-generated method stub
                if (loadingLayout != null) {
                    loadingLayout.setVisibility(View.GONE);
                }
//                if (!NetWorkInfo.isNetworkAvailable(mContext)) {
//                    appException = new AppException(AppException.NO_NETWORK_ERROR_CODE, null);
//                    appException.errorCode = AppException.NO_NETWORK_ERROR_CODE;
//                }

                if (appException != null && resourceDetailModel == null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时


                        SharedStore sharedStore = new SharedStore(mContext, null);
                        String userName = sharedStore.getString(Constant.UPDIS_STORE_KEY_USERNAME, "");
                        String userPwd = sharedStore.getString(Constant.UPDIS_STORE_KEY_USERPWD, "");
                        if (UIUtilities.isNull(userName)) {
                            //跳转到登录页面
                            Intent intent = new Intent(CommonResourceDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            CommonResourceDetailActivity.this.finish();
                            return;
                        }

                        ReLoginTask reLoginTask = new ReLoginTask(mContext);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                    if (resourceDetailModel == null) {
                        onError(appException.errorCode);
                    }
                    return;
                }
                if (resourceDetailModel != null) {
                    writeContent(resourceDetailModel);
                    if (!UIUtilities.isNull(resourceDetailModel.relatedList)) {
                        relatedPanel.setVisibility(View.VISIBLE);
                        int size = resourceDetailModel.relatedList.size();
                        for (int i = 0; i < size; i++) {
                            ResourceModel resource = resourceDetailModel.relatedList.get(i);
                            relatedListAdapter.add(resource);
                        }
                    }
                    if (!UIUtilities.isNull(resourceDetailModel.commentList)) {
                        commentPanel.setVisibility(View.VISIBLE);
                        if (!commentListAdapter.isEmpty()) {
                            commentListAdapter.clear();
                        }
                        int size = resourceDetailModel.commentList.size();
                        for (int i = 0; i < size; i++) {
                            CommentModel commentModel = resourceDetailModel.commentList.get(i);
                            commentListAdapter.add(commentModel);
                        }
                        setListViewHeight(commentList);
                    }
                }
            }
        };
    }

    ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
            loadResourceData();
        }
    };

    private void writeContent(ResourceDetailModel resourceDetailModel) {
        String templateStr = null;
        try {
            InputStream in = getResources().getAssets().open("content.htm");
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
        templateStr = templateStr.replace("$title", checkNull(resourceDetailModel.title));
        templateStr = templateStr.replace("$date", checkNull(resourceDetailModel.date));
        templateStr = templateStr.replace("$dept", checkNull(resourceDetailModel.dept));
        templateStr = templateStr.replace("$author", checkNull(resourceDetailModel.author));
        templateStr = templateStr.replace("$readCount", checkNull(resourceDetailModel.readCount));
        templateStr = templateStr.replace("$custlist", checkNull(resourceDetailModel.messageDetailMeta));
        templateStr = templateStr.replace("$content", checkNull(resourceDetailModel.content));
        contentView.loadDataWithBaseURL(null, templateStr, "text/html", "utf-8", null);
    }

    private String checkNull(String old) {
        return UIUtilities.isNull(old) ? "" : old;
    }

    @Override
    public void navLeftClick() {
        // TODO Auto-generated method stub
        this.finish();
    }

    @Override
    public void navRightClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // hide menu if menu is showing
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        String comment = editTextComment.getText().toString();
        if (StringUtils.isNullAndBlank(comment)) {
            UIUtilities.showToast(mContext, "请输入评论内容.");
            return;
        }


        if (postCommentTask == null
                || postCommentTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            /** 数据接口 */


            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.UrlAlias.PARAMS_KEY_CONTENTID, mContentId);
            params.put(Constant.UrlAlias.PARAMS_KEY_ISANONYMOUS, checkBoxAnonymous.isChecked() ? "true" : "false");
            params.put(Constant.UrlAlias.PARAMS_KEY_COMMENT, comment);

            postCommentTask = new PostCommentTask(mContext, getPostCommentTask()
                    , params);
            postCommentTask.execute();
        }
    }

    private TaskCallBack<Void, String> getPostCommentTask() {
        TaskCallBack<Void, String> taskCallBask = new TaskCallBack<Void, String>() {


            @Override
            public void beforeDoingTask() {

            }

            @Override
            public void doingTask() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void doingProgress(Void... fParam) {

            }

            @Override
            public void endTask(String eParam, AppException appException) {
                if (eParam != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(eParam);
                        if (jsonObject.getString("success").equals("1")) {
                            UIUtilities.showToast(mContext, "评论成功");
                            loadResourceData();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return taskCallBask;
    }

    private void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if (null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for (int i = 0; i < cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lv.setLayoutParams(lp);
    }
}
