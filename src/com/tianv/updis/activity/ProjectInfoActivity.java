package com.tianv.updis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.model.ProjectModel;

/**
 * Created by lm3515 on 14-1-22.
 */
public class ProjectInfoActivity extends Activity {
    private TextView mProjectNameTv;
    private TextView mProjectIdTv;
    private TextView mProjectNumTv;
    private TextView mPartyNameTv;
    private TextView mDesignDepartmentTv;
    private TextView mProjectLeadersTv;
    private TextView mProjectScaleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        findViewById();
        initView();
    }

    private void initView() {
        ProjectModel pm = (ProjectModel) getIntent().getSerializableExtra(Constant.EXTRA_PROJECTMODEL);
        if (pm != null) {
            mProjectIdTv.setText(getFilterString(pm.getProjectId()));
            mProjectNameTv.setText(getFilterString(pm.getProjectName()));
            mProjectNumTv.setText(getFilterString(pm.getProjectNumber()));
            mDesignDepartmentTv.setText(getFilterString(pm.getDesignDepartment()));
            mProjectScaleTv.setText(getFilterString(pm.getProjectScale()));
            mPartyNameTv.setText(getFilterString(pm.getPartyAName()));
            //todo
            String leader[] = pm.getProjectLeaders().split(",");
            if (leader.length > 0) {
                String temp = "";
                for (String leader1 : leader) {
                    temp = temp + leader1 + ",";
                }
                mProjectLeadersTv.setText(temp.subSequence(0, temp.length() - 1));
            } else {
                mProjectLeadersTv.setText("-");
            }
        }
    }

    public String getFilterString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "-";
        } else {
            return str;
        }
    }

    private void findViewById() {
        mProjectNameTv = (TextView) findViewById(R.id.project_name);
        mProjectIdTv = (TextView) findViewById(R.id.project_id);
        mProjectNumTv = (TextView) findViewById(R.id.project_num);
        mPartyNameTv = (TextView) findViewById(R.id.party_name);
        mDesignDepartmentTv = (TextView) findViewById(R.id.designDepartment);
        mProjectLeadersTv = (TextView) findViewById(R.id.projectLeaders);
        mProjectScaleTv = (TextView) findViewById(R.id.projectScale);
    }
}
