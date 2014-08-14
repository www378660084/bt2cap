package com.snowleaf.bt2cap.packet;

import java.util.Arrays;

import com.snowleaf.bt2cap.logger.Log;

public class BtPacket {
	private static final int  UNRELIABLE = 0;
	private static final int  RELIABLE = 1;
	private static final int  BCCMD = 2;
	private static final int  HQ = 3;
	private static final int  DM = 4;
	private static final int  HCI = 5;
	private static final int  ACL = 6;
	private static final int  SCO = 7;
	private static final int  L2CAP = 8;
	private static final int  RFCOMM = 9;
	private static final int  SDP = 10;
	private static final int  DFU = 11;
	private static final int  VM = 12;
	
	private byte[] mBytes;
	private boolean mIsSender;
	private Time mTime;
	private boolean error;
	
	public BtPacket(){
		this.mBytes = null;
		this.mIsSender = false;
		this.mTime = null;
		this.error = false;
	}
	
	public boolean hasError(){
		return error;
	}
	
	public static BtPacket fromBytes(byte[] bytes){
		BtPacket packet = new BtPacket();
		packet.mBytes = bytes;
		packet.mIsSender = (bytes[0] != 0x04);
		packet.mTime = null;
		return packet;
	}
	
	public String toString(){
		return "BtPacket: "+"time="+this.mTime + ",isSender="+mIsSender+",bytes.length="+mBytes.length;
	}
	
	public static BtPacket fromBytes(boolean send,byte[] bytes){
		BtPacket packet = new BtPacket();
		packet.mBytes = bytes;
		packet.mIsSender = send;
		packet.mTime = null;
		return packet;
	}
	
	/**
	 * get bluetooth packet from byte array
	 * @param time time in second
	 * @param send packet send from us
	 * @param bytes packet bytes
	 * @return the bluetooth packet
	 */
	public static BtPacket fromBytes(Time time,boolean send,byte[] bytes){
		BtPacket packet = new BtPacket();
		packet.mBytes = bytes;
		packet.mIsSender = send;
		packet.mTime = time;
		return packet;
	}
	
	private static byte[] unslip(byte[] bytes){
		byte[] result = new byte[bytes.length];
		int len = 0;
		boolean slip = false;
		for(byte b:bytes){
			if(slip){
				if((b&0xFF) == 0xDC){
					result[len++] = (byte)0xC0;
				}else if((b&0xFF) == 0xDD){
					result[len++] = (byte)0xDB;
				}else{
					Log.error("unslip error",bytes);
					return null;
				}
				slip = false;
			}else{
				if((b&0xFF) == 0xDB){
					slip = true;
				}else{
					result[len++] = b; 
				}
			}
		}

		return Arrays.copyOfRange(result, 0, len);
	}
	
	public static BtPacket fromBcsp(Time time,boolean send,byte[] bytes){
		BtPacket packet = new BtPacket();
		packet.mIsSender = send;
		packet.mTime = time;
		
		byte[] data = unslip(bytes);
		if(data == null){
			packet.error = true;
			return packet;
		}
		
		//not care about short packets
		if(data.length <= 4){
			packet.error = true;
			return packet;
		}
		
		if (((data[0] + data[1] + data[2] + data[3]) & 0xff) != 0xff)
		{
			Log.error("header error", bytes);
			packet.error = true;
			return packet;
		}

		boolean real = (data[0] & 0x80) != 0;
		
		if(!real){
			packet.error = true;
			return packet;
		}
		
		
		boolean hasCrc = (data[0] & 0x40) != 0;
		//int ack = ((data[0]&0xff) >> 3)& 0x07;
		//int seq = data[0] & 0x07;
		int channel = data[1] & 0x0f;
		int len = (((data[1]&0xff) >> 4) & 0x0f) | ((data[2]&0xff) << 4);
		
		if (len + 4 + (hasCrc ? 2 : 0) != data.length){
			len = data.length - (4 + (hasCrc ? 2 : 0));          //ignore length error
			Log.error("len error",bytes);
		}
		
		if(!Crc.calcCrc(data)){
			Log.error("crc error",bytes);
			packet.error = true;
			return packet;
		}
		
		byte[] pack = new byte[len+1];
		System.arraycopy(data,4, pack, 1, len);
		
		if(channel == HCI && send){
			pack[0] = 0x01;
		}else if(channel == HCI && (!send)){
			pack[0] = 0x04;
		}else if(channel == ACL){
			pack[0] = 0x02;
		}else if(channel == SCO){
			pack[0] = 0x03;
		}else{
			pack[0] = 0x00;
			packet.error = true;
			Log.error("unhandled channel " + channel, bytes);
		}
		
		packet.mBytes = pack;
		return packet;
	}
	
	/**
	 * packet time
	 * @return time in seconds
	 */
	public Time getTime(){
		return mTime;
	}
	
	public boolean isSender(){
		return mIsSender;
	}
	
	public byte[] getBytes(){
		return mBytes;
	}
}
