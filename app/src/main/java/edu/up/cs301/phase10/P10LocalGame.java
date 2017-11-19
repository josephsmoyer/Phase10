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
        state = new P10State(/*players.length*/ 2);
    }


    /**
     * checks whether the game is over; if so, returns a string giving the result
     * 
     * @result
     * 		the end-of-game message, or null if the game is not over
     */
    @Override
    protected String checkIfGameOver() {
    	/*
    	if (state.getDeck(2).size() > 0) {
    		// there are cards in the middle pile
    		//if (state.getDeck(0).size() == 0 &&
    		//		state.getDeck(1).size() == 0 &&
    				//state.getDeck(2).peekAtTopCard().getRank() != Rank.JACK) {
    			// All the cards have ended up in the middle pile, and the top card
    			// is not a Jack. This situation is a draw, since the only move a player
    			// would would be to slap the top card, causing his opponent to win.
    		//	return "game is a draw";
    		//}
    		//else {
    			// there are either cards in at least two piles, or all cards are in the
    			// middle pile with a Jack on top; return null, as the game is not over
    			return null;
    		///}
    	}
    	else if (state.getDeck(0).size() <= 0) {
    		// player 1 has all the cards
    		return this.playerNames[1]+" is the winner";
    	}
    	else if (state.getDeck(1).size() <= 0) {
    		// player 0 has all the cards
    		return this.playerNames[0]+" is the winner";
    	}
    	else {
    		// each player has some cards: no winner yet
    		return null;
    	}
    	*/
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
		/*
		// if there is no state to send, ignore
		if (state == null) {
			return;
		}

		// make a copy of the state; null out all cards except for the
		// top card in the middle deck
		P10State stateForPlayer = new P10State(state); // copy of state
		stateForPlayer.nullAllButTopOf2(); // put nulls except for visible card
		
		// send the modified copy of the state to the player
		p.sendInfo(stateForPlayer);
		*/
	}
	
	/**
	 * whether a player is allowed to move
	 * 
	 * @param playerIdx
	 * 		the player-number of the player in question
	 */
	protected boolean canMove(int playerIdx) {
		/*
		if (playerIdx < 0 || playerIdx > 1) {
			// if our player-number is out of range, return false
			return false;
		}
		else {
			// player can move if it's their turn, or if the middle deck is non-empty
			// so they can slap
			return state.getDeck(2).size() > 0 || state.getToPlay() == playerIdx;
		}
		*/
		return false;
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
		
		// check that we have slap-jack action; if so cast it
		if (!(action instanceof P10MoveAction)) {
			return false;
		} 
		P10MoveAction P10ma = (P10MoveAction) action;
		
		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(P10ma.getPlayer());
		
		if (thisPlayerIdx < 0) { // illegal player
			return false;
		}

		if (P10ma.isMakePhase()) {
			// if we have a slap 

		}
		else if (P10ma.isPlay()) { // we have a "play" action
			if (thisPlayerIdx != state.getToPlay()) {
				// attempt to play when it's the other player's turn
				return false;
			}
			//else {
				// it's the correct player's turn: move the top card from the
				// player's deck to the top of the middle deck
			//	state.getDeck(thisPlayerIdx).moveTopCardTo(state.getDeck(2));
				// if the opponent has any cards, make it the opponent's move
				//if (state.getDeck(1-thisPlayerIdx).size() > 0) {
				//	state.setToPlay(1-thisPlayerIdx);
				//}
			//}
		}
		else if(P10ma.isHitCard()){


		}
		else if(P10ma.isDrawCard()){

		}
		else if(P10ma.isDiscardCard()){

		}
		else { // some unexpected action
			return false;
		}

		// return true, because the move was successful if we get her
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
