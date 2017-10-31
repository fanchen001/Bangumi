package com.fanchen.imovie.entity.face;

import com.fanchen.imovie.entity.bmob.VideoCollect;

import java.util.List;

/**
 * Created by fanchen on 2017/9/25.
 */
public interface IVideoDetails extends IVideo,IRoot{

    /**
     *
     * @return
     */
    String getIntroduce();

    /**
     *
     * @return
     */
    List<? extends IVideoEpisode> getEpisodes();

    /**
     *
     * @return
     */
    List<? extends IVideo> getRecoms();

    /**
     *
     * @param video
     */
    IVideoDetails setVideo(IVideo video);

    /**
     *
     * @param video
     * @return
     */
    IVideoDetails setVideo(VideoCollect video);

    /**
     * video是否支持下载
     * @return
     */
    boolean canDownload();
}
