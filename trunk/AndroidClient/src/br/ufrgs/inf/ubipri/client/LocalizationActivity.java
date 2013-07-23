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

		// Declarar vari√°veis			
		this.envDAO = new EnvironmentDAO();
		this.devDAO = new DeviceDAO();
		this.useDAO = new UserDAO();
		this.communication = new Communication();
		
		// Fun√ß√µes para localiza√ß√£o
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Se encontrou alguma localiza√ß√£o
		if (location != null) {
			// Logs do Android
			Log.d(TAG, " Provider " + provider + " foi selecionado.");
			Log.d(TAG, "Longitude: "+location.getLongitude()
					+" Latitude: "+location.getLatitude()
					+" Altitude: "+location.getAltitude());
			
			// fun√ß√£o de mudan√ßa de ambiente
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
					// Aplica aÁıes
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
				} catch (RuntimeException e){
					Toast.makeText(getBaseContext(), "N„o foi possÌvel enviar a nova localizaÁ„o para o servidor.", Toast.LENGTH_SHORT).show();
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
		
		for(int i = 0; i < Config.DEVICE_FUNCTIONALITIES.length;i++){
			Functionality functionality = Config.DEVICE_FUNCTIONALITIES[i];
			Action action = isActionFunctionality(functionality,actions);
			if(action != null){
				// Aplica action - ON ou OFF
				Log.d("DEBUG","AÁ„o "+i+" - "+actions.get(i).getAction()+" fid: "+actions.get(i).getFunctionalityId());
				applyAction(action);
			}else {
				// retorna ao estado considerado normal / preferencia do usu·rio
				// em resumo busca todas as preferencias do usu·rio, ou configuraÁıes default do dispositivo, cria uma aÁ„o e envia-a para ser aplicada
				
			}
		}		
		return true;
	}
	
	private void applyAction(Action action) {
		switch (action.getFunctionalityId()) {
		case 1: // BLUETOOTH_STATE
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); 
        	if(adapter != null) {
        		if(action.equals("on")){
        			Log.d("DEBUG", "Bluetooth ser· habilitado!");
        			if(adapter.getState() != BluetoothAdapter.STATE_ON) {
            	        adapter.enable();
            	    }
        		} else if(action.equals("off")){
        			Log.d("DEBUG", "Bluetooth ser· desabilitado!");
        			if (adapter.getState() != BluetoothAdapter.STATE_OFF){
        				adapter.disable();
        			}
         	    }  
        	}
			break;
		case 2: // SILENT_MODE
			   AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			   if(mode != null) {
	        		if(action.equals("on")){
	        			Log.d("DEBUG", "Modo SilÍncioso habilitado!");
	        			mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	        		} else if(action.equals("off")){
	        			Log.d("DEBUG", "Modo SilÍncioso desabilitado!");
	        			mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	         	    }  
	        	}
			break;
		case 3: // VIBRATION_STATE
			AudioManager mode2 = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			if(mode2 != null) {
        		if(action.equals("on")){
        			Log.d("DEBUG", "Modo SilÍncioso habilitado!");
        			mode2.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        		} else if(action.equals("off")){
        			Log.d("DEBUG", "Modo SilÍncioso desabilitado!");
        			mode2.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
         	    }  
        	}
			break;
		case 4: // AIRPLANE_MODE_STATE
			Log.d("DEBUG", "N„o permitido ser alterado!");
			break;
		case 5: // WIFI_STATE    -- WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLED
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if(wifiManager != null) {
        		if(action.equals("on")){
        			Log.d("DEBUG", "Modo SilÍncioso habilitado!");
        			wifiManager.setWifiEnabled(true);
        		} else if(action.equals("off")){
        			Log.d("DEBUG", "Modo SilÍncioso desabilitado!");
        			wifiManager.setWifiEnabled(false);
         	    }  
        	}
			
			break;
		case 9: // RINGER_VOLUME_VALUE
			AudioManager mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if(mgr != null){
				if(action.equals("on")){
					Log.d("DEBUG", "Som RING habilitado!");
					mgr.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				} else if(action.equals("off")){
					Log.d("DEBUG", "Som RING desabilitado!");
					mgr.setStreamVolume(AudioManager.STREAM_RING,  AudioManager.ADJUST_SAME, AudioManager.FLAG_ALLOW_RINGER_MODES);
			
				}
			}
			break;
		case 15: // GPS_STATUS
			if(action.equals("on")){
				Log.d("DEBUG", "Listener do GPS habilitado!");
				locationManager.requestLocationUpdates(provider,400,1,this);
			} else if(action.equals("off")){
				Log.d("DEBUG", "Listener do GPS desabilitado!");
				locationManager.removeUpdates(this);
			}
			break;
		default:
			Log.d("DEBUG", "Funcionalidade {fid:"+action.getFunctionalityId()+",action:"+action.getAction()+"} n„o suportada!");
			break;
		}		
	}
	private Action isActionFunctionality(Functionality f , ArrayList<Action> actions){
		for(Action action : actions){
			if(action.getFunctionalityId() == f.getId()) return action;
		}
		return null;
	}
}