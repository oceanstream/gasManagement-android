package org.whut.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.whut.database.service.imp.TaskInstallServiceDao;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CommonUtils {

	private static long lastClickTime;

	public static boolean isFastDoubleClickForMain(){
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if(0<timeD&&timeD<800){
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isFastDoubleClickForOthers(){
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if(0<timeD&&timeD<500){
			return true;
		}
		lastClickTime = time;
		return false;
	}


	//判断SD卡是否插入
	public static boolean isMounted(){
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}


	public static boolean SaveConfigFiles(InputStream inStream,
			String fileName,String filePath)throws Exception{
		if(isMounted()){
			if(inStream==null){
				return false;
			}
			OutputStream os;
			File file = new File(filePath);
			//如果文件夹不存在，则创建
			if(!file.exists()){
				file.mkdirs();
			}

			file = new File(filePath+"/"+fileName);

			//如果文件存在，则删除
			if(file.exists()){
				file.delete();
			}

			byte[] temp = new byte[1024];
			int len;
			os = new FileOutputStream(file);
			while((len = inStream.read(temp)) != -1){
				os.write(temp,0,len);
			}
			os.close();
			inStream.close();
			//文件保存成功
			Log.i("msg", filePath+"/"+fileName+"下载成功");
			return true;
		}
		//内存卡没有插入，下载失败
		return false;

	}


	public static List<HashMap<String, String>> getTaskInstall(Context context) {
		// TODO Auto-generated method stub
		TaskInstallServiceDao tid = new TaskInstallServiceDao(context);
		List<HashMap<String,String>> list = tid.getTaskInstallations();
		return list;
	}

	//将条码在界面上显示
	public static String FormatBarCode(String barCode){
		if(barCode.length()==13){
			Log.i("msg", "barcodeLength==13");
			char[] temp = barCode.toCharArray();
			return temp[0]+" "+temp[1]+temp[2]+temp[3]+temp[4]+temp[5]+temp[6]+" "+temp[7]+temp[8]+temp[9]+temp[10]+temp[11]+temp[12];
		}
		Log.i("msg", "barcodeLength!=13");
		return barCode;
	}

	//判断输入值是否为数字
	public static boolean isNumeric(String str){
		for (int i = str.length();--i>=0;){   
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}

	//文件复制
	public static void copyFile(String oldPath,String newPath,String fileName){
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(new File(newPath, fileName)); 
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					bytesum += byteread; //字节数 文件大小 
					System.out.println(bytesum); 
					fs.write(buffer, 0, byteread); 
				} 
				inStream.close(); 
				fs.close();
			} 
		} 
		catch (Exception e) {
			e.printStackTrace(); 

		} 

	} 


}
