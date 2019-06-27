package com.fanchen.imovie.picasso;

import android.net.Uri;

import com.fanchen.imovie.util.LogUtil;
import com.squareup.picasso.Picasso;

public class PicassoListener implements Picasso.Listener {

    @Override
    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
        LogUtil.e("PicassoListener", " -------- onImageLoadFailed start -------" );
        LogUtil.e("PicassoListener", " url -> "  + uri.toString());
        LogUtil.e("PicassoListener", " exception -> " + exception.toString());
        LogUtil.e("PicassoListener", " -------- onImageLoadFailed end -------" );
    }

}
