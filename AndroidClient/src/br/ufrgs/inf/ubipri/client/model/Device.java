package br.ufrgs.inf.ubipri.client.model;

public class Device {
	private int id;
	private String code;
	private String name;
	private DeviceType deviceType;
	private Environment currentEnvironment;
	
	public Device() {
		// TODO Auto-generated constructor stub
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}
	public void setCurrentEnvironment(Environment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}
	
	
}
