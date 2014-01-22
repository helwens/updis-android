
package com.tianv.updis.network;

import android.app.Activity;
import android.content.Context;
import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.model.*;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.deviceinfo.DeviceInfo;
import com.uucun.android.utils.newstring.StringUtils;
import com.uucun.android.uunetwork.UUNetWorkServer;
import com.uucun.android.uunetwork.exception.ConnectionException;
import com.uucun.android.uunetwork.httptools.RequestParams;
import com.uucun.android.uunetwork.model.ConnectionType;
import com.uucun.android.uunetwork.model.RequestType;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CollectResource.java
 * @Description: TODO
 * @Date 2013-3-24 下午3:01:02
 */
public class CollectResource {
    static final String LOG_TAG = "CollectResource";

    protected Context mContext;

    public static CollectResource instance = null;

    public JsonDataParser jsonDataParser;

    private SharedStore sharedStore = null;


    /**
     * 取得实例
     *
     * @param context
     * @return
     * @Title: getInstance
     */
    public static CollectResource getInstance(Context context) {
        if (instance == null) {
            instance = new CollectResource(context);
        }
        return instance;
    }

    private CollectResource(Context context) {
        mContext = context;
        jsonDataParser = new JsonDataParser(context);
        sharedStore = new SharedStore(mContext, null);
    }

    /**
     * 从服务端抓取数据,返回泛型
     *
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public Object fetchDataFromServer(Map<String, String> paramsMap) throws AppException {

        String result = null;
        String alias = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS);
        String url = Constant.MAIN_DOMAIN + alias;
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口

            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
        } catch (ConnectionException e) {
            //read from cache
            return null;
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public ArrayList<CommentModel> fetchComment(PageFetcher pageFetch, Map<String, String> paramsMap) throws AppException {

        String result = null;
        ArrayList<CommentModel> resourceList = null;
        String url = null;
        String fileName = null;
        url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_COMMENT;

        String contentId = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_MESSAGEID);
        String currentPage = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX);
        fileName = StringUtils.generateFileName(url) + contentId + currentPage + "_cache.json";
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口

            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();

            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_MESSAGEID, contentId);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, currentPage);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_UUID, uuid);
            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
            resourceList = jsonDataParser.getCommentList(result, pageFetch);
        } catch (ConnectionException e) {
            //read from cache
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
            }
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());

        }
        return resourceList;
    }

    /**
     * 获取projectlist
     */

