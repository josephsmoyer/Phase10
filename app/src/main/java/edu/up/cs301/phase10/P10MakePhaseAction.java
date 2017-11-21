package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * A P10MakePhaseAction is an action that represents slapping the card that is
 * on the "up" pile.
 * 
 * @author Trenton Langer
 * @version November 2017
 */
public class P10MakePhaseAction extends P10MoveAction
{
	private static final long serialVersionUID = 2134321631283669359L;

    private Deck phase;


	/**
     * Constructor for the P10MakePhaseAction class.
     * 
     * @param player
     *          the player making the move
     * @param myCards
     *          the cards consisting of the phase component
     */
    public P10MakePhaseAction(GamePlayer player, Deck myCards)
    {
        // initialize the source with the superclass constructor
        super(player);
        phase = new Deck(myCards);
    }

    /**
	 * @return whether this action is a make phase
     */
    public boolean isMakePhase() {
        return true;
    }

    public Deck getPhase(){
        return phase;
    }

    
}
