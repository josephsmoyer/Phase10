package edu.up.cs301.phase10;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * The LocalGame class for a slapjack game.  Defines and enforces
 * the game rules; handles interactions between players.
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */

public class P10LocalGame extends LocalGame {

	// the game's state
	P10State state;

	Context myContext;

	/**
	 * Constructor for the P10LocalGame.
	 */
	public P10LocalGame(int number) {
		Log.i("P10LocalGame", "creating game");
		// create the state for the beginning of the game
		state = new P10State(number);
		String myStateStr = Integer.toString(state.getHand(1).size());
		Log.i("State Check", myStateStr); //should have 10 cards in the initialized hand
	}
	/**
	 * Constructor for the P10LocalGame.
	 */
	public P10LocalGame(int number, Context context) {
		Log.i("P10LocalGame", "creating game");
		// create the state for the beginning of the game
		state = new P10State(number);
		String myStateStr = Integer.toString(state.getHand(1).size());
		Log.i("State Check", myStateStr); //should have 10 cards in the initialized hand
		myContext = context;
	}


	/**
	 * checks whether the game is over; if so, returns a string giving the result
	 *
	 * @result the end-of-game message, or null if the game is not over
	 */
	@Override
	protected String checkIfGameOver() {
		//basic win check - sends message when someone has made the final phase
		ArrayList<Integer> completed = new ArrayList<Integer>();
		for (int i = 0; i < state.getNumberPlayers(); i++) {
			if (state.getPhases()[i] == 3) {	//change to 10 for full game play
				completed.add(i);
			}
		}
		if(completed.size() == 1){
			return "The winner was Player " + Integer.toString(completed.get(0));
		}
		if(completed.size() > 1){
			int winner = -1;
			int winScore = -1;
			for(int i = 0; i < state.getNumberPlayers(); i++){
				if(completed.contains(i)){
					if(state.getScores()[i] < winScore) {
						winner = i;
						winScore = state.getScores()[i];
					}
				}
			}
			return "Player "+Integer.toString(completed.get(winner))+" won with "+Integer.toString(winScore)+" points!";
		}
		//if no player has won, return null
		return null;
	}

	/**
	 * sends the updated state to the given player. In our case, we need to
	 * make a copy of the Deck, and null out all the cards except the top card
	 * in the middle deck, since that's the only one they can "see"
	 *
	 * @param p the player to which the state is to be sent
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {

		// if there is no state to send, ignore
		if (state == null) {
			return;
		}

		// make a copy of the state; null out all cards except for what the player should see
		P10State stateForPlayer = new P10State(state); //, getPlayerIdx(p)); // copy of state, obscuring some information

		// send the modified copy of the state to the player
		p.sendInfo(stateForPlayer);

	}

	/**
	 * whether a player is allowed to move
	 *
	 * @param playerIdx the player-number of the player in question
	 */
	protected boolean canMove(int playerIdx) {

		if (playerIdx < 0 || playerIdx > 5) {
			// if our player-number is out of range, return false
			return false;
		} else {
			// player can move if it's their turn
			return state.getToPlay() == playerIdx;
		}
	}

