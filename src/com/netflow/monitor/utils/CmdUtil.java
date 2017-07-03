package com.netflow.monitor.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import android.util.Log;

public class CmdUtil {

    public static boolean isBusyBoxEnabled = true;
    
    public static final String TAG = "netflow_cmd_monitor";

    public static String ENCODING  = "utf-8";

    /**
     * 确保没有单引号的 能够临时写一下
     * @param path
     * @param content
     * @return
     */
    public static String execWriteSimpleFile(String path, String content) {
        return execNetFlowCmd(" echo '" + content + "' > " + path);
    }
    
    public static String execChmodFile(String path, String limit) {
        return execNetFlowCmd(" chmod  "+limit+"  " + path);
    }
    
    public static String execPsGrep(String name) {
        return execNetFlowCmd(" ps | grep  " + name);
    }

    public static String readFile(String path) {
        return execNetFlowCmd(" cat  " + path);
    }
    
    public static String execMd5(String path) {
        String res = CmdUtil.execNetFlowCmd(String.format(" md5 %s", path));
        int spaceIndex = res.indexOf(" ");
        if (spaceIndex > 0) {
            res = res.substring(0, spaceIndex);
        }
        return res;
    }

    /*
    public static String execNetFlowCmd(String cmd) {
        Log.i(TAG, " exec cmd :" + cmd);

        // Run command
        Process p = null;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(" " + cmd + "  ");
            os.writeBytes("\nexit\n");
            os.flush();
            p.waitFor();
            String execResult = IOUtils.toString(p.getInputStream(), Charsets.UTF_8);
            Log.i(TAG, " exec res :" + execResult);
            return execResult;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
         //   if (p != null)
           //     p.destroy();
        	   try { 
                   if (p != null) { 
                       // use exitValue() to determine if process is still running.  
                       p.exitValue(); 
                   } 
               } catch (IllegalThreadStateException e) { 
                   // process is still running, kill it. 
                   p.destroy(); 
               }  
        }
        return null;
    }*/

