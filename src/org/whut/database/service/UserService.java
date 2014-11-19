package org.whut.database.service;


import org.whut.database.entities.User;

public interface UserService {

	public void addUser(User user);
	public boolean validateUser(User user);
}
