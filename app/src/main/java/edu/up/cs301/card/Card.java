package edu.up.cs301.card;

import java.io.Serializable;

import edu.up.cs301.game.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * A playing card in the standard Phase10 deck. The images, which have been
 * placed in the res/drawable-hdpi folder in the project, are from
 * Jacob Apenes.
 * 
 * In order to display the card-images on the android you need to call the
 *   Card.initImages(currentActivity)
 * method during initialization; the 52 image files need to be placed in the
 * res/drawable-hdpi project area.
 * 
 * @author Steven R. Vegdahl
 * @author Trenton Langer
 * @version November 2017
 */
public class Card implements Serializable {

	// to satisfy the Serializable interface
	private static final long serialVersionUID = 893542931190030342L;
	
	// instance variables: the card's rank and the suit
    private Rank rank;
    private Color suit;

	/**
	 * Constructor for class card
	 *
	 * @param r the Rank of the card
	 * @param s the Color of the card
	 */
	public Card(Rank r, Color s) {
		rank = r;
		suit = s;
	}

	/**
	 * Constructor for class card (copy constructor)
	 *
	 * @param orig the card to copy
	 */
	public Card(Card orig) {
		rank = orig.rank;
		suit = orig.suit;
	}

    /**
     * Creates a Card from a String.  (Can be used instead of the
     * constructor.)
     *
     * @param str
     * 		a two-character string representing the card, which is
     *		of the form "4C", with the first character representing the rank,
     *		and the second character representing the suit.  Each suit is
     *		denoted by its first letter.  Each single-digit rank is represented
     *		by its digit.  The letters 'T', 'J', 'Q', 'K' and 'A', represent
     *		the ranks Ten, Jack, Queen, King and Ace, respectively.
     * @return
     * 		A Card object that corresponds to the 'str' string. Returns
     *		null if 'str' has improper format.
     */
    public static Card fromString(String str) {
    	// check the string for being null
        if (str == null) return null;
        
        // trim the string; return null if length is not 2
        str = str.trim();
        if (str.length() !=2) return null;
        
        // get the rank and suit corresponding to the two characters
        // in the string
        Rank r = Rank.fromChar(str.charAt(0));
        Color s = Color.fromChar(str.charAt(1));
        
        // if both rank and suit are non-null, create the corresponding
        // card; if either is null, return null
        return r==null || s == null ? null : new Card(r, s);
    }

    /**
     * Produces a textual description of a Card.
     *
     * @return
	 *		A string such as "Red Ten", which describes the card.
     */
    public String toString() {
        return suit.longName()+" " + rank.longName();
    }

	/**
	 * Tells whether object are equal -- in other words that they are both Card
	 * objects that represent the same card.
	 *
	 * @return
	 *		true if the two card objects represent the same card, false
	 *		otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Card)) return false;
		Card c = (Card)other;
		return this.rank == c.rank && this.suit == c.suit;
	}

	@Override
	public int hashCode() {
		return rank.hashCode()*18737 + suit.hashCode()*1737372;
	}

    /**
     * Draws the card on a Graphics object.  The card is drawn as a
     * white card with a black border.  If the card's rank is numeric, the
     * appropriate number of spots is drawn.  Otherwise the appropriate
     * picture (e.g., of a queen) is included in the card's drawing.
     *
     * @param g  the graphics object on which to draw
     * @param where  a rectangle that tells where the card should be drawn
     */
    public void drawOn(Canvas g, RectF where) {
    	// create the paint object
    	Paint p = new Paint();
    	p.setColor(android.graphics.Color.BLACK);
    	
    	// get the bitmap for the card
    	Bitmap bitmap = cardImages[this.getSuit().ordinal()][this.getRank().ordinal()];
    	
    	// create the source rectangle
    	Rect r = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
    	
    	// draw the bitmap into the target rectangle
    	g.drawBitmap(bitmap, r, where, p);
    }

    
    /**
     * Gives a two-character version of the card (e.g., "RT" for a red ten).
     */
    public String shortName() {
        return "" + getRank().shortName() + getSuit().shortName();
    }

    /**
     * Tells the card's rank.
     *
     * @return
	 *		a Rank object (actually of a subclass) that tells the card's
     *		rank (e.g., Wild, three).
     */
    public Rank getRank() {
    	return rank;
    }

    /**
     * Tells the card's suit.
     *
     * @return
	 *		a Color object (actually of a subclass) that tells the card's
     *		color (e.g., red, blue).
     */
    public Color getSuit() {
    	return suit;
    }
 
    // array that contains the android resource indices for the card
    // images
    private static int[][] resIdx = {
    	{
    		R.drawable.r_01, R.drawable.r_02, R.drawable.r_03,
    		R.drawable.r_04, R.drawable.r_05, R.drawable.r_06,
    		R.drawable.r_07, R.drawable.r_08, R.drawable.r_09,
    		R.drawable.r_10, R.drawable.r_11, R.drawable.r_12,
    	},
    	{
			R.drawable.b_01, R.drawable.b_02, R.drawable.b_03,
			R.drawable.b_04, R.drawable.b_05, R.drawable.b_06,
			R.drawable.b_07, R.drawable.b_08, R.drawable.b_09,
			R.drawable.b_10, R.drawable.b_11, R.drawable.b_12,
    	},
    	{
			R.drawable.y_01, R.drawable.y_02, R.drawable.y_03,
			R.drawable.y_04, R.drawable.y_05, R.drawable.y_06,
			R.drawable.y_07, R.drawable.y_08, R.drawable.y_09,
			R.drawable.y_10, R.drawable.y_11, R.drawable.y_12,
    	},
    	{
			R.drawable.g_01, R.drawable.g_02, R.drawable.g_03,
			R.drawable.g_04, R.drawable.g_05, R.drawable.g_06,
			R.drawable.g_07, R.drawable.g_08, R.drawable.g_09,
			R.drawable.g_10, R.drawable.g_11, R.drawable.g_12,
    	},
		{
			0, 0, 0,
			0, 0, 0,
			0, 0, 0,
			0, 0, 0,
			R.drawable.w, R.drawable.s,
		},
        {
			R.drawable.backside
        }
	};
    
    // the array of card images
    private static Bitmap[][] cardImages = null;
    
    /**
     * initializes the card images
     * 
     * @param activity
     * 		the current activity
     */
    public static void initImages(Activity activity) {
    	// if it's already initialized, then ignore
    	if (cardImages != null) return;
    	
    	// create the outer array
    	cardImages = new Bitmap[resIdx.length][];
    	
    	// loop through the resource-index array, creating a
    	// "parallel" array with the images themselves
    	for (int i = 0; i < resIdx.length; i++) {
    		// create an inner array
    		cardImages[i] = new Bitmap[resIdx[i].length];
			Log.i("Bitmap Length", String.valueOf(resIdx[i].length));
    		for (int j = 0; j < resIdx[i].length; j++) {
    			// create the bitmap from the corresponding image
    			// resource, and set the corresponding array element
    			cardImages[i][j] =
    					BitmapFactory.decodeResource(
    							activity.getResources(),
    							resIdx[i][j]);
    		}
    	}
    }

}
