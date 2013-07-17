package br.ufrgs.inf.ubipri.client.dao;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.EnvironmentMap;
import br.ufrgs.inf.ubipri.client.model.Point;
import android.location.Location;
import android.util.Log;

public class EnvironmentDAO {
	private EnvironmentMap environmentMap;
	private static Environment rootEnvironment;
	private static Environment currentEnvironment;
	private InputStream inputStream;

	public EnvironmentDAO() {
		this.environmentMap = new EnvironmentMap();
		//EnvironmentDAO.setRootEnvironment(null);
		currentEnvironment = null;
	}

	public Environment getEnvironment(Location location) {
		// Abre arquivo com dados

		// Algoritmo de busca por coordenada = Search algorithm by coordinates
		return this.environmentMap.find(getEnvironmentInTree(), location);
	}

	public Environment getEnvironmentInTree() {
		if (rootEnvironment == null) {
			if(!this.updateStaticEnvironment()){
				Log.d("ERRO","Não foi possível gerar a arvore e ambientes!");
			}
		}
		// verifica se um ponto est� dentro da regi�o

		// Acessa o banco de dados or arquivo que contem o mapa do ambiente
		// Accesses the database or file that contains the environment map
		// falta fazer
		return EnvironmentDAO.getRootEnvironment();
	}
	
	public synchronized boolean updateStaticEnvironment(){
		if(inputStream != null){
			try {
				EnvironmentDAO.rootEnvironment = environmentMap
						.XMLToEnvironmentTree(inputStream);
				return true;
			} catch (ParserConfigurationException ex) {
				Log.d("EXEPTION", "Exception: " + ex);
			} catch (SAXException ex) {
				Log.d("EXEPTION", "Exception: " + ex);
			} catch (IOException ex) {
				Log.d("EXEPTION", "Exception: " + ex);
			}
		}
		return false;
	}
	
	public boolean hasChangedCurrentEnvironment(Location location){
		// Se o ambiente atual for nulo ent�o deve ser testado se ele mudou de ambiente
		if(EnvironmentDAO.currentEnvironment == null){
			return true;
		}
		if(!EnvironmentDAO.currentEnvironment.isContainedLocation(location)) return true;
		return false;
	}
	
	public boolean onChangeEnvironment(Location location){ // otimizar fun��o
		currentEnvironment = this.environmentMap.find(rootEnvironment, location);
		return true;
	}
	
	public static Environment getCurrentEnvironment(){
		return EnvironmentDAO.currentEnvironment;
	}

	public static Environment getRootEnvironment() {
		return rootEnvironment;
	}

	public static void setRootEnvironment(Environment rootEnvironment) {
		EnvironmentDAO.rootEnvironment = rootEnvironment;
	}
}
