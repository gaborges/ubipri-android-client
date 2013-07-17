package br.ufrgs.inf.ubipri.client.model;

public class Functionality {
	private int id;
	private String name;
	
	public Functionality() {
		// TODO Auto-generated constructor stub
	}
		
	public Functionality(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
