package com.fanchen.imovie.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.NetworkUtil;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 网络请求管理器
 * Created by fanchen on 2017/7/15.
 */
public class RetrofitManager {

    public static String REQUEST_URL = "";

    private static RetrofitManager manager;
    private static Context appContext;
    private static Map<String, Object> clazzCache = new HashMap<>();
    private static Map<String, Integer> keyCache = new HashMap<>();

    private static Map<RetrofitSource, Retrofit> retrofitMap = new HashMap<>();

    static {
        Converter.Factory animFactory = IMovieFactory.create();
        retrofitMap.put(RetrofitSource.XIAOBO_API, new Retrofit.Builder().baseUrl("http://vod.xiaokanba.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ACG12_API, new Retrofit.Builder().baseUrl("https://acg12.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.TUCAO_API, new Retrofit.Builder().baseUrl("http://www.tucao.tv/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DYTT_API, new Retrofit.Builder().baseUrl("http://101.37.135.113/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.MOEAPK_API, new Retrofit.Builder().baseUrl("https://api.moeapk.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.XIAOMA_API, new Retrofit.Builder().baseUrl("http://nav.api.sbxia.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BAIDU_API, new Retrofit.Builder().baseUrl("https://sp0.baidu.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.S80_API, new Retrofit.Builder().baseUrl("http://m.80s.tw/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BUMIMI_API, new Retrofit.Builder().baseUrl("http://m.bumimi.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.JREN_API, new Retrofit.Builder().baseUrl("https://jren100.moe/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DM5_API, new Retrofit.Builder().baseUrl("http://www.5dm.tv/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BILIPLUS_API, new Retrofit.Builder().baseUrl("https://www.biliplus.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DIANXIUMEI_API, new Retrofit.Builder().baseUrl("http://www.dianxiumei.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.XIAOKANBA_API, new Retrofit.Builder().baseUrl("http://xiaokanba.com/").addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.KMAO_API, new Retrofit.Builder().baseUrl("http://m.kkkkmao.com/").addConverterFactory(animFactory).build());
    }

    /**
     * @param context
     */
    private RetrofitManager(Context context) {
        if (appContext == null)
            appContext = context.getApplicationContext();
    }

    /**
     * @param context
     * @return
     */
    public static RetrofitManager with(Context context) {
        if (manager == null) {
            synchronized (RetrofitManager.class) {
                if (manager == null) {
                    manager = new RetrofitManager(context);
                }
            }
        }
        return manager;
    }


    /**
     * @param method
     * @param o
     * @return
     */
    @Nullable
    private Method findMethod(String method, Object o, Object... args) {
        Class<?> types[] = null;
        if (args != null) {
            types = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> aClass = args[i].getClass();
                if (aClass.getName().contains("$")) {
                    types[i] = aClass.getSuperclass();
                } else {
                    types[i] = aClass;
                }
            }
        }
        Method declaredMethod = null;
        try {
            declaredMethod = o.getClass().getDeclaredMethod(method, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (declaredMethod == null) {
            throw new RuntimeException("NoSuchMethodException");
        }
        return declaredMethod;
    }

    /**
     * @param clazz
     * @return
     * @throws RuntimeException
     */
    private Object create(Class<?> clazz) throws RuntimeException {
        RetrofitType annotations = clazz.getAnnotation(RetrofitType.class);
        if (annotations == null) {
            throw new RuntimeException("this class not annotations flag");
        }
        RetrofitSource type = annotations.value();
        Retrofit retrofit = retrofitMap.get(type);
        if (retrofit == null) {
            throw new RuntimeException("can not find retrofit by key " + type);
        }
        return retrofit.create(clazz);
    }

    @NonNull
    private Integer findKey(Class<?> clazz, String method) {
        String key = clazz.getName() + "@" + method;
        Integer integer = keyCache.get(key);
        if (integer == null) {
            integer = keyCache.size();
        }
        return integer;
    }

    /**
     * @param clazz
     * @param callback
     * @param method
     * @param enqueueKey
     * @param <T>
     */
    public <T> void enqueue(Class<?> clazz, RetrofitCallback<T> callback, String method, int enqueueKey) {
        Object[] objs = null;
        enqueue(clazz, callback, method, enqueueKey, objs);
    }

    /**
     * @param clazz
     * @param callback
     * @param method
     * @param <T>
     */
    public <T> void enqueue(Class<?> clazz, RetrofitCallback<T> callback, String method) {
        Integer integer = findKey(clazz, method);
        enqueue(clazz, callback, method, integer);
    }

    /**
     * @param clazzStr
     * @param callback
     * @param method
     * @param args
     * @param <T>
     */
    public <T> void enqueue(String clazzStr, final RetrofitCallback<T> callback, String method, Object... args) {
        if (TextUtils.isEmpty(clazzStr)) return;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            Integer integer = findKey(clazz, method);
            enqueue(clazz, callback, method, integer, args);
        }
    }

    /**
     * @param clazzStr
     * @param callback
     * @param method
     * @param <T>
     */
    public <T> void enqueue(String clazzStr, RetrofitCallback<T> callback, String method) {
        if (TextUtils.isEmpty(clazzStr)) return;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            Integer integer = findKey(clazz, method);
            enqueue(clazz, callback, method, integer);
        }
    }

    /**
     * @param clazzStr
     * @param callback
     * @param method
     * @param enqueueKey
     * @param args
     * @param <T>
     */
    public <T> void enqueue(String clazzStr, final RetrofitCallback<T> callback, String method, final int enqueueKey, Object... args) {
        if (TextUtils.isEmpty(clazzStr)) return;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            enqueue(clazz, callback, method, enqueueKey, args);
        }
    }

