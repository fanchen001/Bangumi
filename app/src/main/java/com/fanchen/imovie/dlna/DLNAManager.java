package com.fanchen.imovie.dlna;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.fanchen.imovie.dlna.server.MediaServer;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class DLNAManager {
    private static final String TAG = "ClingManager";

    private static final int MSG_DISCOVER_START = 0;
    private static final int MSG_DEVICE_ADDED = 1;
    private static final int MSG_DEVICE_REMOVED = 2;
    private static final int MSG_SEARCH = 3;
    private static final DeviceType DEVICE_TYPE = new UDADeviceType("MediaRenderer");
    private MediaServer mediaServer;

    public interface DeviceRefreshListener {
        void onDeviceRefresh();
    }

    private volatile static DLNAManager instance;
    private AndroidUpnpService upnpService;
    private DeviceRefreshListener mDeviceDiscoveryListener;
    private final List<Device> mDeviceList = new ArrayList<>();
    private final List<DeviceWarp> mDeviceWarpList = new ArrayList<>();
    private volatile boolean searchCmd = false;

    private DLNAManager(Context context) {
        if (context == null) {
            throw new NullPointerException("context must not be null!");
        }
        try {
            mediaServer = new MediaServer();
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), AndroidUpnpServiceImpl.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (mDeviceDiscoveryListener == null) {
                return;
            }
            switch (msg.what) {
                case MSG_DISCOVER_START:
                    mDeviceList.clear();
                    mDeviceDiscoveryListener.onDeviceRefresh();
                    break;
                case MSG_DEVICE_REMOVED:
                    Device removedDevice = (Device) msg.obj;
                    int removedPos = mDeviceList.indexOf(removedDevice);
                    if (removedPos >= 0) {
                        mDeviceList.remove(removedPos);
                        mDeviceDiscoveryListener.onDeviceRefresh();
                    }
                    break;
                case MSG_DEVICE_ADDED:
                    Device addedDevice = (Device) msg.obj;
                    int addedPos = mDeviceList.indexOf(addedDevice);
                    if (addedPos < 0) {
                        mDeviceList.add(addedDevice);
                        mDeviceDiscoveryListener.onDeviceRefresh();
                    }
                    break;
                case MSG_SEARCH:
                    search();
                    break;
                default:
                    break;
            }
        }
    };


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            upnpService.getRegistry().addDevice(mediaServer.getDevice());
            upnpService.getRegistry().addListener(mRegistryListener);
            if (searchCmd) {
                search();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService.getRegistry().shutdown();
            upnpService = null;
        }

//        @Override
//        public void onBindingDied(ComponentName name) {
//            upnpService.getRegistry().shutdown();
//            upnpService = null;
//        }
    };
    private final DefaultRegistryListener mRegistryListener = new DefaultRegistryListener() {

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            Log.e(TAG, "remoteDeviceDiscoveryFailed," + ex.getMessage());
            mHandler.obtainMessage(MSG_DEVICE_REMOVED, device).sendToTarget();
        }

        @Override
        public void deviceAdded(Registry registry, Device device) {
            if (device == null) {
                Log.e(TAG, "deviceAdded, device is null");
                return;
            }
            if (device.getType().equals(DEVICE_TYPE)) {
                Log.d(TAG, "deviceAdded," + device.getDetails().getFriendlyName());
                mHandler.obtainMessage(MSG_DEVICE_ADDED, device).sendToTarget();
            }
        }

        @Override
        public void deviceRemoved(Registry registry, Device device) {
            if (device == null) {
                Log.e(TAG, "deviceRemoved(),device device is null");
                return;
            }
            if (device.getType().equals(DEVICE_TYPE)) {
                Log.d(TAG, "deviceRemoved," + device.getDetails().getFriendlyName());
                mHandler.obtainMessage(MSG_DEVICE_REMOVED, device).sendToTarget();
            }
        }
    };

    public void setOnDeviceRefreshListener(DeviceRefreshListener listener) {
        this.mDeviceDiscoveryListener = listener;
    }

    public static DLNAManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DLNAManager.class) {
                if (instance == null) {
                    instance = new DLNAManager(context);
                }
            }
        }
        return instance;
    }


    public void search() {
        if (upnpService == null) {
            searchCmd = true;
            return;
        }
        searchCmd = false;
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getRegistry().addDevice(mediaServer.getDevice());
        upnpService.getControlPoint().search();
    }

    public List<DeviceWarp> getDeviceList() {
        if (mDeviceWarpList.size() != mDeviceList.size()) {
            mDeviceWarpList.clear();
            if (mDeviceList.size() > 0)
                for (Device device : mDeviceList) {
                    mDeviceWarpList.add(new DeviceWarp(device));
                }
        }
        return mDeviceWarpList;
    }

    public ControlPoint getControlPoint() {
        if (upnpService == null) {
            return null;
        }
        return upnpService.getControlPoint();
    }

    public void stop(Context context) {
        if (context == null) {
            throw new NullPointerException("context must not be null !");
        }
        context.getApplicationContext().unbindService(mServiceConnection);
        if (mediaServer != null) {
            mediaServer.stop();
        }
        mDeviceDiscoveryListener = null;
    }

    public static class DeviceWarp {

        public Device device;

        public DeviceWarp(Device device) {
            this.device = device;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DeviceWarp && device != null) {
                return device.equals(((DeviceWarp) o).device);
            }
            return super.equals(o);
        }

        @Override
        public String toString() {
            if (device == null) return "";
            DeviceDetails details = device.getDetails();
            if (details == null) return "";
            return details.getFriendlyName();
        }
    }
}
