package com.snowleaf.bt2cap.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.snowleaf.bt2cap.model.BtModel;
import com.snowleaf.bt2cap.model.InputType;
import com.snowleaf.bt2cap.packet.BtPacket;
import com.snowleaf.bt2cap.packet.CapPacket;
import com.snowleaf.bt2cap.provider.PacketProvider;

public class Bt2CapEngine {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("usage:\n");
			System.out.println("usage:\t Bt2Cap.jar bcsp/usb/h4 uart.txt");
			return;
		}
		
		String filename = args[1];
		String outname = filename.replace(".txt", ".cap");
		File outFile = new File(outname);
		if(outFile.exists())outFile.delete();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		BtModel model = null;
		switch(args[0]){
		case "bcsp":
			model = new BtModel(filename,InputType.BCSP_TXT);
			break;
		case "usb":
			model = new BtModel(filename,InputType.USB_TXT);
			break;
		case "h4":
			model = new BtModel(filename,InputType.H4_TXT);
			break;
		}
		
		PacketProvider provider = model.getPacketProvider();
		BtPacket packet;
		while ((packet = provider.getPacket()) != null) {
			CapPacket cap = new CapPacket(packet);
			try {
				out.write(cap.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
