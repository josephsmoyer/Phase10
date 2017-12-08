package edu.up.cs301.phase10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;
import edu.up.cs301.game.util.MessageBox;

/**
 * A GUI that allows a human to play Slapjack. Moves are made by clicking
 * regions on a surface. Presently, it is laid out for landscape orientation.
 * If the device is held in portrait mode, the cards will be very long and
 * skinny.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class P10HumanPlayer extends GameHumanPlayer implements Animator {

	// sizes and locations of card decks and cards, expressed as percentages
	// of the screen height and width
	private final static float CARD_HEIGHT_PERCENT = 15; // height of a card
	private final static float CARD_WIDTH_PERCENT = 6.375f; // width of a card
	private final static float LEFT_BORDER_PERCENT = 2; // width of left border
	private final static float RIGHT_BORDER_PERCENT = 2; // width of right border
	private final static float VERTICAL_BORDER_PERCENT = 4; // width of top/bottom borders
	private final static float HOR_OVERLAP = 2; //horizontal overlap to still see number
	private final static float VER_OVERLAP = 2; //Vertical overlap to still see number
	private final static float SMALL_CARD_HEIGHT_PERCENT = 5;
	private final static float SMALL_CARD_WIDTH_PERCENT = 2.1246f;
	private final static float SMALL_HOR_OVERLAP = 2; //horizontal overlap to still see number
	private final static float SMALL_VER_OVERLAP = 2; //Vertical overlap to still see number

	// our game state
	protected P10State state;

	// our activity
	private Activity myActivity;

	//our activity context
	private Context myContext;
	private String selectCard;

	// the amination surface
	private AnimationSurface surface;

	// the background color
	private int backgroundColor;
	private RectF[] rects = new RectF[11];
	private int[] selectedCards = new int[12]; //change to 11 but array out of bounds

	//Nonchanging deck locations
	private RectF discardLocation;
	private RectF drawLocation;
	//Nonchanging phase lay down locations
	private RectF[] phaseLocs = new RectF[2]; //2 for the human player
	//Changing Phase lay down locations
	private RectF[][] computerPhaseLocs = new RectF[5][2];
	//Hand Locations for computer players
	//private RectF[] computerHandLocs = new RectF[5];
	//Changing turn indicator locations
	private RectF turnLocation = new RectF();
	private RectF[] computerTurnLocation = new RectF[5];
	Bitmap[] imgBitmap = new Bitmap[3];
	int[] imgArr = new int[7];
	//fonts
	Typeface[] tf = new Typeface[1];

	//indicator for if score/phase info should be shown
	private boolean showScores;
	private RectF scoreRect;

	//indicator for if manual should be shown
	private boolean showHelp;
	private RectF helpRect;
	private RectF closeHelpRect;

	//card holder for if its a skip card
	Card skipCard = null;

	/**
	 * constructor
	 *
	 * @param name
	 * 		the player's name
	 * @param bkColor
	 * 		the background color
	 */
	public P10HumanPlayer(String name, int bkColor, Typeface[] tfs, Context myContext) {
		super(name);
		backgroundColor = bkColor;
		tf = tfs;
		for (int i = 0; i < 11; i++) {
			//selectedCards list of ints for each card in hand
			//-1: Card doesn't exist in array
			//0: Card is unselected
			//1: Card is selected
			selectedCards[i] = -1;
		}
		for(int i = 0; i < phaseLocs.length; i++){
			phaseLocs[i] = null;
		}
		showScores = false;
		showHelp = false;
		imgArr[0] = R.drawable.stats_base;
		imgArr[1] = R.drawable.phases;
		imgArr[2] = R.drawable.scores;
		imgArr[3] = R.drawable.manual;
		imgArr[4] = R.mipmap.cog;
		imgArr[5] = R.drawable.help_icon;
		imgArr[6] = R.drawable.manual;

		this.myContext = myContext;
		selectCard = null;
	}

	/**
	 * callback method: we have received a message from the game
	 *
	 * @param info
	 * 		the message we have received from the game
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		if(info instanceof  P10PopUpMessageInfo){
			//Log.i("REceived error info", "sdfd");
			P10PopUpMessageInfo myInfo = (P10PopUpMessageInfo) info;
			String myMessage = myInfo.getMyMessage();
			MessageBox.popUpMessage(myMessage, myActivity);
		}
		if(info instanceof  P10ToastMessageInfo){
			//Log.i("REceived error info", "sdfd");
			P10ToastMessageInfo myInfo = (P10ToastMessageInfo) info;
			String myMessage = myInfo.getMyMessage();
			if(myInfo.myCard == null) {
				//if the toast message contains no card, display it
				Toast.makeText(myContext, myMessage, Toast.LENGTH_SHORT).show();
			}
			else {	//if the toast message contains a card
				Toast.makeText(myContext, myMessage, Toast.LENGTH_SHORT).show();
				selectCard = myInfo.getMyCard();	//select that card
			}
		}

		Log.i("P10HumanPlayer", "receiving updated state ("+info.getClass()+")");
		if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
			// if we had an out-of-turn or illegal move, flash the screen
			surface.flash(Color.RED, 50);
		}
		else if (!(info instanceof P10State)) {
			// otherwise, if it's not a game-state message, ignore
			return;
		}
		else {
			// it's a game-state object: update the state. Since we have an animation
			// going, there is no need to explicitly display anything. That will happen
			// at the next animation-tick, which should occur within 1/20 of a second
			this.state = (P10State)info;
			Log.i("human player", "receiving");
		}
	}

	/**
	 * call-back method: called whenever the GUI has changed (e.g., at the beginning
	 * of the game, or when the screen orientation changes).
	 *
	 * @param activity
	 * 		the current activity
	 */
	public void setAsGui(GameMainActivity activity) {

		// remember the activity
		myActivity = activity;

		// Load the layout resource for the new configuration
		activity.setContentView(R.layout.sj_human_player);

		// link the animator (this object) to the animation surface
		surface = (AnimationSurface) myActivity
				.findViewById(R.id.animation_surface);
		surface.setAnimator(this);

		// read in the card images
		Card.initImages(activity);

		// if the state is not null, simulate having just received the state so that
		// any state-related processing is done
		if (state != null) {
			receiveInfo(state);
		}
	}

	/**
	 * @return the top GUI view
	 */
	@Override
	public View getTopView() {
		return myActivity.findViewById(R.id.top_gui_layout);
	}

	/**
	 * @return
	 * 		the amimation interval, in milliseconds
	 */
	public int interval() {
		// 1/20 of a second
		return 50;
	}

	/**
	 * @return
	 * 		the background color
	 */
	public int backgroundColor() {
		return backgroundColor;
	}

	/**
	 * @return
	 * 		whether the animation should be paused
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * @return
	 * 		whether the animation should be terminated
	 */
	public boolean doQuit() {
		return false;
	}

	/**
	 * callback-method: we have gotten an animation "tick"; redraw the screen image:
	 * - the middle deck, with the top card face-up, others face-down
	 * - the two players' decks, with all cards face-down
	 * - a red bar to indicate whose turn it is
	 *
	 * @param g
	 * 		the canvas on which we are to draw
	 */
	public void tick(Canvas g) {
		Paint myPaint = new Paint();
		myPaint.setColor(Color.YELLOW);

		Paint scorePaint = new Paint();
		scorePaint.setColor(Color.CYAN);

		Paint helpPaint = new Paint();
		helpPaint.setColor(Color.rgb(255, 180, 0));

		Paint coverPaint = new Paint();
		coverPaint.setColor(Color.GRAY);

		Paint needToDrawPaint = new Paint();
		needToDrawPaint.setColor(Color.rgb(255, 0, 0));
		needToDrawPaint.setTextSize(35);

		Paint phaseTextPaint = new Paint();
		phaseTextPaint.setColor(Color.BLACK);
		phaseTextPaint.setTextSize(40);

		if(state == null){
			return;
		}
		else {

			float width = surface.getWidth();
			float height = surface.getHeight();

			//draw scores on top of everything else if user wants to see them
			if(showScores){

				//for bitmap drawings used in info page
				for (int i = 0; i < 3; i++) {
					imgBitmap[i] = BitmapFactory.decodeResource(myActivity.getResources(), imgArr[i]);
				}

				//float scoreX = width/8;
				Paint textPaint = new Paint();
				textPaint.setColor(Color.WHITE);
				textPaint.setTextSize(55);
				textPaint.setTypeface(tf[0]);


				//placeholder paint
				Paint p = new Paint();
				p.setColor(Color.BLACK);

				//Scores page
				Rect r = new Rect(0,0,imgBitmap[0].getWidth(),imgBitmap[0].getHeight());
				RectF s = new RectF(0, 0, width, height);
				g.drawBitmap(imgBitmap[0], r, s, p);

				//phase text per player
				float myX = 1318f; 	//specific coordinates according
				float myY = 242f;		//to Adobe Illustrator
				int[] phasePl = state.getPhasePlace();
				int[] scorePl = state.getScorePlace();
				for(int i = 0; i < state.getNumberPlayers(); i++) {
					if(allPlayerNames[phasePl[i]].length() > 5) {
						g.drawText(""+allPlayerNames[phasePl[i]].substring(0, 5)+": "+Integer.toString(state.getPhases()[phasePl[i]]), myX, myY, textPaint);
					}
					else {
						g.drawText(""+allPlayerNames[phasePl[i]]+": "+Integer.toString(state.getPhases()[phasePl[i]]), myX, myY, textPaint);
					}
					//scoreX = Math.max(scoreX,);
					myY = myY + 66;
				}

				//score text per player
				myX = 1318f; 	//specific coordinates according
				myY = 738f;		//to Adobe Illustrator
				for(int i = 0; i < state.getNumberPlayers(); i++) {
					if(allPlayerNames[scorePl[i]].length() > 5) {
						g.drawText(""+allPlayerNames[scorePl[i]].substring(0, 5)+": "+Integer.toString(state.getScores()[scorePl[i]]), myX, myY, textPaint);
					}
					else {
						g.drawText(""+allPlayerNames[scorePl[i]]+": "+Integer.toString(state.getScores()[scorePl[i]]), myX, myY, textPaint);
					}
					//scoreX = Math.max(scoreX,);
					myY = myY + 66;
				}
			}
			else if (showHelp){
				//for bitmap drawing used in manual
				imgBitmap[0] = BitmapFactory.decodeResource(myActivity.getResources(), imgArr[6]);

				//placeholder paint
				Paint p = new Paint();
				p.setColor(Color.BLACK);

				//draw manual
				Rect r = new Rect(0,0,imgBitmap[0].getWidth(),imgBitmap[0].getHeight());
				RectF s = new RectF(0, 0, width, height);
				g.drawBitmap(imgBitmap[0], r, s, p);

				closeHelpRect = new RectF(1910,18,2030,1262);
				//g.drawRect(closeHelpRect, helpPaint); To find perfect hitbox
			}
			else {
				height = surface.getHeight();
				width = surface.getWidth();
				//if the players hand is not initialized
				if (state.getHand(playerNum) == null) return;
				//variables for rect creation
				float rectLeft;
				float rectRight;
				float rectTop;
				float rectBottom;

				int length = state.getHand(playerNum).size();
				//Log.i("Hand Length", Integer.toString(length));

				//set up phase area
				for (int i = 0; i < 2; i++) { //expand to all phase areas later
					phaseLocs[i] = getPhaseLoc(i);
					g.drawRect(phaseLocs[i], myPaint);

					// draw phase number and instructions for phase
					int p = Integer.parseInt(Integer.toString(state.getPhases()[playerNum]));
					if (i == 0) {
						g.drawText("Phase " + p, phaseLocs[i].left, phaseLocs[i].top-10, phaseTextPaint);
					}
					switch (p) {
						case 1:
							g.drawText("Set of Three", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							break;
						case 2:
							if (i == 0) {
								g.drawText("Set of Three", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							if (i == 1) {
								g.drawText("Run of Four", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 3:
							if (i == 0) {
								g.drawText("Set of Four", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							if (i == 1) {
								g.drawText("Run of Four", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 4:
							if (i == 0) {
								g.drawText("Run of Seven", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 5:
							if (i == 0) {
								g.drawText("Run of Eight", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 6:
							if (i == 0) {
								g.drawText("Run of Nine", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 7:
							g.drawText("Set of Four", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							break;
						case 8:
							if (i == 0) {
								g.drawText("Seven of One Color", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 9:
							if (i == 0) {
								g.drawText("Set of Five", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							if (i == 1) {
								g.drawText("Set of Two", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 10:
							if (i == 0) {
								g.drawText("Set of Five", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							if (i == 1) {
								g.drawText("Set of Three", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							}
							break;
						case 11:
							g.drawText("Game Over!", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
							break;
						default:
							g.drawText("Error", phaseLocs[i].centerX(), phaseLocs[i].centerY(), phaseTextPaint);
					}
				}

				//Create AI hands
				int players = state.getNumberPlayers();
				if (players < 5) {
					float padding = (100 - (players - 1) * (SMALL_CARD_WIDTH_PERCENT)) / players;
					rectTop = (-10) * height / 100f;
					rectBottom = rectTop + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					for (int i = 0; i < players - 1; i++) {
						//Card
						rectLeft = (padding + (i * (SMALL_CARD_WIDTH_PERCENT + padding))) * width / 100;
						rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
						RectF myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
						//save hand location to use for onTouch events
						//computerHandLocs[i] = myRect;
						g.save();
						g.rotate(180, rectLeft + (SMALL_CARD_WIDTH_PERCENT / 2) * width / 100, 0);
						drawCard(g, myRect, Card.fromString("1x"));
						g.restore();

						//phase location backgrounds
						float rectTop1 = rectTop + 3 * (SMALL_CARD_HEIGHT_PERCENT + SMALL_VER_OVERLAP) * height / 100f;
						float rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
						float rectLeft1 = rectLeft - width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP) / 2) / 10 - width * 3.5f / 100;
						float rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
						RectF myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						g.drawRect(myLoc, myPaint);
						computerPhaseLocs[(i+playerNum)%(players-1)][0] = myLoc;
						rectTop1 = rectTop + 4 * (SMALL_CARD_HEIGHT_PERCENT + SMALL_VER_OVERLAP) * height / 100f;
						rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
						rectLeft1 = rectLeft - width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP) / 2) / 10 - width * 3.5f / 100;
						rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
						myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						g.drawRect(myLoc, myPaint);
						computerPhaseLocs[(i+playerNum)%(players-1)][1] = myLoc;

						//turn indicator
						rectTop1 = rectTop + (10 * height / 100f);
						rectBottom1 = rectTop1 + (2.5f * height / 100f);
						myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						computerTurnLocation[(i+playerNum)%(players-1)] = myLoc;
					}
				} else {
					float rectTop1;
					float rectBottom1;
					float rectLeft1;
					float rectRight1;

					//left card
					rectLeft = (-SMALL_CARD_WIDTH_PERCENT / 2) * width / 100;
					rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
					rectTop = (25) * height / 100f;
					rectBottom = rectTop + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					RectF myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
					//save hand location to use for onTouch events
					//computerHandLocs[0] = myRect;
					g.save();
					g.rotate(90, 0, (35) * height / 100);
					drawCard(g, myRect, Card.fromString("1x"));
					g.restore();

					//phase location
					rectTop1 = rectTop + height * (SMALL_CARD_HEIGHT_PERCENT + 2 * SMALL_VER_OVERLAP) / 100;
					rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					rectLeft1 = rectLeft + width * 5 * (SMALL_CARD_WIDTH_PERCENT) / 100;
					rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
					RectF myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
					g.drawRect(myLoc, myPaint);
					computerPhaseLocs[playerNum%(players-1)][0] = myLoc;
					rectTop1 = rectTop + 1.75f * height * (SMALL_CARD_HEIGHT_PERCENT + 2 * SMALL_VER_OVERLAP) / 100;
					rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					rectLeft1 = rectLeft + width * 5 * (SMALL_CARD_WIDTH_PERCENT) / 100;
					rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
					myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
					g.drawRect(myLoc, myPaint);
					computerPhaseLocs[playerNum%(players-1)][1] = myLoc;

					//turn indicator
					//Still really bad turn indicator code, but w/e
					rectTop1 = rectTop;
					rectBottom1 = rectTop1 + 2*width*(7*(SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP))/10;
					rectLeft1 = 0;
					rectRight1 = (2.5f*height/100f);
					myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);        //Rush for beta code
					computerTurnLocation[playerNum%(players-1)] = myLoc;

					//Spacing for top row
					float padding = (100 - (players - 3) * (SMALL_CARD_WIDTH_PERCENT)) / (players - 2);
					rectTop = (-10) * height / 100f;
					rectBottom = rectTop + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;

					//Top row cards
					for (int i = 1; i < players - 2; i++) {
						//card
						rectLeft = (padding + ((i - 1) * (SMALL_CARD_WIDTH_PERCENT + padding))) * width / 100;
						rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
						myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
						//save hand location to use for onTouch events
						//computerHandLocs[i] = myRect;
						g.save();
						g.rotate(180, rectLeft + (SMALL_CARD_WIDTH_PERCENT / 2) * width / 100, 0);
						drawCard(g, myRect, Card.fromString("1x"));
						g.restore();

						//phase location
						rectTop1 = rectTop + 3 * (SMALL_CARD_HEIGHT_PERCENT + SMALL_VER_OVERLAP) * height / 100f;
						rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
						rectLeft1 = rectLeft - width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP) / 2) / 10 - width * 3.5f / 100;
						rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
						myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						g.drawRect(myLoc, myPaint);
						computerPhaseLocs[(i+playerNum)%(players-1)][0] = myLoc;
						rectTop1 = rectTop + 4 * (SMALL_CARD_HEIGHT_PERCENT + SMALL_VER_OVERLAP) * height / 100f;
						rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
						rectLeft1 = rectLeft - width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP) / 2) / 10 - width * 3.5f / 100;
						rectRight1 = rectLeft1 + 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
						myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						g.drawRect(myLoc, myPaint);
						computerPhaseLocs[(i+playerNum)%(players-1)][1] = myLoc;

						//turn indicator
						rectTop1 = rectTop + (10 * height / 100f);
						rectBottom1 = rectTop1 + (2.5f * height / 100f);
						myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
						computerTurnLocation[(i+playerNum)%(players-1)] = myLoc;
					}

					//right card
					rectLeft = (100 - SMALL_CARD_WIDTH_PERCENT / 2) * width / 100;
					rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
					rectTop = (25) * height / 100f;
					rectBottom = rectTop + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
					//save hand location to use for onTouch events
					//computerHandLocs[players-2] = myRect;
					g.save();
					g.rotate(-90, width, (35) * height / 100);
					drawCard(g, myRect, Card.fromString("1x"));
					g.restore();

					//phase location
					rectTop1 = rectTop + height * (SMALL_CARD_HEIGHT_PERCENT + 2 * SMALL_VER_OVERLAP) / 100;
					rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					rectRight1 = rectRight - width * 5 * (SMALL_CARD_WIDTH_PERCENT) / 100;
					rectLeft1 = rectRight1 - 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
					myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
					g.drawRect(myLoc, myPaint);
					computerPhaseLocs[(players-2+playerNum)%(players-1)][0] = myLoc;
					rectTop1 = rectTop + 1.75f * height * (SMALL_CARD_HEIGHT_PERCENT + 2 * SMALL_VER_OVERLAP) / 100;
					rectBottom1 = rectTop1 + (SMALL_CARD_HEIGHT_PERCENT) * height / 100f;
					rectRight1 = rectRight - width * 5 * (SMALL_CARD_WIDTH_PERCENT) / 100;
					rectLeft1 = rectRight1 - 2 * width * (7 * (SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP)) / 10;
					myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);
					g.drawRect(myLoc, myPaint);
					computerPhaseLocs[(players-2+playerNum)%(players-1)][1] = myLoc;


					//turn indicator
					//Still really bad turn indicator code, but w/e
					rectTop1 = rectTop;
					rectBottom1 = rectTop1 + 2*width*(7*(SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP))/10;
					rectLeft1 = width - (2.5f*height/100f);
					rectRight1 = width;
					myLoc = new RectF(rectLeft1, rectTop1, rectRight1, rectBottom1);        //Rush for beta code
					computerTurnLocation[(players-2+playerNum)%(players-1)] = myLoc;

				}

				//set up discard/draw pile locations
				float start = (100 - (2 * CARD_WIDTH_PERCENT + LEFT_BORDER_PERCENT)) / 2;
				rectLeft = (start) * width / 100;
				rectRight = rectLeft + width * CARD_WIDTH_PERCENT / 100;
				rectTop = (50 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT / 2) * height / 100f;
				rectBottom = (50 - VERTICAL_BORDER_PERCENT + CARD_HEIGHT_PERCENT / 2) * height / 100f;
				discardLocation = new RectF(rectLeft, rectTop, rectRight, rectBottom);
				start = (100 - (2 * CARD_WIDTH_PERCENT + LEFT_BORDER_PERCENT)) / 2;
				rectLeft = (start + LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100;
				rectRight = rectLeft + width * CARD_WIDTH_PERCENT / 100;
				rectTop = (50 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT / 2) * height / 100f;
				rectBottom = (50 - VERTICAL_BORDER_PERCENT + CARD_HEIGHT_PERCENT / 2) * height / 100f;
				drawLocation = new RectF(rectLeft, rectTop, rectRight, rectBottom);

				drawCard(g, discardLocation, state.peekDiscardCard());
				drawCard(g, drawLocation, Card.fromString("1x"));

				if(state.getShouldDraw() && state.getToPlay() == playerNum){
					//if the player should draw, and its the humans turn
					float rectLeft1 = (start + LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100;
					float rectRight1 = rectLeft + width * CARD_WIDTH_PERCENT / 100;
					float rectTop1 = (50 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT / 2) * height / 100f;
					float rectBottom1 = (50 - VERTICAL_BORDER_PERCENT + CARD_HEIGHT_PERCENT / 2) * height / 100f;
					float x, y;
					x = rectRight + (rectRight1-rectLeft1)/2;
					y = rectTop1 + (rectBottom1-rectTop1)/2;
					float radius = 25;
					g.drawCircle(x, y, radius, needToDrawPaint);
					g.drawText("Remember to Draw!", x, y+(radius*3), needToDrawPaint);
				}

				//start of all the possible cards as neither selected or not
				for (int i = length; i < 11; i++) {
					selectedCards[i] = -1;
				}
				//Create the rects and locations for the players cards in hand
				start = (100 - (length * (LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) - LEFT_BORDER_PERCENT)) / 2;
				//state.getHand(playerNum).sortNumerical();
				for (int i = 0; i < length; i++) {
					if (selectedCards[i] == -1) {
						selectedCards[i]++;
					}
					if(selectCard != null){
						if(state.getHand(playerNum).peekAt(i).toString().equals(selectCard)){
							//Log.i("Match Select", selectCard);
							selectedCards[i]++;			//"select" the card in the players hand
							selectCard = null;			//return selectCard to null, so only one instance gets highlighted
						}
					}
					rectLeft = (start + (i * (LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT))) * width / 100;
					rectRight = rectLeft + width * CARD_WIDTH_PERCENT / 100;
					rectTop = (100 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT - (selectedCards[i] * 5)) * height / 100f;
					rectBottom = (100 - VERTICAL_BORDER_PERCENT - (selectedCards[i] * 5)) * height / 100f;
					rects[i] = new RectF(rectLeft, rectTop, rectRight, rectBottom);
					drawCard(g, rects[i], state.getHand(playerNum).peekAt(i));
				}
				//Create the rects and locations for the players cards in the played phases for Human
				for (int j = 0; j < 2 /*phaseLocs.length*/; j++) {
					rectLeft = phaseLocs[j].left;
					rectRight = rectLeft + width * CARD_WIDTH_PERCENT / 100;
					rectTop = phaseLocs[j].top;
					rectBottom = phaseLocs[j].bottom;
					float overlapCrunch = 1;
					if(state.getPlayedPhase()[playerNum][j].size() > 7){
						overlapCrunch = 1.5f;
					}
					for (int k = 0; k < state.getPlayedPhase()[playerNum][j].size(); k++) {
						RectF myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
						drawCard(g, myRect, state.getPlayedPhase()[playerNum][j].peekAt(k));

						rectLeft = rectLeft + width * (CARD_WIDTH_PERCENT - HOR_OVERLAP) / (100 * overlapCrunch);
						rectRight = rectLeft + width * CARD_WIDTH_PERCENT / 100;
					}
				}
				//draw cards in the rects for computer phase locations as neccessary
				//also draws turn indicator
				int offset = 0;
				Paint turnPaint = new Paint();
				turnPaint.setColor(Color.RED);
				rectLeft = 0;
				rectRight = width;
				rectTop = 97.5f * height / 100;
				rectBottom = height;
				turnLocation = new RectF(rectLeft, rectTop, rectRight, rectBottom);
				for (int j = 0; j < players; j++) {
					if (j == playerNum) { //the human player
						offset++;
						//turn indicator
						if (j == state.getToPlay()) {
							g.drawRect(turnLocation, turnPaint);
						}
					} else {
						//phase locations
						for (int k = 0; k < 2; k++) {
							//Log.i("Crashing Player", Integer.toString(j));
							rectLeft = computerPhaseLocs[j - offset][k].left;
							rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
							rectTop = computerPhaseLocs[j - offset][k].top;
							rectBottom = computerPhaseLocs[j - offset][k].bottom;

							float overlapCrunch = 1;	//factopr for card spacing if phase locations get too full
							if(state.getPlayedPhase()[j-offset][k].size() > 7){
								overlapCrunch = 1.5f;
							}
							//cards in phase locations
							for (int l = 0; l < state.getPlayedPhase()[j][k].size(); l++) {
								RectF myRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
								drawCard(g, myRect, state.getPlayedPhase()[j][k].peekAt(l));

								rectLeft = rectLeft + width * (2 * SMALL_CARD_WIDTH_PERCENT - SMALL_HOR_OVERLAP) / (100*overlapCrunch);
								rectRight = rectLeft + width * SMALL_CARD_WIDTH_PERCENT / 100;
							}
						}

						//turn indicator
						if (j == state.getToPlay()) {
							//BAD CODE
							/*if (players > 4) {
								if (j - offset == 0) {
									g.save();
									g.rotate(90, 0, (35) * height / 100);
									g.drawRect(computerTurnLocation[j - offset], turnPaint);
									g.restore();
								} else if (j - offset == players - 2) {
									g.save();
									g.rotate(-90, width, (35) * height / 100);
									g.drawRect(computerTurnLocation[j - offset], turnPaint);
									g.restore();
								} else {
									g.drawRect(computerTurnLocation[j - offset], turnPaint);
								}*/
							//ONLY NEED THIS NOW
							g.drawRect(computerTurnLocation[j - offset], turnPaint);
						}
					}
				}

				//Location for Score Info Toggler
				float rectL = width - CARD_WIDTH_PERCENT * width / 100;
				float rectR = width;
				float rectT = 0;
				float rectB = height * CARD_HEIGHT_PERCENT / 100 / 2;
				scoreRect = new RectF(rectL, rectT, rectR, rectB);
				g.drawRect(scoreRect, scorePaint);

				rectT = height * CARD_HEIGHT_PERCENT / 100 / 2;
				rectB = height * CARD_HEIGHT_PERCENT / 100;
				helpRect = new RectF(rectL, rectT, rectR, rectB);
				g.drawRect(helpRect, helpPaint);

				/*//for bitmap drawings
				Rect r = new Rect(0,0,imgBitmap[4].getWidth(),imgBitmap[4].getHeight());
				//settings box (can be changed after)
				scoreRect = new RectF((92.5f*g.getWidth()/100), 0, g.getWidth(), 7.5f*g.getWidth()/100);
				//placeholder paint
				Paint p = new Paint();
				p.setColor(Color.BLACK);
				//draw the settings icon
				g.drawBitmap(imgBitmap[4], r, scoreRect, p);*/
			}
		}

	}

	/**
	 * @return
	 * 		the rectangle that represents the location on the drawing
	 * 		surface where the indicated phase location should go
	 */
	private RectF getPhaseLoc(int spot) {
		int width = surface.getWidth();
		int height = surface.getHeight();

		float rectLeft;
		float rectRight;
		float rectTop;
		float rectBottom;
		RectF phaseLoc;

		switch(spot) {
			case 0:
				rectLeft = (50 - 2.5f*LEFT_BORDER_PERCENT - 7*(CARD_WIDTH_PERCENT-HOR_OVERLAP)) * width / 100;
				rectRight = rectLeft + width * (7*(CARD_WIDTH_PERCENT-HOR_OVERLAP) + 2*LEFT_BORDER_PERCENT) / 100;
				rectTop = (100 - VERTICAL_BORDER_PERCENT - 2.5f * CARD_HEIGHT_PERCENT) * height / 100f;
				rectBottom = (100 - VERTICAL_BORDER_PERCENT - 1.5f * CARD_HEIGHT_PERCENT) * height / 100f;
				phaseLoc = new RectF(rectLeft, rectTop, rectRight, rectBottom);
				return phaseLoc;

			case 1:
				rectLeft = (50 + 0.5f*LEFT_BORDER_PERCENT) * width / 100;
				rectRight = rectLeft + width * (7*(CARD_WIDTH_PERCENT-HOR_OVERLAP) + 2*LEFT_BORDER_PERCENT) / 100;
				rectTop = (100 - VERTICAL_BORDER_PERCENT - 2.5f * CARD_HEIGHT_PERCENT) * height / 100f;
				rectBottom = (100 - VERTICAL_BORDER_PERCENT - 1.5f * CARD_HEIGHT_PERCENT) * height / 100f;
				phaseLoc = new RectF(rectLeft, rectTop, rectRight, rectBottom);
				return phaseLoc;

			default:
				phaseLoc = null; //set up other locations later
				return phaseLoc;
		}
	}

	/**
	 * @return
	 * 		the rectangle that represents the location on the drawing
	 * 		surface where the top card in the current player's deck is to
	 * 		be drawn
	 */
	private RectF thisPlayerTopCardLocation() {
		// near the right-bottom of the drawing surface, based on the height
		// and width, and the percentages defined above
		int width = surface.getWidth();
		int height = surface.getHeight();
		return new RectF((100-RIGHT_BORDER_PERCENT-CARD_WIDTH_PERCENT)*width/100f,
				(100-VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(100-RIGHT_BORDER_PERCENT)*width/100f,
				(100-VERTICAL_BORDER_PERCENT)*height/100f);
	}

	/**
	 * @return
	 * 		the rectangle that represents the location on the drawing
	 * 		surface where the top card in the middle pile is to
	 * 		be drawn
	 */
	private RectF middlePileTopCardLocation() {
		// near the middle-bottom of the drawing surface, based on the height
		// and width, and the percentages defined above
		int height = surface.getHeight();
		int width = surface.getWidth();
		float rectLeft = (100-CARD_WIDTH_PERCENT+LEFT_BORDER_PERCENT-RIGHT_BORDER_PERCENT)*width/200;
		float rectRight = rectLeft + width*CARD_WIDTH_PERCENT/100;
		float rectTop = (100-VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f;
		float rectBottom = (100-VERTICAL_BORDER_PERCENT)*height/100f;
		return new RectF(rectLeft, rectTop, rectRight, rectBottom);
	}

	/**
	 * draws a sequence of card-backs, each offset a bit from the previous one, so that all can be
	 * seen to some extent
	 *
	 * @param g
	 * 		the canvas to draw on
	 * @param topRect
	 * 		the rectangle that defines the location of the top card (and the size of all
	 * 		the cards
	 * @param deltaX
	 * 		the horizontal change between the drawing position of two consecutive cards
	 * @param deltaY
	 * 		the vertical change between the drawing position of two consecutive cards
	 * @param numCards
	 * 		the number of card-backs to draw
	 */
	private void drawCardBacks(Canvas g, RectF topRect, float deltaX, float deltaY,
							   int numCards) {
		// loop through from back to front, drawing a card-back in each location
		for (int i = numCards-1; i >= 0; i--) {
			// determine theh position of this card's top/left corner
			float left = topRect.left + i*deltaX;
			float top = topRect.top + i*deltaY;
			// draw a card-back (hence null) into the appropriate rectangle
			drawCard(g,
					new RectF(left, top, left + topRect.width(), top + topRect.height()),
					null);
		}
	}

	/**
	 * callback method: we have received a touch on the animation surface
	 *
	 * @param event
	 * 		the motion-event
	 */
	public void onTouch(MotionEvent event) {

		// ignore everything except down-touch events
		if (event.getAction() != MotionEvent.ACTION_DOWN) return;

		// get the location of the touch on the surface
		int x = (int) event.getX();
		int y = (int) event.getY();
		//Log.i("Xloc, yLoc", Integer.toString(x)+", "+Integer.toString(y));

		//if showing scores, any touch will hide scores
		if (showScores){
			showScores = false;
			return;
		}
		//if showing manual, any touch will hide manual
		else if (showHelp){
			if (closeHelpRect.contains(x,y)) {
				showHelp = false;
			}
			return;
		}

		//determine if player wants to see score info
		if(scoreRect.contains(x, y)){
			Log.i("Score","Logged");
			showScores = true;
		}
		//determine if player wants to see manual
		else if(helpRect.contains(x, y)){
			Log.i("Help", "Logged");
			showHelp = true;
		}
		// determine whether the touch occurred on any of the players cards
		int touchedCard = -1;
		for(int i = 0; i < state.getHand(playerNum).size(); i++){
			//Log.i("Reached", Integer.toString(i));
			if(rects[i].contains(x, y)){
				touchedCard = i;
			}
		}
		//if no card was selected
		if(touchedCard == -1){
			// illegal touch-location: flash for 1/20 second
			//surface.flash(Color.RED, 50);
		}
		//If a card was selected
		else {
			selectedCards[touchedCard] = (selectedCards[touchedCard]+1)%2; //Toggle card selected/unselected
			return;
		}

		if(discardLocation.contains(x,y)){
			//boolean to determine if a single card was selected
			boolean cardSelected = false;
			//count how many cards are selected
			int count = 0;
			int loc = -1;
			for(int i = 0; i < selectedCards.length; i++){
				if(selectedCards[i] == 1){
					count++;
					loc = i;
				}
			}
			//if and only if 1 card is selected then cardselected = true
			if(count == 1){
				cardSelected = true;
			}
			if(cardSelected){ //if one card was selected, when pressing the discard, discard that card
				Card toDiscard = state.getHand(playerNum).peekAt(loc);
				Log.i("Card discarding", toDiscard.toString());
				if(toDiscard.getWildValue() == 14){
					//if its a skip card
					state.setChooseSkip(true);
					skipCard = new Card(toDiscard);
					Log.i("setting skipCard", "set choose skip");
				}
				else {
					P10DiscardCardAction myAction = new P10DiscardCardAction(this, toDiscard);
					game.sendAction(myAction);
					for(int j = 0; j < selectedCards.length; j++){
						selectedCards[j] = 0; //deselect all cards
					}
				}
				return;
			}
			else if (count > 1) { //if more than 1 card is selected, flash
				Log.i("Flash", "Discard More than 1");
				surface.flash(Color.RED, 50);
			}
			else{ //if no cards are selected then attempt to draw from the discard pile
				//action contains player (this) and false to indicate discard pile
				P10DrawCardAction myAction = new P10DrawCardAction(this, false);
				//send action to the game
				game.sendAction(myAction);
				return;
			}
		}
		if(drawLocation.contains(x,y)){
			//action contains player (this) and true to indicate draw pile
			P10DrawCardAction myAction = new P10DrawCardAction(this, true);
			//send action to the game
			game.sendAction(myAction);
			return;
		}
		for(int i = 0; i < 2; i++){ /*for(int i = 0; i < phaseLocs.length; i++){*/
			//phaseLocs[i] = getPhaseLoc(i);
			//Log.i("Reached", Integer.toString(i));
			//Log.i("SHould be true", Boolean.toString(phaseLocs[i].contains(x, y)));
			if(phaseLocs[i].contains(x, y)){
				if(state.getPlayedPhase()[playerNum][0].size() == 0) {
					//Log.i("Phase clicked", "yeah");
					Deck myPhase = new Deck();
					for (int j = 0; j < selectedCards.length; j++) {
						if (selectedCards[j] == 1) {
							myPhase.add(state.getHand(playerNum).peekAt(j));
						}
					}
					P10MakePhaseAction myAction = new P10MakePhaseAction(this, myPhase);
					game.sendAction(myAction);
					for (int z = 0; z < selectedCards.length; z++) {
						selectedCards[z] = 0; //deselect all cards
					}
					return;
				}
				else {
					int count = 0;
					Card myCard = null;
					for(int j = 0; j < selectedCards.length; j++){
						if(selectedCards[j] == 1){
							count++;
							myCard = state.getHand(playerNum).peekAt(j);
						}
					}
					if(count == 1){
						P10HitCardAction myAction = new P10HitCardAction(this, myCard, playerNum, i);
						game.sendAction(myAction);
						for (int k = 0; k < selectedCards.length; k++) {
							selectedCards[k] = 0; //deselect all cards
						}
						return;
					}
				}
			}
		}
		int offset = 0;
		for(int i = 0; i < state.getNumberPlayers(); i++){
			if(i == playerNum){
				offset++;
			}
			else{
				Card myCard = null;
				int count = 0;
				for(int j = 0; j < selectedCards.length; j++){
					if(selectedCards[j] == 1){
						count++;
						myCard = state.getHand(playerNum).peekAt(j);
					}
				}
				if(myCard != null) {
					if (computerPhaseLocs[i - offset][0].contains(x, y)) {
						if (myCard.getWildValue() == 14 && count == 1) {
							skipCard = new Card(myCard);
							skipCard.setSkipValue(i);
							P10DiscardCardAction myAction = new P10DiscardCardAction(this, skipCard);
							game.sendAction(myAction);
							for (int j = 0; j < selectedCards.length; j++) {
								selectedCards[j] = 0; //deselect all cards
							}
							return;
						} else if (state.getPlayedPhase()[playerNum][0].size() != 0 && count == 1) {
							//if player has made phase, allow hitting
							P10HitCardAction myAction = new P10HitCardAction(this, myCard, i, 0);
							game.sendAction(myAction);
							Log.i("Hitcard", "Action");
							for (int k = 0; k < selectedCards.length; k++) {
								selectedCards[k] = 0; //deselect all cards
							}
							return;
						} else {
							Log.i("Flash", "Computer Phase locs1");
							surface.flash(Color.RED, 50);
						}
					} else if (computerPhaseLocs[i - offset][1].contains(x, y)) {
						if (myCard.getWildValue() == 14 && count == 1) {
							skipCard = new Card(myCard);
							skipCard.setSkipValue(i);
							P10DiscardCardAction myAction = new P10DiscardCardAction(this, skipCard);
							game.sendAction(myAction);
							for (int j = 0; j < selectedCards.length; j++) {
								selectedCards[j] = 0; //deselect all cards
							}
							return;
						}
						else if (state.getPlayedPhase()[playerNum][0].size() != 0 && count == 1) {
							P10HitCardAction myAction = new P10HitCardAction(this, myCard, i, 1);
							game.sendAction(myAction);
							for (int k = 0; k < selectedCards.length; k++) {
								selectedCards[k] = 0; //deselect all cards
							}
							return;
						} else {
							Log.i("Flash", "Computer Phase locs2");
							surface.flash(Color.RED, 50);
						}
					}
				}
				else{
					Log.i("Flash", "ELSE");
					surface.flash(Color.RED, 50);
				}
			}
		}
	}

	/**
	 * draws a card on the canvas; if the card is null, draw a card-back
	 *
	 * @param g
	 * 		the canvas object
	 * @param rect
	 * 		a rectangle defining the location to draw the card
	 * @param c
	 * 		the card to draw; if null, a card-back is drawn
	 */
	private static void drawCard(Canvas g, RectF rect, Card c) {
		if (c == null) {
			// null: draw a card-back, consisting of a blue card
			// with a white line near the border. We implement this
			// by drawing 3 concentric rectangles:
			// - blue, full-size
			// - white, slightly smaller
			// - blue, even slightly smaller
			Paint white = new Paint();
			white.setColor(Color.WHITE);
			Paint blue = new Paint();
			blue.setColor(Color.BLUE);
			RectF inner1 = scaledBy(rect, 0.96f); // scaled by 96%
			RectF inner2 = scaledBy(rect, 0.98f); // scaled by 98%
			g.drawRect(rect, blue); // outer rectangle: blue
			g.drawRect(inner2, white); // middle rectangle: white
			g.drawRect(inner1, blue); // inner rectangle: blue
		}
		else {
			// just draw the card
			c.drawOn(g, rect);
		}
	}

	/*private static void drawIndicator(Canvas g, RectF[] blocks, int turn, int players) {
		if (blocks != null) {
			Paint red = new Paint();
			red.setColor(Color.RED);
			switch(turn) {
				case 0:

			}
			g.drawRect(blocks[turn], red);
		}
	}*/

	/**
	 * scales a rectangle, moving all edges with respect to its center
	 *
	 * @param rect
	 * 		the original rectangle
	 * @param factor
	 * 		the scaling factor
	 * @return
	 * 		the scaled rectangle
	 */
	private static RectF scaledBy(RectF rect, float factor) {
		// compute the edge locations of the original rectangle, but with
		// the middle of the rectangle moved to the origin
		float midX = (rect.left+rect.right)/2;
		float midY = (rect.top+rect.bottom)/2;
		float left = rect.left-midX;
		float right = rect.right-midX;
		float top = rect.top-midY;
		float bottom = rect.bottom-midY;

		// scale each side; move back so that center is in original location
		left = left*factor + midX;
		right = right*factor + midX;
		top = top*factor + midY;
		bottom = bottom*factor + midY;

		// create/return the new rectangle
		return new RectF(left, top, right, bottom);
	}
}
