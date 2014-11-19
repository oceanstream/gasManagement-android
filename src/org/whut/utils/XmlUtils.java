package org.whut.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.whut.database.entities.TaskInstall;
import org.whut.database.entities.TaskRepair;
import org.whut.database.service.imp.TaskInstallServiceDao;
import org.whut.database.service.imp.TaskRepairServiceDao;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("unchecked")
public class XmlUtils {

	public static List<TaskInstall> getInstallDataFromXML(File file) throws Exception{
		List<TaskInstall> list = new ArrayList<TaskInstall>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> elist = root.elements();
		for(int i=0;i<elist.size();i++){
			TaskInstall ti = new TaskInstall();
			ti.setId(Integer.parseInt(elist.get(i).elementText("id")));
			ti.setAddress(elist.get(i).elementText("address"));
			ti.setBarCode(elist.get(i).elementText("barCode"));
			ti.setIndication(elist.get(i).elementText("indication"));
			ti.setIsComplete(Integer.parseInt(elist.get(i).elementText("isComplete")));
			ti.setPostDate(elist.get(i).elementText("postDate"));
			ti.setUploadFlag(Integer.parseInt(elist.get(i).elementText("uploadFlag")));
			ti.setUserName(elist.get(i).elementText("userName"));
			list.add(ti);
		}
		return list;
	}
	
	public static List<TaskRepair> getRepairDateFromXml(File file) throws Exception{
		// TODO Auto-generated method stub
		List<TaskRepair> list = new ArrayList<TaskRepair>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> elist = root.elements();
		for(int i=0;i<elist.size();i++){
			TaskRepair tr =  new TaskRepair();
			tr.setId(Integer.parseInt(elist.get(i).elementText("id")));
			tr.setAddress(elist.get(i).elementText("address"));
			tr.setIsComplete(Integer.parseInt(elist.get(i).elementText("isComplete")));
			tr.setPostDate(elist.get(i).elementText("postDate"));
			tr.setType(Integer.parseInt(elist.get(i).elementText("type")));
			tr.setUploadFlag(Integer.parseInt(elist.get(i).elementText("uploadFlag")));
			tr.setIsUpdate(Integer.parseInt(elist.get(i).elementText("isUpdate")));
			tr.setUserName(elist.get(i).elementText("userName"));
			list.add(tr);
		}
		return list;
	}
	
	public static void SaveInstallResultToXml(String id,String barCode,String indication,String filePath) throws Exception{
		File f = new File(filePath);
		if(f.exists()){
			SAXReader reader = new SAXReader();
			Document document = reader.read(f);
			Element root = document.getRootElement();
			List<Element> elist = root.elements();
			for(Element e:elist){
				if(e.element("id").getTextTrim().toString().equals(id)){
					e.element("isComplete").setText("1");
					e.element("barCode").setText(barCode);
					e.element("indication").setText(indication);				
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			format.setNewlines(true);
			XMLWriter writer = new XMLWriter(new FileOutputStream(filePath),format);
			writer.write(document);
			writer.close();	
		}else{
			Log.i("msg", "XmlUtils---->"+filePath+"不存在！");
		}
	}
	
	public static void SaveRepairResultToXml(String id,String type,String isUpdate,String description,String filePath) throws Exception {
		// TODO Auto-generated method stub
		File f = new File(filePath);
		if(f.exists()){
			SAXReader reader = new SAXReader();
			Document document = reader.read(f);
			Element root = document.getRootElement();
			List<Element> elist = root.elements();
			for(Element e:elist){
				if(e.element("id").getTextTrim().toString().equals(id)){
					e.element("isComplete").setText("1");
					e.element("type").setText(type);
					e.element("isUpdate").setText(isUpdate);
					e.element("description").setText(description);
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			format.setNewlines(true);
			XMLWriter writer = new XMLWriter(new FileOutputStream(filePath),format);
			writer.write(document);
			writer.close();	
		}else{
			Log.i("msg", "XmlUtils---->"+filePath+"不存在！");
		}
		
	}
	
	
	public static void updateUploadFlag(String id,String filePath) throws Exception{
		File f = new File(filePath);
		if(f.exists()){
			SAXReader reader = new SAXReader();
			Document document = reader.read(f);
			Element root = document.getRootElement();
			List<Element> elist = root.elements();
			for(Element e:elist){
				if(e.element("id").getTextTrim().toString().equals(id)){
					e.element("uploadFlag").setText("1");			
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			format.setNewlines(true);
			XMLWriter writer = new XMLWriter(new FileOutputStream(filePath),format);
			writer.write(document);
			writer.close();	
		}else{
			Log.i("msg", "XmlUtils---->"+filePath+"不存在！");
		}
	}

	public static void SaveInstallToDatabase(List<TaskInstall> list,
			Context context) throws Exception{
		// TODO Auto-generated method stub
		TaskInstallServiceDao  tid = new TaskInstallServiceDao(context);
		tid.addTaskInstall(list);
	}

	public static void SaveRepairToDatabase(List<TaskRepair> list,
			Context context) throws Exception{
		// TODO Auto-generated method stub
		TaskRepairServiceDao trd = new TaskRepairServiceDao(context);
		trd.addTaskRepair(list);
	}





}
