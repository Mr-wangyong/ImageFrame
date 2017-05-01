package com.example.chengwangyong.imageframe;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mrwang.imageframe.ImageFrameProxy;
import com.mrwang.imageframe.ImageFrameView;

import java.io.File;

/**
 *
 * 
 */
public class MainActivity extends AppCompatActivity {
  public String testDir =
      Environment.getExternalStorageDirectory().getAbsolutePath()
          + File.separator + "Android/gift_1_30_12";
  private long start;
  private ImageFrameProxy proxy;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final ImageFrameView imageFrame = (ImageFrameView) findViewById(R.id.image_frame);

    start = System.currentTimeMillis();

    // loadDir(imageFrame);

    //loadFile(imageFrame);


    proxy = new ImageFrameProxy();
    imageFrame.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadRes(imageFrame);
      }
    });
  }

  private void loadRes(final ImageFrameView imageFrame) {
    final int[] resIds = new int[210];
    Resources res = getResources();
    final String packageName = getPackageName();
    for (int i = 0; i < resIds.length; i++) {
      int imageResId = res.getIdentifier("gift_" + (i + 1), "drawable", packageName);
      resIds[i] = imageResId;
      Log.e("TAG", "imageResId=" + imageResId);
    }
    imageFrame.setLoop(true);
    imageFrame.loadImage(resIds, 30, new ImageFrameProxy.OnImageLoadListener() {
      @Override
      public void onImageLoad(BitmapDrawable drawable) {
        ViewCompat.setBackground(imageFrame, drawable);
      }

      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }

  private void loadFile(final ImageFrameView imageFrame) {
    final File dir = new File(testDir);
    if (dir.isDirectory()) {
      imageFrame.setLoop(true);
      imageFrame.loadImage(dir.listFiles(), 30, new ImageFrameProxy.OnImageLoadListener() {

        @Override
        public void onImageLoad(BitmapDrawable drawable) {
          ViewCompat.setBackground(imageFrame, drawable);
        }

        @Override
        public void onPlayFinish() {
          Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start) + " thread="
              + Thread.currentThread().getName());
        }
      });
    }
  }

  private void loadDir(final ImageFrameView imageFrame) {
    imageFrame.loadImage(testDir, 30, new ImageFrameProxy.OnImageLoadListener() {

      @Override
      public void onImageLoad(BitmapDrawable drawable) {
        ViewCompat.setBackground(imageFrame, drawable);
      }

      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }

  private void loadDirFromProxy(final ImageFrameView imageFrame) {
    proxy.loadImage(testDir, 30, new ImageFrameProxy.OnImageLoadListener() {

      @Override
      public void onImageLoad(BitmapDrawable drawable) {
        ViewCompat.setBackground(imageFrame, drawable);
      }

      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }

  @Override
  protected void onDestroy() {
    proxy.stop();
    super.onDestroy();
  }
}
