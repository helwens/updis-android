
package com.melvin.android.base.common.ui;

/**
 * @author Melvin
 * @version V1.0
 * @ClassName: IMessageDialogListener.java
 * @Description: TODO
 * @Date 2013-4-9 下午10:35:51
 */
public interface IMessageDialogListener {
    public void onDialogClickOk(int requestCode);

    public void onDialogClickCancel(int requestCode);

    public void onDialogClickClose(int requestCode);
}
