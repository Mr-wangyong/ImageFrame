# ImageFrame
>高效省内存的播放序列帧控件,支持从文件,resource读取序列帧,内存复用,读取多张只需一张图片内存,流式API,一行代码即可实现序列帧动画

![image](anim.gif)


## 二 项目说明:

1. 项目的初衷是因为自己长期做直播项目,某些地方需要使用一些png序列帧来合成某些动画.当然,webP和Apng或许更加的优秀,但WebP在Android上还有些低版本兼容性问题,Apng在Android的实现均有内存占用大,解码效率低的问题,直到后来看到腾讯的一篇文章

> [通过三次优化，我将gif加载优化了16.9%](http://note.youdao.com/)

由于腾讯的文章向来只有描述,从无具体代码,此项目为该文章的具体实现,

基本原理依旧是双线程协作,子线程负责解码图片,主线程负责接收图片后设置到View上去,由于内部内存复用,同时只占用一张图片的内存

2.性能测试,内存占用,CPU占用

(1)直接读取200张图,直接OOM

(2)边读边写,OPPO x9007上内存占用如下:
![image](read.png)

(3)采用本项目后,x9007上内存占用如下:
![image](cache.png)



内存只占用一张图片内存,CPU占用14%左右,已符合大部分项目场景,无兼容及解码效率的问题;



## 一.配置和开始使用

project build.gradle加入**jitpack**仓库
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
	        compile 'com.github.Mr-wangyong:ImageFrame:v1.3.0'
	}
```


## V1.3 重大更新,流式API,指定播放位置,暂停,专注于序列帧解析,更强大的自定义功能;

​	项目进行重构,部分API已标记为过时`@Deprecated`,不建议使用,结构改为`ImageFrameHandler`负责处理序列帧加载,通过回调`OnImageLoadListener`

返回`BitmapDrawable`给外部的View设置图片,专注于序列帧处理,便于外界进行自由定制操作;同时API参考`AlertDialog` 流式API,更加简洁好用;

增加的API:

播放

```
build.start();//开始播放序列帧
```

暂停

```
build.pause();//暂停播放
```

指定从某一帧开始:

```
build.setStartIndex(10)
```

指定从某一帧结束:

```
build.setEndIndex(10)
```



> 支持从文件/Resource目录读取.流式API,简洁好用

- 从文件读取:

  1. **简洁模式**,你可以创建一个自定义的View,内部封装这些功能,参考[ImageFrameCustomView](https://github.com/Mr-wangyong/ImageFrame/blob/master/app/src/main/java/com/example/chengwangyong/imageframe/ImageFrameCustomView.java),我这里给你的实现(当然,建议根据项目需求定义更好更实用的),直接调用`startImageFrame`方法即可开启序列帧动画

  ```
  imageFrame.startImageFrame(new ImageFrameHandler.FileHandlerBuilder(file.listFiles())
          .setFps(40)// 设置fps(每秒播放多少帧,建议不大于30 默认30 太大了设备性能不够磁盘可能读取不过来丢帧)
          .setWidth(100)// 设置图片宽度 非必须 设置了会内部压缩 否则不会
          .setHeight(300)// 设置图片高度 非必须 同上
          .setStartIndex(10)// 设置开始的序列帧图片index,可以指定从那张图片开始
          .setEndIndex(30)// 设置结束的序列帧图片index,可以指定从那张图片结束
          .setLoop(true)// 设置是否循环播放
          .openLruCache(true)// 设置是否开启LRU缓存,如果不循环,建议不开启,如果循环,建议开启,不过多次测试性能相差并不大
          .build());// 构建一个
  ```

  2. **自定义模式**,任意View,更多自定义控制加载的流程;

  ```
  File file =
          new File(testDir);
      //创建一个FileHandlerBuilder,API和Android系统AlertDialog相似
      ImageFrameHandler build = new ImageFrameHandler.FileHandlerBuilder(file.listFiles())
      //必须传入File[]座位处理源
          .setFps(40)
          // 设置fps(每秒播放多少帧,建议不大于30 默认30 太大了设备性能不够磁盘可能读取不过来丢帧)
          .setWidth(100)
          // 设置图片宽度 非必须 设置了会内部压缩 否则不会
          .setHeight(300)// 设置图片高度 非必须 同上
          .setStartIndex(10)
          // 设置开始的序列帧图片index,可以指定从那张图片开始
          .setEndIndex(30)
          // 设置结束的序列帧图片index,可以指定从那张图片结束
          .setLoop(true)// 设置是否循环播放
          .openLruCache(true)// 设置是否开启LRU缓存,如果不循环,建议不开启,如果循环,建议开启,不过多次测试性能相差并不大
          .setOnImageLoaderListener(new ImageFrameHandler.OnImageLoadListener() {
            //创建一个监听器 监听解析成功 以及完成解析
            @Override
            public void onImageLoad(BitmapDrawable drawable) {
              //给你的View设置上图片
             ViewCompat.setBackground(view,drawable);
            }

            @Override
            public void onPlayFinish() {

            }
          })
          .build();// 构建一个Handler 来处理

      build.start();//开始播放序列帧
  ```

  ​

  当然,你还可以有`stop`,`pause`,更自由的控制加载的过程;

  ```
  build.start();//开始播放序列帧

  build.pause();//暂停播放

  build.stop();//停止播放
  ```

  ​

- 从Resource目录读取:

  原理同上,只是构建的是`ResourceHandlerBuilder`

  ```
  final int[] resIds = new int[210];
      ImageFrameHandler build = new ImageFrameHandler.ResourceHandlerBuilder(getResources(),resIds)//必须传入Resource和resIds资源id集合作为处理源
        .setFps(40)// 设置fps(每秒播放多少帧,建议不大于30 默认30 太大了设备性能不够磁盘可能读取不过来丢帧)
        .setWidth(100)
        // 设置图片宽度 非必须 设置了会内部压缩 否则不会
        .setHeight(300)// 设置图片高度 非必须 同上
        .setStartIndex(10)
        // 设置开始的序列帧图片index,可以指定从那张图片开始
        .setEndIndex(30)
        // 设置结束的序列帧图片index,可以指定从那张图片结束
        .setLoop(true)// 设置是否循环播放
        .openLruCache(true)// 设置是否开启LRU缓存,如果不循环,建议不开启,如果循环,建议开启,不过多次测试性能相差并不大
        .setOnImageLoaderListener(new ImageFrameHandler.OnImageLoadListener() {
          //创建一个监听器 监听解析成功 以及完成解析
          @Override
          public void onImageLoad(BitmapDrawable drawable) {
            //给你的View设置上图片
            ViewCompat.setBackground(view,drawable);
          }

          @Override
          public void onPlayFinish() {

          }
        })
        .build();// 构建一个Handler 来处理

    build.start();//开始播放序列帧
  ```

  ​

## V1.2更新

将图片加载部分抽取成一个模块ImageFrameProxy;API和ImageFrameView一样,ImageFrameView变为一个示例;你可以在获取bitmap后添加更多的处理;



注意:使用proxy请在页面退出时调用proxy.stop()防止内存泄露

```
@Override
  protected void onDestroy() {
    proxy.stop();
    super.onDestroy();
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



