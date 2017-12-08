package edu.up.cs301.phase10;

import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Trenton on 12/8/2017.
 */

public class P10ToastMessageInfo extends GameInfo {
    // to satisfy Serializable interface
    private static final long serialVersionUID = 7165753826661353190L;

    String myMessage;
    String myCard;

    P10ToastMessageInfo(String myString){
        myMessage = myString;
        myCard = null;
    }

    P10ToastMessageInfo(String myString, String myC){
        myMessage = myString;
        myCard = myC;
    }

    public String getMyMessage(){
        return myMessage;
    }

    public String getMyCard() { return myCard; }
}
