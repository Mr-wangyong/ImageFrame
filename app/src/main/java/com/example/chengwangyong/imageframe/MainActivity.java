package com.example.chengwangyong.imageframe;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mrwang.imageframe.ImageFrameView;

import java.io.File;

/**
 * 2017.04.22
 * 任务:探究开启了循环之后为什么不能使用缓存的原因
 * 1.去掉缓存 每次读取 没问题
 * 2.去掉inBitmap选项 从缓存中读取 内存占用很高 不合适
 * 说明开启缓存后 没办法在复用之前的图片内存了
 *
 * 二:探究一下apng是否能解
 *
 * 
 */
public class MainActivity extends AppCompatActivity {
  public String testDir =
      Environment.getExternalStorageDirectory().getAbsolutePath()
          + File.separator + "Android/gift_1_30_12";
  private long start;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ImageFrameView imageFrame = (ImageFrameView) findViewById(R.id.image_frame);

    start = System.currentTimeMillis();

    // loadDir(imageFrame);

    loadFile(imageFrame);
    // loadRes(imageFrame);
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
    imageFrame.loadImage(resIds, 30, new ImageFrameView.OnPlayFinish() {
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
      imageFrame.loadImage(dir.listFiles(), 30, new ImageFrameView.OnPlayFinish() {
        @Override
        public void onPlayFinish() {
          Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start) + " thread="
              + Thread.currentThread().getName());
        }
      });
    }
  }

  private void loadDir(ImageFrameView imageFrame) {
    imageFrame.loadImage(testDir, 30, new ImageFrameView.OnPlayFinish() {
      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }
}
