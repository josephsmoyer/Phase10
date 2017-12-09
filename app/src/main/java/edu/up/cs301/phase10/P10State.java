package edu.up.cs301.phase10;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Color;
import edu.up.cs301.card.Rank;
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
	//Array ordering phase from highest to lowest
	private int[] phasePlace;
	//Array correlating player number to score
	private int[] scores;
	//Array ordering score from lowest to highest
	private int[] scorePlace;
	//Array correlating player number to if they have a skip pending
	private boolean[] toSkip;
	//Array correlating player number to if they have already been skipped
	private boolean[] alreadySkip;
	//Boolean telling player to choose someone to skip
	private boolean chooseSkip;

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
		phasePlace = new int[numPlayers];
		scores = new int[numPlayers];
		scorePlace = new int[numPlayers];
		toSkip = new boolean[numPlayers];
		alreadySkip = new boolean[numPlayers];
		chooseSkip = false;
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
			phasePlace[i] = i;						//start phase placement based off index
			scores[i] = 0;							//start all players with a score of zero
			scorePlace[i] = i;						//start score placement based off index
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
				drawPile.moveTopCardTo(hands[playerIDX]);//deal first card to player starting the game
				//Log.i("Dealing", drawPile.peekAtTopCard().getRank().toString());
				playerIDX++;								//increment the player to give cards to
				if(playerIDX >= numPlayers){
					playerIDX = 0;							//if reached last player, cycle back to player 0
				}
			}
		}

		//start the players as having a sorted hand
		for(int i = 0; i < numPlayers; i++){
			hands[i].sortNumerical();
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
		phasePlace = new int[numPlayers];
		scores = new int[numPlayers];
		scorePlace = new int[numPlayers];
		toSkip = new boolean[numPlayers];
		alreadySkip = new boolean[numPlayers];
		chooseSkip = orig.chooseSkip;
		//Copy the rest of player specific info, been skipped, score, etc...
		for(int i = 0; i < numPlayers; i++){
			phases[i] = orig.phases[i];
			phasePlace[i] = orig.phasePlace[i];
			scores[i] = orig.scores[i];
			scorePlace[i] = orig.scorePlace[i];
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
		chooseSkip = orig.chooseSkip;
		//Copy the rest of player specific info, been skipped, score, etc...
		for(int i = 0; i < numPlayers; i++){
			phases[i] = orig.phases[i];
			phasePlace[i] = orig.phasePlace[i];
			scores[i] = orig.scores[i];
			scorePlace[i] = orig.scorePlace[i];
			toSkip[i] = orig.toSkip[i];
			alreadySkip[i] = orig.alreadySkip[i];
		}
	}

	public void hook(){
		int playerToHook = 0;
		boolean hookHand = true;

		Deck trash = new Deck();						//clean hand
		if(hookHand) {
			hands[playerToHook].moveAllCardsTo(trash);
		}

		Deck myDeck = new Deck();						//customize hand
		Card one = new Card(Rank.ONE, Color.Green);
		Card two = new Card(Rank.TWO, Color.Green);
		Card three = new Card(Rank.THREE, Color.Green);
		Card four = new Card(Rank.FOUR, Color.Green);
		Card five = new Card(Rank.FIVE, Color.Green);
		Card six = new Card(Rank.SIX, Color.Green);
		Card seven = new Card(Rank.SEVEN, Color.Green);
		Card eight = new Card(Rank.EIGHT, Color.Green);
		Card nine = new Card(Rank.NINE, Color.Green);
		Card ten = new Card(Rank.TEN, Color.Green);
		Card eleven = new Card(Rank.ELEVEN, Color.Red);
		Card twelve = new Card(Rank.TWELVE, Color.Green);
		Card wild = new Card(Rank.WILD, Color.Black);
		Card skip = new Card(Rank.SKIP, Color.Black);
		for(int i = 0; i < 1; i++) {	//cards to add once
			//myDeck.add(one);
			myDeck.add(two);
			myDeck.add(three);
			myDeck.add(four);
			myDeck.add(five);
			myDeck.add(six);
			myDeck.add(seven);
			//myDeck.add(eight);
			//myDeck.add(nine);
			//myDeck.add(ten);
			//myDeck.add(eleven);
			//myDeck.add(twelve);
			myDeck.add(wild);
			//myDeck.add(skip);
		}
		for(int i = 0; i < 2; i++) {	//cards to add twice
			//myDeck.add(one);
			//myDeck.add(two);
			//myDeck.add(three);
			//myDeck.add(four);
			//myDeck.add(five);
			//myDeck.add(six);
			//myDeck.add(seven);
			//myDeck.add(eight);
			//myDeck.add(nine);
			//myDeck.add(ten);
			//myDeck.add(eleven);
			//myDeck.add(twelve);
			//myDeck.add(wild);
			//myDeck.add(skip);
		}
		for(int i = 0; i < 3; i++) {	//cards to add three times
			//myDeck.add(one);
			//myDeck.add(two);
			//myDeck.add(three);
			//myDeck.add(four);
			//myDeck.add(five);
			//myDeck.add(six);
			//myDeck.add(seven);
			//myDeck.add(eight);
			//myDeck.add(nine);
			//myDeck.add(ten);
			//myDeck.add(eleven);
			//myDeck.add(twelve);
			myDeck.add(wild);
			//myDeck.add(skip);
		}

		if(hookHand) {
			myDeck.moveAllCardsTo(hands[playerToHook]);                //send hand tp state area holding it

			Deck tempDeck = new Deck();
			tempDeck.add(wild);						 //put wild on discard pile if you wnat it
			//tempDeck.add(skip);						 //put a skip on discard pile for intiail skip testing
			tempDeck.moveTopCardTo(discardPile);
		}

		for(int i = 0; i < numPlayers; i++){
			phases[i] = 4;							//start all players on phase 1
			//scores[i] = 0;							//start all players with a score of zero
			//toSkip[i] = false;						//start no players with a skip pending
			//alreadySkip[i] = false;					//start no players marked as already been skipped
		}
		//phases[0] = 9;
	}

	public boolean getChooseSkip() { return chooseSkip; }

	public void setChooseSkip(boolean yon) {
		Log.i("ChooseSkip set", Boolean.toString(yon));
		chooseSkip = yon;
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
			boolean alreadyRemoved = false;
			for(int i = 0; i < hands[playerID].size(); i++){
				if(hands[playerID].peekAt(i).equals(myCard) && !alreadyRemoved){
					Card temp = hands[playerID].removeCard(i);
					discardPile.add(temp);
					alreadyRemoved = true;
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

	/**
	 * Returns the placings based on it's phase placement
	 *
	 * @return a placing array
	 */
	public int[] getPhasePlace() {
		return phasePlace;
	}

	/**
	 * Returns the placings based on it's score placement
	 *
	 * @return a placing array
	 */
	public int[] getScorePlace() {
		return scorePlace;
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

	/*
	 * Creates rankings/placements for all players
	 * To be displayed on stats page
	 */
	public void updatePlacements() {
		int holder;
		for(int i=0; i<numPlayers; i++) {
			for (int j=0; j<numPlayers-1;j++) {
				//If person at worse ranking has higher phase, switch places
				if (phases[phasePlace[j]] < phases[phasePlace[j + 1]]) {
					holder = phasePlace[j];
					phasePlace[j] = phasePlace[j + 1];
					phasePlace[j + 1] = holder;
				}
				//If person at worse ranking has lower score, switch places
				if (scores[scorePlace[j]] > scores[scorePlace[j + 1]]) {
					holder = scorePlace[j];
					scorePlace[j] = scorePlace[j + 1];
					scorePlace[j + 1] = holder;
				}
			}
		}
	}

	public void cleanDecks(){
		playedPhase = new Deck[numPlayers][2];
		for(int i = 0; i < numPlayers; i++){
			for(int j = 0; j < 2; j ++) {
				playedPhase[i][j] = new Deck();		//create a deck for each possible phase component (2 max per player)
			}
		}
		drawPile = new Deck();
		discardPile = new Deck();
		drawPile.add108();
		drawPile.shuffle();

		//Deal the cards to the players hands
		for(int i = 0; i < 10; i++){						//10 cards each
			int playerIDX = toPlay;
			for (int j = 0; j < numPlayers; j++){
				drawPile.moveTopCardTo(hands[playerIDX]);//deal first card to player starting the game
				//Log.i("Dealing", drawPile.peekAtTopCard().getRank().toString());
				playerIDX++;								//increment the player to give cards to
				if(playerIDX >= numPlayers){
					playerIDX = 0;							//if reached last player, cycle back to player 0
				}
			}
		}

		//sort the new hands
		for(int i = 0; i < numPlayers; i++){
			hands[i].sortNumerical();
		}

		drawPile.moveTopCardTo(discardPile);
	}

}
