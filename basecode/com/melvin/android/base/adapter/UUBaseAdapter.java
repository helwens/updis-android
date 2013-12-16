
package com.melvin.android.base.adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianv.updis.cache.ImageCache;
import com.tianv.updis.listener.LoadResourceListener;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.r.RFileUtil;

/**
 * UU基础的Adapter
 *
 * @param <T>
 * @author wangbx
 */
public abstract class UUBaseAdapter<T> extends ArrayAdapter<T> {
    /**
     * 载入监听器
     */
    public LoadResourceListener listener = null;

    /**
     * 布局反射器
     */
    public LayoutInflater mLayoutInflater;

    /**
     * 图片缓存
     */
    public ImageCache imageCache = null;

    /**
     * 上下文对象
     */
    public Context mContext;

    /**
     * 缓存对象
     */
    public SharedStore sharedStore = null;

    /**
     * 包含此adapter的模块代码
     */
    public String parentModuleCode = "";

    /**
     * List View的PARENT
     */
    public ViewGroup parent = null;

    public RFileUtil fileUtil = null;

    protected TextView mTv = null;

    protected ImageView mImg = null;

    protected ProgressBar mPb = null;

    protected TextView mPv = null;

    public UUBaseAdapter(Context context, String parentModuleCode) {
        super(context, 0);
        fileUtil = RFileUtil.getInstance(context);
        this.mContext = context;
        this.parentModuleCode = parentModuleCode;
        mLayoutInflater = LayoutInflater.from(context);
        imageCache = ImageCache.getInstance(context);
        sharedStore = new SharedStore(context, null);
    }

    /**
     * release memory -- add By Melvin
     */
    public void destroyAdapter() {
        if (listener != null) {
            listener = null;
        }
        if (mLayoutInflater != null) {
            mLayoutInflater = null;
        }
        if (imageCache != null) {
            // imageCache.clearCache();
            imageCache = null;
        }
        if (mContext != null) {
            mContext = null;
        }
        if (sharedStore != null) {
            sharedStore = null;
        }

        if (parent != null) {
            parent = null;
        }

        // 强制GC
        System.gc();

    }

    /**
     * @param context
     * @param listener 监听器
     */
    public UUBaseAdapter(Context context, LoadResourceListener listener, String parentModuleCode) {
        this(context, parentModuleCode);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        convertView = bindView(position, convertView, parent);
        bindData(position, convertView, parent);
        bindEvent(position, convertView, parent);
        return convertView;
    }

    /**
     * 绑定列表的视图
     *
     * @param position
     * @param convertView
     * @param parent
     */
    public abstract View bindView(int position, View convertView, ViewGroup parent);

    /**
     * 绑定列表的数据
     *
     * @param position
     * @param convertView
     * @param parent
     */
    public abstract void bindData(int position, View convertView, ViewGroup parent);

    /**
     * 绑定列表中的事件
     *
     * @param position
     * @param convertView
     * @param parent
     */
    public abstract void bindEvent(int position, View convertView, ViewGroup parent);

    public void onStart(Message msg) {
        this.notifyDataSetChanged();
    }

    public void onProgressUpdate(Message msg, int progress) {
        if (null != parent) {
            String key = (String) msg.obj;
            String tvKey = key + "_tv";
            String iv = key + "_iv";
            String pt = key + "_pt";
            String ptt = key + "_ptt";

            mTv = (TextView) parent.findViewWithTag(tvKey);
            if (mTv != null) {
                int idColor = fileUtil.getColor("state_blue_color");
                mTv.setTextColor(mContext.getResources().getColor(idColor));
                mTv.setText(progress + "%");
            }
            mImg = (ImageView) parent.findViewWithTag(iv);
            if (mImg != null) {
                int idDrable = fileUtil.getDrawableValue("free_download_icon");
                mImg.setBackgroundDrawable(mContext.getResources().getDrawable(idDrable));
            }
            mPb = (ProgressBar) parent.findViewWithTag(pt);
            if (mPb != null)
                mPb.setProgress(progress);
            mPv = (TextView) parent.findViewWithTag(ptt);
            if (mPv != null)
                mPv.setText(progress + "%");

            if (progress == 100) {
                notifyDataSetChanged();
            }
        }
    }

    public void onSuccess(Message msg) {
        this.notifyDataSetChanged();
    }

    public void onError(Message msg) {
        this.notifyDataSetChanged();
    }

}
