package edu.up.cs301.phase10;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

import edu.up.cs301.card.Color;

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

		// set up custom hand for player 0 - for testing
		Deck myDeck = new Deck();
		Card wild = new Card(Rank.WILD, Color.Black);
		Card five = new Card(Rank.FIVE, Color.Green);
		Card eleven = new Card(Rank.ELEVEN, Color.Red);
		for(int i = 0; i < 3; i++) {
			myDeck.add(wild);
			myDeck.add(five);
			myDeck.add(eleven);
		}
		myDeck.add(wild);
		//state.hook(myDeck); //implement the custom hand

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
			//win message with player name
			return "The winner was Player " + playerNames[completed.get(0)]; // Integer.toString(completed.get(0));
			//win message with player number
			//return "The winner was Player " + Integer.toString(completed.get(0));
		}
		if(completed.size() > 1){
			int winner = -1;	//lower than any potential player number
			int winScore = 1000000000; //higher than any potential score
			for(int i = 0; i < completed.size(); i++){
				String playerNum = Integer.toString(completed.get(i));
				Log.i("Player completed phase", playerNum);
			}
			for(int i = 0; i < state.getNumberPlayers(); i++){
				if(completed.contains(i)){
					if(state.getScores()[i] < winScore) {
						winner = i;
						winScore = state.getScores()[i];
					}
				}
			}
			//Log.i("Bad input", Integer.toString(completed.get(winner)));
			String winName = playerNames[winner];
			//return message with input player name
			return winName +" won with "+Integer.toString(winScore)+" points!";
			//return message with player number
			//return "Player "+Integer.toString(completed.get(winner))+" won with "+Integer.toString(winScore)+" points!";
		}
		//if no player has won, return null
		return null;

		/*//basic win check - sends message when someone has made the final phase
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
			return "Player "+Integer.toString(winner)+" won with "+Integer.toString(winScore)+" points!";
		}
		//if no player has won, return null
		return null;*/
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
				int hitCount = 0;
				for (int i = 0; i < state.getHand(thisPlayerIdx).size(); i++) {
					for (int j = 0; j < state.getHand(thisPlayerIdx).size(); j++) {
						if (c.equals(state.getHand(thisPlayerIdx).peekAt(j))) {
							if(hitCount == 0) {
								state.getHand(thisPlayerIdx).removeCard(j);
								hitCount++;
							}
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
				int variety[] = new int[15]; //indicator of if a card is used in the phase need 1-14, ignore zero for readability
				for (int i = 0; i < variety.length; i++) {
					variety[i] = 0;            //initialized to zero
				}
				for (int i = 0; i < myCards.size(); i++) {
					int val = myCards.peekAt(i).getRank().value(1);
					Log.i("Incrementing variety at", Integer.toString(val));
					variety[val]++; //increment the variety at a specific location, based on card value
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
				}
				else if (count == 2 && variety[13] != 0) { //one type of card plus wildcards
					int wildsNeeded = 0;
					for(int i = 0; i <=12; i++){
						if(variety[i] != 0){
							wildsNeeded = 6 - variety[i]; //need to add to six, including wilds and 1 card rank
							if(wildsNeeded == variety[13]){
								shouldPass = true; //if have enough wild cards, valid phase
								Log.i("isValidPhase", "Legal");
							}
						}
					}
				}
				else if (count == 3 && variety[13] != 0) { //two types of cards plus wild cards
					int wildCardsNeeded = 0;
					for (int i  = 0; i <= 12; i++){ //only increment over value cards, not skip (14) or wild (13)
						if(variety[i] != 0){	//if there is a set or incomplete set - already determined to be 1 or 2 ranks only
							wildCardsNeeded = wildCardsNeeded + (3-variety[i]); //mark each incomplete set as needing a wildcard
						}
					}
					if (wildCardsNeeded == variety[13]){
						return true;	//if you have enough wild cards for the sets, it is a valid phase
					}
				} else if (count > 3) { //if there are more than three card values given
					return false;	//not valid phase 1 if more than 3 card types - ignoring wild card case
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
				myCards.sortNumerical();
				Card c = myCards.peekAt(0);
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == c.getRank().value(1)){
						comp0.add(myCards.peekAt(i));
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				if(comp0.size() == 6){				//if all have the same value, move 3 cards to other comp
					comp0.moveTopCardTo(comp1);		//all cards will have been placed in same comp. because they match
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
				}
				int realVal0 = (int) (Math.random()*12)+1;		//set value of set to random legal value
				int realVal1 = (int)( Math.random()*12)+1;		//set value of set to random legal value
				for(int i = 0; i < comp0.size(); i++){
					if(comp0.peekAt(i).getRank().value(1) != 13){	//find not wild cards
						realVal0 = comp0.peekAt(i).getRank().value(1); //that is set value
					}
				}
				for(int i = 0; i < comp1.size(); i++){
					if(comp1.peekAt(i).getRank().value(1) != 13){	//find not wild cards
						realVal1 = comp1.peekAt(i).getRank().value(1); //that is set value
					}
				}
				Log.i("RealVal0", Integer.toString(realVal0));
				Log.i("RealVal1", Integer.toString(realVal1));
				if(countCards[13] != 0){			//if there was a wildcard
					ArrayList<Card> myWilds = new ArrayList<Card>();
					for(int i = comp0.size()-1; i >= 0; i--){
						if(comp0.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = comp0.removeCard(i);
							myWilds.add(x);
						}
					}
					for(int i = comp1.size()-1; i >= 0; i--){
						if(comp1.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = comp1.removeCard(i);
							myWilds.add(x);
						}
					}
					Log.i("myswilds size", Integer.toString(myWilds.size()));
					int size = comp0.size();
					for(int i = 0; i < 3-size; i++){
						Card x = new Card(myWilds.remove(0));
						x.setWildValue(realVal0);
						comp0.add(x);
					}
					Log.i("myswilds new size", Integer.toString(myWilds.size()));
					size = comp1.size();
					for(int i = 0; i < 3-size; i++){
						Card x = new Card(myWilds.remove(0));
						x.setWildValue(realVal1);
						comp1.add(x);
					}
					Log.i("myswilds new size", Integer.toString(myWilds.size()));
					/*
					do{
						if(comp0.size() != 3){
							Card x = myWilds.remove(0);
							x.setWildValue(realVal0);
							comp0.add(x);
						}
					} while(comp0.size() != 3);
					do{
						if(comp1.size() != 3){
							Card x = myWilds.remove(0);
							x.setWildValue(realVal1);
							comp1.add(x);
						}
					} while(comp1.size() != 3); */
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
						else{
							comp1.add(myCards.peekAt(i)); //add to run if already have set of 3
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

		for (int i = 0; i < comp0.size(); i++){
			Log.i("PhaseComp0", comp0.peekAt(i).toString());
			Log.i("PhaseComp0", Integer.toString(i)+" "+comp0.peekAt(i).getWildValue());
		}
		for (int i = 0; i < comp1.size(); i++){
			Log.i("PhaseComp1", comp1.peekAt(i).toString());
			Log.i("PhaseComp1", Integer.toString(i)+" "+comp1.peekAt(i).getWildValue());
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

		Card myCard = new Card(myC);

		if(state.getPlayedPhase()[playerID][0].size() == 0){ //if the player has not yet made his own phase - hits are illegal
			return false;
		}
		if(myCard == null){
			return false;
		}

		if(state.getPhases()[playerToHit] == 8) { //on phase 8 color is the only thing that matters
			if(state.getPlayedPhase()[playerToHit][0].size() != 0){ //if that player has played a phase
				if(myCard.getRank().value(1) == 13){ //if a wildcard
					return true;					//wildcards are always valid for phase 8
				}
				Color myColor = null;
				for(int i = 0; i < state.getPlayedPhase()[playerToHit][0].size(); i++){
					if(state.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit() != Color.Black){ //for not wilds
						myColor = state.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit(); //set color to not black
					}
				}
				if(myColor == myCard.getSuit()){
					return true; //return true if colors match
					//this process will not work if all cards in a phase 8 component are wild. Very rare, fix later
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
					if(myDeck.peekAt(i).getWildValue() != myDeck.peekAt(i+1).getWildValue()){
						set = false;
						Log.i("IsValidHit", "Not a set");
					}
					else if(myDeck.peekAt(i).getWildValue() != (myDeck.peekAt(i+1).getWildValue()-1)){
						run = false;
						Log.i("IsValidHit", "Not a run");
					}
				}
				if(set){
					myCard.setWildValue(myDeck.peekAt(0).getWildValue());
					myC.setWildValue(myDeck.peekAt(0).getWildValue());
					Log.i("myCard Wild Val", Integer.toString(myCard.getWildValue()));
					Log.i("set Wild Val", Integer.toString(myDeck.peekAt(0).getWildValue()));
					if(myCard.getWildValue() == myDeck.peekAt(0).getWildValue()){
						return true;
					}
					else{
						return false;
					}
				}
				else if(run){
					if(myDeck.maxMin(true) == 12 && myDeck.maxMin(false) == 1){
						return false; //if run already has 1 to 12, cannot hit
					}
					else if(myDeck.maxMin(true) < 12){
						myCard.setWildValue(myDeck.maxMin(true)+1); //set to highest possible for now, later allow player to choose
					}
					else if(myDeck.maxMin(false) > 1){
						myCard.setWildValue(myDeck.maxMin(false)-1); //set to lowest possible for now, later allow player to choose
					}
					if(myCard.getWildValue() == (myDeck.maxMin(false)-1)){
						return true;
					}
					if(myCard.getWildValue() == (myDeck.maxMin(true))+1){
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
						int temp = state.getScores()[i] + 25;
						state.setScore(i, temp);    //wild cards
					}
					else if (c.getRank().value(1) == 14) {
						int temp = state.getScores()[i] + 15;
						state.setScore(i, temp);    //skip cards
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
		int variety[] = new int[15]; //indicator of if a card is used in the phase
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