package br.ufrgs.inf.ubipri.client.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.ufrgs.inf.ubipri.client.BDHelper;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.User;
import br.ufrgs.inf.ubipri.util.Config;

public class UserDAO {

	private static final String COLUNA_ID = "id";
	private static final String COLUNA_USER_NAME = "username";
	private static final String COLUNA_PASSWORD = "userpassword";
	private static final String COLUNA_FULL_NAME = "name";
	private static final String NOME_TABELA = "user";
	
	private String[] colunas = new String[] { COLUNA_ID, COLUNA_USER_NAME,
			COLUNA_PASSWORD };
	
	private Context ctx;
	private BDHelper sqLiteHelper = null;
	private SQLiteDatabase dataBase = null;
	
	public UserDAO(Context context) {
		sqLiteHelper = new BDHelper(context);
		dataBase = sqLiteHelper.getWritableDatabase();// acesso ao objeto
														// SQLiteDatabase
		this.ctx = context;
	}
	
	public void insert(User user){
		ContentValues values = new ContentValues();
		values.put(COLUNA_FULL_NAME, "");
		values.put(COLUNA_PASSWORD, user.getUserPassword());
		values.put(COLUNA_USER_NAME, user.getUserName());
		long id = dataBase.insert(NOME_TABELA, "id", values);
		user.setId((int) id);
	}
	
	public User get(String userName,String userPassword){
		// Por enquanto estático
		SQLiteDatabase db = new BDHelper(ctx).getWritableDatabase();
		String[] param = new String[] { userName,userPassword};

		Cursor c = db.query(NOME_TABELA, colunas, "username=? and userpassword = ?", param, null, null, null);

		if (c.moveToFirst()) {
			if(userName.equals(c.getString(c.getColumnIndex(COLUNA_USER_NAME)))
					&& userPassword.equals(c.getString(c.getColumnIndex(COLUNA_PASSWORD)))){
				User user = new User();
				user.setId(c.getInt(c.getColumnIndex(COLUNA_ID)));
				user.setUserName(c.getString(c.getColumnIndex(COLUNA_USER_NAME)));
				user.setUserPassword(c.getString(c.getColumnIndex(COLUNA_PASSWORD)));
				//user.setId(Config.STATIC_LOGGED_USER_ID);
				//user.setUserName(Config.STATIC_LOGGED_USER_NAME);
				//user.setUserPassword(Config.STATIC_LOGGED_USER_PASSWORD);
				return user;
			}
		}
		return null;
	}
	
	public User getLastLoggedUser(){
		User value = null; 
		SQLiteDatabase db = new BDHelper(ctx).getWritableDatabase();
		String[] param = new String[] { Config.LOGGED_USER_NAME};

		Cursor c = db.query(NOME_TABELA, colunas, "username=?", param, null, null, null);

		if (c.moveToFirst()) {
			value = new User();
			value.setId(c.getInt(c.getColumnIndex(COLUNA_ID)));
			value.setUserName(c.getString(c.getColumnIndex(COLUNA_USER_NAME)));
			value.setUserPassword(c.getString(c.getColumnIndex(COLUNA_PASSWORD)));
		}

		return value;
		/*
		user.setId(Config.STATIC_LOGGED_USER_ID);
		user.setUserName(Config.STATIC_LOGGED_USER_NAME);
		user.setUserPassword(Config.STATIC_LOGGED_USER_PASSWORD);
		return user;*/
	}
	
	public void updateUserEnvironment(Environment environment){
		Config.CURRENT_DEVICE_ENVIRONMENT = environment;
	}
}
