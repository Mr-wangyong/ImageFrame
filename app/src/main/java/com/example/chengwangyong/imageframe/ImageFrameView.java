package com.example.chengwangyong.imageframe;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.View;

import com.mrwang.imageframe.ImageFrameHandler;

import java.io.File;

/**
 * 序列帧控件
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午4:30
 */
public class ImageFrameView extends View {

  private int height;
  private int width;
  private ImageFrameHandler proxy;

  public ImageFrameView(Context context) {
    super(context);
    init();
  }

  public ImageFrameView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ImageFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    proxy = new ImageFrameHandler();
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


  /**
   * load frame form file directory;
   *
   * @param width request width
   * @param height request height
   * @param fileDir file directory
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  public void loadImage(final String fileDir, int width, int height, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(fileDir, width, height, fps, onPlayFinish);
  }

  /**
   * load frame form file directory;
   *
   * @param fileDir file directory
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  public void loadImage(final String fileDir, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(fileDir, fps, onPlayFinish);
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
  public void loadImage(final File[] files, int width, int height, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(files, width, height, fps, onPlayFinish);
  }


  /**
   * load frame form file files Array;
   *
   * @param files files Array
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  public void loadImage(final File[] files, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(files, fps, onPlayFinish);
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
  public void loadImage(Resources resources, @RawRes int[] resArray, int width, int height, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(resources, resArray, width, height, fps, onPlayFinish);
  }

  /**
   * load frame form file resources Array;
   *
   * @param resArray resources Array
   * @param fps The number of broadcast images per second
   * @param onPlayFinish finish callback
   */
  public void loadImage(@RawRes int[] resArray, int fps,
      ImageFrameHandler.OnImageLoadListener onPlayFinish) {
    proxy.loadImage(getResources(),resArray, fps, onPlayFinish);
  }

  @Override
  protected void onDetachedFromWindow() {
    proxy.stop();
    super.onDetachedFromWindow();
  }

  public void setLoop(boolean loop) {
    proxy.setLoop(loop);
  }

  public void stop() {
    proxy.stop();
  }

}