	/**
	 * makes a move on behalf of a player
	 *
	 * @param action the action denoting the move to be made
	 * @return true if the move was legal; false otherwise
	 */
	@Override
	protected boolean makeMove(GameAction action) {

		// check that we have phase 10action; if so cast it
		if (!(action instanceof P10MoveAction)) {
			return false;
		}
		P10MoveAction P10ma = (P10MoveAction) action;

		for(int i = 0; i < state.getNumberPlayers(); i++){
			for(int j = 0; j < 2; j++){
				if(state.getPlayedPhase()[i][j].size() != 0){
					state.getPlayedPhase()[i][j].sortNumerical();
				}
			}
		}

		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(P10ma.getPlayer());

		if (thisPlayerIdx < 0 || thisPlayerIdx > 5) { // illegal player
			return false;
		}
		if (!canMove(thisPlayerIdx)) {    //not their turn
			return false;
		}

		if (P10ma.isMakePhase()) {
			//if it is supposed to be a draw action
			if (state.getShouldDraw()) {
				return false;
			}
			// if we have a make phase
			P10MakePhaseAction myAction = (P10MakePhaseAction) P10ma;
			if (isValidPhase(thisPlayerIdx, myAction.getPhase())) {
				if (state.getPlayedPhase()[thisPlayerIdx][1].size() != 0) {
					return false;
				} //if the player has already made a phase
				Deck phaseComp0 = getPhaseComp(0, thisPlayerIdx, myAction.getPhase());
				Deck phaseComp1 = getPhaseComp(1, thisPlayerIdx, myAction.getPhase());
				for (int i = 0; i < phaseComp0.size(); i++) {
					Card c = phaseComp0.peekAt(i);
					boolean foundC = false;
					for(int j = 0; j < state.getHand(thisPlayerIdx).size(); j++){
						if(c.equals(state.getHand(thisPlayerIdx).peekAt(j)) && !foundC){
							state.getHand(thisPlayerIdx).removeCard(j);
							foundC = true;
						}
					}
					state.getPlayedPhase()[thisPlayerIdx][0].add(c);
				}
				for (int i = 0; i < phaseComp1.size(); i++) {
					Card c = phaseComp1.peekAt(i);
					boolean foundC = false;
					for(int j = 0; j < state.getHand(thisPlayerIdx).size(); j++){
						if(c.equals(state.getHand(thisPlayerIdx).peekAt(j)) && !foundC){
							state.getHand(thisPlayerIdx).removeCard(j);
							foundC = true;
						}
					}
					state.getPlayedPhase()[thisPlayerIdx][1].add(c);
				}
			}
			else {
				return false;
			}
			return true;
		} else if (P10ma.isPlay()) { // we have a "play" action
			//What is this action? We only need make phase, draw, discard, and hit
		} else if (P10ma.isHitCard()) {
			//if it is supposed to be a draw action
			if (state.getShouldDraw()) {
				return false;
			}
			//if we have a hit card action
			P10HitCardAction myAction = (P10HitCardAction) P10ma;
			if(isValidHit(thisPlayerIdx, myAction.getHitCard(), myAction.getPlayerToHit(), myAction.getPhaseToHit())) {
				Card c = myAction.getHitCard();
				for (int i = 0; i < state.getHand(thisPlayerIdx).size(); i++) {
					for (int j = 0; j < state.getHand(thisPlayerIdx).size(); j++) {
						if (c.equals(state.getHand(thisPlayerIdx).peekAt(j))) {
							state.getHand(thisPlayerIdx).removeCard(j);
						}
					}
				}
				state.getPlayedPhase()[myAction.getPlayerToHit()][myAction.getPhaseToHit()].add(c);
			}
		} else if (P10ma.isDrawCard()) {
			//if it is not supposed to be a draw action
			if (!state.getShouldDraw()) {
				return false;
			}
			//if we have a draw card action, at the proper time
			P10DrawCardAction myAction = (P10DrawCardAction) P10ma;
			//determine which pile should be drawn from
			if (myAction.drawPile) {
				Card c = state.getDrawCard();
				state.getHand(thisPlayerIdx).add(c);
			} else {
				Card c = state.getDiscardCard();
				state.getHand(thisPlayerIdx).add(c);
			}
			//after a successful draw, the next move will not be a draw
			state.setShouldDraw(false);
		} else if (P10ma.isDiscardCard()) {
			//if it is supposed to be a draw action
			if (state.getShouldDraw()) {
				return false;
			}
			//if we have a discard card action
			P10DiscardCardAction myAction = (P10DiscardCardAction) P10ma;
			Card c = myAction.toDiscard;
			state.discardFromHand(thisPlayerIdx, c);
			//after discarding, the next action should be a draw
			state.setShouldDraw(true);
			Log.i("Turn is player", Integer.toString(thisPlayerIdx));
			int nextIDX = thisPlayerIdx + 1; //increment players whose turn it is
			if (nextIDX >= state.getNumberPlayers()) {
				nextIDX = 0;                //if it was the last players turn, reset to player zero
			}
			Log.i("Turn is player", Integer.toString(nextIDX));
			state.setToPlay(nextIDX);        //update\
		} else { // some unexpected action
			return false;
		}

		if(checkIfRoundOver()){
			roundAdjustment();
			Toast.makeText(myContext, "Round Ended - Player ran out of cards", Toast.LENGTH_LONG).show();
		}
		// return true, because the move was successful if we get here
		return true;
	}

