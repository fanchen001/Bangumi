package com.vbyte.p2p.old;

import android.os.Message;

public class p2pEventHandler {
    private static p2pEventHandler p2pHandler;
    private VbyteHandler handler;

    private p2pEventHandler() {
        eventAttach(this);
    }

    public static p2pEventHandler a() {
        if(p2pHandler == null) {
            p2pHandler = new p2pEventHandler();
        }
        return p2pHandler;
    }

    private static native void eventAttach(p2pEventHandler var0);

    public static native void eventDetach();

    public final void sendMessage(int var1, String var2) {
        Message var3 = new Message();
        var3.what = var1;
        var3.obj = var2;
        if(this.handler != null) {
            this.handler.sendMessage(var3);
        }
    }

    public void setHandler(VbyteHandler var1) {
        this.handler = var1;
    }

    public void clearHandler() {
        this.handler = null;
    }

    public final void exec() {
        try {
            Runtime.getRuntime().exec(new String[]{"adb", "logcat", "-d", "-v", "threadtime"});
        } catch (Exception var2) {
        	var2.printStackTrace();
        }
    }
}
