package com.mrwang.imageframe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午5:23
 */
public class ImageCache {

  final Set<SoftReference<Bitmap>> mReusableBitmaps;
  private LruCache<String, BitmapDrawable> mMemoryCache;

  public ImageCache() {
    mReusableBitmaps =
        Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
    long memCacheSize = Runtime.getRuntime().freeMemory() / 8;
    if (memCacheSize <= 0) {
      memCacheSize = 1;
    }
    // If you're running on Honeycomb or newer, create a
    // synchronized HashSet of references to reusable bitmaps.
    mMemoryCache = new LruCache<String, BitmapDrawable>((int) memCacheSize) {

      // // Notify the removed entry that is no longer being cached.
      // @Override
      // protected void entryRemoved(boolean evicted, String key,
      // BitmapDrawable oldValue, BitmapDrawable newValue) {
      // //Log.i("TAG","mReusableBitmaps add2");
      // //mReusableBitmaps.add(new SoftReference<>(oldValue.getBitmap()));
      // }
      @Override
      protected int sizeOf(String key, BitmapDrawable value) {
        return value.getBitmap().getByteCount();
      }
    };
  }

  // This method iterates through the reusable bitmaps, looking for one
  // to use for inBitmap:
  Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
    Bitmap bitmap = null;

    if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
      synchronized (mReusableBitmaps) {
        final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
        Bitmap item;

        while (iterator.hasNext()) {
          item = iterator.next().get();

          if (null != item && item.isMutable()) {
            // Check to see it the item can be used for inBitmap.
            if (canUseForInBitmap(item, options)) {
              bitmap = item;
              // Remove from reusable set so it can't be used again.
              iterator.remove();
              break;
            }
          } else {
            // Remove from the set if the reference has been cleared.
            iterator.remove();
          }
        }
      }
    }
    return bitmap;
  }


  private static boolean canUseForInBitmap(
      Bitmap candidate, BitmapFactory.Options targetOptions) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // From Android 4.4 (KitKat) onward we can re-use if the byte size of
      // the new bitmap is smaller than the reusable bitmap candidate
      // allocation byte count.
      int width = targetOptions.outWidth / targetOptions.inSampleSize;
      int height = targetOptions.outHeight / targetOptions.inSampleSize;
      int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
      return byteCount <= candidate.getAllocationByteCount();
    }

    // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
    return candidate.getWidth() == targetOptions.outWidth
        && candidate.getHeight() == targetOptions.outHeight
        && targetOptions.inSampleSize == 1;
  }

  /**
   * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
   */
  static int getBytesPerPixel(Bitmap.Config config) {
    if (config == Bitmap.Config.ARGB_8888) {
      return 4;
    } else if (config == Bitmap.Config.RGB_565) {
      return 2;
    } else if (config == Bitmap.Config.ARGB_4444) {
      return 2;
    } else if (config == Bitmap.Config.ALPHA_8) {
      return 1;
    }
    return 1;
  }

  public BitmapDrawable getBitmapFromCache(String filename) {
    return mMemoryCache.get(filename);
  }

  public void addBitmap(String filename, BitmapDrawable bitmapFromCache) {
    mMemoryCache.put(filename, bitmapFromCache);
  }
}
