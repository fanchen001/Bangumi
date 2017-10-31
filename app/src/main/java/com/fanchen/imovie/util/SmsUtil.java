package com.fanchen.imovie.util;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsMessage;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.bmob.User;

import cn.bmob.v3.BmobQuery;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by fanchen on 2017/10/18.
 */
public class SmsUtil {

    private static OnMsmListener msmListener;

    /**
     *
     */
    public static void register() {
        SMSSDK.registerEventHandler(handler);
    }

    /**
     *
     */
    public static void unRegister() {
        SmsUtil.msmListener = null;
        SMSSDK.unregisterEventHandler(handler);
    }

    /**
     *
     * @param phone
     * @param msmListener
     */
    public static void  getVerificationCode(String phone,OnMsmListener msmListener){
        SmsUtil.msmListener = msmListener;
        SMSSDK.getVerificationCode("86", phone);
    }

    /**
     *
     * @param phone
     * @param verification
     */
    public static void  submitVerificationCode(String phone,String verification){
        SMSSDK.submitVerificationCode("86", phone, verification);
    }

    /**
     * 提取短信中的验证码
     *
     * @param intent
     * @return
     */
    public static String extractCode(Intent intent) {
        // 获取拦截到的短信数据
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus == null) {
            return null;
        }
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        return extractCode(messages);
    }

    /**
     * 提取短信中的数字
     *
     * @param messages
     * @return
     */
    public static String extractCode(SmsMessage[] messages) {
        // 将送来的短信合并自定义信息于StringBuilder当中
        for (SmsMessage message : messages) {
            String body = message.getDisplayMessageBody();
            if (body != null && body.indexOf("次元番") != -1) {
                // 提取短信中的数字
                StringBuilder sb = new StringBuilder();
                if (body != null && !"".equals(body)) {
                    for (int i = 0; i < body.length(); i++) {
                        if (body.charAt(i) >= 48 && body.charAt(i) <= 57) {
                            sb.append(body.charAt(i));
                        }
                    }
                }
                return sb.toString();
            }
        }
        return "";
    }

    private static EventHandler handler = new EventHandler() {
        /**
         * 在操作之后被触发
         *
         * @param event  参数1
         * @param result 参数2 SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.RESULT_ERROR表示操作失败
         * @param data   事件操作的结果
         */
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = Message.obtain();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }

    };

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msmListener != null) {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 提交验证码成功
                    msmListener.onSuccess(event);
                } else if (data instanceof Throwable) {
                    Throwable exption = ((Throwable) data);
                    msmListener.onFinal(exption);
                }
            }
        }

    };

    public interface OnMsmListener {
        /**
         *
         */
        void onSuccess(int event);

        /**
         * @param e
         */
        void onFinal(Throwable e);
    }
}
