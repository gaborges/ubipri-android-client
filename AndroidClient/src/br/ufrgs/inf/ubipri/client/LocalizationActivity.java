package br.ufrgs.inf.ubipri.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import br.ufrgs.inf.ubipri.client.communication.Communication;
import br.ufrgs.inf.ubipri.client.dao.DeviceDAO;
import br.ufrgs.inf.ubipri.client.dao.EnvironmentDAO;
import br.ufrgs.inf.ubipri.client.dao.UserDAO;
import br.ufrgs.inf.ubipri.client.model.Action;
import br.ufrgs.inf.ubipri.client.model.Environment;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class LocalizationActivity extends Activity implements LocationListener {

	private static final String TAG = " MainActivity ";
	private TextView txtLatitude;
	private TextView txtLongitude;
	private TextView txtAltitude;
	private TextView txtTime;
	private TextView txtEnvironment;
	private EnvironmentDAO envDAO;
	private Communication communication;
	private DeviceDAO devDAO;
	private UserDAO useDAO;
	private LocationManager locationManager;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localization);

		// Vincular Views com Elementos do Layout XML
		txtLatitude = (TextView) findViewById(R.id.txtLatitude);
		txtLongitude = (TextView) findViewById(R.id.txtLongitude);
		txtAltitude = (TextView) findViewById(R.id.txtAltitude);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtEnvironment = (TextView) findViewById(R.id.txtCurrentEnvironment);

		// Declarar variáveis			
		this.envDAO = new EnvironmentDAO();
		this.devDAO = new DeviceDAO();
		this.useDAO = new UserDAO();
		this.communication = new Communication();
		
		// Funções para localização
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Se encontrou alguma localização
		if (location != null) {
			// Logs do Android
			Log.d(TAG, " Provider " + provider + " foi selecionado.");
			Log.d(TAG, "Longitude: "+location.getLongitude()
					+" Latitude: "+location.getLatitude()
					+" Altitude: "+location.getAltitude());
			
			// função de mudança de ambiente
			onLocationChanged(location);
		} else {
			txtLatitude.setText(R.string.location_not_available);
			txtLongitude.setText(R.string.location_not_available);
			txtAltitude.setText(R.string.location_not_available);
			txtEnvironment.setText(R.string.location_not_available);
			txtTime.setText(R.string.location_not_available);
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider,400,1,this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	 	locationManager.removeUpdates(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Date time = new Date(location.getTime());
		txtLatitude.setText(String.valueOf(lat));
		txtLongitude.setText(String.valueOf(lng));
		txtAltitude.setText(String.valueOf(location.getAltitude()));
		txtTime.setText(time.toString());
		if(envDAO.hasChangedCurrentEnvironment(location)){
			Environment environment = envDAO.getEnvironment(location);
			if(environment == null){
				txtEnvironment.setText(R.string.location_not_available);
			} else {
				txtEnvironment.setText(environment.getName());
				try {
					ArrayList<Action> actions = this.communication.sendNewLocalization(environment, useDAO.getLastLoggedUser(),devDAO.getDevice());
					// Aplica a��es
					applyActions(actions);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			useDAO.updateUserEnvironment(environment);
			devDAO.updateDeviceEnvironment(environment);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this," Provider desabilitado " + provider,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this," Novo provider " + provider,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider , int status , Bundle extras) {
		// TODO Auto-generated method stub

	}

	public boolean applyActions(ArrayList<Action> actions){
		/*
		 * public static final Functionality DEVICE_FUNCTIONALITIES[] = {
		new Functionality(1, "Bluetooh"), // Adicionar o estado atual do dispositico, String actual Action
		new Functionality(2, "Silent Mode"),
		new Functionality(3, "Vibrate Alert"),
		new Functionality(4, "Airplane Mode"),
		new Functionality(5, "Wi-Fi"),
		new Functionality(6, "Mobile Network Data Access"),
		new Functionality(7, "System Volume"),
		new Functionality(8, "Media Volume"),
		new Functionality(9, "Ringer Volume"),
		new Functionality(10, "Screen Timeout"),
		new Functionality(11, "Screen Brightness"),
		new Functionality(12, "SMS"),
		new Functionality(13, "Launch App"),
		new Functionality(14, "Camera Access"),
		new Functionality(15, "GPS")
	};
		 * 
		 */
		return true;
	}
}