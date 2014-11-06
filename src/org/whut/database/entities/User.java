package org.whut.database.entities;

public class User {

	//主键
	private int id;
	//用户名
	private String userName;
	//姓名
	private String name;
	//工号
	private int workId;
	//性别
	private String sex;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWorkId() {
		return workId;
	}
	public void setWorkId(int workId) {
		this.workId = workId;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
	
}
