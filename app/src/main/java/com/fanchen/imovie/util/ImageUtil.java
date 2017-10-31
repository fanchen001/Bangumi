/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanchen.imovie.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

// TODO: Auto-generated Javadoc

/**
 * 描述：图片处理类.
 * 
 */
public class ImageUtil {

	/** 图片处理：裁剪. */
	public static final int CUTIMG = 0;

	/** 图片处理：缩放. */
	public static final int SCALEIMG = 1;

	/** 图片处理：不处理. */
	public static final int ORIGINALIMG = 2;

	/** 图片最大宽度. */
	public static final int MAX_WIDTH = 4096 / 2;

	/** 图片最大高度. */
	public static final int MAX_HEIGHT = 4096 / 2;

	private static final int ww=480;
	private static final int hh=800;
	
	/**
	 * 从互联网上获取原始大小图片.
	 * 
	 * @param url
	 *            要下载文件的网络地址
	 * @return Bitmap 新图片
	 */
	public static Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		URLConnection con = null;
		InputStream is = null;
		try {
			URL imageURL = new URL(url);
			con = imageURL.openConnection();
			con.setDoInput(true);
			con.connect();
			is = con.getInputStream();
			// 获取资源图片
			bitmap = BitmapFactory.decodeStream(is, null, null);
		} catch (Exception e) {
			LogUtil.d(ImageUtil.class, "" + e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public static void saveImageSync(final Bitmap toBitmap, final String path) {
		if (toBitmap != null) {
			String downloadPath = path;
			File dir = new File(downloadPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, System.currentTimeMillis() + ".jpg");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				CompressFormat formatv = CompressFormat.JPEG;
				int quality = 70;
				toBitmap.compress(formatv, quality, fos);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap readBitMap(Context context, int resId) {
		Options opt = new Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	/**
	 * 以最省内存的方式读取图片
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap readBitMap(String file) {
		Options opt = new Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		return BitmapFactory.decodeFile(file, opt);
	}

	/**
	 * 描述：获取原图.
	 * 
	 * @param file
	 *            File对象
	 * @return Bitmap 图片
	 */
	public static Bitmap getBitmap(File file) {
		Bitmap resizeBmp = null;
		try {
			resizeBmp = BitmapFactory.decodeFile(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resizeBmp;
	}

	/**
	 * 从互联网上获取指定大小的图片.
	 * 
	 * @param url
	 *            要下载文件的网络地址
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap getBitmap(String url, int desiredWidth,
			int desiredHeight) {
		Bitmap bitmap = null;
		URLConnection con = null;
		InputStream is = null;
		try {
			URL imageURL = new URL(url);
			con = imageURL.openConnection();
			con.setDoInput(true);
			con.connect();
			is = con.getInputStream();
			bitmap = getBitmap(is, desiredWidth, desiredHeight);
			// 超出的裁掉
			if (bitmap.getWidth() > desiredWidth
					|| bitmap.getHeight() > desiredHeight) {
				bitmap = getCutBitmap(bitmap, desiredWidth, desiredHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d(ImageUtil.class, "" + e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 从流中获取指定大小的图片.
	 * 
	 * @param inputStream
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 */
	public static Bitmap getBitmap(InputStream inputStream, int desiredWidth,
			int desiredHeight) {
		Bitmap bitmap = null;
		try {
			byte[] data = StreamUtil.stream2bytes(inputStream);
			bitmap = getBitmap(data, desiredWidth, desiredHeight);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d(ImageUtil.class, "" + e.getMessage());
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 从流中获取指定大小的图片.
	 * 
	 * @param data
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap getBitmap(byte[] data, int desiredWidth,
			int desiredHeight) {
		Bitmap bitmap = null;
		try {
			Options opts = new Options();
			// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);

			// 获取图片的原始宽度高度
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth,
					desiredHeight);
			desiredWidth = size[0];
			desiredHeight = size[1];

			// 缩放的比例
			float scale = getMinScale(srcWidth, srcHeight, desiredWidth,
					desiredHeight);
			int destWidth = srcWidth;
			int destHeight = srcHeight;
			if (scale != 0) {
				destWidth = (int) (srcWidth * scale);
				destHeight = (int) (srcHeight * scale);
			}

			// 默认为ARGB_8888.
			opts.inPreferredConfig = Config.RGB_565;
			// 以下两个字段需一起使用：
			// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
			opts.inPurgeable = true;
			// 位图可以共享一个参考输入数据(inputstream、阵列等)
			opts.inInputShareable = true;
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			int sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth,
					destHeight);
			opts.inSampleSize = sampleSize;

			// 设置大小
			opts.outWidth = destWidth;
			opts.outHeight = destHeight;

			// 创建内存
			opts.inJustDecodeBounds = false;
			// 使图片不抖动
			opts.inDither = false;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d(ImageUtil.class, "" + e.getMessage());
		}
		return bitmap;
	}

	/**
	 * 描述：缩放图片.
	 * 
	 * @param file
	 *            File对象
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap getScaleBitmap(File file, int desiredWidth,
			int desiredHeight) {

		Bitmap resizeBmp = null;
		Options opts = new Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);

		// 获取图片的原始宽度高度
		int srcWidth = opts.outWidth;
		int srcHeight = opts.outHeight;
		int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		desiredWidth = size[0];
		desiredHeight = size[1];

		// 缩放的比例
		float scale = getMinScale(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		int destWidth = srcWidth;
		int destHeight = srcHeight;
		if (scale != 0) {
			destWidth = (int) (srcWidth * scale);
			destHeight = (int) (srcHeight * scale);
		}

		// 默认为ARGB_8888.
		opts.inPreferredConfig = Config.RGB_565;
		// 以下两个字段需一起使用：
		// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
		opts.inPurgeable = true;
		// 位图可以共享一个参考输入数据(inputstream、阵列等)
		opts.inInputShareable = true;
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		int sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth,
				destHeight);
		opts.inSampleSize = sampleSize;

		// 设置大小
		opts.outWidth = destWidth;
		opts.outHeight = destHeight;

		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;

		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		// 缩小或者放大
		resizeBmp = scaleBitmap(resizeBmp, scale);
		// 超出的裁掉
		if (resizeBmp.getWidth() > desiredWidth
				|| resizeBmp.getHeight() > desiredHeight) {
			resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
		}
		return resizeBmp;
	}

	/**
	 * 描述：缩放图片.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, int desiredWidth,
			int desiredHeight) {

		if (!checkBitmap(bitmap)) {
			return null;
		}
		Bitmap resizeBmp = null;

		// 获得图片的宽高
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();

		int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		desiredWidth = size[0];
		desiredHeight = size[1];

		float scale = getMinScale(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		resizeBmp = scaleBitmap(bitmap, scale);
		// 超出的裁掉
		if (resizeBmp.getWidth() > desiredWidth
				|| resizeBmp.getHeight() > desiredHeight) {
			resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
		}
		return resizeBmp;
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param file
	 *            File对象
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap getCutBitmap(File file, int desiredWidth,
			int desiredHeight) {

		Bitmap resizeBmp = null;

		Options opts = new Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);

		// 获取图片的原始宽度
		int srcWidth = opts.outWidth;
		// 获取图片原始高度
		int srcHeight = opts.outHeight;

		int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		desiredWidth = size[0];
		desiredHeight = size[1];

		// 缩放的比例
		float scale = getMinScale(srcWidth, srcHeight, desiredWidth,
				desiredHeight);
		int destWidth = srcWidth;
		int destHeight = srcHeight;
		// 只缩小，不放大
		if (scale > 1) {
			destWidth = (int) (srcWidth * scale);
			destHeight = (int) (srcHeight * scale);
		}

		// 默认为ARGB_8888.
		opts.inPreferredConfig = Config.RGB_565;
		// 以下两个字段需一起使用：
		// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
		opts.inPurgeable = true;
		// 位图可以共享一个参考输入数据(inputstream、阵列等)
		opts.inInputShareable = true;
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		int sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth,
				destHeight);
		opts.inSampleSize = sampleSize;
		// 设置大小
		opts.outHeight = destHeight;
		opts.outWidth = destWidth;
		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
		if (bitmap != null) {
			resizeBmp = getCutBitmap(bitmap, desiredWidth, desiredHeight);
		}
		return resizeBmp;
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap getCutBitmap(Bitmap bitmap, int desiredWidth,
			int desiredHeight) {

		if (!checkBitmap(bitmap)) {
			return null;
		}

		if (!checkSize(desiredWidth, desiredHeight)) {
			return null;
		}

		Bitmap resizeBmp = null;

		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			int offsetX = 0;
			int offsetY = 0;

			if (width > desiredWidth) {
				offsetX = (width - desiredWidth) / 2;
			} else {
				desiredWidth = width;
			}

			if (height > desiredHeight) {
				offsetY = (height - desiredHeight) / 2;
			} else {
				desiredHeight = height;
			}

			resizeBmp = Bitmap.createBitmap(bitmap, offsetX, offsetY,
					desiredWidth, desiredHeight);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resizeBmp != bitmap) {
				bitmap.recycle();
			}
		}
		return resizeBmp;
	}

	/**
	 * 描述：根据等比例缩放图片.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param scale
	 *            比例
	 * @return Bitmap 新图片
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {

		if (!checkBitmap(bitmap)) {
			return null;
		}

		if (scale == 1) {
			return bitmap;
		}

		Bitmap resizeBmp = null;
		try {
			// 获取Bitmap资源的宽和高
			int bmpW = bitmap.getWidth();
			int bmpH = bitmap.getHeight();

			// 注意这个Matirx是android.graphics底下的那个
			Matrix matrix = new Matrix();
			// 设置缩放系数，分别为原来的0.8和0.8
			matrix.postScale(scale, scale);
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix,
					true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resizeBmp != bitmap) {
				bitmap.recycle();
			}
		}
		return resizeBmp;
	}

	/**
	 * 描述：获取图片尺寸
	 * 
	 * @param file
	 *            File对象
	 * @return Bitmap 新图片
	 */
	public static float[] getBitmapSize(File file) {
		float[] size = new float[2];
		Options opts = new Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);
		// 获取图片的原始宽度高度
		size[0] = opts.outWidth;
		size[1] = opts.outHeight;
		return size;
	}

	/**
	 * 
	 * 获取缩小的比例.
	 * 
	 * @param srcWidth
	 * @param srcHeight
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 */
	private static float getMinScale(int srcWidth, int srcHeight,
			int desiredWidth, int desiredHeight) {
		// 缩放的比例
		float scale = 0;
		// 计算缩放比例，宽高的最小比例
		float scaleWidth = (float) desiredWidth / srcWidth;
		float scaleHeight = (float) desiredHeight / srcHeight;
		if (scaleWidth > scaleHeight) {
			scale = scaleWidth;
		} else {
			scale = scaleHeight;
		}

		return scale;
	}

	private static int[] resizeToMaxSize(int srcWidth, int srcHeight,
			int desiredWidth, int desiredHeight) {
		int[] size = new int[2];
		if (desiredWidth <= 0) {
			desiredWidth = srcWidth;
		}
		if (desiredHeight <= 0) {
			desiredHeight = srcHeight;
		}
		if (desiredWidth > MAX_WIDTH) {
			// 重新计算大小
			desiredWidth = MAX_WIDTH;
			float scaleWidth = (float) desiredWidth / srcWidth;
			desiredHeight = (int) (desiredHeight * scaleWidth);
		}

		if (desiredHeight > MAX_HEIGHT) {
			// 重新计算大小
			desiredHeight = MAX_HEIGHT;
			float scaleHeight = (float) desiredHeight / srcHeight;
			desiredWidth = (int) (desiredWidth * scaleHeight);
		}
		size[0] = desiredWidth;
		size[1] = desiredHeight;
		return size;
	}

	private static boolean checkBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			LogUtil.e(ImageUtil.class, "原图Bitmap为空了");
			return false;
		}

		if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
			LogUtil.e(ImageUtil.class, "原图Bitmap大小为0");
			return false;
		}
		return true;
	}

	private static boolean checkSize(int desiredWidth, int desiredHeight) {
		if (desiredWidth <= 0 || desiredHeight <= 0) {
			LogUtil.e(ImageUtil.class, "请求Bitmap的宽高参数必须大于0");
			return false;
		}
		return true;
	}

	/**
	 * Drawable转Bitmap.
	 * 
	 * @param drawable
	 *            要转化的Drawable
	 * @return Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap对象转换Drawable对象.
	 * 
	 * @param bitmap
	 *            要转化的Bitmap对象
	 * @return Drawable 转化完成的Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		BitmapDrawable mBitmapDrawable = null;
		try {
			if (bitmap == null) {
				return null;
			}
			mBitmapDrawable = new BitmapDrawable(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * Bitmap对象转换TransitionDrawable对象.
	 * 
	 * @param bitmap
	 *            要转化的Bitmap对象 imageView.setImageDrawable(td);
	 *            td.startTransition(200);
	 * @return Drawable 转化完成的Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public static TransitionDrawable bitmapToTransitionDrawable(Bitmap bitmap) {
		TransitionDrawable mBitmapDrawable = null;
		try {
			if (bitmap == null) {
				return null;
			}
			mBitmapDrawable = new TransitionDrawable(new Drawable[] {
					new ColorDrawable(android.R.color.transparent),
					new BitmapDrawable(bitmap) });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * Drawable对象转换TransitionDrawable对象.
	 * 
	 * @param drawable
	 *            要转化的Drawable对象 imageView.setImageDrawable(td);
	 *            td.startTransition(200);
	 * @return Drawable 转化完成的Drawable对象
	 */
	public static TransitionDrawable drawableToTransitionDrawable(
			Drawable drawable) {
		TransitionDrawable mBitmapDrawable = null;
		try {
			if (drawable == null) {
				return null;
			}
			mBitmapDrawable = new TransitionDrawable(new Drawable[] {
					new ColorDrawable(Color.parseColor("#00000000")), drawable });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * 将Bitmap转换为byte[].
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param mCompressFormat
	 *            图片格式 Bitmap.CompressFormat.JPEG,CompressFormat.PNG
	 * @param needRecycle
	 *            是否需要回收
	 * @return byte[] 图片的byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap,
			CompressFormat mCompressFormat, final boolean needRecycle) {
		byte[] result = null;
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			bitmap.compress(mCompressFormat, 100, output);
			result = output.toByteArray();
			if (needRecycle) {
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 获取Bitmap大小.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param mCompressFormat
	 *            图片格式 Bitmap.CompressFormat.JPEG,CompressFormat.PNG
	 * @return 图片的大小
	 */
	public static int getByteCount(Bitmap bitmap,
			CompressFormat mCompressFormat) {
		int size = 0;
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			bitmap.compress(mCompressFormat, 100, output);
			byte[] result = output.toByteArray();
			size = result.length;
			result = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return size;
	}

	/**
	 * 描述：将byte[]转换为Bitmap.
	 * 
	 * @param b
	 *            图片格式的byte[]数组
	 * @return bitmap 得到的Bitmap
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		Bitmap bitmap = null;
		try {
			if (b.length != 0) {
				bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 将ImageView转换为Bitmap.
	 * 
	 * @param view
	 *            要转换为bitmap的View
	 * @return byte[] 图片的byte[]
	 */
	public static Bitmap imageView2Bitmap(ImageView view) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 将View转换为Drawable.需要最上层布局为Linearlayout
	 * 
	 * @param view
	 *            要转换为Drawable的View
	 * @return BitmapDrawable Drawable
	 */
	@SuppressWarnings("deprecation")
	public static Drawable view2Drawable(View view) {
		BitmapDrawable mBitmapDrawable = null;
		try {
			Bitmap newbmp = view2Bitmap(view);
			if (newbmp != null) {
				mBitmapDrawable = new BitmapDrawable(newbmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * 将View转换为Bitmap.需要最上层布局为Linearlayout
	 * 
	 * @param view
	 *            要转换为bitmap的View
	 * @return byte[] 图片的byte[]
	 */
	public static Bitmap view2Bitmap(View view) {
		Bitmap bitmap = null;
		try {
			if (view != null) {
				view.setDrawingCacheEnabled(true);
				view.measure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				view.layout(0, 0, view.getMeasuredWidth(),
						view.getMeasuredHeight());
				view.buildDrawingCache();
				bitmap = view.getDrawingCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 将View转换为byte[].
	 * 
	 * @param view
	 *            要转换为byte[]的View
	 * @param compressFormat
	 *            the compress format
	 * @return byte[] View图片的byte[]
	 */
	public static byte[] view2Bytes(View view,
			CompressFormat compressFormat) {
		byte[] b = null;
		try {
			Bitmap bitmap = ImageUtil.view2Bitmap(view);
			b = ImageUtil.bitmap2Bytes(bitmap, compressFormat, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 描述：旋转Bitmap为一定的角度.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param degrees
	 *            the degrees
	 * @return the bitmap
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		Bitmap mBitmap = null;
		try {
			Matrix m = new Matrix();
			m.setRotate(degrees % 360);
			mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmap;
	}

	/**
	 * 描述：旋转Bitmap为一定的角度并四周暗化处理.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param degrees
	 *            the degrees
	 * @return the bitmap
	 */
	public static Bitmap rotateBitmapTranslate(Bitmap bitmap, float degrees) {
		Bitmap mBitmap = null;
		int width;
		int height;
		try {
			Matrix matrix = new Matrix();
			if ((degrees / 90) % 2 != 0) {
				width = bitmap.getWidth();
				height = bitmap.getHeight();
			} else {
				width = bitmap.getHeight();
				height = bitmap.getWidth();
			}
			int cx = width / 2;
			int cy = height / 2;
			matrix.preTranslate(-cx, -cy);
			matrix.postRotate(degrees);
			matrix.postTranslate(cx, cy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmap;
	}

	/**
	 * 转换图片转换成圆形.
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return the bitmap
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 转换图片转换成镜面效果的图片.
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return the bitmap
	 */
	public static Bitmap toReflectionBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		try {
			int reflectionGap = 1;
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			// This will not scale but will flip on the Y axis
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);

			// Create a Bitmap with the flip matrix applied to it.
			// We only want the bottom half of the image
			Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
					width, height / 2, matrix, false);

			// Create a new bitmap with same width but taller to fit
			// reflection
			Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / 2), Config.ARGB_8888);

			// Create a new Canvas with the bitmap that's big enough for
			// the image plus gap plus reflection
			Canvas canvas = new Canvas(bitmapWithReflection);
			// Draw in the original image
			canvas.drawBitmap(bitmap, 0, 0, null);
			// Draw in the gap
			Paint deafaultPaint = new Paint();
			canvas.drawRect(0, height, width, height + reflectionGap,
					deafaultPaint);
			// Draw in the reflection
			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
			// Create a shader that is a linear gradient that covers the
			// reflection
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, bitmap.getHeight(),
					0, bitmapWithReflection.getHeight() + reflectionGap,
					0x70ffffff, 0x00ffffff, TileMode.CLAMP);
			// Set the paint to use this shader (linear gradient)
			paint.setShader(shader);
			// Set the Transfer mode to be porter duff and destination in
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			// Draw a rectangle using the paint with our linear gradient
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);

			bitmap = bitmapWithReflection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 释放Bitmap对象.
	 * 
	 * @param bitmap
	 *            要释放的Bitmap
	 */
	public static void releaseBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			try {
				if (!bitmap.isRecycled()) {
					LogUtil.d(ImageUtil.class, "Bitmap释放" + bitmap.toString());
					bitmap.recycle();
				}
			} catch (Exception e) {
			}
			bitmap = null;
		}
	}

	/**
	 * 释放Bitmap数组.
	 * 
	 * @param bitmaps
	 *            要释放的Bitmap数组
	 */
	public static void releaseBitmapArray(Bitmap[] bitmaps) {
		if (bitmaps != null) {
			try {
				for (Bitmap bitmap : bitmaps) {
					if (bitmap != null && !bitmap.isRecycled()) {
						LogUtil.d(ImageUtil.class,
								"Bitmap释放" + bitmap.toString());
						bitmap.recycle();
					}
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 描述：简单的图像的特征值，用于缩略图找原图比较好.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the hash code
	 */
	public static String getHashCode(Bitmap bitmap) {
		// 第一步，缩小尺寸。
		// 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，
		// 只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。

		Bitmap temp = Bitmap.createScaledBitmap(bitmap, 8, 8, false);

		int width = temp.getWidth();
		int height = temp.getHeight();
		Log.i("th", "将图片缩小到8x8的尺寸:" + width + "*" + height);

		// 第二步，第二步，简化色彩。
		// 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
		int[] pixels = new int[width * height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i * height + j] = rgbToGray(temp.getPixel(i, j));
			}
		}

		releaseBitmap(temp);

		// 第三步，计算平均值。
		// 计算所有64个像素的灰度平均值。
		int avgPixel = MathUtil.average(pixels);

		// 第四步，比较像素的灰度。
		// 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
		int[] comps = new int[width * height];
		for (int i = 0; i < comps.length; i++) {
			if (pixels[i] >= avgPixel) {
				comps[i] = 1;
			} else {
				comps[i] = 0;
			}
		}

		// 第五步，计算哈希值。
		// 将上一步的比较结果，组合在一起，就构成了一个64位的整数，
		// 这就是这张图片的指纹。
		StringBuffer hashCode = new StringBuffer();
		for (int i = 0; i < comps.length; i += 4) {
			int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1]
					* (int) Math.pow(2, 2) + comps[i + 2]
					* (int) Math.pow(2, 1) + comps[i + 2];
			hashCode.append(MathUtil.binaryToHex(result));
		}
		String sourceHashCode = hashCode.toString();
		// 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
		// 在理论上，这等同于计算"汉明距离"（Hamming distance）。
		// 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。
		return sourceHashCode;
	}

	/**
	 * 描述：图像的特征值余弦相似度.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the DCT hash code
	 */
	public static String getDCTHashCode(Bitmap bitmap) {

		// 将图片缩小到32x32的尺寸
		Bitmap temp = Bitmap.createScaledBitmap(bitmap, 32, 32, false);

		int width = temp.getWidth();
		int height = temp.getHeight();
		Log.i("th", "将图片缩小到32x32的尺寸:" + width + "*" + height);

		// 简化色彩。
		int[] pixels = new int[width * height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i * height + j] = rgbToGray(temp.getPixel(i, j));
			}
		}

		releaseBitmap(temp);

		int[][] pxMatrix = MathUtil.arrayToMatrix(pixels, width, height);
		double[][] doublePxMatrix = MathUtil.intToDoubleMatrix(pxMatrix);

		// 计算DCT,已经变成8*8了
		double[][] dtc = FDCT.fDctTransform(doublePxMatrix);

		// 计算平均值。
		double[] dctResult = MathUtil.matrixToArray(dtc);
		int avgPixel = MathUtil.average(dctResult);

		// 比较像素的灰度。
		// 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
		int[] comps = new int[8 * 8];
		for (int i = 0; i < comps.length; i++) {
			if (dctResult[i] >= avgPixel) {
				comps[i] = 1;
			} else {
				comps[i] = 0;
			}
		}

		// 计算哈希值。
		// 将上一步的比较结果，组合在一起，就构成了一个64位的整数，
		// 这就是这张图片的指纹。
		StringBuffer hashCode = new StringBuffer();
		for (int i = 0; i < comps.length; i += 4) {
			int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1]
					* (int) Math.pow(2, 2) + comps[i + 2]
					* (int) Math.pow(2, 1) + comps[i + 2];
			hashCode.append(MathUtil.binaryToHex(result));
		}
		String sourceHashCode = hashCode.toString();
		// 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
		// 在理论上，这等同于计算"汉明距离"（Hamming distance）。
		// 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。
		return sourceHashCode;
	}

	/**
	 * 描述：图像的特征值颜色分布 将颜色分4个区，0,1,2,3 区组合共64组，计算每个像素点属于哪个区.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the color histogram
	 */
	@SuppressWarnings("unused")
	public static int[] getColorHistogram(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 区颜色分布
		int[] areaColor = new int[64];

		// 获取色彩数组。
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixels = bitmap.getPixel(i, j);
				int alpha = (pixels >> 24) & 0xFF;
				int red = (pixels >> 16) & 0xFF;
				int green = (pixels >> 8) & 0xFF;
				int blue = (pixels) & 0xFF;
				int redArea = 0;
				int greenArea = 0;
				int blueArea = 0;
				// 0-63 64-127 128-191 192-255
				if (red >= 192) {
					redArea = 3;
				} else if (red >= 128) {
					redArea = 2;
				} else if (red >= 64) {
					redArea = 1;
				} else if (red >= 0) {
					redArea = 0;
				}

				if (green >= 192) {
					greenArea = 3;
				} else if (green >= 128) {
					greenArea = 2;
				} else if (green >= 64) {
					greenArea = 1;
				} else if (green >= 0) {
					greenArea = 0;
				}

				if (blue >= 192) {
					blueArea = 3;
				} else if (blue >= 128) {
					blueArea = 2;
				} else if (blue >= 64) {
					blueArea = 1;
				} else if (blue >= 0) {
					blueArea = 0;
				}
				int index = redArea * 16 + greenArea * 4 + blueArea;
				// 加入
				areaColor[index] += 1;
			}
		}
		return areaColor;
	}

	/**
	 * 计算"汉明距离"（Hamming distance）。
	 * 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。.
	 * 
	 * @param sourceHashCode
	 *            源hashCode
	 * @param hashCode
	 *            与之比较的hashCode
	 * @return the int
	 */
	public static int hammingDistance(String sourceHashCode, String hashCode) {
		int difference = 0;
		int len = sourceHashCode.length();
		for (int i = 0; i < len; i++) {
			if (sourceHashCode.charAt(i) != hashCode.charAt(i)) {
				difference++;
			}
		}
		return difference;
	}

	/**
	 * 灰度值计算.
	 * 
	 * @param pixels
	 *            像素
	 * @return int 灰度值
	 */
	private static int rgbToGray(int pixels) {
		// int _alpha = (pixels >> 24) & 0xFF;
		int _red = (pixels >> 16) & 0xFF;
		int _green = (pixels >> 8) & 0xFF;
		int _blue = (pixels) & 0xFF;
		return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
	}

	/**
	 * 找到最合适的SampleSize
	 * 
	 * @param actualWidth
	 * @param actualHeight
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 */
	private static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}
		return (int) n;
	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 图片变圆角
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * save
	 * 
	 * @param path
	 * @param buffer
	 * @return
	 */
	public static int saveBitmap(String path, byte[] buffer) {
		int result = -1;
		try {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream out = new FileOutputStream(new File(path, System.currentTimeMillis() + ".jpg"));
			out.write(buffer);
			out.flush();
			out.close();
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getdecodeBitmap(String filePath) {
		if (filePath == null) {
			return null;
		}
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

		int width = options.outWidth;
		int height = options.outHeight;
		float scale = 1f;
		if (width > ww && width > height) {
			scale = width / ww;
		} else if (height > hh && height > width) {
			scale = height / hh;
		} else {
			scale = 1;
		}

		options.inSampleSize = (int) scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}

	public static int saveBitmap(String path, Bitmap bitmap) {
		int result = -1;
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public interface DCT {

		/** The C7. */
		static double C1 = 0.98078528, C2 = 0.923879532, C3 = 0.831469612,
				C4 = 0.707106781, C5 = 0.555570233, C6 = 0.382683432,
				C7 = 0.195090322;
	}

	// TODO: Auto-generated Javadoc
	/**
	 * The Class FDCT.
	 */
	public static class FDCT implements DCT {

		/**
		 * F dct transform.
		 * 
		 * @param ablk
		 *            the ablk
		 * @return the double[][]
		 */
		public static double[][] fDctTransform(double[][] ablk) {
			double[][] blk = new double[8][8];
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					blk[i][j] = ablk[i][j];
				}
			}
			// 对行变换
			for (int i = 0; i <= 7; i++) {
				double S07, S16, S25, S34, S0734, S1625;
				double D07, D16, D25, D34, D0734, D1625;
				S07 = blk[i][0] + blk[i][7];
				S16 = blk[i][1] + blk[i][6];
				S25 = blk[i][2] + blk[i][5];
				S34 = blk[i][3] + blk[i][4];
				S0734 = S07 + S34;
				S1625 = S16 + S25;
				D07 = blk[i][0] - blk[i][7];
				D16 = blk[i][1] - blk[i][6];
				D25 = blk[i][2] - blk[i][5];
				D34 = blk[i][3] - blk[i][4];
				D0734 = S07 - S34;
				D1625 = S16 - S25;
				blk[i][0] = 0.5 * (C4 * (S0734 + S1625));
				blk[i][1] = 0.5 * (C1 * D07 + C3 * D16 + C5 * D25 + C7 * D34);
				blk[i][2] = 0.5 * (C2 * D0734 + C6 * D1625);
				blk[i][3] = 0.5 * (C3 * D07 - C7 * D16 - C1 * D25 - C5 * D34);
				blk[i][4] = 0.5 * (C4 * (S0734 - S1625));
				blk[i][5] = 0.5 * (C5 * D07 - C1 * D16 + C7 * D25 + C3 * D34);
				blk[i][6] = 0.5 * (C6 * D0734 - C2 * D1625);
				blk[i][7] = 0.5 * (C7 * D07 - C5 * D16 + C3 * D25 - C1 * D34);
			}
			// 对列变换
			for (int j = 0; j <= 7; j++) {
				double S07, S16, S25, S34, S0734, S1625;
				double D07, D16, D25, D34, D0734, D1625;
				S07 = blk[0][j] + blk[7][j];
				S16 = blk[1][j] + blk[6][j];
				S25 = blk[2][j] + blk[5][j];
				S34 = blk[3][j] + blk[4][j];
				S0734 = S07 + S34;
				S1625 = S16 + S25;
				D07 = blk[0][j] - blk[7][j];
				D16 = blk[1][j] - blk[6][j];
				D25 = blk[2][j] - blk[5][j];
				D34 = blk[3][j] - blk[4][j];
				D0734 = S07 - S34;
				D1625 = S16 - S25;
				blk[0][j] = 0.5 * (C4 * (S0734 + S1625));
				blk[1][j] = 0.5 * (C1 * D07 + C3 * D16 + C5 * D25 + C7 * D34);
				blk[2][j] = 0.5 * (C2 * D0734 + C6 * D1625);
				blk[3][j] = 0.5 * (C3 * D07 - C7 * D16 - C1 * D25 - C5 * D34);
				blk[4][j] = 0.5 * (C4 * (S0734 - S1625));
				blk[5][j] = 0.5 * (C5 * D07 - C1 * D16 + C7 * D25 + C3 * D34);
				blk[6][j] = 0.5 * (C6 * D0734 - C2 * D1625);
				blk[7][j] = 0.5 * (C7 * D07 - C5 * D16 + C3 * D25 - C1 * D34);
			}
			return blk;
		}
	}

}
