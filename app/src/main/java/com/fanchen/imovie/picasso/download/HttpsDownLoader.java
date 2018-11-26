package com.fanchen.imovie.picasso.download;

import android.net.Uri;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * HttpsDownLoader
 * Created by fanchen on 2018/3/13.
 */
public class HttpsDownLoader implements Downloader {

    protected OkHttpClient client = null;

    public HttpsDownLoader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        okhttp3.Response response = doResponse(uri, networkPolicy);
        int responseCode = response.code();
        if (responseCode >= 300) {
            ResponseBody body = response.body();
            if (body != null) body.close();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy, responseCode);
        }
        boolean fromCache = response.cacheResponse() != null;
        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

    protected okhttp3.Response doResponse(Uri uri, int networkPolicy) throws IOException {
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                CacheControl.Builder builder = new CacheControl.Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }
        Request.Builder builder = new Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            builder.cacheControl(cacheControl);
        }
        return client.newCall(builder.build()).execute();
    }

    @Override
    public void shutdown() {
        Cache cache = client.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
