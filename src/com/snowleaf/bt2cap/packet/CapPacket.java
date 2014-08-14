package com.snowleaf.bt2cap.packet;


/*
struct hcidump_hdr {
	uint16_t	len;
	uint8_t	in;
	uint8_t	pad;
	uint32_t	ts_sec;
	uint32_t	ts_usec;
} __attribute__ ((packed));
 */

public class CapPacket {
	private int len;
	private boolean in;
	private Time time;
	private byte[] data;
	
	public CapPacket(BtPacket btPacket){
		byte[] bytes = btPacket.getBytes();
		
		this.data = new byte[12 + bytes.length];
		this.len = bytes.length;
		this.in = !btPacket.isSender();
		this.time = btPacket.getTime();
		
		this.data[0] = (byte)(len & 0xFF);
		this.data[1] = (byte)((len >> 8) & 0xFF);
		this.data[2] = (byte)(this.in?1:0);
		this.data[3] = (byte)0x00;
		
		if(this.time == null){
			this.data[4] = 0x00;
			this.data[5] = 0x00;
			this.data[6] = 0x00;
			this.data[7] = 0x00;
			
			this.data[8] = 0x00;
			this.data[9] = 0x00;
			this.data[10] = 0x00;
			this.data[11] = 0x00;
		}else{
			this.data[4] = (byte)((time.sec >> 0) & 0xFF);
			this.data[5] = (byte)((time.sec >> 8) & 0xFF);
			this.data[6] = (byte)((time.sec >> 16) & 0xFF);
			this.data[7] = (byte)((time.sec >> 24) & 0xFF);
			
			this.data[8] =  (byte)((time.usec >> 0) & 0xFF);
			this.data[9] =  (byte)((time.usec >> 8) & 0xFF);
			this.data[10] = (byte)((time.usec >> 16) & 0xFF);
			this.data[11] = (byte)((time.usec >> 24) & 0xFF);
		}
		
		System.arraycopy(bytes, 0, this.data, 12, bytes.length);
	}
	
	public byte[] getBytes(){
		return data;
	}
}
