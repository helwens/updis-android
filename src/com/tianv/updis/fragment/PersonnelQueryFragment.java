
package com.tianv.updis.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melvin.android.base.fragment.BaseFragment;
import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.adapter.CommonListAdapter;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.DictionaryModel;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource;
import com.tianv.updis.task.FetchDictionaryTask;
import com.tianv.updis.task.QueryPersonTask;
import com.tianv.updis.task.ReLoginTask;
import com.tianv.updis.task.TaskCallBack;
import com.uucun.android.data.query.Select;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.networkinfo.NetWorkInfo;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: PersonnelQueryFragment.java
 * @Description: TODO
 * @Date 2013-4-10 下午8:51:17
 */
public class PersonnelQueryFragment extends BaseFragment implements OnClickListener
        , OnItemClickListener, QueryPersonDetailView.DetailViewListener {

    GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;

    private static final int VIEW_BACK_LIST = 11;
    private static final int VIEW_BACK_FROM = 12;

    private FetchDictionaryTask fetchDictionaryTask = null;

    private RelativeLayout mContainer = null;

    private QueryPersonTask queryPersonTask = null;

    private int totalPage = 0;

    private int currentPageSize = 1;

    private Map<String, String> hashMap = null;

    private ImageView mQueryLogo = null;

    private RelativeLayout mSubjectPanel = null;

    private Button mQueryPost = null;

    private LinearLayout loadingLayout;

    private EditText mEditUserName = null;

    private TextView mEditUserDept = null;

    private TextView mEditUserSubject = null;

    /**
     * 姓名
     */
    private String userName = null;

    /**
     * 单位
     */
    private String userDept = null;

    /**
     * 专业
     */
    private String userSubject = null;

    /**
     * 人员列表
     */
    private View queryResultListView = null;

    private RelativeLayout queryFormPanel;

    private ListView queryListView = null;
    private View mFootView;

    private CommonListAdapter queryListAdapter = null;

    private QueryPersonDetailView queryPersonDetailView = null;

    private SharedStore mSharedStore = null;

    private String[] deptArr = null;
    private String[] subjectArr = null;

    private ArrayList<PersonModel> personModelArrayList = null;

    private boolean inListView = false;

    /**
     * 导航栏左侧按纽
     */
//    private ImageView mNavLeftButton = null;
    public PersonnelQueryFragment(Activity activity, String moduleCode) {
        super(activity, moduleCode);
        initView();
    }

    /**
     * 数据字典是否需要更新
     *
     * @return
     */
    private boolean checkNow() {
        if (mSharedStore == null) {
            mSharedStore = new SharedStore(mActivity, null);
        }
        long lastTime = mSharedStore.getLong(Constant.UU_APP_UPDATE_TIME_KEY, 0);
        if (((System.currentTimeMillis() - lastTime) > Constant.HOUR_TIME
                * Constant.DEFAULT_UPDATE_HOUR)) {
            return true;
        }
        return false;
    }


    /**
     * 初始化UI
     */
    private void initView() {
        hashMap = new HashMap<String, String>();
        mFootView = LayoutInflater.from(mActivity).inflate(R.layout.resources_progress_overlay,
                null);
//        if (mNavLeftButton == null) {
//            mNavLeftButton = (ImageView) mActivity.findViewById(R.id.nav_left_image);
//        }

//        mNavLeftButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                navLeftClick();
//            }
//        });
        if (checkNow()) {
            if (fetchDictionaryTask == null
                    || fetchDictionaryTask.getStatus() == AsyncMockTask.Status.FINISHED) {
                fetchDictionaryTask = new FetchDictionaryTask(mActivity, null);
                fetchDictionaryTask.execute();
            }
        }


        // init query result list view
        if (queryResultListView == null) {
            queryResultListView = LayoutInflater.from(mActivity).inflate(
                    R.layout.person_listview_layout, null);
        }
        if (queryListView == null) {
            queryListView = (ListView) queryResultListView.findViewById(R.id.list_view);
        }
        queryListAdapter = new CommonListAdapter(mActivity, getListenerResource(), queryListView,
                moduleCode);

        queryListView.setAdapter(queryListAdapter);
        queryListView.setOnItemClickListener(this);
        loadingLayout = (LinearLayout) queryResultListView.findViewById(R.id.loading);

        // ******************明细页面布局
        if (queryPersonDetailView == null) {
            queryPersonDetailView = new QueryPersonDetailView(mActivity);
            queryPersonDetailView.setDetailViewListener(this);
        }
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
    public View setBtnLayout(Context context, LayoutInflater inflate) {
        return null;
    }

    public View setBodyView(Context context, LayoutInflater inflate, int index) {
        View view = inflater.inflate(R.layout.personnel_query_layout, null);

        mContainer = (RelativeLayout) view.findViewById(R.id.body_container);
        queryFormPanel = (RelativeLayout) view.findViewById(R.id.query_form_panel);
        mQueryLogo = (ImageView) view.findViewById(R.id.query_logo);
        mSubjectPanel = (RelativeLayout) view.findViewById(R.id.query_subject_panel);
        mQueryPost = (Button) view.findViewById(R.id.btn_query);
        mEditUserName = (EditText) view.findViewById(R.id.txt_query_username);
        mEditUserDept = (TextView) view.findViewById(R.id.txt_query_dept);
        mEditUserDept.setOnClickListener(this);
        mEditUserSubject = (TextView) view.findViewById(R.id.txt_query_subject);
        mEditUserSubject.setOnClickListener(this);
        mQueryPost.setOnClickListener(this);
        if (this.moduleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
            // 通讯录
            mQueryLogo.setBackgroundResource(R.drawable.titles_2);
            mSubjectPanel.setVisibility(View.GONE);
        } else {
            // 人员查询
            mQueryLogo.setBackgroundResource(R.drawable.titles_1);
            mSubjectPanel.setVisibility(View.VISIBLE);
        }
        hashMap.put(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS, this.moduleCode);
        return view;
    }

    public void onChangeView(int index, View view) {
//        onLoadingResource();
        Logger.i("onChangeView:--------------", String.valueOf(index));
    }

    private void onLoadingResource() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            /*** 无数据，无网络 **/
            if (queryListAdapter.isEmpty()) {
                onError(AppException.NO_NETWORK_ERROR_CODE);
            } else {
                /** 有数据无网络 ***/
                UIUtilities.showCustomToast(mActivity, R.string.updis_network_error_tip);
            }
            return;
        }

        if (personModelArrayList != null) {
            if (currentPageSize > totalPage) {
                return;
            }
        } else {
            if (currentPageSize != 1 && currentPageSize > totalPage) {
                return;
            }
        }

        if (queryPersonTask == null
                || queryPersonTask.getStatus() == AsyncMockTask.Status.FINISHED) {
            String page = String.valueOf(currentPageSize);
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, page);
            // query list
            hashMap.put(Constant.UrlAlias.PARAMS_KEY_FLAG, "1");

            if (!UIUtilities.isNull(userName))
                hashMap.put(Constant.UrlAlias.PARAMS_KEY_NAME, userName);
            if (!UIUtilities.isNull(userDept))
                hashMap.put(Constant.UrlAlias.PARAMS_KEY_DEPT, userDept);
            if (!UIUtilities.isNull(userSubject))
                hashMap.put(Constant.UrlAlias.PARAMS_KEY_SUBJECT, userSubject);
            queryPersonTask = new QueryPersonTask(mActivity,
                    getQueryTaskCallBack(mActivity), getPageFetcherNew(), hashMap);
            queryPersonTask.execute();
        }
    }

    public void onDisplay() {
        Logger.i("onDisplay---", "onDisplay");
        super.onDisplay();
    }

    @Override
    public void onRetry() {
        onLoadingResource();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /**
     * 当无网络时展现提示信息
     */
    private boolean isNetworkAvailable() {
        if (!NetWorkInfo.isNetworkAvailable(mActivity)) {
            mDialog.showInfo(
                    mActivity.getResources().getString(R.string.updis_network_error_title),
                    mActivity.getResources().getString(R.string.updis_network_error_tip));
            return false;
        }
        return true;
    }

    /**
     * 检测文本输入
     */
    private boolean isInputAvailable() {
        userName = mEditUserName.getText().toString();
        userDept = mEditUserDept.getText().toString();
        userSubject = mEditUserSubject.getText().toString();
        return true;
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_query:
                InputMethodManager imm = (InputMethodManager) mActivity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mQueryPost.getWindowToken(), 0);
                if (!isNetworkAvailable()) {
                    return;
                }
                if (!isInputAvailable()) {
                    return;
                }
                //查询前清空缓存
                currentPageSize = 1;
                if (personModelArrayList != null) {
                    personModelArrayList.clear();
                    personModelArrayList = null;
                }
                queryListAdapter.clear();
                onLoadingResource();
                break;
            case R.id.txt_query_dept:
                if (deptArr == null) {
                    List<DictionaryModel> tempDept = new Select().from(DictionaryModel.class).where("dictKey='" + Constant.UPDIS_DIC_KEY_DEPT + "'").execute();
                    deptArr = new String[tempDept.size()];
                    for (int i = 0; i < tempDept.size(); i++) {
                        DictionaryModel temp = tempDept.get(i);
                        deptArr[i] = temp.dictValue;
                    }
                }
                mDialog.showInfo(0, "选择部门", deptArr, null, deptSelect);
                break;

            case R.id.txt_query_subject:
                if (subjectArr == null) {
                    List<DictionaryModel> tempSubject = new Select().from(DictionaryModel.class).where("dictKey='" + Constant.UPDIS_DIC_KEY_SUBJECT + "'").execute();
                    subjectArr = new String[tempSubject.size()];
                    for (int i = 0; i < tempSubject.size(); i++) {
                        DictionaryModel temp = tempSubject.get(i);
                        subjectArr[i] = temp.dictValue;
                    }
                }
                mDialog.showInfo(0, "选择专业", subjectArr, null, subjectSelect);
                break;
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

    /**
     * 查询结果回调
     *
     * @param currentActivity
     * @return
     */
    public TaskCallBack<Void, ArrayList<PersonModel>> getQueryTaskCallBack(final Activity currentActivity) {
        return new TaskCallBack<Void, ArrayList<PersonModel>>() {
            public void beforeDoingTask() {
                showProgressDialog();
                queryListView.addFooterView(mFootView, null, false);
                mFootView.setVisibility(View.VISIBLE);
                if (queryListAdapter != null && queryListAdapter.isEmpty()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }
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
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                if (appException != null) {
                    if (appException.errorCode == AppException.LOGIN_TIME_OUT) {
                        //登录超时
                        ReLoginTask reLoginTask = new ReLoginTask(mActivity);
                        reLoginTask.setReLoginTaskListener(reLoginTaskListener);
                        reLoginTask.login();
                        return;
                    }
                }
                loadingLayout.setVisibility(View.GONE);
                if (personModelArrayList != null) {
                    personModelArrayList.clear();
                    personModelArrayList = null;
                }
                personModelArrayList = eParam;
                loadPersonListView();

            }
        };
    }

    ReLoginTask.ReLoginTaskListener reLoginTaskListener = new ReLoginTask.ReLoginTaskListener() {
        @Override
        public void loginOK() {
            onLoadingResource();
        }
    };

    private void loadPersonListView() {
        if (personModelArrayList != null && personModelArrayList.size() > 0) {
            mContainer.removeAllViews();
            mContainer.addView(queryResultListView);
            inListView = true;
            int size = personModelArrayList.size();
            for (int i = 0; i < size; i++) {
                PersonModel personModel = personModelArrayList.get(i);
                queryListAdapter.add(personModel);
            }
            currentPageSize++;
        }
    }


    DialogInterface.OnClickListener deptSelect = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Logger.i("which", String.valueOf(which));
            mEditUserDept.setText(deptArr[which]);
            dialog.dismiss();
        }
    };

    DialogInterface.OnClickListener subjectSelect = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Logger.i("which", String.valueOf(which));
            mEditUserSubject.setText(subjectArr[which]);
            dialog.dismiss();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub
        PersonModel personModel = (PersonModel) queryListView.getAdapter().getItem(position);
        if (personModel != null) {
            // 显示人员明细
            Logger.i("click person", personModel.name);
            if (queryPersonDetailView == null) {
                queryPersonDetailView = new QueryPersonDetailView(mActivity);
            }
            if (moduleCode.equals(Constant.VIEW_PERSONNEL_QUERY)) {
                queryPersonDetailView.setPersonModel(personModel, false);
            } else {
                queryPersonDetailView.setPersonModel(personModel, true);
            }
            mContainer.removeAllViews();
            mContainer.addView(queryPersonDetailView.createView());
        }
    }


    @Override
    public void back() {
        Message message = sHandler.obtainMessage(VIEW_BACK_LIST);
        message.sendToTarget();
    }

    private final Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            Animation fade = AnimationUtils.loadAnimation(mActivity,
                    R.anim.fade);
            switch (msg.what) {
                case VIEW_BACK_LIST:

                    mContainer.removeAllViews();
                    queryResultListView.startAnimation(fade);
                    mContainer.addView(queryResultListView);
                    break;

                case VIEW_BACK_FROM:
                    mContainer.removeAllViews();
                    queryFormPanel.startAnimation(fade);
                    mContainer.addView(queryFormPanel);
                    break;

            }
        }
    };


    @Override
    public void clickTab(int index) {
        Logger.i("clickTab---------", "in this " + String.valueOf(index));
        if (inListView) {
            Animation fade = AnimationUtils.loadAnimation(mActivity,
                    R.anim.fade);
            mContainer.removeAllViews();
            queryFormPanel.startAnimation(fade);
            mContainer.addView(queryFormPanel);
            inListView = false;
        }
    }

}
