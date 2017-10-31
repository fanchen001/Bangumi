package com.fanchen.imovie.retrofit.coverter;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.jsoup.IBangumiParser;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.parser.BiliplusImpl;
import com.fanchen.imovie.jsoup.parser.BumimiImpl;
import com.fanchen.imovie.jsoup.parser.DianxiumeiImpl;
import com.fanchen.imovie.jsoup.parser.Dm5Impl;
import com.fanchen.imovie.jsoup.parser.JrenImpl;
import com.fanchen.imovie.jsoup.parser.KmaoImpl;
import com.fanchen.imovie.jsoup.parser.S80Impl;
import com.fanchen.imovie.jsoup.parser.TucaoImpl;
import com.fanchen.imovie.jsoup.parser.XiaokanbaImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 需要解析网页获取关心数据的时候
 * 使用JsoupResponseCoverter
 * Created by fanchen on 2017/7/18.
 */
public class JsoupResponseCoverter extends StringResponseConverter {
    private static Map<JsoupSource, IVideoParser> map = new HashMap<>();

    static {
        map.put(JsoupSource.TUCAO, new TucaoImpl());
        map.put(JsoupSource.S80, new S80Impl());
        map.put(JsoupSource.BUMIMI, new BumimiImpl());
        map.put(JsoupSource.JREN, new JrenImpl());
        map.put(JsoupSource.DM5, new Dm5Impl());
        map.put(JsoupSource.BILIPLUS, new BiliplusImpl());
        map.put(JsoupSource.DIANXIUMEI, new DianxiumeiImpl());
        map.put(JsoupSource.XIAOKANBA, new XiaokanbaImpl());
        map.put(JsoupSource.KMAO, new KmaoImpl());
    }

    private MethodSource method;
    private JsoupSource jsoup;

    public JsoupResponseCoverter(MethodSource method, JsoupSource jsoup) {
        this.method = method;
        this.jsoup = jsoup;
    }

    @Override
    protected Object convertString(String str) throws IOException {
        IVideoParser videoParser = map.get(jsoup);
        if (videoParser == null) {
            throw new IOException("Parser not found");
        }
        Object object = null;
        if (method == MethodSource.HOME) {
            object = videoParser.home(str);
        } else if (method == MethodSource.SEARCH) {
            object = videoParser.search(str);
        } else if (method == MethodSource.DETAILS) {
            object = videoParser.details(str);
        } else if (method == MethodSource.PLAYURL) {
            object = videoParser.playUrl(str);
        } else if (method == MethodSource.MORE) {
            object = ((IVideoMoreParser) videoParser).more(str);
        } else {
            IBangumiParser bangumiParser = (IBangumiParser) videoParser;
            if (method == MethodSource.RANKING) {
                object = bangumiParser.ranking(str);
            } else if (method == MethodSource.TIME_LINE) {
                object = bangumiParser.timeLine(str);
            }
        }
        return object;
    }

}
