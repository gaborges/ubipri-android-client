package br.ufrgs.inf.ubipri.client;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

///Essa classe herda de SQLiteOpenHelper que auxilia na criação do BD com seus métodos nativos

public class BDHelper extends SQLiteOpenHelper {
	
	  private static final String NOME_BANCO = "androidubipri";
	  private static final int VERSAO_BANCO = 1;

	  private String scriptSQLDelete =  "DROP TABLE IF EXISTS pessoa";
	  
	  private String[] scriptSQLCreate = new String[] {
	    " create table user (id integer primary key autoincrement, username text not null," + 
	    		" userpassword text not null, name text); ",
	    " create table log (id integer primary key autoincrement, user text not null, device text not null," +
	    		" enviroment integer not null, comment text, time text"};

	  
	  /*
	   * O construtor necessita do contexto da aplicação
	   */
	  public BDHelper(Context context) {
	    super(context, NOME_BANCO, null, VERSAO_BANCO);	
	    /* O primeiro argumento é o contexto da aplicacao
	     * O segundo argumento é o nome do banco de dados
	     * O terceiro é um ponteiro para manipulação de dados,  não precisaremos dele.
	     * O quarto é a versão do banco de dados
	     */
	  }

	  
	  /*
	   * Os métodos onCreate e onUpgrade precisam ser sobrescritos
	   */
	  @Override
	  public void onCreate(SQLiteDatabase dataBase) {
		  //este método é executado apenas se o banco não existir
	    int quantidadeScript = scriptSQLCreate.length;

	    for(int i = 0; i < quantidadeScript; i++){
	      String sql = scriptSQLCreate[i];
	      dataBase.execSQL(sql);
	    }	
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase dataBase, 
	  int oldVersion, int newVersion) {
		  // quando muda a versão do banco este método é executado
		  // deve conter o código da alteração...
		  
	    dataBase.execSQL(scriptSQLDelete); //apaga tabela existente
	    onCreate(dataBase);//chama o método onCreate para criar novamente
	  }

	}
