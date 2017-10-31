package com.fanchen.imovie.view.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by fanchen on 2017/7/26.
 */
public class NestedRingtonePreference extends RingtonePreference {

    private Object defaultValue;

    public NestedRingtonePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NestedRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRingtonePreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        if(defaultValue == null || !(defaultValue instanceof String))return;
        String s = defaultValue.toString();
        if(s.contains("method:")){
            //执行方法
            try {
                Method declaredMethod = getContext().getClass().getDeclaredMethod(s.substring(7));
                declaredMethod.setAccessible(true);
                Object[] args = null;
                declaredMethod.invoke(getContext(),args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //打开某个页面
            try{
                Intent intent = new Intent(getContext(),Class.forName(s));
                getContext().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return defaultValue = super.onGetDefaultValue(a, index);
    }
}
