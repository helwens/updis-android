
package com.tianv.updis.model;

import java.util.ArrayList;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: ResourceDetail.java
 * @Description: TODO
 * @Date 2013-4-13 下午3:22:23
 */
public class ResourceDetailModel {
    /**
     * 标题
     */
    public String title;

    /**
     * 发布日期
     */
    public String date;

    /**
     * 发布部门
     */
    public String dept;

    /**
     * 发布人
     */
    public String author;

    /**
     * 浏览量
     */
    public String readCount;

    /**
     * 内容
     */
    public String content;

    /**
     * 自定义内容
     */
    public String messageDetailMeta;
    /**
     * 相关阅读列表
     */
    public ArrayList<ResourceModel> relatedList;

    /**
     * 评论列表
     */
    public ArrayList<CommentModel> commentList;
}
