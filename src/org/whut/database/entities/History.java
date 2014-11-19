package org.whut.database.entities;

public class History {
	
	//主键
	private int id;
	
	/***
	 * 历史类型：
	 * 	 0：安装
	 * 	 1：维修
	 * 
	 */
	private int type;
	
	//操作人
	private String userName;
	
	//完成时间
	private String finishTime;
	
	private int taskInstallId;
	
	private int taskRepairId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public int getTaskInstallId() {
		return taskInstallId;
	}

	public void setTaskInstallId(int taskInstallId) {
		this.taskInstallId = taskInstallId;
	}

	public int getTaskRepairId() {
		return taskRepairId;
	}

	public void setTaskRepairId(int taskRepairId) {
		this.taskRepairId = taskRepairId;
	}
	
	
	
}
