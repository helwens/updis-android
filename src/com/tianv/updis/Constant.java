
package com.tianv.updis;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: Constant.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:45:25
 */
public class Constant {

    public static String COOKIES = "";
    /**
     * 一小时
     */
    public static long HOUR_TIME = 3600000;

    /**
     * sharedPreference 的 更新检测时间key
     */
    public static String UU_APP_UPDATE_TIME_KEY = "uu_app_update_time";

    /**
     * 默认7天检测一次更新
     */
    public static int DEFAULT_UPDATE_HOUR = 7 * 24;

    /**
     * ***************** 各页面ID ***********************
     */
    public static final String VIEW_HOME_NOTICE = "1";

    public static final String VIEW_HOME_BIDDING = "2";

    public static final String VIEW_HOME_TALK = "3";

    public static final String VIEW_HOME_AMATEUR = "4";

    public static final String VIEW_PROJECT = "5";

    public static final String VIEW_PERSONNEL_QUERY = "6";

    public static final String VIEW_PERSONNEL_ADDRESS_BOOK = "7";

    public static final String VIEW_PERSONNEL_QUERY_LIST = "8";

    public static final String VIEW_CHANGE_PWD = "10";

    public static final String VIEW_USER_INFO = "11";

    public static final String VIEW_USER_SEND_LIST = "12";

    public static final String VIEW_USER_SEND_MSG = "13";

    public static final String VIEW_COMMENT_LIST = "14";

    /**
     * ***************** 错误代码 ***********************
     */
    public static final int UPDIS_NO_NETWORK_ERROR = 1008;

    public static final int UPDIS_DEFAULT_DIALOG_ID = 888888;

    /**
     * ***************** intent传递参数 ***********************
     */
    public static final String UPDIS_INTENT_KEY_CONTENTID = "contentId";

    public static final String UPDIS_INTENT_KEY_CATEGORYTYPE = "categoryType";
    public static final String UPDIS_INTENT_KEY_FILENAME = "updis_intent_key_filename";

    public class UrlAlias {

        /**
         * 数据抓取通用参数标识
         */
        public final static String PARAMS_KEY_URL_ALIAS = "ParamsKeyUrlAlias";

        /**************** 列表消息区分 **************************/
        /**
         * 分类消息-通知
         */
        public final static String CATEGORY_NOTICE_ALIAS = "CategoryNoticeResources";

        public final static String CATEGORY_BIDDING_ALIAS = "CategoryBiddingResources";

        public final static String CATEGORY_TALK_ALIAS = "CategoryTalkResources";

        public final static String CATEGORY_AMATEUR_ALIAS = "CategoryAmateurResources";

        /**************** 登录验证区分 **************************/
        /**
         * 用户登录
         */
        public final static String LOGIN_USER_ALIAS = "UserAlias";

        public final static String LOGIN_USER_LOGOUT_ALIAS = "UserLogout";

        /**
         * 注册手机号码
         */
        public final static String LOGIN_PHONENUM_ALIAS = "PhoneNumAlias";

        /**
         * ************* POST参数 *************************
         */

        public final static String PARAMS_KEY_CUREENT_PAGE_INDEX = "currentPage";

        public final static String PARAMS_KEY_CATEGORY_TYPE = "categoryType";
        public final static String PARAMS_KEY_TITLE = "title";
        public final static String PARAMS_KEY_CONTENT = "content";
        public final static String PARAMS_KEY_PUBLISHDEPT = "publishDept";

        public final static String PARAMS_KEY_SMSCONTENT = "SMScontent";
        public final static String PARAMS_KEY_UUID = "uuid";

        public final static String PARAMS_KEY_MAC = "mac";

        /**
         * 用户登录,手机注册
         */
        public static final String PARAMS_KEY_USERNAME = "userName";

        public static final String PARAMS_KEY_USEID = "userId";

        public static final String PARAMS_KEY_USERPWD = "pwd";

        public static final String PARAMS_KEY_PHONENUM = "phonenum";

        public static final String PARAMS_KEY_VERCODE = "verificationCode";

        public static final String PARAMS_KEY_FLAG = "flag";

        /**
         * 部门
         */
        public static final String PARAMS_KEY_DEPT = "deptName";

        /**
         * 专业
         */
        public static final String PARAMS_KEY_SUBJECT = "specialtyName";

        /**
         * 姓名
         */
        public static final String PARAMS_KEY_NAME = "userName";

