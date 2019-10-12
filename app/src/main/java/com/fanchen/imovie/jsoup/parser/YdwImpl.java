package com.fanchen.imovie.jsoup.parser;

import android.text.Html;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.service.YdwService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class YdwImpl implements IVideoParser {

    private CcyImpl impl = new CcyImpl(YdwService.class.getName());

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        VideoHome search = (VideoHome) impl.search(retrofit, baseUrl, html);
        List<Video> list = (List<Video>) search.getList();
        if(list != null && !list.isEmpty()){
            for (Video v : list){
                v.setTitle(Html.fromHtml(v.getTitle()).toString());
            }
        }
        return search;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return impl.home(retrofit,baseUrl,html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        List<VideoEpisode> episodes = new ArrayList<>();
        Node node = new Node(html);
        List<Node> li = node.list("ul.nav.nav-tabs.pull-right > li");
        List<Node> div = node.list("div.tab-content.stui-pannel_bd.col-pd > div > ul.stui-content__playlist.clearfix");
        int count = 0;
        if(li.size() == div.size()){
            for (Node n : div){
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(YdwService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    episode.setTitle(li.get(count).text() + "_" + sub.text());
                    episodes.add(episode);
                }
                count++;
            }
        }
        VideoDetails details = (VideoDetails) impl.details(retrofit, baseUrl, html);
        details.setEpisodes(episodes);
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        return impl.playUrl(retrofit,baseUrl,html);
    }
}
