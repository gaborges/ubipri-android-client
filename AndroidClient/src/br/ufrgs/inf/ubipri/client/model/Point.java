package br.ufrgs.inf.ubipri.client.model;

public class Point {
	private int id;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Point nextPoint;
	private Environment environment;
	private Double operatingRange;
	private boolean isfinal;

	public Point() {
		this.isfinal = false;
		this.latitude = 0.0;
		this.longitude = 0.0;
		this.altitude = 0.0;
		this.operatingRange = 0.0;
		this.environment = null;
		this.nextPoint = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Point getNextPoint() {
		return nextPoint;
	}

	public void setNextPoint(Point nextPoint) {
		this.nextPoint = nextPoint;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Double getOperatingRange() {
		return operatingRange;
	}

	public void setOperatingRange(Double scale) {
		this.operatingRange = scale;
	}

	public boolean isfinal() {
		return isfinal;
	}

	public void setIsfinal(boolean isfinal) {
		this.isfinal = isfinal;
	}

}