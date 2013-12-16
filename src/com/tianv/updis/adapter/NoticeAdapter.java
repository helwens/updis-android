
package com.tianv.updis.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melvin.android.base.adapter.UUBaseAdapter;
import com.tianv.updis.R;
import com.tianv.updis.cache.SyncImageLoader;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: NoticeAdapter.java
 * @Description: TODO
 * @Date 2013-3-24 下午1:51:10
 */
public class NoticeAdapter extends UUBaseAdapter<ResourceModel> {

    private ListView listView = null;

    private SyncImageLoader syncImageLoader;

    /**
     * @param
     * @return
     * @Description:
     */
    public NoticeAdapter(Context context, LoadResourceListener listener, ListView listView,
                         String parentModuleCode) {
        super(context, listener, parentModuleCode);
        this.listView = listView;
        syncImageLoader = new SyncImageLoader(context);
        listView.setOnScrollListener(onScrollListener);
    }

    private static final class AppViewHolder {
        ImageView iconView;

        TextView titleView;

        @SuppressWarnings("unused")
        TextView dateView;

        @SuppressWarnings("unused")
        TextView subtitleView;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.resource_common_item, parent, false);
            AppViewHolder holder = new AppViewHolder();

            holder.iconView = (ImageView) convertView.findViewById(R.id.resource_icon);
            holder.titleView = (TextView) convertView.findViewById(R.id.txt_title);
            holder.dateView = (TextView) convertView.findViewById(R.id.txt_date);
            holder.subtitleView = (TextView) convertView.findViewById(R.id.txt_subtitle);

            convertView.setTag(holder);
        }

        return convertView;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent) {
        final ResourceModel resResult = getItem(position);
        AppViewHolder holder = (AppViewHolder) convertView.getTag();
        holder.titleView.setText(resResult.title);

        holder.iconView.setImageResource(R.drawable.uu_icon);
        holder.iconView.setTag(position);
        Bitmap bm = syncImageLoader.getBitmapFromMemory(resResult.iconUrl);
        if (bm == null || bm.isRecycled()) {
            syncImageLoader.loadImage(position, resResult.iconUrl, imageLoadListener, parent,
                    mContext);
        } else {
            holder.iconView.setImageBitmap(bm);
        }
    }

    @Override
    public void bindEvent(final int position, final View convertView, final ViewGroup parent) {
        final AppViewHolder holder = (AppViewHolder) convertView.getTag();
        holder.iconView.setOnClickListener(null);
        int count = getCount();
        UIUtilities.setLoadResource(position, count, listener);
    }

    SyncImageLoader.OnImageLoadListener imageLoadListener = new SyncImageLoader.OnImageLoadListener() {

        @Override
        public void onImageLoad(Integer t, Bitmap bm, View parent) {
            ImageView img = (ImageView) parent.findViewWithTag(t);
            if (img != null) {
                if (bm == null || bm.isRecycled()) {
                    img.setImageResource(R.drawable.uu_icon);
                } else {
                    img.setImageBitmap(bm);
                }
            }
        }

        @Override
        public void onError(Integer t, View parent) {
            ImageView img = (ImageView) parent.findViewWithTag(t);
            if (img != null) {
                img.setImageResource(R.drawable.uu_icon);
            }
        }
    };

    public void loadImage() {
        int start = listView.getFirstVisiblePosition();
        int end = listView.getLastVisiblePosition();
        if (end >= getCount()) {
            end = getCount() - 1;
        }
        syncImageLoader.setLoadLimit(start, end);
        syncImageLoader.unlock();
    }

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    syncImageLoader.lock();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    loadImage();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    syncImageLoader.lock();
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            // TODO Auto-generated method stub

        }
    };
}
