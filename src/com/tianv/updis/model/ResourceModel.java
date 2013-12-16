
package com.tianv.updis.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: ResourceModel.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:53:57
 */
public class ResourceModel implements Parcelable, Serializable {

    private static final long serialVersionUID = -2779607547440083175L;

    /**
     * 内容ID
     */
    public String contentId;

    /**
     * 主标题
     */
    public String title;

    /**
     * 副标题
     */
    public String subTitle;

    /**
     * 作者
     */
    public String author;

    /**
     * 日期
     */
    public String dateTime;

    /**
     * 小图标绝对路径
     */
    public String iconUrl;

    public String messageListMeta;

    /**
     * 评论条数
     */
    public String readCount;

    public String comments;

    public ResourceModel() {

    }

    private ResourceModel(Parcel in) {
        contentId = in.readString();
        title = in.readString();
        subTitle = in.readString();
        author = in.readString();
        dateTime = in.readString();
        iconUrl = in.readString();
        readCount = in.readString();
        comments = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        // TODO Auto-generated method stub
        parcel.writeString(contentId);
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(author);
        parcel.writeString(dateTime);
        parcel.writeString(iconUrl);
        parcel.writeString(readCount);
        parcel.writeString(comments);

    }

    public static final Creator<ResourceModel> CREATOR = new Creator<ResourceModel>() {
        public ResourceModel createFromParcel(Parcel in) {
            return new ResourceModel(in);
        }

        public ResourceModel[] newArray(int size) {
            return new ResourceModel[size];
        }
    };
}
