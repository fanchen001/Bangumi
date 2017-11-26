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
        return fromResponseBody(type, annotations,retrofit);
    }

    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations,Retrofit retrofit) {
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
        if (parser == null) throw new NullPointerException("RetrofitType == null");
        Converter<ResponseBody, Object> converter = null;
       if (parser.isJsonResponse()) {
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            converter = new GsonResponseConverter(gson, adapter);
        } else if (parser.isBaiduResponse()) {
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            converter = new BaiduResponseConverter(gson, adapter);
        } else if(parser.isJsoupResponse() && method != null && jsoup != null){
            converter = new JsoupResponseCoverter(retrofit,method.value(),jsoup.value());
        }
        if (converter == null) throw new NullPointerException("RetrofitType  is inexistence");
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
        if (parser == null) throw new NullPointerException("RetrofitType == null");
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
