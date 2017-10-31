package com.fanchen.imovie.retrofit;

import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.retrofit.coverter.BaiduResponseConverter;
import com.fanchen.imovie.retrofit.coverter.GsonRequestConverter;
import com.fanchen.imovie.retrofit.coverter.GsonResponseConverter;
import com.fanchen.imovie.retrofit.coverter.JsoupResponseCoverter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *
 * Created by fanchen on 2017/7/15.
 */
public class IMovieFactory extends Converter.Factory {

    public static IMovieFactory create() {
        return new IMovieFactory();
    }

    private IMovieFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return fromResponseBody(type, annotations);
    }

    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        if (annotations == null) throw new NullPointerException("annotations == null");
        RetrofitType parser = null;
        MethodType method = null;
        JsoupType jsoup = null;
        for (Annotation a : annotations) {
            if (a instanceof RetrofitType) {
                parser = (RetrofitType) a;
            } else if(a instanceof MethodType){
                method = (MethodType) a;
            } else if(a instanceof JsoupType){
                jsoup = (JsoupType) a;
            }
        }
        if (parser == null) throw new NullPointerException("AnimParser == null");
        Converter<ResponseBody, Object> converter = null;
        RetrofitSource animValue = parser.value();
       if (animValue == RetrofitSource.XIAOMA_API
                || animValue == RetrofitSource.MOEAPK_API
                || animValue == RetrofitSource.DYTT_API
                || animValue == RetrofitSource.ACG12_API
                || animValue == RetrofitSource.XIAOBO_API) {
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            converter = new GsonResponseConverter(gson, adapter);
        } else if (animValue == RetrofitSource.BAIDU_API) {
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            converter = new BaiduResponseConverter(gson, adapter);
        } else if(animValue == RetrofitSource.TUCAO_API
                || animValue == RetrofitSource.S80_API
                || animValue == RetrofitSource.BUMIMI_API
                || animValue == RetrofitSource.JREN_API
               || animValue == RetrofitSource.DM5_API
               || animValue == RetrofitSource.BILIPLUS_API
               || animValue == RetrofitSource.DIANXIUMEI_API
               || animValue == RetrofitSource.XIAOKANBA_API
               || animValue == RetrofitSource.KMAO_API){
            if (method == null || jsoup == null)
                throw new NullPointerException("jsoupAnnotation == null");
            converter = new JsoupResponseCoverter(method.value(),jsoup.value());
        }
        if (converter == null) throw new NullPointerException("AnimType  is inexistence");
        return converter;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (methodAnnotations == null) throw new NullPointerException("annotations == null");
        RetrofitType parser = null;
        for (Annotation a : methodAnnotations) {
            if (a instanceof RetrofitType) {
                parser = (RetrofitType) a;
            }
        }
        Converter<?, RequestBody> converter = null;
        if (parser.isJsonRequest()){
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            converter = new GsonRequestConverter(gson,adapter);
        }else{
            converter = super.requestBodyConverter(type,parameterAnnotations,methodAnnotations,retrofit);
        }
        return converter;
    }
}
