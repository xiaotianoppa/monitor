package com.netflow.monitor.utils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;

public class DSUtils {

    public static Object EMPTY_OBJ = new Object();
    
    public static String SID_COOKIE_VALUE = ""; //定义一个静态的字段，保存_sid_字段
    
    public static String[] domainArr = new String[]{"www.154158.com","wx.tianxiaomao.com","kb.sutuijingling.com"};

    public static final void sleep250ms() {
        sleepQuietly(250L);
    }

    public static final void sleep1s() {
        sleepQuietly(1000L);
    }

    public static final void sleep60s() {
        sleepQuietly(60000L);
    }

    public static final void sleep2s() {
        sleepQuietly(2000L);
    }

    public static final void sleep3s() {
        sleepQuietly(3000L);
    }
    
    public static final void sleep5s() {
        sleepQuietly(5000L);
    }

    public static final void sleep7s() {
        sleepQuietly(7000L);
    }

    public static final void sleep10s() {
        sleepQuietly(10000L);
    }
    
    public static final void sleep30s() {
        sleepQuietly(30000L);
    }

    public static final void sleepQuietly(long millis) {
        baseSleep(millis);
    }
    
    public static final void baseSleep(long millis){
        try {        	
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void consumeQuite(HttpEntity entity) {

        if (entity == null) {
            return;
        }
        try {
            entity.consumeContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断Collection是否为空。
     * 
     * @param collection
     *            要判断的Collection。
     * @return 如果Collection为 null 或者 {@link Collection#isEmpty()} 返回true时，则返回
     *         true。
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断Map是否为空。
     * 
     * @param map
     *            要判断的Map。
     * @return 如果Map为 null 或者 {@link Map#isEmpty()} 返回true时，则返回 true。
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(List<?> list) {
        return (list == null || list.isEmpty());
    }

    public static int size(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        return collection.size();
    }

    public static int size(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return 0;
        }
        return map.size();
    }

    public static int size(List<?> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        return list.size();
    }

    public static String fastRemove(String strSource, String strFrom) {
        if (strSource == null) {
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            // char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuilder buf = new StringBuilder(cSrc.length);
            buf.append(cSrc, 0, i);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }

    public static String fastReplace(String strSource, String strFrom, String strTo) {
        if (strSource == null) {
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuilder buf = new StringBuilder(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }
    
  public static final SimpleDateFormat hm = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    
    //获取当前时间的小时数
    public static String getCurFormatTime(long time){
           Date date = new Date(time);
           String timeStr = hm.format(date);
           return timeStr;
    }
}
