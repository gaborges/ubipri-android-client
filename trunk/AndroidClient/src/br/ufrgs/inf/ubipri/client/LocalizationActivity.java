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
import br.ufrgs.inf.ubipri.client.dao.LogDAO;
import br.ufrgs.inf.ubipri.client.dao.UserDAO;
import br.ufrgs.inf.ubipri.client.model.Action;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.Functionality;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import br.ufrgs.inf.ubipri.util.Config;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class LocalizationActivity extends Activity implements LocationListener {

	// Attributes of the object
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
	private LogDAO logDAO;
	private LocationManager locationManager;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localization);

		// Vincular Views com Elementos do Layout XML
		// They have the references to the elements in the XML layout
		txtLatitude = (TextView) findViewById(R.id.txtLatitude);
		txtLongitude = (TextView) findViewById(R.id.txtLongitude);
		txtAltitude = (TextView) findViewById(R.id.txtAltitude);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtEnvironment = (TextView) findViewById(R.id.txtCurrentEnvironment);

		// Instance variables based access to local and remote			
		this.envDAO = new EnvironmentDAO();
		this.devDAO = new DeviceDAO();
		this.useDAO = new UserDAO(getBaseContext());
		this.logDAO = new LogDAO(getBaseContext());
		this.communication = new Communication();
		
		// Funções para localização
		// Localization functions
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Criteria for select the provider of localization. Only are considered available providers.
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		// Get the last Known Location
		Location location = locationManager.getLastKnownLocation(provider);

		// If it found any location
		if (location != null) {
			// Logs do Android
			if(Config.DEBUG_LOCATION_ACTIVITY) Log.d(TAG, " Provider " + provider + " foi selecionado.");
			if(Config.DEBUG_LOCATION_ACTIVITY) Log.d(TAG, "Longitude: "+location.getLongitude()
					+" Latitude: "+location.getLatitude()
					+" Altitude: "+location.getAltitude());
			
			// this function will be called if has the localization change
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
		// Takes the values ​​of longitude, latitude and time of the location
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Date time = new Date(location.getTime());
		
		// Show the values in the screen
		txtLatitude.setText(String.valueOf(lat));
		txtLongitude.setText(String.valueOf(lng));
		txtAltitude.setText(String.valueOf(location.getAltitude()));
		txtTime.setText(time.toString());
		
		// if the new location not changing environment, then is not necessary to communicate the server.
		if(envDAO.hasChangedCurrentEnvironment(location)){
			
			// if the environment has changed, then get the informations of him. Return null, if not found.
			Environment environment = envDAO.getEnvironment(location);	
			// if the environment is not found, then saves at log
			if(environment == null){
				txtEnvironment.setText(R.string.location_not_available);
				this.logDAO.newLog(Config.LOGGED_USER_NAME, Config.DEVICE_CODE, -1);
			} else {
				// If is found the environment, then saves in log and shows in screen
				this.logDAO.newLog(Config.LOGGED_USER_NAME, Config.DEVICE_CODE, environment.getId());
				txtEnvironment.setText(environment.getName());
				try {
					// Send to server the new location and wait for response that contains the message with the actions
					ArrayList<Action> actions = this.communication.sendNewLocalization(environment, useDAO.getLastLoggedUser(),devDAO.getDevice());
					
					// Aplica ações
					// Apply the actions
					// OBS.: Not is implemented yet. Only read the action, but don't modify the system.
					applyActions(actions);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RuntimeException e){
					Toast.makeText(getBaseContext(), "Não foi possível enviar a nova localização para o servidor.", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
			// Update the location information to the user and the device are currently
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
		
		for(int i = 0; i < Config.DEVICE_FUNCTIONALITIES.length;i++){
			Functionality functionality = Config.DEVICE_FUNCTIONALITIES[i];
			Action action = isActionFunctionality(functionality,actions);
			if(action != null){
				// Aplica action - ON ou OFF
				if(Config.DEBUG_LOCATION_ACTIVITY) Log.d("DEBUG APPLY","Ação "+i+" - "+action.getAction()+" fid: "+action.getFunctionalityId());
				applyActionTest(action);
			}else {
				// retorna ao estado considerado normal / preferencia do usuário
				// em resumo busca todas as preferencias do usuário, ou configurações default do dispositivo, cria uma ação e envia-a para ser aplicada
				
			}
		}		
		return true;
	}
	
	protected void applyAction(Action action) {
		switch (action.getFunctionalityId()) {
		case 1: // BLUETOOTH_STATE
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); 
        	if(adapter != null) {
        		if(action.getAction().equals("on")){
        			if(Config.DEBUG_LOCATION_ACTIVITY) Log.d("DEBUG", "Bluetooth será habilitado!");
        			if(adapter.getState() != BluetoothAdapter.STATE_ON) {
            	        adapter.enable();
            	    }
        		} else if(action.getAction().equals("off")){
        			if(Config.DEBUG_LOCATION_ACTIVITY) Log.d("DEBUG", "Bluetooth será desabilitado!");
        			if (adapter.getState() != BluetoothAdapter.STATE_OFF){
        				adapter.disable();
        			}
         	    }  
        	}
			break;
		case 2: // SILENT_MODE
			   AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			   if(mode != null) {
	        		if(action.getAction().equals("on")){
	        			Log.d("DEBUG", "Modo Silêncioso habilitado!");
	        			mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	        		} else if(action.getAction().equals("off")){
	        			Log.d("DEBUG", "Modo Silêncioso desabilitado!");
	        			mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	         	    }  
	        	}
			break;
		case 3: // VIBRATION_STATE
			AudioManager mode2 = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			if(mode2 != null) {
        		if(action.getAction().equals("on")){
        			Log.d("DEBUG", "Modo Vibratório habilitado!");
        			mode2.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        		} else if(action.getAction().equals("off")){
        			Log.d("DEBUG", "Modo Vibratório desabilitado!");
        			mode2.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
         	    }  
        	}
			break;
		case 4: // AIRPLANE_MODE_STATE
			Log.d("DEBUG", "Não permitido ser alterado!");
			break;
		case 5: // WIFI_STATE    -- WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLED
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if(wifiManager != null) {
        		if(action.getAction().equals("on")){
        			Log.d("DEBUG", "Wi-fi habilitado!");
        			wifiManager.setWifiEnabled(true);
        		} else if(action.getAction().equals("off")){
        			Log.d("DEBUG", "Wi-fi desabilitado!");
        			wifiManager.setWifiEnabled(false);
         	    }  
        	}
			
			break;
		case 9: // RINGER_VOLUME_VALUE
			AudioManager mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if(mgr != null){
				if(action.getAction().equals("on")){
					Log.d("DEBUG", "Som RING habilitado!");
					mgr.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				} else if(action.getAction().equals("off")){
					Log.d("DEBUG", "Som RING desabilitado!");
					mgr.setStreamVolume(AudioManager.STREAM_RING,  AudioManager.ADJUST_SAME, AudioManager.FLAG_ALLOW_RINGER_MODES);
			
				}
			}
			break;
		case 15: // GPS_STATUS
			if(action.getAction().equals("on")){
				Log.d("DEBUG", "Listener do GPS habilitado!");
				locationManager.requestLocationUpdates(provider,400,1,this);
			} else if(action.getAction().equals("off")){
				Log.d("DEBUG", "Listener do GPS desabilitado!");
				locationManager.removeUpdates(this);
			}
			break;
		default:
			Log.d("DEBUG", "Funcionalidade {fid:"+action.getFunctionalityId()+",action:"+action.getAction()+"} não suportada!");
			break;
		}		
	}
	private Action isActionFunctionality(Functionality f , ArrayList<Action> actions){
		for(Action action : actions){
			if(action.getFunctionalityId() == f.getId()) return action;
		}
		return null;
	}
	private void applyActionTest(Action action) {
		switch (action.getFunctionalityId()) {
		case 1: // BLUETOOTH_STATE
    		if(action.getAction().equals("on")){
    			Log.d("DEBUG", "Bluetooth será habilitado!");
    		} else if(action.getAction().equals("off")){
    			Log.d("DEBUG", "Bluetooth será desabilitado!");
     	    }  
			break;
		case 2: // SILENT_MODE
    		if(action.getAction().equals("on")){
    			Log.d("DEBUG", "Modo Silêncioso habilitado!");
    		} else if(action.getAction().equals("off")){
    			Log.d("DEBUG", "Modo Silêncioso desabilitado!");
     	    }  
			break;
		case 3: // VIBRATION_STATE
    		if(action.getAction().equals("on")){
    			Log.d("DEBUG", "Modo Vibratório habilitado!");
    		} else if(action.getAction().equals("off")){
    			Log.d("DEBUG", "Modo Vibratório desabilitado!");
     	    }  
			break;
		case 4: // AIRPLANE_MODE_STATE
			Log.d("DEBUG", "Não permitido ser alterado!");
			break;
		case 5: // WIFI_STATE    -- WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLED
        		if(action.getAction().equals("on")){
        			Log.d("DEBUG", "Wi-fi habilitado!");
        		} else if(action.getAction().equals("off")){
        			Log.d("DEBUG", "Wi-fi desabilitado!");
         	    }  
			break;
		case 9: // RINGER_VOLUME_VALUE
			if(action.getAction().equals("on")){
				Log.d("DEBUG", "Som RING habilitado!");
			} else if(action.getAction().equals("off")){
				Log.d("DEBUG", "Som RING desabilitado!");
			}
			break;
		case 15: // GPS_STATUS
			if(action.getAction().equals("on")){
				Log.d("DEBUG", "Listener do GPS habilitado!");
			} else if(action.getAction().equals("off")){
				Log.d("DEBUG", "Listener do GPS desabilitado!");
			}
			break;
		default:
			Log.d("DEBUG", "Funcionalidade {fid:"+action.getFunctionalityId()+",action:"+action.getAction()+"} não suportada!");
			break;
		}		
	}
}