package edu.up.cs301.phase10;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by josephsmoyer on 11/8/17.
 */

public class P10DiscardCardAction extends P10MoveAction {

    //Indicates which card the player would like to discard
    public Card toDiscard;
    /**
     * Constructor for P10MoveAction
     *
     * @param player the player making the move
     */
    public P10DiscardCardAction(GamePlayer player, Card myCard) {
        super(player);
        toDiscard = myCard;
    }
    public boolean isDiscardCard() {
        return true;
    }
}
