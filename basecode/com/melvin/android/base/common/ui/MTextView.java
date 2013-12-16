
package com.melvin.android.base.common.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianv.updis.R;

public class MTextView extends LinearLayout {
    public EditText editText;

    private TextView textView;

    private LinearLayout viewPanel;

    public MTextView(Context context) {
        super(context);
    }

    public MTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray text = context.obtainStyledAttributes(attrs, R.styleable.MTextView);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tag_text_view, this);
        editText = (EditText) findViewById(R.id.txt_edittext);
        textView = (TextView) findViewById(R.id.txt_tag);
        textView.setText(text.getText(R.styleable.MTextView_text));
        viewPanel = (LinearLayout) findViewById(R.id.view_panel);
    }

    /**
     * 设置文本区域标题
     *
     * @param resId
     */
    public void setTagText(int resId) {
        textView.setText(resId);
    }

    /**
     * 文本赋值
     *
     * @param str
     */
    public void setEditTextStr(String str) {
        editText.setText(str);
    }

    /**
     * 获取值
     * @return
     */
    public String getText(){
        return editText.getText().toString();
    }
    /**
     * 设置输入框读写属性
     *
     * @param sFlag
     */
    public void setEditEnable(boolean sFlag) {
        editText.setEnabled(sFlag);
    }

    public void setBackground(int resId) {
        viewPanel.setBackgroundResource(resId);
    }

}
