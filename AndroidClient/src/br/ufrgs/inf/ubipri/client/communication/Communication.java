package br.ufrgs.inf.ubipri.client.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import br.ufrgs.inf.ubipri.client.model.Action;
import br.ufrgs.inf.ubipri.client.model.Device;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.User;
import br.ufrgs.inf.ubipri.util.Config;

public class Communication {
	/*
	 * This method sends the new location to the server and waits for a return message containing the action. 
	 * Then, process the message and generates a list of actions
	 */
	public ArrayList<Action> sendNewLocalization(Environment environment,
			User user, Device device) throws ClientProtocolException,
			IOException, InterruptedException, ExecutionException, JSONException,RuntimeException {
		
		TaskSendNewLocation t = new TaskSendNewLocation();
		t.execute(user.getUserName(),user.getUserPassword(),device.getCode(),environment.getId());
		String strJson = t.get();
		
		strJson = cleanStrJsonMessage(strJson);
		
		if(Config.DEBUG_COMMUNICATION)Log.d("DEBUG","Response strJSON: "+strJson);

		JSONObject status = null;
		ArrayList<Action> actions = new ArrayList<Action>();
		status = (JSONObject) new JSONTokener(strJson).nextValue();
		
		
		if(status != null){
			if(status.has("status")){
				if(Config.DEBUG_COMMUNICATION) Log.d("DEBUG","Status: "+status.getString("status"));
				if(status.has("actions")){
					
					actions = JSONArrayToArrayList(status.getJSONArray("actions"));		
					if(Config.DEBUG_COMMUNICATION) Log.d("DEBUG","Num. Actions: "+actions.size());
				}
			}
		}
		return actions;
	}
	
	/*
	 * This method process the JSONArray and generates a list of actions
	 */
	private ArrayList<Action> JSONArrayToArrayList(JSONArray jsonActions) throws JSONException{
		if(Config.DEBUG_COMMUNICATION) Log.d("DEBUG","Num. JSON actions: "+jsonActions.length());
		ArrayList<Action> actions = new ArrayList<Action>();
		JSONObject temp = null;
		for(int i = 0; i < jsonActions.length();i++){
			temp = jsonActions.getJSONObject(i);
			actions.add(new Action(temp.getString("action"), temp.getInt("fid")));
			if(Config.DEBUG_COMMUNICATION) Log.d("DEBUG","Ação "+i+" - "+actions.get(i).getAction()+" fid: "+actions.get(i).getFunctionalityId());
		}
		return actions;
	}

	/*
	 * This method is used to bring environments remotely.
	 * OBS.: This function not is implemented yet!
	 */
	public Environment getEnvironments() {

		return null;
	}

	/*
	 * This method checks if the user is registered on the server. 
	 * If yes, creates an object User with the login data, else it returns null.
	 */
	public User getUser(String userName, String password) throws InterruptedException, ExecutionException, JSONException {
		if(remoteLogin(userName, password, Config.DEVICE_CODE)){
			return new User(userName, password);
		}
		return null;
	}

	/*
	 * This method checks if the user is registered on the server. 
	 * If yes, return true, else it returns false.
	 */
	public boolean remoteLogin(String userName, String password, String deviceCode) throws InterruptedException, ExecutionException, JSONException{
		TaskRemoteLogin task = new TaskRemoteLogin();
		task.execute(userName,password,deviceCode);
		String strJson = task.get();
		
		strJson = cleanStrJsonMessage(strJson);
		
		if(Config.DEBUG_COMMUNICATION)Log.d("DEBUG","Response strJSON: "+strJson);
		
		JSONObject status = null;
		status = (JSONObject) new JSONTokener(strJson).nextValue();
		
		if(status != null){
			if(status.has("status")){
				if(Config.DEBUG_COMMUNICATION)Log.d("DEBUG","Status: "+status.getString("status"));
				if(status.getString("status").equals("OK")) return true;
			}
		}
		return false;
	}

	/*
	 * This method is used to bring the device information remotely.
	 * OBS.: This function not is implemented yet!
	 */
	public Device deviceInformations(String code) {
		return null;
	}
	
	/*
	 * This method send to server the new device code for be used in the Google Cloud Message
	 * Returns true if successful, else it returns false.
	 */
	public boolean sendNewDeviceCommunicationCode(String userName, String userPassword, 
			String deviceCode, String communicationCode) throws JSONException, InterruptedException, ExecutionException{
		TaskSendNewCommunicationCode t = new TaskSendNewCommunicationCode();

		String strJson = null;
		t.execute(userName, userPassword, deviceCode, communicationCode);
	
		strJson = t.get();

		if(Config.DEBUG_COMMUNICATION) Log.d("DEBUG","Response Change Communication Code: "+strJson);
		
		strJson = cleanStrJsonMessage(strJson);
		
		JSONObject status = null;
		status = (JSONObject) new JSONTokener(strJson).nextValue();
		
		if(status != null){
			if(status.has("status")){
				if(Config.DEBUG_COMMUNICATION)Log.d("DEBUG","Status: "+status.getString("status"));
				if(status.getString("status").equals("OK")) return true;
			}
		}
		return false;
	}
	
	
	/*
	 * Clean the JSON message. Unusually brackets appear at the beginning 
	 *  and end of the message, which impairs reading the message. This method removes them
	 */
	private String cleanStrJsonMessage(String strJson) {
		// Por algum motivo infernal aparecem colchetes no início e no fim da
		// mensagem
		// Desta forma se elas aparecem serão removidas
		if (strJson.charAt(0) == '[' && strJson.endsWith("]")) {
			char a[] = strJson.toCharArray();
			strJson = "";
			for (int i = 1; i < (a.length - 1); i++) {
				strJson += a[i];
			}
		}
		return strJson;
	}

}