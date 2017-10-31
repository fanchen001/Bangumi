package com.vbyte.p2p.old;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {
	
	public static boolean version(Context paramContext) {
		SharedPreferences.Editor localEditor;
		String version = "";
		try {
			SharedPreferences preferences = paramContext.getSharedPreferences("P2PMODULE", 0);
			version = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
			localEditor = preferences.edit();
			if ((preferences == null) || (!preferences.contains("HOSTVERSION"))) {
				localEditor.putString("HOSTVERSION", version);
				localEditor.commit();
				return false;
			}
			if (preferences.getString("HOSTVERSION", "1.0").equals( paramContext)) {
				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return true;
	}
}
