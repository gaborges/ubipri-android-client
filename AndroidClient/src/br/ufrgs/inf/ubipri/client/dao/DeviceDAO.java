package br.ufrgs.inf.ubipri.client.dao;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import br.ufrgs.inf.ubipri.client.model.Device;
import br.ufrgs.inf.ubipri.client.model.DeviceType;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.Functionality;
import br.ufrgs.inf.ubipri.util.Config;

public class DeviceDAO {
	
	public ArrayList<Functionality> getDeviceFunctionalities(){
		ArrayList<Functionality> list = new ArrayList<Functionality>();
		for(Functionality f : Config.DEVICE_FUNCTIONALITIES){
			list.add(f);
		}
		return list;
	}
	
	public Device getDevice(){
		Device dev = new Device();
		dev.setCode(Config.DEVICE_CODE);
		dev.setDeviceType(new DeviceType(Config.DEVICE_TYPE));
		dev.setName(Config.DEVICE_NAME);
		return dev;
	}
	
	public void updateDeviceEnvironment(Environment environment){
		Config.CURRENT_DEVICE_ENVIRONMENT =  environment;
	}
}
