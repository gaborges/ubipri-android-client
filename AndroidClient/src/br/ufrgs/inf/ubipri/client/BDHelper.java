package br.ufrgs.inf.ubipri.client;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

///Essa classe herda de SQLiteOpenHelper que auxilia na cria��o do BD com seus m�todos nativos

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
	   * O construtor necessita do contexto da aplica��o
	   */
	  public BDHelper(Context context) {
	    super(context, NOME_BANCO, null, VERSAO_BANCO);	
	    /* O primeiro argumento � o contexto da aplicacao
	     * O segundo argumento � o nome do banco de dados
	     * O terceiro � um ponteiro para manipula��o de dados,  n�o precisaremos dele.
	     * O quarto � a vers�o do banco de dados
	     */
	  }

	  
	  /*
	   * Os m�todos onCreate e onUpgrade precisam ser sobrescritos
	   */
	  @Override
	  public void onCreate(SQLiteDatabase dataBase) {
		  //este m�todo � executado apenas se o banco n�o existir
	    int quantidadeScript = scriptSQLCreate.length;

	    for(int i = 0; i < quantidadeScript; i++){
	      String sql = scriptSQLCreate[i];
	      dataBase.execSQL(sql);
	    }	
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase dataBase, 
	  int oldVersion, int newVersion) {
		  // quando muda a vers�o do banco este m�todo � executado
		  // deve conter o c�digo da altera��o...
		  
	    dataBase.execSQL(scriptSQLDelete); //apaga tabela existente
	    onCreate(dataBase);//chama o m�todo onCreate para criar novamente
	  }

	}
