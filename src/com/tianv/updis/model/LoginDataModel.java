
package com.tianv.updis.model;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: LoginDataModel.java
 * @Description: TODO
 * @Date 2013-4-9 下午1:59:29
 */
public class LoginDataModel {
    public String success;

    public String msg;

    public String userid;

    public String registered;

    public String phoneNum;

    //登录成功后,这个字段标示是否可以查看 categoryType 为 5 的消息.
//    0 不可以
//    1 可以.

    public String isSpecailUser;
}
