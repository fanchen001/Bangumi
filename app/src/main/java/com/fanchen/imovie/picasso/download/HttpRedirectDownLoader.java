package com.fanchen.imovie.picasso.download;

import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * HttpRedirectDownLoader
 * 已经做了重定向处理的DownLoader
 * Created by fanchen on 2018/10/12.
 */
public class HttpRedirectDownLoader extends HttpsDownLoader {

    public HttpRedirectDownLoader(OkHttpClient client) {
        super(client);
    }

    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        okhttp3.Response response = doResponse(uri, networkPolicy);
        int responseCode = response.code();
        if (responseCode == 301 || responseCode == 302) {
            String location = response.header("Location", "");
            if (TextUtils.isEmpty(location)) {
                ResponseBody body = response.body();
                if (body != null) body.close();
                throw new ResponseException(responseCode + " " + response.message(), networkPolicy, responseCode);
            } else {
                ResponseBody body = response.body();
                if (body != null) body.close();
                return load(Uri.parse(location), networkPolicy);
            }
        } else if (responseCode >= 300) {
            ResponseBody body = response.body();
            if (body != null) body.close();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy, responseCode);
        }
        boolean fromCache = response.cacheResponse() != null;
        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

}
