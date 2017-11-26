package com.fanchen.imovie.retrofit.coverter;

import android.text.TextUtils;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.jsoup.IBangumiParser;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.parser.A4dyImpl;
import com.fanchen.imovie.jsoup.parser.BabayuImpl;
import com.fanchen.imovie.jsoup.parser.BiliplusImpl;
import com.fanchen.imovie.jsoup.parser.BumimiImpl;
import com.fanchen.imovie.jsoup.parser.DianxiumeiImpl;
import com.fanchen.imovie.jsoup.parser.Dm5Impl;
import com.fanchen.imovie.jsoup.parser.HaliHaliParser;
import com.fanchen.imovie.jsoup.parser.IKanFanParser;
import com.fanchen.imovie.jsoup.parser.JrenImpl;
import com.fanchen.imovie.jsoup.parser.K8dyImpl;
import com.fanchen.imovie.jsoup.parser.KankanwuImpl;
import com.fanchen.imovie.jsoup.parser.KmaoImpl;
import com.fanchen.imovie.jsoup.parser.LL520Impl;
import com.fanchen.imovie.jsoup.parser.MmyyImpl;
import com.fanchen.imovie.jsoup.parser.S80Impl;
import com.fanchen.imovie.jsoup.parser.TucaoImpl;
import com.fanchen.imovie.jsoup.parser.XiaokanbaImpl;
import com.fanchen.imovie.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 需要解析网页获取关心数据的时候
 * 使用JsoupResponseCoverter
 * Created by fanchen on 2017/7/18.
 */
public class JsoupResponseCoverter extends StringResponseConverter {

    private static Map<JsoupSource, IVideoParser> map = new HashMap<>();
    private MethodSource method;
    private JsoupSource jsoup;

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
        map.put(JsoupSource.A4DY, new A4dyImpl());
        map.put(JsoupSource.KANKAN, new KankanwuImpl());
        map.put(JsoupSource.BABAYU, new BabayuImpl());
        map.put(JsoupSource.K8DY, new K8dyImpl());
        map.put(JsoupSource.LL520, new LL520Impl());
        map.put(JsoupSource.HALIHALI, new HaliHaliParser());
        map.put(JsoupSource.IKANFAN, new IKanFanParser());
        map.put(JsoupSource.MMYY, new MmyyImpl());
    }

    public JsoupResponseCoverter(Retrofit retrofit, MethodSource method, JsoupSource jsoup) {
        super(retrofit);
        this.method = method;
        this.jsoup = jsoup;
    }

    @Override
    protected Object convertString(String str) throws IOException {
        IVideoParser videoParser = map.get(jsoup);
        if (videoParser == null) throw new IOException("Parser not found");
        if (retrofit == null) throw new IOException("retrofit not null");
        String baseUrl = retrofit.baseUrl().toString();
        if(TextUtils.isEmpty(baseUrl)) throw new IOException("baseUrl not null");
        Object object = null;
        LogUtil.e("jsoup","jsoup" + jsoup);
        if (method == MethodSource.HOME) {
            object = videoParser.home(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
        } else if (method == MethodSource.SEARCH) {
            object = videoParser.search(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
        } else if (method == MethodSource.DETAILS) {
            object = videoParser.details(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
        } else if (method == MethodSource.PLAYURL) {
            object = videoParser.playUrl(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
        } else if (method == MethodSource.MORE) {
            object = ((IVideoMoreParser) videoParser).more(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
        } else {
            IBangumiParser bangumiParser = (IBangumiParser) videoParser;
            if (method == MethodSource.RANKING) {
                object = bangumiParser.ranking(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
            } else if (method == MethodSource.TIME_LINE) {
                object = bangumiParser.timeLine(retrofit,baseUrl.substring(0,baseUrl.length() - 1),str);
            }
        }
        return object;
    }

}
