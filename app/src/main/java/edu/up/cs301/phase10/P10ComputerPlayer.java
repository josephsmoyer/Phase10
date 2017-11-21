package edu.up.cs301.phase10;

import android.util.Log;

import edu.up.cs301.card.Card;
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

        switch(myPhaseNumber){
            case 1:
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
                /*
                for(int i = 0; i < myCards.size(); i++){ //for all cards
                    int count = 0;
                    for(int j = 0; j < myCards.size(); j++){ //compare to each subsequent card
                        if(myCards.peekAt(i).equals(myCards.peekAt(j))){
                            count++;                       //declare match that card
                        }
                    }
                    if(count >= 3){ //if have a set of 3
                        int subCount = 0;
                        for(int l = 0; l < myCards.size(); l++){    //for all the cards in hand
                            if(subCount < 3) {  //for the first three cards in the set
                                if(myCards.peekAt(i).equals(myCards.peekAt(l))) {
                                    toReturn.add(myCards.removeCard(l)); //add card to return pile
                                    subCount++;
                                }
                            }
                            else{
                                break;                          //if 3 cards are put together, break
                            }
                        }
                    }
                }
                */
                break;
            case 2:
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
}
