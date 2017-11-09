package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * A P10PlayAction is an action that represents playing a card on the "up"
 * pile.
 * 
 * @author Steven R. Vegdahl
 * @version 31 July 2002
 */
public class P10PlayAction extends P10MoveAction
{
	private static final long serialVersionUID = 3250639793499599047L;

	/**
     * Constructor for the SJPlayMoveAction class.
     * 
     * @param player  the player making the move
     */
    public P10PlayAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

    /**
     * @return
     * 		whether this action is a "play" move
     */
    public boolean isPlay() {
        return true;
    }
    
}
