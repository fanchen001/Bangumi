package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.service.ZzzvzService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

/**
 * ZzzvzImpl
 * Created by fanchen on 2018/7/27.
 */
public class ZzzvzImpl implements IVideoMoreParser{
    private KankanwuImpl smdy = new KankanwuImpl(ZzzvzService.class.getName(),false);

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return smdy.search(retrofit,baseUrl,html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return smdy.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        VideoDetails details = (VideoDetails) smdy.details(retrofit, baseUrl, html);
        List<VideoEpisode> episodes = (List<VideoEpisode>) details.getEpisodes();
        if(episodes != null && episodes.size() > 0){
            List<VideoEpisode> newEpisodes = new ArrayList<>();
            for (VideoEpisode episode : episodes){
                if(!episode.getTitle().contains("极速1")){
                    newEpisodes.add(episode);
                }
            }
            details.setEpisodes(newEpisodes);
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        return smdy.playUrl(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return smdy.more(retrofit, baseUrl, html);
    }
}
