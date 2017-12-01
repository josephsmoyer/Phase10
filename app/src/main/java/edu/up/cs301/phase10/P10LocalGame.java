package edu.up.cs301.phase10;

import android.util.Log;

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
     * checks whether the game is over; if so, returns a string giving the result
     * 
     * @result
     * 		the end-of-game message, or null if the game is not over
     */
    @Override
    protected String checkIfGameOver() {
    	//basic win check - sends message when someone has made the final phase
    	for(int i : state.getPhases()){
			if(state.getPhases()[i] == 11){
				return this.playerNames[i]+" is the winner";
			}
		}
		//if no player has won, return null
    	return null;
    }

    /**
     * sends the updated state to the given player. In our case, we need to
     * make a copy of the Deck, and null out all the cards except the top card
     * in the middle deck, since that's the only one they can "see"
     * 
     * @param p
     * 		the player to which the state is to be sent
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
	 * @param playerIdx
	 * 		the player-number of the player in question
	 */
	protected boolean canMove(int playerIdx) {

		if (playerIdx < 0 || playerIdx > 5) {
			// if our player-number is out of range, return false
			return false;
		}
		else {
			// player can move if it's their turn
			return  state.getToPlay() == playerIdx;
		}
	}

	/**
	 * makes a move on behalf of a player
	 * 
	 * @param action
	 * 		the action denoting the move to be made
	 * @return
	 * 		true if the move was legal; false otherwise
	 */
	@Override
	protected boolean makeMove(GameAction action) {
		
		// check that we have phase 10action; if so cast it
		if (!(action instanceof P10MoveAction)) {
			return false;
		} 
		P10MoveAction P10ma = (P10MoveAction) action;
		
		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(P10ma.getPlayer());
		
		if (thisPlayerIdx < 0 || thisPlayerIdx > 5) { // illegal player
			return false;
		}
		if (!canMove(thisPlayerIdx)){	//not their turn
			return false;
		}

		if (P10ma.isMakePhase()) {
			//if it is supposed to be a draw action
			if(state.getShouldDraw()){
				return false;
			}
			// if we have a make phase
			P10MakePhaseAction myAction = (P10MakePhaseAction) P10ma;
			if(isValidPhase(thisPlayerIdx, myAction.getPhase())){
				if(myAction.getSide()){
					if(state.getPlayedPhase()[thisPlayerIdx][0].size() != 0){ return false;} //if there is already something there
				}
				else if(!myAction.getSide()){
					if(state.getPlayedPhase()[thisPlayerIdx][1].size() != 0){ return false;}
				}
				for(int i = 0; i < myAction.getPhase().size(); i++){
					for(int j = 0; j < state.getHand(thisPlayerIdx).size(); j++){
						if(myAction.getPhase().peekAt(i).equals(state.getHand(thisPlayerIdx).peekAt(j))){
							Card c = state.getHand(thisPlayerIdx).removeCard(j);
							if(myAction.getSide()) {
								state.getPlayedPhase()[thisPlayerIdx][0].add(c);
							}
							else{
								state.getPlayedPhase()[thisPlayerIdx][1].add(c);
							}
						}
					}
				}
			}
			else{
				return false;
			}
			return true;
		}
		else if (P10ma.isPlay()) { // we have a "play" action
			//What is this action? We only need make phase, draw, discard, and hit
		}
		else if(P10ma.isHitCard()){
			//if it is supposed to be a draw action
			if(state.getShouldDraw()){
				return false;
			}
			//if we have a hit card action
			P10HitCardAction myAction = (P10HitCardAction) P10ma;
		}
		else if(P10ma.isDrawCard()){
			//if it is not supposed to be a draw action
			if(!state.getShouldDraw()){
				return false;
			}
			//if we have a draw card action, at the proper time
			P10DrawCardAction myAction = (P10DrawCardAction) P10ma;
			//determine which pile should be drawn from
			if(myAction.drawPile){
				Card c = state.getDrawCard();
				state.getHand(thisPlayerIdx).add(c);
			}
			else{
				Card c = state.getDiscardCard();
				state.getHand(thisPlayerIdx).add(c);
			}
			//after a successful draw, the next move will not be a draw
			state.setShouldDraw(false);
		}
		else if(P10ma.isDiscardCard()){
			//if it is supposed to be a draw action
			if(state.getShouldDraw()){
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
			if(nextIDX >= state.getNumberPlayers()){
				nextIDX = 0;				//if it was the last players turn, reset to player zero
			}
            Log.i("Turn is player", Integer.toString(nextIDX));
			state.setToPlay(nextIDX);		//update\
		}
		else { // some unexpected action
			return false;
		}

		// return true, because the move was successful if we get here
		return true;
	}
	
	/**
	 * helper method that determines if a phase component is valid
	 * 
	 * @param playerNumber
	 * 		the player giving the cards
	 *
	 * @param myCards
	 * 		the deck making up the suppossed phase
	 */
	private boolean isValidPhase(int playerNumber, Deck myCards) {
		int[] phases = state.getPhases();
		int phaseNum = phases[playerNumber];

		return true;

		/*
		if(phaseNum == 1){
			if(myCards.size() != 3){ //both components of phase 1 are 3 cards
				return false;
			}
			if(myCards.peekAt(0).getRank() == myCards.peekAt(1).getRank() && myCards.peekAt(1).getRank() == myCards.peekAt(2).getRank()){
				return true; //returns true if all cards match value
			}
		}
		else if (phaseNum == 2){
			if(myCards.size() != 3){
				return false;
			}
			else{
			for(int i=4; i>0; i--)
			{
				for(int j = 4; j>0 ; j--)
				{

				}
			}

			}

		}
		else if (phaseNum == 3){

		}
		else if (phaseNum == 4){

		}
		else if (phaseNum == 5){

		}
		else if (phaseNum == 6){

		}
		else if (phaseNum == 7){

		}
		else if (phaseNum == 8){

		}
		else if (phaseNum == 9){

		}
		else if (phaseNum == 10){

		}
		return false;
		*/
	}
}
