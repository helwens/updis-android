
package com.melvin.android.base.common.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

import com.tianv.updis.Constant;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: MessageDialog.java
 * @Description: TODO
 * @Date 2013-4-9 下午10:34:56
 */
public class MessageDialog {
    private String sCaptionClose = "关闭";

    private String sCaptionOk = "确定";

    private String sCaptionCancel = "取消";

    private int iIcoResourceId = -1;

    private Activity activity = null;

    private String[] mChioceItems = null;

    public String getCaptionClose() {
        return sCaptionClose;
    }

    public void setCaptionClose(String sCaptionClose) {
        this.sCaptionClose = sCaptionClose;
    }

    public String getCaptionOk() {
        return sCaptionOk;
    }

    public void setCaptionOk(String sCaptionOk) {
        this.sCaptionOk = sCaptionOk;
    }

    public String getCaptionCancel() {
        return sCaptionCancel;
    }

    public void setCaptionCancel(String sCaptionCancel) {
        this.sCaptionCancel = sCaptionCancel;
    }

    public int getIcoResourceId() {
        return iIcoResourceId;
    }

    public void setIcoResourceId(int iIcoResourceId) {
        this.iIcoResourceId = iIcoResourceId;
    }

    // -----------------------------------------------------------------------

    public MessageDialog(Activity activity) {
        this.activity = activity;
    }

    // -----------------------------------------------------------------------

    /**
     * 显示带有标题的提示信息对话框 该对话框仅带有一个“关闭”按钮
     *
     * @param requestCode 激活ID号
     * @param title       信息提示标题
     * @param message     信息提示内容
     * @param listener    对话框按键点击事件侦听对象
     */
    public void showInfo(int requestCode, String title, String message,
                         IMessageDialogListener listener) {

        Builder builder = createDialogBuilder(title, message);

        if (listener != null) {
            builder.setPositiveButton(sCaptionClose, new DialogOnClickListener(requestCode, 0,
                    listener));
        } else {
            builder.setPositiveButton(sCaptionClose, null);
        }
        builder.create().show();
    }

    /**
     * 显示单选列表对话框
     *
     * @param requestCode
     * @param title
     * @param message
     * @param listener      对话框按纽事件监听
     * @param clicklistener 选项列表监听
     */
    public void showInfo(int requestCode, String title, String message,
                         IMessageDialogListener listener, DialogInterface.OnClickListener clicklistener) {

        Builder builder = createDialogBuilder(title, message);

        if (listener != null) {
            builder.setPositiveButton(sCaptionClose, new DialogOnClickListener(requestCode, 0,
                    listener));
        } else {
            builder.setPositiveButton(sCaptionClose, null);
        }

        if (clicklistener != null) {
            if (this.mChioceItems != null) {
                builder.setSingleChoiceItems(mChioceItems, 0, clicklistener);
            }
        }
        builder.create().show();
    }

    /**
     * 显示单选列表
     *
     * @param requestCode
     * @param title
     * @param items
     * @param listener
     * @param clicklistener
     */
    public void showInfo(int requestCode, String title, String[] items,
                         IMessageDialogListener listener, DialogInterface.OnClickListener clicklistener) {

        Builder builder = createDialogBuilder(title, "");

        if (listener != null) {
            builder.setPositiveButton(sCaptionClose, new DialogOnClickListener(requestCode, 0,
                    listener));
        } else {
            builder.setPositiveButton(sCaptionClose, null);
        }
        builder.setSingleChoiceItems(items, 0, clicklistener);
        if (clicklistener != null) {
            if (this.mChioceItems != null) {
                builder.setSingleChoiceItems(mChioceItems, 0, clicklistener);
            }
        }
        builder.create().show();
    }


    /**
     * 显示带有标题的提示信息对话框 该对话框仅带有一个“关闭”按钮
     *
     * @param title    信息提示标题
     * @param messager 信息提示内容
     */
    public void showInfo(String title, String messager) {
        showInfo(Constant.UPDIS_DEFAULT_DIALOG_ID, title, messager, null);
    }

    /**
     * 显示不含标题的提示信息对话框 该对话框仅带有一个“关闭”按钮 默认激活ID号为
     *
     * @param messager 信息提示内容
     */
    public void showInfo(String messager) {
        showInfo(Constant.UPDIS_DEFAULT_DIALOG_ID, "", messager, null);
    }

    /**
     * 显示带有标题的确认信息对话框 该对话框带有“确认”和“取消”两个按钮
     *
     * @param requestCode 激活ID号
     * @param title       信息提示标题
     * @param message     信息提示内容
     * @param listener    对话框按键点击事件侦听对象
     */
    public void showConfirm(int requestCode, String title, String message,
                            IMessageDialogListener listener) {

        Builder builder = createDialogBuilder(title, message);

        if (listener != null) {
            builder.setPositiveButton(sCaptionOk, new DialogOnClickListener(requestCode, 1,
                    listener));
            builder.setNegativeButton(sCaptionCancel, new DialogOnClickListener(requestCode, 2,
                    listener));
        } else {
            builder.setPositiveButton(sCaptionOk, null);
            builder.setPositiveButton(sCaptionCancel, null);
        }
        builder.create().show();
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * 创建对话框Android的AlertDialog.Builder对象
     *
     * @param title   信息提示标题
     * @param message 信息提示内容
     * @return AlertDialog.Builder对象
     */
    public AlertDialog.Builder createDialogBuilder(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        if (message != null && !message.equals("")) {
            builder.setMessage(message);
        }

        if (this.iIcoResourceId != -1) {
            builder.setIcon(this.iIcoResourceId);
        }

        return builder;
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * 以Toast的方式显示提示信息 该Toast不含任何按钮
     *
     * @param message 提示信息
     */
    public void showToast(String message) {

        Toast t = null;

        t = Toast.makeText(this.activity, message, Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    // ----------------------------------------------------------------------------------------------------------------

    public String[] getChioceItems() {
        return mChioceItems;
    }

    public void setChioceItems(String[] mChioceItems) {
        this.mChioceItems = mChioceItems;
    }

    private class DialogOnClickListener implements DialogInterface.OnClickListener {

        private int requestCode;

        private int clickid = 0;

        private IMessageDialogListener listener;

        public DialogOnClickListener(int requestCode, int clickid, IMessageDialogListener listener) {
            this.requestCode = requestCode;
            this.clickid = clickid;
            this.listener = listener;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            if (this.listener != null) {
                switch (this.clickid) {
                    case 0:
                        this.listener.onDialogClickClose(this.requestCode);
                        break;
                    case 1:
                        this.listener.onDialogClickOk(this.requestCode);
                        break;
                    case 2:
                        this.listener.onDialogClickCancel(this.requestCode);
                        break;
                }
            }

        }

    }// end class
}
