package com.mrwang.imageframe;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;

/**
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午5:13
 */
public class BitmapLoadUtils {

  /**
   * 两种模式
   * 一种启用缓存 loop后下一次不读取磁盘 但是内存抖动 内存占用大 空间换磁盘 cpu性能 内存抖动
   * 一种不启用缓存, loop会读取磁盘,占用磁盘资源,但是内存永远是一张图片的内存 内存不抖动
   *
   */
  public static BitmapDrawable decodeSampledBitmapFromFile(String filename,
      int reqWidth, int reqHeight,
      ImageCache cache, boolean isOpenLruCache) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    decodeFile(filename, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


    BitmapDrawable bitmapFromCache;
    if (isOpenLruCache) {
      bitmapFromCache = cache.getBitmapFromCache(filename);
      if (bitmapFromCache == null) {
        // if (Utils.hasHoneycomb()) {
        bitmapFromCache = readBitmapFromFile(filename, cache, options);
        cache.addBitmap(filename, bitmapFromCache);
      }
    } else {
      bitmapFromCache = readBitmapFromFile(filename, cache, options);
    }

    return bitmapFromCache;
  }

  @NonNull
  private static BitmapDrawable readBitmapFromFile(String filename, ImageCache cache,
      BitmapFactory.Options options) {
    BitmapDrawable bitmapFromCache;
    addInBitmapOptions(options, cache);
    // }
    // If we're running on Honeycomb or newer, try to use inBitmap.
    options.inJustDecodeBounds = false;
    bitmapFromCache = new BitmapDrawable(BitmapFactory.decodeFile(filename, options));
    return bitmapFromCache;
  }

  static BitmapDrawable decodeSampledBitmapFromRes(Resources resources, @RawRes int resId,
                                                   int reqWidth, int reqHeight,
                                                   ImageCache cache, boolean isOpenLruCache) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    decodeStream(resources.openRawResource(resId), null, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // String resourceName = resources.getResourceName(resId);
    // if (Utils.hasHoneycomb()) {
    BitmapDrawable bitmapFromCache;
    if (isOpenLruCache) {
      String resourceName = resources.getResourceName(resId);
      bitmapFromCache = cache.getBitmapFromCache(resourceName);
      if (bitmapFromCache == null) {
        // if (Utils.hasHoneycomb()) {
        bitmapFromCache = readBitmapFromRes(resources, resId, cache, options);
        cache.addBitmap(resourceName, bitmapFromCache);
      }
    } else {
      bitmapFromCache = readBitmapFromRes(resources, resId, cache, options);
    }
    return bitmapFromCache;
  }

  @NonNull
  private static BitmapDrawable readBitmapFromRes(Resources resources, @RawRes int resId, ImageCache cache, BitmapFactory.Options options) {
    addInBitmapOptions(options, cache);
    // }
    // If we're running on Honeycomb or newer, try to use inBitmap.
    options.inJustDecodeBounds = false;
    return new BitmapDrawable(resources,
      decodeStream(resources.openRawResource(resId), null, options));
  }

  private static int calculateInSampleSize(
    BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // 原始图片的宽高
    if (reqHeight == 0 || reqWidth == 0) {
      return 1;
    }
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
      // The maximum value of the inSampleSize can be obtained on the premise of ensuring that the
      // width and height of the bitmap are larger than the target size
      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  private static void addInBitmapOptions(BitmapFactory.Options options,
      ImageCache cache) {
    // inBitmap only works with mutable bitmaps, so force the decoder to
    // return mutable bitmaps.
    options.inMutable = true;

    if (cache != null) {
      // Try to find a bitmap to use for inBitmap.
      Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

      if (inBitmap != null) {
        // If a suitable bitmap has been found, set it as the value of
        // inBitmap.
        options.inBitmap = inBitmap;
      }
    }
  }

}
