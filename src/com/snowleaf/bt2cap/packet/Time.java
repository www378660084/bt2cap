package com.snowleaf.bt2cap.packet;

public class Time {
	public long sec;
	public long usec;
	
	public Time(long sec,long usec){
		this.sec = sec;
		this.usec = usec;
	}
	
	public Time(){
		sec = 0;
		usec = 0;
	}
}
