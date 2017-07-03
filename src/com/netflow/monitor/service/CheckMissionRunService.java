package com.netflow.monitor.service;

import org.apache.commons.lang.StringUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.netflow.monitor.utils.CmdUtil;
import com.netflow.monitor.utils.DSUtils;
import com.netflow.monitor.utils.IoUtils;

public class CheckMissionRunService extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	public static final String TAG = "checkNetflowMissionRun";
	private static final String REBOOT_MOBILE_ACT = "reboot.mobile.service.action";
	private static final String STOP_SERVICE_ACT = "stop.netflow.service.action";
	public static final String autoRunnerPath = "/data/data/jp.jun_nama.test.utf7ime/tools/AutoRunner.jar";
	private static boolean isFinished = false;

	private MyBinder myBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return myBinder;
	}

	public class MyBinder extends Binder {

		public CheckMissionRunService getService1() {
			return CheckMissionRunService.this;
		}
	}

	// 处理从线程收到的消
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			try {

				if (isFinished == true) {
					return;
				}

				int flag = msg.what;
				Context context = getApplicationContext();
				switch (flag) {
				case 1: // 正在工作
					checkMissionStatus();
					break;
				case 2: // 已经停止了
					continueStartMission(context);
					break;
				default:
					stopSelf();// 停止
					break;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isFinished = false;
		Log.i(TAG, "check netflow mission service start run !!!,startId:"+startId);

		checkMissionStatus();
		return START_REDELIVER_INTENT;

	}

	public static final long maxWaitTime = 20 * 60 *1000;//20分钟
	public void checkMissionStatus() {

		new Thread(new Runnable() {
			public void run() {

				try {

					int i = 0;
					while(i++ < 10){						
						DSUtils.sleep2s();
						if (isFinished == true) {
							return;
						}
						
					}
					
					if (isFinished == true) {
						return;
					}

					Message msg = mServiceHandler.obtainMessage();
					
					long prevRunTs = 0;
					String runTime =  IoUtils.readContent(IoUtils.prevRunTs);
//					Log.i(TAG, "read prev runTs:"+runTime);
					if( !StringUtils.isEmpty(runTime) && StringUtils.isNumeric(runTime)){

						 prevRunTs = Long.parseLong(runTime);

					}

					if( prevRunTs > 0){
//						Log.i(TAG, "read prev run time:"+DSUtils.getCurFormatTime(prevRunTs));
						if(System.currentTimeMillis() - prevRunTs > maxWaitTime){
							msg.what = 2;
							Log.i(TAG, "不正常停止!");
							mServiceHandler.sendMessage(msg);
							return;
						}
					}

					// 如果不是正常停止
					
					Log.i(TAG, "netflow service is running!"+ DSUtils.getCurFormatTime(System.currentTimeMillis()));
					msg.what = 1;
					mServiceHandler.sendMessage(msg);
					return;

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}).start();

	}

	public void continueStartMission(final Context mContext) {
		
		new Thread(new Runnable() {
			public void run() {

				Message msg = mServiceHandler.obtainMessage();
				try {

				
					Intent stopIntent = new Intent(STOP_SERVICE_ACT);
					mContext.sendBroadcast(stopIntent);
					DSUtils.sleep5s();
					
					Log.i(TAG, "netflow mission service has innormal stop!!! send action to reboot !");
//					Intent intent = new Intent(REBOOT_MOBILE_ACT);
//					mContext.sendBroadcast(intent);
					
					doRebootMobile();
					
					DSUtils.sleep60s();
					if (isFinished == true) {
						return;
					}
					
					msg.what = 1;
					mServiceHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					
					DSUtils.sleep30s();
					msg.what = 1;
					mServiceHandler.sendMessage(msg);
				}
			}
		}).start();
		
		

	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "check netflow mission service stop run !!! ");

		new Thread(new Runnable() {
			@Override
			public void run() {
				isFinished = true;
			}
		}).start();
		super.onDestroy();
	}
	
	   public void doRebootMobile() {

	        new Thread(new Runnable() {
	            @Override
	            public void run() {

	                CmdUtil.execRestart();
	                
	                DSUtils.sleep3s();
	                killPreAutomator();
	                for(int i=0; i< 5;i++){
	                	 String cmdText = " uiautomator runtest " + autoRunnerPath
	                			 + "  -c dowork.RebootRunner ";
	                     CmdUtil.execNetFlowCmd(cmdText);
	                     DSUtils.sleep5s();
	                }

	            }
	        }).start();

	    }
	   
	   private void killPreAutomator() {
	        //  String psCmd = CmdUtil.execPsGrep("uiautomator");
	        String psCmd = CmdUtil.execNetFlowCmd("ps uiautomator");
	        if (StringUtils.isEmpty(psCmd) == false) {
	            String[] ps = psCmd.split("\\s+");
	            if (ps.length > 10) {
	                //	psCmd = psCmd.trim().replace("%0A", "");
	                String pid = ps[9].trim();
	                Log.i(TAG, "utf7ime kill pid:" + pid);
	                CmdUtil.execNetFlowCmd(" kill " + pid);
	            }
	        }
	    }


}
