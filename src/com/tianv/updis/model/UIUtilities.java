
package com.tianv.updis.model;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianv.updis.AppException;
import com.tianv.updis.Constant;
import com.tianv.updis.R;
import com.tianv.updis.activity.WebContentActivity;
import com.tianv.updis.listener.LoadResourceListener;

/**
 * UI Utility class used to handle UI display.
 *
 * @author Melvin
 * @since 2012-2-3
 */
public class UIUtilities {

    @SuppressWarnings("unused")
    private UIUtilities() {
    }

    public UIUtilities(Context context) {
    }

    public static void showToast(Context context, int id) {
        showToast(context, id, false);
    }

    public static void showToast(Context context, int id, boolean longToast) {
        Toast.makeText(context, id, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void showFormattedToast(Context context, int id, Object... args) {
        Toast.makeText(context, String.format(context.getText(id).toString(), args),
                Toast.LENGTH_LONG).show();
    }

    /**
     * @param context
     * @param msg
     * @Title: showToast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param context
     * @param view
     * @Title: 列表弹出的动画效果
     */

    public static void spaceAnimation(View view, Context context) {
        Animation animation = new TranslateAnimation(150, 0.0f, 0.0f, 0.0f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        animation.setInterpolator(AnimationUtils.loadInterpolator(context,
                android.R.anim.bounce_interpolator));
        view.startAnimation(animation);
    }

    public static void showPanel(Context context, View panel, boolean slideUp) {
        panel.startAnimation(AnimationUtils.loadAnimation(context, slideUp ? R.anim.slide_in
                : R.anim.slide_out_top));
        panel.setVisibility(View.VISIBLE);
    }

    /**
     * @param context
     * @param resId
     * @Title: 自定义toast
     */
    public static void showCustomToast(Context context, int resId) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        TextView tv = (TextView) layout.findViewById(R.id.toast_tv_id);
        tv.setText(resId);
        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 80);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 详情无网络或者无数据时显示界面 *
     */
    public static void showExceptionView(int type, ImageView img, TextView to, TextView ts,
                                         TextView tt, Button tryBtn) {
        if (type == AppException.NO_NETWORK_ERROR_CODE) {
            img.setBackgroundResource(R.drawable.no_networking_icon);
            to.setText(R.string.no_networking_text);
            ts.setVisibility(View.VISIBLE);
            ts.setText(R.string.check_networking);
            tt.setText(R.string.click_try_again);
        } else {
            img.setBackgroundResource(R.drawable.data_error_icon);
            to.setText(R.string.data_error);
            ts.setVisibility(View.GONE);
            tt.setText(R.string.data_get);
        }
    }

    /**
     * 设置弹出层最后两行弹出位置
     *
     * @param context
     * @param position
     */
    public static void setListViewLocation(Context context, ListView listView, int position) {
        if (listView != null) {
            int pp = listView.getLastVisiblePosition();
            float mScaledFactor = context.getResources().getDisplayMetrics().density;
            int h = listView.getBottom() - (int) (130 * mScaledFactor);
            if (position == pp) {
                listView.setSelectionFromTop(position, h);
            }
            if (pp > 1 && position == pp - 1) {
                listView.setSelectionFromTop(position, h);
            }
        }
    }

    /**
     * 控制列表下载
     *
     * @param position
     * @param count
     * @param listener
     */
    public static void setLoadResource(int position, int count, LoadResourceListener listener) {
        if (count > 6) {
            if (position == count - 6) {
                // TODO 加载资源触发器
                if (listener != null)
                    listener.loadResource();
            }
        } else {
            if (position == count - 1) {
                // TODO 加载资源触发器
                if (listener != null)
                    listener.loadResource();
            }
        }
    }

    public static String splitAndFilterString(String input) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素,
        String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "").trim();

        return str;
    }

    /**
     * 检测是否为空
     */
    public static boolean isNull(Object object) {
        if (object == null || object.equals("") || object.equals("null")) {
            return true;
        }
        return false;
    }

    public static void showDetail(Context context, Class<?> detailActivity, String contentId,
                                  String categoryType) {
        Intent intent = new Intent(context, detailActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.UPDIS_INTENT_KEY_CONTENTID, contentId);
        intent.putExtra(Constant.UPDIS_INTENT_KEY_CATEGORYTYPE, categoryType);
        context.startActivity(intent);
    }

    public static void showWebContent(Context context,String fileName){
        Intent intent = new Intent(context, WebContentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.UPDIS_INTENT_KEY_FILENAME, fileName);
        context.startActivity(intent);
    }
}
