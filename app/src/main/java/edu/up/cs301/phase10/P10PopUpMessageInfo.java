package edu.up.cs301.phase10;

import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Trenton on 12/7/2017.
 */

public class P10PopUpMessageInfo extends GameInfo {

    // to satisfy Serializable interface
    private static final long serialVersionUID = 7165334826661353190L;

    String myMessage;

    P10PopUpMessageInfo(String myString){
        myMessage = myString;
    }

    public String getMyMessage(){
        return myMessage;
    }
}
