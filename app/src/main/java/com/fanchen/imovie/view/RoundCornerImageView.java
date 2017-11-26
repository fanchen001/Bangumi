package com.fanchen.imovie.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.ImageUtil;

import java.lang.reflect.Field;

/**
 * @author fanchen
 */
public class RoundCornerImageView extends ImageView {

    private Bitmap drawableBitmap;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        drawableBitmap = null;
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        drawableBitmap = null;
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        drawableBitmap = null;
        invalidate();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        drawableBitmap = null;
        invalidate();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        drawableBitmap = null;
        invalidate();
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        drawableBitmap = null;
        invalidate();
    }


    protected void onDraw(Canvas canvas) {
        int roundPx = DisplayUtil.dip2px(getContext(),4);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE); //这里的颜色决定了边缘的颜色
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        int w = getWidth();
        int h = getHeight();
        RectF rectF = new RectF(0, 0, w, h);
        if(drawableBitmap == null)
            drawableBitmap = getCroppedBitmap(bitmap, w,h, roundPx);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        if(drawableBitmap != null){
            canvas.drawBitmap(drawableBitmap, 0, 0, null);
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int length, int h,int roundPx) {
        Bitmap output = null;
        Bitmap sbmp = null;
        try {
            sbmp = ImageUtil.getScaleBitmap(bmp, getWidth(), getHeight());
            output = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            final RectF rectF = new RectF(6, 6, canvas.getWidth() - 6, canvas.getHeight() - 6);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);
        }catch (Throwable e){
            e.printStackTrace();
            if(output != null){
                output.recycle();
            }
            if(sbmp != null){
                sbmp.recycle();
            }
            System.gc();
            output = null;
        }
        return output;
    }
}

