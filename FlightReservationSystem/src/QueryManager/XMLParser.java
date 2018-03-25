package QueryManager;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import Models.Airplane;
import Models.Airport;
import Models.Flight;


public class XMLParser {
	private XMLParser() { } // prevent this class from being instantiated
	
	private static List<Airport> airports;
	private static List<Airplane> airplanes;

	// Parse a given Airports.xml file into java objects 
	protected static List<Airport> parseAirports(File file){
		if(airports != null) { return airports; } // don't parse file more than once
		airports = new ArrayList<Airport>();

		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList airportList = doc.getElementsByTagName("Airport");
			for(int i =0;i<airportList.getLength();i++){
				Node airportNode = airportList.item(i);
				if(airportNode.getNodeType() == Node.ELEMENT_NODE){
					Element airportElement = (Element)airportNode;
					Airport airport = new Airport(airportElement.getAttribute("Name"),
							airportElement.getAttribute("Code"),
							Integer.valueOf(airportElement.getElementsByTagName("GMTOffset").item(0).getTextContent()));
					airports.add(airport);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return airports;
	}
	
	// Parse a given Airplanes.xml file into java objects 
	protected static List<Airplane> parseAirplanes(File file){
		if(airplanes != null) { return airplanes; } // don't parse file more than once
		airplanes = new ArrayList<Airplane>();

		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList airplaneList = doc.getElementsByTagName("Airplane");
			for(int i =0;i<airplaneList.getLength();i++){
				Node airplaneNode = airplaneList.item(i);
				if(airplaneNode.getNodeType() == Node.ELEMENT_NODE){
					Element airplaneElement = (Element)airplaneNode;
					Airplane airplane = new Airplane(airplaneElement.getAttribute("Manufacturer"),airplaneElement.getAttribute("Model"),
							Integer.valueOf(airplaneElement.getElementsByTagName("FirstClassSeats").item(0).getTextContent()),
							Integer.valueOf(airplaneElement.getElementsByTagName("CoachSeats").item(0).getTextContent()));
					airplanes.add(airplane);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return airplanes;
	}
	
	//parse the arrivingFlight and departingFlight xml string
	protected static List<Flight> parseFlights(String xml){
		List<Flight> flights = new ArrayList<Flight>();
		try{
			Document doc = loadXMLFromString(xml);
			NodeList flightList = doc.getElementsByTagName("Flight");
			for(int i =0;i<flightList.getLength();i++){
				Node flightNode = flightList.item(i);
				if(flightNode.getNodeType()==Node.ELEMENT_NODE){
					Element flightElement = (Element)flightNode;
					String airplaneType = flightElement.getAttribute("Airplane");
					NodeList departureList = flightElement.getElementsByTagName("Departure");
					String departureCode=null,depTime=null;
					for(int j=0;j<departureList.getLength();j++){
						Node departureNode = departureList.item(j);
						Element departureElement = (Element)departureNode;
						departureCode = departureElement.getElementsByTagName("Code").item(0).getTextContent();
						depTime = departureElement.getElementsByTagName("Time").item(0).getTextContent();
						
					}
					
					NodeList arrivalList = flightElement.getElementsByTagName("Arrival");
					String arrivalCode=null,arrTime=null;
					for(int k=0;k<arrivalList.getLength();k++){
						Node arrivalNode = arrivalList.item(k);
						Element arrivalElement = (Element)arrivalNode;
						arrivalCode = arrivalElement.getElementsByTagName("Code").item(0).getTextContent();
						arrTime = arrivalElement.getElementsByTagName("Time").item(0).getTextContent();
					}
					
					NodeList seatingList = flightElement.getElementsByTagName("Seating");
					double firstPrice=0,coachPrice=0;
					int firstNum=0,coachNum=0;
					for(int j=0;j<seatingList.getLength();j++){
						Node seatingNode = seatingList.item(j);
						Element seatingElement = (Element)seatingNode;
						NodeList firstList = seatingElement.getElementsByTagName("FirstClass");
				    	firstNum = Integer.parseInt(seatingElement.getElementsByTagName("FirstClass").item(0).getTextContent());
					    Node firstNode = firstList.item(0);
					    Element firstElement = (Element)firstNode;
					    int len1 =  firstElement.getAttribute("Price").length();
					    firstPrice = Double.valueOf(firstElement.getAttribute("Price").replaceAll(",", "").substring(1,len1-1));
					    NodeList coachList = seatingElement.getElementsByTagName("Coach");
					    coachNum = Integer.parseInt(seatingElement.getElementsByTagName("Coach").item(0).getTextContent());
					    Node coachNode = coachList.item(0);
					    Element coachElement = (Element)coachNode;
					    int len = coachElement.getAttribute("Price").length();
					    coachPrice = Double.valueOf(coachElement.getAttribute("Price").substring(1, len-1));   
					}
					Flight flight = new Flight(getAirplaneModel(airplaneType),Integer.parseInt(flightElement.getAttribute("FlightTime")),
							Integer.parseInt(flightElement.getAttribute("Number")),getAirport(departureCode),getAirport(arrivalCode),
							depTime,arrTime,firstPrice,coachPrice,firstNum,coachNum);
					flights.add(flight);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flights;
	}
	
	///////////////////////     HELPER METHODS BELOW HERE     //////////////////////////////////
	
	private static Document loadXMLFromString(String xml) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}
	
	private static Airplane getAirplaneModel(String airplaneType){
		File file = new File("src/Data/Airplanes.xml");
		List<Airplane> airplanes = parseAirplanes(file);
		for(Airplane airplane : airplanes){
			if(airplane.model.equals(airplaneType)){
				return airplane;
			}
		}
		return null;
	}
	
	private static Airport getAirport(String airportCode){
		File file = new File("src/Data/airports.xml");
		List<Airport> airports = parseAirports(file);
		for(Airport airport : airports){
			if(airport.code.equals(airportCode)){
				return airport;
			}
		}
		return null;
	}
}
