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
	        compile 'com.github.Mr-wangyong:ImageFrame:V1.2.0'
	}
```
1.直接使用View,在xml文件中加入自定义的ImageFrameView;
```
<com.mrwang.imageframe.ImageFrameView
      android:id="@+id/image_frame"
      android:layout_width="match_parent"
      android:layout_height="match_parent"/>
```

2.只使用图片加载功能,方便对图片进行后处理
```
 ImageFrameProxy proxy = new ImageFrameProxy();
 proxy.loadImage(testDir, 30, new ImageFrameProxy.OnImageLoadListener() {

      @Override
      public void onImageLoad(BitmapDrawable drawable) {
        //You can do something of the BitmapDrawable here
        ViewCompat.setBackground(view, drawable);
      }

      @Override
      public void onPlayFinish() {
        Log.i("TAG", "userTime=" + (System.currentTimeMillis() - start));
      }
    });
```

## 以下API对ImageFrameProxy同样适用

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
## V1.1更新
1. 增加循环播放功能(去掉了Lru缓存功能)
```
imageFrame.setLoop(true);
```
2. 增加停止播放功能
```
imageFrame.stop();
```

---

## V1.2更新
将图片加载部分抽取成一个模块ImageFrameProxy;API和ImageFrameView一样,ImageFrameView变为一个示例;你可以在获取bitmap后添加更多的处理;

```
proxy = new ImageFrameProxy();
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
```
注意:使用proxy请在页面退出时调用proxy.stop()防止内存泄露

```
@Override
  protected void onDestroy() {
    proxy.stop();
    super.onDestroy();
  }
```



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

## 项目中踩过的坑:
1. 关于bitmap的解析
```
BitmapFactory.decodeResource
```
方式读取图片,设置inBitmap有坑,图片会闪烁部分加载不完整,

解决办法:直接用

```
decodeStream(resources.openRawResource(resId),null,options)
```
代替BitmapFactory.decodeResource

2. 关于LruCache图片复用问题
1.0.1版本按照官方实例LRU缓存每次解析的图片,1.1.0版本增加loop功能发现无法循环播放,经过大量分析,发现lru缓存逻辑有问题,下次进入即使拿不同的key返回相同的bitmap,故去掉了缓存,经测试,不影响内存占用及性能.


## 参考:
官网 关于Bitmap复用:
> https://developer.android.com/training/displaying-bitmaps/manage-memory.html#recycle
