package com.tianv.updis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.ProjectModel;
import com.tianv.updis.task.ProjectListTask;
import com.tianv.updis.task.TaskCallBack;

import java.util.ArrayList;

/**
 * Created by lm3515 on 14-1-19.
 */
public class ProjectListActivity extends Activity {
    private ListView mProjectListLv;
    private View mFootView;
    private int totalPage;
    private ProjectAdapter projectAdapter;
    private LinearLayout bodyLayout;
    private ProjectListTask projectListTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectlist);
        findViewById();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        onLoadingResource();
    }

    private void findViewById() {
        mProjectListLv = (ListView) findViewById(R.id.projectlist_lv);
        mProjectListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("asdf", position + "");
                startActivityForResult(new Intent(ProjectListActivity.this, ProjectInfoActivity.class).putExtra(Constant.EXTRA_PROJECTMODEL, (ProjectModel) projectAdapter.getItem(position)), 11);
            }
        });
    }


    /**
     * 抓取资源
     *
     * @Title: fetchResource
     */
    public void onLoadingResource() {
        projectListTask = new ProjectListTask(getResourceListTask(), ProjectListActivity.this);
        projectListTask.execute();
    }


    private TaskCallBack<ProjectModel, ArrayList<ProjectModel>> getResourceListTask() {
        TaskCallBack<ProjectModel, ArrayList<ProjectModel>> taskCallBask = new TaskCallBack<ProjectModel, ArrayList<ProjectModel>>() {

            public void beforeDoingTask() {
            }

            public void doingTask() {

            }

            public void onCancel() {

            }

            /**
             * @param fParam
             * @Title: doingProgress
             * @Description: 有进度更新回调
             */
            @Override
            public void doingProgress(ProjectModel... fParam) {

            }

            public void endTask(ArrayList<ProjectModel> pModels, AppException appException) {
                final ProjectAdapter projectAdapter = new ProjectAdapter(pModels);
                mProjectListLv.setAdapter(projectAdapter);
                mProjectListLv.invalidate();
                mProjectListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("asdf", position + "");
                        startActivityForResult(new Intent(ProjectListActivity.this, ProjectInfoActivity.class).putExtra(Constant.EXTRA_PROJECTMODEL, (ProjectModel) projectAdapter.getItem(position)), 11);
                    }
                });
            }

            public void doingProgress(CommentModel... fParam) {

            }
        };
        return taskCallBask;
    }

    private void showToast(int data_empty) {
        Toast.makeText(this, data_empty, Toast.LENGTH_SHORT).show();
    }

    private class ProjectAdapter extends BaseAdapter {
        private ArrayList<ProjectModel> projectModels;

        private ProjectAdapter(ArrayList<ProjectModel> projectModels) {
            this.projectModels = projectModels;
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
                convertView = LayoutInflater.from(ProjectListActivity.this).inflate(R.layout.item_project, null);
            }
            TextView projectNameTv = (TextView) convertView.findViewById(R.id.project_name);
            TextView projectNumTv =(TextView) convertView.findViewById(R.id.project_num);
            projectNameTv.setText(projectModels.get(position).getProjectName());
            projectNumTv.setText(projectModels.get(position).getProjectNumber());
            return convertView;
        }

    }
}
