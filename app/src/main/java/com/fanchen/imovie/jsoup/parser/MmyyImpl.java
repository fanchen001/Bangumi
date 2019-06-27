package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.MmyyService;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

/**
 * MmyyImpl
 * Created by fanchen on 2017/10/28.
 */
public class MmyyImpl implements IVideoMoreParser {

    private KankanwuImpl kankanwu = new KankanwuImpl(MmyyService.class.getName(), false);

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.more(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        String idName = "";
        try{
            String[] split = RetrofitManager.PATH_ID.split("/");
            idName = split[split.length - 1];
        }catch (Throwable e){
            e.printStackTrace();
        }

        LogUtil.e("MmyyImpl","idName -> " + idName);

        VideoDetails details = (VideoDetails) kankanwu.details(retrofit, baseUrl, html);
        List<VideoEpisode> episodes = (List<VideoEpisode>) details.getEpisodes();
        if (episodes == null) return details;
        List<VideoEpisode> newEpisodes = new ArrayList<>();
        for (VideoEpisode episode : episodes) {
            if(episode.getTitle().contains("迅雷")) {
                episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
                String replace = episode.getUrl().replace(baseUrl, "");
                episode.setUrl(replace);
                episodes.add(episode);
            }else if (!episode.getTitle().contains("网盘") &&
                    !episode.getTitle().contains("奇艺") && !episode.getTitle().contains("腾讯") &&
                    !episode.getTitle().contains("优酷") && !episode.getTitle().contains("芒果")) {
                String id = episode.getId();
                String url = episode.getUrl();
                if(!id.contains(idName)){
                    episode.setId(id.replace("//","/" + idName + "/"));
                }
                if(!url.contains(idName)){
                    episode.setUrl(url.replace("//","/" + idName + "/"));
                }
                newEpisodes.add(episode);
            }
        }
        details.setEpisodes(newEpisodes);
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.playUrl(retrofit, baseUrl, html);
    }

//    private static final String URL_MAT = "https://ckplayer.jjddyy.com/jx.php?id=%s";
//
//    @Override
//    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            home.setList(videos);
//            for (Node n : node.list("ul#resize_list > li")){
//                String title = n.text("a > div > label.name");
//                String cover = n.attr("a > div > img", "src");
//                String clazz = n.textAt("div.list_info > p", 0);
//                String type = n.textAt("div.list_info > p", 1);
//                String author = n.textAt("div.list_info > p", 2);
//                String url = baseUrl + n.attr("a","href");
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(MmyyService.class.getName());
//                video.setCover(cover);
//                video.setId(url);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setAuthor(author);
//                video.setClazz(clazz);
//                video.setType(type);
//                videos.add(video);
//            }
//            home.setSuccess(true);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<Node> list = node.list("div[class^=modo_title]");
//            if(list != null && list.size() > 2){
//                List<Node> ullist = node.list("ul.focusList > li.con");
//                if(ullist != null && ullist.size() > 0){
//                    List<VideoBanner> banners = new ArrayList<>();
//                    for (Node n : ullist){
//                        VideoBanner banner = new VideoBanner();
//                        banner.setCover(n.attr("a > img", "src"));
//                        banner.setId(baseUrl + n.attr("a", "href"));
//                        banner.setTitle(n.text("a > span"));
//                        banner.setUrl(baseUrl + n.attr("a", "href"));
//                        banner.setServiceClass(MmyyService.class.getName());
//                        banners.add(banner);
//                    }
//                    home.setHomeBanner(banners);
//                }
//                int count = 0;
//                List<VideoTitle> titles = new ArrayList<>();
//                home.setHomeResult(titles);
//                for (Node n : list){
//                    String topTitle = n.text("h2");
//                    String topUrl = n.attr("i > a", "href");
//                    String topId = n.attr("i > a", "href","/",2).replace(".html", "");
//                    List<Video> videos = new ArrayList<>();
//                    VideoTitle videoTitle = new VideoTitle();
//                    videoTitle.setTitle(topTitle);
//                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
//                    videoTitle.setId(topId);
//                    videoTitle.setUrl(topUrl);
//                    videoTitle.setList(videos);
//                    videoTitle.setServiceClass(MmyyService.class.getName());
//                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")){
//                        String title = sub.text("a > div > label.name");
//                        if(TextUtils.isEmpty(title))
//                            title = sub.text("h2");
//                        String cover = sub.attr("a > div > img", "data-original");
//                        if(TextUtils.isEmpty(cover))
//                            continue;
//                        String hd = sub.text("a > div > label.title");
//                        String score = n.text("a > div > label.score");
//                        String url = baseUrl + sub.attr("a","href");
//                        Video video = new Video();
//                        video.setHasDetails(true);
//                        video.setServiceClass(MmyyService.class.getName());
//                        video.setCover(cover);
//                        video.setId(url);
//                        video.setAuthor(score);
//                        video.setTitle(title);
//                        video.setUrl(url);
//                        video.setType(hd);
//                        videos.add(video);
//                    }
//                    if(videos.size() > 0)
//                        titles.add(videoTitle);
//                    videoTitle.setMore(videoTitle.getList().size() == 6);
//                }
//            }else{
//                List<Video> videos = new ArrayList<>();
//                for (Node n : node.list("ul#resize_list > li")){
//                    String title = n.text("h2");
//                    String cover = n.attr("a > div > img", "src");
//                    if(TextUtils.isEmpty(cover))
//                        continue;
//                    String hd = n.text("a > div > label.title");
//                    String score = n.text("a > div > label.score");
//                    String url = baseUrl + n.attr("a","href");
//                    Video video = new Video();
//                    video.setHasDetails(true);
//                    video.setServiceClass(MmyyService.class.getName());
//                    video.setCover(cover);
//                    video.setId(url);
//                    video.setAuthor(score);
//                    video.setTitle(title);
//                    video.setUrl(url);
//                    video.setType(hd);
//                    videos.add(video);
//                }
//                home.setList(videos);
//            }
//            home.setSuccess(true);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
//        Node node = new Node(html);
//        VideoDetails details = new VideoDetails();
//        try {
//            List<VideoEpisode> episodes = new ArrayList<>();
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("ul.list_tab_img > li")){
//                String title = n.text("h2");
//                String cover = n.attr("a > div > img", "src");
//                if(TextUtils.isEmpty(cover))
//                    continue;
//                String hd = n.text("a > div > label.title");
//                String score = n.text("a > div > label.score");
//                String url = baseUrl + n.attr("a","href");
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(MmyyService.class.getName());
//                video.setCover(cover);
//                video.setId(url);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setAuthor(score);
//                video.setType(hd);
//                videos.add(video);
//            }
//            int count = 0;
//            List<Node> list = node.list("div.play-title > span");
//            for (Node n : node.list("ul.plau-ul-list")){
//                for (Node sub : n.list("li")){
//                    VideoEpisode episode = new VideoEpisode();
//                    episode.setServiceClass(MmyyService.class.getName());
//                    episode.setId(baseUrl + sub.attr("a", "href"));
//                    episode.setUrl(baseUrl + sub.attr("a", "href"));
//                    if(list.size() > count){
//                        episode.setTitle(list.get(count).text() + "_" + sub.text());
//                    }else{
//                        episode.setTitle(sub.text());
//                    }
//                    if(!episode.getTitle().contains("迅雷") && !episode.getTitle().contains("西瓜")&& !episode.getTitle().contains("网盘")){
//                        episodes.add(episode);
//                    }
//                }
//                count ++ ;
//            }
//            details.setServiceClass(MmyyService.class.getName());
//            details.setCover(node.attr("div.vod-n-img > img.loading", "src"));
//            details.setClazz(node.textAt("div.vod-n-l > p", 0));
//            details.setType(node.textAt("div.vod-n-l > p", 1));
//            details.setAuthor(node.textAt("div.vod-n-l > p",2));
//            details.setTitle(node.text("div.vod-n-l > h1"));
//            details.setIntroduce(node.text("div.vod_content"));
//            details.setEpisodes(episodes);
//            details.setRecomm(videos);
//            details.setSuccess(true);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
//        VideoPlayUrls playUrl = new VideoPlayUrls();
//        try{
//            String match = JavaScriptUtil.match("unescape\\([\"%\\w\\d$]+\"\\)", html, 0);
//            String evalDecrypt = JavaScriptUtil.evalDecrypt(match);
//            String[] split = evalDecrypt.split("\\$\\$\\$");
//            String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
//            if (split.length > Integer.valueOf(splitUrl[1])) {
//                String[] urls = split[Integer.valueOf(splitUrl[1])].split("\\$\\$");
//                for (int j = 1; j < urls.length; j += 2) {
//                    String[] ids = urls[j].split("#");
//                    for (int k = 0; k < ids.length; k++) {
//                        if (k == Integer.valueOf(splitUrl[2].replace(".html", ""))) {
//                            String[] strings = ids[k].split("\\$");
//                            Map<String,String> map = new HashMap<>();
//                            if(strings[1].startsWith("ftp://") || strings[1].startsWith("xg://")){
//                                map.put(strings[0],strings[1]);
//                            }else{
//                                map.put(strings[0],String.format(URL_MAT,strings[1]));
//                            }
//                            playUrl.setUrls(map);
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                            playUrl.setUrlType(IPlayUrls.URL_WEB);
//                            playUrl.setSuccess(true);
//                        }
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return playUrl;
//    }
//
//    @Override
//    public IBangumiMoreRoot more(Retrofit retrofit,String baseUrl,String html) {
//        return (IBangumiMoreRoot)home(retrofit,baseUrl,html);
//    }

}
