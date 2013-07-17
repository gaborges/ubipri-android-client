package br.ufrgs.inf.ubipri.client;

import br.ufrgs.inf.ubipri.client.communication.Communication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	private Button btnLocalization;
	private Button btnLogout;
	private Button btnUpdateLocalEnvironments;
	private Button btnDeviceInformation;
	private Communication communication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_menu);

		this.btnLocalization = (Button) findViewById(R.id.btnLocalization);
		this.btnDeviceInformation = (Button) findViewById(R.id.btnDeviceInformation);
		this.btnLogout = (Button) findViewById(R.id.btnLogOut);
		this.btnUpdateLocalEnvironments = (Button) findViewById(R.id.btnUpdateLocalEnvironments);

		this.btnLocalization
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// entra na tela de localização
						Intent intentMenu = new Intent(getBaseContext(),LocalizationActivity.class);
						startActivity(intentMenu);
					}
				});

		this.btnDeviceInformation
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// entra na tela para exibir informações do dispositivo
						Intent intentMenu = new Intent(getBaseContext(),DeviceInfoActivity.class);
						startActivity(intentMenu);
					}
				});

		this.btnUpdateLocalEnvironments
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Atualiza os locais cadastrados
						communication.getEnvironments();
					}
				});

		this.btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
}
