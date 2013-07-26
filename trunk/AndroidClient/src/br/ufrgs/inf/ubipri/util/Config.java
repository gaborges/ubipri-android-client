package br.ufrgs.inf.ubipri.util;

import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.Functionality;

public class Config {
	
	public static final String DEVICE_CODE = "1234554321";
	public static final String DEVICE_NAME = "VitualMachineAndroid_2.3";
	public static final String DEVICE_TYPE = "VMAndroid";
	public static final Functionality DEVICE_FUNCTIONALITIES[] = {
		new Functionality(1, "Bluetooh"), // Adicionar o estado atual do dispositico, String actual Action
		new Functionality(2, "Silent Mode"),
		new Functionality(3, "Vibrate Alert"),
	//	new Functionality(4, "Airplane Mode"),
		new Functionality(5, "Wi-Fi"),
	//	new Functionality(6, "Mobile Network Data Access"),
	//	new Functionality(7, "System Volume"),
	//	new Functionality(8, "Media Volume"),
		new Functionality(9, "Ringer Volume"),
	//	new Functionality(10, "Screen Timeout"),
	//	new Functionality(11, "Screen Brightness"),
	//	new Functionality(12, "SMS"),
	//	new Functionality(13, "Launch App"),
	//	new Functionality(14, "Camera Access"),
		new Functionality(15, "GPS")
	};
	
	public static String STATIC_LOGGED_USER_NAME = "borges";
	public static String STATIC_LOGGED_USER_FULL_NAME = "Guilherme A. Borges";
	public static String STATIC_LOGGED_USER_PASSWORD = "12345";
	public static int STATIC_LOGGED_USER_ID = 1;
	
	public static Environment CURRENT_USER_ENVIRONMENT = null;
	public static Environment CURRENT_DEVICE_ENVIRONMENT = null; 
	public static String LOGGED_USER_NAME = "borges";
	public static String SERVER_HOST = "http://192.168.1.120:8080/UbipriServer/"; // Casa
	//public static String SERVER_HOST = "http://localhost:8084/UbipriServer/"; // Lab 205
	
	public static boolean DEBUG_COMMUNICATION = true;
}
