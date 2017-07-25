package com.example.chengwangyong.imageframe;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mrwang.imageframe.ImageFrameHandler;

public class AnimActivity extends AppCompatActivity {

  private ImageFrameCustomView countDown;
  private ImageFrameCustomView num;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_anim);
    countDown = (ImageFrameCustomView) findViewById(R.id.count_down);
    num = (ImageFrameCustomView) findViewById(R.id.num);

    initAnim();
  }

  private void initAnim() {
    loadRes(countDown, 30, "count_down_", 30, true);
    loadRes(num, 5, "num_", 1, true);
  }

  private void loadRes(final ImageFrameCustomView imageFrame, int count, String title, int fps,
      boolean loop) {
    final int[] resIds = new int[count];
    Resources res = getResources();
    final String packageName = getPackageName();
    for (int i = 0; i < resIds.length; i++) {
      String resName = title + (i + 1);
      Log.i("TAG", "resName=" + resName);
      int imageResId = res.getIdentifier(resName, "drawable", packageName);
      resIds[i] = imageResId;
      Log.e("TAG", "imageResId=" + imageResId);
    }

    ImageFrameHandler build = new ImageFrameHandler.ResourceHandlerBuilder(getResources(), resIds)
        .setFps(fps)
        .setLoop(loop)
        .build();

    imageFrame.startImageFrame(build);
  }
}
