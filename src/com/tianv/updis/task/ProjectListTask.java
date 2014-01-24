package com.tianv.updis.task;

import android.content.Context;

import com.melvin.android.base.task.BaseTask;
import com.tianv.updis.AppException;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.ProjectModel;
import com.tianv.updis.network.CollectResource;
import com.uucun.android.logger.Logger;
import com.uucun.android.utils.io.IOUtils;

import java.util.ArrayList;

/**
 * Created by lm3515 on 14-1-22.
 */
public class ProjectListTask extends BaseTask<String, ProjectModel, ArrayList<ProjectModel>> {

    private CollectResource.PageFetcher pageFetcher;
    public ProjectListTask(TaskCallBack<ProjectModel, ArrayList<ProjectModel>> taskCallBack, Context context) {
        super(taskCallBack, context);
        this.pageFetcher = pageFetcher;
    }

    @Override
    protected void onPostExecute(ArrayList<ProjectModel> result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ProjectModel> doInBackground(String... params) {
        try {
            return CollectResource.getInstance(context).fetchProjectList(pageFetcher);
        } catch (AppException e) {
            appException = e;
            String s = IOUtils.exception2String(e);
            Logger.w("ResourcesLoadTask", "" + s);
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
