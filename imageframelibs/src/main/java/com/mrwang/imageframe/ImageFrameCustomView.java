package com.mrwang.imageframe;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 序列帧解析分离,需要的时候只需要传入一个解析器即可
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/6/7
 * Time: 下午2:50
 */
public class ImageFrameCustomView extends View {
  private ImageFrameHandler imageFrameHandler;

  public ImageFrameCustomView(Context context) {
    super(context);
  }

  public ImageFrameCustomView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ImageFrameCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ImageFrameCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onDetachedFromWindow() {
    if (imageFrameHandler != null) {
      imageFrameHandler.stop();
    }
    super.onDetachedFromWindow();
  }

  public void startImageFrame(final ImageFrameHandler imageFrameHandler) {
    if (this.imageFrameHandler == null) {
      this.imageFrameHandler = imageFrameHandler;
    }else{
      this.imageFrameHandler.stop();
      this.imageFrameHandler = imageFrameHandler;
    }
    imageFrameHandler.setOnImageLoaderListener(new ImageFrameHandler.OnImageLoadListener() {
      @Override
      public void onImageLoad(BitmapDrawable drawable) {
        ViewCompat.setBackground(ImageFrameCustomView.this, drawable);
      }

      @Override
      public void onPlayFinish() {

      }
    });
    post(new Runnable() {
      @Override
      public void run() {
        imageFrameHandler.start();
      }
    });

  }

  @Nullable
  public ImageFrameHandler getImageFrameHandler() {
    return imageFrameHandler;
  }
}
