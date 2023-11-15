# BlurView

<img src="https://user-images.githubusercontent.com/1433500/174389657-f52837db-005b-4a68-b9c6-ce196fa03395.jpg" width="40%">

Dynamic iOS-like blur for Android Views. Includes library and small example project.

BlurView can be used as a regular FrameLayout. It blurs specified view content and draws it as a
background for its children. The children of the BlurView are not blurred. BlurView redraws its
blurred content when changes in view hierarchy are detected (draw() called). It honors its position
and size changes, including view animation and property animation.

## How to use
```XML
<com.windmill.blur.BlurView
    android:id="@+id/blurView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/white"
    app:blurRadius="25"
    app:blurScale="12"
    app:blurTarget="@id/blur_target"
    app:blurWithPreDraw="false"
    app:overlayColor="#10FF00FF">

    <!--
      you can declare app:blurTarget to use without call BlurView#with methods
      app:blurTarget view id, target view to blur
      app:blurWithPreDraw only used when app:blurTarget set

      frameClearDrawable is setFrameClearDrawable

      Any child View here, TabLayout for example. This View will NOT be blurred
    -->

</com.windmill.blur.BlurView>
```

```Java
//see :app for example & information
//method chaining is removed
blurView.with(rootView, new RenderScriptBlur(this)) //or RenderEffectBlur
blurView.setBlurRadius(radius)
```

Always try to choose the closest possible root layout to BlurView. This will greatly reduce the amount of work needed for creating View hierarchy snapshot.

## SurfaceView, TextureView, VideoView, MapFragment, GLSurfaceView, etc
BlurView currently doesn't support blurring of these targets, because they work only with hardware-accelerated Canvas, and BlurView relies on a software Canvas to make a snapshot of Views to blur.

## Rounded corners and other clips
It's possible to set rounded corners as same way as other regular Views:
- [ViewOutlineProvider](https://developer.android.com/reference/android/view/ViewOutlineProvider) (API 21+)
- [Drawable](https://developer.android.com/reference/android/graphics/drawable/Drawable) with [getOutline](https://developer.android.com/reference/android/graphics/drawable/Drawable?hl=en#getOutline(android.graphics.Outline)) overridden (API 21+)

For API below 21, you can still rounded the blur background but the view content is still rectangle.

Related thread - https://github.com/Dimezis/BlurView/issues/37

## Why blurring on the main thread?
Because blurring on other threads would introduce 1-2 frames of latency.

## Comparing to other blurring libs
- The main advantage of BlurView over almost any other library is that it doesn't trigger redundant redraw.
- The BlurView never invalidates itself or other Views in the hierarchy and updates only when needed relying on just a Bitmap mutation, which is recorded on a hardware-accelerated canvas.
- It supports multiple BlurViews on the screen without triggering a draw loop.
- It uses optimized RenderScript Allocations on devices that require certain Allocation sizes, which greatly increases blur performance.
- It allows choosing a custom root view to take a snapshot from, which reduces the amount of drawing traversals and allows greater flexibility.
- Supports blurring of Dialogs (and Dialog's background)

Other libs:
- 🛑 [BlurKit](https://github.com/CameraKit/blurkit-android) - constantly invalidates itself
- 🛑 [RealtimeBlurView](https://github.com/mmin18/RealtimeBlurView) - constantly invalidates itself

License
-------

    Copyright 2022 Dmytro Saviuk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
