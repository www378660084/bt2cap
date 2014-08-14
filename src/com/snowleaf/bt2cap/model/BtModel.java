package com.snowleaf.bt2cap.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.snowleaf.bt2cap.logger.Log;
import com.snowleaf.bt2cap.provider.BcspTxtProvider;
import com.snowleaf.bt2cap.provider.H4TxtProvider;
import com.snowleaf.bt2cap.provider.PacketProvider;
import com.snowleaf.bt2cap.provider.UsbTxtProvider;

public class BtModel {
	private String mInputFilename;
	private String mOutputFilename;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private InputType mInputType;
	
	public BtModel(String inputfile,InputType type){
		this.mInputFilename = inputfile;
		this.mOutputFilename = null;
		this.mInputStream = null;
		this.mOutputStream = null;
		this.mInputType = type;
	}
	
	public PacketProvider getPacketProvider(){
		switch (mInputType) {
		case BCSP_TXT:
			return new BcspTxtProvider(getInputStream());
		case H4_TXT:
			return new H4TxtProvider(getInputStream());
		case USB_TXT:
			return new UsbTxtProvider(getInputStream());
		default:
			break;
		}
		
		return null;
	}
	
	public void setInputFilename(String filename){
		if(this.mInputStream != null)this.mInputStream = null;
		this.mInputFilename = filename;
	}
	
	public void setOutputFilename(String filename){
		if(this.mOutputStream != null)this.mOutputStream = null;
		this.mOutputFilename = filename;
	}
	
	
	
	private InputStream getInputStream(){
		if(this.mInputStream != null)return this.mInputStream;
		
		File file = new File(mInputFilename);
		if(!file.exists())return null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.error("open file " + mInputFilename + "error",e);
		}
		return in;
	}
	
	private OutputStream getOutputStream(){
		if(this.mOutputStream != null)return this.mOutputStream;
		
		File file = new File(mOutputFilename);
		if(file.exists()){
			if(!file.delete()){
				Log.error("delete " + mOutputFilename + " error!");
				return null;
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Log.error("getOutputStream:open file error",e);
		}
		
		return out;
	}
	
}
