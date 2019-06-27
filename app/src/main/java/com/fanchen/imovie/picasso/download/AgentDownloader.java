package com.fanchen.imovie.picasso.download;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.net.HttpURLConnection;

public class AgentDownloader extends RefererDownloader {

    public AgentDownloader(Context context, String referer) {
        super(context, referer);
    }

    public AgentDownloader(Context context) {
        this(context, "");
    }

    @Override
    protected HttpURLConnection openConnection(Uri path) throws IOException {
        HttpURLConnection httpURLConnection = super.openConnection(path);
        httpURLConnection.setRequestProperty("Connection", "keep-alive");
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        httpURLConnection.setRequestProperty("Accept-Encoding", "");
        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        return httpURLConnection;
    }

}
