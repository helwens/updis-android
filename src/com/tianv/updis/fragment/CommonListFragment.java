
package com.tianv.updis.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.melvin.android.base.common.ui.MListView;
import com.melvin.android.base.fragment.BaseFragment;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.activity.CommonResourceDetailActivity;
import com.tianv.updis.adapter.CommonListAdapter;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.tianv.updis.task.ReLoginTask;
import com.tianv.updis.task.ResourcesLoadTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: NoticeFragment.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:41:12
 */
public class CommonListFragment extends BaseFragment implements OnItemClickListener {

    private MListView listView;

    private CommonListAdapter commonListAdapter;

    private ResourcesLoadTask resourcesLoadTask;

    private int totalPage = 0;

    private int currentPageSize = 1;

    private Map<String, String> hashMap = null;

    private View mFootView;

    private LinearLayout loadingLayout;

    /**
     * 最新索引
     */
    public static int NEW_INDEX = 0;

    private ArrayList<ResourceModel> resourceList;

    private int loginTotalPage;

    private boolean firstLoaded = false;

    /**
     * 资源载入任务
     */

    public CommonListFragment(Activity activity, String moduleCode) {
        super(activity, moduleCode);
        initView();
    }

    private void initView() {
        hashMap = new HashMap<String, String>();
        mFootView = LayoutInflater.from(mActivity).inflate(R.layout.resources_progress_overlay,
                null);
    }

    @Override
    public View setBtnLayout(Context context, LayoutInflater inflater) {
        return null;
    }

    public View setBodyView(Context context, LayoutInflater inflater, int index) {
        View view = inflater.inflate(R.layout.listview_no_ad_layout, null);
        loadingLayout = (LinearLayout) view.findViewById(R.id.loading);

        listView = (MListView) view.findViewById(R.id.list_view);

        listView.setonRefreshListener(new MListView.OnRefreshListener() {
            public void onRefresh() {
                //清空缓存数据
                currentPageSize = 1;
                if (resourceList != null) {
                    resourceList.clear();
                    resourceList = null;
                }
                commonListAdapter.clear();
                onLoadingResource();
            }
        });
        View dummy = null;
        if (listView.getHeaderViewsCount() == 0) {
            dummy = new View(mActivity);
            listView.addFooterView(dummy);
        }

        commonListAdapter = new CommonListAdapter(mActivity, getListenerResource(), listView,
                this.moduleCode);
        listView.setAdapter(commonListAdapter);
        if (resourceList != null) {
            totalPage = loginTotalPage;
            if (loginTotalPage == 1) {
                currentPageSize = 2;
            } else if (loginTotalPage >= 2) {
                currentPageSize = 3;
            }
            loadingLayout.setVisibility(View.GONE);
        }
        if (dummy != null) {
            listView.removeFooterView(dummy);
        }
        listView.setOnItemClickListener(this);

        return view;
    }

    public void onChangeView(int index, View view) {
        if (commonListAdapter != null && !commonListAdapter.isEmpty()) {
            return;
        }
        onLoadingResource();
    }

    private void onLoadingResource() {
        Logger.i("onLoadingResource", "onLoadingResource");
        if (firstLoaded) {
            if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
                /*** 无数据，无网络 **/
                if (commonListAdapter.isEmpty()) {
                    onError(AppException.NO_NETWORK_ERROR_CODE);
                } else {
                    /** 有数据无网络 ***/
                    UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
                }
                return;
            }
        }
        if (resourceList != null) {
            if (currentPageSize > totalPage) {
                return;
            }
        } else {
            if (currentPageSize != 1 && currentPageSize > totalPage) {
                return;
            }
        }

        if (resourcesLoadTask == null
                || resourcesLoadTask.getStatus() == AsyncMockTask.Status.FINISHED) {

            /** 数据接口 */
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS, this.moduleCode);
            /** 当前页码 */
            String page = String.valueOf(currentPageSize);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, page);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE, moduleCode);
            resourcesLoadTask = new ResourcesLoadTask(mActivity, getResourceListTask(),
                    getPageFetcherNew(), hashMap);
            resourcesLoadTask.execute();
        }
    }

    private PageFetcher getPageFetcherNew() {
        PageFetcher pageFetcher = new PageFetcher() {
            public void fetchPageInfo(int totalSize) {
                totalPage = totalSize;
            }
        };
        return pageFetcher;
    }

    private TaskCallBack<ResourceModel, ArrayList<ResourceModel>> getResourceListTask() {
        TaskCallBack<ResourceModel, ArrayList<ResourceModel>> taskCallBask = new TaskCallBack<ResourceModel, ArrayList<ResourceModel>>() {

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

            public void endTask(ArrayList<ResourceModel> eParam, AppException appException) {
                listView.removeFooterView(mFootView);
                loadingLayout.setVisibility(View.GONE);
                firstLoaded = true;
                if (appException != null && eParam == null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时
                        ReLoginTask reLoginTask = new ReLoginTask(mActivity);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                    if (commonListAdapter != null && !commonListAdapter.isEmpty()) {
                        Logger.i("endTask-----", String.valueOf(currentPageSize));
                        UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
                    } else {
                        onError(appException.errorCode);
                    }
                    listView.onRefreshComplete();
                    return;
                }
                if (eParam != null) {
                    int size = eParam.size();
                    for (int i = 0; i < size; i++) {
                        ResourceModel resource = eParam.get(i);
                        commonListAdapter.add(resource);
                    }
                    currentPageSize++;
                }
                listView.onRefreshComplete();
            }

            public void doingProgress(ResourceModel... fParam) {

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

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        ResourceModel resource = (ResourceModel) listView.getAdapter().getItem(position);
        if (resource != null) {
            // 打开详情页面
            UIUtilities.showDetail(mActivity, CommonResourceDetailActivity.class,
                    resource.contentId, this.moduleCode);
        }
    }

    public void onDisplay() {
        super.onDisplay();
        if (commonListAdapter != null && !commonListAdapter.isEmpty()) {
            commonListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRetry() {
        onLoadingResource();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (listView != null) {
            listView.setSelection(listView.getFirstVisiblePosition());
        }
    }

    ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
            onLoadingResource();
        }
    };


}
