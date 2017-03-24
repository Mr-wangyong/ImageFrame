# ImageFrame
>高效省内存的播放序列帧控件,支持从文件,resource读取序列帧,内存复用,读取多张只需一张图片内存

## 一.配置和开始使用

project build.gradle加入jitpack
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

项目的build.gradle加入
```
	dependencies {
	        compile 'com.github.Mr-wangyong:ImageFrame:V1.0.1'
	}
```
然后,直接使用自定义的ImageFrameView;
```
<com.example.imageframelibs.ImageFrame.ImageFrameView
      android:id="@+id/image_frame"
      android:layout_width="match_parent"
      android:layout_height="match_parent"/>
```




1. 从Resource里面读取:
```
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
```

2. 从文件中读取:

```
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
```


3. 从文件中读取

```
private void loadDir(ImageFrameView imageFrame) {
    imageFrame.loadImage(testDir, 30, new ImageFrameView.OnPlayFinish() {
      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
  }
```


---

## 二 项目说明:
1. 项目最早是源于腾讯的一篇文章

> [通过三次优化，我将gif加载优化了16.9%](http://note.youdao.com/)

由于腾讯的文章向来只有描述,从无具体代码,此项目为该文章的具体实现,

2.内存占用,

(1)直接读取200张图,直接OOM

(2)边读边写,OPPO x9007上内存占用如下:
![image](read.png)

(3)采用本项目后,x9007上内存占用如下:
![image](cache.png)

3.项目中踩过的坑:

```
BitmapFactory.decodeResource
```
方式读取图片,设置inBitmap有坑,图片会闪烁部分加载不完整,

解决办法:直接用

```
decodeStream(resources.openRawResource(resId),null,options)
```
代替BitmapFactory.decodeResource


## 参考:
官网 关于Bitmap复用:
> https://developer.android.com/training/displaying-bitmaps/manage-memory.html#recycle


