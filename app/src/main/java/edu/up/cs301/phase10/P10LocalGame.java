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
 * The LocalGame class for a Phase 10 game.  Defines and enforces
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
		//Log.i("P10LocalGame", "creating game");
		// create the state for the beginning of the game
		state = new P10State(number);
		String myStateStr = Integer.toString(state.getHand(1).size());
		//Log.i("State Check", myStateStr); //should have 10 cards in the initialized hand
	}
	/**
	 * Constructor for the P10LocalGame.
	 */
	public P10LocalGame(int number, Context context) {
		myContext = context;
		//Log.i("P10LocalGame", "creating game");
		// create the state for the beginning of the game
		state = new P10State(number);
		String myStateStr = Integer.toString(state.getHand(1).size());
		//Log.i("State Check", myStateStr); //should have 10 cards in the initialized hand

		// set up custom hand for player 0 - for testing
		state.hook(); //implement the custom state

		if(state.getDiscardCard().getWildValue() == 14){ //if flips a wild for first card
			state.setAlreadySkip(state.getToPlay(), true);	//mark first player as being skipped
			int nextIDX = (state.getToPlay() + 1) % (state.getNumberPlayers()); //increment players whose turn it is
			state.setToPlay(nextIDX);
			Toast.makeText(myContext, "First Player skipped, because of skip in discard Pile", Toast.LENGTH_SHORT).show();
		}
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
			if (state.getPhases()[i] == 11) {	//change to 10 for full game play
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

			for(int i = 0; i < players.length; i++){
				state.getHand(i).nullifyDeck();
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
				phaseComp0.sortNumerical();
				Deck phaseComp1 = getPhaseComp(1, thisPlayerIdx, myAction.getPhase());
				phaseComp1.sortNumerical();
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
				Card c = new Card(myAction.getHitCard());
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
				int phaseToHit = myAction.getPhaseToHit();
				if (state.getPhases()[myAction.getPlayerToHit()] > 3 && state.getPhases()[myAction.getPlayerToHit()] < 7) {//phases 4, 5, 6 being hit on
					phaseToHit = 0; //only one phase comp to hit on
				}
				if (state.getPhases()[myAction.getPlayerToHit()] == 8) {//phases 8 being hit on
					phaseToHit = 0; //only one phase comp to hit on
				}
				state.getPlayedPhase()[myAction.getPlayerToHit()][phaseToHit].add(c);
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
				Card c = new Card(state.getDrawCard());
				state.getHand(thisPlayerIdx).add(c);
			} else {
				Card c = new Card(state.peekDiscardCard());
				//if the discard pile contains a skip card, do not pick up
				if (c.getWildValue() == 14) {
					return false;
				}
				else {
					c = new Card(state.getDiscardCard());
					state.getHand(thisPlayerIdx).add(c);
				}
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
			Card c = new Card(myAction.toDiscard);
			state.discardFromHand(thisPlayerIdx, c);
			//if discarded card is a skip card trigger a SkipPlayerAction
			if (c.getRank().value(1) == 14) {
				for (int i=0; i < state.getNumberPlayers(); i++) {
					//If any player (excluding the discarder) has not been skipped, trigger a SkipPlayerAction
					if (!state.getToSkip()[i] && !state.getAlreadySkip()[i]) {
						if (i != thisPlayerIdx) {
							state.setChooseSkip(true);
						}
					}
				}
			}
			//If a SkipPlayerAction was not triggered, move play to next player
			if (!state.getChooseSkip()) {
				//after discarding, the next action should be a draw
				state.setShouldDraw(true);
				//Log.i("Turn is player", Integer.toString(thisPlayerIdx));
				int nextIDX = (thisPlayerIdx + 1) % (state.getNumberPlayers()); //increment players whose turn it is
				while (state.getToSkip()[nextIDX]) {
					state.setToSkip(nextIDX, false);
					state.setAlreadySkip(nextIDX, true);
					nextIDX = (nextIDX + 1) % (state.getNumberPlayers());
				}
				Log.i("Turn is player", Integer.toString(nextIDX));
				state.setToPlay(nextIDX);        //update
			}
		} else if (P10ma.isSkipPlayer()) {
			P10SkipPlayerAction myAction = (P10SkipPlayerAction) P10ma;
			int playerToSkip = myAction.getPlayerID();
			//If player can be skipped
			String PlayerBeingSkipped = playerNames[playerToSkip];
			if (!state.getAlreadySkip()[playerToSkip]) {
				Log.i("Skipping Player", Integer.toString(playerToSkip));
				Toast.makeText(myContext, "Skip played on "+PlayerBeingSkipped, Toast.LENGTH_SHORT).show();
				//Skip player
				state.setToSkip(playerToSkip, true);
				state.setChooseSkip(false);
				//move play to next player
				state.setShouldDraw(true);
				//Log.i("Turn is player", Integer.toString(thisPlayerIdx));
				int nextIDX = (thisPlayerIdx + 1) % (state.getNumberPlayers()); //increment players whose turn it is
				while (state.getToSkip()[nextIDX]) {
					state.setToSkip(nextIDX, false);
					state.setAlreadySkip(nextIDX, true);
					nextIDX = (nextIDX + 1) % (state.getNumberPlayers());
				}
				Log.i("Turn is player", Integer.toString(nextIDX));
				state.setToPlay(nextIDX);        //update
			}
			//If player can NOT be skipped
			else {
				Log.i("Cannot skip player", Integer.toString(playerToSkip));
				Toast.makeText(myContext, PlayerBeingSkipped+" has already been skipped", Toast.LENGTH_SHORT).show();
			}
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
					//Log.i("Amount of Cards given", Integer.toString(myCards.size()));
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
				//Log.i("Count of variety", Integer.toString(count));
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
								//Log.i("isValidPhase", "Legal");
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
				int usedWilds = 0;
				if(myCards.size() != 7){
					return false;
				}
				int groups = 0;
				for(int i = 0; i < countCards.length-2; i++){ //minus 2 to ignore wild/skip
					if(countCards[i] >= 3){	//need at least one set of three cards
						comp1 = true;
					}
					if(countCards[i] > 1){
						groups++;
					}
				}
				if(!comp1) {		//if didnt find a real set, look for set with wildcards
					//Log.i("No Real Set", "sdf");
					for (int i = 0; i < countCards.length - 2; i++) { //minus 2 to ignore wild/skip
						if (countCards[i] >= 2 ) { //if a pair
							//Log.i("pair, #wilds, #used", Integer.toString(i)+" ,"+Integer.toString(countCards[13])+" ,"+Integer.toString(usedWilds));
							if(countCards[13] - usedWilds > 0) {	//if enough wilds left
								//Log.i("In LOOOP", "yes");
								comp1 = true;
								usedWilds++;					//use a wild
							}
						}
					}
					if((!comp1) && countCards[13]-usedWilds >1){			//if still no set with a pair
						usedWilds = usedWilds+2;				//will need two wilds to make a set with a single card
						comp1 = true;
					}
				}
				if(groups != 1 && countCards[13] == 0){	//must allow only one type of card to be included multiple times if no wilds
					//Log.i("break 1", "logic test");
					return false;
				}
				else if(groups > 2 && countCards[13] != 0){	//if wilds, wilds and one other group can be multiple for phase to be valid
					//Log.i("groups, numWild", Integer.toString(groups)+" ,"+Integer.toString(countCards[13]));
					return false;
				}
				for(int i = 0; i < countCards.length-3-2; i++){//minus 3 for run of 4, minus 2 for ignore wild/skip
					if(countCards[i] >= 1 && countCards[i+1] >= 1 && countCards[i+2] >= 1 && countCards[i+3] >= 1){	//need at least one run of 4 cards
						comp2 = true;
					}

					int runPieces = 0;
					if(countCards[i] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					runPieces = runPieces + countCards[13] - usedWilds; //if 4 runpieces, including unused wilds and actual cards
					//Log.i("RunPieces", Integer.toString(runPieces));
					if(runPieces>=4){
						comp2 = true;
					}

				}
				if(comp1 && comp2){
					return true;
				}
				break;
			case 3:
				comp1 = false;
				comp2 = false;
				usedWilds = 0;
				if(myCards.size() != 8){
					return false;
				}
				groups = 0;
				for(int i = 0; i < countCards.length-2; i++){ //minus 2 to ignore wild/skip
					if(countCards[i] >= 4){	//need at least one set of four cards
						comp1 = true;
					}
					if(countCards[i] > 1){
						groups++;
					}
				}
				if(!comp1) {		//if didnt find a real set, look for set with wildcards
					//Log.i("No Real Set", "sdf");
					for (int i = 0; i < countCards.length - 2; i++) { //minus 2 to ignore wild/skip
						if (countCards[i] >= 3 ) { //if a triple
							//Log.i("pair, #wilds, #used", Integer.toString(i)+" ,"+Integer.toString(countCards[13])+" ,"+Integer.toString(usedWilds));
							if(countCards[13] - usedWilds > 0) {	//if enough wilds left
								//Log.i("In LOOOP", "yes");
								comp1 = true;
								usedWilds++;					//use a wild
							}
						}
					}
					if((!comp1) && countCards[13]-usedWilds >1){			//if still no set with a triple
						for (int i = 0; i < countCards.length - 2; i++) { //minus 2 to ignore wild/skip
							if (countCards[i] >= 2 ) { //if a pair
								//Log.i("pair, #wilds, #used", Integer.toString(i)+" ,"+Integer.toString(countCards[13])+" ,"+Integer.toString(usedWilds));
								if(countCards[13] - usedWilds > 0) {	//if enough wilds left
									//Log.i("In LOOOP", "yes");
									comp1 = true;
									usedWilds = usedWilds+2;					//use two wild
								}
							}
						}
						if((!comp1) && countCards[13]-usedWilds >1){			//if still no set with a pair
							usedWilds = usedWilds+3;				//will need three wilds to make a set with a single card
							comp1 = true;
						}
					}
				}
				if(groups != 1 && countCards[13] == 0){	//must allow only one type of card to be included multiple times if no wilds
					//Log.i("break 1", "logic test");
					return false;
				}
				else if(groups > 2 && countCards[13] != 0){	//if wilds, wilds and one other group can be multiple for phase to be valid
					//Log.i("groups, numWild", Integer.toString(groups)+" ,"+Integer.toString(countCards[13]));
					return false;
				}
				for(int i = 0; i < countCards.length-3-2; i++){//minus 3 for run of 4, minus 2 for ignore wild/skip
					if(countCards[i] >= 1 && countCards[i+1] >= 1 && countCards[i+2] >= 1 && countCards[i+3] >= 1){	//need at least one run of 4 cards
						comp2 = true;
					}

					int runPieces = 0;
					if(countCards[i] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					if(countCards[i+1] >=1){
						runPieces++;
					}
					runPieces = runPieces + countCards[13] - usedWilds; //if 4 runpieces, including unused wilds and actual cards
					//Log.i("RunPieces", Integer.toString(runPieces));
					if(runPieces>=4){
						comp2 = true;
					}

				}
				if(comp1 && comp2){
					return true;
				}
				break;
			case 4:
				int[] runPotentials;
				runPotentials = runPots(myCards, 7);
				usedWilds = 0;
				if(myCards.size() != 7){
					return false;
				}
				int maxRunPot = 0;
				for(int i = 0; i < runPotentials.length; i++){
					if(runPotentials[i] > maxRunPot){
						maxRunPot = runPotentials[i];
					}
				}
				if(maxRunPot + countCards[13] >= 7){
					return true;
				}
				break;
			case 5:
				runPotentials = runPots(myCards, 8);
				usedWilds = 0;
				if(myCards.size() != 8){
					return false;
				}
				maxRunPot = 0;
				for(int i = 0; i < runPotentials.length; i++){
					if(runPotentials[i] > maxRunPot){
						maxRunPot = runPotentials[i];
					}
				}
				if(maxRunPot + countCards[13] >= 8){
					return true;
				}
				break;
			case 6:
				runPotentials = runPots(myCards, 9);
				usedWilds = 0;
				if(myCards.size() != 9){
					return false;
				}
				maxRunPot = 0;
				for(int i = 0; i < runPotentials.length; i++){
					if(runPotentials[i] > maxRunPot){
						maxRunPot = runPotentials[i];
					}
				}
				if(maxRunPot + countCards[13] >= 9){
					return true;
				}
				break;
			case 7:
				if (myCards.size() != 8) { //both components of phase 7 are 4 cards
					Log.i("Amount of Cards given", Integer.toString(myCards.size()));
					return false;
				}
				variety = new int[15]; //indicator of if a card is used in the phase need 1-14, ignore zero for readability
				for (int i = 0; i < variety.length; i++) {
					variety[i] = 0;            //initialized to zero
				}
				for (int i = 0; i < myCards.size(); i++) {
					int val = myCards.peekAt(i).getRank().value(1);
					Log.i("Incrementing variety at", Integer.toString(val));
					variety[val]++; //increment the variety at a specific location, based on card value
				}
				count = 0;
				shouldPass = true;
				for (int i = 0; i < variety.length; i++) {
					if (variety[i] != 0) {
						count++;
					}
					if (variety[i] != 4 && variety[i] != 0) {    //if there are not 4 of each variety
						Log.i("variety_shouldPass","false");
						shouldPass = false;
					}
				}
				//Log.i("Count of variety", Integer.toString(count));
				if (count == 1) { //all 8 of the same card
					return true;
				}
				else if ((count == 2) && variety[13] != 0) { //one type of card plus wildcards
					int wildsNeeded = 0;
					for(int i = 0; i <=12; i++){
						if(variety[i] != 0){
							wildsNeeded = 8 - variety[i]; //need to add to eight, including wilds and 1 card rank
							if(wildsNeeded == variety[13]){
								shouldPass = true; //if have enough wild cards, valid phase
								//Log.i("isValidPhase", "Legal");
							}
						}
					}
				}
				else if (count == 3 && variety[13] != 0) { //two types of cards plus wild cards
					int wildCardsNeeded = 0;
					for (int i  = 0; i <= 12; i++){ //only increment over value cards, not skip (14) or wild (13)
						if(variety[i] != 0){	//if there is a set or incomplete set - already determined to be 1 or 2 ranks only
							wildCardsNeeded = wildCardsNeeded + (4-variety[i]); //mark each incomplete set as needing a wildcard
						}
					}
					if (wildCardsNeeded == variety[13]){
						return true;	//if you have enough wild cards for the sets, it is a valid phase
					}
				} else if (count > 4) { //if there are more than three card values given
					return false;	//not valid phase 1 if more than 3 card types - ignoring wild card case
				}
				return shouldPass;
			case 8:
				Color phaseColor = null;
				if(myCards.size() != 7){
					return false; //need 7 cards of same color
				}
				for(int i = 0; i < myCards.size(); i++){
					if(phaseColor == null && myCards.peekAt(i).getSuit() != Color.Black){
						//if phase color isnt set, and its not a wild card
						phaseColor = myCards.peekAt(i).getSuit(); //set phase color
						Log.i("Phase Color", phaseColor.toString());
					}
					if(!(myCards.peekAt(i).getSuit() == phaseColor || (myCards.peekAt(i).getSuit() == Color.Black && myCards.peekAt(i).getWildValue() == 13)) ){
						//if doesnt match phase color, and not a wild
						return false;
					}
				}
				return true;
			case 9:
				if (myCards.size() != 7) { //both components of phase 9 add to 7 cards
					//Log.i("Amount of Cards given", Integer.toString(myCards.size()));
					return false;
				}
				variety = new int[15]; //indicator of if a card is used in the phase need 1-14, ignore zero for readability
				for (int i = 0; i < variety.length; i++) {
					variety[i] = 0;            //initialized to zero
				}
				for (int i = 0; i < myCards.size(); i++) {
					int val = myCards.peekAt(i).getRank().value(1);
					Log.i("Incrementing variety at", Integer.toString(val));
					variety[val]++; //increment the variety at a specific location, based on card value
				}
				count = 0;
				shouldPass = true;
				for (int i = 0; i < variety.length; i++) {
					if (variety[i] != 0) {
						count++;
					}
					if (!(variety[i] == 5 || variety[i] == 2) && variety[i] != 0) {    //if there are not 2 or 5 of each variety
						shouldPass = false;
					}
				}
				//Log.i("Count of variety", Integer.toString(count));
				if (count == 1) { //all 7 of the same card
					return true;
				}
				else if (count == 2 && variety[13] != 0) { //one type of card plus wildcards
					int wildsNeeded = 0;
					for(int i = 0; i <=12; i++){
						if(variety[i] != 0){
							wildsNeeded = 7 - variety[i]; //need to add to seven, including wilds and 1 card rank
							if(wildsNeeded == variety[13]){
								shouldPass = true; //if have enough wild cards, valid phase
								//Log.i("isValidPhase", "Legal");
							}
						}
					}
				}
				else if (count == 3 && variety[13] != 0) { //two types of cards plus wild cards
					int wildCardsNeeded = 0;
					boolean bigPiece = false;		//increments to true when bigpiece has been found
					for (int i  = 0; i <= 12; i++){ //only increment over value cards, not skip (14) or wild (13)
						if(variety[i] != 0){	//if there is a set or incomplete set - already determined to be 1 or 2 ranks only
							if(variety[i] > 2 && !bigPiece) {
								//if found a group, and its already more than 2
								bigPiece = true;	//first time this happens, flag as bigPiece
								wildCardsNeeded = wildCardsNeeded + (5 - variety[i]); //mark each incomplete set as needing a wildcard
							}
							else if(variety[i] > 2 && bigPiece){
								return false; //cant have two sets of more than 2
							}
							else if(variety[i] <= 2){
								//only need to add to 2 for the smaller set
								wildCardsNeeded = wildCardsNeeded + (2 - variety[i]);
							}
						}
					}
					if(!bigPiece){
						//if neither set portion had more than 2 cards, need 3 more wilds
						wildCardsNeeded = wildCardsNeeded + 3;
					}
					if (wildCardsNeeded == variety[13]){
						return true;	//if you have enough wild cards for the sets, it is a valid phase
					}
				} else if (count > 3) { //if there are more than three card values given
					return false;	//not valid phase 1 if more than 3 card types - ignoring wild card case
				}
				return shouldPass;
			case 10:
				if (myCards.size() != 8) { //both components of phase 9 add to 7 cards
					//Log.i("Amount of Cards given", Integer.toString(myCards.size()));
					return false;
				}
				variety = new int[15]; //indicator of if a card is used in the phase need 1-14, ignore zero for readability
				for (int i = 0; i < variety.length; i++) {
					variety[i] = 0;            //initialized to zero
				}
				for (int i = 0; i < myCards.size(); i++) {
					int val = myCards.peekAt(i).getRank().value(1);
					Log.i("Incrementing variety at", Integer.toString(val));
					variety[val]++; //increment the variety at a specific location, based on card value
				}
				count = 0;
				shouldPass = true;
				for (int i = 0; i < variety.length; i++) {
					if (variety[i] != 0) {
						count++;
					}
					if (!(variety[i] == 5 || variety[i] == 3) && variety[i] != 0) {    //if there are not 2 or 5 of each variety
						shouldPass = false;
					}
				}
				//Log.i("Count of variety", Integer.toString(count));
				if (count == 1) { //all 8 of the same card
					return true;
				}
				else if (count == 2 && variety[13] != 0) { //one type of card plus wildcards
					int wildsNeeded = 0;
					for(int i = 0; i <=12; i++){
						if(variety[i] != 0){
							wildsNeeded = 8 - variety[i]; //need to add to seven, including wilds and 1 card rank
							if(wildsNeeded == variety[13]){
								shouldPass = true; //if have enough wild cards, valid phase
								//Log.i("isValidPhase", "Legal");
							}
						}
					}
				}
				else if (count == 3 && variety[13] != 0) { //two types of cards plus wild cards
					int wildCardsNeeded = 0;
					boolean bigPiece = false;		//increments to true when bigpiece has been found
					for (int i  = 0; i <= 12; i++){ //only increment over value cards, not skip (14) or wild (13)
						if(variety[i] != 0){	//if there is a set or incomplete set - already determined to be 1 or 2 ranks only
							if(variety[i] > 3 && !bigPiece) {
								//if found a group, and its already more than 2
								bigPiece = true;	//first time this happens, flag as bigPiece
								wildCardsNeeded = wildCardsNeeded + (5 - variety[i]); //mark each incomplete set as needing a wildcard
							}
							else if(variety[i] > 3 && bigPiece){
								return false; //cant have two sets of more than 2
							}
							else if(variety[i] <= 3){
								//only need to add to 2 for the smaller set
								wildCardsNeeded = wildCardsNeeded + (3 - variety[i]);
							}
						}
					}
					if(!bigPiece){
						//if neither set portion had more than 3 cards, need 2 more wilds
						wildCardsNeeded = wildCardsNeeded + 2;
					}
					if (wildCardsNeeded == variety[13]){
						return true;	//if you have enough wild cards for the sets, it is a valid phase
					}
				} else if (count > 3) { //if there are more than three card values given
					return false;	//not valid phase 1 if more than 3 card types - ignoring wild card case
				}
				return shouldPass;
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
		myCards.sortNumerical();
		int[] countCards = cardsCount(myCards);

		switch(myPhaseNumber){
			case 1:
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
				//Log.i("RealVal0", Integer.toString(realVal0));
				//Log.i("RealVal1", Integer.toString(realVal1));
				Log.i("count wilds", Integer.toString(countCards[13]));
				if(countCards[13] != 0){			//if there was a wildcard
					ArrayList<Card> myWilds = new ArrayList<Card>();
					for(int i = comp0.size()-1; i >= 0; i--){
						if(comp0.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp0.removeCard(i));
							myWilds.add(x);
						}
					}
					for(int i = comp1.size()-1; i >= 0; i--){
						if(comp1.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp1.removeCard(i));
							myWilds.add(x);
						}
					}
					Log.i("myswilds size", Integer.toString(myWilds.size()));
					int size = comp0.size();
					for(int i = 0; i < 3-size; i++){
						if(!myWilds.isEmpty()) {
							Card x = new Card(myWilds.remove(0));
							x.setWildValue(realVal0);
							comp0.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
					size = comp1.size();
					for(int i = 0; i < 3-size; i++){
						if(!myWilds.isEmpty()) {
							Card x = new Card(myWilds.remove(0));
							x.setWildValue(realVal1);
							comp1.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
				}
				break;
			case 2:
				int valueSet = -1;
				for(int i = 1; i < countCards.length-2; i++){//-2 to not allow skip or wild to be marked as value of set
					if(countCards[i] > 1){	//any duplicate card must be part of set, may also be part of run
						valueSet = i;
					}
				}
				if(countCards[13] == 7){	//if all wild, choose random value
					valueSet = (int) Math.random()*12 + 1;
				}
				int twoMatch = 1;
				for(int i = 1; i < countCards.length-2; i++){//-2 to not allow skip or wild to be marked as value of set
					if(countCards[13] == 6) {	//if only 1 non-wild
						if (countCards[i] == 1) {
							valueSet = i;		//value of set is that cards value
						}
					}
					else if (countCards[13] == 5){	//if 2 non-wilds
						if (countCards[i] == 1) {	//compare to other value
							if(twoMatch == i) {		//if both cards match
								valueSet = i;        //value of set is that cards value
							}
							else{
								twoMatch = i;		//if not a match (first card), update twoMatch value
							}
						}
					}
				}
				int[] runPotentials = new int[countCards.length-2-3];
				int bestRunStart = 1;

				//-2 to not allow wild/skip, -3 because comparing next 3 cards
				for(int i = 1; i < countCards.length-2-3; i++){
					int cc0 = 0;
					int cc1 = 0;
					int cc2 = 0;
					int cc3 = 0;
					if(countCards[i] > 0){
						cc0 = 1;
					}
					if(countCards[i+1] > 0){
						cc1 = 1;
					}
					if(countCards[i+2] > 0){
						cc2 = 1;
					}
					if(countCards[i+3] > 0){
						cc3 = 1;
					}
					int runPotential = cc0 + cc1 + cc2 + cc3;	//count up whether cards are there or not
					if(valueSet != -1){	//if a pair (or more) of cards exist
						if(countCards[valueSet] <=3){	//if only have enough for a set
							if(i == valueSet || i+1 == valueSet || i+2 == valueSet || i+3 == valueSet){
								runPotential--;	//dont include thst value in run potentisl calcs
							}
						}
					}
					runPotentials[i] = runPotential;
					Log.i("RunP at "+Integer.toString(i), Integer.toString(runPotential));
				}
				for(int i = 1; i < runPotentials.length; i++){
					if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
						bestRunStart = i;
					}
				}

				if(valueSet == -1){		//only true if no pairs/triples/etc... single cards only, less than 5 wilds
					for(int i = 0; i < countCards.length-2; i++){
						if(countCards[i] > 0 && i != bestRunStart && i != bestRunStart+1 && i != bestRunStart+2 && i != bestRunStart+3){
							//found card not part of best run. If it was part of best run, it would have been a double
							valueSet = i;
						}
					}
				}
				Deck myWilds = new Deck();
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == valueSet){	//if value matches
						if(comp0.size()< 3) {
							Card temp = new Card(myCards.peekAt(i));
							comp0.add(temp);
						}
						else{
							Card temp = new Card(myCards.peekAt(i));
							comp1.add(temp); //add to run if already have set of 3
						}
					}
					else{
						if(myCards.peekAt(i).getRank().value(1) == 13){//if wild, save in wild deck
							Card temp = new Card(myCards.peekAt(i));
							myWilds.add(temp);
						}
						else {	//if not in comp0, and not a wild
							Card temp = new Card(myCards.peekAt(i));
							comp1.add(temp);    //anything not part of set, add to run
						}
					}
				}
				Log.i("myWilds Size", Integer.toString(myWilds.size()));
				int numWildToRun = 0;
				for(int i = 0; i < myWilds.size(); i++){
					if(comp0.size()< 3) {
						Card temp = new Card(myWilds.peekAt(i));
						temp.setWildValue(valueSet);
						Log.i("Add wild to set", Integer.toString(valueSet));
						comp0.add(temp);
					}
					else{ //if part of run, not set
						int[] runPieces = cardsCountWildVal(comp1);
						boolean onceThrough = false;
						for(int j = numWildToRun; j < 4; j++) {
							if (runPieces[bestRunStart+j] == 0 && !onceThrough) {    //if run piece isnt there
								Card temp = new Card(myWilds.peekAt(i));
								temp.setWildValue(bestRunStart+j);	 //set wildval to that run piece
								Log.i("add wild to run", Integer.toString(bestRunStart+j));
								comp1.add(temp);
								onceThrough = true;	//only allow one of checks to happen
								numWildToRun++;
							}
						}
					}
				}
				break;
			case 3:
				valueSet = -1;
				for(int i = 0; i < countCards.length-2; i++){//-2 to not allow skip or wild to be marked as value of set
					if(countCards[i] > 1){	//any duplicate card must be part of set, may also be part of run
						valueSet = i;
					}
				}
				if(countCards[13] == 8){	//if all wild, choose random value
					valueSet = (int) Math.random()*12 + 1;
				}
				twoMatch = 1;
				for(int i = 0; i < countCards.length-2; i++){//-2 to not allow skip or wild to be marked as value of set
					if(countCards[13] == 7) {	//if only 1 non-wild
						if (countCards[i] == 1) {
							valueSet = i;		//value of set is that cards value
						}
					}
					else if (countCards[13] == 6){	//if 2 non-wilds
						if (countCards[i] == 1) {	//compare to other value
							if(twoMatch == i) {		//if both cards match
								valueSet = i;        //value of set is that cards value
							}
							else{
								twoMatch = i;		//if not a match (first card), update twoMatch value
							}
						}
					}
				}
				runPotentials = new int[countCards.length-2-3];
				bestRunStart = 1;

				//-2 to not allow wild/skip, -3 because comparing next 3 cards
				for(int i = 1; i < countCards.length-2-3; i++){
					int cc0 = 0;
					int cc1 = 0;
					int cc2 = 0;
					int cc3 = 0;
					if(countCards[i] > 0){
						cc0 = 1;
					}
					if(countCards[i+1] > 0){
						cc1 = 1;
					}
					if(countCards[i+2] > 0){
						cc2 = 1;
					}
					if(countCards[i+3] > 0){
						cc3 = 1;
					}
					int runPotential = cc0 + cc1 + cc2 + cc3;	//count up whether cards are there or not
					if(valueSet != -1){	//if a pair (or more) of cards exist
						if(countCards[valueSet] <=3){	//if only have enough for a set
							if(i == valueSet || i+1 == valueSet || i+2 == valueSet || i+3 == valueSet){
								runPotential--;	//dont include thst value in run potentisl calcs
							}
						}
					}
					runPotentials[i] = runPotential;
					Log.i("RunP at "+Integer.toString(i), Integer.toString(runPotential));
				}
				for(int i = 1; i < runPotentials.length; i++){
					if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
						bestRunStart = i;
					}
				}

				if(valueSet == -1){		//only true if no pairs/triples/etc... single cards only, less than 5 wilds
					for(int i = 0; i < countCards.length-2; i++){
						if(countCards[i] > 0 && i != bestRunStart && i != bestRunStart+1 && i != bestRunStart+2 && i != bestRunStart+3){
							//found card not part of best run. If it was part of best run, it would have been a double
							valueSet = i;
						}
					}
				}
				myWilds = new Deck();
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == valueSet){	//if value matches
						if(comp0.size()< 4) {
							Card temp = new Card(myCards.peekAt(i));
							comp0.add(temp);
						}
						else{
							Card temp = new Card(myCards.peekAt(i));
							comp1.add(temp); //add to run if already have set of 4
						}
					}
					else{
						if(myCards.peekAt(i).getRank().value(1) == 13){//if wild, save in wild deck
							Card temp = new Card(myCards.peekAt(i));
							myWilds.add(temp);
						}
						else {	//if not in comp0, and not a wild
							Card temp = new Card(myCards.peekAt(i));
							comp1.add(temp);    //anything not part of set, add to run
						}
					}
				}
				Log.i("myWilds Size", Integer.toString(myWilds.size()));
				numWildToRun = 0;
				for(int i = 0; i < myWilds.size(); i++){
					if(comp0.size()< 4) {
						Card temp = new Card(myWilds.peekAt(i));
						temp.setWildValue(valueSet);
						Log.i("Add wild to set", Integer.toString(valueSet));
						comp0.add(temp);
					}
					else{ //if part of run, not set
						int[] runPieces = cardsCount(comp1);
						boolean onceThrough = false;
						for(int j = numWildToRun; j < 4; j++) {
							if (runPieces[bestRunStart+j] == 0 && !onceThrough) {    //if run piece isnt there
								Card temp = new Card(myWilds.peekAt(i));
								temp.setWildValue(bestRunStart+j);	 //set wildval to that run piece
								Log.i("add wild to run", Integer.toString(bestRunStart+j));
								comp1.add(temp);
								onceThrough = true;	//only allow one of checks to happen
								numWildToRun++;
							}
						}
					}
				}
				break;
			case 4:
				runPotentials = runPots(myCards, 7);
				bestRunStart = 1;

				for(int i = 1; i < runPotentials.length; i++){
					if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
						bestRunStart = i;
					}
				}

				myWilds = new Deck();
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == 13){//if wild, save in wild deck
						Card temp = new Card(myCards.peekAt(i));
						myWilds.add(temp);
					}
					else {	//if not a wild
						Card temp = new Card(myCards.peekAt(i));
						comp0.add(temp);    //anything not wild, add to run
					}

				}
				//Log.i("myWilds Size", Integer.toString(myWilds.size()));
				numWildToRun = 0;
				for(int i = 0; i < myWilds.size(); i++){
					Log.i("Loop Start", "wildComp0");
					int[] runPieces = cardsCountWildVal(comp0);
					boolean onceThrough = false;
					for(int j = numWildToRun; j < 7; j++) {
						Log.i("RunPiece at "+Integer.toString(bestRunStart+j), Integer.toString(runPieces[bestRunStart+j]));
						if (runPieces[bestRunStart+j] == 0 && !onceThrough) {    //if run piece isnt there
							Card temp = new Card(myWilds.peekAt(i));
							temp.setWildValue(bestRunStart+j);	 //set wildval to that run piece
							Log.i("add wild to run", Integer.toString(bestRunStart+j));
							comp0.add(temp);
							onceThrough = true;	//only allow one of checks to happen
							numWildToRun++;
						}
					}
				}
				break;
			case 5:
				runPotentials = runPots(myCards, 8);
				bestRunStart = 1;

				for(int i = 1; i < runPotentials.length; i++){
					if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
						bestRunStart = i;
					}
				}

				myWilds = new Deck();
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == 13){//if wild, save in wild deck
						Card temp = new Card(myCards.peekAt(i));
						myWilds.add(temp);
					}
					else {	//if not a wild
						Card temp = new Card(myCards.peekAt(i));
						comp0.add(temp);    //anything not wild, add to run
					}

				}
				//Log.i("myWilds Size", Integer.toString(myWilds.size()));
				numWildToRun = 0;
				for(int i = 0; i < myWilds.size(); i++){
					Log.i("Loop Start", "wildComp0");
					int[] runPieces = cardsCountWildVal(comp0);
					boolean onceThrough = false;
					for(int j = numWildToRun; j < 8; j++) {
						Log.i("RunPiece at "+Integer.toString(bestRunStart+j), Integer.toString(runPieces[bestRunStart+j]));
						if (runPieces[bestRunStart+j] == 0 && !onceThrough) {    //if run piece isnt there
							Card temp = new Card(myWilds.peekAt(i));
							temp.setWildValue(bestRunStart+j);	 //set wildval to that run piece
							Log.i("add wild to run", Integer.toString(bestRunStart+j));
							comp0.add(temp);
							onceThrough = true;	//only allow one of checks to happen
							numWildToRun++;
						}
					}
				}
				break;
			case 6:
				runPotentials = runPots(myCards, 9);
				bestRunStart = 1;

				for(int i = 1; i < runPotentials.length; i++){
					if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
						bestRunStart = i;
					}
				}

				myWilds = new Deck();
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == 13){//if wild, save in wild deck
						Card temp = new Card(myCards.peekAt(i));
						myWilds.add(temp);
					}
					else {	//if not a wild
						Card temp = new Card(myCards.peekAt(i));
						comp0.add(temp);    //anything not wild, add to run
					}

				}
				//Log.i("myWilds Size", Integer.toString(myWilds.size()));
				numWildToRun = 0;
				for(int i = 0; i < myWilds.size(); i++){
					Log.i("Loop Start", "wildComp0");
					int[] runPieces = cardsCountWildVal(comp0);
					boolean onceThrough = false;
					for(int j = numWildToRun; j < 9; j++) {
						Log.i("RunPiece at "+Integer.toString(bestRunStart+j), Integer.toString(runPieces[bestRunStart+j]));
						if (runPieces[bestRunStart+j] == 0 && !onceThrough) {    //if run piece isnt there
							Card temp = new Card(myWilds.peekAt(i));
							temp.setWildValue(bestRunStart+j);	 //set wildval to that run piece
							Log.i("add wild to run", Integer.toString(bestRunStart+j));
							comp0.add(temp);
							onceThrough = true;	//only allow one of checks to happen
							numWildToRun++;
						}
					}
				}
				break;
			case 7:
				c = myCards.peekAt(0);
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == c.getRank().value(1)){
						comp0.add(myCards.peekAt(i));
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				if(comp0.size() == 8){				//if all have the same value, move 4 cards to other comp
					comp0.moveTopCardTo(comp1);		//all cards will have been placed in same comp. because they match
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
				}
				realVal0 = (int) (Math.random()*12)+1;		//set value of set to random legal value
				realVal1 = (int)( Math.random()*12)+1;		//set value of set to random legal value
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
				//Log.i("RealVal0", Integer.toString(realVal0));
				//Log.i("RealVal1", Integer.toString(realVal1));
				Log.i("count wilds", Integer.toString(countCards[13]));
				if(countCards[13] != 0){			//if there was a wildcard
					ArrayList<Card> myWildCards = new ArrayList<Card>();
					for(int i = comp0.size()-1; i >= 0; i--){
						if(comp0.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp0.removeCard(i));
							myWildCards.add(x);
						}
					}
					for(int i = comp1.size()-1; i >= 0; i--){
						if(comp1.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp1.removeCard(i));
							myWildCards.add(x);
						}
					}
					Log.i("myswilds size", Integer.toString(myWildCards.size()));
					int size = comp0.size();
					for(int i = 0; i < 4-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal0);
							comp0.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
					size = comp1.size();
					for(int i = 0; i < 4-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal1);
							comp1.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
				}
				break;
			case 8:
				for(int i = 0; i < myCards.size(); i++){
					Card temp = myCards.peekAt(i);
					comp0.add(temp);	//all cards go in phase component 0
				}
				break;
			case 9:
				int bigSet = 1;
				int smallSet = 1;
				//set bigSet value
				for(int i = 1; i < countCards.length-2; i++){//minus 2 to ignore wild/skip
					if(countCards[i] > countCards[bigSet]){
						bigSet = i;
					}
				}
				//set smallSet value
				for(int i = 1; i < countCards.length-2; i++){//minus 2 to ignore wild/skip
					if(smallSet == bigSet){
						smallSet = i;       //if small & big have same value, auto increment small
					}
					if(countCards[i] > countCards[smallSet] && i != bigSet){ //dont let bigSet == smallSet
						smallSet = i;
					}
				}
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == bigSet){
						comp0.add(myCards.peekAt(i));
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				if(comp0.size() == 7){				//if all have the same value, move 2 cards to other comp
					comp0.moveTopCardTo(comp1);		//all cards will have been placed in same comp. because they match
					comp0.moveTopCardTo(comp1);
				}
				realVal0 = (int) (Math.random()*12)+1;		//set value of set to random legal value
				realVal1 = (int)( Math.random()*12)+1;		//set value of set to random legal value
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
				//Log.i("RealVal0", Integer.toString(realVal0));
				//Log.i("RealVal1", Integer.toString(realVal1));
				Log.i("count wilds", Integer.toString(countCards[13]));
				if(countCards[13] != 0){			//if there was a wildcard
					ArrayList<Card> myWildCards = new ArrayList<Card>();
					for(int i = comp0.size()-1; i >= 0; i--){
						if(comp0.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp0.removeCard(i));
							myWildCards.add(x);
						}
					}
					for(int i = comp1.size()-1; i >= 0; i--){
						if(comp1.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp1.removeCard(i));
							myWildCards.add(x);
						}
					}
					Log.i("myswilds size", Integer.toString(myWildCards.size()));
					int size = comp0.size();
					for(int i = 0; i < 5-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal0);
							comp0.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
					size = comp1.size();
					for(int i = 0; i < 2-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal1);
							comp1.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
				}
				break;
			case 10:
				bigSet = 1;
				smallSet = 1;
				//set bigSet value
				for(int i = 1; i < countCards.length-2; i++){//minus 2 to ignore wild/skip
					if(countCards[i] > countCards[bigSet]){
						bigSet = i;
					}
				}
				//set smallSet value
				for(int i = 1; i < countCards.length-2; i++){//minus 2 to ignore wild/skip
					if(smallSet == bigSet){
						smallSet = i;       //if small & big have same value, auto increment small
					}
					if(countCards[i] > countCards[smallSet] && i != bigSet){ //dont let bigSet == smallSet
						smallSet = i;
					}
				}
				for(int i = 0; i < myCards.size(); i++){
					if(myCards.peekAt(i).getRank().value(1) == bigSet){
						comp0.add(myCards.peekAt(i));
					}
					else{
						comp1.add(myCards.peekAt(i));
					}
				}
				if(comp0.size() == 8){				//if all have the same value, move 3 cards to other comp
					comp0.moveTopCardTo(comp1);		//all cards will have been placed in same comp. because they match
					comp0.moveTopCardTo(comp1);
					comp0.moveTopCardTo(comp1);
				}
				realVal0 = (int) (Math.random()*12)+1;		//set value of set to random legal value
				realVal1 = (int)( Math.random()*12)+1;		//set value of set to random legal value
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
				//Log.i("RealVal0", Integer.toString(realVal0));
				//Log.i("RealVal1", Integer.toString(realVal1));
				Log.i("count wilds", Integer.toString(countCards[13]));
				if(countCards[13] != 0){			//if there was a wildcard
					ArrayList<Card> myWildCards = new ArrayList<Card>();
					for(int i = comp0.size()-1; i >= 0; i--){
						if(comp0.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp0.removeCard(i));
							myWildCards.add(x);
						}
					}
					for(int i = comp1.size()-1; i >= 0; i--){
						if(comp1.peekAt(i).getRank().value(1) == 13){	//find wild cards
							Card x = new Card(comp1.removeCard(i));
							myWildCards.add(x);
						}
					}
					Log.i("myswilds size", Integer.toString(myWildCards.size()));
					int size = comp0.size();
					for(int i = 0; i < 5-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal0);
							comp0.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
					size = comp1.size();
					for(int i = 0; i < 3-size; i++){
						if(!myWildCards.isEmpty()) {
							Card x = new Card(myWildCards.remove(0));
							x.setWildValue(realVal1);
							comp1.add(x);
						}
					}
					//Log.i("myswilds new size", Integer.toString(myWilds.size()));
				}
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
			toReturn = new Deck (comp1); //return comp1
		}
		return toReturn;
	}

	private boolean isValidHit(int playerID, Card myC, int playerToHit, int phaseToHit){
		//return true; //always assume valid hit for now

		if (state.getPhases()[playerToHit] > 3 && state.getPhases()[playerToHit] < 7) {//phases 4, 5, 6 being hit on
			phaseToHit = 0; //only one phase comp to hit on
		}
		if (state.getPhases()[playerToHit] == 8) {//phases 8 being hit on
			phaseToHit = 0; //only one phase comp to hit on
		}

		Card myCard = new Card(myC);

		if(state.getPlayedPhase()[playerID][0].size() == 0){ //if the player has not yet made his own phase - hits are illegal
			return false;
		}
		if(state.getPlayedPhase()[playerToHit][0].size() == 0) { //if that player has not made a phase - cannot hit on that
			return false;
		}
		if(myCard == null){
			return false;
		}
		if(myCard.getWildValue() == 14){ //skip cards are never a valid hit
			return false;
		}

		if(state.getPhases()[playerToHit] == 8) { //on phase 8 color is the only thing that matters
			if(state.getPlayedPhase()[playerToHit][0].size() != 0){ //if that player has played a phase
				if(myCard.getRank().value(1) == 13){ //if a wildcard
					return true;					//wildcards are always valid for phase 8
				}
				Color myColor = null;
				for(int i = 0; i < state.getPlayedPhase()[playerToHit][0].size(); i++){
					if(state.getPlayedPhase()[playerToHit][0].peekAt(i).getSuit() != Color.Black){ //for not wilds
						myColor = state.getPlayedPhase()[playerToHit][0].peekAt(i).getSuit(); //set color to not black
						Log.i("PhaseColor in ValidHit", myColor.toString());
					}
				}
				if(myColor == null){
					return true; //if all wilds, any color is valid
				}
				if(myColor == myCard.getSuit()){
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
					if(myDeck.peekAt(i).getWildValue() != myDeck.peekAt(i+1).getWildValue()){
						set = false;
						//Log.i("IsValidHit", "Not a set");
					}
					else if(myDeck.peekAt(i).getWildValue() != (myDeck.peekAt(i+1).getWildValue()-1)){
						run = false;
						//Log.i("IsValidHit", "Not a run");
					}
				}
				if(set){
					myCard.setWildValue(myDeck.peekAt(0).getWildValue());
					myC.setWildValue(myDeck.peekAt(0).getWildValue());
					//Log.i("myCard Wild Val", Integer.toString(myCard.getWildValue()));
					//Log.i("set Wild Val", Integer.toString(myDeck.peekAt(0).getWildValue()));
					if(myCard.getWildValue() == myDeck.peekAt(0).getWildValue()){
						return true;
					}
					else{
						return false;
					}
				}
				else if(run){
					if(myDeck.maxMin(true) == 12 && myDeck.maxMin(false) == 1){
						Log.i("Run Hit Blocker", "Number1");
						return false; //if run already has 1 to 12, cannot hit
					}
					else if(myDeck.maxMin(true) < 12){
						myC.setWildValue(myDeck.maxMin(true)+1); //set to highest possible for now, later allow player to choose
					}
					else if(myDeck.maxMin(false) > 1){
						myC.setWildValue(myDeck.maxMin(false)-1); //set to lowest possible for now, later allow player to choose
					}
					Log.i("PhaseMin", Integer.toString(myDeck.maxMin(false)));
					Log.i("PhaseMax", Integer.toString(myDeck.maxMin(true)));
					Log.i("MyVal", Integer.toString(myC.getWildValue()));
					if(myC.getWildValue() == (myDeck.maxMin(false)-1)){
						Log.i("New Wild Val", Integer.toString(myC.getWildValue()));
						return true;
					}
					else if(myC.getWildValue() == (myDeck.maxMin(true))+1){
						Log.i("New Wild Val", Integer.toString(myC.getWildValue()));
						return true;
					}
					else{
						Log.i("Run Hit Blocker", "Number2");
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
				if(state.getHand(i).size() != 0) { //this if statement is a little redundant with the do-while loop
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
			state.setChooseSkip(false);
		}
		state.updatePlacements();
		//Reset "the Dealer" to be player 0
		state.setToPlay(0);
		//Start with a draw action
		state.setShouldDraw(true);
		//empty the discard/draw piles & reDeal cards to the players & put a card from draw to start discard
		state.cleanDecks();

		if(state.getDiscardCard().getWildValue() == 14){ //if flips a wild for first card
			state.setAlreadySkip(state.getToPlay(), true);	//mark first player as being skipped
			int nextIDX = (state.getToPlay() + 1) % (state.getNumberPlayers()); //increment players whose turn it is
			state.setToPlay(nextIDX);
			Toast.makeText(myContext, "First Player skipped, because of skip in discard Pile", Toast.LENGTH_SHORT).show();
		}
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
			int val = myCards.peekAt(i).getRank().value(1); //getWildValue();
			Log.i("Incrementing variety at", Integer.toString(val));
			variety[val]++; //increment the variety at a specific location
		}
		return variety;
	}

	/*
	 * Returns the count of how many of each rank of cards there are
	 */
	protected int[] cardsCountWildVal(Deck myCards){
		int variety[] = new int[15]; //indicator of if a card is used in the phase
		for(int i = 0; i < variety.length; i++){
			variety[i] = 0;			//initialized to zero
		}
		for(int i = 0; i < myCards.size(); i++){
			int val = myCards.peekAt(i).getWildValue(); //getWildValue();
			Log.i("Incrementing variety at", Integer.toString(val));
			variety[val]++; //increment the variety at a specific location
		}
		return variety;
	}

	/*
	 * Returns the run potential at each card value, based on run size desired
	 */
	protected int[] runPots(Deck myCards, int runsize){
		int[] countCards = cardsCount(myCards);
		int[] toReturn = new int[countCards.length-(runsize-1)];

		//minus 2 for ignoring wild/skip, minus (runsize-1) to allow comparison of next cards
		for(int i = 1; i < countCards.length-2-(runsize-1); i++) {

			int[] cc = new int[runsize];
			for(int j = 0; j < cc.length; j++){
				if(countCards[i+j] > 0){
					cc[j] = 1;
				}
				else {
					cc[j] = 0;
				}
			}

			int runPotential = 0;
			for(int j = 0; j < cc.length; j++){
				runPotential = runPotential + cc[j];
			}
			toReturn[i] = runPotential;
			Log.i("RunP at " + Integer.toString(i), Integer.toString(runPotential));
		}

		return toReturn;
	}
}