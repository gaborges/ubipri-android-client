package br.ufrgs.inf.ubipri.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.ufrgs.inf.ubipri.client.communication.Communication;
import br.ufrgs.inf.ubipri.client.dao.EnvironmentDAO;
import br.ufrgs.inf.ubipri.client.dao.UserDAO;
import br.ufrgs.inf.ubipri.client.model.Environment;
import br.ufrgs.inf.ubipri.client.model.Point;
import br.ufrgs.inf.ubipri.client.model.User;
import br.ufrgs.inf.ubipri.util.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	// Values for email and password at the time of the login attempt.
	private String userName;
	private String userPassword;
	private UserDAO userDAO;
	private Communication communication;
	
	// UI references.
	private EditText edtUserName;
	private EditText edtPassword;
	private Button btnExit;
	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		
		// They have the references to the elements in the XML layout
		this.edtUserName = (EditText) findViewById(R.id.edtUserName);
		this.edtPassword = (EditText) findViewById(R.id.edtPassword);
		this.btnLogin = (Button) findViewById(R.id.btnLogin);
		this.btnExit = (Button) findViewById(R.id.btnExit);
		
		// UserDao instantiates an object to manipulate the database, if the database has not been created then it is created
		this.userDAO = new UserDAO(getBaseContext());
		
		try {
			// Open the file: environments.xml in assets. 
			InputStream is = getAssets().open("environments.xml");
			if(Config.DEBUG_LOGIN_ACTIVITY)Log.d("DEBUG", "Possível ler:  "+is.available());
			// This function read the file environments.xml for create a tree to be used for localization activity
			EnvironmentDAO.setRootEnvironment(XMLToEnvironmentTree(is));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		// This function adds support to the event onClickListener
		this.btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If the btnLogin Button is clicked the values of edit texts will be recovered 
				// Ao ser clicado no login recupera os valores dos edit texts
				userName = edtUserName.getText().toString();
				userPassword = edtPassword.getText().toString();
				
				// Checks if the user is registered in the local database. If he not, return null;
				User user = userDAO.get(userName, userPassword);
			
				if(user == null){ 
					// The Object Communication is instantiates
					communication = new Communication();
					try {
						// Checks if the user is registred in the remote server. If he not, return null;
						user = communication.getUser(userName, userPassword);
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
					if(user == null){
						// If the userLogin and the userPassword not found on the server database and on the local database
						// 	show a message: Incorrect Username or Password.
						Toast.makeText(getBaseContext(), "Incorrect Username or Password.", Toast.LENGTH_LONG).show();
						return;
					}
					// If the user is found on the server, then he will be inserted in the local database
					userDAO.insert(user);
				}
				// Clears the values ​​in the Edit Texts
				edtPassword.setText("");
				edtUserName.setText("");
				
				// Open the menu and put the userName in the LOGGED_USER_NAME static variable, to be used.
				Intent intentMenu = new Intent(getBaseContext(),MenuActivity.class);
				Config.LOGGED_USER_NAME = user.getUserName();
				startActivity(intentMenu);
			}
		});
		
		this.btnExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/*
	 * Function for read the XML that contains the registred environments.
	 * OBS.: This approach is for testing. Future environments should be registered by the server remotely.
	 */
	 public ArrayList<Environment> XMLToEnvironmentList(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		    if(Config.DEBUG_LOGIN_ACTIVITY)Log.d("DEBUG", "Possível ler no MAP:  "+inputStream.available());
	    	ArrayList<Environment> listEnvironment = new ArrayList<Environment>();
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(inputStream);
	        doc.getDocumentElement().normalize();

	        // NodeList utilizado para acessar os Elementos internos dos carpapios
	        NodeList list = doc.getElementsByTagName("environment"), tempList;

	        for (int i = 0; i < list.getLength(); i++) {
	            Environment temp = new Environment();
	            Element element = (Element) list.item(i), elementTemp;
	            temp.setId(Integer.parseInt(element.getAttribute("id")));
	            temp.setName(element.getAttribute("name"));
	           /* if (element.hasAttribute("type")) {
	                Log.d("READ XML","Type: " + element.getAttribute("type"));
	            } else {
	            	Log.d("READ XML","Default Type: " + element.getAttribute("type"));
	            }*/
	            if (element.hasAttribute("parentEnvironment")) {
	            	if(Config.DEBUG_LOGIN_ACTIVITY) Log.d("READ XML","parentEnvironment: " + element.getAttribute("parentEnvironment"));
	                // depois verifica se possui na lista o id do ambiente, senão cria uma objeto somente com o ID
	                temp.setParentEnvironment(new Environment(Integer.parseInt(element.getAttribute("parentEnvironment"))));
	            }

	            if (0 < element.getElementsByTagName("basePoint").getLength()) {
	                elementTemp = (Element) element.getElementsByTagName("basePoint").item(0);
	                temp.setBasePoint(new Point());
	                temp.getBasePoint().setLatitude(Double.parseDouble(elementTemp.getAttribute("latitude")));
	                temp.getBasePoint().setLongitude(Double.parseDouble(elementTemp.getAttribute("longitude")));
	                temp.getBasePoint().setAltitude(Double.parseDouble(elementTemp.getAttribute("altitude")));
	                temp.getBasePoint().setOperatingRange(Double.parseDouble(elementTemp.getAttribute("operatingRange")));
	                if (elementTemp.hasAttribute("final")) {
	                    temp.getBasePoint().setIsfinal(Boolean.parseBoolean(elementTemp.getAttribute("final")));
	                } else {
	                    temp.getBasePoint().setIsfinal(false);
	                }
	            }

	            if (0 < element.getElementsByTagName("point").getLength()) {
	                if (temp.getBasePoint() != null) {
	                    if (!temp.getBasePoint().isfinal()) {
	                        tempList = element.getElementsByTagName("point");
	                        for (int j = 0; j < tempList.getLength(); j++) {
	                            elementTemp = (Element) tempList.item(j);
	                            temp.getPoints().add(new Point());
	                            temp.getPoints().get(j).setLatitude(Double.parseDouble(elementTemp.getAttribute("latitude")));
	                            temp.getPoints().get(j).setLongitude(Double.parseDouble(elementTemp.getAttribute("longitude")));
	                            temp.getPoints().get(j).setAltitude(Double.parseDouble(elementTemp.getAttribute("altitude")));
	                            if (elementTemp.hasAttribute("operatingRange")) {
	                                temp.getPoints().get(j).setOperatingRange(Double.parseDouble(elementTemp.getAttribute("operatingRange")));
	                            }
	                        }
	                    }
	                } else {
	                    tempList = element.getElementsByTagName("point");
	                    for (int j = 0; j < tempList.getLength(); j++) {
	                        elementTemp = (Element) tempList.item(j);
	                        temp.getPoints().add(new Point());
	                        temp.getPoints().get(j).setLatitude(Double.parseDouble(elementTemp.getAttribute("latitude")));
	                        temp.getPoints().get(j).setLongitude(Double.parseDouble(elementTemp.getAttribute("longitude")));
	                        temp.getPoints().get(j).setAltitude(Double.parseDouble(elementTemp.getAttribute("altitude")));
	                        //System.out.println("Latitude: "+temp.getPoints().get(j).getLatitude());
	                        if (elementTemp.hasAttribute("operatingRange")) {
	                            temp.getPoints().get(j).setOperatingRange(Double.parseDouble(elementTemp.getAttribute("operatingRange")));
	                        }
	                    }
	                }
	            }

	            listEnvironment.add(temp);
	        }
	        return listEnvironment;
	    }
	 
	 public Environment XMLToEnvironmentTree(InputStream inputStream) throws ParserConfigurationException, SAXException, SAXException, IOException {
	        Environment root = null;
	        ArrayList<Environment> list = this.XMLToEnvironmentList(inputStream);
	        // Encontra o nó raiz
	        for(Environment e : list){
	            if(e.getParentEnvironment() == null){
	                root = e;
	                break;
	            }
	        }
	        // se possui raiz
	        if(root != null){
	            // procura os filhos do root
	            for (Environment o : list){
	                this.addChildrenEnvironment(o, list);
	            }
	        }
	        return root;
	    }
	 public void addChildrenEnvironment(Environment root, ArrayList<Environment> list){
	        for (Environment e : list){
	            if(e.getParentEnvironment() != null) {
	                if(e.getParentEnvironment().getId() == root.getId()){
	                    root.addChild(e);
	                }
	            }
	        }
	    }
}
