package com.netflow.monitor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EncodingUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class IoUtils {

	public static final String otherAppPackageName = "jp.jun_nama.test.utf7ime";
	public static final String TAG = "IOUtils";
	public static final String prevRunTs = "/data/data/com.netflow.monitor/prevRunTs";
	

	public static boolean checkRunningServiceInfo(Context mContext, String serviceName) {

		int defaultNum = 50;
		ActivityManager mAm = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runServiceList = mAm
				.getRunningServices(defaultNum);

		if (runServiceList != null) {
			Log.i(TAG, "running service size:" + runServiceList.size());
		}
		
		if(runServiceList.size() >= defaultNum){
			runServiceList = mAm.getRunningServices(2 * defaultNum);
		}

		for (RunningServiceInfo runServiceInfo : runServiceList) {

			ComponentName serviceCMP = runServiceInfo.service;
			String curServiceName = serviceCMP.getClassName().toString();
			if (StringUtils.isEmpty(curServiceName)) {
				continue;
			}

//			Log.i(TAG, "running service name:" + curServiceName);
			if(curServiceName.contains(serviceName)){
				Log.i(TAG, "running service name:" + curServiceName);
				return true;
			}

		}

		return false;
	}
	
	 public static String readContent(String filePath){
	    	String content = ""; 
	    	FileInputStream fin = null;
	    	File file = new File(filePath);
	        try { 
	        	if(!file.exists()){
	        		return null;
	        	}
	        	fin = new FileInputStream(file);
	            int length = fin.available(); 
	            byte[] buffer = new byte[length]; 
	            fin.read(buffer); 
	            content = EncodingUtils.getString(buffer, "UTF-8"); 
	            content = trimValue(content);
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        }finally{
	        	if(fin != null){
	        		 try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
	        	}
	           
	        } 
	        return content; 

	    }
	 
	    
	    private static String trimValue(String content){
	    	if(StringUtils.isEmpty(content)){
	    		return "";
	    	}
	    	return content.trim();
	    }

}
