package com.netflow.monitor.receiver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.netflow.monitor.MainActivity;
import com.netflow.monitor.service.CheckMissionRunService;
import com.netflow.monitor.utils.IoUtils;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.DONUT)
public class OsReceiver extends BroadcastReceiver {
	
    private static String TAG = "OSReceiver";
    
    private static final String START_MONITOR_ACTION = "start.monitor.service.action";
    
    private static final String END_MONITOR_ACTION = "end.monitor.service.action";

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    public void onReceive(final Context context, Intent intent) {
    	
        Log.i(TAG, " intent :" + intent);
        Log.i(TAG, " action :" + intent.getAction());
        Log.i(TAG, " data :" + intent.getData());

        String action = intent.getAction();

        if (action.equals(START_MONITOR_ACTION)) {
        	
        	String serviceName = "CheckMissionRunService";
			boolean isRunning = IoUtils.checkRunningServiceInfo(context, serviceName);
			Log.i(TAG, "check service:[" + serviceName + "] is running :"+ isRunning);

			if(isRunning == false){
				
				Intent startIntent = new Intent(context, CheckMissionRunService.class);
	            context.startService(startIntent);
			}
        	
        }else if(action.equals(END_MONITOR_ACTION)) {
        	Intent endIntent = new Intent(context, CheckMissionRunService.class);
            context.stopService(endIntent);
        }
    }

    
}
