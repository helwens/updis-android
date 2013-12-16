
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
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.cache.SyncImageLoader;
import com.tianv.updis.listener.LoadResourceListener;
import com.tianv.updis.model.CommentModel;
import com.tianv.updis.model.PersonModel;
import com.tianv.updis.model.ResourceModel;
import com.tianv.updis.model.UIUtilities;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: CommonListAdapter.java
 * @Description: 通用列表Adapter
 * @Date 2013-4-10 下午1:42:35
 */
public class CommonListAdapter extends UUBaseAdapter<Object> {

    private ListView listView = null;

    private SyncImageLoader syncImageLoader;

    /**
     * @param
     * @return
     * @Description:
     */
    public CommonListAdapter(Context context, LoadResourceListener listener, ListView listView,
                             String parentModuleCode) {
        super(context, listener, parentModuleCode);
        this.listView = listView;
        syncImageLoader = new SyncImageLoader(context);
        listView.setOnScrollListener(onScrollListener);
    }

    private static class AppViewHolder {
        ImageView iconView;

        TextView titleView;

        TextView dateView;

        TextView commentCountView;

        TextView subtitleView;

    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY_LIST)) {
                convertView = mLayoutInflater.inflate(R.layout.queryperson_common_item, parent, false);
            } else if (parentModuleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
                convertView = mLayoutInflater.inflate(R.layout.addressbook_common_item, parent, false);
            } else if (parentModuleCode.equals(Constant.VIEW_COMMENT_LIST)) {
                convertView = mLayoutInflater.inflate(R.layout.comment_list_item, parent, false);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.resource_common_item, parent, false);
            }
            AppViewHolder holder = new AppViewHolder();

            holder.iconView = (ImageView) convertView.findViewById(R.id.resource_icon);
            holder.titleView = (TextView) convertView.findViewById(R.id.txt_title);
            holder.dateView = (TextView) convertView.findViewById(R.id.txt_date);
            holder.dateView.setVisibility(View.GONE);
            holder.commentCountView = (TextView) convertView.findViewById(R.id.txt_commentcount);
            holder.commentCountView.setVisibility(View.GONE);
            holder.subtitleView = (TextView) convertView.findViewById(R.id.txt_subtitle);
            holder.subtitleView.setVisibility(View.GONE);

            convertView.setTag(holder);
        }

        return convertView;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent) {
        if (parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY_LIST) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
            final PersonModel personModel = (PersonModel) getItem(position);
            AppViewHolder holder = (AppViewHolder) convertView.getTag();
            holder.titleView.setText(UIUtilities.splitAndFilterString(personModel.name));
            if (parentModuleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
                if (!UIUtilities.isNull(personModel.dept)) {
                    holder.subtitleView.setVisibility(View.VISIBLE);
                    holder.subtitleView.setText(personModel.dept);
                }
            } else {
                if (!UIUtilities.isNull(personModel.dept)) {
                    holder.dateView.setVisibility(View.VISIBLE);
                    holder.dateView.setText("部门:" + personModel.dept);
                }
                if (!UIUtilities.isNull(personModel.specialty)) {
                    holder.subtitleView.setVisibility(View.VISIBLE);
                    holder.subtitleView.setText("专业:" + personModel.specialty);
                }
                holder.iconView.setImageResource(R.drawable.nopic);
                holder.iconView.setTag(position);
                Bitmap bm = syncImageLoader.getBitmapFromMemory(personModel.iconurl);
                if (bm == null || bm.isRecycled()) {
                    syncImageLoader.loadImage(position, personModel.iconurl, imageLoadListener, parent,
                            mContext);
                } else {
                    holder.iconView.setImageBitmap(bm);
                }
            }

        } else if (parentModuleCode.equals(Constant.VIEW_COMMENT_LIST)) {
            //详情页面评论列表
            final CommentModel resResult = (CommentModel) getItem(position);
            AppViewHolder holder = (AppViewHolder) convertView.getTag();
            holder.titleView.setText(String.format("%s 于%s的留言", resResult.author, resResult.datetime));
            holder.subtitleView.setVisibility(View.VISIBLE);
            holder.subtitleView.setText(UIUtilities.splitAndFilterString(resResult.content));
            holder.iconView.setImageResource(R.drawable.person_no_pic);
            holder.iconView.setTag(position);
            Bitmap bm = syncImageLoader.getBitmapFromMemory(resResult.iconUrl);
            if (bm == null || bm.isRecycled()) {
                syncImageLoader.loadImage(position, resResult.iconUrl, imageLoadListener, parent,
                        mContext);
            } else {
                holder.iconView.setImageBitmap(bm);
            }


        } else {
            final ResourceModel resResult = (ResourceModel) getItem(position);
            AppViewHolder holder = (AppViewHolder) convertView.getTag();
            holder.titleView.setText(UIUtilities.splitAndFilterString(resResult.title));
//            holder.titleView.setText(resResult.messageListMeta);

            if (!UIUtilities.isNull(resResult.messageListMeta)) {
                holder.dateView.setVisibility(View.VISIBLE);
                holder.dateView.setText(resResult.messageListMeta);
            }
            if (!UIUtilities.isNull(resResult.readCount)) {
                holder.commentCountView.setVisibility(View.VISIBLE);
                holder.commentCountView.setText(resResult.readCount);
            }
            if (!UIUtilities.isNull(resResult.subTitle)) {
                holder.subtitleView.setVisibility(View.VISIBLE);
                holder.subtitleView.setText(resResult.subTitle);
            }
            if (parentModuleCode.equals(Constant.VIEW_HOME_AMATEUR)) {
                holder.iconView.setImageResource(R.drawable.nopic);
                holder.iconView.setTag(position);
                Bitmap bm = syncImageLoader.getBitmapFromMemory(resResult.iconUrl);
                if (bm == null || bm.isRecycled()) {
                    syncImageLoader.loadImage(position, resResult.iconUrl, imageLoadListener, parent,
                            mContext);
                } else {
                    holder.iconView.setImageBitmap(bm);
                }
            } else {
                holder.iconView.setBackgroundResource(R.drawable.icoarr);
                holder.iconView.setTag(position);
            }
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
                    if (parentModuleCode.equals(Constant.VIEW_COMMENT_LIST) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY_LIST) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
                        img.setImageResource(R.drawable.person_no_pic);
                    } else {
                        img.setImageResource(R.drawable.nopic);
                    }
                } else {
                    img.setImageBitmap(bm);
                }
            }
        }

        @Override
        public void onError(Integer t, View parent) {
            ImageView img = (ImageView) parent.findViewWithTag(t);
            if (img != null) {
                if (parentModuleCode.equals(Constant.VIEW_COMMENT_LIST) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_QUERY_LIST) || parentModuleCode.equals(Constant.VIEW_PERSONNEL_ADDRESS_BOOK)) {
                    img.setImageResource(R.drawable.person_no_pic);
                } else {
                    img.setImageResource(R.drawable.nopic);
                }
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
