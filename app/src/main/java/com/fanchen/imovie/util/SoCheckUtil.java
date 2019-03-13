package com.fanchen.imovie.util;

public class SoCheckUtil {

    public static boolean check(final String[] sos) {
        boolean isSuccess = true;
        for (String name : sos) {
            try {
                System.loadLibrary(name);
            } catch (Throwable e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        return isSuccess;
    }

}
