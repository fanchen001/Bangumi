package com.fanchen.imovie.dlna;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.fanchen.imovie.dlna.server.MediaServer;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetCurrentTransportActions;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.igd.callback.GetStatusInfo;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;
import org.fourthline.cling.support.model.item.VideoItem;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.seamless.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class DLNAPlayer {
    private static final String TAG = "ClingPlayer";
    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
    private static final int MSG_INVALID_URL = 0x01;
    private static final int MSG_GET_CONNECTION_FAILED = 0x02;
    private static final int MSG_GET_CONNECTION_SUCCESS = 0x03;
    private static final int MSG_AV_TRANSPORT_NOT_FOUND = 0x04;

    private static final int MSG_SET_URL_SUCCESS = 0x05;
    private static final int MSG_ON_PLAY = 0x06;
    private static final int MSG_ON_PAUSE = 0x07;
    private static final int MSG_ON_STOP = 0x08;
    private static final int MSG_SEEK_COMPLETE = 0x09;
    private static final int MSG_ON_GET_MEDIAINFO = 0x10;
    private static final int MSG_ON_GET_TRANSPORT_STATE = 0x11;

    private static final int MSG_VOLUME_CHANGED = 0x12;
    private static final int MSG_MUTE_STATUS_CHANGED = 0x13;
    private static final int MSG_TIMELINE_CHANGED = 0x14;

    private static final int MSG_SET_URI_FAILED = -1 * MSG_SET_URL_SUCCESS;
    private static final int MSG_PLAY_FAILED = -1 * MSG_ON_PLAY;
    private static final int MSG_PAUSE_FAILED = -1 * MSG_ON_PAUSE;
    private static final int MSG_STOP_FAILED = -1 * MSG_ON_STOP;
    private static final int MSG_SEEK_FAILED = -1 * MSG_SEEK_COMPLETE;
    private static final int MSG_SET_VOLUME_FAILED = -1 * MSG_VOLUME_CHANGED;
    private static final int MSG_SET_MUTE_FAILED = -0x12;
    private static final int MSG_GET_VOLUME_FAILED = -0x13;
    private static final int MSG_GET_MUTE_FAILED = -0x14;

    public interface EventListener {
        void onPlay();

        void onGetMediaInfo(MediaInfo mediaInfo);

        void onPlayerError();

        void onTimelineChanged(PositionInfo positionInfo);

        void onSeekCompleted();

        void onPaused();

        void onMuteStatusChanged(boolean isMute);

        void onVolumeChanged(int volume);

        void onStop();
    }

    private EventListener mListener;
    private ControlPoint mControlPoint;
    private Device mDevice;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVALID_URL:
                    break;
                case MSG_GET_CONNECTION_FAILED:
                    break;
                case MSG_GET_CONNECTION_SUCCESS:
                    break;
                case MSG_AV_TRANSPORT_NOT_FOUND:
                    break;
                case MSG_SET_URL_SUCCESS:
                    break;
                case MSG_ON_PLAY:
                    if (mListener != null) {
                        mListener.onPlay();
                    }
                    break;
                case MSG_ON_PAUSE:
                    if (mListener != null) {
                        mListener.onPaused();
                    }
                    break;
                case MSG_ON_STOP:
                    if (mListener != null) {
                        mListener.onStop();
                    }
                    break;
                case MSG_SEEK_COMPLETE:
                    if (mListener != null) {
                        mListener.onSeekCompleted();
                    }
                    break;
                case MSG_ON_GET_MEDIAINFO:
                    MediaInfo mediaInfo = (MediaInfo) msg.obj;
                    if (mListener != null) {
                        mListener.onGetMediaInfo(mediaInfo);
                    }
                    break;
                case MSG_VOLUME_CHANGED:
                    if (mListener != null) {
                        mListener.onVolumeChanged(msg.arg1);
                    }
                    break;
                case MSG_MUTE_STATUS_CHANGED:
                    if (mListener != null) {
                        mListener.onMuteStatusChanged((Boolean) msg.obj);
                    }
                    break;
                case MSG_TIMELINE_CHANGED:
                    if (mListener != null) {
                        mListener.onTimelineChanged((PositionInfo) msg.obj);
                    }
                    break;
                case MSG_SET_URI_FAILED:
                    break;
                case MSG_PLAY_FAILED:
                case MSG_PAUSE_FAILED:
                case MSG_STOP_FAILED:
                    if (mListener != null) {
                        mListener.onPlayerError();
                    }
                    break;
                case MSG_ON_GET_TRANSPORT_STATE:
                    TransportInfo transportInfo = (TransportInfo) msg.obj;
                    if (transportInfo != null) {
                        TransportStatus status = transportInfo.getCurrentTransportStatus();
                        if (mListener != null && (status == TransportStatus.OK || status == TransportStatus.CUSTOM)) {
                            TransportState state = transportInfo.getCurrentTransportState();
                            switch (state) {
                                case STOPPED:
                                case NO_MEDIA_PRESENT:
                                    mListener.onStop();
                                    break;
                                case PAUSED_PLAYBACK:
                                    mListener.onPaused();
                                    break;
                                case PLAYING:
                                    mListener.onPlay();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            mListener.onPlayerError();
                        }
                    }
                    break;
                case MSG_SEEK_FAILED:
                    break;
                case MSG_SET_VOLUME_FAILED:
                    break;
                case MSG_GET_MUTE_FAILED:
                    break;
                default:
                    break;
            }
        }
    };

    public void addListener(EventListener listener) {
        this.mListener = listener;
    }

    public void removeListener() {
        this.mListener = null;
    }

    public void setUp(Device device, ControlPoint controlPoint) {
        this.mControlPoint = controlPoint;
        this.mDevice = device;
    }

    public void play(String name, String url) {
        setUriAndPlay(name, url);
    }

    public void resume() {
        playInner();
    }

    public void pause() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "pause failed, AVTransport service is null.");
            mHandler.obtainMessage(MSG_PAUSE_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new Pause(service) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                Log.d(TAG, "pause success");
                mHandler.obtainMessage(MSG_ON_PAUSE).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "pause failed," + defaultMsg);
                mHandler.obtainMessage(MSG_PAUSE_FAILED).sendToTarget();
            }
        });
    }

    public void getTransportInfo() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "getTransportInfo failed, AVTransport service is null.");
            return;
        }
        mControlPoint.execute(new GetTransportInfo(service) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                Log.d(TAG, "getTransportInfo success");
            }

            @Override
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                String msg = "getTransportInfo received. curState:" + transportInfo.getCurrentTransportState() + ",curStatus:" + transportInfo.getCurrentTransportStatus()
                        + ",speed:" + transportInfo.getCurrentSpeed();
                mHandler.obtainMessage(MSG_ON_GET_TRANSPORT_STATE, transportInfo).sendToTarget();
                Log.d(TAG, msg);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getTransportInfo failed," + defaultMsg);
            }
        });
    }

    public void getCurrentTransportActions() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "getCurrentTransportActions failed, AVTransport service is null.");
            return;
        }
        mControlPoint.execute(new GetCurrentTransportActions(service) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "getCurrentTransportActions success");
            }

            @Override
            public void received(ActionInvocation actionInvocation, TransportAction[] actions) {
                StringBuilder sb = new StringBuilder();
                if (actions != null && actions.length != 0) {
                    sb.append("[");
                    for (TransportAction action : actions) {
                        sb.append(action);
                        sb.append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append("]");
                }
                Log.d(TAG, "getCurrentTransportActions received:" + sb.toString());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getCurrentTransportActions failed," + defaultMsg);
            }
        });
    }

    public void seekTo(final int timeSeconds) {
        if (timeSeconds < 0) {
            Log.w(TAG, "seek failed,invalid param timeSeconds:" + timeSeconds);
            return;
        }
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "seekTo failed, AVTransport service is null.");
            mHandler.obtainMessage(MSG_SEEK_FAILED).sendToTarget();
            return;
        }
        final String time = secondsToString(timeSeconds);
        mControlPoint.execute(new Seek(service, time) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "seekTo success");
                mHandler.obtainMessage(MSG_SEEK_COMPLETE).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "seek to " + time + " failed ," + defaultMsg);
                mHandler.obtainMessage(MSG_SEEK_FAILED).sendToTarget();
            }
        });
    }

    public void setVolume(final int volume) {
        check();
        Service service = mDevice.findService(new UDAServiceType("RenderingControl"));
        if (service == null) {
            Log.w(TAG, "setVolume failed, RenderingControl service is null.");
            mHandler.obtainMessage(MSG_SET_VOLUME_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new SetVolume(service, volume) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "setVolume success");
                mHandler.obtainMessage(MSG_VOLUME_CHANGED, volume, 0).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "setVolume failed," + defaultMsg);
                mHandler.obtainMessage(MSG_SET_VOLUME_FAILED).sendToTarget();
            }
        });
    }

    public void setMute(final boolean mute) {
        check();
        Service service = mDevice.findService(new UDAServiceType("RenderingControl"));
        if (service == null) {
            Log.w(TAG, "setMute failed, RenderingControl service is null.");
            mHandler.obtainMessage(MSG_SET_MUTE_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new SetMute(service, mute) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "setMute success");
                mHandler.obtainMessage(MSG_MUTE_STATUS_CHANGED, mute).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "setMute failed," + defaultMsg);
                mHandler.obtainMessage(MSG_SET_MUTE_FAILED).sendToTarget();
            }
        });
    }


    public void stop() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "stop failed, AVTransport service is null.");
            mHandler.obtainMessage(MSG_STOP_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new Stop(service) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "stop success.");
                mHandler.obtainMessage(MSG_ON_STOP).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "stop failed," + defaultMsg);
                mHandler.obtainMessage(MSG_STOP_FAILED).sendToTarget();
            }
        });
    }

    public void getConnectionStatus() {
        check();
        Service service = mDevice.findService(new UDAServiceId("WANIPConnection"));
        if (service == null) {
            Log.w(TAG, "getConnectionStatus failed, WANIPConnection service is null.");
            mHandler.obtainMessage(MSG_GET_CONNECTION_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(
                new GetStatusInfo(service) {
                    @Override
                    protected void success(Connection.StatusInfo statusInfo) {
                        Log.d(TAG, "getConnectionStatus success,status:" + statusInfo.getStatus() + ",uptimeSeconds:" +
                                statusInfo.getUptimeSeconds() + ",lastError:" + statusInfo.getLastError());
                        mHandler.obtainMessage(MSG_GET_CONNECTION_SUCCESS, statusInfo).sendToTarget();
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Log.e(TAG, "getConnectionStatus failed," + defaultMsg);
                        mHandler.obtainMessage(MSG_GET_CONNECTION_FAILED).sendToTarget();
                    }
                }
        );
    }


    public void getCurrentPosition() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "getCurrentPosition failed, AVTransport service is null.");
            return;
        }
        mControlPoint.execute(new GetPositionInfo(service) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                Log.d(TAG, "getCurrentPosition success");

            }

            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                Log.i(TAG, "getCurrentPosition received," + positionInfo);
                mHandler.obtainMessage(MSG_TIMELINE_CHANGED, positionInfo).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getCurrentPosition failed," + defaultMsg);
            }
        });
    }

    public void getVolume() {
        check();
        Service service = mDevice.findService(new UDAServiceType("RenderingControl"));
        if (service == null) {
            Log.w(TAG, "getVolume failed, RenderingControl service is null.");
            mHandler.obtainMessage(MSG_GET_VOLUME_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new GetVolume(service) {
            @Override
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                Log.e(TAG, "getVolume success," + currentVolume);
                mHandler.obtainMessage(MSG_VOLUME_CHANGED, currentVolume, 0).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getVolume failed," + defaultMsg);
                mHandler.obtainMessage(MSG_GET_VOLUME_FAILED).sendToTarget();
            }
        });
    }

    public void getMute() {
        check();
        Service service = mDevice.findService(new UDAServiceType("RenderingControl"));
        if (service == null) {
            Log.w(TAG, "getMute failed, RenderingControl service is null.");
            mHandler.obtainMessage(MSG_GET_MUTE_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new GetMute(service) {
            @Override
            public void received(ActionInvocation actionInvocation, boolean currentMute) {
                Log.d(TAG, "getMute received,currentMute:" + currentMute);
                mHandler.obtainMessage(MSG_MUTE_STATUS_CHANGED, currentMute).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getMute failed," + defaultMsg);
                mHandler.obtainMessage(MSG_GET_MUTE_FAILED).sendToTarget();
            }
        });
    }

    public void getMediaInfo() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "getMediaInfo failed, AVTransport service is null.");
            return;
        }
        mControlPoint.execute(new GetMediaInfo(service) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                Log.i(TAG, "getMediaInfo success");
            }

            @Override
            public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                Log.i(TAG, "getMediaInfo received," + mediaInfo.getMediaDuration() + "," + mediaInfo.getCurrentURI() + "," + mediaInfo.getCurrentURIMetaData());
                mHandler.obtainMessage(MSG_ON_GET_MEDIAINFO, mediaInfo).sendToTarget();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "getMediaInfo failed," + defaultMsg);
            }
        });
    }

    private void setUriAndPlay(String name, String uri) {
        check();
        if (TextUtils.isEmpty(uri)) {
            Log.e(TAG, "播放地址为空！");
            mHandler.obtainMessage(MSG_INVALID_URL).sendToTarget();
            return;
        }
        if (!(uri.startsWith("http") || uri.startsWith("rtsp"))) {
            if(uri.startsWith("/")){
                uri = "http://" + MediaServer.IP_ADDRESS + ":" + MediaServer.PORT  + uri;
            }else{
                uri = "http://" + MediaServer.IP_ADDRESS + ":" + MediaServer.PORT + "/" + uri;
            }
        }
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "setAVTransportURI failed, AVTransport service is null.");
            mHandler.obtainMessage(MSG_SET_URI_FAILED).sendToTarget();
            return;
        }
        String mediaData = pushMediaToRender(uri, "id", name, "0");
        mControlPoint.execute(new SetAVTransportURI(service, uri, mediaData) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "setAVTransportURI success.");
                mHandler.obtainMessage(MSG_SET_URL_SUCCESS).sendToTarget();
                playInner();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.d(TAG, "setAVTransportURI failed," + defaultMsg);
                mHandler.obtainMessage(MSG_SET_URI_FAILED).sendToTarget();
            }
        });
    }

    private void playInner() {
        check();
        Service service = mDevice.findService(new UDAServiceType("AVTransport"));
        if (service == null) {
            Log.w(TAG, "play failed, AVTransport service is null.");
            mHandler.obtainMessage(MSG_PLAY_FAILED).sendToTarget();
            return;
        }
        mControlPoint.execute(new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG, "play failed," + defaultMsg);
                mHandler.obtainMessage(MSG_PLAY_FAILED).sendToTarget();
            }

            @Override
            public void success(ActionInvocation invocation) {
                Log.e(TAG, "play success");
                mHandler.obtainMessage(MSG_ON_PLAY).sendToTarget();
                getMediaInfo();
            }
        });
    }

    private void check() {
        if (mControlPoint == null) {
            throw new NullPointerException("mControlPoint must not be null,you should invoke" +
                    " setControlPoint(ControlPoint) method first.");
        }
        if (mDevice == null) {
            throw new NullPointerException("MediaRender device must not be null.");
        }
    }


    /**
     * 把时间戳转换成 00:00:00 格式
     *
     * @param secs 时间(s)
     * @return 00:00:00 时间格式
     */
    public static String secondsToString(int secs) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        int seconds = secs % 60;
        int minutes = (secs / 60) % 60;
        int hours = secs / 3600;
        formatBuilder.setLength(0);
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    /**
     * 把 00:00:00 格式转成时间戳
     *
     * @param formatTime 00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    public static int stringToSeconds(String formatTime) {
        if (TextUtils.isEmpty(formatTime)) {
            return 0;
        }

        String[] tmp = formatTime.split(":");
        if (tmp.length < 3) {
            return 0;
        }
        int second = Integer.valueOf(tmp[0]) * 3600 + Integer.valueOf(tmp[1]) * 60 + Integer.valueOf(tmp[2]);
        return second * 1000;
    }

    private String pushMediaToRender(String url, String id, String name, String duration) {
        long size = 0;
        long bitrate = 0;
        Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);

        String creator = "unknow";
        String resolution = "unknow";
        VideoItem videoItem = new VideoItem(id, "0", name, creator, res);

        String metadata = createItemMetadata(videoItem);
        Log.e(TAG, "metadata: " + metadata);
        return metadata;
    }

    private String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));

        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        // metadata.append(String.format("<upnp:album>%s</upnp:album>",
        // item.get);

        // <res protocolInfo="http-get:*:audio/mpeg:*"
        // resolution="640x478">http://192.168.1.104:8088/Music/07.我醒著做夢.mp3</res>

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolInfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolInfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }
            Log.e(TAG, "protocolInfo: " + protocolInfo);

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            // metadata.append(String.format("<res %s>", protocolInfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolInfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }
}