	/**
	 * helper method that determines if a phase component is valid
	 *
	 * @param playerNumber the player giving the cards
	 * @param myCards      the deck making up the suppossed phase
	 */
	private boolean isValidPhase(int playerNumber, Deck myCards) {
		int[] phases = state.getPhases();
		int phaseNum = phases[playerNumber];

		int[] countCards = cardsCount(myCards);

		switch(phaseNum) {
			case 1:
				if (myCards.size() != 6) { //both components of phase 1 are 3 cards
					Log.i("Amount of Cards given", Integer.toString(myCards.size()));
					return false;
				}
				int variety[] = new int[14]; //indicator of if a card is used in the phase
				for (int i = 0; i < variety.length; i++) {
					variety[i] = 0;            //initialized to zero
				}
				for (int i = 0; i < myCards.size(); i++) {
					int val = myCards.peekAt(i).getRank().value(1);
					Log.i("Incrementing variety at", Integer.toString(val));
					variety[val]++; //increment the variety at a specific location
				}
				int count = 0;
				boolean shouldPass = true;
				for (int i = 0; i < variety.length; i++) {
					if (variety[i] != 0) {
						count++;
					}
					if (variety[i] != 3 && variety[i] != 0) {    //if there are not 3 of each variety
						shouldPass = false;
					}
				}
				Log.i("Count of variety", Integer.toString(count));
				if (count == 1) { //all 6 of the same card
					return true;
				} else if (count != 2) {
					return false;
				}
				return shouldPass;
			case 2:
				boolean comp1 = false;
				boolean comp2 = false;
				if(myCards.size() != 7){
					return false;
				}
				int groups = 0;
				for(int i = 0; i < countCards.length; i++){
					if(countCards[i] >= 3){	//need at least one set of three cards
						comp1 = true;
					}
					if(countCards[i] > 1){
						groups++;
					}
				}
				if(groups != 1){
					return false;
				}
				for(int i = 0; i < countCards.length-3; i++){
					if(countCards[i] >= 1 && countCards[i+1] >= 1 && countCards[i+2] >= 1 && countCards[i+3] >= 1){	//need at least one run of 4 cards
						comp2 = true;
					}
				}
				if(comp1 && comp2){
					return true;
				}
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				break;
		}
		return false;
	}

	/**
	 * Returns a subset of cards from the players hand that consist of a valid phase component
	 * If no valid component exists, returns an empty deck
	 *
	 * @param myCards
	 * 		the player's "phase"
	 * @param playerNumber
	 *      the player number for the requesting player
	 * @param phaseComp
	 * 		which component of the phase
	 */
	protected Deck getPhaseComp(int phaseComp, int playerNumber, Deck myCards){
		Deck toReturn;

		int myPhaseNumber = state.getPhases()[playerNumber];
		Deck comp0 = new Deck();
		Deck comp1 = new Deck();

		int[] countCards = cardsCount(myCards);

		switch(myPhaseNumber){
			case 1:
				Card c = myCards.peekAt(0);
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank() == c.getRank()){
						comp0.add(myCards.peekAt(i));
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				if(comp0.size() == 6){				//if all have the same value, move 3 cards to other comp
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
				}
				break;
			case 2:
				int valueSet = -1;
				for(int i = 0; i < countCards.length; i++){
					if(countCards[i] >= 3){
						valueSet = i;
					}
				}
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == valueSet){
						if(comp0.size()< 3) {
							comp0.add(myCards.peekAt(i));
						}
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				break;

		}

		if(phaseComp == 0){
			toReturn = new Deck(comp0);
		}
		else{
			toReturn = new Deck (comp1);
		}
		return toReturn;
	}

