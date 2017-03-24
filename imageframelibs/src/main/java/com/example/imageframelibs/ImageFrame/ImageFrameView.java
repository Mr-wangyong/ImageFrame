package com.example.imageframelibs.ImageFrame;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.ref.SoftReference;

/**
 * 序列帧控件
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午4:30
 */
public class ImageFrameView extends View implements WorkHandler.WorkMessageProxy {
  public String TAG = "ImageFrameView";
  private static final int FILE = 0;
  private static final int RES = 1;
  private int width;
  private int height;


  private ImageCache imageCache;
  private Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      ViewCompat.setBackground(ImageFrameView.this, bitmapDrawable);
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
  private float frameTime;
  private int index;
  private BitmapDrawable bitmapDrawable;
  private OnPlayFinish onPlayFinish;



  public ImageFrameView(Context context) {
    super(context);
  }

  public ImageFrameView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ImageFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void loadImage(final String fileDir, int width, int height, int fps,
      OnPlayFinish onPlayFinish) {
    this.width = width;
    this.height = height;
    loadImage(fileDir, fps, onPlayFinish);
  }

  public void loadImage(final String fileDir, int fps, OnPlayFinish onPlayFinish) {
    if (!TextUtils.isEmpty(fileDir) && fps > 0) {
      Log.d(TAG, "run() called Thread=" + Thread.currentThread().getName());
      File dir = new File(fileDir);
      if (dir.exists() && dir.isDirectory()) {
        File[] files = dir.listFiles();
        loadImage(files, fps, onPlayFinish);
      }
    }
  }

  public void loadImage(final File[] files, int width, int height, int fps,
      OnPlayFinish onPlayFinish) {
    this.width = width;
    this.height = height;
    loadImage(files, fps, onPlayFinish);
  }


  public void loadImage(final File[] files, int fps, OnPlayFinish onPlayFinish) {
    if (imageCache == null) {
      imageCache = new ImageCache();
    }
    this.onPlayFinish = onPlayFinish;
    index = 0;
    frameTime = 1000f / fps + 0.5f;
    WorkHandler.getInstance().addMessageProxy(this);
    load(files);
  }

  public void loadImage(@RawRes int[] res, int width, int height, int fps,
      OnPlayFinish onPlayFinish) {
    this.width = width;
    this.height = height;
    loadImage(res, fps, onPlayFinish);
  }

  public void loadImage(@RawRes int[] res, int fps, OnPlayFinish onPlayFinish) {
    if (imageCache == null) {
      imageCache = new ImageCache();
    }
    this.onPlayFinish = onPlayFinish;
    index = 0;
    frameTime = 1000f / fps + 0.5f;
    WorkHandler.getInstance().addMessageProxy(this);
    load(res);
  }


//  private File copyToFileFromResource(int id, String filePath) {
//    File localeFile = new File(filePath);
//    if (localeFile.exists()) {
//      return localeFile;
//    } else {
//      InputStream inputStream = getResources()
//          .openRawResource(id);
//      copyFile(inputStream, filePath);
//    }
//
//    return localeFile;
//  }

  private void load(final File[] files) {
    Message message = Message.obtain();
    message.obj = files;
    message.what = FILE;
    WorkHandler.getInstance().getHanler().sendMessage(message);
  }

  private void load(@RawRes int[] res) {
    Message message = Message.obtain();
    message.obj = res;
    message.what = RES;
    WorkHandler.getInstance().getHanler().sendMessage(message);
  }



  private void loadInThreadFromFile(final File[] files) {
    Log.d(TAG, "loadInThread thread=" + Thread.currentThread().getName());
    if (index < files.length) {
      // 边读边写 展示(同时读取下一张)-->直接再读取下一张
      File file = files[index];
      if (file.isFile() && isPicture(file)) {
        if (bitmapDrawable != null) {// 上一张直接复用
          imageCache.mReusableBitmaps.add(new SoftReference<>(bitmapDrawable.getBitmap()));
        }
        long start = System.currentTimeMillis();
        bitmapDrawable =
            BitmapLoadUtils.decodeSampledBitmapFromFile(file.getAbsolutePath(), width, height,
                imageCache);
        long end = System.currentTimeMillis();
        frameTime = (frameTime - (end - start)) > 0 ? (frameTime - (end - start)) : 0;
        Message message = Message.obtain();
        message.what = FILE;
        message.obj = files;
        handler.sendMessageAtTime(message,
            index == 0 ? 0 : (int) (SystemClock.uptimeMillis() + frameTime));
        index++;
      } else {
        index++;
        loadInThreadFromFile(files);
      }
    } else {
      index++;
      bitmapDrawable = null;
      frameTime = 0;
      if (onPlayFinish != null) {
        onPlayFinish.onPlayFinish();
      }
      onPlayFinish = null;
    }
  }


  private void loadInThreadFromRes(final int[] resIds) {
    Log.d(TAG, "loadInThread thread=" + Thread.currentThread().getName());
    if (index < resIds.length) {
      // 边读边写 展示(同时读取下一张)-->直接再读取下一张
      int resId = resIds[index];

//      int key = getResources().getResourceName(resId).hashCode();
//      String path = Environment.getExternalStorageDirectory().getAbsolutePath()
//          + File.separator + key;
//      File file = copyToFileFromResource(resId, path);


      if (bitmapDrawable != null) {// 上一张直接复用
        imageCache.mReusableBitmaps.add(new SoftReference<>(bitmapDrawable.getBitmap()));
      }
      long start = System.currentTimeMillis();
       bitmapDrawable =
       BitmapLoadUtils.decodeSampledBitmapFromRes(getContext().getResources(), resId, width,
       height,
       imageCache);
//      bitmapDrawable =
//          BitmapLoadUtils.decodeSampledBitmapFromFile(file.getAbsolutePath(), width,
//              height,
//              imageCache);
      long end = System.currentTimeMillis();
      frameTime = (frameTime - (end - start)) > 0 ? (frameTime - (end - start)) : 0;
      Message message = Message.obtain();
      message.what = RES;
      message.obj = resIds;
      handler.sendMessageAtTime(message,
          index == 0 ? 0 : (int) (SystemClock.uptimeMillis() + frameTime));
      index++;
    } else {
      index++;
      bitmapDrawable = null;
      frameTime = 0;
      if (onPlayFinish != null) {
        onPlayFinish.onPlayFinish();
      }
      onPlayFinish = null;
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

  public interface OnPlayFinish {
    void onPlayFinish();
  }


  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    if (width == 0) {
      width = w;
    }
    if (height == 0) {
      height = h;
    }
    super.onSizeChanged(w, h, oldw, oldh);
  }

  @Override
  protected void onDetachedFromWindow() {
    handler.removeCallbacksAndMessages(null);
    WorkHandler.getInstance().removeMessageProxy(this);
    super.onDetachedFromWindow();
  }
}
