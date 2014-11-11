package org.whut.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.whut.database.entities.TaskInstall;
import org.whut.database.service.imp.TaskInstallServiceDao;

import android.content.Context;

@SuppressWarnings("unchecked")
public class XmlUtils {

	public static List<TaskInstall> getDataFromXML(File file) throws Exception{
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
			ti.setCustomerId(Integer.parseInt(elist.get(i).elementText("customerId")));
			ti.setIndication(elist.get(i).elementText("indication"));
			ti.setIsComplete(Integer.parseInt(elist.get(i).elementText("isComplete")));
			ti.setPostDate(elist.get(i).elementText("postDate"));
			ti.setUploadFlag(Integer.parseInt(elist.get(i).elementText("uploadFlag")));
			ti.setUserName(elist.get(i).elementText("userName"));
			list.add(ti);
		}
		return list;
	}

	public static void SaveToDatabase(List<TaskInstall> list,
			Context context) throws Exception{
		// TODO Auto-generated method stub
		TaskInstallServiceDao  tid = new TaskInstallServiceDao(context);
		tid.addTaskInstall(list);
	}

}
