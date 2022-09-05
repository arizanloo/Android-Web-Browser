# Android WebBrowser
## Preview

https://user-images.githubusercontent.com/87603058/188381881-c64b26ef-9d52-4682-bec6-60272239f7f4.mp4

## How to use
For using a Webview in your app you need to add this code as a java class to your project:
```ruby 
public class CustomWebViewClient extends WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
``` 
Then you need to initialize Webview in your activity:
```ruby 
webview = findViewById(R.id.web);
webview.setWebViewClient(new CustomWebViewClient());
WebSettings webSettings = webview.getSettings();
webSettings.setJavaScriptEnabled(true);
``` 
## Features
Features include:
* History save and load using Shared Preferences
* Search engine
* Swipe refresh layout

## Dependencies
For using swipe refresh layout you need to use this dependency from androidx libraries:
```ruby 
implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
``` 

## Manifest
You also need to add this permission in AndroidManifest.xml:
```ruby 
<uses-permission android:name="android.permission.INTERNET" />
``` 

## Software
This Project was created using Android Studio 4.0 . <br />
Gradle version: 6.1.1
## Extras
If you have any idea that can improve project or is there any bugs that you've solved,
submit it in Pull Requests or email me. 
