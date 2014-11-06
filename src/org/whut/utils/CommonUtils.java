package org.whut.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class CommonUtils {

	private static long lastClickTime;

	public static boolean isFastDoubleClick(){
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if(0<timeD&&timeD<800){
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
}
