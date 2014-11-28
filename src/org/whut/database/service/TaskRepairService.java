package org.whut.database.service;

import java.util.HashMap;
import java.util.List;

import org.whut.database.entities.TaskRepair;

public interface TaskRepairService {

	public void addTaskRepair(List<TaskRepair> list);
	public boolean isTaskRepairAdded(TaskRepair tr);
	public List<HashMap<String,String>> getTaskRepairs(String userName);	
	public void updateRepairResult(int id,int type,int isUpdate,String userName,String description,String filePath,String finishTime);
	public void updateUploadFlag(int id);
	public HashMap<String,String> getIntentParams(int id);
}
