package com.fanchen.imovie.retrofit.coverter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 返回数据为json
 * 使用GsonResponseConverter来进行处理
 * Created by fanchen on 2017/7/15.
 */
public class GsonResponseConverter extends StringResponseConverter {

    private final Gson gson;
    private final TypeAdapter<?> adapter;

    public GsonResponseConverter(Gson gson, TypeAdapter<?> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    public GsonResponseConverter(Retrofit retrofit, Gson gson, TypeAdapter<?> adapter) {
        super(retrofit);
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    protected Object convertString(String str) throws IOException {
        StringReader stringReader = new StringReader(str);
        JsonReader jsonReader = gson.newJsonReader(stringReader);
        try {
            return adapter.read(jsonReader);
        } finally {
            stringReader.close();
        }
    }
}
