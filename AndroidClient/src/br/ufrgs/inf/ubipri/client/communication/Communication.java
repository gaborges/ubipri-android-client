package br.ufrgs.inf.ubipri.client.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;
import br.ufrgs.inf.ubipri.client.model.Action;
import br.ufrgs.inf.ubipri.client.model.Device;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.User;

public class Communication {
	public ArrayList<Action> sendNewLocalization(Environment environment,
			User user, Device device) throws ClientProtocolException,
			IOException, InterruptedException, ExecutionException, JSONException {
		TaskSendNewLocation t = new TaskSendNewLocation();
		Object params[] = new Object[4];
		params[0] = user.getUserName();
		params[1] = user.getUserPassword();
		params[2] = device.getCode();
		params[3] = environment.getId();
		
		t.execute(params);
		String strJson = t.get();
		// Por algum motivo infernal aparecem conchetes no início e outra no fim
		// Desta forma se elas aparecem, removeos
		if(strJson.charAt(0) == '[' && strJson.endsWith("]")) {
			char a[] = strJson.toCharArray();
			strJson = "";
			for(int i = 1; i < (a.length-1); i++){
				strJson += a[i];
			}
		}
		Log.d("DEBUG","Response strJSON: "+strJson);
		//strJson = "{\"status\":\"OK\"}";
		JSONObject status = null;
		ArrayList<Action> actions = new ArrayList<Action>();
		status = (JSONObject) new JSONTokener(strJson).nextValue();
		
		
		if(status != null){
			if(status.has("status")){
				Log.d("DEBUG","Status: "+status.getString("status"));
				if(status.has("actions")){
					
					actions = JSONArrayToArrayList(status.getJSONArray("actions"));		
					Log.d("DEBUG","Num. Actions: "+actions.size());
				}
			}
		}
		return actions;

	}
	
	// fid - action
	private ArrayList<Action> JSONArrayToArrayList(JSONArray jsonActions) throws JSONException{
		Log.d("DEBUG","Num. JSON actions: "+jsonActions.length());
		ArrayList<Action> actions = new ArrayList<Action>();
		JSONObject temp = null;
		for(int i = 0; i < jsonActions.length();i++){
			temp = jsonActions.getJSONObject(i);
			actions.add(new Action(temp.getString("action"), temp.getInt("fid")));
			Log.d("DEBUG","Ação "+i+" - "+actions.get(i).getAction()+" fid: "+actions.get(i).getFunctionalityId());
		}
		return actions;
	}

	public Environment getEnvironments() {

		return null;
	}

	public User getUser(String userName, String password) {
		return null;
	}

	public Device deviceInformations(String code) {
		return null;
	}

}