package com.vbyte.p2p.old;

import android.app.Application;
import android.content.Context;

import com.vbyte.p2p.old.p2pEventHandler;
import com.vbyte.p2p.old.p2pNativeInterface;

import java.io.File;
import java.util.UUID;

public class Vbyte {
    private static String var0;
    private static String var1;
    private static String var2;
    private static Context context;
    private static Vbyte vbyte = null;
    private static String var3;
    private static String g = "libvbyte-v7a";
    private static String name;
    
    private Vbyte() {
        if("vbyte-v7a".equals(name)) {
            System.loadLibrary("vbyte-v7a");
        } else {
            System.load(name);
        }
        p2pNativeInterface.initSDK();
        p2pNativeInterface.setAppInfo(var0, var1, var2, var3, new DelegateApplication((Application)context));
    }

    
    public static Vbyte getVbyte(Context var3) {
    	return getVbyte("562f47e826d02336535c5ca0", "KZbDkHx7XjSbntgi", "FY8DjlInInaYg7j5LTa6rayQLG8jK72t", var3);
    }

    public static Vbyte getVbyte(String var0, String var1, String var2, Context var3) {
        if(vbyte == null) {
        	Vbyte.var0 = var0;
        	Vbyte.var1= var1;
        	Vbyte.var2 = var2;
        	Vbyte.var3 = UUID.randomUUID().toString();
        	context = var3;
        	name = getName();
            vbyte = new Vbyte();
        }
        return vbyte;
    }

    public static String getSDKVersion() {
        return p2pNativeInterface.getSDKVersion();
    }

    private static String getName() {
        if(PreferencesUtil.version(context)) {
            String var1 = context.getFilesDir().getAbsolutePath() + "/" + g + ".newest.so";
            String var0 = context.getFilesDir().getAbsolutePath() + "/" + g + ".so";
            File var3 = new File(var1);
            File var2 = new File(var0);
            if(var3.exists()) {
                var3.renameTo(var2);
            }
            if(var2.exists()) {
                return var2.getAbsolutePath();
            }
        }
        return "vbyte-v7a";
    }

    public String getPlayPath(String var1) {
        return this.getPlayPath(var1, 1);
    }

    public String getPlayPath(String var1, int var2) {
        p2pNativeInterface.openNative(context.getFilesDir().getAbsolutePath() + "/" + var1, var1, var2, 0);
        return p2pNativeInterface.getPlayPath();
    }

    public String getPlayPath(String var1, int var2, int var3) {
        p2pNativeInterface.openNative(context.getFilesDir().getAbsolutePath() + "/" + var1, var1, var2, var3);
        return p2pNativeInterface.getPlayPath();
    }

    public void seekTo(int var1) {
        p2pNativeInterface.seekTo(var1);
    }

    public void setHandler(VbyteHandler var1) {
        p2pEventHandler.a().setHandler(var1);
    }

    public void closeNative() {
        p2pNativeInterface.closeNative();
        vbyte = null;
    }

    public String getStatistics() {
        return p2pNativeInterface.getStatistics();
    }

    public int getCurrentPlayTime() {
        return p2pNativeInterface.getCurrentPlayTime();
    }
}
