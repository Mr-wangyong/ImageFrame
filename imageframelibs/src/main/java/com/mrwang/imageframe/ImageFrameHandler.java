package com.mrwang.imageframe;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.SoftReference;

/**
 * 直接内置序列帧解析的View
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/5/1
 * Time: 上午11:09
 */
public class ImageFrameHandler implements WorkHandler.WorkMessageProxy {

  private Resources resources;
  private int[] resArray;
  private int width;
  private int height;
  private boolean isRunning;
  private final WorkHandler workHandler;
  private File[] files;


  private static final int FILE = 0;
  private static final int RES = 1;
  private int type;
  private boolean isOpenCache;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({FILE, RES})
  public @interface Operation {}

  ImageFrameHandler(@Operation int type) {
    this.type = type;
    imageCache = new ImageCache();
    workHandler = new WorkHandler();
  }

  @Deprecated
  ImageFrameHandler() {
    imageCache = new ImageCache();
    workHandler = new WorkHandler();
  }

  private ImageCache imageCache;
  private Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      if (onImageLoadListener != null) {
        onImageLoadListener.onImageLoad(bitmapDrawable);
      }
      switch (msg.what) {
        case RES:
          load((int[]) msg.obj);
          break;
        case FILE:
          load((File[]) msg.obj);
          break;
      }
    }
  };
  private volatile float frameTime;
  private volatile int index = 0;
  private volatile BitmapDrawable bitmapDrawable;
  private OnImageLoadListener onImageLoadListener;
  private boolean loop;


  /**
   * load frame form file directory;
   *
   * @param width request width
   * @param height request height
   * @param fileDir file directory
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(final String fileDir, int width, int height, int fps,
      OnImageLoadListener onPlayFinish) {
    if (!isRunning) {
      isRunning = true;
      this.width = width;
      this.height = height;
      loadImage(fileDir, fps, onPlayFinish);
    }

  }

  /**
   * load frame form file directory;
   *
   * @param fileDir file directory
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(final String fileDir, int fps, OnImageLoadListener onPlayFinish) {
    if (!isRunning && !TextUtils.isEmpty(fileDir) && fps > 0) {
      isRunning = true;
      File dir = new File(fileDir);
      if (dir.exists() && dir.isDirectory()) {
        File[] files = dir.listFiles();
        loadImage(files, width, height, fps, onPlayFinish);
      }
    }


  }

  /**
   * load frame form file files Array;
   *
   * @param width request width
   * @param height request height
   * @param files files Array
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(final File[] files, int width, int height, int fps,
      OnImageLoadListener onPlayFinish) {
    if (!isRunning) {
      setImageFrame(files, width, height, fps, onPlayFinish);
      load(files);
    }
  }

  /**
   * load frame form file files Array;
   *
   * @param files files Array
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(final File[] files, int fps, OnImageLoadListener onPlayFinish) {
    loadImage(files, width, height, fps, onPlayFinish);
  }


  private void setImageFrame(final File[] files, int width, int height, int fps,
      OnImageLoadListener onPlayFinish) {
    this.width = width;
    this.height = height;
    if (imageCache == null) {
      imageCache = new ImageCache();
    }
    this.onImageLoadListener = onPlayFinish;
    frameTime = 1000f / fps + 0.5f;
    workHandler.addMessageProxy(this);
    this.files = files;
  }


  /**
   * load frame form file resources Array;
   *
   * @param resArray resources Array
   * @param width request width
   * @param height request height
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(Resources resources, @RawRes int[] resArray, int width, int height, int fps,
      OnImageLoadListener onPlayFinish) {
    loadImage(resources, resArray, width, height, fps, onPlayFinish);
  }

  private void setImageFrame(Resources resources, @RawRes int[] resArray, int width, int height,
      int fps,
      OnImageLoadListener onPlayFinish) {
    this.width = width;
    this.height = height;
    this.resources = resources;
    if (imageCache == null) {
      imageCache = new ImageCache();
    }
    this.onImageLoadListener = onPlayFinish;
    frameTime = 1000f / fps + 0.5f;
    workHandler.addMessageProxy(this);
    this.resArray = resArray;
  }

  /**
   * load frame form file resources Array;
   *
   * @param resArray resources Array
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  @Deprecated
  public void loadImage(Resources resources, @RawRes int[] resArray, int fps,
      OnImageLoadListener onPlayFinish) {
    if (!isRunning) {
      setImageFrame(resources, resArray, width, height, fps, onPlayFinish);
      load(resArray);
    }
  }


  /**
   * loop play frame
   *
   * @param loop true is loop
   */
  public ImageFrameHandler setLoop(boolean loop) {
    if (!isRunning) {
      this.loop = loop;
    }
    return this;
  }


  /**
   * stop play frame
   */
  public void stop() {
    workHandler.getHandler().removeCallbacksAndMessages(null);
    workHandler.removeMessageProxy(this);
    handler.removeCallbacksAndMessages(null);
    resources = null;
    isRunning = false;
  }

  /**
   * 新增方法
   * 暂停
   * stop play frame
   */
  public void pause() {
    isRunning = false;
    workHandler.getHandler().removeCallbacksAndMessages(null);
    // workHandler.removeMessageProxy(this);
    handler.removeCallbacksAndMessages(null);
  }

  public void start() {
    if (!isRunning) {
      isRunning = true;
      switch (type) {
        case FILE:
          load(files);
          break;
        case RES:
          load(resArray);
          break;
      }
    }

  }

  private void load(final File[] files) {
    Message message = Message.obtain();
    message.obj = files;
    message.what = FILE;
    workHandler.getHandler().sendMessage(message);
  }

  private void load(@RawRes int[] res) {
    Message message = Message.obtain();
    message.obj = res;
    message.what = RES;
    workHandler.getHandler().sendMessage(message);
  }


  private void loadInThreadFromFile(final File[] files) {
    if (index < files.length) {
      File file = files[index];
      if (file.isFile() && isPicture(file)) {
        if (bitmapDrawable != null) {
          imageCache.mReusableBitmaps.add(new SoftReference<>(bitmapDrawable.getBitmap()));
          Log.i("TAG", "mReusableBitmaps add1");
        }
        long start = System.currentTimeMillis();
        bitmapDrawable =
            BitmapLoadUtils.decodeSampledBitmapFromFile(file.getAbsolutePath(), width, height,
                imageCache, isOpenCache);
        long end = System.currentTimeMillis();
        float updateTime = (frameTime - (end - start)) > 0 ? (frameTime - (end - start)) : 0;
        Message message = Message.obtain();
        message.what = FILE;
        message.obj = files;
        handler.sendMessageAtTime(message,
            index == 0 ? 0 : (int) (SystemClock.uptimeMillis() + updateTime));
        index++;
      } else {
        index++;
        loadInThreadFromFile(files);
      }
    } else {
      if (loop) {
        index = 0;
        loadInThreadFromFile(files);
      } else {
        index++;
        bitmapDrawable = null;
        frameTime = 0;
        if (onImageLoadListener != null) {
          onImageLoadListener.onPlayFinish();
        }
        isRunning = false;
        onImageLoadListener = null;
      }

    }
  }


  private void loadInThreadFromRes(final int[] resIds) {
    if (index < resIds.length) {
      Log.e("TAG", "loadInThreadFromRes index=" + index);
      int resId = resIds[index];
      if (bitmapDrawable != null) {
        imageCache.mReusableBitmaps.add(new SoftReference<>(bitmapDrawable.getBitmap()));
      }
      long start = System.currentTimeMillis();
      bitmapDrawable =
          BitmapLoadUtils.decodeSampledBitmapFromRes(resources, resId, width,
              height,
              imageCache, isOpenCache);
      long end = System.currentTimeMillis();
      float updateTime = (frameTime - (end - start)) > 0 ? (frameTime - (end - start)) : 0;
      Message message = Message.obtain();
      message.what = RES;
      message.obj = resIds;
      handler.sendMessageAtTime(message,
          index == 0 ? 0 : (int) (SystemClock.uptimeMillis() + updateTime));
      index++;
    } else {
      if (loop) {
        index = 0;
        loadInThreadFromRes(resIds);
      } else {
        index++;
        bitmapDrawable = null;
        frameTime = 0;
        if (onImageLoadListener != null) {
          onImageLoadListener.onPlayFinish();
        }
        isRunning = false;
        onImageLoadListener = null;
      }
    }
  }

  private boolean isPicture(File file) {
    return file.getName().endsWith("png") || file.getName().endsWith("jpg");
  }

  @Override
  public void handleMessage(Message msg) {
    switch (msg.what) {
      case RES:
        loadInThreadFromRes((int[]) msg.obj);
        break;
      case FILE:
        loadInThreadFromFile((File[]) msg.obj);
        break;
    }


  }

  public void setFps(int fps) {
    frameTime = 1000f / fps + 0.5f;
  }

  public void setOnImageLoaderListener(OnImageLoadListener onPlayFinish) {
    this.onImageLoadListener = onPlayFinish;
  }



  public interface OnImageLoadListener {
    void onImageLoad(BitmapDrawable drawable);

    void onPlayFinish();
  }

  /**
   * 改造成build构建者模式
   * <p>
   * 两种 一种是file
   * 一种是Resource
   */
  public static class FileHandlerBuilder implements FrameBuild {
    private int width;
    private int height;
    private int fps = 30;
    private File[] files;
    private OnImageLoadListener onPlayFinish;
    private ImageFrameHandler imageFrameHandler;
    private int startIndex;
    private int endIndex;

    public FileHandlerBuilder(@NonNull File[] files) {
      if (files.length == 0) {
        throw new IllegalArgumentException("fileDir is not empty");
      }
      this.files = files;
      createHandler();
    }

    public FileHandlerBuilder(@NonNull String fileDir) {
      if (TextUtils.isEmpty(fileDir)) {
        throw new IllegalArgumentException("fileDir is not empty");
      }
      File dir = new File(fileDir);
      if (dir.exists() && dir.isDirectory()) {
        files = dir.listFiles();
      }
      createHandler();
    }

    @Override
    public FrameBuild setLoop(boolean loop) {
      imageFrameHandler.setLoop(loop);
      return this;
    }

    @Override
    public FrameBuild stop() {
      imageFrameHandler.stop();
      return this;
    }

    // 只需要改变数组长度即可
    @Override
    public FrameBuild setStartIndex(int startIndex) {
      if (startIndex >= files.length) {
        throw new IllegalArgumentException("startIndex is not  big to files length");
      }
      this.startIndex = startIndex;
      return this;
    }

    @Override
    public FrameBuild setEndIndex(int endIndex) {
      if (endIndex > files.length) {
        throw new IllegalArgumentException("endIndex is not  big to files length");
      }
      if (endIndex <= startIndex) {
        throw new IllegalArgumentException("endIndex is not to small startIndex");
      }
      this.endIndex = endIndex;
      return this;
    }

    @Override
    public FrameBuild setWidth(int width) {
      this.width = width;
      return this;
    }

    @Override
    public FrameBuild setHeight(int height) {
      this.height = height;
      return this;
    }

    @Override
    public FrameBuild setFps(int fps) {
      this.fps = fps;
      imageFrameHandler.setFps(fps);
      return this;
    }

    @Override
    public FrameBuild setOnImageLoaderListener(OnImageLoadListener onPlayFinish) {
      this.onPlayFinish = onPlayFinish;
      return this;
    }

    @Override
    public FrameBuild openLruCache(boolean isOpenCache) {
      imageFrameHandler.openLruCache(isOpenCache);
      return this;
    }

    @Override
    public ImageFrameHandler build() {
      if (!imageFrameHandler.isRunning) {
        clip();
        imageFrameHandler.setImageFrame(files, width, height, fps,
            onPlayFinish);
      }
      return imageFrameHandler;
    }

    private void createHandler() {
      if (imageFrameHandler == null) {
        imageFrameHandler = new ImageFrameHandler(FILE);
      }
    }

    @Override
    public FrameBuild clip() {
      if (startIndex >= 0 && endIndex > 0 && startIndex < endIndex) {
        files = split(files, startIndex, endIndex);
      }
      return this;
    }

    File[] split(File[] resArray, int start, int end) {
      File[] ints = new File[end - start];
      int index = 0;
      for (int i = start; i < end; i++) {
        ints[index] = resArray[i];
        index++;
      }
      return ints;
    }
  }

  private void openLruCache(boolean isOpenCache) {
    this.isOpenCache = isOpenCache;
  }


  /**
   * 改造成build构建者模式
   * <p>
   * 两种 一种是file
   * 一种是Resource
   */
  public static class ResourceHandlerBuilder implements FrameBuild {
    @NonNull
    private final Resources resources;
    private int width;
    private int height;
    private int fps = 30;
    private int[] resArray;
    private OnImageLoadListener onPlayFinish;
    private ImageFrameHandler imageFrameHandler;
    private int startIndex;
    private int endIndex;

    public ResourceHandlerBuilder(@NonNull Resources resources, @NonNull @RawRes int[] resArray) {
      if (resArray.length == 0) {
        throw new IllegalArgumentException("resArray is not empty");
      }
      this.resources = resources;
      this.resArray = resArray;
      createHandler();
    }

    @Override
    public FrameBuild setLoop(boolean loop) {
      imageFrameHandler.setLoop(loop);
      return this;
    }

    @Override
    public FrameBuild stop() {
      imageFrameHandler.stop();
      return this;
    }

    @Override
    public FrameBuild setStartIndex(int startIndex) {
      if (startIndex >= resArray.length) {
        throw new IllegalArgumentException("startIndex is not to big resArray length");
      }
      this.startIndex = startIndex;
      return this;
    }

    @Override
    public FrameBuild setEndIndex(int endIndex) {
      if (endIndex > resArray.length) {
        throw new IllegalArgumentException("endIndex is not  big to resArray length");
      }
      if (endIndex <= startIndex) {
        throw new IllegalArgumentException("endIndex is not to small startIndex");
      }
      this.endIndex = endIndex;
      return this;
    }

    @Override
    public FrameBuild clip() {
      if (startIndex >= 0 && endIndex > 0 && startIndex < endIndex) {
        resArray = split(resArray, startIndex, endIndex);
      }
      return this;
    }


    @Override
    public FrameBuild setWidth(int width) {
      this.width = width;
      return this;
    }

    @Override
    public FrameBuild setHeight(int height) {
      this.height = height;
      return this;
    }

    @Override
    public FrameBuild setFps(int fps) {
      this.fps = fps;
      imageFrameHandler.setFps(fps);// 这里有一个重复计算 后期想个更好的办法支持动态换
      return this;
    }

    @Override
    public FrameBuild openLruCache(boolean isOpenCache) {
      imageFrameHandler.openLruCache(isOpenCache);
      return this;
    }

    @Override
    public FrameBuild setOnImageLoaderListener(OnImageLoadListener onPlayFinish) {
      this.onPlayFinish = onPlayFinish;
      return this;
    }

    @Override
    public ImageFrameHandler build() {
      if (!imageFrameHandler.isRunning) {
        clip();
        imageFrameHandler.setImageFrame(resources, resArray, width, height, fps,
            onPlayFinish);
      }
      return imageFrameHandler;
    }

    private void createHandler() {
      if (imageFrameHandler == null) {
        imageFrameHandler = new ImageFrameHandler(RES);
      }
    }

    int[] split(int[] resArray, int start, int end) {
      int[] ints = new int[end - start];
      int index = 0;
      for (int i = start; i < end; i++) {
        ints[index] = resArray[i];
        index++;
      }
      return ints;
    }
  }

}
