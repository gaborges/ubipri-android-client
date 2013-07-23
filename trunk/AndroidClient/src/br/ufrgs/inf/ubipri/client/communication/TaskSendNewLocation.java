package br.ufrgs.inf.ubipri.client.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.ufrgs.inf.ubipri.util.Config;

import android.os.AsyncTask;

public class TaskSendNewLocation extends AsyncTask<Object, Void, String> {
	
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
	protected String doInBackground(Object ... params) {
		try {
	        URL url = new URL(Config.SERVER_HOST+"webresources/rest/change/location/json/response");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setDoOutput(true);
	                conn.setRequestMethod("PUT");
	                conn.setRequestProperty("Accept", "application/json");
	                conn.setRequestProperty("Content-Type", "application/json");
	                
	                //String input = "{\"environmentId\":5,\"userName\":\"borges\",\"userPassword\":\"12345\",\"deviceCode\":\"1234554321\"}";
	                String input = "{\"environmentId\":"+params[3]+",\"userName\":\""+params[0]
	                		+"\",\"userPassword\":\""+params[1]+"\",\"deviceCode\":\""+params[2]+"\"}";
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
