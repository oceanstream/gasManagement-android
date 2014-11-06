package org.whut.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlUtils {

	@SuppressWarnings("unchecked")
	public static List<HashMap<String,String>> getDataFromXML(File file) throws Exception{
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> elist = root.elements();
		for(int i=0;i<elist.size();i++){
			HashMap<String,String> params = new HashMap<String, String>();
			params.put("taskName", "安装任务"+(i+1));
			params.put("isComplete", elist.get(i).elementText("isComplete"));
			params.put("isUpload", elist.get(i).elementText("uploadFlag"));
			params.put("address", elist.get(i).elementText("address"));
			params.put("postDate", elist.get(i).elementText("postDate"));
			params.put("userName", elist.get(i).elementText("username"));
			list.add(params);
		}
		return list;
	}

}
