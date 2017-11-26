package com.fanchen.imovie.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 为了解决部分机器上。有提示的功能，如果限制了EditText输入长度时
 * 这个时候如果在输入框输入了 this is andrd,并且达到了最大的字数限制，根据纠错功能会弹出一个下拉框，
 * 让你选其他的词语，如android,这个时候如果你选了它提示的词语，就会造成上面的那个错误。
 * java.lang.IndexOutOfBoundsException
 * setSpan (12 ... 12) ends beyond length 11
 * Created by fanchen on 2017/11/18.
 */
public class SelectionEditText extends EditText{

    public SelectionEditText(Context context) {
        super(context);
    }

    public SelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void selectAll() {
        try {
            super.selectAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setSelection(int index) {
        try {
            super.setSelection(index);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setSelection(int start, int stop) {
        try {
            super.setSelection(start, stop);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
