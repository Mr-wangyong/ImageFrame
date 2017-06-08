package com.mrwang.imageframe;

/**
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/6/7
 * Time: 下午3:06
 */
public interface FrameBuild {
  public FrameBuild setLoop(boolean loop);

  public FrameBuild stop();

  public FrameBuild clip();

  public FrameBuild setStartIndex(int startIndex);

  public FrameBuild setEndIndex(int endIndex);

  public FrameBuild setWidth(int width);

  public FrameBuild setHeight(int height);

  public FrameBuild setFps(int fps);

  public FrameBuild openLruCache(boolean isOpenCache);

  public FrameBuild setOnImageLoaderListener(ImageFrameHandler.OnImageLoadListener onPlayFinish);

  ImageFrameHandler build();
}
