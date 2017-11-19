package edu.up.cs301.phase10;

import android.util.Log;

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
    public P10LocalGame() {
        Log.i("P10LocalGame", "creating game");
        // create the state for the beginning of the game
        state = new P10State(6);
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
		P10State stateForPlayer = new P10State(state, getPlayerIdx(p)); // copy of state, obscuring some information
		
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

		if (P10ma.isMakePhase()) {
			// if we have a make phase

		}
		else if (P10ma.isPlay()) { // we have a "play" action
			//What is this action? We only need make phase, draw, discard, and hit
		}
		else if(P10ma.isHitCard()){
			//if we have a hit card action
		}
		else if(P10ma.isDrawCard()){
			//if we have a draw card action
		}
		else if(P10ma.isDiscardCard()){
			//if we have a discard card action
		}
		else { // some unexpected action
			return false;
		}

		// return true, because the move was successful if we get here
		return true;
	}
	
	/**
	 * helper method that gives all the cards in the middle deck to
	 * a given player; also shuffles the target deck
	 * 
	 * @param idx
	 * 		the index of the player to whom the cards should be given
	 */
	private void giveMiddleCardsToPlayer(int idx) {
		/*
		// illegal player: ignore
		if (idx < 0 || idx > 1) return;
		
		// move all cards from the middle deck to the target deck
		state.getDeck(2).moveAllCardsTo(state.getDeck(idx));
		
		// shuffle the target deck
		state.getDeck(idx).shuffle();
		*/
	}
}
