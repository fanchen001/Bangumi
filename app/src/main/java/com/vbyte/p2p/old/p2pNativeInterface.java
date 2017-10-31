package com.vbyte.p2p.old;

import android.content.Context;

public class p2pNativeInterface {

	public static native void closeNative();

	public static native int getCurrentPlayTime();

	public static native String getPlayPath();

	public static native String getSDKVersion();

	public static native String getStatistics();

	public static native void initSDK();

	public static native void openNative(String var0, String var1, int var2,
			int var3);

	public static native void seekTo(int var0);

	public static native void setAppInfo(String var0, String var1, String var2,
			String var3, Context var4);
}
