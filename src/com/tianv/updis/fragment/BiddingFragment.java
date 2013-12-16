
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
import android.widget.ListView;

import com.melvin.android.base.fragment.BaseFragment;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.adapter.NoticeAdapter;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.tianv.updis.task.ResourcesLoadTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: BiddingFragment.java
 * @Description: TODO
 * @Date 2013-4-10 上午10:06:36
 */
public class BiddingFragment extends BaseFragment implements OnItemClickListener {
    private ListView listView;

    private NoticeAdapter noticeAdapter;

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

    // private TextView tvLoading;

    private ArrayList<ResourceModel> resourceList;

    private int loginTotalPage;

    /**
     * 资源载入任务
     */

    public BiddingFragment(Activity activity, String moduleCode) {
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
        // tvLoading = (TextView)
        // loadingLayout.findViewById(R.id.first_loading_text_id);
        // String rand = UIUtilities.getRandomStr(mActivity);
        // tvLoading.setText(rand);

        listView = (ListView) view.findViewById(R.id.list_view);
        View dummy = null;
        if (listView.getHeaderViewsCount() == 0) {
            dummy = new View(mActivity);
            listView.addFooterView(dummy);
        }

        noticeAdapter = new NoticeAdapter(mActivity, getListenerResource(), listView,
                Constant.VIEW_HOME_NOTICE);
        listView.setAdapter(noticeAdapter);
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
        if (noticeAdapter != null && !noticeAdapter.isEmpty()) {
            return;
        }
        onLoadingResource();
    }

    private void onLoadingResource() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            /*** 无数据，无网络 **/
            if (noticeAdapter.isEmpty()) {
                onError(AppException.NO_NETWORK_ERROR_CODE);
            } else {
                /** 有数据无网络 ***/
                UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
            }
            return;
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
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS,
                    Constant.UrlAlias.CATEGORY_NOTICE_ALIAS);
            /** 当前页码 */
            String page = String.valueOf(currentPageSize);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, page);
            /** 数据标识 */
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE, Constant.VIEW_HOME_NOTICE);
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
                if (noticeAdapter != null && noticeAdapter.isEmpty()) {
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
                if (appException != null) {
                    if (noticeAdapter != null && !noticeAdapter.isEmpty()) {
                        UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
                    } else {
                        onError(appException.errorCode);
                    }
                    return;
                }
                if (eParam != null) {
                    int size = eParam.size();
                    for (int i = 0; i < size; i++) {
                        ResourceModel resource = eParam.get(i);

                        noticeAdapter.add(resource);

                    }
                    currentPageSize++;
                }
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
        }
    }

    public void onDisplay() {
        super.onDisplay();
        if (noticeAdapter != null && !noticeAdapter.isEmpty()) {
            noticeAdapter.notifyDataSetChanged();
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
}
