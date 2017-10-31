package com.fanchen.imovie.util;

import android.text.TextUtils;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.SoftReference;

/**
 * Created by fanchen on 2017/7/26.
 */
public class ShareUtil {

    /**
     *
     * @param baseActivity
     * @param title
     * @param content
     * @param url
     */
    public static void share(BaseActivity baseActivity,String title,String content,String url){
        ShareAction shareAction = new ShareAction(baseActivity);
        shareAction.setShareboardclickCallback(new Boardlistener(baseActivity,title,content,url,false));
        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.EMAIL, SHARE_MEDIA.MORE);
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        shareAction.open(config);
    }

    /**
     *
     * @param baseActivity
     * @param content
     */
    public static void share(BaseActivity baseActivity,String content){
        ShareAction shareAction = new ShareAction(baseActivity);
        shareAction.setShareboardclickCallback(new Boardlistener(baseActivity,null,content,null,true));
        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.EMAIL, SHARE_MEDIA.MORE);
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        shareAction.open(config);
    }

    /**
     *
     * @param baseActivity
     * @param title
     * @param content
     */
    public static void share(BaseActivity baseActivity,String title,String content){
        share(baseActivity, title, content, null);
    }

    /**
     *
     * @param baseActivity
     */
    public static void share(BaseActivity baseActivity){
        share(baseActivity,null,null);
    }

    private static class Boardlistener implements ShareBoardlistener {

        private SoftReference<BaseActivity> mActivity;
        private boolean withText;
        private String title;
        private String content;
        private String url;

        private Boardlistener(BaseActivity activity,String title,String content,String url,boolean withText) {
            mActivity = new SoftReference(activity);
            this.withText = withText;
            this.content = TextUtils.isEmpty(content) ? activity.getString(R.string.def_content) : content;
            this.title = TextUtils.isEmpty(title)?activity.getString(R.string.def_title) : title;
            this.url = TextUtils.isEmpty(url)?activity.getString(R.string.def_url) : url;
        }

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            BaseActivity settingsActivity = mActivity.get();
            if(settingsActivity == null)return;
            if (share_media == SHARE_MEDIA.SMS || withText) {
                new ShareAction(settingsActivity).
                        withText(content).
                        withMedia(new UMImage(settingsActivity, R.drawable.ic_launcher)).
                        setPlatform(share_media).
                        setCallback(new CustomShareListener(settingsActivity)).
                        share();
            } else {
                UMWeb web = new UMWeb(url);
                web.setTitle(title);
                web.setDescription(content);
                web.setThumb(new UMImage(settingsActivity, R.drawable.ic_launcher));
                new ShareAction(settingsActivity).
                        withMedia(web).
                        setPlatform(share_media).
                        setCallback(new CustomShareListener(settingsActivity)).
                        share();
            }
        }

    };

    private static class CustomShareListener implements UMShareListener {

        private SoftReference<BaseActivity> mActivity;

        private CustomShareListener(BaseActivity activity) {
            mActivity = new SoftReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            BaseActivity settingsActivity = mActivity.get();
            if(settingsActivity == null)return;
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                settingsActivity.showSnackbar(platform + " 收藏成功啦");
            } else {
                settingsActivity.showSnackbar(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            BaseActivity settingsActivity = mActivity.get();
            if(settingsActivity == null)return;
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                settingsActivity.showSnackbar(platform + " 收藏失败啦");
            } else {
                settingsActivity.showSnackbar(platform + " 分享失败啦");
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            BaseActivity settingsActivity = mActivity.get();
            if(settingsActivity == null)return;
            settingsActivity.showSnackbar(platform + " 分享取消了");
        }
    }
}
