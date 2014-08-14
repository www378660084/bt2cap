package com.snowleaf.bt2cap.logger;

public class Log {
	public static void debug(String msg){
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();  
		System.out.println(stack[2].getClassName()+"."+stack[2].getMethodName() + ":" + stack[2].getLineNumber());
		System.out.println(msg);
		System.out.println();
	}
	
	public static void error(String msg){
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();  
		System.out.println(stack[2].getClassName()+"."+stack[2].getMethodName()+ ":" + stack[2].getLineNumber());
		System.out.println(msg);
		System.out.println();
	}
	
	public static void error(String msg,Exception e){
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();  
		System.out.println(stack[2].getClassName()+"."+stack[2].getMethodName()+ ":" + stack[2].getLineNumber());
		System.out.println(msg);
		e.printStackTrace();
		System.out.println();
	}
	
	public static void error(String msg,byte[] data){
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();  
		System.out.println(stack[2].getClassName()+"."+stack[2].getMethodName()+ ":" + stack[2].getLineNumber());
		System.out.println(msg);
		for(byte b : data){
			System.out.print(String.format("%02X ", b & 0x0FF));
		}
		System.out.println();
		System.out.println();
	}
}
