package edu.up.cs301.phase10;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Phase10 game.  Sent by the game when
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
		Log.i("NUmberPlayers", Integer.toString(numPlayers));
		for(int i = 0; i < numPlayers; i++){
			Log.i("Hand Created for", Integer.toString(i));
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
		toPlay = 0; //(int)(numPlayers*Math.random());

		//initialize the draw pile
		drawPile.add108(); 			//fill the deck with cards
		drawPile.shuffle(); 		//shuffle the deck

		//Deal the cards to the players hands
		for(int i = 0; i < 10; i++){		//10 cards each

			int playerIDX = toPlay;
			for (int j = 0; j < numPlayers; j++){
				drawPile.moveTopCardTo(hands[playerIDX]);//deal first card to player starting the game

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
		//Log.i("Number Players", Integer.toString(numPlayers));

		// set index of player whose turn it is
		toPlay = orig.toPlay;

		//Indicate that game should start with a draw action
		shouldDraw = orig.shouldDraw;

		//Copy the decks shared by all (only sending the legal info for the player to know)
		drawPile = new Deck(); //player should not have access to any card in the deck
		discardPile = new Deck(orig.discardPile); //all cards in discard pile are face up

		//initialize the new deck for the hands
		hands = new Deck[numPlayers];
		//fill the hands deck with info from orig
		for(int i = 0; i < numPlayers; i++){
			hands[i] = new Deck(orig.hands[i]);
			if(i != playerID) {
				hands[i].nullifyDeck(); // only give player access to his/her own hand
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
     * Tells how many players there are
     *
     * @return the number of players in the game
     */
    public int getNumberPlayers() {
        return numPlayers;
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
    	Log.i("To Play is now", Integer.toString(idx));
		toPlay = idx;
    }

    /**
     * Returns the top card of the draw pile
     *
     * @return the top card from the draw pile. If no access to draw pile / empty pile, returns null
     */
    public Card getDrawCard() {
        if(drawPile.size() == 0){               //if the draw pile is empty
            reshuffle();
        }
        return drawPile.removeTopCard();
    }

    /**
     * Returns the top card of the discard pile
     *
     * @return the top card from the discard pile. If no access to discard pile / empty pile, returns null
     */
    public Card getDiscardCard() {
        return discardPile.removeTopCard();
    }

	/**
	 * shows the top card of the discard pile
	 *
	 * @return the top card from the discard pile. If no access to discard pile / empty pile, returns null
	 */
	public Card peekDiscardCard() {
		return discardPile.peekAtTopCard();
	}

    /**
     * 'Sets' the top card of the discard pile, by adding a card to it
     *
     * @param discard
     *      the card to add to the discard pile
     */
    public void setDiscardCard(Card discard) {
        discardPile.add(discard);
    }

    /**
     * Returns whether or not a draw action is expected next
     *
     * @return boolean indicator of what the next expected action is
     */
    public boolean getShouldDraw() {
        return shouldDraw;
    }

    /**
     * Sets the shouldDraw variable
     *
     * @param expectedDraw
     *      boolean to updated what the next expected move type is
     */
    public void setShouldDraw(boolean expectedDraw) {
        shouldDraw = expectedDraw;
    }

    /**
     * Returns the array listing what phase each player is on
     *
     * @return the array listing what phase each player is on
     */
    public int[] getPhases() {
        return phases;
    }

    /**
     * Sets a phase for an individual player
     *
     * @param playerID
     *      the ID of the player that is having his/her phase updated
     * @param myPhase
     *      the phase the player should be on
     */
    public void setPhase(int playerID, int myPhase) {
        phases[playerID] = myPhase;
    }

    /**
     * Returns the array listing what score each player has
     *
     * @return the array listing what score each player has
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * Sets the score for an individual player
     *
     * @param playerID
     *      the ID of the player that is having his/her phase updated
     * @param myScore
     *      the score to change that players score to
     */
    public void setScore(int playerID, int myScore) {
        scores[playerID] = myScore;
    }

    /**
     * Returns the array listing which players are set to be skipped
     *
     * @return the array listing which players are set to be skipped
     */
    public boolean[] getToSkip() {
        return toSkip;
    }

    /**
     * Sets the toSkip for an individual player
     *
     * @param playerID
     *      the ID of the player that is having his/her toSkip updated
     * @param mySkip
     *      the boolean indicating if that player should be skipped or not
     */
    public void setToSkip(int playerID, boolean mySkip) {
        toSkip[playerID] = mySkip;
    }

    /**
     * Returns the array listing which players have been skipped
     *
     * @return the array listing which players have been skipped
     */
    public boolean[] getAlreadySkip() {
        return alreadySkip;
    }

    /**
     * Sets the alreadySkip for an individual player
     *
     * @param playerID
     *      the ID of the player that is having his/her alreadySkip updated
     * @param mySkip
     *      the boolean indicating if that player has been skipped or not
     */
    public void setAlreadySkip(int playerID, boolean mySkip) {
        alreadySkip[playerID] = mySkip;
    }

    /**
     * Returns the hand for a specific player
     *
     * @param playerID
     * @return the hand for a specific player
     */
    public Deck getHand(int playerID) {
		if(hands == null){
			return null;
		}
		else {
            //Log.i("Hands is", "Valid");
			//Log.i("Player id is", Integer.toString(playerID));
			for(int i = 0; i < numPlayers; i++) {
				if(hands[i] != null){
					//Log.i("Valid hand at id", Integer.toString(i));
				}
				//Log.i("Hand size of player "+Integer.toString(i), Integer.toString(hands[i].size()));
			}
			//Log.i("Peek Card is", hands[playerID].peekAtTopCard().toString());
			return hands[playerID];
		}
    }

    /**
     * discards a card from a specific players hand to the discard pile
     *
     * @param playerID
     *      the ID of the player that is discarding
     * @param myCard
     *      the card he/she wants to discard
     */
    public void discardFromHand(int playerID, Card myCard) {
        if(hands[playerID].size() != 0){                            //if trying to remove a card from a valid hand (i.e. your own)
            for(int i = 0; i < hands[playerID].size(); i++){
				if(hands[playerID].peekAt(i) == myCard){
					Card temp = hands[playerID].removeCard(i);
					discardPile.add(temp);
				}
			}
        }
    }

    /**
     * Returns the played phases on the table
     *
     * @return the array with all of the played phases
     */
    public Deck[][] getPlayedPhase() {
        return playedPhase;
    }

    /*
     * Moves all cards, except top card, from discard pile back to draw pile, and shuffles
     */
    private void reshuffle(){
        Card top = discardPile.removeTopCard();
        for(int i = 0; i < discardPile.size(); i++){
            discardPile.moveTopCardTo(drawPile);
        }
        drawPile.shuffle();
        discardPile.add(top);                               //return the top card to the discard pile
    }

}