        /**
         * 详情ID
         */
        public static final String PARAMS_KEY_CONTENTID = "contentId";

        public static final String PARAMS_KEY_MESSAGEID = "messageId";

        public static final String PARAMS_KEY_ISANONYMOUS = "isAnonymous";

        public static final String PARAMS_KEY_COMMENT = "comment";

        /**************** POST参数 修改密码 **************************/
        /**
         * 当前密码
         */
        public static final String PARAMS_KEY_NOWPWD = "nowpwd";

        /**
         * 新密码
         */
        public static final String PARAMS_KEY_NEWPWD = "newpwd";

    }

    public static final String CACHE_DIR_KEY = "cache_dir_key";

    /**
     * 缓存登录信息
     */
    public static final String UPDIS_STORE_KEY_LOGINFLAG = "updis_store_key_loginflag";

    public static final String UPDIS_STORE_KEY_USERNAME = "updis_store_key_username";

    public static final String UPDIS_STORE_KEY_USERPWD = "updis_store_key_userpwd";

    public static final String UPDIS_STORE_KEY_ISSPECIALUSER = "updis_store_key_isspecialuser";

    /**
     * PUSH通知信息
     */
    public static final String UPDIS_STORE_KEY_PUSH_NOTICE = "updis_store_key_push_notice";

    /**
     * PUSH招投标信息
     */
    public static final String UPDIS_STORE_KEY_PUSH_BIDDING = "updis_store_key_push_bidding";

    /**
     * PUSH畅所欲言信息
     */
    public static final String UPDIS_STORE_KEY_PUSH_TALK = "updis_store_key_push_talk";

    /**
     * PUSH业余生活信息
     */
    public static final String UPDIS_STORE_KEY_PUSH_AMATEUR = "updis_store_key_push_amateur";


    public static final String UPDIS_STORE_KEY_PUSH_PROJECT = "updis_store_key_push_project";

    /**
     * push时间段设置
     */
    public static final String UPDIS_STORE_KEY_PUSH_TIME = "updis_store_key_push_time";

    /**
     * 打开PUSH
     */
    public static final String UPDIS_PUSH_OPEN = "1";

    /**
     * 关闭PUSH
     */
    public static final String UPDIS_PUSH_CLOSE = "0";

    /**
     * 只在夜间接收PUSH
     */
    public static final String UPDIS_PUSH_NIGHT = "2";

    /**
     * 数据字典表-部门key
     */
    public static final String UPDIS_DIC_KEY_DEPT = "dept_key";

    /**
     * 数据字典表-专业key
     */
    public static final String UPDIS_DIC_KEY_SUBJECT = "subject_key";

    //debug 8081 8010
    public static final String MAIN_DOMAIN = "http://phone.updis.cn:8010/rest";

    /**
     * 列表数据接口
     */
    public static final String INTERFACE_FETCH_DATA_LIST = "/messages/fetchListData";

    /**
     * 数据字典接口
     */
    public static final String INTERFACE_FETCH_DICTIONARY = "/users/fetchDictData";

    /**
     * 用户登录接口
     */
    public static final String INTERFACE_USER_LOGIN = "/users/login";

    /**
     * 用户登出接口
     */
    public static final String INTERFACE_USER_LOGOUT = "/users/logout";

    /**
     * 人事查询接口
     */
    public static final String INTERFACE_USER_QUERY = "/users/queryPerson";

    /**
     * 详情查询接口
     */
    public static final String INTERFACE_FETCH_DETAIL = "/messages/fetchDetail";

    /**
     * 获取评论列表
     */
    public static final String INTERFACE_FETCH_COMMENT = "/messages/fetchComment";

    /**
     * 提交评论
     */
    public static final String INTERFACE_POST_COMMENT = "/messages/postComment";

    /**
     * 手机号码验证接口
     */
    public static final String INTERFACE_USER_REGPHONE = "/users/deviceVerify";

    /**
     * 消息发送接口
     */
    public static final String INTERFACE_POST_MESSAGE = "/messages/postMessage";

    /**
     * 检查更新
     */
    public static final String INTERFACE_CHECKVERSION = "/settings/checkVersion";

    /**
     * 关于接口
     */
    public static final String INTERFACE_ABOUT = "/settings/about";


    /**
     * Preferences Key : load image or not
     */
    public static final String KEY_NO_IMAGE = "no_load_image_key";
}
