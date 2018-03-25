package Models;

/*
 * A model meant to contain data about a given customer flight order.
 * Ideally, this class only holds data that was input from UI.
 */
public class Order {
	public boolean roundTrip;	// whether order is for a round trip
	public boolean firstClass;	// whether order is first class
	
	public Airport dep;			// departing airpot
	public Airport arr;			// arrival airport
	
	String startTime;			// formatted as: "YYYY MMM DD HH:MM" + " GMT"
	
	public Order(Airport dep, Airport arr, String startTime, boolean roundTrip, boolean firstClass) {
		this.roundTrip = roundTrip;
		this.firstClass = firstClass;
		this.dep = dep;
		this.arr = arr;
		this.startTime = startTime;
	}
}