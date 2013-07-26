package br.ufrgs.inf.ubipri.client.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.ufrgs.inf.ubipri.util.Config;

import android.os.AsyncTask;
import android.util.Log;

public class TaskSendNewCommunicationCode extends
		AsyncTask<Object, Void, String> {
	private String result;

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		result = "";
	}

	@Override
	protected String doInBackground(Object... params) {
		try {
			URL url = new URL(Config.SERVER_HOST
					+ "webresources/rest/insert/communicationCode/gcm");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"communicationCode\":\"" + params[3]
					+ "\",\"userName\":\"" + params[0] + "\",\"userPassword\":\""
					+ params[1] + "\",\"deviceCode\":\"" + params[2] + "\"}";
			conn.connect();
			
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				result += output;
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return result;
	}

}
