package com.vbyte.p2p.old;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelegateApplication extends Application {
	private Application app;

	public DelegateApplication() {
	}

	public DelegateApplication(Application var1) {
		this.app = var1;
	}

	public boolean bindService(Intent var1, ServiceConnection var2, int var3) {
		return this.app.bindService(var1, var2, var3);
	}

	public int checkCallingOrSelfPermission(String var1) {
		return this.app.checkCallingOrSelfPermission(var1);
	}

	public int checkCallingOrSelfUriPermission(Uri var1, int var2) {
		return this.app.checkCallingOrSelfUriPermission(var1, var2);
	}

	public int checkCallingPermission(String var1) {
		return this.app.checkCallingPermission(var1);
	}

	public int checkCallingUriPermission(Uri var1, int var2) {
		return this.app.checkCallingUriPermission(var1, var2);
	}

	public int checkPermission(String var1, int var2, int var3) {
		return this.app.checkPermission(var1, var2, var3);
	}


	public int checkUriPermission(Uri var1, int var2, int var3, int var4) {
		return this.app.checkUriPermission(var1, var2, var3, var4);
	}

	public int checkUriPermission(Uri var1, String var2, String var3, int var4,
			int var5, int var6) {
		return this.app.checkUriPermission(var1, var2, var3, var4, var5, var6);
	}

	public void clearWallpaper() throws IOException {
		this.app.clearWallpaper();
	}

	
	public Context createConfigurationContext(Configuration var1) {
		return this.app.createConfigurationContext(var1);
	}


	
	public Context createDisplayContext(Display var1) {
		return this.app.createDisplayContext(var1);
	}

	public Context createPackageContext(String var1, int var2)
			throws NameNotFoundException {
		return this.app.createPackageContext(var1, var2);
	}

	public String[] databaseList() {
		return this.app.databaseList();
	}

	public boolean deleteDatabase(String var1) {
		return this.app.deleteDatabase(var1);
	}

	public boolean deleteFile(String var1) {
		return this.app.deleteFile(var1);
	}


	public void enforceCallingOrSelfPermission(String var1, String var2) {
		this.app.enforceCallingOrSelfPermission(var1, var2);
	}

	public void enforceCallingOrSelfUriPermission(Uri var1, int var2,
			String var3) {
		this.app.enforceCallingOrSelfUriPermission(var1, var2, var3);
	}

	public void enforceCallingPermission(String var1, String var2) {
		this.app.enforceCallingPermission(var1, var2);
	}

	public void enforceCallingUriPermission(Uri var1, int var2, String var3) {
		this.app.enforceCallingUriPermission(var1, var2, var3);
	}

	public void enforcePermission(String var1, int var2, int var3, String var4) {
		this.app.enforcePermission(var1, var2, var3, var4);
	}

	public void enforceUriPermission(Uri var1, int var2, int var3, int var4,
			String var5) {
		this.app.enforceUriPermission(var1, var2, var3, var4, var5);
	}

	public void enforceUriPermission(Uri var1, String var2, String var3,
			int var4, int var5, int var6, String var7) {
		this.app.enforceUriPermission(var1, var2, var3, var4, var5, var6, var7);
	}

	public boolean equals(Object var1) {
		return this.app.equals(var1);
	}

	public String[] fileList() {
		return this.app.fileList();
	}

	public Context getApplicationContext() {
		return this.app.getApplicationContext();
	}

	public ApplicationInfo getApplicationInfo() {
		return this.app.getApplicationInfo();
	}

	public AssetManager getAssets() {
		return this.app.getAssets();
	}

	public Context getBaseContext() {
		return this.app.getBaseContext();
	}

	public File getCacheDir() {
		return this.app.getCacheDir();
	}

	public ClassLoader getClassLoader() {
		return this.app.getClassLoader();
	}

	
	public File getCodeCacheDir() {
		return this.app.getCodeCacheDir();
	}

	public ContentResolver getContentResolver() {
		return this.app.getContentResolver();
	}


	public File getDatabasePath(String var1) {
		return this.app.getDatabasePath(var1);
	}

	public File getDir(String var1, int var2) {
		return this.app.getDir(var1, var2);
	}

	public File getExternalCacheDir() {
		return this.app.getExternalCacheDir();
	}

	
	public File[] getExternalCacheDirs() {
		return this.app.getExternalCacheDirs();
	}

	public File getExternalFilesDir(String var1) {
		return this.app.getExternalFilesDir(var1);
	}

	
	public File[] getExternalFilesDirs(String var1) {
		return this.app.getExternalFilesDirs(var1);
	}

	
	public File[] getExternalMediaDirs() {
		return this.app.getExternalMediaDirs();
	}

	public File getFileStreamPath(String var1) {
		return this.app.getFileStreamPath(var1);
	}

	public File getFilesDir() {
		return this.app.getFilesDir();
	}

	public Looper getMainLooper() {
		return this.app.getMainLooper();
	}

	
	public File getNoBackupFilesDir() {
		return this.app.getNoBackupFilesDir();
	}

	public File getObbDir() {
		return this.app.getObbDir();
	}

	
	public File[] getObbDirs() {
		return this.app.getObbDirs();
	}

	public String getPackageCodePath() {
		return this.app.getPackageCodePath();
	}

	public PackageManager getPackageManager() {
		return this.app.getPackageManager();
	}

	public String getPackageName() {
		return "dopool.player";
	}

	public String getPackageResourcePath() {
		return this.app.getPackageResourcePath();
	}

	public Resources getResources() {
		return this.app.getResources();
	}

	public SharedPreferences getSharedPreferences(String var1, int var2) {
		return this.app.getSharedPreferences(var1, var2);
	}

	public Object getSystemService(String var1) {
		return this.app.getSystemService(var1);
	}


	public Theme getTheme() {
		return this.app.getTheme();
	}

	public Drawable getWallpaper() {
		return this.app.getWallpaper();
	}

	public int getWallpaperDesiredMinimumHeight() {
		return this.app.getWallpaperDesiredMinimumHeight();
	}

	public int getWallpaperDesiredMinimumWidth() {
		return this.app.getWallpaperDesiredMinimumWidth();
	}

	public void grantUriPermission(String var1, Uri var2, int var3) {
		this.app.grantUriPermission(var1, var2, var3);
	}

	public int hashCode() {
		return this.app.hashCode();
	}

	public boolean isRestricted() {
		return this.app.isRestricted();
	}


	public void onConfigurationChanged(Configuration var1) {
		this.app.onConfigurationChanged(var1);
	}

	public void onCreate() {
		this.app.onCreate();
	}

	public void onLowMemory() {
		this.app.onLowMemory();
	}

	public void onTerminate() {
		this.app.onTerminate();
	}

	public void onTrimMemory(int var1) {
		this.app.onTrimMemory(var1);
	}

	public FileInputStream openFileInput(String var1)
			throws FileNotFoundException {
		return this.app.openFileInput(var1);
	}

	public FileOutputStream openFileOutput(String var1, int var2)
			throws FileNotFoundException {
		return this.app.openFileOutput(var1, var2);
	}

	public SQLiteDatabase openOrCreateDatabase(String var1, int var2,
			CursorFactory var3) {
		return this.app.openOrCreateDatabase(var1, var2, var3);
	}

	public SQLiteDatabase openOrCreateDatabase(String var1, int var2,
			CursorFactory var3, DatabaseErrorHandler var4) {
		return this.app.openOrCreateDatabase(var1, var2, var3, var4);
	}

	public Drawable peekWallpaper() {
		return this.app.peekWallpaper();
	}

	public void registerActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks var1) {
		this.app.registerActivityLifecycleCallbacks(var1);
	}

	public void registerComponentCallbacks(ComponentCallbacks var1) {
		this.app.registerComponentCallbacks(var1);
	}

	public void registerOnProvideAssistDataListener(
			OnProvideAssistDataListener var1) {
		this.app.registerOnProvideAssistDataListener(var1);
	}

	public Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2) {
		return this.app.registerReceiver(var1, var2);
	}

	public Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2,
			String var3, Handler var4) {
		return this.app.registerReceiver(var1, var2, var3, var4);
	}

	public void removeStickyBroadcast(Intent var1) {
		this.app.removeStickyBroadcast(var1);
	}

	
	public void removeStickyBroadcastAsUser(Intent var1, UserHandle var2) {
		this.app.removeStickyBroadcastAsUser(var1, var2);
	}

	public void revokeUriPermission(Uri var1, int var2) {
		this.app.revokeUriPermission(var1, var2);
	}

	public void sendBroadcast(Intent var1) {
		this.app.sendBroadcast(var1);
	}

	public void sendBroadcast(Intent var1, String var2) {
		this.app.sendBroadcast(var1, var2);
	}

	
	public void sendBroadcastAsUser(Intent var1, UserHandle var2) {
		this.app.sendBroadcastAsUser(var1, var2);
	}

	
	public void sendBroadcastAsUser(Intent var1, UserHandle var2, String var3) {
		this.app.sendBroadcastAsUser(var1, var2, var3);
	}

	public void sendOrderedBroadcast(Intent var1, String var2) {
		this.app.sendOrderedBroadcast(var1, var2);
	}

	public void sendOrderedBroadcast(Intent var1, String var2,
			BroadcastReceiver var3, Handler var4, int var5, String var6,
			Bundle var7) {
		this.app.sendOrderedBroadcast(var1, var2, var3, var4, var5, var6, var7);
	}

	
	public void sendOrderedBroadcastAsUser(Intent var1, UserHandle var2,
			String var3, BroadcastReceiver var4, Handler var5, int var6,
			String var7, Bundle var8) {
		this.app.sendOrderedBroadcastAsUser(var1, var2, var3, var4, var5, var6,
				var7, var8);
	}

	public void sendStickyBroadcast(Intent var1) {
		this.app.sendStickyBroadcast(var1);
	}

	
	public void sendStickyBroadcastAsUser(Intent var1, UserHandle var2) {
		this.app.sendStickyBroadcastAsUser(var1, var2);
	}

	public void sendStickyOrderedBroadcast(Intent var1, BroadcastReceiver var2,
			Handler var3, int var4, String var5, Bundle var6) {
		this.app.sendStickyOrderedBroadcast(var1, var2, var3, var4, var5, var6);
	}

	
	public void sendStickyOrderedBroadcastAsUser(Intent var1, UserHandle var2,
			BroadcastReceiver var3, Handler var4, int var5, String var6,
			Bundle var7) {
		this.app.sendStickyOrderedBroadcastAsUser(var1, var2, var3, var4, var5,
				var6, var7);
	}

	public void setTheme(int var1) {
		this.app.setTheme(var1);
	}

	public void setWallpaper(Bitmap var1) throws IOException {
		this.app.setWallpaper(var1);
	}

	public void setWallpaper(InputStream var1) throws IOException {
		this.app.setWallpaper(var1);
	}

	public void startActivities(Intent[] var1) {
		this.app.startActivities(var1);
	}

	
	public void startActivities(Intent[] var1, Bundle var2) {
		this.app.startActivities(var1, var2);
	}

	public void startActivity(Intent var1) {
		this.app.startActivity(var1);
	}

	
	public void startActivity(Intent var1, Bundle var2) {
		this.app.startActivity(var1, var2);
	}

	public boolean startInstrumentation(ComponentName var1, String var2,
			Bundle var3) {
		return this.app.startInstrumentation(var1, var2, var3);
	}

	public void startIntentSender(IntentSender var1, Intent var2, int var3,
			int var4, int var5) throws SendIntentException {
		this.app.startIntentSender(var1, var2, var3, var4, var5);
	}

	
	public void startIntentSender(IntentSender var1, Intent var2, int var3,
			int var4, int var5, Bundle var6) throws SendIntentException {
		this.app.startIntentSender(var1, var2, var3, var4, var5, var6);
	}

	public ComponentName startService(Intent var1) {
		return this.app.startService(var1);
	}

	public boolean stopService(Intent var1) {
		return this.app.stopService(var1);
	}

	public String toString() {
		return this.app.toString();
	}

	public void unbindService(ServiceConnection var1) {
		this.app.unbindService(var1);
	}

	public void unregisterActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks var1) {
		this.app.unregisterActivityLifecycleCallbacks(var1);
	}

	public void unregisterComponentCallbacks(ComponentCallbacks var1) {
		this.app.unregisterComponentCallbacks(var1);
	}

	public void unregisterOnProvideAssistDataListener(
			OnProvideAssistDataListener var1) {
		this.app.unregisterOnProvideAssistDataListener(var1);
	}

	public void unregisterReceiver(BroadcastReceiver var1) {
		this.app.unregisterReceiver(var1);
	}
}
