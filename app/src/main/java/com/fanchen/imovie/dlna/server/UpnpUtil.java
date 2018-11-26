package com.fanchen.imovie.dlna.server;

import android.util.Log;

import org.fourthline.cling.model.types.UDN;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.UUID;

public class UpnpUtil {

    private static final String TAG = "UpnpUtil";

    public static UDN uniqueSystemIdentifier(String salt, String hostName, String hostAddress) {
        StringBuilder systemSalt = new StringBuilder();
        Log.d(TAG, "host:" + hostName + " ip:" + hostAddress);
        systemSalt.append(hostAddress).append(
                hostAddress);
        systemSalt.append(android.os.Build.MODEL);
        systemSalt.append(android.os.Build.MANUFACTURER);

        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getIP() throws SocketException {
        String ipAddress = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                .hasMoreElements(); ) {
            NetworkInterface networkInterface = en.nextElement();
            if (networkInterface.getName().toLowerCase().equals("eth0")
                    || networkInterface.getName().toLowerCase().equals("wlan0")) {
                for (Enumeration<InetAddress> netAddress = networkInterface.getInetAddresses(); netAddress
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = netAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress();
                        if (!ipAddress.contains("::")) {// ipV6的地址
                            Log.e(TAG, ipAddress);
                            return ipAddress;
                        }
                    }
                }
            }
        }
        return ipAddress;
    }
}
