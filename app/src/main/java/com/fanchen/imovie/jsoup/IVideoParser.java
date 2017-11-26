package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/9/23.
 */
public interface IVideoParser {

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html);

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IHomeRoot home(Retrofit retrofit,String baseUrl,String html) ;

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IVideoDetails details(Retrofit retrofit,String baseUrl,String html);

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html);

    int[] SEASON = {
            R.drawable.bangumi_home_ic_season_1,
            R.drawable.bangumi_home_ic_season_2,
            R.drawable.bangumi_home_ic_season_3,
            R.drawable.bangumi_home_ic_season_4,
    };

    int[] RANK_SEASON = {
            R.drawable.ic_rank_1,
            R.drawable.ic_rank_2,
            R.drawable.ic_rank_3,
            R.drawable.ic_rank_4,
            R.drawable.ic_rank_5,
            R.drawable.ic_rank_6,
            R.drawable.ic_rank_7,
            R.drawable.ic_rank_8,
            R.drawable.ic_rank_9,
            R.drawable.ic_rank_10
    };

    int[] TIME_SEASON = {
            R.drawable.bangumi_timeline_ic_weekday_0,
            R.drawable.bangumi_timeline_ic_weekday_1,
            R.drawable.bangumi_timeline_ic_weekday_2,
            R.drawable.bangumi_timeline_ic_weekday_3,
            R.drawable.bangumi_timeline_ic_weekday_4,
            R.drawable.bangumi_timeline_ic_weekday_5,
            R.drawable.bangumi_timeline_ic_weekday_6,
    };
}
