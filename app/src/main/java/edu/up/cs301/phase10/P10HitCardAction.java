package edu.up.cs301.phase10;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by josephsmoyer on 11/8/17.
 */

public class P10HitCardAction extends P10MoveAction {
    private static final long serialVersionUID = -3107100271012698849L;
    private Card hitCard;
    private int playerToHit;
    private int phaseToHit;
    /**
     * Constructor for P10MoveAction
     *
     * @param player the player making the move
     */
    public P10HitCardAction(GamePlayer player, Card myCard, int playa, int phase) {
        super(player);
        hitCard = myCard;
        playerToHit = playa;
        phaseToHit = phase;
    }
    public boolean isHitCard() {
        return true;
    }

    public Card getHitCard(){
        return hitCard;
    }

    public int getPlayerToHit(){
        return playerToHit;
    }

    public int getPhaseToHit(){
        return phaseToHit;
    }
}
