package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A game-move object that a Phase 10 player sends to the game to make
 * a move.
 * 
 * @author Steven R. Vegdahl
 * @author Trenton Langer
 * @version November 2017
 */
public abstract class P10MoveAction extends GameAction {
	
	private static final long serialVersionUID = -3107100271012188849L;

    /**
     * Constructor for P10MoveAction
     *
     * @param player the player making the move
     */
    public P10MoveAction(GamePlayer player)
    {
        // invoke superclass constructor to set source
        super(player);
    }
    
    /**
     * @return
     * 		whether the move was a slap
     */
    public boolean isMakePhase() {
    	return false;
    }
    
    /**
     * @return
     * 		whether the move was a "play"
     */
    public boolean isPlay() {
    	return false;
    }
    public boolean isHitCard() {
        return false;
    }
    public boolean isDrawCard(){return false;}
    public boolean isDiscardCard(){return false;}
}
