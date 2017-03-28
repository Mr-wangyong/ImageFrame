package com.example.imageframelibs.ImageFrame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.RawRes;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;

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


    ;
    addInBitmapOptions(options, cache);
    // }
    // If we're running on Honeycomb or newer, try to use inBitmap.
    options.inJustDecodeBounds = false;
    // cache.addBitmap(filename, bitmapFromCache);
    return new BitmapDrawable(BitmapFactory.decodeFile(filename, options));
  }

  public static BitmapDrawable decodeSampledBitmapFromRes(Resources resources, @RawRes int resId,
      int reqWidth, int reqHeight,
      ImageCache cache) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    decodeStream(resources.openRawResource(resId), null, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    String resourceName = resources.getResourceName(resId);
    // if (Utils.hasHoneycomb()) {
    addInBitmapOptions(options, cache);
    // }
    // If we're running on Honeycomb or newer, try to use inBitmap.
    options.inJustDecodeBounds = false;
    BitmapDrawable bitmapFromCache =
        new BitmapDrawable(resources,
            decodeStream(resources.openRawResource(resId), null, options));
    return bitmapFromCache;
  }

  public static int calculateInSampleSize(
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
