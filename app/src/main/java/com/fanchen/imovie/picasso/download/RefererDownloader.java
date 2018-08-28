package com.fanchen.imovie.picasso.download;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 *
 * Created by fanchen on 2017/10/2.
 */
public class RefererDownloader extends UrlConnectionDownloader{

    private String referer;

    public RefererDownloader(Context context,String referer) {
        super(context);
        this.referer = referer;
    }

    @Override
    protected HttpURLConnection openConnection(Uri path) throws IOException {
        HttpURLConnection httpURLConnection = super.openConnection(path);
        httpURLConnection.setRequestProperty("Referer",referer);
        return httpURLConnection;
    }
}
