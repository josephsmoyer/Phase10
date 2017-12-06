package edu.up.cs301.phase10;

import android.util.Log;

import java.util.ArrayList;

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

        /*if(myCards.size() > 9){     //if havent hit yet, always discard wilds. Dumb computer wont make phase with them
            if(count[13] != 0){
                valueToDiscard = 13;
            }
            for(int i = 0; i < myCards.size(); i++){
                if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                    toDiscard = myCards.peekAt(i);
                }
            }
            if(toDiscard != null){
                return toDiscard;
            }
        }*/


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
                    int random;
                    if(myCards.size() == count[13]){
                        random = (int) Math.random() * myCards.size();     //pick a random card
                    }
                    else {  //only loop if not all wilds
                        do {
                            random = (int) Math.random() * myCards.size();     //pick a random card
                        }
                        while (myCards.peekAt(random).getRank().value(1) != 13); //repick if its wild
                    }
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
                    int random;
                    if(myCards.size() == count[13]){
                        random = (int) Math.random() * myCards.size();     //pick a random card
                    }
                    else {  //only loop if not all wilds
                        do {
                            random = (int) Math.random() * myCards.size();     //pick a random card
                        }
                        while (myCards.peekAt(random).getRank().value(1) != 13); //repick if its wild
                    }
                    toDiscard = myCards.peekAt(random);
                }
                break;
            case 3:
                hasSet = false;
                for(int i = 0; i < count.length; i++){
                    if(count[i] == 4){
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
                        if(count[i] >= 4){
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
                    int random;
                    if(myCards.size() == count[13]){
                        random = (int) Math.random() * myCards.size();     //pick a random card
                    }
                    else {  //only loop if not all wilds
                        do {
                            random = (int) Math.random() * myCards.size();     //pick a random card
                        }
                        while (myCards.peekAt(random).getRank().value(1) != 13); //repick if its wild
                    }
                    toDiscard = myCards.peekAt(random);
                }
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
