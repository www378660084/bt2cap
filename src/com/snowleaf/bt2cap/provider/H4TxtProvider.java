package com.snowleaf.bt2cap.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.snowleaf.bt2cap.logger.Log;
import com.snowleaf.bt2cap.packet.BtPacket;
import com.snowleaf.bt2cap.packet.Time;

public class H4TxtProvider extends PacketProvider{
	private static final int NONE = 0;
	private static final int SEND = 1;
	private static final int RECV = 2;
	
	private byte mRxBytes[];
	private byte mTxBytes[];
	private BufferedReader mReader;
	
	private int mCurrentDirection;
	private Time mCurrentTime = null;
	
	private static final Pattern RX_PATTERN = Pattern.compile("^\\s*(?:\\[([^\\[]+)\\])?\\s*rx:((?:\\s+[0-9a-z]{1,2})+)\\s*$",Pattern.CASE_INSENSITIVE);
	private static final Pattern TX_PATTERN = Pattern.compile("^\\s*(?:\\[([^\\[]+)\\])?\\s*tx:((?:\\s+[0-9a-z]{1,2})+)\\s*$",Pattern.CASE_INSENSITIVE);

	public H4TxtProvider(InputStream inputStream) {
		super(inputStream);
		mRxBytes = new byte[0];
		mTxBytes = new byte[0];
		mCurrentDirection = SEND;
		mReader = new BufferedReader(new InputStreamReader(mInputStream));
	}
	
	private BtPacket handleBytes(){
		byte[] bytes;
		
		if(mCurrentDirection == RECV)bytes = mRxBytes;
		else bytes = mTxBytes;
		
		if(bytes == null)return null;
		if(bytes.length == 0)return null;
		
		int len;
		BtPacket packet = null;;
		
		if(bytes[0] == 0x01){
			if(bytes.length < 4)return null;
			len = bytes[3] & 0x0FF;
			if(bytes.length < 4 + len)return null;
			packet = BtPacket.fromBytes(mCurrentTime, mCurrentDirection == SEND, Arrays.copyOf(bytes, 4 + len));
			if(bytes.length > 4 + len)bytes = Arrays.copyOfRange(bytes, 4+len, bytes.length);
			else bytes = new byte[0];
		}else if(bytes[0] == 0x02){
			if(bytes.length < 5)return null;
			len = (((int)bytes[4] << 8) & 0x0FF00) | ((int)bytes[3] & 0x0FF);
			
			if(bytes.length < 5 + len)return null;

			packet = BtPacket.fromBytes(mCurrentTime, mCurrentDirection == SEND, Arrays.copyOf(bytes, 5 + len));
			if(bytes.length > 5 + len)bytes = Arrays.copyOfRange(bytes, 5+len, bytes.length);
			else bytes = new byte[0];
		}else if (bytes[0] == 0x03) {
			if(bytes.length < 4)return null;
			len = bytes[3] & 0x0FF;
			if(bytes.length < 4 + len)return null;
			packet = BtPacket.fromBytes(mCurrentTime, mCurrentDirection == SEND, Arrays.copyOf(bytes, 4 + len));
			if(bytes.length > 4 + len)bytes = Arrays.copyOfRange(bytes, 4+len, bytes.length);
			else bytes = new byte[0];
		}else if(bytes[0] == 0x04){
			if(bytes.length < 3)return null;
			len = bytes[2] & 0x0FF;
			if(bytes.length < 3 + len)return null;
			packet = BtPacket.fromBytes(mCurrentTime, mCurrentDirection == SEND, Arrays.copyOf(bytes, 3 + len));
			if(bytes.length > 3 + len)bytes = Arrays.copyOfRange(bytes, 3 + len, bytes.length);
			else bytes = new byte[0];
		}else{
			Log.error("handle h4 error unkown,type: " + bytes[0] + ",direction:"+mCurrentDirection);
			bytes = new byte[0];
		}
		
		if(mCurrentDirection == RECV)mRxBytes = bytes;
		else mTxBytes = bytes;
		
		return packet;
	}
	
	
	@Override
	public BtPacket getPacket() {
		BtPacket packet = null;
		while (packet == null) {
			packet = handleBytes();
			
			if(packet == null){
				String line = getLine();
				if(line == null)break;
				mCurrentDirection = parseLine(line);
			}
		}
		
		return packet;
	}
	
	private int parseLine(String line){
		int direction = NONE;
		
		Matcher matcher = RX_PATTERN.matcher(line);
		if(matcher.matches()) direction = RECV;
		
		if(direction == NONE){
			matcher = TX_PATTERN.matcher(line);
			if(matcher.matches())direction = SEND;
		}
		
		if(direction == NONE){
			Log.debug("unmatched line: " + line);
			return mCurrentDirection;
		}
		
		String tm = matcher.group(1);
		String data = matcher.group(2);

		if(tm != null){
			mCurrentTime = new Time();
			if(tm.indexOf(':') != -1)tm = tm.substring(tm.lastIndexOf(':') + 1);
			String[] arr = tm.split("\\.");
			if(arr.length > 1){
				mCurrentTime.sec = Long.parseLong(arr[0].trim());
				mCurrentTime.usec = Long.parseLong(arr[1].trim());
			}else{
				mCurrentTime.sec = Long.parseLong(arr[0].trim());
				mCurrentTime.usec = 0;
			}
		}
		
		if(data != null){
			String[] arr = data.split("\\s+");
			int count = 0;
			byte[] buffer = new byte[arr.length];
			for(String str:arr){
				str = str.trim();
				if(str.length() == 0)continue;
				buffer[count++] = (byte) Integer.parseInt(str,16);
			}
			if(direction == RECV){
				byte[] buf = new byte[mRxBytes.length + count];
				if(mRxBytes.length > 0)System.arraycopy(mRxBytes, 0, buf, 0, mRxBytes.length);
				if(count > 0)System.arraycopy(buffer, 0, buf, mRxBytes.length,count);
				mRxBytes = buf;
			}else{
				byte[] buf = new byte[mTxBytes.length + count];
				if(mTxBytes.length > 0)System.arraycopy(mTxBytes, 0, buf, 0, mTxBytes.length);
				if(count > 0)System.arraycopy(buffer, 0, buf, mTxBytes.length,count);
				mTxBytes = buf;
			}
		}
		
		return direction;
	}
	
	private String getLine(){
		try {
			return mReader.readLine();
		} catch (IOException e) {
			Log.error("read line error",e);
		}
		return null;
	}
}
