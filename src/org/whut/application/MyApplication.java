package org.whut.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Service;


//MyApplication类实现对所有Activity的统一管理
public class MyApplication extends Application{

	private List<Activity> activities = null;
	private List<Service> services = null;
	private static MyApplication instance;
	
	private MyApplication(){
		activities = new LinkedList<Activity>();
		services = new LinkedList<Service>();
	}
	
	public static MyApplication getInstance(){
		if(instance==null){
			instance = new MyApplication();
		}
		return instance;
	}
	
	public void addActivity(Activity activity){
		if(activities!=null&&activities.size()>0){
			if(!activities.contains(activity)){
				activities.add(activity);
			}
		}else{
			activities.add(activity);
		}
	}
	
	public void addService(Service service){
		if(services!=null&&services.size()>0){
			if(!services.contains(service)){
				services.add(service);
			}
		}else{
			services.add(service);
		}
	}
	
	public void exit(){
		if(activities!=null&&activities.size()>0){
			for(Activity activity:activities){
				activity.finish();
			}	
		}
		
		if(services!=null&&services.size()>0){
			for(Service service : services){
				service.stopSelf();
			}
		}
		System.exit(0);
	}
}
