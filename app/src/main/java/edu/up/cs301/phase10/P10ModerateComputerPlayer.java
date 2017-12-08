package edu.up.cs301.phase10;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Color;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Trenton on 11/18/2017.
 */

public class P10ModerateComputerPlayer extends P10ComputerPlayer {
    P10ModerateComputerPlayer(String playerName){
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
            //if next valid action is to skip player
            else if (savedState.getChooseSkip()) {
                int random;
                boolean[] alreadySkipped = savedState.getAlreadySkip();
                boolean allSkipped = true;
                do {
                    random = (int) (Math.random() * (savedState.getNumberPlayers()));
                    if (alreadySkipped[random]){ //if that player has already been skipped
                        random = playerNum; //set value so that loop will reset
                    }
                    for(int i = 0; i < alreadySkipped.length; i++){
                        if(i != random){    //ignoring skipping yourself
                            if(!alreadySkipped[i]){
                                allSkipped = false;      //update allskipped if you can find a player to skip
                            }
                        }
                    }
                    if(allSkipped){
                        random = -1; //if everyone has been skipped exit loop by setting value
                    }
                } while (random == playerNum);
                //if (savedState.getAlreadySkip() && !savedState.getToSkip)
                myAction = new P10SkipPlayerAction(this, random);
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


        if(count[14] > 0){  //always discard skips first
            Card temp = null;
            for(int i = 0; i < myCards.size(); i++) {
                if (myCards.peekAt(i).getWildValue() == 14) {
                    temp = new Card(myCards.peekAt(i));
                    return temp;
                }
            }
        }
        switch(myPhaseNumber){
            case 1:
                for(int i = 0; i < count.length-2; i++){                  //finds a card you only have one of
                    if (count[i] == 1){
                        valueToDiscard = i;
                    }
                }
                if(valueToDiscard == -1){                               //if no single cards
                    for(int i = 0; i < count.length-2; i++){
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
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
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
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
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
                    if(myCards.size() == count[13]){                        //if all wilds
                        random = (int) Math.random() * myCards.size();     //pick a random card
                    }
                    else {  //only loop if not all wilds
                        do {
                            //Log.i("Stuck in", "Do-While");
                            random = (int) Math.random() * myCards.size();     //pick a random card
                        }
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
                    }
                    toDiscard = new Card(myCards.peekAt(random));
                }
                break;
            case 4:
                valueToDiscard = -1;
                for(int i = 0; i < count.length-2; i++){ //dont check wild/skip
                    if(count[i] > 1){
                        valueToDiscard = i; //discards highest value duplicate
                    }
                }
                if(valueToDiscard == -1){
                    int normalCards = 0;
                    for(int i = 0; i < count.length-2; i++){
                        if(count[i] > 0){
                            normalCards++;  //find out how many non-special cards you have
                        }
                    }
                    if(normalCards != 0) {  //if there is a non-special card
                        do {
                            //Log.i("Stuck in", "Non-Special");
                            valueToDiscard = (int) (Math.random() * 12 + 1);    //if no duplciates, discard value = 1->12
                            //Log.i("value to discard", Integer.toString(valueToDiscard));
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                    else{
                        do {
                            //Log.i("Stuck in", "All-Special");
                            valueToDiscard = (int) (Math.random() * 14 + 1);    //if no duplciates, discard value = 1->12, Wild, Skip
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                }
                for(int i = 0; i < myCards.size(); i++){
                    if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                        toDiscard = new Card(myCards.peekAt(i));
                    }
                }
                break;
            case 5:
                valueToDiscard = -1;
                for(int i = 0; i < count.length-2; i++){ //dont check wild/skip
                    if(count[i] > 1){
                        valueToDiscard = i; //discards highest value duplicate
                    }
                }
                if(valueToDiscard == -1){
                    int normalCards = 0;
                    for(int i = 0; i < count.length-2; i++){
                        if(count[i] > 0){
                            normalCards++;  //find out how many non-special cards you have
                        }
                    }
                    if(normalCards != 0) {  //if there is a non-special card
                        do {
                            //Log.i("Stuck in", "Non-Special");
                            valueToDiscard = (int) (Math.random() * 12 + 1);    //if no duplciates, discard value = 1->12
                            //Log.i("value to discard", Integer.toString(valueToDiscard));
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                    else{
                        do {
                            //Log.i("Stuck in", "All-Special");
                            valueToDiscard = (int) (Math.random() * 14 + 1);    //if no duplciates, discard value = 1->12, Wild, Skip
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                }
                for(int i = 0; i < myCards.size(); i++){
                    if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                        toDiscard = new Card(myCards.peekAt(i));
                    }
                }
                break;
            case 6:
                valueToDiscard = -1;
                for(int i = 0; i < count.length-2; i++){ //dont check wild/skip
                    if(count[i] > 1){
                        valueToDiscard = i; //discards highest value duplicate
                    }
                }
                if(valueToDiscard == -1){
                    int normalCards = 0;
                    for(int i = 0; i < count.length-2; i++){
                        if(count[i] > 0){
                            normalCards++;  //find out how many non-special cards you have
                        }
                    }
                    if(normalCards != 0) {  //if there is a non-special card
                        do {
                            //Log.i("Stuck in", "Non-Special");
                            valueToDiscard = (int) (Math.random() * 12 + 1);    //if no duplciates, discard value = 1->12
                            //Log.i("value to discard", Integer.toString(valueToDiscard));
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                    else{
                        do {
                            //Log.i("Stuck in", "All-Special");
                            valueToDiscard = (int) (Math.random() * 14 + 1);    //if no duplciates, discard value = 1->12, Wild, Skip
                        }
                        while (count[valueToDiscard] == 0); //repeat random until found a card to discard in hand
                    }
                }
                for(int i = 0; i < myCards.size(); i++){
                    if(myCards.peekAt(i).getRank().value(1) == valueToDiscard){
                        toDiscard = new Card(myCards.peekAt(i));
                    }
                }
                break;
            case 7:
                for(int i = 0; i < count.length-2; i++){                  //finds a card you only have one of
                    if (count[i] == 1){
                        valueToDiscard = i;
                    }
                }
                if(valueToDiscard == -1){                               //if no single cards
                    for(int i = 0; i < count.length-2; i++){
                        if(count[i] == 2){                              //find a value you have multiple of
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
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
                    }
                    toDiscard = myCards.peekAt(random);
                }
                break;
            case 8:
                int[] colors = new int[5]; //red(0), blue(1), green(2), yellow(3), black(4)
                for(int i = 0; i < colors.length; i++){
                    colors[i] = 0;
                }
                for(int i = 0; i < myCards.size(); i++){
                    if(myCards.peekAt(i).getSuit() == Color.Red){
                        colors[0]++;
                    }
                    if(myCards.peekAt(i).getSuit() == Color.Blue){
                        colors[1]++;
                    }
                    if(myCards.peekAt(i).getSuit() == Color.Green){
                        colors[2]++;
                    }
                    if(myCards.peekAt(i).getSuit() == Color.Yellow){
                        colors[3]++;
                    }
                    if(myCards.peekAt(i).getSuit() == Color.Black){
                        colors[4]++;
                    }
                }
                int minPos = 0;
                for(int i = 1; i < colors.length-1; i++){ //minus 1, never discard wilds
                    if(colors[minPos] == 0){    //if no cards of that color, auto update minpos
                        minPos = i;
                    }

                    if(colors[i] < colors[minPos] && colors[i] > 0){    //only check if wouldnt set to zero
                        minPos = i; //find color with least cards
                    }
                }
                Color killColor = null;
                if(minPos == 0){
                    killColor = Color.Red;
                }
                if(minPos == 1){
                    killColor = Color.Blue;
                }
                if(minPos == 2){
                    killColor = Color.Green;
                }
                if(minPos == 3){
                    killColor = Color.Yellow;
                }
                for(int i = 0; i < myCards.size(); i++){
                    if(myCards.peekAt(i).getSuit() == killColor){
                        toDiscard = new Card(myCards.peekAt(i)); //discards highest value card of minColor
                    }
                }
                break;
            case 9:
                for(int i = 0; i < count.length-2; i++){                  //finds a card you only have one of
                    if (count[i] == 1){
                        valueToDiscard = i;
                    }
                }
                if(valueToDiscard == -1){                               //if no single cards
                    for(int i = 0; i < count.length-2; i++){
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
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
                    }
                    toDiscard = myCards.peekAt(random);
                }
                break;
            case 10:
                for(int i = 0; i < count.length-2; i++){                  //finds a card you only have one of
                    if (count[i] == 1){
                        valueToDiscard = i;
                    }
                }
                if(valueToDiscard == -1){                               //if no single cards
                    for(int i = 0; i < count.length-2; i++){
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
                        while (myCards.peekAt(random).getRank().value(1) == 13); //repick if its wild
                    }
                    toDiscard = myCards.peekAt(random);
                }
                break;
        }

        Log.i("Card being Discarded", toDiscard.toString());
        return toDiscard;
    }
}
