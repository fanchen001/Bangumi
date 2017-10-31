package com.fanchen.imovie.picasso.inter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Acg12Interceptor implements Interceptor {

    private String referer;

    public Acg12Interceptor(String referer){
        this.referer = referer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        request.addHeader("Referer", referer);
        return chain.proceed(request.build());
    }

}
