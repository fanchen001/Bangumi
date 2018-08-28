package com.fanchen.imovie.retrofit.coverter;

import android.text.TextUtils;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.jsoup.ITvParser;
import com.fanchen.imovie.jsoup.parser.HlyyTvParser;
import com.fanchen.imovie.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * 需要解析网页获取关心数据的时候
 * 使用JsoupResponseCoverter
 * Created by fanchen on 2017/7/18.
 */
public class JsoupLiveResponseCoverter extends StringResponseConverter {

    private static Map<JsoupSource, ITvParser> map = new HashMap<>();
    private MethodSource method;
    private JsoupSource jsoup;

    static {
        map.put(JsoupSource.KUAIKAN_TV,new HlyyTvParser());
    }

    public JsoupLiveResponseCoverter(Retrofit retrofit, MethodSource method, JsoupSource jsoup) {
        super(retrofit);
        this.method = method;
        this.jsoup = jsoup;
    }

    @Override
    public Object convert(ResponseBody responseBody) throws IOException {
        if (this.jsoup == JsoupSource.KANKAN) {
            try {
                byte[] bs = null;
                if (responseBody == null) {
                    throw new IOException("response body is empty");
                }
                bs = responseBody.bytes();
                if (bs == null || bs.length == 0) {
                    throw new IOException("response body is empty");
                }
                return convertString(new String(StreamUtil.stream2bytes(new GZIPInputStream(new ByteArrayInputStream(bs))), getCharset()));
            } catch (Exception e) {
                throw new IOException("charset is not supported.");
            } finally {
                if (responseBody != null)
                    responseBody.close();
            }
        }
        return super.convert(responseBody);
    }

    @Override
    protected Object convertString(String str) throws IOException {
        ITvParser videoParser = map.get(jsoup);
        if (videoParser == null) throw new IOException("Parser not found");
        if (retrofit == null) throw new IOException("retrofit not null");
        String baseUrl = retrofit.baseUrl().toString();
        if (TextUtils.isEmpty(baseUrl)) throw new IOException("baseUrl not null");
        Object object = null;
        if (method == MethodSource.HOME) {
            object = videoParser.liveList(retrofit, baseUrl.substring(0, baseUrl.length() - 1), str);
        } if (method == MethodSource.PLAYURL) {
            object = videoParser.liveUrl(retrofit, baseUrl.substring(0, baseUrl.length() - 1), str);
        }
        return object;
    }

}
