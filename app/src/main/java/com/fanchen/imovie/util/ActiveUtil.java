package com.fanchen.imovie.util;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by fanchen on 2017/11/13.
 */
public class ActiveUtil {

    private static final int SYNTHETIC = 0x00001000;
    private static final int FINAL = 0x00000010;
    private static final int SYNTHETIC_AND_FINAL = SYNTHETIC | FINAL;

    private static boolean checkModifier(int mod) {
        return (mod & SYNTHETIC_AND_FINAL) == SYNTHETIC_AND_FINAL;
    }

    public static Object getExternalClass(Object target) {
        if(target == null)return null;
        try {
            return getField(target, null, null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getField(Object target, String name, Class classCache, int tier) throws Exception {
        if (classCache == null) {
            classCache = target.getClass();
        }
        if (classCache == null) {
            return null;
        }
        //初步判断该类是不是内部类
        if (!classCache.getName().contains("$")) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            name = "this$0";
        }
        if (tier == 5) {
            //为避免传入的不是内部类而造成死循环的调用，最多5层
            return null;
        }
        Field field = classCache.getDeclaredField(name);
        field.setAccessible(true);
        if (checkModifier(field.getModifiers())) {
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return getField(target, name + "$", classCache, tier += 1);
    }

    public static boolean checkActive(Object o) {
        Object outerClass = getExternalClass(o);
        if (outerClass != null && outerClass instanceof Activity) {
            Activity activity = (Activity) outerClass;
            if (activity.isFinishing() || activity.isDestroyed()) {
                LogUtil.e("ActiveUtil", "Activity已经销毁");
                return false;
            }
        } else if (outerClass != null && outerClass instanceof Fragment) {
            Fragment fragment = (Fragment) outerClass;
            if (!fragment.isAdded() || fragment.isDetached()) {
                LogUtil.e("ActiveUtil", "Fragment已经销毁或没有被添加到Activity");
                return false;
            }
        } else if (outerClass != null && outerClass instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) outerClass;
            if (!fragment.isAdded() || fragment.isDetached()) {
                LogUtil.e("ActiveUtil", "android.app.Fragment已经销毁或没有被添加到Activity");
                return false;
            }
        }else if(outerClass != null && outerClass instanceof Dialog){
            Dialog dialog = (Dialog) outerClass;
            if(!dialog.isShowing()){
                LogUtil.e("ActiveUtil", "Dialog已经销毁");
                return false;
            }
        }
        if (outerClass != null) {
            return true;
        }
        return false;
    }
}
