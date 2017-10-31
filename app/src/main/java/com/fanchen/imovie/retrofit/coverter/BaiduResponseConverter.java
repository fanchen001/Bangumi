package com.fanchen.imovie.retrofit.coverter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * 搜索智能词语联想
 *
 * 百度的API
 * Created by fanchen on 2017/7/15.
 */
public class BaiduResponseConverter extends StringResponseConverter {

    private final Gson gson;
    private final TypeAdapter<?> adapter;

    public BaiduResponseConverter(Gson gson, TypeAdapter<?> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    protected Object convertString(String str) throws IOException {
        if (str.length() > 20) {
            //去掉json数据多出的头部和尾部
            str = str.substring(17, str.length() - 2);
        }
        StringReader stringReader = new StringReader(str);
        JsonReader jsonReader = gson.newJsonReader(stringReader);
        try {
            return adapter.read(jsonReader);
        } finally {
            stringReader.close();
        }
    }

    @Override
    protected String getCharset() {
        return "GBK";
    }

}
