package br.ufrgs.inf.ubipri.client.dao;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import br.ufrgs.inf.ubipri.client.BDHelper;

public class LogDAO {

	private static final String COLUNA_ID = "id";
	private static final String COLUNA_USER = "user";
	private static final String COLUNA_DEVICE = "device";
	private static final String COLUNA_ENVIRONMENT = "enviroment";
	private static final String COLUNA_COMMENT = "comment";
	private static final String COLUNA_TIME = "time";
	private static final String NOME_TABELA = "log";
	
	private Context ctx;
	private BDHelper sqLiteHelper = null;
	private SQLiteDatabase dataBase = null;


	public LogDAO(Context context) {
		sqLiteHelper = new BDHelper(context);
		dataBase = sqLiteHelper.getWritableDatabase();// acesso ao objeto
														// SQLiteDatabase
		this.ctx = context;
	}
	
	public boolean newLog(String userName,String deviceCode,int environment){
		return newLog(userName,deviceCode,environment,"OK");
	}

	public boolean newLog(String userName, String deviceCode, int environment,
			String comment) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_DEVICE, deviceCode);
		values.put(COLUNA_COMMENT, comment);
		values.put(COLUNA_ENVIRONMENT, environment);
		values.put(COLUNA_TIME, String.valueOf(new Date().getTime()));
		values.put(COLUNA_USER, userName);
		dataBase.insert(NOME_TABELA, "id", values);
		return true;
	}
}
