package edu.up.cs301.phase10;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Trenton on 11/18/2017.
 */

public class P10DumbComputerPlayer extends P10ComputerPlayer {
    P10DumbComputerPlayer(String playerName){
        super(playerName);
    }

    @Override
    protected void receiveInfo(GameInfo info){

    	// if we don't have a game-state, ignore
    	if (!(info instanceof P10State)) {
    		return;
    	}

    	// update our state variable
    	savedState = (P10State)info;

        //if its this players turn
    	if (savedState.getToPlay() == this.playerNum) {
            // delay for 5 seconds for debugging - remove this for true gameplay
        	sleep(5000);

            //create an generic action to be set and sent later
            GameAction myAction;

            //if the next valid action is a draw
            if(savedState.getShouldDraw()){
                myAction = new P10DrawCardAction(this, true);  //dumb player always draws from draw pile
            }
            else { //for now, if the player doesnt draw, discard
                int cardLoc = (int)(Math.random()*9); //choose a random card to discard position 0-9
                Deck myHand = savedState.getHand(this.playerNum);
                Card c = myHand.removeCard(cardLoc);
                myAction = new P10DiscardCardAction(this, c);
            }

        	// submit our move to the game object
        	game.sendAction(myAction);
    	}
    }
}
