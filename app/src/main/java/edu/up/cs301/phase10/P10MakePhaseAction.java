package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * A P10MakePhaseAction is an action that represents slapping the card that is
 * on the "up" pile.
 * 
 * @author Steven R. Vegdahl
 * @version 31 July 2002
 */
public class P10MakePhaseAction extends P10MoveAction
{
	private static final long serialVersionUID = 2134321631283669359L;

	/**
     * Constructor for the SJSlapMoveAction class.
     * 
     * @param player  the player making the move
     */
    public P10MakePhaseAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

    /**
	 * @return whether this action is a "slap" move
     */
    public boolean isMakePhase() {
        return true;
    }
    
}
