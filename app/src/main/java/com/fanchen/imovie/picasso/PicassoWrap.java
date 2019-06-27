package com.fanchen.imovie.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.picasso.download.HttpsDownLoader;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.LogUtil;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * PicassoWrap
 * Created by fanchen on 2017/9/18.
 */
public class PicassoWrap {

    private Picasso picasso;

    public Picasso getPicasso(){
        return picasso;
    }

    /**
     *
     * @param context
     */
    public PicassoWrap(Context context){
        File cacheDir = AppUtil.getExternalCacheDir(context);
        if (!cacheDir.exists())  cacheDir.mkdirs();
        //64Mb的缓存
        OkHttpClient client = new OkHttpClient.Builder().cache(new Cache(cacheDir, 64 * 1024 * 1024)).build();
        picasso = new Picasso.Builder(context).listener(new PicassoListener()).downloader(new HttpsDownLoader(client)).build();
    }

    /**
     *
     * @param context
     * @param downloader
     */
    public PicassoWrap(Context context,Downloader downloader){
        picasso = new Picasso.Builder(context).listener(new PicassoListener()).downloader(downloader).build();
    }

    public PicassoWrap(Picasso picasso){
        this.picasso = picasso;
    }
    /**
     *
     * @param cover
     * @param tag
     * @param placeholder
     * @param error
     * @param config
     * @param imageView
     */
    public void load(int cover,Object tag,int placeholder,int error,Bitmap.Config config,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(cover <= 0){
            cover = R.drawable.image_load_h_pre;
        }
        picasso.load(cover).tag(tag).placeholder(placeholder).error(error).config(config).into(imageView);
    }

    /**
     *
     * @param cover
     * @param placeholder
     * @param error
     * @param config
     * @param imageView
     */
    public void load(int cover,int placeholder,int error,Bitmap.Config config,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(cover <= 0){
            cover = R.drawable.image_load_h_pre;
        }
        picasso.load(cover).placeholder(placeholder).error(error).config(config).into(imageView);
    }

    public void load(int cover,Object tag,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(cover <= 0){
            cover = R.drawable.image_load_h_pre;
        }
        picasso.load(cover).tag(tag).config(Bitmap.Config.RGB_565).into(imageView);
    }
    /**
     *
     * @param cover
     * @param tag
     * @param config
     * @param imageView
     */
    public void load(int cover,Object tag,Bitmap.Config config,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(cover <= 0){
            cover = R.drawable.image_load_h_pre;
        }
        picasso.load(cover).tag(tag).config(config).into(imageView);
    }
    /**
     *
     * @param cover
     * @param tag
     * @param placeholder
     * @param error
     * @param config
     * @param imageView
     */
    public void load(String cover,Object tag,int placeholder,int error,Bitmap.Config config,boolean centerCrop,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(centerCrop){
            if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
                picasso.load(R.drawable.image_load_pre).tag(tag).placeholder(placeholder).error(error).config(config).fit().centerCrop().into(imageView);
            }else{
                picasso.load(cover).tag(tag).placeholder(placeholder).error(error).config(config).fit().centerCrop().into(imageView);
            }
        }else{
            if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
                picasso.load(R.drawable.image_load_pre).tag(tag).placeholder(placeholder).error(error).config(config).into(imageView);
            }else{
                picasso.load(cover).tag(tag).placeholder(placeholder).error(error).config(config).into(imageView);
            }
        }
    }

    /**
     *
     * @param cover
     * @param placeholder
     * @param error
     * @param config
     * @param imageView
     */
    public void load(String cover,int placeholder,int error,Bitmap.Config config,ImageView imageView){
        if(picasso == null || imageView == null)return;
        if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
            picasso.load(R.drawable.image_load_pre).placeholder(placeholder).error(error).config(config).fit().centerCrop().into(imageView);
        }else{
            picasso.load(cover).placeholder(placeholder).error(error).config(config).fit().centerCrop().into(imageView);
        }
    }

    /**
     *
     * @param cover
     * @param tag
     * @param config
     * @param imageView
     */
    public void load(String cover,Object tag,Bitmap.Config config,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
            picasso.load(R.drawable.image_load_pre).tag(tag).config(config).fit().centerCrop().into(imageView);
        }else{
            picasso.load(cover).tag(tag).config(config).fit().centerCrop().into(imageView);
        }
    }

    /**
     *
     * @param cover
     * @param tag
     * @param imageView
     */
    public void load(String cover,Object tag,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
            picasso.load(R.drawable.image_load_pre).tag(tag).config(Bitmap.Config.RGB_565).fit().centerCrop().into(imageView);
        }else{
            picasso.load(cover).tag(tag).config(Bitmap.Config.RGB_565).fit().centerCrop().into(imageView);
        }
    }

    public void load(String cover,ImageView imageView){
        if(picasso == null|| imageView == null)return;
        if(TextUtils.isEmpty(cover) || cover.trim().length() == 0){
            picasso.load(R.drawable.image_load_pre).config(Bitmap.Config.RGB_565).fit().centerCrop().into(imageView);
        }else{
            picasso.load(cover).config(Bitmap.Config.RGB_565).fit().centerCrop().into(imageView);
        }
    }

    /**
     *
     * @param cover
     * @param imageView
     */
    public void loadVertical(String cover,ImageView imageView){
        load(cover, R.drawable.image_load_h_pre, R.drawable.image_load_h_error, Bitmap.Config.RGB_565, imageView);
    }

    /**
     *
     * @param cover
     * @param tag
     * @param config
     * @param imageView
     */
    public void loadVertical(String cover,Object tag,Bitmap.Config config,ImageView imageView){
        load(cover, tag, R.drawable.image_load_h_pre, R.drawable.image_load_h_error, config,true, imageView);
    }

    /**
     *
     * @param cover
     * @param tag
     * @param imageView
     */
    public void loadVertical(String cover,Object tag,ImageView imageView){
        load(cover, tag, R.drawable.image_load_h_pre, R.drawable.image_load_h_error, Bitmap.Config.RGB_565, true,imageView);
    }

    public void loadVertical(String cover,Object tag,boolean centerCrop,ImageView imageView){
        load(cover, tag, R.drawable.image_load_h_pre, R.drawable.image_load_h_error, Bitmap.Config.RGB_565,centerCrop, imageView);
    }

    /**
     *
     * @param cover
     * @param imageView
     */
    public void loadHorizontal(String cover,ImageView imageView){
        load(cover, R.drawable.image_load_pre, R.drawable.image_load_error, Bitmap.Config.RGB_565, imageView);
    }

    /**
     *
     * @param cover
     * @param tag
     * @param config
     * @param imageView
     */
    public void loadHorizontal(String cover,Object tag,Bitmap.Config config,ImageView imageView){
        load(cover,tag, R.drawable.image_load_pre,R.drawable.image_load_error,config,true,imageView);
    }

    /**
     *
     * @param cover
     * @param tag
     * @param imageView
     */
    public void loadHorizontal(String cover,Object tag,ImageView imageView){
        load(cover, tag, R.drawable.image_load_pre, R.drawable.image_load_error, Bitmap.Config.RGB_565,true, imageView);
    }
}