    /**
     * @param clazz
     * @param callback
     * @param method
     * @param args
     * @param <T>
     */
    public <T> void enqueue(Class<?> clazz, final RetrofitCallback<T> callback, String method, Object... args) {
        Integer integer = findKey(clazz, method);
        enqueue(clazz, callback, method, integer, args);
    }

    /**
     * @param clazz
     * @param callback
     * @param method
     * @param enqueueKey
     * @param args
     * @param <T>
     */
    public <T> void enqueue(Class<?> clazz, final RetrofitCallback<T> callback, String method, final int enqueueKey, Object... args) {
        if (clazz == null || callback == null || TextUtils.isEmpty(method)) return;
        boolean isRefreshCallback = false;
        if (callback instanceof RefreshCallback)
            isRefreshCallback = true;
        try {
            String key = clazz.getSimpleName();
            //取缓存
            Object o = clazzCache.get(key);
            if (o == null) {
                o = create(clazz);
                clazzCache.put(key, o);
            }
            Method enqueueMethod = findMethod(method, o, args);
            if (enqueueMethod == null) {
                throw new RuntimeException("can not find Method name " + method);
            }
            String proHost = android.net.Proxy.getDefaultHost();
            int proPort = android.net.Proxy.getDefaultPort();
            //简单的防Fiddler抓包检测
//            if (!TextUtils.isEmpty(proHost) && (proHost.startsWith("192") || proHost.startsWith("127")) && proPort != -1) {
//                if (isRefreshCallback) {
//                    ((RefreshCallback) callback).onFailure(enqueueKey, "请勿使用抓包工具,谢谢");
//                    ((RefreshCallback) callback).onFinish(enqueueKey);
//                }
//            } else {
                Call<T> invoke = (Call<T>) enqueueMethod.invoke(o, args);
                if (NetworkUtil.isNetWorkAvailable(appContext)) {
                    if (isRefreshCallback)
                        ((RefreshCallback) callback).onStart(enqueueKey);
                    invoke.enqueue(new EnqueueCallback<T>(callback, isRefreshCallback, enqueueKey));
                } else {
                    //网络不可用
                    if (isRefreshCallback) {
                        ((RefreshCallback) callback).onFailure(enqueueKey, "当前网络不可用");
                        ((RefreshCallback) callback).onFinish(enqueueKey);
                    }
                }
//            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (isRefreshCallback) {
                ((RefreshCallback) callback).onFailure(enqueueKey, "请求网络数据出错");
                ((RefreshCallback) callback).onFinish(enqueueKey);
            }
        }
    }

    private static class EnqueueCallback<T> implements Callback<T> {
        private SoftReference<RetrofitCallback<T>> callback;
        private int enqueueKey;
        private boolean isRefreshCallback;

        public EnqueueCallback(RetrofitCallback<T> callback, boolean isRefreshCallback, int enqueueKey) {
            this.callback = new SoftReference<>(callback);
            this.enqueueKey = enqueueKey;
            this.isRefreshCallback = isRefreshCallback;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            RetrofitCallback<T> tRetrofitCallback = callback.get();
            if (tRetrofitCallback == null) return;
            tRetrofitCallback.onSuccess(enqueueKey, (T) response.body());
            if (isRefreshCallback)
                ((RefreshCallback) tRetrofitCallback).onFinish(enqueueKey);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            t.printStackTrace();
            RetrofitCallback<T> tRetrofitCallback = callback.get();
            if (tRetrofitCallback == null) return;
            if (isRefreshCallback) {
                ((RefreshCallback) tRetrofitCallback).onFailure(enqueueKey, "请求网络数据失败");
                ((RefreshCallback) tRetrofitCallback).onFinish(enqueueKey);
            }

        }
    }
}
