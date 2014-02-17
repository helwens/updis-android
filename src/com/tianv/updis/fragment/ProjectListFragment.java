package com.tianv.updis.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melvin.android.base.common.ui.MListView;
import com.melvin.android.base.fragment.BaseFragment;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.activity.ProjectInfoActivity;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.ProjectModel;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource;
import com.tianv.updis.task.ProjectListTask;
import com.tianv.updis.task.ReLoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wincent on 14-2-16.
 */
public class ProjectListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private MListView listView;

//    private CommonListAdapter commonListAdapter;

    private ProjectListAdapter projectListAdapter;

    private ProjectListTask projectListTask;

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

    public ProjectListFragment(Activity activity, String moduleCode) {
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
                onLoadingResource();
            }
        });
        View dummy = null;
        if (listView.getHeaderViewsCount() == 0) {
            dummy = new View(mActivity);
            listView.addFooterView(dummy);
        }

//        commonListAdapter = new CommonListAdapter(mActivity, getListenerResource(), listView,
//                this.moduleCode);
        projectListAdapter = new ProjectListAdapter(new ArrayList<ProjectModel>());

        listView.setAdapter(projectListAdapter);
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
        if (projectListAdapter != null && !projectListAdapter.isEmpty()) {
            return;
        }
        onLoadingResource();
    }

    private void onLoadingResource() {
        Logger.i("onLoadingResource", "onLoadingResource");
        if (firstLoaded) {
            if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
                /*** 无数据，无网络 **/
                if (projectListAdapter.isEmpty()) {
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

        if (projectListTask == null
                || projectListTask.getStatus() == AsyncMockTask.Status.FINISHED) {

            projectListTask = new ProjectListTask(getResourceListTask(), ProjectListFragment.this.mActivity);
            projectListTask.execute();
        }
    }

    private CollectResource.PageFetcher getPageFetcherNew() {
        CollectResource.PageFetcher pageFetcher = new CollectResource.PageFetcher() {
            public void fetchPageInfo(int totalSize) {
                totalPage = totalSize;
            }
        };
        return pageFetcher;
    }

    private TaskCallBack<ProjectModel, ArrayList<ProjectModel>> getResourceListTask() {
        TaskCallBack<ProjectModel, ArrayList<ProjectModel>> taskCallBask = new TaskCallBack<ProjectModel, ArrayList<ProjectModel>>() {
            /**
             * @Title: beforeDoingTask
             * @Description: 任务开始回调
             */
            @Override
            public void beforeDoingTask() {
                listView.addFooterView(mFootView, null, false);
                mFootView.setVisibility(View.VISIBLE);
                if (projectListAdapter != null && projectListAdapter.isEmpty()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }
            }

            /**
             * @Title: doingTask
             * @Description: 任务正在进行中回调
             */
            @Override
            public void doingTask() {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void doingProgress(ProjectModel... fParam) {
            }

            /**
             * @param eParam
             * @param appException
             * @Title: endTask
             * @Description: 结束任务回调
             */
            @Override
            public void endTask(ArrayList<ProjectModel> eParam, AppException appException) {
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
                    if (projectListAdapter != null && projectListAdapter.getCount() != 0) {
                        Logger.i("endTask-----", String.valueOf(currentPageSize));
                        UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
                    } else {
                        onError(appException.errorCode);
                    }
                    listView.onRefreshComplete();
                    return;
                }
                if (eParam != null) {
                    projectListAdapter = new ProjectListAdapter(eParam);
                    listView.setAdapter(projectListAdapter);

                }
                listView.onRefreshComplete();
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
//        ResourceModel resource = (ResourceModel) listView.getAdapter().getItem(position);
//        if (resource != null) {
//            // 打开详情页面
//            UIUtilities.showDetail(mActivity, CommonResourceDetailActivity.class,
//                    resource.contentId, this.moduleCode);
//        }

        ProjectListFragment.this.mActivity.startActivityForResult(
                new Intent(this.mActivity, ProjectInfoActivity.class).putExtra(Constant.EXTRA_PROJECTMODEL,
                        (ProjectModel) projectListAdapter.getItem(position)), 11);
    }

    public void onDisplay() {
        super.onDisplay();
        if (projectListAdapter != null && !projectListAdapter.isEmpty()) {
            projectListAdapter.notifyDataSetChanged();
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

    private class ProjectListAdapter extends BaseAdapter {
        private ArrayList<ProjectModel> projectModels;

        private ProjectListAdapter(ArrayList<ProjectModel> projectModels) {
            this.projectModels = projectModels;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override
        public int getCount() {
            return projectModels.size();
        }

        @Override
        public Object getItem(int position) {
            return projectModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ProjectListFragment.this.mActivity).inflate(R.layout.resource_common_item, null);
            }


            TextView projectNameTv = (TextView) convertView.findViewById(R.id.project_name);
            TextView projectNumTv = (TextView) convertView.findViewById(R.id.project_num);
            projectNameTv.setText(projectModels.get(position).getProjectName());
            projectNumTv.setText(projectModels.get(position).getProjectNumber());
            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView iconView;

        TextView titleView;

        TextView dateView;

        TextView commentCountView;

        TextView subtitleView;

    }
}
