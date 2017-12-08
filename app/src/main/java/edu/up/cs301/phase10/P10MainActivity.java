package edu.up.cs301.phase10;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * this is the primary activity for Phase10 game
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class P10MainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a Phase10 game for 2-6 players. The default is human vs. computer */
	@Override
	public GameConfig createDefaultConfig() {

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

		//Create some custom background colors
		final int backgroundGreen = Color.rgb(29, 207, 88);

		//Add some fonts
		Typeface tf0 = Typeface.createFromAsset(getAssets(), "mermaid.ttf");
		final Typeface[] tfArr = new Typeface[1];
		tfArr[0] = tf0;

		final Context myContext = this;

		playerTypes.add(new GamePlayerType("Human player (Green)") {
			public GamePlayer createPlayer(String name) {
				return new P10HumanPlayer(name, backgroundGreen, tfArr, myContext);
			}
		});
		/*playerTypes.add(new GamePlayerType("Human player (Yellow)") {
			public GamePlayer createPlayer(String name) {
				return new P10HumanPlayer(name, Color.YELLOW);
			}
		});*/
		playerTypes.add(new GamePlayerType("Computer player (Dumb)") {
			public GamePlayer createPlayer(String name) {
				return new P10DumbComputerPlayer(name);
			}
		});
		playerTypes.add(new GamePlayerType("Computer player (Moderate)") {
			public GamePlayer createPlayer(String name) {
				return new P10ModerateComputerPlayer(name);
			}
		});

		playerTypes.add(new GamePlayerType("Computer player (genius)") {
			public GamePlayer createPlayer(String name) {
				return new P10GeniusComputerPlayer(name);
			}
		});



		// Create a game configuration class for Phase10
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 6, "Phase10", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("1Computer", 2);
		defaultConfig.addPlayer("2Computer", 2);
		defaultConfig.addPlayer("3Computer", 2);
		defaultConfig.addPlayer("4Computer", 2);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame() {
		int number = this.scrapeData().getNumPlayers();
		//String[] names = this.scrapeData().getSelNames();
		String numbers = Integer.toString(number);
		Log.i("NumberPlayers", numbers);
		return new P10LocalGame(number, this);
	}
}
