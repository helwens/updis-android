
package com.tianv.updis.network;

import android.content.Context;

import com.tianv.updis.Constant;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.DictionaryModel;
import com.tianv.updis.model.JsonConst;
import com.tianv.updis.model.LoginDataModel;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.ProjectModel;
import com.tianv.updis.model.ResourceDetailModel;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;
import com.tianv.updis.network.CollectResource.PageFetcher;
import com.uucun.android.data.query.Select;
import com.uucun.android.data.util.Log;
import com.uucun.android.utils.date.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: JsonDataParser.java
 * @Description: TODO
 * @Date 2013-3-24 下午3:02:16
 */
public class JsonDataParser {

    /**
     * @param
     * @return
     * @Description:构造函数
     */
    public JsonDataParser(Context context) {
        super();
    }

    /**
     * 资源列表
     *
     * @param json
     * @param pageFetch
     * @return
     * @throws JSONException
     */
    public ArrayList<ResourceModel> getResourceList(String json, PageFetcher pageFetch)
            throws JSONException {
        if (json == null || json.trim().equals("")) {
            return null;
        }
        ArrayList<ResourceModel> arrayList = new ArrayList<ResourceModel>();
        try {

            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray(JsonConst.DATA);

            /** 开始循环解析 **/
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                ResourceModel resource;
                if (jsonObj.has(JsonConst.TOTAL_PAGE)) {
                    int total = Integer.valueOf(jsonObj.getString(JsonConst.TOTAL_PAGE));
                    if (pageFetch != null) {
                        pageFetch.fetchPageInfo(total);
                    }
                }
                resource = getResourceModel(jsonObject);
                arrayList.add(resource);
            }
        } catch (JSONException e) {
            throw e;
        }
        return arrayList.isEmpty() ? null : arrayList;
    }

    public LoginDataModel getLoginData(String json) throws JSONException {
        if (json == null || json.trim().equals("")) {
            return null;
        }
        LoginDataModel loginDataModel = new LoginDataModel();
        try {

            JSONObject jsonObject = new JSONObject(json);
            /** 开始解析 **/
            if (jsonObject.has(JsonConst.UPDIS_JSON_SUCCESS)) {
                loginDataModel.success = jsonObject.getString(JsonConst.UPDIS_JSON_SUCCESS);
            }
            if (jsonObject.has(JsonConst.UPDIS_JSON_MSG)) {
                loginDataModel.msg = jsonObject.getString(JsonConst.UPDIS_JSON_MSG);
            }
            if (jsonObject.has(JsonConst.UPDIS_JSON_USERID)) {
                loginDataModel.userid = jsonObject.getString(JsonConst.UPDIS_JSON_USERID);
            }
            if (jsonObject.has(JsonConst.UPDIS_JSON_REG)) {
                loginDataModel.registered = jsonObject.getString(JsonConst.UPDIS_JSON_REG);
            }
            if (jsonObject.has(JsonConst.UPDIS_JSON_PHONENUM)) {
                loginDataModel.phoneNum = jsonObject.getString(JsonConst.UPDIS_JSON_PHONENUM);
            }
            if (jsonObject.has(JsonConst.UPDIS_JSON_ISSPECAILUSER)) {
                loginDataModel.isSpecailUser = jsonObject.getString(JsonConst.UPDIS_JSON_ISSPECAILUSER);
            }

        } catch (JSONException e) {
            throw e;
        }
        return loginDataModel;
    }

    public void getDictData(String json) throws JSONException {
        // TODO Auto-generated method stub
        if (json == null || json.trim().equals("")) {
            return;
        }
        try {

            JSONObject jsonObj = new JSONObject(json);
            JSONObject tempObj = jsonObj.getJSONObject(JsonConst.DATA);
            if (tempObj.has(JsonConst.UPDIS_JSON_DEPT)) {
                JSONArray jsonArray = tempObj.getJSONArray(JsonConst.UPDIS_JSON_DEPT);
                if (jsonArray != null && jsonArray.length() > 0) {
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                        DictionaryModel dictionaryModel = new DictionaryModel();
                        dictionaryModel.dictKey = Constant.UPDIS_DIC_KEY_DEPT;
                        dictionaryModel.dictValue = jsonObject
                                .getString(JsonConst.UPDIS_JSON_DEPT_NAME);
                        List<DictionaryModel> nowList = new Select()
                                .from(DictionaryModel.class)
                                .where("dictKey='" + Constant.UPDIS_DIC_KEY_DEPT + "' and dictValue='"
                                        + jsonObject.getString(JsonConst.UPDIS_JSON_DEPT_NAME) + "'")
                                .execute();
                        if (nowList != null && nowList.size() > 0) {
                            for (DictionaryModel temp : nowList) {
                                temp.delete();
                            }
                        }
                        dictionaryModel.save();
                    }
                }
            }
            if (tempObj.has(JsonConst.UPDIS_JSON_SUBJECT)) {
                JSONArray jsonArray = tempObj.getJSONArray(JsonConst.UPDIS_JSON_SUBJECT);
                if (jsonArray != null && jsonArray.length() > 0) {
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                        DictionaryModel dictionaryModel = new DictionaryModel();
                        dictionaryModel.dictKey = Constant.UPDIS_DIC_KEY_SUBJECT;
                        dictionaryModel.dictValue = jsonObject
                                .getString(JsonConst.UPDIS_JSON_SUBJECT_NAME);
                        List<DictionaryModel> nowList = new Select()
                                .from(DictionaryModel.class)
                                .where("dictKey='" + Constant.UPDIS_DIC_KEY_SUBJECT
                                        + "' and dictValue='"
                                        + jsonObject.getString(JsonConst.UPDIS_JSON_SUBJECT_NAME)
                                        + "'").execute();
                        if (nowList != null && nowList.size() > 0) {
                            for (DictionaryModel temp : nowList) {
                                temp.delete();
                            }
                        }
                        dictionaryModel.save();
                    }
                }
            }

        } catch (JSONException e) {
            throw e;
        }
    }

    /**
     * 解析详情页面JSON数据
     */
    public ResourceDetailModel getResourceDetail(String json) throws JSONException {
        // TODO Auto-generated method stub
        if (json == null || json.trim().equals("")) {
            return null;
        }
        ResourceDetailModel resourceDetailModel = new ResourceDetailModel();
        try {

            JSONObject jsonObj = new JSONObject(json);
            JSONObject tempObj = jsonObj.getJSONObject(JsonConst.DATA);
            /** 开始详情数据解析 **/
            if (tempObj.has(JsonConst.UPDIS_JSON_CONTENT)) {
                JSONObject contentObj = tempObj.getJSONObject(JsonConst.UPDIS_JSON_CONTENT);
                if (contentObj != null && contentObj.length() > 0) {
                    if (contentObj.has(JsonConst.UPDIS_JSON_TITLE)) {
                        resourceDetailModel.title = contentObj
                                .getString(JsonConst.UPDIS_JSON_TITLE);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_DATE)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(contentObj.getString(JsonConst.UPDIS_JSON_DATE));
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        resourceDetailModel.date = DateUtil.dateToString(date);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_DEPT)) {
                        resourceDetailModel.dept = contentObj.getString(JsonConst.UPDIS_JSON_DEPT);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_AUTHOR)) {
                        resourceDetailModel.author = contentObj
                                .getString(JsonConst.UPDIS_JSON_AUTHOR);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_READCOUNT)) {
                        resourceDetailModel.readCount = contentObj
                                .getString(JsonConst.UPDIS_JSON_READCOUNT);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_CONTENT)) {
                        resourceDetailModel.content = contentObj
                                .getString(JsonConst.UPDIS_JSON_CONTENT);
                    }
                    if (contentObj.has(JsonConst.UPDIS_JSON_MESSAGEDETAILMETA)) {
                        resourceDetailModel.messageDetailMeta = contentObj
                                .getString(JsonConst.UPDIS_JSON_MESSAGEDETAILMETA);
                    }
                }
            }

            /** 开始解析评论数据 **/
            if (tempObj.has(JsonConst.UPDIS_JSON_COMMENT)) {
                try {
                    JSONArray commentArray = tempObj.getJSONArray(JsonConst.UPDIS_JSON_COMMENT);
                    ArrayList<CommentModel> tempList = new ArrayList<CommentModel>();
                    if (commentArray != null && commentArray.length() > 0) {
                        for (int i = 0; i < commentArray.length(); i++) {
                            JSONObject temp = commentArray.getJSONObject(i);
                            if (temp != null) {
                                CommentModel commentModel = new CommentModel();
                                if (temp.has(JsonConst.UPDIS_JSON_COMMENT_COMMENTID)) {
                                    commentModel.commentId = temp.getString(JsonConst.UPDIS_JSON_COMMENT_COMMENTID);
                                }
                                if (temp.has(JsonConst.UPDIS_JSON_AUTHOR)) {
                                    commentModel.author = temp.getString(JsonConst.UPDIS_JSON_AUTHOR);
                                    if (UIUtilities.isNull(commentModel.author)) {
                                        commentModel.author = "匿名";
                                    }
                                }
                                if (temp.has(JsonConst.UPDIS_JSON_DATETIME)) {
                                    commentModel.datetime = temp.getString(JsonConst.UPDIS_JSON_DATETIME);
                                }
                                if (temp.has(JsonConst.UPDIS_JSON_ICONURL)) {
                                    commentModel.iconUrl = temp.getString(JsonConst.UPDIS_JSON_ICONURL);
                                }
                                if (temp.has(JsonConst.UPDIS_JSON_CONTENT)) {
                                    commentModel.content = temp.getString(JsonConst.UPDIS_JSON_CONTENT);
                                }
                                if (temp.has(JsonConst.UPDIS_JSON_COMMENT_ISANONYMOUS)) {
                                    commentModel.isAnonymous = temp.getString(JsonConst.UPDIS_JSON_COMMENT_ISANONYMOUS);
                                }

                                tempList.add(commentModel);
                            }
                        }
                        resourceDetailModel.commentList = tempList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            /** 开始解析相关阅读 **/
            if (tempObj.has(JsonConst.UPDIS_JSON_RELATED)) {
                try {
                    JSONArray relatedArray = tempObj.getJSONArray(JsonConst.UPDIS_JSON_RELATED);
                    ArrayList<ResourceModel> tempList = new ArrayList<ResourceModel>();
                    if (relatedArray != null && relatedArray.length() > 0) {
                        for (int i = 0; i < relatedArray.length(); i++) {
                            JSONObject temp = relatedArray.getJSONObject(i);
                            if (temp != null) {
                                ResourceModel resourceModel = getResourceModel(temp);
                                tempList.add(resourceModel);
                            }
                        }
                        resourceDetailModel.relatedList = tempList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            throw e;
        }
        return resourceDetailModel;
    }

    /**
     * 通用解析列表数据JSON
     */
    private ResourceModel getResourceModel(JSONObject jsonObject) throws JSONException {
        ResourceModel resourceModel = new ResourceModel();
        if (jsonObject.has(JsonConst.UPDIS_JSON_CONTENT_ID)) {
            resourceModel.contentId = jsonObject.getString(JsonConst.UPDIS_JSON_CONTENT_ID);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_TITLE)) {
            resourceModel.title = jsonObject.getString(JsonConst.UPDIS_JSON_TITLE);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_SUBTITLE)) {
            resourceModel.subTitle = jsonObject.getString(JsonConst.UPDIS_JSON_SUBTITLE);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_AUTHOR)) {
            resourceModel.author = jsonObject.getString(JsonConst.UPDIS_JSON_AUTHOR);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_DATETIME)) {
            resourceModel.dateTime = jsonObject.getString(JsonConst.UPDIS_JSON_DATETIME);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_ICONURL)) {
            resourceModel.iconUrl = jsonObject.getString(JsonConst.UPDIS_JSON_ICONURL);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_READCOUNT)) {
            resourceModel.readCount = jsonObject.getString(JsonConst.UPDIS_JSON_READCOUNT);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_COMMENTS)) {
            resourceModel.comments = jsonObject.getString(JsonConst.UPDIS_JSON_COMMENTS);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_MESSAGELISTMETA)) {
            resourceModel.messageListMeta = jsonObject.getString(JsonConst.UPDIS_JSON_MESSAGELISTMETA);
        }
        return resourceModel;
    }

    public ArrayList<PersonModel> getPersonList(String json, PageFetcher pageFetch, String flag) throws JSONException {
        if (json == null || json.trim().equals("")) {
            return null;
        }
        ArrayList<PersonModel> arrayList = new ArrayList<PersonModel>();
        try {

            if (flag.equals("1")) {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray(JsonConst.DATA);

                /** 开始循环解析 **/
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                    PersonModel personModel;
                    if (jsonObj.has(JsonConst.TOTAL_PAGE)) {
                        int total = Integer.valueOf(jsonObj.getString(JsonConst.TOTAL_PAGE));
                        if (pageFetch != null) {
                            pageFetch.fetchPageInfo(total);
                        }
                    }
                    personModel = getPersonModel(jsonObject);

                    if (personModel != null)
                        arrayList.add(personModel);
                }
            } else {
                JSONObject jsonObj = new JSONObject(json);
                JSONObject dataObj = jsonObj.getJSONObject(JsonConst.DATA);
                PersonModel personModel = getPersonModel(dataObj);
                if (personModel != null)
                    arrayList.add(personModel);
            }
        } catch (JSONException e) {
            throw e;
        }
        return arrayList.isEmpty() ? null : arrayList;
    }

    /**
     * 解析人员JSON
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private PersonModel getPersonModel(JSONObject jsonObject) throws JSONException {
        PersonModel personModel = new PersonModel();
        if (jsonObject.has(JsonConst.UPDIS_JSON_USERID)) {
            personModel.userid = jsonObject.getString(JsonConst.UPDIS_JSON_USERID);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_NAME)) {
            personModel.name = jsonObject.getString(JsonConst.UPDIS_JSON_NAME);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_DEPT)) {
            personModel.dept = jsonObject.getString(JsonConst.UPDIS_JSON_DEPT);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_BIRTHDAY)) {
            personModel.birthday = jsonObject.getString(JsonConst.UPDIS_JSON_BIRTHDAY);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_GENDER)) {
            personModel.gender = jsonObject.getString(JsonConst.UPDIS_JSON_GENDER);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_SPECIALTY)) {
            personModel.specialty = jsonObject.getString(JsonConst.UPDIS_JSON_SPECIALTY);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_EDUCATIONAL)) {
            personModel.educational = jsonObject.getString(JsonConst.UPDIS_JSON_EDUCATIONAL);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_DEGREE)) {
            personModel.degree = jsonObject.getString(JsonConst.UPDIS_JSON_DEGREE);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_GRADUATIONDATE)) {
            personModel.graduationdate = jsonObject.getString(JsonConst.UPDIS_JSON_GRADUATIONDATE);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_ENTRYDATE)) {
            personModel.entrydate = jsonObject.getString(JsonConst.UPDIS_JSON_ENTRYDATE);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_RANK)) {
            personModel.rank = jsonObject.getString(JsonConst.UPDIS_JSON_RANK);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_TITLES)) {
            personModel.titles = jsonObject.getString(JsonConst.UPDIS_JSON_TITLES);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_ICONURL)) {
            personModel.iconurl = jsonObject.getString(JsonConst.UPDIS_JSON_ICONURL);
        }
        if (jsonObject.has(JsonConst.UPDIS_JSON_RESUME))
            personModel.resume = jsonObject.getString(JsonConst.UPDIS_JSON_RESUME);

        if (jsonObject.has(JsonConst.UPDIS_JSON_MOBILEPHONE))
            personModel.mobilePhone = jsonObject.getString(JsonConst.UPDIS_JSON_MOBILEPHONE);
        if (jsonObject.has(JsonConst.UPDIS_JSON_OFFICEPHONE))
            personModel.officePhone = jsonObject.getString(JsonConst.UPDIS_JSON_OFFICEPHONE);
        if (jsonObject.has(JsonConst.UPDIS_JSON_HOMENUM))
            personModel.homeNum = jsonObject.getString(JsonConst.UPDIS_JSON_HOMENUM);
        if (jsonObject.has(JsonConst.UPDIS_JSON_MAIL))
            personModel.mail = jsonObject.getString(JsonConst.UPDIS_JSON_MAIL);

        return personModel;
    }

    public ArrayList<CommentModel> getCommentList(String result, PageFetcher pageFetch) throws JSONException {
        if (result == null || result.trim().equals("")) {
            return null;
        }
        ArrayList<CommentModel> arrayList = new ArrayList<CommentModel>();
        try {

            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArray = jsonObj.getJSONArray("comments");

            /** 开始循环解析 **/
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                CommentModel commentModel = new CommentModel();
                if (jsonObj.has(JsonConst.TOTAL_PAGE)) {
                    int total = Integer.valueOf(jsonObj.getString(JsonConst.TOTAL_PAGE));
                    if (pageFetch != null) {
                        pageFetch.fetchPageInfo(total);
                    }
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_COMMENT_COMMENTID)) {
                    commentModel.commentId = jsonObject.getString(JsonConst.UPDIS_JSON_COMMENT_COMMENTID);
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_AUTHOR)) {
                    commentModel.author = jsonObject.getString(JsonConst.UPDIS_JSON_AUTHOR);
                    if (UIUtilities.isNull(commentModel.author)) {
                        commentModel.author = "匿名用户";
                    }
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_DATETIME)) {
                    commentModel.datetime = jsonObject.getString(JsonConst.UPDIS_JSON_DATETIME);
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_ICONURL)) {
                    commentModel.iconUrl = jsonObject.getString(JsonConst.UPDIS_JSON_ICONURL);
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_CONTENT)) {
                    commentModel.content = jsonObject.getString(JsonConst.UPDIS_JSON_CONTENT);
                }
                if (jsonObject.has(JsonConst.UPDIS_JSON_COMMENT_ISANONYMOUS)) {
                    commentModel.isAnonymous = jsonObject.getString(JsonConst.UPDIS_JSON_COMMENT_ISANONYMOUS);
                }
                arrayList.add(commentModel);
            }
        } catch (JSONException e) {
            throw e;
        }
        return arrayList.isEmpty() ? null : arrayList;
    }

    public ArrayList<ProjectModel> getProjectList(String result, PageFetcher pageFetch) {
        if (result == null || result.trim().equals("")) {
            return null;
        }
        ArrayList<ProjectModel> arrayList = new ArrayList<ProjectModel>();
        try {

            JSONObject jsonObj = new JSONObject(result);
//            JSONObject jsonObj = new JSONObject("{\"data\":[{\"projectId\":3489,\"projectNumber\":\"2345\"," +
//                    "\"projectName\":\"其实地方规划局考虑\",\"partyAName\":\"shen深圳市龙岗政府采购中心\",\"designDepartment\":\"院部\",\"projectLeaders\":[\"贝思琪\",\"丁年\",\"丁淑芳\"],\"projectScale\":\"sss\"},{\"projectId\":3488,\"projectNumber\":\"\",\"projectName\":\"是地方\",\"partyAName\":\"\",\"designDepartment\":\"\",\"projectLeaders\":[],\"projectScale\":\"\"}],\"success\":1}");
            if (jsonObj.has("success")
                    && jsonObj.getString("success").equals("1")
                    && jsonObj.has("data")) {
                JSONArray ja = jsonObj.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject pjo = ja.getJSONObject(i);
                    //{"projectId":3489,"projectNumber":"2345","projectName":"其实地方规划局考虑",
                    // "partyAName":"shen深圳市龙岗政府采购中心","designDepartment":"院部",
                    // "projectLeaders":["贝思琪","丁年","丁淑芳"],"projectScale":"sss"}
                    ProjectModel pm = new ProjectModel();
                    pm.setProjectId(getStringValue(pjo, "projectId"));
                    pm.setProjectNumber(getStringValue(pjo, "projectNumber"));
                    pm.setProjectName(getStringValue(pjo, "projectName"));
                    pm.setPartyAName(getStringValue(pjo, "partyAName"));
//                    String partyNames[] = pm.getPartyAName().split(",");
//                    for (int j = 0; j < partyNames.length; j++) {
//
//                    }
                    pm.setDesignDepartment(getStringValue(pjo, "designDepartment"));
                    JSONArray la = pjo.getJSONArray("projectLeaders");
                    if (la != null && la.length() > 0) {
                        String leaderStr = "";
                        for (int j = 0; j < la.length(); j++) {
                            String leaderOne = (String) la.getString(j);
                            leaderStr = leaderStr + leaderOne + (j == la.length() - 1 ? "" : ",");
                        }
                        pm.setProjectLeaders(leaderStr);
                    }
//                    pm.setProjectLeaders(getStringValue(pjo, "projectLeaders"));
                    pm.setProjectScale(getStringValue(pjo, "projectScale"));
                    arrayList.add(pm);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList.isEmpty() ? null : arrayList;
    }

    protected static String getStringValue(JSONObject obj, String key) throws JSONException {
        if (obj.has(key)) {
            return obj.getString(key);
        } else {
            Log.d("sorry, the key [" + key + "] is nnnnullllll");
            return "";
        }
    }
}
