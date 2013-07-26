package br.ufrgs.inf.ubipri.client;

import br.ufrgs.inf.ubipri.client.communication.Communication;
import br.ufrgs.inf.ubipri.util.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	private Button btnLocalization;
	private Button btnLogout;
	private Button btnDeviceInformation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_menu);

		// Values for email and password at the time of the login attempt.
		this.btnLocalization = (Button) findViewById(R.id.btnLocalization);
		this.btnDeviceInformation = (Button) findViewById(R.id.btnDeviceInformation);
		this.btnLogout = (Button) findViewById(R.id.btnLogOut);
		
		// This function adds support to the event onClickListener, for Open the Localization Activity
		this.btnLocalization
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Open the Localization View
						Intent intentMenu = new Intent(getBaseContext(),LocalizationActivity.class);
						startActivity(intentMenu);
					}
				});

		// This function adds support to the event onClickListener, for Open the Device Information Activity
		this.btnDeviceInformation
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						// Open the DeviceInfoActivity View
						// OBS.: This screen is not implemented yet.
						Intent intentMenu = new Intent(getBaseContext(),DeviceInfoActivity.class);
						startActivity(intentMenu);
					}
				});

		// This function adds support to the event onClickListener, for logout of the user
		this.btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// It invalidate the last user logged session and close the menu screen
				Config.LOGGED_USER_NAME = "";
				finish();
			}
		});

	}
}
