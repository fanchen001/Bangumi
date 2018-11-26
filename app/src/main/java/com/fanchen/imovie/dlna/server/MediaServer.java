
package com.fanchen.imovie.dlna.server;

import android.util.Log;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;
import java.net.SocketException;

public class MediaServer {
    private final static String TAG = "MediaServer";

    public static final int PORT = 6660;
    private static final int VERSION = 1;
    private static final String DMS_DESC = "MSI MediaServer";
    private static final String DMR_DESC = "MSI MediaRenderer";
    private static final String deviceType = "MediaServer";

    private LocalDevice mLocalDevice;
    private HttpServer mHttpServer;

    public volatile static String IP_ADDRESS;

    public MediaServer() throws ValidationException, SocketException {
        DeviceType type = new UDADeviceType(deviceType, VERSION);
        DeviceDetails details = new DeviceDetails("DMS  (" + android.os.Build.MODEL + ")", new ManufacturerDetails(
                android.os.Build.MANUFACTURER), new ModelDetails(android.os.Build.MODEL, DMS_DESC, "v1"));
        String ip = UpnpUtil.getIP();
        IP_ADDRESS = ip;
        UDN udn = UpnpUtil.uniqueSystemIdentifier("GNaP-MediaServer", "localhost", "http://" + ip + "/" + PORT);
        // service.setManager(new DefaultServiceManager(service, ContentDirectoryService.class));

        mLocalDevice = new LocalDevice(new DeviceIdentity(udn), type, details, createDefaultDeviceIcon(), getLocalService());

        Log.v(TAG, "MediaServer device created: ");
        Log.v(TAG, "friendly name: " + details.getFriendlyName());
        Log.v(TAG, "manufacturer: " + details.getManufacturerDetails().getManufacturer());
        Log.v(TAG, "model: " + details.getModelDetails().getModelName());
        if (mHttpServer != null) {
            mHttpServer.stop();
            mHttpServer = null;
        }
        // start http server
        try {
            mHttpServer = new HttpServer(PORT);
        } catch (IOException ioe) {
            Log.e(TAG, "Couldn't start server:\n" + ioe);
            System.exit(-1);
        }
        Log.e(TAG, "Started Http Server on port " + PORT);
    }

    public LocalDevice getDevice() {
        return mLocalDevice;
    }

    public void stop() {
        if (mHttpServer != null) {
            mHttpServer.stop();
        }
    }

    private Icon createDefaultDeviceIcon() {
        return null;
    }

    private LocalService getLocalService() {
        return null;
    }

}
