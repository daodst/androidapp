


package com.wallet.ctc.util;
import android.util.Log;

import common.app.BuildConfig;


public class LogUtil {
	public final static boolean logable =BuildConfig.DEBUG;
	public final static String tag = "zzz";

	public static void d(String msg)
	{
		if (logable){
			
			
			
			Log.d(tag, getMsg(msg));
		}
	}
	
	public static void e(String msg)
	{
		if (logable)
			Log.e(tag,  getMsg(msg));
	}
	
	public static void i(String msg)
	{
		if (logable)
			Log.i(tag, getMsg(msg));
	}
	
	public static void v(String msg)
	{
		if (logable)
			Log.v(tag, getMsg(msg));
	}
	
	public static void w(String msg)
	{
		if (logable)
			Log.w(tag, getMsg(msg));
	}
	public static String getMsg(String msg){
		Exception e = new Exception();
		StackTraceElement[] trace = e.getStackTrace();
		StringBuilder builder = new StringBuilder();
		builder.append("║ ")
				.append(trace[2].getClassName()).append(".")
				.append(trace[2].getMethodName())
				.append(" ").append(" (")
				.append(trace[2].getFileName())
				.append(":")
				.append(trace[2].getLineNumber())
				.append(")");
		return builder.toString()+"\n║ "+msg;
	}
}
