
package com.tianv.updis.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.melvin.android.base.common.ui.MListView;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.adapter.CommonListAdapter;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.tianv.updis.task.CommentListLoadTask;
import com.tianv.updis.task.PostCommentTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.utils.networkinfo.NetWorkInfo;
import com.uucun.android.utils.newstring.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CommonListActivity.java
 * @Description: TODO
 * @Date 2013-4-11 下午8:20:40
 */
public class CommonListActivity extends Activity implements OnClickListener {

    private LinearLayout bodyLayout;

    private MListView listView;

    private LinearLayout loadingLayout;

    private View view;

    private View viewException;

    private View mFootView;

    private CommonListAdapter commonListAdapter;

    private int totalPage = 0;

    private int currentPageSize = 1;

    private Map<String, String> hashMap = null;

    private CommentListLoadTask commentListLoadTask = null;

    private PostCommentTask postCommentTask = null;

    private Context mContext;

    private String mContentId;

    private String moduleCode;

    private EditText editTextComment;

    private Button btnPostComment;

    private LinearLayout mPostPanel;

    private CheckBox checkBoxAnonymous;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list_layout);
        mContext = this;
        bodyLayout = (LinearLayout) findViewById(R.id.body_layout_id);

        mPostPanel = (LinearLayout) findViewById(R.id.post_comment_panel);
        btnPostComment = (Button) findViewById(R.id.btn_post_comment);
        btnPostComment.setOnClickListener(this);
        editTextComment = (EditText) findViewById(R.id.edit_comment);
        TextView tvHeader = (TextView) findViewById(R.id.nav_title);
        mFootView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.resources_progress_overlay, null);

        checkBoxAnonymous = (CheckBox) findViewById(R.id.comment_isanonymous);
        checkBoxAnonymous.setChecked(false);

        tvHeader.setText("评论列表");

        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        };
        ImageView btn_back = (ImageView) findViewById(R.id.nav_left_image);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(clickListener);


        ImageView rightButton = (ImageView) findViewById(R.id.nav_right_image);
        rightButton.setVisibility(View.GONE);
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtilities.spaceAnimation(mPostPanel, mContext);
                if (mPostPanel.getVisibility() == View.VISIBLE) {
                    mPostPanel.setVisibility(View.GONE);
                } else {
                    mPostPanel.setVisibility(View.VISIBLE);
                }
            }
        });


        mContentId = getIntent().getStringExtra(Constant.UPDIS_INTENT_KEY_CONTENTID);
        moduleCode = getIntent().getStringExtra(Constant.UPDIS_INTENT_KEY_CATEGORYTYPE);

