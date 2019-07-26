package com.fanchen.imovie.hook;

import android.content.Context;

import com.fanchen.imovie.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FuckHuaweiLoadedApk {

    private static final HuaWeiVerifier IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 26) {
            IMPL = new V26VerifierImpl();
        } else if (version >= 24) {
            IMPL = new V24VerifierImpl();
        } else {
            IMPL = new BaseVerifierImpl();
        }
    }

    public static void hookHuaWeiVerifier(Context baseContext) {
        try {
            if (null != baseContext && "ContextImpl".equals(baseContext.getClass().getSimpleName())) {
                IMPL.verifier(baseContext);
            } else {
                LogUtil.e(FuckHuaweiLoadedApk.class, "baseContext is't instance of ContextImpl");
            }
        } catch (Throwable e) {
            LogUtil.e(FuckHuaweiLoadedApk.class, "baseContext is " + e.toString());
        }
    }

    private static class FieldUtils {

        public static Field getDeclaredField(String name, String field, boolean accessible) {
            try {
                Field declaredField = Class.forName(name).getDeclaredField(field);
                declaredField.setAccessible(accessible);
                return declaredField;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Object readField(Field field, Object obj) {
            return readField(field, obj, true);
        }

        public static Object readField(Field field, Object obj, boolean accessible) {
            try {
                field.setAccessible(accessible);
                return field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Object readField(Object obj, String name) {
            try {
                Field declaredField = obj.getClass().getDeclaredField(name);
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Object writeField(Object obj, String name) {
            try {
                Field declaredField = obj.getClass().getDeclaredField(name);
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void writeField(Object receiverResourceObject, String whiteList, String[] toArray) {
            receiverResourceObject.getClass();
        }

    }

    private static class V26VerifierImpl extends BaseVerifierImpl {

        private static final String WHITE_LIST = "mWhiteListMap";

        @Override
        public void verifier(Context baseContext) throws Throwable {
            Object whiteListMapObject = getWhiteListObject(baseContext, WHITE_LIST);
            if (whiteListMapObject instanceof Map) {
                Map whiteListMap = (Map) whiteListMapObject;
                List whiteList = (List) whiteListMap.get(0);
                if (null == whiteList) {
                    whiteList = new ArrayList<>();
                    whiteListMap.put(0, whiteList);
                }
                whiteList.add(baseContext.getPackageName());
            }
        }
    }

    private static class V24VerifierImpl extends BaseVerifierImpl {

        private static final String WHITE_LIST = "mWhiteList";

        @Override
        public void verifier(Context baseContext) throws Throwable {
            Object whiteListObject = getWhiteListObject(baseContext, WHITE_LIST);
            if (whiteListObject instanceof List) {
                List whiteList = (List) whiteListObject;
                whiteList.add(baseContext.getPackageName());
            }
        }
    }

    private static class BaseVerifierImpl implements HuaWeiVerifier {

        private static final String WHITE_LIST = "mWhiteList";

        @Override
        public void verifier(Context baseContext) throws Throwable {
            Object receiverResourceObject = getWhiteListObject(baseContext, WHITE_LIST);
            if (receiverResourceObject instanceof String[]) {
                String[] whiteList = (String[]) receiverResourceObject;
                List<String> newWhiteList = new ArrayList<>();
                newWhiteList.add(baseContext.getPackageName());
                Collections.addAll(newWhiteList, whiteList);
                FieldUtils.writeField(receiverResourceObject, WHITE_LIST, newWhiteList.toArray(new String[newWhiteList.size()]));
            }
        }

        Object getWhiteListObject(Context baseContext, String whiteList) throws Throwable {
            Field receiverResourceField = FieldUtils.getDeclaredField("android.app.LoadedApk", "mReceiverResource", true);
            if (null != receiverResourceField) {
                Field packageInfoField = FieldUtils.getDeclaredField("android.app.ContextImpl", "mPackageInfo", true);
                if (null != packageInfoField) {
                    Object packageInfoObject = FieldUtils.readField(packageInfoField, baseContext);
                    if (null != packageInfoObject) {
                        Object receivedResource = FieldUtils.readField(receiverResourceField, packageInfoObject, true);
                        if (null != receivedResource) {
                            return FieldUtils.readField(receivedResource, whiteList);
                        }
                    }
                }
            }
            return null;
        }
    }

    private interface HuaWeiVerifier {
        void verifier(Context baseContext) throws Throwable;
    }

}
