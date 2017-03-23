package com.example.chengwangyong.imageframe;

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
    imageFrame.loadImage(testDir, 30, new ImageFrameView.OnPlayFinish() {
      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }
}