	private boolean isValidHit(int playerID, Card myC, int playerToHit, int phaseToHit){
		//return true; //always assume valid hit for now

		Card myCard = new Card(myC.getRank(), myC.getSuit());

		if(state.getPlayedPhase()[playerID][0].size() == 0){ //if the player has not yet made his own phase - hits are illegal
			return false;
		}
		if(myCard == null){
			return false;
		}

		if(state.getPhases()[playerToHit] == 8) { //on phase 8 color is the only thing that matters
			if(state.getPlayedPhase()[playerToHit][0].size() != 0){ //if that player has played a phase
				if(state.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit() == myCard.getSuit()){
					return true; //return true if colors match
				}
			}
			return false; //otherwise return false if trying to hit on someones phase 8
		}
		else { //if trying to hit on any phase except phase 8
			Deck myDeck = null;
			if(phaseToHit < 2 && phaseToHit >= 0){
				myDeck = state.getPlayedPhase()[playerToHit][phaseToHit];
				myDeck.sortNumerical();
				boolean set = true;
				boolean run = true;
				for(int i = 0; i < myDeck.size()-1; i++){
					if(myDeck.peekAt(i).getRank().value(1) != myDeck.peekAt(i+1).getRank().value(1)){
						set = false;
					}
					else if(myDeck.peekAt(i).getRank().value(1) != (myDeck.peekAt(i+1).getRank().value(1)-1)){
						run = false;
					}
				}
				if(set){
					if(myCard.getRank().value(1) == myDeck.peekAt(0).getRank().value(1)){
						return true;
					}
					else{
						return false;
					}
				}
				else if(run){
					if(myCard.getRank().value(1) == (myDeck.maxMin(false)-1)){
						return true;
					}
					if(myCard.getRank().value(1) == (myDeck.maxMin(true))+1){
						return true;
					}
					else{
						return false;
					}
				}
			}
			else{
				return false;	//maximum two phase components - return false for bad input
			}
		}
		return false;

	}

	/*
	 * Checks if the round has ended
	 */
	protected boolean checkIfRoundOver(){
		for(int i = 0; i < state.getNumberPlayers(); i++){
			if(state.getHand(i).size() == 0){
				return true;
			}
		}
		return false;
	}

	/*
	 * Code to run to reset state, when the round ends
	 */
	protected void roundAdjustment(){
		for(int i = 0; i < state.getNumberPlayers(); i++){
			Card c = null;
			//Empty the hands for each player, and score
			do{
				if(state.getHand(i).size() != 0) {
					c = state.getHand(i).removeTopCard();
					if (c.getRank().value(1) < 10) {
						int temp = state.getScores()[i] + 5;
						state.setScore(i, temp); //low rank cards
					}
					else if (c.getRank().value(1) == 13) {
						int temp = state.getScores()[i] + 15;
						state.setScore(i, temp);    //skip cards
					}
					else if (c.getRank().value(1) == 14) {
						int temp = state.getScores()[i] + 25;
						state.setScore(i, temp);    //wild cards
					}
					else {
					int temp = state.getScores()[i] + 10;
					state.setScore(i, temp); //high rank cards
					}
				}
			} while (state.getHand(i).size() != 0);
			//Update Phase information
			if(state.getPlayedPhase()[i][0].size() != 0){
				int playerPhase = state.getPhases()[i];
				state.setPhase(i, playerPhase+1);
			}
			//empty the played phase locations
			for(int j = 0; j < 2; j++){
				for(int k = 0; k < state.getPlayedPhase()[i][j].size(); k++) {
					state.getPlayedPhase()[i][j].removeTopCard();
				}
			}
			//reset skip information
			state.setToSkip(i, false);
			state.setAlreadySkip(i, false);
		}
		//Reset "the Dealer" to be player 0
		state.setToPlay(0);
		//Start with a draw action
		state.setShouldDraw(true);
		//empty the discard/draw piles & reDeal cards to the players & put a card from draw to start discard
		state.cleanDecks();
	}

	/*
	 * Returns the count of how many of each rank of cards there are
	 */
	protected int[] cardsCount(Deck myCards){
		int variety[] = new int[14]; //indicator of if a card is used in the phase
		for(int i = 0; i < variety.length; i++){
			variety[i] = 0;			//initialized to zero
		}
		for(int i = 0; i < myCards.size(); i++){
			int val = myCards.peekAt(i).getRank().value(1);
			Log.i("Incrementing variety at", Integer.toString(val));
			variety[val]++; //increment the variety at a specific location
		}
		return variety;
	}
}