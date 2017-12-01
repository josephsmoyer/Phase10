package edu.up.cs301.phase10;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by josephsmoyer on 11/8/17.
 *
 * ///P10SmartComputerPlayer:///
 *
 * /DRAW from Discard or Draw pile according to hand and phase:/
 *
 * if phase has not yet been played, check hand.
 * - Phase 1 (SET OF THREE, SET OF THREE):
 * If there are sets of 2 in hand,
 * check discard pile for matching cards.
 * Pull from discard (return FALSE, break).
 * - Phase 2 (SET OF THREE, RUN OF FOUR):
 * If there is a set of two and not a set of three,
 * check discard pile for matching cards.
 * Then, if there is a run of 2-3,
 * check discard pile
 * - Phase 3 (SET OF FOUR, RUN OF FOUR):
 * - Phase 4 (RUN OF SEVEN):
 * - Phase 5 (RUN OF EIGHT):
 * - Phase 6 (RUN OF NINE):
 * - Phase 7 (SET OF FOUR, SET OF FOUR):
 * - Phase 8 (SEVEN OF ONE COLOR):
 * - Phase 9 (SET OF FIVE, SET OF TWO):
 * - Phase 10 (SET OF FIVE, SET OF THREE):
 *
 * if phase has been played OR no matches in hand,
 * check if other players have made phases.
 * if discard matches set, run, or color (PHASE 8 only),
 * draw from discard (return FALSE, break).
 *
 * take from draw (TRUE).
 *
 * /DISCARD least important cards:/
 *
 * @author Trenton Langer
 * @version nov 2017
 * base code taken from P10DumbComputerPlayer
 *
 * @author Kaitlin Larson
 * @version Nov 2017
 * draw logic
 */

public class P10SmartComputerPlayer extends P10ComputerPlayer {

    public P10SmartComputerPlayer(String playerName) {
        super(playerName);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        // if we don't have a game-state, ignore
        if (!(info instanceof P10State)) {
            return;
        }

        // update our state variable
        savedState = (P10State)info;

        // On player turn:
        if (savedState.getToPlay() == this.playerNum) {
            String turnIs = Integer.toString(savedState.getToPlay());

            // delay for 0.5 seconds
            sleep(500);

            // create a generic action to be set and sent later
            GameAction myAction = null;

            //if the next valid action is a draw
            if(savedState.getShouldDraw()){

                // get phase.
                int currentPhase = savedState.getPhases()[playerNum];

                // find if their own phase is completed
                boolean phasePlayed;
                if ( savedState.getPlayedPhase()[playerNum] == null ) {
                    phasePlayed = false;
                } else {
                    phasePlayed = true;
                }

                // if they have played a phase
                if ( phasePlayed ) {
                    switch (currentPhase) {
                        case 1:
                    }
                }

                // if they haven't played a phase
                else {
                    // for each other player's phase
                    for (int i=0;i<savedState.getPlayedPhase().length;i++) {
                        // for each of their two phases
                        for (int j=0;j<2;j++) {
                            // if phase exists:
                            if (savedState.getPlayedPhase()[i][j] != null) {
                                // check every card in deck:
                                for (int k = 1; k<savedState.getPlayedPhase()[i][j].deckSize(); k++) {
                                    // first by rank:
                                }
                            }
                        }
                    }
                }

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
                else if (savedState.getPlayedPhase()[playerNum][0].size() != 0 && savedState.getPlayedPhase()[playerNum][1].size() != 0) {
                    //will put code for hitting cards here
                    P10HitCardAction temp = generateHitCardAction();
                    if(temp != null){
                        myAction = temp;
                    }
                }

                if(myAction == null){   //if didnt make phase or hit
                    Log.i("Discarding - Player", Integer.toString(this.playerNum));
                    //sleeps for half a second before discarding
                    sleep(500);
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
        int count[] = cardsCount(myCards);                      //returns array with count of each rank of card
        int valueToDiscard = -1;
        Card toDiscard = null;
        ArrayList<Integer> toSave = new ArrayList<Integer>();

        switch(myPhaseNumber){
            case 1:
                for(int i = 0; i < count.length; i++){                  //finds a card you only have one of
                    if (count[i] == 1){
                        valueToDiscard = i;
                    }
                }
                if(valueToDiscard == -1){                               //if no single cards
                    for(int i = 0; i < count.length; i++){
                        if(count[i] != 0){                              //find a value you have multiple of
                            valueToDiscard = i;                         //decide to discard that value
                        }
                    }
                }

                for(int i = 0; i < myCards.size(); i++){                //check all cards
                    if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){ //set discard card as one that matches discard value
                        toDiscard = myCards.peekAt(i);
                    }
                }

                if(toDiscard == null){                                  //if discard card never got set
                    int random = (int)Math.random()*myCards.size();     //pick a random card
                    toDiscard = myCards.peekAt(random);
                }
                break;
            case 2:
                boolean hasSet = false;
                for(int i = 0; i < count.length; i++){
                    if(count[i] == 3){
                        hasSet = true;
                    }
                }
                if(!hasSet){        //if no set yet
                    for(int i = 0; i < count.length; i++){
                        if(count[i] == 1){
                            valueToDiscard = i;     //chooses to discard the highest value card not in a group
                        }
                    }
                    for(int i = 0; i < myCards.size(); i++){
                        if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                            toDiscard = myCards.peekAt(i);
                        }
                    }

                }
                if(hasSet){
                    for(int i = 0; i < count.length; i++){
                        if(count[i] >= 3){
                            toSave.add(i);
                        }
                    }
                    for(int i = 0; i < count.length-3; i++){
                        if(count[i] > 1 && count[i+1] > 1 && count[i+2] > 1 && count[i+3] > 1){
                            toSave.add(i);
                        }
                    }
                    for(int i = 0; i < count.length-2; i++){
                        if(count[i] > 1 && count[i+1] > 1 && count[i+2] > 1){
                            toSave.add(i);
                        }
                    }
                    for(int i = 0; i < count.length-1; i++){
                        if(count[i] > 1 && count[i+1] > 1){
                            toSave.add(i);
                        }
                    }
                    for(int i = 0; i < count.length-1; i++){
                        if(count[i] > 1 && count[i+1] == 0){
                            toSave.add(i);
                        }
                    }
                    valueToDiscard = toSave.get(toSave.size()-1);
                    for(int i = 0; i < myCards.size(); i++){
                        if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                            toDiscard = myCards.peekAt(i);
                        }
                    }
                }
                if(toDiscard == null){                                  //if discard card never got set
                    int random = (int)Math.random()*myCards.size();     //pick a random card
                    toDiscard = myCards.peekAt(random);
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


        return toDiscard;


    }
}