//        mPostPanel.setVisibility(View.GONE);
        setupViews();
    }

    protected void onDestroy() {
        if (commonListAdapter != null) {
            commonListAdapter.destroyAdapter();
            commonListAdapter = null;
        }

        if (commentListLoadTask != null) {
            commentListLoadTask.cancel(true);
            commentListLoadTask = null;
        }
        bodyLayout = null;
        listView = null;
        loadingLayout = null;
        view = null;
        viewException = null;
        mFootView = null;

        // GC
        System.gc();
        super.onDestroy();
    }

    private void setupViews() {
        View dummy = null;
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        } else {
            view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.listview_no_ad_layout, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            bodyLayout.addView(view, layoutParams);
            listView = (MListView) view.findViewById(R.id.list_view);

            listView.setonRefreshListener(new MListView.OnRefreshListener() {
                public void onRefresh() {
                    //清空缓存数据
                    currentPageSize = 1;
                    commonListAdapter.clear();
                    onLoadingResource();
                }
            });
            loadingLayout = (LinearLayout) view.findViewById(R.id.loading);


            if (listView.getHeaderViewsCount() == 0) {
                dummy = new View(mContext);
                listView.addFooterView(dummy);
            }
            if (commonListAdapter == null) {
                commonListAdapter = new CommonListAdapter(mContext, getListenerResource(),
                        listView, moduleCode);
            }
            listView.setAdapter(commonListAdapter);

            if (dummy != null) {
                listView.removeFooterView(dummy);
            }
            listView.setOnItemClickListener(getItemClick(listView, moduleCode));
        }
        onLoadingResource();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (commonListAdapter != null) {
            commonListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 抓取资源
     *
     * @Title: fetchResource
     */
    public void onLoadingResource() {
        if (!NetWorkInfo.isNetworkAvailable(mContext)) {
            if (commonListAdapter.isEmpty()) {
                setExceptionView(bodyLayout, AppException.NO_NETWORK_ERROR_CODE);
                loadingLayout.setVisibility(View.GONE);
            } else {
                /** 有数据无网络 ***/
                UIUtilities.showCustomToast(mContext, R.string.updis_network_error_tip);
            }
            return;
        }
        if (currentPageSize != 1 && currentPageSize > totalPage) {
            return;
        }
        if (commentListLoadTask == null
                || commentListLoadTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            /** 数据接口 */
            if (hashMap == null) {
                hashMap = new HashMap<String, String>();
            }

            hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS, moduleCode);
            /** 当前页码 */
            String page = String.valueOf(currentPageSize);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, page);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_MESSAGEID, mContentId);


            commentListLoadTask = new CommentListLoadTask(mContext, getResourceListTask(),
                    getPageFetcherNew(), hashMap);
            commentListLoadTask.execute();
        }

    }

    /**
     * 当无网络或者无数据时展现的view
     */
    private void setExceptionView(LinearLayout bodyLayout, int exceptionCode) {
        if (bodyLayout == null) {
            return;
        }

        if (view != null) {
            view.setVisibility(View.GONE);
        }

        if (viewException == null) {
            viewException = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.no_networking_status, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            bodyLayout.addView(viewException, layoutParams);
        } else {
            viewException.setVisibility(View.VISIBLE);
        }
        ImageView img = (ImageView) viewException.findViewById(R.id.img_id);
        TextView to = (TextView) viewException.findViewById(R.id.text_one);
        TextView ts = (TextView) viewException.findViewById(R.id.text_second);
        TextView tt = (TextView) viewException.findViewById(R.id.text_three);
        Button tryBtn = (Button) viewException.findViewById(R.id.tryagain_btn_id);
        tryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkInfo.isNetworkAvailable(CommonListActivity.this)) {
                    UIUtilities.showCustomToast(CommonListActivity.this,
                            R.string.updis_network_error_tip);
                } else {
                    viewException.setVisibility(View.GONE);
                    setupViews();
                }
            }
        });
        UIUtilities.showExceptionView(exceptionCode, img, to, ts, tt, tryBtn);
    }

    private PageFetcher getPageFetcherNew() {
        PageFetcher pageFetcher = new PageFetcher() {
            public void fetchPageInfo(int totalSize) {
                totalPage = totalSize;
            }
        };
        return pageFetcher;
    }

    private TaskCallBack<CommentModel, ArrayList<CommentModel>> getResourceListTask() {
        TaskCallBack<CommentModel, ArrayList<CommentModel>> taskCallBask = new TaskCallBack<CommentModel, ArrayList<CommentModel>>() {

            public void beforeDoingTask() {
                listView.addFooterView(mFootView, null, false);
                mFootView.setVisibility(View.VISIBLE);
                if (commonListAdapter != null && commonListAdapter.isEmpty()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }
            }

            public void doingTask() {

            }

            public void onCancel() {

            }

            public void endTask(ArrayList<CommentModel> eParam, AppException appException) {
                listView.removeFooterView(mFootView);
                loadingLayout.setVisibility(View.GONE);
                if (appException != null) {
                    if (commonListAdapter != null && !commonListAdapter.isEmpty()) {
                        UIUtilities.showCustomToast(mContext, R.string.updis_network_error_tip);
                    } else {
                        setExceptionView(bodyLayout, AppException.NO_NETWORK_ERROR_CODE);
                    }
                    return;
                }
                if (eParam != null) {
                    int size = eParam.size();
                    for (int i = 0; i < size; i++) {
                        CommentModel commentModel = eParam.get(i);
                        commonListAdapter.add(commentModel);
                    }
                    currentPageSize++;
                }
                listView.onRefreshComplete();
            }

            public void doingProgress(CommentModel... fParam) {

            }
        };
        return taskCallBask;
    }

    private LoadResourceListener getListenerResource() {
        LoadResourceListener listener = new LoadResourceListener() {
            public void loadResource() {
                onLoadingResource();
            }
        };
        return listener;
    }

    /**
     * 取得点击的事件
     *
     * @param listView
     * @return
     */
    private OnItemClickListener getItemClick(final ListView listView, final String moduleCode) {
        OnItemClickListener listener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                setItemClick(listView, position, moduleCode);
            }
        };
        return listener;
    }

    private void setItemClick(ListView listView, int position, String moduleCode) {

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
                            currentPageSize = 1;
                            commonListAdapter.clear();
                            onLoadingResource();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return taskCallBask;
    }
}