    public ArrayList<CommentModel> fetchProjectList(Map<String, String> paramsMap) throws AppException {

        String result = null;
        ArrayList<CommentModel> resourceList = null;
        String url = null;
        String fileName = null;
        url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_PROJECTLIST;

        String projectListValue = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_PROJECTLIST);


        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口

            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();

            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_PROJECTLIST, projectListValue);

            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
            resourceList = jsonDataParser.getProjectList(result, pageFetch);
        } catch (ConnectionException e) {
            //read from cache
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
            }
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());

        }
        return resourceList;
    }

    /**
     * 提交评论
     *
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public String postComment(Map<String, String> paramsMap) throws AppException {
        String result = null;
        String url = Constant.MAIN_DOMAIN + Constant.INTERFACE_POST_COMMENT;

        String contentId = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CONTENTID);
        String isAnonymous = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_ISANONYMOUS);
        String comment = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_COMMENT);
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口
            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();
            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CONTENTID, contentId);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_ISANONYMOUS, isAnonymous);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_COMMENT, comment);
            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            return result;
        } catch (ConnectionException e) {
            //read from cache
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());

        }
    }

    /**
     * 发送消息
     *
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public String postMessage(Map<String, String> paramsMap) throws AppException {
        String result = null;
        String url = Constant.MAIN_DOMAIN + Constant.INTERFACE_POST_MESSAGE;
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口
            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();
            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            for (String key : paramsMap.keySet()) {
                String value = paramsMap.get(key);
                requestParams.put(key, value);
            }

            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            return result;
        } catch (ConnectionException e) {
            //read from cache
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());

        }
    }

    public String updateApp(Map<String, String> paramsMap) throws AppException {
        String result = null;
        String url = Constant.MAIN_DOMAIN + Constant.INTERFACE_CHECKVERSION;
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口
            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();
            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            for (String key : paramsMap.keySet()) {
                String value = paramsMap.get(key);
                requestParams.put(key, value);
            }

            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            return result;
        } catch (ConnectionException e) {
            //read from cache
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());

        }
    }


    public static interface PageFetcher {
        void fetchPageInfo(int totalSize);
    }

    public static interface ResultFlagFetcher {
        void fetchResultFlag(String resultFlag);
    }


    /**
     * 分页抓取数据
     *
     * @param pageFetch
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public ArrayList<ResourceModel> fetchResource(PageFetcher pageFetch,
                                                  Map<String, String> paramsMap) throws AppException {
        String result = null;
        ArrayList<ResourceModel> resourceList = null;
        String url = null;
        String fileName = null;
        String alias = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS);
        if (alias.equals(Constant.VIEW_PERSONNEL_QUERY_LIST)) {
            url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_DATA_LIST;
        } else {
            url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_DATA_LIST;
        }
        String categoryType = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE);
        String currentPage = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX);
        fileName = StringUtils.generateFileName(url) + categoryType + currentPage + "_cache.json";
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口

            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();

            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CATEGORY_TYPE, categoryType);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, currentPage);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_UUID, uuid);
            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
            //cache to file
            FileOutputStream outputStream = mContext.openFileOutput(fileName,
                    Activity.MODE_PRIVATE);
            outputStream.write(result.getBytes());
            outputStream.flush();
            outputStream.close();
            resourceList = jsonDataParser.getResourceList(result, pageFetch);
        } catch (ConnectionException e) {
            //read from cache
            try {
                resourceList = readFromCache(mContext, fileName, pageFetch);
            } catch (JSONException e1) {
                throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
            } catch (IOException e1) {
                throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
            }
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                try {
                    resourceList = readFromCache(mContext, fileName, pageFetch);
                } catch (JSONException e1) {
                    throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
                } catch (IOException e1) {
                    throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
                }
            }
        } catch (Exception e) {
            try {
                resourceList = readFromCache(mContext, fileName, pageFetch);
            } catch (JSONException e1) {
                throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
            } catch (IOException e1) {
                throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
            }
        }
        return resourceList;
    }

    public ArrayList<ResourceModel> readFromCache(Context context, String fileName, PageFetcher pageFetch) throws JSONException, IOException {

        Logger.i("readFromCache", fileName);

        FileInputStream inputStream = context.openFileInput(fileName);
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        while (inputStream.read(bytes) != -1) {
            arrayOutputStream.write(bytes, 0, bytes.length);
        }
        inputStream.close();
        arrayOutputStream.close();
        String content = new String(arrayOutputStream.toByteArray());
        return jsonDataParser.getResourceList(content, pageFetch);

    }

    /**
     * 登录
     *
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public LoginDataModel fetchLoginData(Map<String, String> paramsMap) throws AppException {
        // TODO Auto-generated method stub
        String result = null;
        LoginDataModel loginDataModel = null;
        String url = null;
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();
            String alias = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_URL_ALIAS);

            if (alias.equals(Constant.UrlAlias.LOGIN_USER_ALIAS)) {
                url = Constant.MAIN_DOMAIN + Constant.INTERFACE_USER_LOGIN;
                String username = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_USERNAME);
                String pwd = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_USERPWD);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_USERNAME, username);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_USERPWD, pwd);
            } else if (alias.equals(Constant.UrlAlias.LOGIN_USER_LOGOUT_ALIAS)) {
                // 退出
                url = Constant.MAIN_DOMAIN + Constant.INTERFACE_USER_LOGIN;

            } else {
                String firstCookie = sharedStore.getString("login_cookies", "");
                Constant.COOKIES = firstCookie;
                uunetWorkServer.addHeader("Cookie", firstCookie);
                url = Constant.MAIN_DOMAIN + Constant.INTERFACE_USER_REGPHONE;
                String phoneNum = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_PHONENUM);
                String verCode = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_VERCODE);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_PHONENUM, phoneNum);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_VERCODE, verCode);
            }

            String uuid = DeviceInfo.getMacAddress(mContext);
            if (uuid == null || uuid.equals("")) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_MAC, uuid);
            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
            loginDataModel = jsonDataParser.getLoginData(result);
        } catch (ConnectionException e) {
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
            }
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
        }
        return loginDataModel;
    }

    /**
     * 抓取数据字典数据
     */
    public DictionaryModel fetchDictData() throws AppException {
        // TODO Auto-generated method stub
        String result = null;
        String url = null;
        try {
            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);

            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_DICTIONARY;
            String[] results = uunetWorkServer.startSynchronous(url);
            if (results != null) {
                Logger.d("UrlconPostStreamsynTest code ", results[0]);
                Logger.d("UrlconPostStreamsynTest content ", results[1]);
                result = results[1];
            }
            if (result == null) {
                return null;
            }
            jsonDataParser.getDictData(result);
            sharedStore.putLong(Constant.UU_APP_UPDATE_TIME_KEY,
                    System.currentTimeMillis());

        } catch (ConnectionException e) {
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
            }
            // throw new AppException(AppException.PARSE_DATA_ERROR_CODE,
            // e.getMessage());
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
        }
        return null;
    }

    /**
     * 人员查询
     *
     * @param paramsMap
     * @return
     * @throws AppException
     */
    public ArrayList<PersonModel> fetchPersonData(PageFetcher pageFetch, Map<String, String> paramsMap) throws AppException {
        // TODO Auto-generated method stub
        String result = null;
        ArrayList<PersonModel> personList = null;
        String url = null;
        try {
            if (paramsMap == null) {
                return null;
            }

            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            // 列表数据接口
            url = Constant.MAIN_DOMAIN + Constant.INTERFACE_USER_QUERY;

            String firstCookie = sharedStore.getString("login_cookies", "");
            Constant.COOKIES = firstCookie;
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setTimeout(40000);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();

            String flag = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_FLAG);
            String currentPage = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CUREENT_PAGE_INDEX, currentPage);
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_FLAG, flag);


            if (paramsMap.containsKey(Constant.UrlAlias.PARAMS_KEY_USERNAME)) {
                String userName = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_USERNAME);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_USERNAME, userName);
            }

            if (paramsMap.containsKey(Constant.UrlAlias.PARAMS_KEY_DEPT)) {
                String deptName = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_DEPT);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_DEPT, deptName);
            }

            if (paramsMap.containsKey(Constant.UrlAlias.PARAMS_KEY_SUBJECT)) {
                String subjectName = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_SUBJECT);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_SUBJECT, subjectName);
            }

            if (paramsMap.containsKey(Constant.UrlAlias.PARAMS_KEY_USEID)) {
                String userId = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_USEID);
                requestParams.put(Constant.UrlAlias.PARAMS_KEY_USEID, userId);
            }

            uunetWorkServer.setRequestParams(requestParams);
            String[] results = uunetWorkServer.startSynchronous(url);
            if (results != null) {
                Logger.d("UrlconPostStreamsynTest code ", results[0]);
                Logger.d("UrlconPostStreamsynTest content ", results[1]);
                result = results[1];
            }
            if (result == null) {
                return null;
            }
            // 05-18 14:40:08.979: I/HttpClientNetwork:connect(13395): result:
            // {"sessionTimeout":1}

            personList = jsonDataParser.getPersonList(result, pageFetch, flag);
        } catch (ConnectionException e) {
            throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
            }
        } catch (Exception e) {
            throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
        }
        return personList;
    }

    public ResourceDetailModel fetchResourceDetail(Map<String, String> paramsMap)
            throws AppException {
        // TODO Auto-generated method stub
        String result = null;
        ResourceDetailModel resourceDetailModel = null;
        String url = Constant.MAIN_DOMAIN + Constant.INTERFACE_FETCH_DETAIL;
        String contentId = paramsMap.get(Constant.UrlAlias.PARAMS_KEY_CONTENTID);
        String fileName = StringUtils.generateFileName(url) + contentId + "_cache.json";
        try {
            if (paramsMap == null) {
                return null;
            }
            UUNetWorkServer uunetWorkServer = new UUNetWorkServer(mContext, ConnectionType.URLCON);
            String firstCookie = sharedStore.getString("login_cookies", "");
            uunetWorkServer.addHeader("Cookie", firstCookie);
            uunetWorkServer.setRequestType(RequestType.GET);
            RequestParams requestParams = new RequestParams();
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_CONTENTID, contentId);

            String uuid = DeviceInfo.getMacAddress(mContext);
            if (UIUtilities.isNull(uuid)) {
                uuid = "000000";
            }
            requestParams.put(Constant.UrlAlias.PARAMS_KEY_UUID, uuid);
            uunetWorkServer.setRequestParams(requestParams);
            String[] bsting = uunetWorkServer.startSynchronous(url);
            if (bsting != null) {
                Logger.d("UrlconPostStreamsynTest code ", bsting[0]);
                Logger.d("UrlconPostStreamsynTest content ", bsting[1]);
                result = bsting[1];
            }
            if (result == null) {
                return null;
            }
            //cache to file
            FileOutputStream outputStream = mContext.openFileOutput(fileName,
                    Activity.MODE_PRIVATE);
            outputStream.write(result.getBytes());
            outputStream.flush();
            outputStream.close();
            resourceDetailModel = jsonDataParser.getResourceDetail(result);
        } catch (ConnectionException e) {
            try {
                resourceDetailModel = readDetailFromCache(mContext, fileName);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (resourceDetailModel == null) {
                throw new AppException(AppException.CONNECTION_CMS_ERROR_CODE, e.getMessage());
            }

        } catch (JSONException e) {
            if (result.contains("sessionTimeout")) {
                throw new AppException(AppException.LOGIN_TIME_OUT, e.getMessage());
            } else {
                try {
                    resourceDetailModel = readDetailFromCache(mContext, fileName);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (resourceDetailModel == null) {
                    throw new AppException(AppException.PARSE_DATA_ERROR_CODE, e.getMessage());
                }
            }
        } catch (Exception e) {
            try {
                resourceDetailModel = readDetailFromCache(mContext, fileName);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (resourceDetailModel == null) {
                throw new AppException(AppException.UN_KNOW_ERROR_CODE, e.getMessage());
            }
        }
        return resourceDetailModel;
    }

    public ResourceDetailModel readDetailFromCache(Context context, String fileName) throws JSONException {
        try {
            Logger.i("readFromCache", fileName);

            FileInputStream inputStream = context.openFileInput(fileName);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());
            return jsonDataParser.getResourceDetail(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
