package br.ufrgs.inf.ubipri.client.model;

public class DeviceType {
	private int id;
	private String name;
	
	public DeviceType() {
		// TODO Auto-generated constructor stub
	}
	
	public DeviceType(String name){
		super();
		this.name = name;
	}
	
	public DeviceType(int id, String name) {
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
