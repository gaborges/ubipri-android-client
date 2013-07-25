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
		
		// une a referï¿½ncia do XML com objetos
		this.edtUserName = (EditText) findViewById(R.id.edtUserName);
		this.edtPassword = (EditText) findViewById(R.id.edtPassword);
		this.btnLogin = (Button) findViewById(R.id.btnLogin);
		this.btnExit = (Button) findViewById(R.id.btnExit);
		
		// Cria o banco de dados caso ele nï¿½o exista
		this.createDB();
		this.userDAO = new UserDAO();
		
		try {
			InputStream is = getAssets().open("environments.xml");
			Log.d("DEBUG", "Possível ler:  "+is.available());
			EnvironmentDAO.setRootEnvironment(XMLToEnvironmentTree(is));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Ao ser clicado no login recupera os valores dos edit texts
				userName = edtUserName.getText().toString();
				userPassword = edtPassword.getText().toString();
				
				// verifica se o usuï¿½rio jï¿½ estï¿½ cadastrado
				User user = userDAO.get(userName, userPassword);
				if(user == null){ 
					// se nï¿½o estï¿½ cadastrato pergunta se o usuï¿½rio estaï¿½cadastrado no servidor
					communication = new Communication();
					try {
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
						// se nï¿½o encontrou no servidor, usuï¿½rio nï¿½o tem permissï¿½o de uso ou errou a senha
						Toast.makeText(getBaseContext(), "Incorrect Username or Password.", Toast.LENGTH_LONG).show();
						return; // termina execuÃ§Ã£o
					}
					// Se encontrou o novo usuÃ¡rio, insere ele no banco local
					userDAO.insert(user);
				}
				// Apaga os valores dos EditTexts
				edtPassword.setText("");
				edtUserName.setText("");
				
				// se usuï¿½rio foi encontrado passa dados por parï¿½metro e abre o menu
				Intent intentMenu = new Intent(getBaseContext(),MenuActivity.class);
				intentMenu.putExtra("userName", userName);
				intentMenu.putExtra("userPassword", userPassword);
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
	
	private void createDB(){
		// Falta criar o banco com tabelas
		
	}
	
	 public ArrayList<Environment> XMLToEnvironmentList(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
	    	Log.d("DEBUG", "Possível ler no MAP:  "+inputStream.available());
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
	                Log.d("READ XML","parentEnvironment: " + element.getAttribute("parentEnvironment"));
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
