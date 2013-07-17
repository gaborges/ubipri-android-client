package br.ufrgs.inf.ubipri.client.model;

public class User {
	private int id;
	private String userName;
	private String userPassword;
	private Environment currentEnvironment;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String userName, String userPassword) {
		super();
		this.userName = userName;
		this.userPassword = userPassword;
	}
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
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}
	public void setCurrentEnvironment(Environment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}
}
