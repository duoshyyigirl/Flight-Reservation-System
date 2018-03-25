import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class gmtDatabaseBuilder {
	private gmtDatabaseBuilder() {}
	
	/*
	 * Provided that there is a file at src/Data/airports.xml,
	 * this will ensure that the airports.xml file contains GMTOffset fields.
	 */
	public static void run() {
		try {
			//loading airports.xml document
			File fXmlFile = new File("src/Data/airports.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element root = doc.getDocumentElement();
			
			//looping through each airport
			NodeList Airports = root.getElementsByTagName("Airport");
			for(int i = 0; i < Airports.getLength(); i++) {
				Element Airport = (Element) Airports.item(i);					
				NodeList children = Airport.getChildNodes();
				
				//extracting lat/long fields
				double[] coords = new double[2];
				for(int j = 0; j < children.getLength(); j++) {
					String name = children.item(j).getNodeName();
					String val = "";
					if(name.equals("Latitude")) {
						val = children.item(j).getTextContent();
						coords[0] = Double.parseDouble(val);
					} else if(name.equals("Longitude")) {
						val = children.item(j).getTextContent();
						coords[1] = Double.parseDouble(val);
					}
				}
				insertGMTField(doc, Airport, coords);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * queries an API to get a GMT offset in hours (requires 1s between queries)
	 */
	private static int getGMTOffsetFromAPI(double[] coords) {
		try {
			//query server
			TimeUnit.SECONDS.sleep(1); //api requires >1s between queries
			String baseStr = "http://api.timezonedb.com/v2/get-time-zone?key=7SAIBM5LH9VW&by=position";
			URL url = new URL(baseStr + "&lat=" + coords[0] + "&lng=" + coords[1]);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				String encoding = connection.getContentEncoding();
				encoding = (encoding == null ? "UTF-8" : encoding);
				
				//interpret server query as a string
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
		        StringBuilder sb = new StringBuilder();
				while((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
				
				//convert string to .xml
				Document doc = convertStringToDocument(sb.toString());
				Element root = doc.getDocumentElement();
				NodeList children = root.getChildNodes();
				
				//search through .xml
				for(int i = 0; i < children.getLength(); i++) {
					if(children.item(i).getNodeName().equals("gmtOffset")) {
						return Integer.parseInt(children.item(i).getTextContent())/(60*60);
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/*
	 *  Inserts a GMT offset field into the given DOM,
	 *  then saves to file
	 */
	private static void insertGMTField(Document doc, Element Airport, double[] coords) {
		//inserting GMTOffset field
		boolean offsetExists = Airport.getElementsByTagName("GMTOffset").getLength() == 1;
		if(offsetExists) { return; }
		int offsetTime = getGMTOffsetFromAPI(coords);
		
		Element offset = doc.createElement("GMTOffset");
		offset.appendChild(doc.createTextNode(offsetTime + ""));
		Airport.appendChild(offset);
		
		Source source = new DOMSource(doc);
		File file = new File("src/Data/airports.xml");
	    StreamResult result = new StreamResult(file);
		
	    try {
		    Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
			return;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * converts java string object to DOM parsable xml document
	 */
    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
}
