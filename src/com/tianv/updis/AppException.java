/**
 * @Title: AppException.java
 * @Package com.uucun.android.cms
 * @author Wang Baoxi 
 * @date 2011-12-19 下午04:26:54
 * @version V1.0
 */

package com.tianv.updis;

/**
 *
 */
public class AppException extends Exception {
    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 2768202839927371148L;

    /**
     * 网路连接异常
     */
    public static final int CONNECTION_CMS_ERROR_CODE = 0x1;

    /**
     * 数据解析异常
     */
    public static final int PARSE_DATA_ERROR_CODE = 0x2;

    /**
     * 读取数据异常
     */
    public static final int READ_DATA_ERROR_CODE = 0x3;

    /**
     * 无网络异常
     */
    public static final int NO_NETWORK_ERROR_CODE = 0x4;

    /**
     * 未知异常
     */
    public static final int UN_KNOW_ERROR_CODE = 0x5;

    /**
     * 失效异常
     */
    public static final int INVALID_ERROR_CODE = 0x6;

    /**
     * 登录超时
     */
    public static final int LOGIN_TIME_OUT = 0x7;

    public int errorCode = 0;

    public String msg = null;

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param errorCode 错误代码
     * @param msg       消息
     */
    public AppException(int errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public AppException(int errorCode) {
        this.errorCode = errorCode;
    }
}