    public static String execNetFlowCmd(String cmd) {
        Log.i(TAG, " exec cmd :" + cmd);

        // Run command
        Process p = null;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(" " + cmd + "  ");
            os.writeBytes("\nexit\n");
            os.flush();
            final InputStream is1 = p.getInputStream();
            //获取进程的错误流
            final InputStream is2 = p.getErrorStream();
            new Thread() {
                public void run() {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
                    try {
                        String line1 = null;
                        while ((line1 = br1.readLine()) != null) {
                            if (line1 != null){
                                Log.i("Netflow inputstream: ", line1);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally{
                        try {
                            is1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread() {
                public void  run() {
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
                    try {
                        String line2 = null ;
                        while ((line2 = br2.readLine()) !=  null ) {
                            if (line2 != null){
                                Log.i("Netflow inputstream: ", line2);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally{
                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            p.waitFor();
            String execResult = IOUtils.toString(p.getInputStream(), Charsets.UTF_8);
//            Log.i(TAG, " exec res :" + execResult);
            return execResult;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            //   if (p != null)
            //     p.destroy();
            try {
                if (p != null) {
                    // use exitValue() to determine if process is still running.
                    p.exitValue();
                }
            } catch (IllegalThreadStateException e) {
                // process is still running, kill it.
                p.destroy();
            }
        }
        return null;
    }



    public static void execRestart() {
        execNetFlowCmd("busybox reboot");
        execNetFlowCmd("reboot");
    }

    static Boolean IS_BLUESTACK = null;

    public static Boolean isBlueStack() {
        if (IS_BLUESTACK != null) {
            return IS_BLUESTACK;
        }

        String aux = execNetFlowCmd(" ps  | grep bluestack");
        Log.i(TAG, ">>>>  aux for next :" + aux);

        if (aux != null && aux.contains("bluestack")) {
            IS_BLUESTACK = Boolean.TRUE;
        } else {
            IS_BLUESTACK = Boolean.FALSE;
        }

        return IS_BLUESTACK;
    }

    /**
     * 点击脚本是否在运行
     * 
     * @return
     */
    public static boolean isClickRunning() {

        String res = execNetFlowCmd(" ps  | grep AutoRunner.jar ");
        return res != null && res.contains("AutoRunner");
    }

    public static String execCpRecurse(String src, String target) {

        return CmdUtil.execNetFlowCmd(String.format(" cp -r %s %s", src, target));
    }

    public static String execCpFource(String src, String target) {
        return CmdUtil.execNetFlowCmd(String.format(" cp -rf %s %s", src, target));
    }

    public static String execMvRecurse(String src, String target) {

        return CmdUtil.execNetFlowCmd(String.format(" mv  %s %s", src, target));
    }

    public static String execMkdir(String src) {
        return CmdUtil.execNetFlowCmd(String.format(" mkdir -p %s ", src));
    }

    public static String execRmRecurse(String src) {
        return CmdUtil.execNetFlowCmd(String.format(" rm -r %s ", src));
    }

    public static String execChown(String uname, String settingsDbPath) {
        if (isBusyBoxEnabled) {
            return CmdUtil.execNetFlowCmd(String.format(" busybox chown -R %s:%s %s", uname, uname, settingsDbPath));
        } else {
            return CmdUtil.execNetFlowCmd(String.format(" chown %s:%s %s", uname, uname, settingsDbPath));
        }

    }

    public static String execChown(String creator, String groupname, String settingsDbPath) {
        if (isBusyBoxEnabled) {
            return CmdUtil.execNetFlowCmd(String.format(" busybox chown -R %s:%s %s", creator, groupname, settingsDbPath));
        } else {
            return CmdUtil.execNetFlowCmd(String.format(" chown %s:%s %s", creator, groupname, settingsDbPath));
        }

    }

//    public static String execChown(int uid, String settingsDbPath) {
//        if (isBusyBoxEnabled) {
//            return CmdUtil.execNetFlowCmd(String.format(" busybox chown -R  %d:%d %s", uid, uid, settingsDbPath));
//        } else {
//            return CmdUtil.execNetFlowCmd(String.format(" chown %d:%d %s", uid, uid, settingsDbPath));
//        }
//    }

    public static String execEcho(String empty) {
        return CmdUtil.execNetFlowCmd("echo '" + empty + "'");
    }

    public static String mkdir(File file) {
        return mkdir(file.getAbsolutePath());
    }

    public static String mkdir(String absPath) {
        return CmdUtil.execNetFlowCmd(String.format("mkdir -p  %s", absPath));
    }

    public static String mkdir(File file, String groupName) {
        Log.i(TAG, String.format(" mkdir [%s] with name [%s]", file.getAbsolutePath(), groupName));
        String path = file.getAbsolutePath();
        String mkdirRes = mkdir(path);
        Log.i(TAG, "mkdir res :" + mkdirRes);
        return execChown(groupName, path);
    }

    public static String execLs(String path) {
        return execNetFlowCmd(String.format(" ls -al %s", path));
    }

    public static boolean existFile(String string) {
        String lsRes = execLs(string);
        Log.i(TAG, " ls res ["+lsRes+"], for file ["+string+"]");
        if (StringUtils.isBlank(lsRes) || lsRes.contains("No such file or directory ")) {
            return false;
        }

        return true;
    }
    
    public static String execInstallApk(String path) {
        return CmdUtil.execNetFlowCmd(" pm install "+path);
    }

    public static String getFileCreatorName(String path, String name, String cmdFormat) {

        String lsRes = execNetFlowCmd(String.format(cmdFormat, path, name));
        if (StringUtils.isBlank(lsRes)) {
            Log.i(TAG, " no ls res for file :" + path + " with nextfile:" + name);
            return null;
        }
        Log.i(TAG, " found file ls res :" + path + " with content ::" + lsRes);

        //        String[] arr = lsRes.split("\\s");＼
        String[] arr = lsRes.replaceAll("\\t", " ").replaceAll(" +", " ").split(" ");
        Log.i(TAG, " split res :" + StringUtils.join(arr, ','));

        String creator = arr[1];
        String group = arr[2];

        return creator;
    }

    public static String getFileGroupUid(String absPath, String fileName) {
        return getFileCreatorName(absPath, fileName, " ls -nl %s | grep %s");
    }

    public static String getFileGroupName(String absPath, String fileName) {
        return getFileCreatorName(absPath, fileName, " ls -al %s | grep %s");
    }

}
