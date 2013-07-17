package br.ufrgs.inf.ubipri.client.model;

public class Action {
	private String action;
	private int functionalityId;
	
	public Action() {
		// TODO Auto-generated constructor stub
	}
	
	public Action(String action, int functionalityId) {
		super();
		this.action = action;
		this.functionalityId = functionalityId;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getFunctionalityId() {
		return functionalityId;
	}
	public void setFunctionalityId(int functionalityId) {
		this.functionalityId = functionalityId;
	}
	
	
}
