# Android VideoView cache  
Cache wrapper for standard android VideoView for downloading and playing video.

✔️ One-line usage.  
✔️ Works with android VideoView or withs wrapper. Both are almost the same.  
✔️ Lightweight. It uses only Okhttp and Rxjava inside.  
✔️ No proxy server is needed (like here https://github.com/danikula/AndroidVideoCache).  
✔️ Files are storing in the default cache directory.  
✔️ Automatic cancellation of the download if view detached from window. No resources leaking.  

# Setup:  

[![](https://jitpack.io/v/Andrew0000/Android-VideoView-cache.svg)](https://jitpack.io/#Andrew0000/Android-VideoView-cache)

1. Add `maven { url 'https://jitpack.io' }` to the `allprojects` or `dependencyResolutionManagement` section in top-leve `build.gradle` or `settings.gradle`.  
For example (`settings.gradle`):
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven { url "https://jitpack.io" }
    }
}
```
2. Add `implementation 'com.github.Andrew0000:Android-VideoView-cache:$latest_version'` to the module-level `build.gradle`

# Usage:

## First way  
With extension for android **VideoView**.

```
videoView.playUrl("https://your/video/file.mp4")
```


## Second way  
With **VideoViewCached**. It shows progress bar and performs auto-scaling. 

For example in xml:

    <crocodile8008.videoviewcache.lib.VideoViewCached
        android:id="@+id/cachedVideoView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintHeight_max="200dp"/>

And then start loading:
```
cachedVideoView2.playUrl("https://your/video/file.mp4")
```
That's all!  
Video will be downloaded, cached and played.  
On the next invocation it will be loaded from cache.  
