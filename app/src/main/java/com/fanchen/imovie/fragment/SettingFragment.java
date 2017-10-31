package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;

import com.fanchen.imovie.R;

import java.lang.reflect.Method;


/**
 * Created by fanchen on 2017/7/26.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Class<?> superclass = getClass().getSuperclass();
        try {
            //取消下划线
            Method mList = superclass.getDeclaredMethod("getListView");
            mList.setAccessible(true);
            Object[] args = null;
            ListView invoke = (ListView) mList.invoke(this, args);
            invoke.setDivider(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
