package com.stephen.cli.project.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HomeKeyEventBroadcastReceiver extends BroadcastReceiver {
    private static final String SYSTEM_EVENT_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "homekey";
    private static final String SYSTEM_RECENT_APPS = "recentapps";
    private HomeKeyListenerHelper.HomeKeyListener listener;

    public HomeKeyEventBroadcastReceiver(HomeKeyListenerHelper.HomeKeyListener l) {
        listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent)return;
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
            String reason = intent.getStringExtra(SYSTEM_EVENT_REASON);
            if(null != reason){
                if(reason.equals(SYSTEM_HOME_KEY)){
                    if(null != listener)listener.onHomeKeyShortPressed();
                }else if(reason.equals(SYSTEM_RECENT_APPS)) {
                    if(null != listener)listener.onHomeKeyLongPressed();
                }//end of else if
            }//end of if
        }//end of if
    }
}
