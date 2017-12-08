package edu.up.cs301.phase10;

/**
 * Created by Trenton on 12/7/2017.
 */

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Color;
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

        //Log.i("Players hand size", Integer.toString(savedState.getHand(playerNum).size()));

        //if its this players turn
        if (savedState.getToPlay() == this.playerNum) {
            String turnIs = Integer.toString(savedState.getToPlay());
            Log.i("Players turn", turnIs);
            // delay for 0.5 seconds
            sleep(500);

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
                            myAction = new P10MakePhaseAction(this, phaseComponent); //attempt to place phase component
                        }
                    }
                }
                else if (savedState.getPlayedPhase()[playerNum][0].size() != 0 /*&& savedState.getPlayedPhase()[playerNum][1].size() != 0*/) {
                    //dont compare both phase components - will break when only one phase component
                    P10HitCardAction temp = generateHitCardAction();
                    if(temp != null){
                        myAction = temp;
                    }
                }

                if(myAction == null){   //if didnt make phase or hit
                    Log.i("Discarding - Player", Integer.toString(this.playerNum));
                    //sleeps for half a second before discarding
                    sleep(500);
                    Card c = new Card(toDiscard(savedState.getHand(playerNum), savedState.getPhases()[playerNum]));
                    if(c.getWildValue() ==  14){
                        //if its a skip card
                        c.setSkipValue(generateToSkip());
                    }
                    myAction = new P10DiscardCardAction(this, c);
                }
            }



            // submit our move to the game object
            game.sendAction(myAction);
        }
    }

    @Override
    protected Card toDiscard(Deck myCards, int myPhaseNumber){
        int count[] = cardsCount(myCards);                      //returns array with count of each rank of card
        int valueToDiscard = -1;
        Card toDiscard = null;

        do{
            valueToDiscard = (int) (Math.random()*14 + 1);
        } while (count[valueToDiscard] == 0); //keep choosing random cards to discard while the value selected has no cards

        for(int i = 0; i < myCards.size(); i++){
            if(myCards.peekAt(i).getWildValue() == valueToDiscard){
                toDiscard = new Card(myCards.peekAt(i));
            }
        }

        Log.i("Card being Discarded", toDiscard.toString());
        return toDiscard;
    }
}

