package com.github.lzyzsd.jsbridge.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

public class MainActivity extends Activity implements OnClickListener {

	private final String TAG = "MainActivity";

	BridgeWebView webView;

	Button button;

	int RESULT_CODE = 0;

	ValueCallback<Uri> mUploadMessage;

	ValueCallback<Uri[]> mUploadMessageArray;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//获取布局xml文件中配置的webview组件
        webView = (BridgeWebView) findViewById(R.id.webView);
//获取布局xml文件中的button组件
		button = (Button) findViewById(R.id.button);
//设置button的监听事件
		button.setOnClickListener(this);
//设置webview的handler
		webView.setDefaultHandler(new DefaultHandler());
//设置谷歌浏览器客户端  重写该方法
		webView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
				System.out.println("---------------------lalalallala3-------------");
				this.openFileChooser(uploadMsg);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
				System.out.println("---------------------lalalallala4-------------");
				this.openFileChooser(uploadMsg);
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				System.out.println("---------------------lalalallala2-------------");
				mUploadMessage = uploadMsg;
				pickFile();
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				mUploadMessageArray = filePathCallback;
				System.out.println("---------------------lalalallala-------------");
				pickFile();
				return true;
			}
		});
//加载html页面
		webView.loadUrl("file:///android_asset/demo.html");
		webView.setWebContentsDebuggingEnabled(true);
/**
 * 注册handler,生命handler名
 */
		webView.registerHandler("submitFromWeb", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
//				输出来自web的数据
				Log.i(TAG, "js:" + data);
                function.onCallBack("andriod收到");
			}

		});

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "adroid:收到我信息了么";
		/**
		 * android发消息给js
		 */
		webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
				System.out.println("js:"+data.toString());

            }
        });

        webView.send("hello");

	}
//获取图片文件 并跳转
	public void pickFile() {
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, RESULT_CODE);
	}

//	回调 返回值
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == RESULT_CODE) {
			if (null == mUploadMessage && null == mUploadMessageArray){
				return;
			}
			if(null!= mUploadMessage && null == mUploadMessageArray){
				Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
				mUploadMessage.onReceiveValue(result);
				mUploadMessage = null;
			}

			if(null == mUploadMessage && null != mUploadMessageArray){
				Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
				mUploadMessageArray.onReceiveValue(new Uri[]{result});
				mUploadMessageArray = null;
			}

		}
	}





	/**
	 * 安卓点击事件   安卓调用js
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		if (button.equals(v)) {
            webView.callHandler("functionInJs", "js收到我信息了么", new CallBackFunction() {

				@Override
				public void onCallBack(String data) {
					// TODO Auto-generated method stub
//					输出js返回给安卓的数据
					Log.i(TAG, "js: " + data);
				}

			});
		}

	}

}
