package edu.up.cs301.phase10;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Color;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class P10ComputerPlayer extends GameComputerPlayer
{
    // the minimum reaction time for this player, in milliseconds
    protected double minReactionTimeInMillis;

    // the most recent state of the game
    protected P10State savedState;

    /**
     * Constructor for the P10ComputerPlayer class; creates an "average"
     * player.
     *
     * @param name
     * 		the player's name
     */
    public P10ComputerPlayer(String name) {
        // invoke general constructor to create player whose average reaction
        // time is half a second.
        this(name, 0.5);
    }

    /*
     * Constructor for the P10ComputerPlayer class
     */
    public P10ComputerPlayer(String name, double avgReactionTime) {
        // invoke superclass constructor
        super(name);

        // set the minimim reaction time, which is half the average reaction
        // time, converted to milliseconds (0.5 * 1000 = 500)
        minReactionTimeInMillis = 500*avgReactionTime;
    }

    /**
     * Invoked whenever the player's timer has ticked. It is expected
     * that this will be overridden in many players.
     */
    //@Override
    //protected void timerTicked() {
		/*
    	// we had seen a Jack, now we have waited the requisite time to slap

    	// look at the top card now. If it's still a Jack, slap it
    	Card topCard = savedState.getDeck(2).peekAtTopCard();
    	if (topCard != null && topCard.getRank() == Rank.SKIP) {
    		// the Jack is still there, so submit our move to the game object
    		game.sendAction(new P10MakePhaseAction(this));
    	}

    	// stop the timer, since we don't want another timer-tick until it
    	// again is explicitly started
    	getTimer().stop();
    	*/
    //}

    /**
     * callback method, called when we receive a message, typically from
     * the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
    	/*
    	// if we don't have a game-state, ignore
    	if (!(info instanceof P10State)) {
    		return;
    	}

    	// update our state variable
    	savedState = (P10State)info;

    	// access the state's middle deck
    	Deck middleDeck = savedState.getDeck(2);

    	// look at the top card
    	Card topCard = middleDeck.peekAtTopCard();

    	// if it's a Jack, slap it; otherwise, if it's our turn to
    	// play, play a card
    	if (topCard != null && topCard.getRank() == Rank.WILD) {
    		// we have a Jack to slap: set up a timer, depending on reaction time.
    		// The slap will occur when the timer "ticks". Our reaction time will be
    		// between the minimum reaction time and 3 times the minimum reaction time
        	int time = (int)(minReactionTimeInMillis*(1+2*Math.random()));
    		this.getTimer().setInterval(time);
    		this.getTimer().start();
    	}
    	else if (savedState.toPlay() == this.playerNum) {
    		// not a Jack but it's my turn to play a card

    		// delay for up to two seconds; then play
        	sleep((int)(2000*Math.random()));

        	// submit our move to the game object
        	game.sendAction(new P10PlayAction(this));
    	}
    	*/
    }

    /**
     * Returns a subset of cards from the players hand that consist of a valid phase component
     * If no valid component exists, returns an empty deck
     *
     * @param myCards
     * 		the player's hand
     * @param myPhaseNumber
     *      the phase the player is working on
     */
    protected Deck validPhase(Deck myCards, int myPhaseNumber){
        Deck toReturn = new Deck();
        int cards[] = cardsCount(myCards);

        int numSets = 0;                                //actual number and array for switch case to use
        boolean sets[] = new boolean[cards.length];

        int numSets5 = 0;                               //subcomponetns used to build actual array and number
        boolean sets5[] = new boolean[cards.length];
        int numSets4 = 0;
        boolean sets4[] = new boolean[cards.length];
        int numSets3 = 0;
        boolean sets3[] = new boolean[cards.length];
        int numSets2 = 0;
        boolean sets2[] = new boolean[cards.length];
        int numSets1 = 0;
        boolean sets1[] = new boolean[cards.length];
        int numWilds = cards[13];
        for(int i = 0; i < cards.length-2; i++){
            sets5[i] = false;
            sets4[i] = false;
            sets3[i] = false;
            sets2[i] = false;
            sets1[i] = false;
            if (cards[i] >= 5) {
                numSets5++;                              //count the number of sets
                sets5[i] = true;                         //change that value to be true
            }
            else if (cards[i] >= 4) {
                numSets4++;                             //count the number of sets
                sets4[i] = true;                         //change that value to be true
            }
            else if (cards[i] >= 3) {
                numSets3++;                              //count the number of sets
                sets3[i] = true;                         //change that value to be true
            }
            else if (cards[i] >= 2) {
                numSets2++;
                sets2[i] = true;                         //change that value to be true
            }
            else if (cards[i] >= 1) {
                numSets1++;
                sets1[i] = true;                         //change that value to be true
            }
        }


        int num4Runs = 0;
        boolean run4Start[] = new boolean[cards.length];
        for(int i = 0; i < cards.length-3-2; i++){          //minus 3 for consecutive runs, minus 2 for wilds and skips
            run4Start[i] = false;
            if(cards[i] >= 1 && cards[i+1] >= 1 && cards[i+2] >= 1 && cards[i+3] >= 1){ //if 4 consecutive cards
                num4Runs++;
                run4Start[i] = true;                         //change that value to be true
            }
        }

        switch(myPhaseNumber){
            case 1:
                for(int i = 0; i < sets.length-2; i++){
                    int usedWilds = 0;
                    if(sets3[i]){
                        Log.i("numsets", "sets3 "+Integer.toString(i));
                        numSets++;
                        sets[i] = true; //if there are 3 of a kind, there is a set their
                    }
                    else if (sets2[i]) { //if there is NOT a set of 3, but a partial set of 2
                        int potentialUsedWilds = usedWilds+1;
                        if(cards[13] - potentialUsedWilds > 0){//only increment count of sets if there are enough wilds
                            numSets++;
                            Log.i("numsets", "sets2 "+Integer.toString(i));
                            usedWilds++;
                            Log.i("cards13 - used", Integer.toString(cards[13]-usedWilds));
                            sets[i] = true;
                        }
                    }
                    else if (sets1[i]){ //if there is NOT a set of 2 or 3, but a partial set of 1
                        int potentialUsedWilds = usedWilds+2;
                        if(cards[13] - potentialUsedWilds > 0){//only increment count of sets if there are enough wilds
                            numSets++;
                            Log.i("numsets", "sets1 "+Integer.toString(i));
                            usedWilds++;
                            usedWilds++;
                            Log.i("cards13 - used", Integer.toString(cards[13]-usedWilds));
                            sets[i] = true;
                        }
                    }
                }
                if(numSets >= 2){                               //if there are two sets
                    int set1 = -1;      //value of set 1 cards
                    int added1 = 0;     //how many set 1 cards were added
                    int set2 = -1;
                    int added2 = 0;
                    for(int j = 0; j < sets.length; j++){
                        if(sets[j]){
                            if(set1 == -1){
                                set1 = j;
                            }
                            else if (set2 == -1){
                                set2 = j;
                            }
                        }
                    }
                    for(int j = 0; j < myCards.size(); j++){    //for all cards
                        if(toReturn.size() < 6){                //if the phase isnt complete already
                            if(myCards.peekAt(j).getRank().value(1) == set1){
                                if(added1 < 3) {
                                    toReturn.add(myCards.peekAt(j));
                                    added1++;
                                }
                            }
                            else if(myCards.peekAt(j).getRank().value(1) == set2){
                                if(added2 < 3) {
                                    toReturn.add(myCards.peekAt(j));
                                    added2++;
                                }
                            }
                        }
                    }
                    for(int j = 0; j < myCards.size(); j++){    //for all cards
                        if(toReturn.size() < 6){                //if the phase isnt complete already
                            if(myCards.peekAt(j).getRank().value(1) == 13){ //if wild
                                if(added1 < 3) {
                                    Card tempCard = new Card(myCards.peekAt(j));
                                    tempCard.setWildValue(set1);
                                    toReturn.add(tempCard);
                                    added1++;
                                }
                                if(added2 < 3) {
                                    Card tempCard = new Card(myCards.peekAt(j));
                                    tempCard.setWildValue(set2);
                                    toReturn.add(tempCard);
                                    added2++;
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                boolean haveRun = false;
                int[] runPotential = new int[10];
                boolean haveSet = false;
                boolean[] setVal = new boolean[14]; //ignores 0, counts 1-13 for wilds included
                boolean havePair = false;
                boolean[] pairVal = new boolean[14]; //ignores 0, counts 1-13 for wilds included

                //minus 1 to not count skip cards for a set
                for(int i = 0; i < cards.length-1; i++){
                    if(cards[i] >= 3){
                        haveSet = true;
                        setVal[i] = true;
                    }
                    if(cards[i] > 1){
                        havePair = true;
                        pairVal[i] = true;
                    }
                }
                //minus 2 to not check wild/skip, minus 3 because checking next 3 cards
                for(int i = 0; i < cards.length-2-3; i++){
                    int cc0 = 0;
                    int cc1 = 0;
                    int cc2 = 0;
                    int cc3 = 0;
                    if(cards[i] > 0){
                        cc0 = 1;
                    }
                    if(cards[i+1] > 0){
                        cc1 = 1;
                    }
                    if(cards[i+2] > 0){
                        cc2 = 1;
                    }
                    if(cards[i+3] > 0){
                        cc3 = 1;
                    }
                    runPotential[i] = cc0 + cc1 + cc2 + cc3;
                    if(runPotential[i] == 4){
                        haveRun = true;
                    }
                }
                if(haveRun && haveSet){
                    //ignore for now - satisfied by top case - consolidate first part here later
                }
                int numWildsNeeded = 12; //starter value, greater than possible
                int bestI = -1;
                int bestJ = -1;

                //minus two to not check wilds/skips
                for(int i = 0; i < cards.length-2; i++){
                    if(cards[i] + cards[13] >= 3){           //have right cards to make a set
                        int numWildsUsed = 3 - cards[i];
                        if(numWildsUsed < 1) {numWildsUsed = 0;}    //if have more than 3 of set value, need 0 wilds
                        //minus 2 for wild/skip ignoring, minus 3 for run potential
                        for(int j = 0; j < cards.length-2-3; j++){
                            if(i >= j && i <= j+3){                 //if set value is within run
                                if(cards[i] > 3){                   //need 4 or more cards at set value

                                }
                                else {                              //or another wild
                                    numWildsUsed++;
                                }
                            }
                            if(runPotential[j] + cards[13] - numWildsUsed >= 4){ //if enough wilds for set and run
                                numWildsUsed = numWildsUsed + (4 - runPotential[j]);
                                if(numWildsUsed < numWildsNeeded){
                                    bestI = i;  //find best set value
                                    bestJ = j;  //find best runstart value
                                    numWildsNeeded = numWildsUsed;
                                }
                            }
                        }
                    }
                }

                if(bestI != -1 && bestJ != -1){ //if a potential run/set combo was found
                    int added1 = 0;
                    int added2 = 0;
                    int[] added2vals = new int[5];
                    for(int q = 0; q < added2vals.length; q++){
                        added2vals[q] = 0;
                    }
                    int wildsSent = 0;
                    for(int k = 0; k < myCards.size(); k++){    //for all cards
                        if(toReturn.size() < 7){                //if the phase isnt complete already
                            if(myCards.peekAt(k).getRank().value(1) == bestI){
                                if(added1 < 3) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added1++;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ){
                                if(added2 < 4 && added2vals[1] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[1] = 1;                                                    }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+1){
                                if(added2 < 4 && added2vals[2] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[2]= 1;                                                    }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+2){
                                if(added2 < 4 && added2vals[3] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[3]= 1;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+3){
                                if(added2 < 4 && added2vals[4] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[4]= 1;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == 13){
                                if(wildsSent < numWildsNeeded){
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    wildsSent++;
                                }
                            }
                        }
                    }
                }
                break;
            case 3:
                haveRun = false;
                runPotential = new int[10];
                haveSet = false;
                setVal = new boolean[14]; //ignores 0, counts 1-13 for wilds included
                pairVal = new boolean[14]; //ignores 0, counts 1-13 for wilds included

                //minus 1 to not count skip cards for a set
                for(int i = 0; i < cards.length-1; i++){
                    if(cards[i] >= 4){
                        haveSet = true;
                        setVal[i] = true;
                    }
                    if(cards[i] > 1){
                        pairVal[i] = true;
                    }
                }
                //minus 2 to not check wild/skip, minus 3 because checking next 3 cards
                for(int i = 0; i < cards.length-2-3; i++){
                    int cc0 = 0;
                    int cc1 = 0;
                    int cc2 = 0;
                    int cc3 = 0;
                    if(cards[i] > 0){
                        cc0 = 1;
                    }
                    if(cards[i+1] > 0){
                        cc1 = 1;
                    }
                    if(cards[i+2] > 0){
                        cc2 = 1;
                    }
                    if(cards[i+3] > 0){
                        cc3 = 1;
                    }
                    runPotential[i] = cc0 + cc1 + cc2 + cc3;
                    if(runPotential[i] == 4){
                        haveRun = true;
                    }
                }
                if(haveRun && haveSet){
                    //ignore for now - satisfied by top case - consolidate first part here later
                }
                numWildsNeeded = 12; //starter value, greater than possible
                bestI = -1;
                bestJ = -1;

                //minus two to not check wilds/skips
                for(int i = 0; i < cards.length-2; i++){
                    if(cards[i] + cards[13] >= 4){           //have right cards to make a set
                        int numWildsUsed = 4 - cards[i];
                        if(numWildsUsed < 1) {numWildsUsed = 0;}    //if have more than 3 of set value, need 0 wilds
                        //minus 2 for wild/skip ignoring, minus 3 for run potential
                        for(int j = 0; j < cards.length-2-3; j++){
                            if(i >= j && i <= j+3){                 //if set value is within run
                                if(cards[i] > 4){                   //need 5 or more cards at set value

                                }
                                else {                              //or another wild
                                    numWildsUsed++;
                                }
                            }
                            if(runPotential[j] + cards[13] - numWildsUsed >= 4){ //if enough wilds for set and run
                                numWildsUsed = numWildsUsed + (4 - runPotential[j]);
                                if(numWildsUsed < numWildsNeeded){
                                    bestI = i;  //find best set value
                                    bestJ = j;  //find best runstart value
                                    numWildsNeeded = numWildsUsed;
                                }
                            }
                        }
                    }
                }

                if(bestI != -1 && bestJ != -1){ //if a potential run/set combo was found
                    int added1 = 0;
                    int added2 = 0;
                    int[] added2vals = new int[5];
                    for(int q = 0; q < added2vals.length; q++){
                        added2vals[q] = 0;
                    }
                    int wildsSent = 0;
                    for(int k = 0; k < myCards.size(); k++){    //for all cards
                        if(toReturn.size() < 8){                //if the phase isnt complete already
                            if(myCards.peekAt(k).getRank().value(1) == bestI){
                                if(added1 < 4) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added1++;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ){
                                if(added2 < 4 && added2vals[1] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[1] = 1;                                                    }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+1){
                                if(added2 < 4 && added2vals[2] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[2]= 1;                                                    }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+2){
                                if(added2 < 4 && added2vals[3] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[3]= 1;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == bestJ+3){
                                if(added2 < 4 && added2vals[4] != 1) {
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    added2++;
                                    added2vals[4]= 1;
                                }
                            }
                            if(myCards.peekAt(k).getRank().value(1) == 13){
                                if(wildsSent < numWildsNeeded){
                                    Card temp = new Card(myCards.peekAt(k));
                                    toReturn.add(temp);
                                    wildsSent++;
                                }
                            }
                        }
                    }
                }
                break;
            case 4:
                int[] runPotentials = runPots(myCards, 7);
                int bestRunStart = 1;

                for(int i = 1; i < runPotentials.length; i++){
                    if(runPotentials[i] > runPotentials[bestRunStart]){	//find group of cards best fit for a run
                        bestRunStart = i;
                    }
                }

                if(runPotentials[bestRunStart] + cards[13] >= 7){ //if have enough wilds
                    boolean alreadyAdded[] = new boolean[13];
                    for(int i = 1; i < alreadyAdded.length; i++){
                        alreadyAdded[i] = false;
                    }
                    int numWildsAdded = 0;
                    numWildsNeeded = 7-runPotentials[bestRunStart];
                    for(int i = 0; i < myCards.size(); i++){
                        Card temp = new Card(myCards.peekAt(i));
                        int value = temp.getWildValue();
                        if(value < 13) {   //if not wild or skip
                            if ((!alreadyAdded[value]) && value < bestRunStart + 7 && value >= bestRunStart) {
                                toReturn.add(temp);
                                alreadyAdded[value] = true;
                            }
                        }
                        if(value == 13 && numWildsAdded < numWildsNeeded){
                            toReturn.add(temp);
                            numWildsAdded++;
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

                if(runPotentials[bestRunStart] + cards[13] >= 8){ //if have enough wilds
                    boolean alreadyAdded[] = new boolean[13];
                    for(int i = 1; i < alreadyAdded.length; i++){
                        alreadyAdded[i] = false;
                    }
                    int numWildsAdded = 0;
                    numWildsNeeded = 8-runPotentials[bestRunStart];
                    for(int i = 0; i < myCards.size(); i++){
                        Card temp = new Card(myCards.peekAt(i));
                        int value = temp.getWildValue();
                        if(value < 13) {   //if not wild or skip
                            if ((!alreadyAdded[value]) && value < bestRunStart + 8 && value >= bestRunStart) {
                                toReturn.add(temp);
                                alreadyAdded[value] = true;
                            }
                        }
                        if(value == 13 && numWildsAdded < numWildsNeeded){
                            toReturn.add(temp);
                            numWildsAdded++;
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

                if(runPotentials[bestRunStart] + cards[13] >= 9){ //if have enough wilds
                    boolean alreadyAdded[] = new boolean[13];
                    for(int i = 1; i < alreadyAdded.length; i++){
                        alreadyAdded[i] = false;
                    }
                    int numWildsAdded = 0;
                    numWildsNeeded = 9-runPotentials[bestRunStart];
                    for(int i = 0; i < myCards.size(); i++){
                        Card temp = new Card(myCards.peekAt(i));
                        int value = temp.getWildValue();
                        if(value < 13) {   //if not wild or skip
                            if ((!alreadyAdded[value]) && value < bestRunStart + 9 && value >= bestRunStart) {
                                toReturn.add(temp);
                                alreadyAdded[value] = true;
                            }
                        }
                        if(value == 13 && numWildsAdded < numWildsNeeded){
                            toReturn.add(temp);
                            numWildsAdded++;
                        }
                    }
                }
                break;
            case 7:
                for(int i = 0; i < sets.length-2; i++){
                    int usedWilds = 0;
                    if(sets4[i]){
                        //Log.i("numsets", "sets3 "+Integer.toString(i));
                        numSets++;
                        sets[i] = true; //if there are 3 of a kind, there is a set their
                    }
                    else if(sets3[i]){
                        int potentialUsedWilds = usedWilds+1;
                        if(cards[13] - potentialUsedWilds > 0){//only increment count of sets if there are enough wilds
                            numSets++;
                            //Log.i("numsets", "sets3 "+Integer.toString(i));
                            usedWilds++;
                            //Log.i("cards13 - used", Integer.toString(cards[13]-usedWilds));
                            sets[i] = true;
                        }
                    }
                    else if (sets2[i]) { //if there is NOT a set of 4, but a partial set of 2
                        int potentialUsedWilds = usedWilds+2;
                        if(cards[13] - potentialUsedWilds > 0){//only increment count of sets if there are enough wilds
                            numSets++;
                            //Log.i("numsets", "sets2 "+Integer.toString(i));
                            usedWilds++;
                            //Log.i("cards13 - used", Integer.toString(cards[13]-usedWilds));
                            sets[i] = true;
                        }
                    }
                    else if (sets1[i]){ //if there is NOT a set of 2 or 3, but a partial set of 1
                        int potentialUsedWilds = usedWilds+3;
                        if(cards[13] - potentialUsedWilds > 0){//only increment count of sets if there are enough wilds
                            numSets++;
                            Log.i("numsets", "sets1 "+Integer.toString(i));
                            usedWilds++;
                            usedWilds++;
                            Log.i("cards13 - used", Integer.toString(cards[13]-usedWilds));
                            sets[i] = true;
                        }
                    }
                }
                if(numSets >= 2){                               //if there are two sets
                    int set1 = -1;      //value of set 1 cards
                    int added1 = 0;     //how many set 1 cards were added
                    int set2 = -1;
                    int added2 = 0;
                    for(int j = 0; j < sets.length; j++){
                        if(sets[j]){
                            if(set1 == -1){
                                set1 = j;
                            }
                            else if (set2 == -1){
                                set2 = j;
                            }
                        }
                    }
                    for(int j = 0; j < myCards.size(); j++){    //for all cards
                        if(toReturn.size() < 8){                //if the phase isnt complete already
                            if(myCards.peekAt(j).getRank().value(1) == set1){
                                if(added1 < 4) {
                                    toReturn.add(myCards.peekAt(j));
                                    added1++;
                                }
                            }
                            else if(myCards.peekAt(j).getRank().value(1) == set2){
                                if(added2 < 4) {
                                    toReturn.add(myCards.peekAt(j));
                                    added2++;
                                }
                            }
                        }
                    }
                    for(int j = 0; j < myCards.size(); j++){    //for all cards
                        if(toReturn.size() < 8){                //if the phase isnt complete already
                            if(myCards.peekAt(j).getRank().value(1) == 13){ //if wild
                                if(added1 < 4) {
                                    Card tempCard = new Card(myCards.peekAt(j));
                                    tempCard.setWildValue(set1);
                                    toReturn.add(tempCard);
                                    added1++;
                                }
                                if(added2 < 4) {
                                    Card tempCard = new Card(myCards.peekAt(j));
                                    tempCard.setWildValue(set2);
                                    toReturn.add(tempCard);
                                    added2++;
                                }
                            }
                        }
                    }
                }
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;

        }
        Log.i("Card List", "Start");
        for(int i = 0; i < toReturn.size(); i++){
            Log.i("Sending Cards", toReturn.peekAt(i).toString());
        }
        Log.i("Card List", "End");

        return toReturn;
    }

    /**
     * Returns a card location within the hand for discarding
     *
     * @param myCards
     * 		the player's hand
     * @param myPhaseNumber
     *      the phase the player is working on
     */
    protected Card toDiscard(Deck myCards, int myPhaseNumber) {
        Card toReturn;

        toReturn = myCards.peekAt((int)Math.random()*myCards.size());

        return toReturn;
    }

    protected int[] cardsCount(Deck myCards){
        myCards.sortNumerical();
        int variety[] = new int[15]; //indicator of if a card is used in the phase
        for(int i = 0; i < variety.length; i++){
            variety[i] = 0;			//initialized to zero
        }
        for(int i = 0; i < myCards.size(); i++){
            int val = myCards.peekAt(i).getRank().value(1);
            Log.i("Incrementing variety at", Integer.toString(val));
            variety[val]++; //increment the variety at a specific location
        }
        return variety;
    }

    protected P10HitCardAction generateHitCardAction(){
        P10HitCardAction myAction = null;
        for(int u = 0; u < savedState.getHand(playerNum).size(); u++) { //for each card in the hand
            Card c = savedState.getHand(playerNum).peekAt(u);
            Log.i("Comp Hit Card Val", Integer.toString(c.getWildValue()));
            if(c != null) {
                for (int i = 0; i < savedState.getNumberPlayers(); i++) {   //for every player
                    for (int j = 0; j < 2; j++) {                           //for each phase component
                        if (savedState.getPlayedPhase()[i][j].size() != 0) {
                            if (isValidHit(c, i, j)) {                            //if its valid to hit that card at that location
                                myAction = new P10HitCardAction(this, c, i, j);
                            }
                        }
                    }
                }
            }
        }
        return myAction;
    }

    private boolean isValidHit(Card myC, int playerToHit, int phaseToHit){
        //return true; //always assume valid hit for now

        if (savedState.getPhases()[playerToHit] > 3 && savedState.getPhases()[playerToHit] < 7) {//phases 4, 5, 6 being hit on
            phaseToHit = 0; //only one phase comp to hit on
        }

        Card myCard = new Card(myC);

        if(savedState.getPlayedPhase()[playerNum][0].size() == 0){ //if the player has not yet made his own phase - hits are illegal
            return false;
        }
        if(myCard == null){
            return false;
        }

        if(savedState.getPhases()[playerToHit] == 8) { //on phase 8 color is the only thing that matters
            if(savedState.getPlayedPhase()[playerToHit][0].size() != 0){ //if that player has played a phase
                if(myCard.getRank().value(1) == 13){ //if a wildcard
                    return true;					//wildcards are always valid for phase 8
                }
                Color myColor = null;
                for(int i = 0; i < savedState.getPlayedPhase()[playerToHit][0].size(); i++){
                    if(savedState.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit() != Color.Black){ //for not wilds
                        myColor = savedState.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit(); //set color to not black
                    }
                }
                if(myColor == myCard.getSuit()){
                    return true; //return true if colors match
                    //this process will not work if all cards in a phase 8 component are wild. Very rare, fix later
                }
            }
            return false; //otherwise return false if trying to hit on someones phase 8
        }
        else { //if trying to hit on any phase except phase 8
            Deck myDeck = null;
            if(phaseToHit < 2 && phaseToHit >= 0){
                myDeck = savedState.getPlayedPhase()[playerToHit][phaseToHit];
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
