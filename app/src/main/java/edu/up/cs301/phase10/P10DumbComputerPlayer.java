package edu.up.cs301.phase10;

import android.util.Log;

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
        String message = "Computer"+Integer.toString(playerNum);
        //Log.i(message, "Received");

    	// if we don't have a game-state, ignore
    	if (!(info instanceof P10State)) {
    		return;
    	}

    	// update our state variable
    	savedState = (P10State)info;

        Log.i("Players hand size", Integer.toString(savedState.getHand(playerNum).size()));

        //if its this players turn
    	if (savedState.getToPlay() == this.playerNum) {
            String turnIs = Integer.toString(savedState.getToPlay());
            Log.i("Players turn", turnIs);
            // delay for 2 seconds for debugging - remove this for true gameplay
        	sleep(2000);

            //create an generic action to be set and sent later
            GameAction myAction = null;

            //if the next valid action is a draw
            if(savedState.getShouldDraw()){
                Log.i("Drawing - Player", Integer.toString(this.playerNum));
                myAction = new P10DrawCardAction(this, true);  //dumb player always draws from draw pile
            }
            else { //if its not time to draw
                //if neither phase component has been made
                //Log.i("Sizes", Integer.toString(savedState.getPlayedPhase()[playerNum][0].size())+Integer.toString(savedState.getPlayedPhase()[playerNum][1].size()));
                if(savedState.getPlayedPhase()[playerNum][0].size() == 0 && savedState.getPlayedPhase()[playerNum][1].size() == 0) {
                    Deck phaseComponent = validPhase(savedState.getHand(playerNum), savedState.getPhases()[playerNum]);
                    Log.i("Phase Component Size", Integer.toString(phaseComponent.size()));
                    if (phaseComponent.size() > 0) {  //if there are cards in the attempted phase component
                        if (savedState.getPlayedPhase()[playerNum][0].size() == 0) {
                            myAction = new P10MakePhaseAction(this, phaseComponent, false); //attempt to place phase component
                        } else {
                            myAction = new P10MakePhaseAction(this, phaseComponent, true);  //if full, try other spot
                        }
                    }
                }
                else if(savedState.getPlayedPhase()[playerNum][1].size() == 0) {
                    Deck phaseComponent = validPhase(savedState.getHand(playerNum), savedState.getPhases()[playerNum]);
                    Log.i("Phase Component Size", Integer.toString(phaseComponent.size()));
                    if (phaseComponent.size() > 0) {  //if there are cards in the attempted phase component
                        if (savedState.getPlayedPhase()[playerNum][0].size() == 0) {
                            myAction = new P10MakePhaseAction(this, phaseComponent, false); //attempt to place phase component
                        } else {
                            myAction = new P10MakePhaseAction(this, phaseComponent, true);  //if full, try other spot
                        }
                    }
                }
                else if (savedState.getPlayedPhase()[playerNum][0].size() == 1 && savedState.getPlayedPhase()[playerNum][1].size() == 1) {
                    //will put code for hitting cards here
                }

                if(myAction == null){
                    Log.i("Discarding - Player", Integer.toString(this.playerNum));
                    Card c = toDiscard(savedState.getHand(playerNum), savedState.getPhases()[playerNum]);
                    myAction = new P10DiscardCardAction(this, c);
                }
            }

        	// submit our move to the game object
        	game.sendAction(myAction);
    	}
    }

    @Override
    protected Card toDiscard(Deck myCards, int myPhaseNumber){
        for(int i = 0; i < myCards.size(); i++){ //for all cards
            boolean[] match = new boolean[myCards.size()]; //holds which cards match that value
            for(int k = 0; k < match.length; k++){
                match[k] = false;
            }
            for(int j = 0; j < myCards.size(); j++){ //compare to each subsequent card
                if(myCards.peekAt(i).equals(myCards.peekAt(j))){
                    match[j] = true;                        //declare match that card
                }
            }
            int count = 0;
            for(int k = i; k < match.length; k++){
                if(match[k]){
                    count++;                                //count how many matched cards
                }
            }
            if(count == 1){                                 //if there is not a match, discard
                Log.i("Player "+Integer.toString(playerNum)+" discarding", Integer.toString(i));
                return myCards.peekAt(i);
            }
        }
        return myCards.peekAt((int)Math.random()*myCards.size());
    }
}
