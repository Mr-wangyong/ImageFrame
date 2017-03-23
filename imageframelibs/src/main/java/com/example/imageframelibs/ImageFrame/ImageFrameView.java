package com.example.imageframelibs.ImageFrame;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.ref.SoftReference;

/**
 * 序列帧控件
 * 测试地址
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午4:30
 */
public class ImageFrameView extends View implements WorkHandler.WorkMessageProxy {
  public String TAG = "ImageFrameView";


  private ImageCache imageCache;
  private Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      ViewCompat.setBackground(ImageFrameView.this, bitmapDrawable);
      load((File[]) msg.obj);
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

  private void load(final File[] files) {
    Message message = Message.obtain();
    message.obj = files;
    WorkHandler.getInstance().getHanler().sendMessage(message);
  }

  private void loadInThread(final File[] files) {
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
            BitmapLoadUtils.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 1000,
                imageCache);
        long end = System.currentTimeMillis();
        frameTime = (frameTime - (end - start)) > 0 ? (frameTime - (end - start)) : 0;
        Message message = Message.obtain();
        message.obj = files;
        handler.sendMessageAtTime(message,
            index == 0 ? 0 : (int) (SystemClock.uptimeMillis() + frameTime));
        index++;
      } else {
        index++;
        loadInThread(files);
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

  private boolean isPicture(File file) {
    return file.getName().endsWith("png") || file.getName().endsWith("jpg");
  }

  @Override
  public void handleMessage(Message msg) {
    loadInThread((File[]) msg.obj);
  }

  public interface OnPlayFinish {
    void onPlayFinish();
  }

  @Override
  protected void onDetachedFromWindow() {
    handler.removeCallbacksAndMessages(null);
    WorkHandler.getInstance().removeMessageProxy(this);
    super.onDetachedFromWindow();
  }
}
