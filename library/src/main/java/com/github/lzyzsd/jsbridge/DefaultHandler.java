package com.github.lzyzsd.jsbridge;

public class DefaultHandler implements BridgeHandler{

	String TAG = "DefaultHandler";
	
	@Override
	public void handler(String data, CallBackFunction function) {
		System.out.println("js："+data.toString());
		if(function != null){
			function.onCallBack("我已收到js你的信息");
		}
	}

}
