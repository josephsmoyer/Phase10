package edu.up.cs301.phase10;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by sheriffsoco on 12/6/17.
 */

public class P10SkipPlayerAction extends P10MoveAction {

    private static final long serialVersionUID = 3250639793489588049L;
    public int playerID;

    public P10SkipPlayerAction(GamePlayer player, int idx) {
        super(player);
        playerID = idx;
    }

    public boolean isSkipPlayer() { return true; }

    public int getPlayerID() { return playerID; }
}
