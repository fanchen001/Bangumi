package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.ZhandiService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 战地视频
 * Created by fanchen on 2017/12/23.
 */
public class ZhandiImpl implements IVideoMoreParser {

    private SmdyImpl smdy = new SmdyImpl(ZhandiService.class.getName());

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return smdy.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return smdy.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        return smdy.details(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        Map<String, String> urlMap = new HashMap<>();
        urls.setReferer(baseUrl);
        urls.setUrls(urlMap);
        try {
            String regex = "\\{[\\w\\d`~!@#$%^&*_\\-+=<>?:\"|,.\\\\ \\/;']+\\}";
            String match = JavaScriptUtil.match(regex, html, 0);
            JSONObject jsonObject = new JSONObject(match);
            String url = jsonObject.getString("url");
            if (url.contains(".m3u8") ) {
                urlMap.put("标清", url);
                urls.setUrlType(VideoPlayUrls.URL_M3U8);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                urls.setSuccess(true);
            } else if(url.contains(".mp4") || url.contains(".wmv") || url.contains(".mkv")
                    || url.contains(".3gp") || url.contains(".avi")){
                urlMap.put("标清", url);
                urls.setUrlType(VideoPlayUrls.URL_FILE);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                urls.setSuccess(true);
            }else if(url.contains("share")){
                URL base = new URL(url);
                String s = StreamUtil.url2String(url);
                String reg = "var main = \"[\\w\\d`~!@#$%^&*_\\-+=<>?:\"|,.\\\\ \\/;']+\";";
                String mat = JavaScriptUtil.match(reg, s, 0, 12, 2);
                if(mat.startsWith("http")){
                }else if(mat.startsWith("//")){
                    mat = base.getProtocol() + ":" + mat;
                }else  if(mat.startsWith("/")){
                    mat = base.getProtocol() + "://" + base.getHost()  + mat;
                }else{
                    mat = base.getProtocol() + "://" + base.getHost() + "/"  + mat;
                }
                urlMap.put("标清", mat);
                urls.setUrlType(VideoPlayUrls.URL_M3U8);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                urls.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return smdy.more(retrofit, baseUrl, html);
    }

//    @Override
//    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
//        throw new RuntimeException("this method not impl");
//    }
//
//    @Override
//    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("ul#data_list > li")) {
//                String title = n.text("div > span.sTit");
//                String cover = n.attr("img", "data-src");
//                if (TextUtils.isEmpty(cover))
//                    continue;
//                String score = n.textAt("div > p > span", 0);
//                String author = n.textAt("div > p > span", 1);
//                String hd = n.textAt("div > p > span", 2);
//                String area = n.text("div > p > span", 3);
//                String url = baseUrl + n.attr("a", "href");
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(ZhandiService.class.getName());
//                video.setCover(cover);
//                video.setId(url);
//                video.setType(area);
//                video.setDanmaku(score);
//                video.setExtras(author);
//                video.setTitle(TextUtils.isEmpty(title) ? n.text("h2") : title);
//                video.setUrl(url);
//                video.setType(hd);
//                videos.add(video);
//            }
//            home.setList(videos);
//            home.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<Node> list = node.list("div.mod_a.globalPadding");
//            if (list == null || list.size() <= 2) {
//                List<Video> videos = new ArrayList<>();
//                for (Node n : node.list("ul#data_list > li")) {
//                    String title = n.text("div > a > span.sTit");
//                    String cover = n.attr("div > a > img", "data-src");
//                    if (TextUtils.isEmpty(cover))
//                        continue;
//                    String update = n.attr("div > a > img", "alt");
//                    String url = baseUrl + n.attr("a", "href");
//                    Video video = new Video();
//                    video.setHasDetails(true);
//                    video.setServiceClass(ZhandiService.class.getName());
//                    video.setCover(cover);
//                    video.setId(url);
//                    video.setTitle(title);
//                    video.setUrl(url);
//                    video.setType(update);
//                    videos.add(video);
//                }
//                home.setList(videos);
//            } else {
//                int count = 0;
//                List<VideoTitle> titles = new ArrayList<>();
//                home.setHomeResult(titles);
//                for (Node n : list) {
//                    Node first = n.first("div.th_a");
//                    String topTitle = first.text("span");
//                    String topUrl = first.attr("a", "href");
//                    String topId = first.attr("i > a", "href", "/", 1);
//                    List<Video> videos = new ArrayList<>();
//                    VideoTitle videoTitle = new VideoTitle();
//                    videoTitle.setTitle(topTitle);
//                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
//                    videoTitle.setId(topId);
//                    videoTitle.setUrl(topUrl);
//                    videoTitle.setList(videos);
//                    videoTitle.setMore(false);
//                    videoTitle.setServiceClass(ZhandiService.class.getName());
//                    for (Node sub : n.first("div.tb_a").list("ul > li")) {
//                        String title = sub.text("div > a > span.sTit");
//                        String cover = sub.attr("div > a > div > img", "data-src");
//                        if (TextUtils.isEmpty(cover))
//                            cover = sub.attr("div > a > img", "data-src");
//                        if (TextUtils.isEmpty(cover))
//                            continue;
//                        String hd = sub.text("div > a > div > span");
//                        String author = sub.text("div > a span.sDes");
//                        String url = baseUrl + sub.attr("a", "href");
//                        Video video = new Video();
//                        video.setHasDetails(true);
//                        video.setServiceClass(ZhandiService.class.getName());
//                        video.setCover(cover);
//                        video.setId(url);
//                        video.setDanmaku(author);
//                        video.setTitle(title);
//                        video.setUrl(url);
//                        video.setType(hd);
//                        videos.add(video);
//                    }
//                    if (videos.size() > 0)
//                        titles.add(videoTitle);
//                }
//            }
//            home.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoDetails details = new VideoDetails();
//        try {
//            List<VideoEpisode> episodes = new ArrayList<>();
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("ul#resize_list > li")) {
//                String title = n.text("div > a > span.sTit");
//                String cover = n.attr("div > a > img", "src");
//                if (TextUtils.isEmpty(cover))
//                    continue;
//                String update = n.text("div > a > span.sNum");
//                String url = baseUrl + n.attr("div > a", "href");
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(ZhandiService.class.getName());
//                video.setCover(cover);
//                video.setId(url);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setType(update);
//                videos.add(video);
//            }
//            int count = 0;
//            List<Node> list = node.list("dl.tab2 > dt.tabt3 > span[id]");
//            for (Node n : node.list("dd > ul.ulNumList.clearfix.list_1")) {
//                for (Node sub : n.list("li")) {
//                    VideoEpisode episode = new VideoEpisode();
//                    episode.setServiceClass(ZhandiService.class.getName());
//                    episode.setId(baseUrl + sub.attr("a", "href"));
//                    episode.setUrl(baseUrl + sub.attr("a", "href"));
//                    if (list.size() > count) {
//                        episode.setTitle(list.get(count).text() + "_" + sub.text());
//                    } else {
//                        episode.setTitle(sub.text());
//                    }
//                    if(episode.getTitle().contains("迅雷")) {
//                        episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
//                        String replace = episode.getUrl().replace(baseUrl, "");
//                        episode.setUrl(replace);
//                        episodes.add(episode);
//                    }else if (!episode.getTitle().contains("网盘")
//                            && !episode.getTitle().contains("奇艺") && !episode.getTitle().contains("腾讯")
//                            &&!episode.getTitle().contains("优酷")&&!episode.getTitle().contains("芒果")) {
//                        episodes.add(episode);
//                    }
//                }
//                count++;
//            }
//            details.setServiceClass(ZhandiService.class.getName());
//            details.setCover(node.attr("div.posterPic > img", "src"));
//            details.setLast(node.textAt("div.introTxt > div > span", 0));
//            details.setExtras(node.textAt("div.introTxt > div > span", 1));
//            details.setDanmaku(node.textAt("div.introTxt > div > span", 2));
//            details.setTitle(node.text("div.introTxt > h1"));
//            details.setIntroduce(node.text("p#movie_info_intro_s"));
//            details.setEpisodes(episodes);
//            details.setRecomm(videos);
//            details.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
//        VideoPlayUrls urls = new VideoPlayUrls();
//        Map<String, String> url = new HashMap<>();
//        urls.setUrls(url);
//        try {
//            String match = JavaScriptUtil.match("\\{\"[ -=?/.,:@|\\w\\d\"\\[\\]\\{\\}\\\\]+\\}", html, 0);
//            if (!TextUtils.isEmpty(match)) {
//                JSONArray data = new JSONObject(match).getJSONArray("Data");
//                for (int i = 0; i < data.length(); i++) {
//                    String playname = data.getJSONObject(i).getString("playname");
//                    JSONArray playurls = data.getJSONObject(i).getJSONArray("playurls");
//                    for (int j = 0; j < playurls.length(); j++) {
//                        JSONArray jsonArray = playurls.getJSONArray(j);
//                        String sm_url = jsonArray.getString(1);
//                        String sm_player = jsonArray.getString(2);
//                        if (sm_player != null && RetrofitManager.REQUEST_URL.contains(sm_player)) {
//                            if (playname.contains("qq") && !sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_WEB);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                url.put("标清", String.format("https://player.quankan.tv/playm3u8/index.php?vid=%s&type=qq", sm_url));
//                            } else if (playname.contains("youku") && !sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_WEB);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                url.put("标清", String.format("http://player.quankan.tv/playm3u8/index.php?vid=%s&type=youku", sm_url));
//                            }  else if (playname.contains("mgtv") && !sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_WEB);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                url.put("标清", String.format("http://player.quankan.tv/playm3u8/index.php?vid=%s&type=mgtv", sm_url));
//                            } else if (playname.contains("qiyi") && !sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_WEB);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                url.put("标清", String.format("http://player.quankan.tv/playm3u8/index.php?vid=%s&type=iqiyi", sm_url));
//                            }else if (playname.contains("m3u") && sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_M3U8);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                                url.put("标清", sm_url);
//                            } else if (playname.contains("33uu") || sm_url.contains("letv")) {
//                                urls.setUrlType(VideoPlayUrls.URL_FILE);
//                                if (sm_url.startsWith("ftp")) {
//                                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
//                                } else if (sm_url.contains("yuboyun")) {
//                                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                } else {
//                                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_ZZPLAYER);
//                                }
//                                url.put("标清", sm_url);
//                            } else if (playname.contains("zyp") && sm_url.startsWith("http")) {
//                                urls.setUrlType(VideoPlayUrls.URL_M3U8);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                                url.put("标清", sm_url);
//                            } else if (sm_url.startsWith("http")) {
//                                url.put("标清", sm_url);
//                                urls.setUrlType(VideoPlayUrls.URL_FILE);
//                                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                            }
//                        }
//                    }
//                }
//            }
//            urls.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        VideoPlayUrls iPlayUrls = (VideoPlayUrls) new SmdyImpl().playUrl(retrofit, baseUrl, html);
////        LogUtil.e("zhandi playUrl ", "getPlayType ===> " + iPlayUrls.getPlayType());
////        LogUtil.e("zhandi playUrl ", "getUrlType ===> " + iPlayUrls.getUrlType());
//        return urls;
//    }

}
