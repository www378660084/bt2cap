package com.snowleaf.bt2cap.provider;

import java.io.InputStream;

import com.snowleaf.bt2cap.packet.BtPacket;

public abstract class PacketProvider {
	protected InputStream mInputStream;
	
	public PacketProvider(InputStream inputStream){
		this.mInputStream = inputStream;
	}
	
	public abstract BtPacket getPacket();
}
