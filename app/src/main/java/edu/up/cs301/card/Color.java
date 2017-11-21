package edu.up.cs301.card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Color the suit of a playing card (e.g., Club).
 * 
 * @author Steven R. Vegdahl 
 * @version December 2016
 */
public enum Color implements Serializable {
	
	// Red
	Red,
	
	// Blue
	Blue,
	
	// Yellow
	Yellow,
	
	// Green
	Green,

	// Black
	Black,

	//Backside
	Backside
	;

	// to satisfy the Serializable interface
	private static final long serialVersionUID = 521100342061033418L;

	/**
	 * the "short" (one-character) name that represents the suit
	 * 
	 * @return
	 * 		the suit's short name
	 */
	public char shortName() {
		if (longName() == Black.toString()){
			return 'z'; 						//z to indicate black, since blue is using 'b'
		}
		if (longName() == Backside.toString()){
			return 'x'; 						//z to indicate black, since blue is using 'b'
		}
		else {
			return longName().charAt(0);		//otherwise use the first letter for the color shortname
		}
	}
	
	/**
	 * the "long" (full-word) name that represents the suit
	 * 
	 * @return
	 * 		the suit's long name
	 */
	public String longName() {
		return toString();
	}

	// an array to help us convert from character to suit; it will be initialized
	// the first time the fromChar method is called
	private static ArrayList<Character> suitChars = null;
	
	/**
	 * convert a character into a suit
	 * 
	 * @param c
	 * 		the character
	 * @return
	 * 		the suit that corresponds to that character, or null if the character
	 * 		does not denote a suit
	 */
	public static Color fromChar(char c) {
		// if the helper-array is null, create it
		if (suitChars == null) {
			initSuitChars();
		}
		// find the numeric rank of the card denoted by the character
		int idx = suitChars.indexOf(Character.toLowerCase(c));
		// return null if the character did not denote a rank
		if (idx < 0) return null;
		// return the corresponding rank
		return Color.values()[idx];
	}
	
	/**
	 * method that initializes the helper-array for use by the 'fromChar' method
	 */	
	private static void initSuitChars() {
		
		// the list of suits, in numeric order
		Color[] vals = Color.values();
		
		// create the list of characters
		suitChars = new ArrayList<Character>();
		
		// initialize the list with the characters, in suit-order
		for (Color s : vals) {
			suitChars.add(Character.toLowerCase(s.shortName()));
		}
	}
}
