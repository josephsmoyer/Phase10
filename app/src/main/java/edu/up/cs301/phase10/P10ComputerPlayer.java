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
                for(int i = 0; i < myCards.size(); i++){ //for all cards
                    boolean[] match = new boolean[myCards.size()]; //holds which cards match that value
                    for(int k = 0; k < match.length; k++){
                        match[k] = false;
                    }
                    match[i] = true;
                    for(int j = 0; j < myCards.size(); j++){ //compare to each subsequent card
                        if(myCards.peekAt(i).equals(myCards.peekAt(j))){
                            match[j] = true;                        //declare match that card
                        }
                    }
                    int count = 0;
                    for(int k = 0; k < match.length; k++){
                        if(match[k]){
                            count++;                                //count how many similar cards
                            Log.i("Total Matches", Integer.toString(count));
                        }
                    }
                    if(count >= 3){                                 //if have a set of 3
                        for(int l = 0; l < myCards.size(); l++){    //for all the cards in hand
                            if(match[l]) {                          //if they are part of the set
                                if(toReturn.size() < 3) {           //if less than 3 cards have been put together
                                    toReturn.add(myCards.peekAt(l)); //add card
                                }
                                else{
                                    break;                          //if 3 cards are put together, break
                                }
                            }
                        }
                    }
                }
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
}
