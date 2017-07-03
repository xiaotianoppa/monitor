package com.netflow.monitor;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.netflow.monitor.data.OsConfig;
import com.netflow.monitor.service.CheckMissionRunService;
import com.netflow.monitor.utils.CmdUtil;
import com.netflow.monitor.utils.DSUtils;
import com.netflow.monitor.utils.IoUtils;

public class MainActivity extends Activity {

	TextView mCheckStateTextView = null;
	TextView mNetflowStateTextView = null;
	private Button mStartCheckBtn = null;
	private Button mEndCheckBtn = null;

	public static final String TAG = "Monitor";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mCheckStateTextView = (TextView) findViewById(R.id.check_service_state);
		mNetflowStateTextView = (TextView) findViewById(R.id.netflow_service_state);

	}

	@Override
	protected void onStart() {
		super.onStart();

		initRootRight();
		initCheckServiceState();
		initNetflowServiceState();
		initEnvent();
	}

	public void initEnvent() {

		mStartCheckBtn = (Button) findViewById(R.id.start_monitor);
		mStartCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this, CheckMissionRunService.class);
				startService(intent);

				initCheckServiceState();
			}
		});

		mEndCheckBtn = (Button) findViewById(R.id.end_monitor);
		mEndCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, CheckMissionRunService.class);
				stopService(intent);

				initCheckServiceState();
			}
		});

	}
	
	public void initRootRight() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try{
					
					CmdUtil.execNetFlowCmd("ls /dev/input");
				}catch(Exception e){					
				}

			}
		}).start();
	}

	public void initCheckServiceState() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = mhandler.obtainMessage();

				String serviceName = "CheckMissionRunService";
				boolean isRunning = IoUtils.checkRunningServiceInfo(MainActivity.this, serviceName);
				Log.i(TAG, "check service:[" + serviceName + "] is running :"
						+ isRunning);

				int status = OsConfig.checkServiceIsStoped;// 还未开始监控
				if (isRunning == true) {
					status = OsConfig.checkServiceIsRunning;// 正在监控中
				}

				// DSUtils.getCurFormatTime();
				msg.what = status;
				mhandler.sendMessage(msg);

			}
		}).start();
	}

	public void initNetflowServiceState() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = mhandler.obtainMessage();

				String serviceName = "NetflowMissionService";
				boolean isRunning = IoUtils.checkRunningServiceInfo(
						MainActivity.this, serviceName);
				Log.i(TAG, "check service:[" + serviceName + "] is running :"
						+ isRunning);

				int status = OsConfig.netflowServiceIsStoped;// 服务已经结束
				if (isRunning == true) {
					status = OsConfig.netflowServiceIsRunning;// 开始运行
				}

				msg.what = status;
				mhandler.sendMessage(msg);

			}
		}).start();
	}

	// 处理从线程收到的消
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case OsConfig.checkServiceIsRunning:

					String runCheckMsg = "正在监控中。。。";
					mCheckStateTextView.setText(runCheckMsg);
					break;
				case OsConfig.checkServiceIsStoped:

					String stopCheckMsg = "还未监控！";
					mCheckStateTextView.setText(stopCheckMsg);
					break;
				case OsConfig.netflowServiceIsRunning:

					String runNetflowMsg = "流量任务正在执行。。。";
					
					String time =  IoUtils.readContent(IoUtils.prevRunTs);
					Log.i(TAG, "read prev runTs:"+time);
					if( !StringUtils.isEmpty(time) && StringUtils.isNumeric(time)){
						String prevTime = DSUtils.getCurFormatTime(Long.parseLong(time));
						
						runNetflowMsg = runNetflowMsg+"\n 上次运行时间:"+prevTime;
					}
					mNetflowStateTextView.setText(runNetflowMsg);
					break;
				case OsConfig.netflowServiceIsStoped:

					String stopNetflowMsg = "流量任务已经停止！";
					String runTime =  IoUtils.readContent(IoUtils.prevRunTs);
					Log.i(TAG, "read prev runTs:"+runTime);
					if( !StringUtils.isEmpty(runTime) && StringUtils.isNumeric(runTime)){

						String prevTime = DSUtils.getCurFormatTime(Long.parseLong(runTime));					
						stopNetflowMsg = stopNetflowMsg+"\n 上次运行时间:"+prevTime;
					}
					

					mNetflowStateTextView.setText(stopNetflowMsg);
					break;
				default:
					break;
				}

			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

		}
	};

	@Override
	protected void onStop() {
		super.onStop();
	}
}
