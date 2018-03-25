package Models;

/*
 * A model meant to contain data about a given US airport.
 * Ideally, this class only holds data queried from server.
 */
public class Airport {
	public int gmtOffset; // offset for local time, from GMT
	public String code;   // 3-character airport code
	public String name;   // full airport name
	
	public Airport(String name, String code, int gmtOffset) {
		this.gmtOffset = gmtOffset;
		this.code = code;
		this.name = name;
	}
	public String toString(){
		return "Airport[code = "+code+",name = "+name+",GMTOffset = "+gmtOffset+"]";
	}
	
	public String getName() { return name; }
}
