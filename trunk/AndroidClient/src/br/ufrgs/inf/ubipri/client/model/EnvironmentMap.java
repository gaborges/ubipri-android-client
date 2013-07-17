package br.ufrgs.inf.ubipri.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.location.Location;
import android.util.Log;

	public class EnvironmentMap {
	    public static double EARTH_RADIUS_KM = 6378.140;
	    public static int EARTH_RADIUS_M = 6378140;
	    /*
	  <environments>
	    <environment id="1" name="Porto Alegre" type="absotute" parentEnvironment="" >
	     <basePoint latitude="-30.072296142578118" longitude="-51.17763595581054" altitude="0.0" operatingRange="17550.786787873123" final="true" />
	        <point id="1" latitude="-29.9612808227539" longitude="-51.198184967041" altitude="0.0" />
	        <point id="2" latitude="-30.1073989868164" longitude="-51.2952117919922" altitude="0.0"  />
	        <point id="3" latitude="-30.2264022827148" longitude="-51.216136932373" altitude="0.0" />
	        <point id="4" latitude="-30.0949935913086" longitude="-51.0650444030762" altitude="0.0"  />
	        <point id="5" latitude="-29.9714050292969" longitude="-51.1136016845703" altitude="0.0" />
	    </environment>
	   </environment>
	     * 
	     */

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

	    
	    public Environment listEnvironmentToTree(ArrayList<Environment> list){
	        Environment root = null;
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

	    // mÃ©todo recursivo = Recursive Method
	    public Environment findEnvironment(Environment root, Location location) {
	        // if the environment is null: returns null; = Se ambiente for nulo: retorna null ;
	        if (root == null) {
	            return null;
	        }

	        // se foi encontrado pelo ponto base
	        if (isConteinedInThePoint(root.getBasePoint(), location)) {
	            // verifica pelo mapa interno se pertence a ele
	            if (root.isContainedLocation(location)) {
	                // verifica se possui filho
	                if (root.getFirstChild() != null) {
	                    Environment temp2 = findEnvironment(root.getFirstChild(), location);
	                    // se encontrou retorna
	                    if (temp2 != null) {
	                        return temp2;
	                    }
	                }
	                // se nÃ£o for nenhum filho retorna ele mesmo
	                return root;
	            }
	        }
	        // Verifica se Ã© um irmÃ£o,  Se nada, retorna null
	        return findEnvironment(root.getNext(), location);
	    }
	    
	    public Environment find(Environment root,Location location){
	        Environment found = null;
	        // possui no root, se sim procura se um filho é mais específico
	        if(this.isConteinedInThePoint(root.getBasePoint(), location)){
	            if(root.hasChild()){
	            found = find(root.getFirstChild(),location);
	            if(found != null) {
	                    return found;
	                } 
	            }  
	            return root;  
	        }
	        // Pesquisa nos irmãos
	        if(root.hasNext()){
	            found = find(root.getNext(), location);
	        }
	        return found;
	    }

	    private boolean isConteinedInThePoint(Point point, Location location) {
	        Double distance = geoDistanceInM(point.getLatitude(), point.getLongitude(), location.getLatitude(), location.getLongitude());
	        if (distance <= point.getOperatingRange()) {
	            return true;
	        }
	        return false;
	    }

	    public void show(Environment env) {
	      String m = "Environment{id:" + env.getId() + ",name:" + env.getName() + ",type:,"
	                + "parentEnvironment:" + ((env.getParentEnvironment() == null) ? "null" : env.getParentEnvironment().getId()) + ",basePoint{"
	                + ((env.getBasePoint() == null) ? "null" : ("latitude:" + env.getBasePoint().getLatitude()
	                + ",logitude:" + env.getBasePoint().getLongitude() + ",operatingRange:" + env.getBasePoint().getOperatingRange()
	                + ",final:" + env.getBasePoint().isfinal()) + "},points[");
	        for (Point p : env.getPoints()) {
	        	 m+=(",id:" + p.getId() + "latitude:" + p.getLatitude() + ",logitude:"
	                    + p.getLongitude() + ",operatingRange:" + p.getOperatingRange());
	        }
	        m+=("]}");
	        Log.d("SHOW",m);
	    }
	    public void showTree(Environment env) {
	        this.showTree(env, 0);
	    }
	    private void showTree(Environment env, int level){
	    	String m = "";
	        for (int i = 0; i < level; i++) m+=("\t");
	        m+=("id: "+env.getId()+" name: "+env.getName());
	        Log.d("SHOW",m);
	        // Child = filho
	        if(env.getFirstChild() != null) showTree(env.getFirstChild(), level+1);
	        // Brother = irmão
	        if(env.getNext() != null) showTree(env.getNext(), level);
	    }
	    

	    public Point makeBasePoint(ArrayList<Point> list) {
	        Point point = null;
	        // Se não possui elementos na lista retorna null
	        if (list.size() > 0) {
	            point = new Point();
	            // Obtem o ponto médio de latitude, longitude e altitude
	            for (Point p : list) {
	                point.setLatitude((point.getLatitude() + p.getLatitude()));
	                point.setLongitude(point.getLongitude() + p.getLongitude());
	                point.setAltitude(point.getAltitude() + p.getAltitude());
	            }
	            point.setLatitude((point.getLatitude() / list.size()));
	            point.setLongitude((point.getLongitude() / list.size()));
	            point.setAltitude((point.getAltitude() / list.size()));

	            // Busca a maior distância entre o ponto central e os demais pontos
	            for (Point p : list) {
	                double radius = this.getDistance(point, p);
	                //System.out.println("Radius: "+radius);
	                if (point.getOperatingRange() < radius) {
	                    point.setOperatingRange(radius);
	                }
	            }
	        }
	        return point;
	    }
	    
	    public double geoDistanceInKm(double firstLatitude,
				double firstLongitude, double secondLatitude, double secondLongitude) {

			// ConversÃ£o de graus pra radianos das latitudes
			double firstLatToRad = Math.toRadians(firstLatitude);
			double secondLatToRad = Math.toRadians(secondLatitude);

			// DiferenÃ§a das longitudes
			double deltaLongitudeInRad = Math.toRadians(secondLongitude
					- firstLongitude);

			// CÃ¡lcula da distÃ¢ncia entre os pontos
			return Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
					* Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
					* Math.sin(secondLatToRad))
					* EARTH_RADIUS_KM;
		}
	    public double geoDistanceInM(double firstLatitude,
				double firstLongitude, double secondLatitude, double secondLongitude) {

			// ConversÃ£o de graus pra radianos das latitudes
			double firstLatToRad = Math.toRadians(firstLatitude);
			double secondLatToRad = Math.toRadians(secondLatitude);

			// DiferenÃ§a das longitudes
			double deltaLongitudeInRad = Math.toRadians(secondLongitude
					- firstLongitude);

			// CÃ¡lcula da distÃ¢ncia entre os pontos
			return Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
					* Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
					* Math.sin(secondLatToRad))
					* EARTH_RADIUS_M;
		}

	    // Retorna a distancia entre duas coordenadas em metros
	    public double getDistance(double latitude, double longitude, double latitudePto, double longitudePto) {
	        double dlon, dlat, a, distancia;
	        dlon = longitudePto - longitude;
	        dlat = latitudePto - latitude;
	        a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon / 2), 2);
	        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/
	    }

	    public double getDistance(Point firstPoint, Point secondPoint) {
	        /*
	         return this.getDistance(firstPoint.getLatitude(), firstPoint.getLongitude(),
	                secondPoint.getLatitude(), secondPoint.getLongitude());
	        */
	        return this.geoDistanceInM(firstPoint.getLatitude(), firstPoint.getLongitude(),
	                secondPoint.getLatitude(), secondPoint.getLongitude());
	    }
}
