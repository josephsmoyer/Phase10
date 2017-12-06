package edu.up.cs301.phase10;

import android.util.Log;

import edu.up.cs301.card.Card;
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
     * callback method, called when we receive a message, typicallly from
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

        int numSets = 0;
        boolean sets[] = new boolean[cards.length];
        for(int i = 0; i < cards.length; i++){
            sets[i] = false;
            if(cards[i] >= 3){
                numSets++;                              //count the number of sets
                sets[i] = true;                         //change that value to be true
            }
        }

        int numRuns = 0;
        boolean runStart[] = new boolean[cards.length];
        for(int i = 0; i < cards.length-3; i++){
            runStart[i] = false;
            if(cards[i] >= 1 && cards[i+1] >= 1 && cards[i+2] >= 1 && cards[i+3] >= 1){ //if 4 consecutive cards
                numRuns++;
                runStart[i] = true;                         //change that value to be true
            }
        }

        switch(myPhaseNumber){
            case 1:
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
                }
                break;
            case 2:
                if(numRuns >= 1 && numSets >= 1){
                    for(int i = 0; i < sets.length; i++){ //for all possible set values
                        if(sets[i]){                        //if there is a set
                            for(int j = 0; j < runStart.length; j++){ //for all possible run starts
                                if(runStart[j]){ //if there is a run
                                    if(i != j && i != j+1 &&i != j+2 &&i != j+3) { //if set is not inside run
                                        int added1 = 0;
                                        int added2 = 0;
                                        int[] added2vals = new int[5];
                                        for(int q = 0; q < added2vals.length; q++){
                                            added2vals[q] = 0;
                                        }
                                        for(int k = 0; k < myCards.size(); k++){    //for all cards
                                            if(toReturn.size() < 7){                //if the phase isnt complete already
                                                if(myCards.peekAt(k).getRank().value(1) == i){
                                                    if(added1 < 3) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added1++;
                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j){
                                                    if(added2 < 4 && added2vals[1] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[1] = 1;                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+1){
                                                    if(added2 < 4 && added2vals[2] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[2]= 1;                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+2){
                                                    if(added2 < 4 && added2vals[3] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[3]= 1;
                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+3){
                                                    if(added2 < 4 && added2vals[4] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[4]= 1;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        int added1 = 0;
                                        int added2 = 0;
                                        int[] added2vals = new int[5];
                                        for(int q = 0; q < added2vals.length; q++){
                                            added2vals[q] = 0;
                                        }
                                        for(int k = 0; k < myCards.size(); k++){    //for all cards
                                            if(toReturn.size() < 7){                //if the phase isnt complete already
                                                if(myCards.peekAt(k).getRank().value(1) == i){
                                                    if(added1 < 3) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added1++;
                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j){
                                                    if(added2 < 4 && added2vals[1] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[1] = 1;                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+1){
                                                    if(added2 < 4 && added2vals[2] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[2]= 1;                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+2){
                                                    if(added2 < 4 && added2vals[3] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[3]= 1;
                                                    }
                                                }
                                                if(myCards.peekAt(k).getRank().value(1) == j+3){
                                                    if(added2 < 4 && added2vals[4] != 1) {
                                                        toReturn.add(myCards.peekAt(k));
                                                        added2++;
                                                        added2vals[4]= 1;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
        int variety[] = new int[14]; //indicator of if a card is used in the phase
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
        if(myC == null){
            return false;
        }

        Card myCard = new Card(myC.getRank(), myC.getSuit());

        if(savedState.getPlayedPhase()[playerNum][0].size() == 0){ //if the player has not yet made his own phase - hits are illegal
            return false;
        }

        if(savedState.getPhases()[playerToHit] == 8) { //on phase 8 color is the only thing that matters
            if(savedState.getPlayedPhase()[playerToHit][0].size() != 0){ //if that player has played a phase
                if(savedState.getPlayedPhase()[playerToHit][0].peekAtTopCard().getSuit() == myCard.getSuit()){
                    return true; //return true if colors match
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
                    if(myDeck.peekAt(i).getRank().value(1) != myDeck.peekAt(i+1).getRank().value(1)){
                        set = false;
                    }
                    else if(myDeck.peekAt(i).getRank().value(1) != (myDeck.peekAt(i+1).getRank().value(1)-1)){
                        run = false;
                    }
                }
                if(set){
                    if(myCard.getRank().value(1) == myDeck.peekAt(0).getRank().value(1)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else if(run){
                    if(myCard.getRank().value(1) == (myDeck.maxMin(false)-1)){
                        return true;
                    }
                    if(myCard.getRank().value(1) == (myDeck.maxMin(true))+1){
                        return true;
                    }
                    else{
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
}
