package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by josephsmoyer on 11/8/17.
 */

public class P10DrawCardAction extends P10MoveAction {

    //will indicate which pile to draw from
    public boolean drawPile;
    /**
     * Constructor for P10MoveAction
     *
     * @param player the player making the move
     */
    public P10DrawCardAction(GamePlayer player, boolean whichPile){
        super(player);
        drawPile = whichPile;
    }

    public boolean isDrawCard() {
        return true;
    }
}
