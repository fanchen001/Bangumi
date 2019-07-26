package com.fanchen.imovie.retrofit;

import android.content.Context;
import android.text.TextUtils;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.util.ActiveUtil;
import com.fanchen.imovie.util.NetworkUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

    public static String PATH_ID = "";
    public static String REQUEST_URL = "";

    private static RetrofitManager manager;
    private static Context appContext;
    private static Map<String, Object> clazzCache = new HashMap<>();
    private static Map<String, Integer> keyCache = new HashMap<>();

    private static Map<RetrofitSource, Retrofit> retrofitMap = new HashMap<>();

    static {
        Converter.Factory animFactory = IMovieFactory.create();
        //部分为非公开API。这里用baidu代替
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).sslSocketFactory(StreamUtil.getSSLSocketFactory()).hostnameVerifier(StreamUtil.getHostnameVerifier()).build();
        retrofitMap.put(RetrofitSource.XIAOBO_API, new Retrofit.Builder().baseUrl("http://vod.xiaokanba.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ACG12_API, new Retrofit.Builder().baseUrl("https://acg12.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.MOEAPK_API, new Retrofit.Builder().baseUrl("https://api.apk.moe/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.XIAOMA_API, new Retrofit.Builder().baseUrl("http://nav.api.sbxia.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BAIDU_API, new Retrofit.Builder().baseUrl("https://sp0.baidu.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DYTT_API, new Retrofit.Builder().baseUrl("http://app.xiaokanba.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.JREN_API, new Retrofit.Builder().baseUrl("https://jren100.vip/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.JREN_API, new Retrofit.Builder().baseUrl("http://www.tianjiyy123.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DM5_API, new Retrofit.Builder().baseUrl("https://www.5dm.tv/").client(okHttpClient).client(okHttpClient).addConverterFactory(animFactory).build());
        //http://dianxiumei.com/    http://www.k4pp.com
        retrofitMap.put(RetrofitSource.BILIPLUS_API, new Retrofit.Builder().baseUrl("https://www.biliplus.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.DIANXIUMEI_API, new Retrofit.Builder().baseUrl("https://www.kwpig.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.DIANXIUMEI_API, new Retrofit.Builder().baseUrl("http://m.sdyy001.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.TUCAO_API, new Retrofit.Builder().baseUrl("http://www.tucao.one/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.S80_API, new Retrofit.Builder().baseUrl("https://m.80ying.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.S80_API, new Retrofit.Builder().baseUrl("https://m.80s.tw/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.V6_API, new Retrofit.Builder().baseUrl("http://v.6.cn/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BUMIMI_API, new Retrofit.Builder().baseUrl("http://m.4ktv8.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.XIAOKANBA_API, new Retrofit.Builder().baseUrl("https://www.kankan001.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.XIAOKANBA_API, new Retrofit.Builder().baseUrl("https://www.52dy.me/").client(okHttpClient).addConverterFactory(animFactory).build());
//      retrofitMap.put(RetrofitSource.XIAOKANBA_API, new Retrofit.Builder().baseUrl("http://xiaokanba.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.KANKANWU_API, new Retrofit.Builder().baseUrl("https://m.kankanwu.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.KMAO_API, new Retrofit.Builder().baseUrl("http://m.kkkkmao.com/").client(okHttpClient).addConverterFactory(animFactory).build());

        retrofitMap.put(RetrofitSource.LL520_API, new Retrofit.Builder().baseUrl("http://m.ism89.net/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.LL520_API, new Retrofit.Builder().baseUrl("http://m.4xb.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.MMYY_API, new Retrofit.Builder().baseUrl("https://m.tongbuyy.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.K8DY_API, new Retrofit.Builder().baseUrl("http://m.17ktv.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.K8DY_API, new Retrofit.Builder().baseUrl("http://m.567ktv.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.K8DY_API, new Retrofit.Builder().baseUrl("http://m.4g33.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BABAYU_API, new Retrofit.Builder().baseUrl("https://m.jukantv.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.A4DY_API, new Retrofit.Builder().baseUrl("http://m.aaxxr.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.IKANFAN_API, new Retrofit.Builder().baseUrl("https://m.ysba.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.HALIHALI_API, new Retrofit.Builder().baseUrl("https://suyingtv.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.HALIHALI_API, new Retrofit.Builder().baseUrl("https://m.halihali.me/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.SMDY_API, new Retrofit.Builder().baseUrl("http://m.sm5.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.SMDY_API, new Retrofit.Builder().baseUrl("https://m.ism88.net/").client(okHttpClient).addConverterFactory(animFactory).build());

        retrofitMap.put(RetrofitSource.SMDY_API, new Retrofit.Builder().baseUrl("http://m.xigua15.net/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.AISM_API, new Retrofit.Builder().baseUrl("https://m.aism.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.WANDOU_API, new Retrofit.Builder().baseUrl("https://www.wandouys.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ZHANDI_API, new Retrofit.Builder().baseUrl("https://m.ywt.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.ZHANDI_API, new Retrofit.Builder().baseUrl("http://m.zhandi.cc/").client(okHttpClient).addConverterFactory(animFactory).build());

        // retrofitMap.put(RetrofitSource.BOBMAO_API, new Retrofit.Builder().baseUrl("http://m.haodianying.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BOBMAO_API, new Retrofit.Builder().baseUrl("https://www.kdyy.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.BILIBILI_API, new Retrofit.Builder().baseUrl("https://app.bilibili.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.KUPIAN_API, new Retrofit.Builder().baseUrl("http://m.kupian.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.TAIHAN_API, new Retrofit.Builder().baseUrl("https://www.taiju.la/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.TEPIAN_API, new Retrofit.Builder().baseUrl("https://m.itepian.net/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.JUGOU_API, new Retrofit.Builder().baseUrl("http://www.nanjiyy.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.JUGOU_API, new Retrofit.Builder().baseUrl("http://video.jfenxiang.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.LAOSIJI_API, new Retrofit.Builder().baseUrl("http://m.smdy88.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ZZZVZ_API, new Retrofit.Builder().baseUrl("https://m.kuaikan66.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.HLYY_API, new Retrofit.Builder().baseUrl("http://m.9178.tv/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.VIPYS_API, new Retrofit.Builder().baseUrl("http://m.vipys.net/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.CCY_API, new Retrofit.Builder().baseUrl("https://m.teshiw.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.WEILAI_API, new Retrofit.Builder().baseUrl("https://www.55vcd.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.WEILAI_API, new Retrofit.Builder().baseUrl("http://www.zaixian88.com/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.HAOQU_API, new Retrofit.Builder().baseUrl("http://m.haoqu.net/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ICAN_API, new Retrofit.Builder().baseUrl("http://m.icantv.cn/").client(okHttpClient).addConverterFactory(animFactory).build());
        retrofitMap.put(RetrofitSource.ZZYO_API, new Retrofit.Builder().baseUrl("http://m.tufutv8.com/").client(okHttpClient).addConverterFactory(animFactory).build());
//        retrofitMap.put(RetrofitSource.ZZYO_API, new Retrofit.Builder().baseUrl("http://www.zzyo.cc/").client(okHttpClient).addConverterFactory(animFactory).build());
    }


    public static Map<RetrofitSource, Retrofit> getRetrofitMap(){
        return retrofitMap;
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

    public static String  warpUrl(String base,String url){
        if(url.startsWith("http")){
            return url;
        }else if(url.startsWith("//")){
            return base.split("/")[0]  + url;
        }else if(url.startsWith("/")){
            return base + url;
        }else{
            String[] split = RetrofitManager.REQUEST_URL.split("/");
            String ssplit = split[split.length - 1];
            return RetrofitManager.REQUEST_URL.replace(ssplit,"") + url;
        }
    }

    /**
     * @param method
     * @param o
     * @return
     */
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
    public <T> void enqueue(String clazzStr, RetrofitCallback<T> callback, String method, Object... args) {
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
    public <T> void enqueue(String clazzStr, RetrofitCallback<T> callback, String method, int enqueueKey, Object... args) {
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
    public <T> void enqueue(Class<?> clazz, RetrofitCallback<T> callback, String method, Object... args) {
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
    public <T> void enqueue(Class<?> clazz, RetrofitCallback<T> callback, String method, int enqueueKey, Object... args) {
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
//            if (!TextUtils.isEmpty(proHost) && (proHost.startsWith("127") || proHost.startsWith("192") || proHost.startsWith("127")) && proPort != -1) {
//                if (isRefreshCallback && ActiveUtil.checkActive(callback)) {
//                    ((RefreshCallback) callback).onFailure(enqueueKey, "请勿使用抓包工具,谢谢");
//                    ((RefreshCallback) callback).onFinish(enqueueKey);
//                }
//            } else {
                Call<T> invoke = (Call<T>) enqueueMethod.invoke(o, args);
                if (NetworkUtil.isNetWorkAvailable(appContext)) {
                    if (isRefreshCallback && ActiveUtil.checkActive(callback))
                        ((RefreshCallback) callback).onStart(enqueueKey);
                    invoke.enqueue(new EnqueueCallback<T>(callback, isRefreshCallback, enqueueKey));
                } else {
                    //网络不可用
                    if (isRefreshCallback && ActiveUtil.checkActive(callback)) {
                        ((RefreshCallback) callback).onFailure(enqueueKey, "当前网络不可用");
                        ((RefreshCallback) callback).onFinish(enqueueKey);
                    }
                }
//            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (isRefreshCallback && ActiveUtil.checkActive(callback)) {
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
            if (tRetrofitCallback == null || !ActiveUtil.checkActive(tRetrofitCallback)) return;
            tRetrofitCallback.onSuccess(enqueueKey, (T) response.body());
            if (isRefreshCallback)
                ((RefreshCallback) tRetrofitCallback).onFinish(enqueueKey);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            t.printStackTrace();
            RetrofitCallback<T> tRetrofitCallback = callback.get();
            if (tRetrofitCallback == null || !ActiveUtil.checkActive(tRetrofitCallback)) return;
            if (isRefreshCallback) {
                ((RefreshCallback) tRetrofitCallback).onFailure(enqueueKey, "请求网络数据失败");
                ((RefreshCallback) tRetrofitCallback).onFinish(enqueueKey);
            }

        }

    }


}
