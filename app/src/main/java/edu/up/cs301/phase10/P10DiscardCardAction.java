package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by josephsmoyer on 11/8/17.
 */

public class P10DiscardCardAction extends P10MoveAction {
    /**
     * Constructor for P10MoveAction
     *
     * @param player the player making the move
     */
    public P10DiscardCardAction(GamePlayer player) {
        super(player);
    }
    public boolean isDiscardCard() {
        return true;
    }
}
