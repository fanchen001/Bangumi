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
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.ImageUtil;

import java.lang.reflect.Field;

/**
 * @author fanchen
 */
public class RoundCornerImageView extends ImageView {

    public RoundCornerImageView(Context context) {
        super(context);

    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

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
//        Matrix matrix = new Matrix();
//        //获取图片的宽高
//        int dwidth = drawable.getIntrinsicWidth();
//        int dheight = drawable.getIntrinsicHeight();
//        int vwidth =getWidth();
//        int vheight = getHeight();
//        float scale;
//        float dx = 0, dy = 0;
//
//        if (dwidth * vheight > vwidth * dheight) {
//            scale = (float) vheight / (float) dheight;
//            dx = (vwidth - dwidth * scale) * 0.5f;
//        } else {
//            scale = (float) vwidth / (float) dwidth;
//            dy = (vheight - dheight * scale) * 0.5f;
//        }
//        matrix.setScale(scale, scale);
//        matrix.postTranslate(Math.round(dx), Math.round(dy));

//        canvas.concat(matrix);
//        drawable.draw(canvas);
//        canvas.restore();


        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        int w = getWidth();
        int h = getHeight();
        RectF rectF = new RectF(0, 0, w, h);
        Bitmap roundBitmap = getCroppedBitmap(bitmap, w,h, roundPx);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int length, int h,int roundPx) {
        Bitmap sbmp = ImageUtil.getScaleBitmap(bmp, getWidth(), getHeight());
        Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
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



        return output;
    }
}

