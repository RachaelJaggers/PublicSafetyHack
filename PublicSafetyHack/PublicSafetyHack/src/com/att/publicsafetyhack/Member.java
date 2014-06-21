package com.att.publicsafetyhack;


//Imports------------------------------------------------------------------

//put imports here

//-------------------------------------------------------------------------
/**
* This is the seat class that will be used as the seat objects on the map.
* 
* @author Christina Olk (co271b)
* @version 2014.06.19
*/
public class Member {

	private String name;
	private String type;

	public Member() {
	} // empty constructor

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param loc
	 *            This represents the location of the seat.
	 * @param phNumber
	 *            This represents the phone number associated with the seat.
	 * @param prNumber
	 *            This represents the printer number associated with the seat.
	 */
	public Member(String n, String t) {
		name = n;
		type = t;
	}
}
