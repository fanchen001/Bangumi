package com.fanchen.imovie.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 获取手机信息工具类<br>
 * 
 * @author fanchen
 *
 */
public class DeviceUtil {

	/**
	 * 获取应用程序的IMEI号
	 */
	public static String getIMEI(Context context) {
		if (context == null) {
		}
		TelephonyManager telecomManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telecomManager.getDeviceId();
		return imei;
	}

	/**
	 * 获取设备的系统版本号
	 */
	public static int getDeviceSDK() {
		int sdk = android.os.Build.VERSION.SDK_INT;
		return sdk;
	}

	/**
	 * 获取设备的型号
	 */
	public static String getDeviceName() {
		String model = android.os.Build.MODEL;
		return model;
	}
	
	/**
	 * 获取sim卡卡号
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getSimNum(Context context) {
		TelephonyManager tm= (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		boolean simState=true;
		if (tm.getSimState() == tm.SIM_STATE_ABSENT) { // 未发现sim卡
			simState = false;
		}
		if (simState) {
			String simNum = tm.getSimSerialNumber();
			if (simNum != null) {
				return simNum;
			}
		}
		return "";
	}

	/**
	 * 获取设备id
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getDeviceId(Context context) {
		try {
			TelephonyManager tm= (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			boolean simState=true;
			if (tm.getSimState() == tm.SIM_STATE_ABSENT) { // 未发现sim卡
				simState = false;
			}
			if (simState) {
				String deviceId = tm.getDeviceId();
				if (deviceId != null) {
					return deviceId;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取手机 IMSI 码
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getIMSI(Context context) {
		TelephonyManager tm= (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi != null) {
			return imsi;
		}
		return "";
	}

	/**
	 * 获取手机号码，绝大部分时候不灵
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getPhoneNO(Context context) {
		TelephonyManager tm= (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String phoneNO = tm.getLine1Number();
		if (phoneNO != null) {
			return phoneNO;
		}
		return "";
	}
	
	
	/**
	 *  获取唯一设备ID
	 * @return
	 */
	public static String getOnlyId(Context context) {
		String id;
		if (TextUtils.isEmpty(id = getDeviceId(context)) == false
				&& id.length() > 3) {
		} else if (TextUtils.isEmpty(id = getIMSI(context)) == false
				&& id.length() > 3) {
		} else if (TextUtils.isEmpty(id = getSimNum(context)) == false
				&& id.length() > 3) {
		} else if (TextUtils.isEmpty(id = getIMEI(context)) == false
				&& id.length() > 3) {
		} else {
			id = System.currentTimeMillis() + "";
		}
		id = id.trim();
		StringBuilder sb = new StringBuilder(); 
		if (id != null && !"".equals(id)) {
			for (int i = 0; i < id.length(); i++) {
				if (id.charAt(i) >= 48 && id.charAt(i) <= 57) {
					sb.append(id.charAt(i));
				}
			}
		}
		String string = sb.toString();
		if(string.length() < 3){
			Random ran = new Random();
			for (int i = 0; i < 3; i++) {
				sb.append(ran.nextInt(9));
			}
		}
		return sb.toString();
	}

	/** 返回手机服务商名字 */
	public static String getProvidersName(Context context) {
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = getIMSI( context) ;
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		} else {
			ProvidersName = "其他服务商:" + IMSI;
		}
		return ProvidersName;
	}

	/** 获取当前设备的SN */
	public static String getSimSN(Context context) {
		if (null == context) {
			return null;
		}
		String simSN = null;
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			simSN = tm.getSimSerialNumber();
		} catch (Exception e) {
		}
		return simSN;
	}

	/** 获取当前设备的MAC地址 */
	public static String getMacAddress(Context context) {
		if (null == context) {
			return null;
		}
		String mac = null;
		try {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wm.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
		}
		return mac;
	}

	/** 获得设备ip地址 */
	public static String getLocalAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
		}
		return null;
	}
	
	/** 
	 * 获取cpu核心数
	 * filesystem at "/sys/devices/system/cpu" 
	 * @return The number of cores, or 1 if failed to get result 
	 */ 
	public static int getNumCores() { 
		try { 
			File dir = new File("/sys/devices/system/cpu/"); 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			return files.length; 
		} catch(Exception e) { 
			e.printStackTrace();
			return 1; 
		} 
	} 
	
}
