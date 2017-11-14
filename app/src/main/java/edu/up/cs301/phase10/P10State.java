package edu.up.cs301.phase10;

import java.util.ArrayList;
import java.util.HashMap;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Slapjack game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 * 
 * @author Steven R. Vegdahl
 * @author Trenton Langer
 * @version November 2018
 */
public class P10State extends GameState
{
	private static final long serialVersionUID = -8269749892027578792L;

    ///////////////////////////////////////////////////
    // ************** instance variables ************
    ///////////////////////////////////////////////////

	//Indicates the number of players this game
	private int numPlayers;
	// Indicates which player's turn it is
	private int toPlay;
	//Deck object to hold the cards for the draw pile
	private Deck drawPile;
	//Deck object to hold the cards for the discard pile
	private Deck discardPile;
	//boolean to indicate if the next expected action is a draw or not
	private boolean shouldDraw;
	//Array correlating player number to phase
	private int[] phases;
	//Array correlating player number to score
	private int[] scores;
	//Array correlating player number to if they have a skip pending
	private boolean[] toSkip;
	//Array correlating player number to if they have already been skipped
	private boolean[] alreadySkip;

	//Array of deck objects, for each players hand
	private Deck[] hands;
	//Array of array of deck objects, for each players cards on the table
	private Deck[][] playedPhase;


    /**
     * Constructor for objects of class P10State. Initializes for the beginning of the
     * game, with a random player as the first to play
     *  
     */
    public P10State(int gameSize) {
		//Constructor must tell the state how many players to set up for
		numPlayers = gameSize;

		//initialize the instance variables to the proper sizes
		drawPile = new Deck();
		discardPile = new Deck();
		shouldDraw = true;
		phases = new int[numPlayers];
		scores = new int[numPlayers];
		toSkip = new boolean[numPlayers];
		alreadySkip = new boolean[numPlayers];
		hands = new Deck[numPlayers];
		for(int i = 0; i < numPlayers; i++){
			hands[i] = new Deck();					//create a deck for each players hand
		}
		playedPhase = new Deck[numPlayers][2];
		for(int i = 0; i < numPlayers; i++){
			for(int j = 0; j < 2; j ++) {
				playedPhase[i][j] = new Deck();		//create a deck for each possible phase component (2 max per player)
			}
		}

		//Initialize the rest of player specific info, been skipped, score, etc...
		for(int i = 0; i < numPlayers; i++){
			phases[i] = 1;							//start all players on phase 1
			scores[i] = 0;							//start all players with a score of zero
			toSkip[i] = false;						//start no players with a skip pending
			alreadySkip[i] = false;					//start no players marked as already been skipped
		}

		// randomly pick the player who starts
		toPlay = (int)(numPlayers*Math.random());

		//initialize the draw pile
		drawPile.add108(); 			//fill the deck with cards
		drawPile.shuffle(); 		//shuffle the deck

		//Deal the cards to the players hands
		for(int i = 0; i < 10; i++){						//10 cards each
			int playerIDX = toPlay;
			for (int j = 0; j < numPlayers; j++){
				drawPile.moveTopCardTo(hands[playerIDX]);	//deal first card to player starting the game
				playerIDX++;								//increment the player to give cards to
				if(playerIDX >= numPlayers){
					playerIDX = 0;							//if reached last player, cycle back to player 0
				}
			}
		}

		//After Dealing, move top card from draw pile to be face up in discard pile
		drawPile.moveTopCardTo(discardPile);

		//Indicate that game should start with a draw action
		shouldDraw = true;
    }
    
    /**
     * Copy constructor for objects of class P10State. Makes a copy of the given state
     *  
     * @param orig  the state to be copied
     */
    public P10State(P10State orig) {
		//copy number of players
		numPlayers = orig.numPlayers;

		// set index of player whose turn it is
		toPlay = orig.toPlay;

		//Indicate that game should start with a draw action
		shouldDraw = orig.shouldDraw;

		//Copy the decks shared by all
		drawPile = new Deck(orig.drawPile);
		discardPile = new Deck(orig.discardPile);

		//initialize the new deck for the hands
		hands = new Deck[numPlayers];
		//fill the hands deck with info from orig
		for(int i = 0; i < numPlayers; i++){
			hands[i] = new Deck(orig.hands[i]);
		}
		//initialize the new decks for phase components
		playedPhase = new Deck[numPlayers][2];
		//fill the phase components with info from orig
		for(int i = 0; i < numPlayers; i++){
			for(int j = 0; j < 2; j ++) {
				playedPhase[i][j] = new Deck(orig.playedPhase[i][j]);
			}
		}

		phases = new int[numPlayers];
		scores = new int[numPlayers];
		toSkip = new boolean[numPlayers];
		alreadySkip = new boolean[numPlayers];
		//Copy the rest of player specific info, been skipped, score, etc...
		for(int i = 0; i < numPlayers; i++){
			phases[i] = orig.phases[i];
			scores[i] = orig.scores[i];
			toSkip[i] = orig.toSkip[i];
			alreadySkip[i] = orig.alreadySkip[i];
		}
    }

