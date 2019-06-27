package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.TepianService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * TepianImpl
 * Created by fanchen on 2018/4/19.
 */
public class TepianImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
       return search(retrofit,baseUrl,html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot)home(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.globalPicList.threeList.clearfix > li")) {
                String title = n.attr("div > a > img", "alt");
                String cover = n.attr("div > a > img", "src");
                if (TextUtils.isEmpty(cover))  continue;
                String hd = n.text("div > a > span");
                String area = n.text("div > span.sDes");
                String url = baseUrl + n.attr("div > a", "href");
                Video video = new Video();
                video.setServiceClass(TepianService.class.getName());
                video.setHasDetails(true);
                video.setCover(cover);
                video.setId(url);
                video.setDanmaku(area);
                video.setTitle(title);
                video.setUrl(url);
                video.setExtras(hd);
                videos.add(video);
            }
            home.setList(videos);
            home.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        details.setServiceClass(TepianService.class.getName());
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("ul.picTxtA.guessLike.clearfix > li")) {
                String title = n.attr("div > img", "alt");
                String cover = n.attr("div > img", "src");
                if (TextUtils.isEmpty(cover))  continue;
                String hd = n.text("div > span.sName");
                String score = n.text("div > span.sDes");
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setCover(cover);
                video.setServiceClass(TepianService.class.getName());
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(score);
                video.setExtras(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div#play > div.tabList");
            for (Node n : node.list("div#play > div > span")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(TepianService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text().replace("高清云播线路，无广告、无插件【推荐】，[支持手机]","") + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count++;
            }
            details.setCover(node.attr("a#detail_play_pic", "src"));
            details.setLast(node.textAt("span.sDes", 1));
            details.setExtras(node.textAt("span.sDes",0));
            details.setDanmaku(node.text("span.sSource"));
            details.setTitle(node.text("span.sName"));
            details.setIntroduce(node.text("p.pIntroTxt"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {//[]
            String match = JavaScriptUtil.match("VideoInfoList=\"[$.:/\\w\\d#\\u4e00-\\u9fa5\\-]+\"", html, 0, 15, 1);
            LogUtil.e("playUrl","match => " + match);
            String[] split = match.split("\\$\\$\\$");
            String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
            if (split.length > Integer.valueOf(splitUrl[1])) {
                String[] urls = split[Integer.valueOf(splitUrl[1])].split("\\$\\$");
                for (int j = 1; j < urls.length; j += 2) {
                    String[] ids = urls[j].split("#");
                    for (int k = 0; k < ids.length; k++) {
                        if (k == Integer.valueOf(splitUrl[2].replace(".html", ""))) {
                            String[] strings = ids[k].split("\\$");
                            Map<String,String> map = new HashMap<>();
                            if(strings[1].startsWith("ftp:") || strings[1].startsWith("xg:")){
                                map.put(strings[0],strings[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                            }else if(strings[1].contains(".m3u8")){
                                map.put(strings[0],strings[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                playUrl.setUrlType(IPlayUrls.URL_M3U8);
                            }else if(!strings[1].startsWith("http")){
                                String format = String.format("http://www.itepian.com/api/opentv.php?v=%s&hd=2&E-mail=88888888", strings[1]);
                                Map<String, String> header = new HashMap<>();
                                header.put("Referer", " http://www.itepian.com/js/player/opentv.html");
                                header.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
                                header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                                header.put("Accept-Encoding", "gzip, deflate");
                                String s = StreamUtil.url2String(format, header);
                                String attr = new Node(s).attr("iframe", "src");
                                LogUtil.e("playUrl", "attr => " + attr);
                                map.put(strings[0], attr);
                                playUrl.setReferer(format);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                playUrl.setUrlType(IPlayUrls.URL_WEB);
                            } else {
                                map.put(strings[0],strings[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                playUrl.setUrlType(IPlayUrls.URL_WEB);
                            }
                            playUrl.setUrls(map);
                            playUrl.setSuccess(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
