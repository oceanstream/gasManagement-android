package org.whut.database.service;

import java.util.HashMap;
import java.util.List;

import org.whut.database.entities.TaskInstall;

public interface TaskInstallService {

	public void addTaskInstall(List<TaskInstall> list);
	public boolean isTaskInstallAdded(TaskInstall ti);
	public List<HashMap<String,String>> getTaskInstallations();
	public void updateTaskInstallResult(int id,String barCode,String indication);
}
