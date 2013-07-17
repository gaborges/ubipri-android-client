package br.ufrgs.inf.ubipri.client.dao;

import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.User;
import br.ufrgs.inf.ubipri.util.Config;

public class UserDAO {
	public void insert(User user){
		
	}
	
	public User get(String userName,String userPassword){
		// Por enquanto estático
		if(userName.equals(Config.STATIC_LOGGED_USER_NAME) && userPassword.equals(Config.STATIC_LOGGED_USER_PASSWORD)){
			User user = new User();
			user.setId(Config.STATIC_LOGGED_USER_ID);
			user.setUserName(Config.STATIC_LOGGED_USER_NAME);
			user.setUserPassword(Config.STATIC_LOGGED_USER_PASSWORD);
			return user;
		}
		return null;
	}
	
	public User getLastLoggedUser(){
		User user = new User();
		user.setId(Config.STATIC_LOGGED_USER_ID);
		user.setUserName(Config.STATIC_LOGGED_USER_NAME);
		user.setUserPassword(Config.STATIC_LOGGED_USER_PASSWORD);
		return user;
	}
	
	public void updateUserEnvironment(Environment environment){
		Config.CURRENT_DEVICE_ENVIRONMENT = environment;
	}
}
