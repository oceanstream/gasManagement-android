package org.whut.database.entities;

public class TaskRepair {
	
	private int id;
	
	private String address;
	
	/***
	 * 维修原因
	 * -1 未知	
	 * 	0.电池耗尽
	 * 	1.开关未开启
	 * 	2.其他
	 * 
	 */
	
	private int type;
	
	//原因为其他时的描述
	private String description;
	
	//任务下发时间
	private String postDate;
	
	private String oldBarCode;
	
	private String newBarCode;
	
	private String oldIndication;
	
	private String newIndication;
	
	//是否换表
	private int isUpdate;
	
	//任务是否完成
	private int isComplete;
	
	//任务是否上传
	private int uploadFlag;
	
	//文件保存路径
	private String filePath;
	
	//操作人
	private String userName;
	
	//完成时间
	private String completeDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getOldBarCode() {
		return oldBarCode;
	}

	public void setOldBarCode(String oldBarCode) {
		this.oldBarCode = oldBarCode;
	}

	public String getNewBarCode() {
		return newBarCode;
	}

	public void setNewBarCode(String newBarCode) {
		this.newBarCode = newBarCode;
	}

	public String getOldIndication() {
		return oldIndication;
	}

	public void setOldIndication(String oldIndication) {
		this.oldIndication = oldIndication;
	}

	public String getNewIndication() {
		return newIndication;
	}

	public void setNewIndication(String newIndication) {
		this.newIndication = newIndication;
	}

	public int getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(int isUpdate) {
		this.isUpdate = isUpdate;
	}

	public int getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(int isComplete) {
		this.isComplete = isComplete;
	}

	public int getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(int uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	
	
	
	

}
