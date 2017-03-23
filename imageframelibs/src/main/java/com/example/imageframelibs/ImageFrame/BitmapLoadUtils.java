package com.example.imageframelibs.ImageFrame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午5:13
 */
public class BitmapLoadUtils {

  public static BitmapDrawable decodeSampledBitmapFromFile(String filename,
                                                           int reqWidth, int reqHeight,
                                                           ImageCache cache) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    decodeFile(filename, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


    BitmapDrawable bitmapFromCache = cache.getBitmapFromCache(filename);
    if (bitmapFromCache == null) {
      // if (Utils.hasHoneycomb()) {
      addInBitmapOptions(options, cache);
      // }
      // If we're running on Honeycomb or newer, try to use inBitmap.
      options.inJustDecodeBounds = false;
      bitmapFromCache = new BitmapDrawable(BitmapFactory.decodeFile(filename, options));
			cache.addBitmap(filename,bitmapFromCache);
    }
    return bitmapFromCache;
  }

  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // 原始图片的宽高
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
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
