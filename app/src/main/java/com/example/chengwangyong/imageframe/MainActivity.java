package com.example.chengwangyong.imageframe;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.imageframelibs.ImageFrame.ImageFrameView;

import java.io.File;

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

     //loadDir(imageFrame);

    // loadFile(imageFrame);
    loadRes(imageFrame);
  }

  private void loadRes(ImageFrameView imageFrame) {
    int[] resIds = new int[210];
    Resources res = getResources();
    final String packageName = getPackageName();
    for (int i = 0; i < resIds.length; i++) {
      int imageResId = res.getIdentifier("gift_" + (i + 1), "drawable", packageName);
      resIds[i] = imageResId;
      Log.e("TAG", "imageResId=" + imageResId);
    }
    imageFrame.loadImage(resIds, 30, new ImageFrameView.OnPlayFinish() {
      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }

  private void loadFile(ImageFrameView imageFrame) {
    File dir = new File(testDir);
    if (dir.isDirectory()) {
      imageFrame.loadImage(dir.listFiles(), 30, new ImageFrameView.OnPlayFinish() {
        @Override
        public void onPlayFinish() {
          Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
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
