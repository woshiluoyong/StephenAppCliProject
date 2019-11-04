package com.stephen.cli.project.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HomeKeyListenerHelper {
    private Context context;
    private BroadcastReceiver receiver;

    public HomeKeyListenerHelper(Context ctx) {
        context = ctx;
    }

    public void registerHomeKeyListener(HomeKeyListener l) {
        try {
            if(null != context){
                receiver = new HomeKeyEventBroadcastReceiver(l);
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                if(null != context && null != receiver)context.registerReceiver(receiver, intentFilter);
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterHomeKeyListener() {
        try{if(null != context && null != receiver)context.unregisterReceiver(receiver);}catch(Exception e){}
    }

    public interface HomeKeyListener {
        void onHomeKeyShortPressed();
        void onHomeKeyLongPressed();
    }
}