	/**
	 * Copy constructor for objects of class P10State. Makes a copy of the given state, specific to a certain player
	 *
	 * @param orig  the state to be copied
	 * @param playerID the player this state copy will be for
	 */
	public P10State(P10State orig, int playerID) {
		//copy number of players
		numPlayers = orig.numPlayers;

		// set index of player whose turn it is
		toPlay = orig.toPlay;

		//Indicate that game should start with a draw action
		shouldDraw = orig.shouldDraw;

		//Copy the decks shared by all
		drawPile = new Deck(orig.drawPile);
		discardPile = new Deck(orig.discardPile);

		//initialize the new deck for the hands
		hands = new Deck[numPlayers];
		//fill the hands deck with info from orig
		for(int i = 0; i < numPlayers; i++){
			if(i == playerID) {
				hands[i] = new Deck(orig.hands[i]);
			}
			else{
				hands[i] = null; // only give player access to his own hand
			}
		}
		//initialize the new decks for phase components
		playedPhase = new Deck[numPlayers][2];
		//fill the phase components with info from orig
		for(int i = 0; i < numPlayers; i++){
			for(int j = 0; j < 2; j ++) {
				playedPhase[i][j] = new Deck(orig.playedPhase[i][j]);
			}
		}

		phases = new int[numPlayers];
		scores = new int[numPlayers];
		toSkip = new boolean[numPlayers];
		alreadySkip = new boolean[numPlayers];
		//Copy the rest of player specific info, been skipped, score, etc...
		for(int i = 0; i < numPlayers; i++){
			phases[i] = orig.phases[i];
			scores[i] = orig.scores[i];
			toSkip[i] = orig.toSkip[i];
			alreadySkip[i] = orig.alreadySkip[i];
		}
	}

    /**
     * Gives the given deck.
     * 
     * @return  the deck for the given player, or the middle deck if the
     *   index is 2
     */
    public Deck getDeck(int num) {
        if (num < 0 || num > 2) return null;
        return piles[num];
    }
    
    /**
     * Tells which player's turn it is.
     * 
     * @return the index (0 to 5) of the player whose turn it is.
     */
    public int getToPlay() {
        return toPlay;
    }
    
    /**
     * change whose move it is
     * 
     * @param idx
     * 		the index of the player whose move it now is
     */
    public void setToPlay(int idx) {
    	toPlay = idx;
    }
 
    /**
     * Replaces all cards with null, except for the top card of deck 2
     */
    public void nullAllButTopOf2() {
    	// see if the middle deck is empty; remove top card from middle deck
    	boolean empty2 = piles[2].size() == 0;
    	Card c = piles[2].removeTopCard();
    	
    	// set all cards in deck to null
    	for (Deck d : piles) {
    		d.nullifyDeck();
    	}
    	
    	// if middle deck had not been empty, add back the top (non-null) card
    	if (!empty2) {
    		piles[2].add(c);
    	}
    }

    /*
    //SlapJack Code for Reference

		///////////////////////////////////////////////////
		// ************** instance variables ************
		///////////////////////////////////////////////////

		// the three piles of cards:
		//  - 0: pile for player 0
		//  - 1: pile for player 1
		//  - 2: the "up" pile, where the top card
		// Note that when players receive the state, all but the top card in all piles
		// are passed as null.
		private Deck[] piles;
		private int turn;
		private boolean drawMade;
		private ArrayList<Card> drawPile;
		private ArrayList<Card> discardPile;

		private HashMap<String, Integer> playerScores;
		private ArrayList<Card>playerHand;


		// whose turn is it to turn a card?
		private int toPlay;


		//@return  the deck specified
		public Deck getDeck(int num) {
			if (num < 0 || num > 2) return null;
			return piles[num];
		}

		//Tells which player's turn it is.
		public int toPlay() {
			return toPlay;
		}

		//change whose move it is
		//@param idx the index of the player whose move it now is

		public void setToPlay(int idx) {
			toPlay = idx;
		}


		//Replaces all cards with null, except for the top card of deck 2
		public void nullAllButTopOf2() {
			// see if the middle deck is empty; remove top card from middle deck
			boolean empty2 = piles[2].size() == 0;
			Card c = piles[2].removeTopCard();

			// set all cards in deck to null
			for (Deck d : piles) {
				d.nullifyDeck();
			}

			// if middle deck had not been empty, add back the top (non-null) card
			if (!empty2) {
				piles[2].add(c);
			}
		}
     */
}
