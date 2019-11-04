package com.xyebank.stephen.push;

import android.content.Context;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushMessageReceiver extends OpenClientPushMessageReceiver {
    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage upsNotificationMessage) {
        if(null != upsNotificationMessage) {
            long msgId = upsNotificationMessage.getMsgId();
            String customeContent = upsNotificationMessage.getSkipContent();
            System.out.println("===com.stephen.push==VivoPush==onNotificationMessageClicked====>获取通知内容如下:msgId = " + msgId + ";customeContent=" + customeContent);
        }else{
            System.out.println("===com.stephen.push==VivoPush==onNotificationMessageClicked====>获取通知内容为空!");
        }
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        System.out.println("===com.stephen.push==VivoPush==onReceiveRegId====>"+regId);
        StephenPushUtils.getInstance().recordStephenPushToken(StephenPushUtils.PushTypeVIVO, regId);
    }
}
