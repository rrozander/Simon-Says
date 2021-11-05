import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class GamePanel extends JPanel{
	
	private final Color OFF = Color.green;
	private final Color ON = Color.red;
	final static Color BACKGROUND = new Color(0,162,232);
	private final Color FONT_COLOR = Color.red;
	
	private final int BOX_SIZE = 200;
	private final int ROUND_DELAY = 1000;

	private Image rightArrow;
	private Image rightArrowFilled;
	private Image leftArrow;
	private Image leftArrowFilled;
	private Image downArrow;
	private Image downArrowFilled;
	private Image upArrow;
	private Image upArrowFilled;
	
	private int sTimer; // Timer for how long each simon move is displayed red
	private int colorTimer; // Timer for how long all squares are green between each color display
	private int currSMoveIdx; // Used for displaying Simon moves
	private int currPMoveIdx; // Used for displaying player moves
	private Integer roundNum;
	private Integer highScore;
	
	private boolean playerTurn;
	private boolean pColor; // Used to distinguish between a key press and release in repaint()
	private boolean gameOver;
	
	ArrayList<Moves> simonMoves;
	ArrayList<Moves> playerMoves;
	
	
	GamePanel() {		
		this.setPreferredSize(new Dimension(GameFrame.WIDTH, GameFrame.HEIGHT));
		this.setBackground(BACKGROUND);
		
		rightArrow = new ImageIcon("right-arrow.png").getImage();
		rightArrowFilled = new ImageIcon("right-arrow-filled.png").getImage();
		leftArrow = new ImageIcon("left-arrow.png").getImage();
		leftArrowFilled = new ImageIcon("left-arrow-filled.png").getImage();
		upArrow = new ImageIcon("up-arrow.png").getImage();
		upArrowFilled = new ImageIcon("up-arrow-filled.png").getImage();
		downArrow = new ImageIcon("down-arrow.png").getImage();
		downArrowFilled = new ImageIcon("down-arrow-filled.png").getImage();
		
		highScore = 0;
		
		setKeyBindings();
		reset();
	}
	
//---------------------------- Display ---------------------------------------------	
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);		
		Graphics2D page = (Graphics2D) gr;	
		
		// Draw 4 Boxes			
		page.drawImage(upArrow, 200, 0, null); // UP
		page.drawImage(leftArrow, 0, 200, null); // LEFT
		page.drawImage(rightArrow ,400, 200, null); // RIGHT
		page.drawImage(downArrow, 200, 400, null); // DOWN

		// Score Display
		page.setColor(FONT_COLOR);
		page.setFont(new Font("MV Boli",Font.BOLD,30));	
		String score = "Score: "+roundNum.toString();
		page.drawString(score, 0, 30);
		
		// HighScore Display
		String highScoreStr = "HS: "+highScore;
		page.drawString(highScoreStr, 400, 30);
		
		// GameOver Display
		if(gameOver) {
			page.setFont(new Font("MV Boli",Font.BOLD,80));
			page.drawString("GAME OVER", 40, 320);
		} else {
		
	//----------------------------- Display Simon moves -------------------------------------
		/* Display mechanism was roughly based off code found here:
			https://stackoverflow.com/questions/5446294/java-simon-says
		*/
		
		// Sets the current move box to red until sTimer hits 0
		if(!playerTurn) {
			if (colorTimer == 0) { 
				if(sTimer > 0) {			
					Moves currMove = simonMoves.get(currSMoveIdx);
					switch(currMove) {
						case LEFT:		
							page.drawImage(leftArrowFilled, 0, 200, null); // LEFT
						break;
						
						case RIGHT:
							page.drawImage(rightArrowFilled ,400, 200, null); // RIGHT
						break;
						
						case UP:
							page.drawImage(upArrowFilled, 200, 0, null); // UP
						break;
						
						case DOWN:
							page.drawImage(downArrowFilled, 200, 400, null); // DOWN
						break;
					default: System.out.println("ERROR");
						break;
					}		
					sTimer--;
					repaint();
				}
			} else {
				// Set all boxes to green				
				page.drawImage(upArrow, 200, 0, null); // UP
				page.drawImage(leftArrow, 0, 200, null); // LEFT
				page.drawImage(rightArrow ,400, 200, null); // RIGHT
				page.drawImage(downArrow, 200, 400, null); // DOWN
				colorTimer--;
				repaint();			
			}
			// sets current move to the next move or stops if all moves where displayed
			if(sTimer == 0) {
				if(currSMoveIdx < simonMoves.size()-1) {
					currSMoveIdx++;
					sTimer = 300;
					colorTimer = 300;
				} else {
					sTimer--;
					playerMove();
				}
			}
		}
	//-------------------------------------------------------------------------------------------
	
		
	//----------------------------- Display Player Turn -----------------------------------------
		if(playerTurn && pColor) {
			if(playerMoves.get(currPMoveIdx) != Moves.NONE ) {
				page.setColor(ON);
				Moves currMove = playerMoves.get(currPMoveIdx);
				
				switch(currMove) {
					case LEFT:		
						page.drawImage(leftArrowFilled, 0, 200, null); // LEFT
					break;
					
					case RIGHT:
						page.drawImage(rightArrowFilled ,400, 200, null); // RIGHT
					break;
					
					case UP:
						page.drawImage(upArrowFilled, 200, 0, null); // UP
					break;
					
					case DOWN:
						page.drawImage(downArrowFilled, 200, 400, null); // DOWN
					break;
					
					default: repaint();
					break;
				}
			}
		}
	//--------------------------------------------------------------------------------------	
		
		}

	}
//------------------------------------------------------------------------------------
	
	
	
//-------------------------------- Key Bindings --------------------------------------
	/* Code for key binding mechanism taken from:
	 * https://stackoverflow.com/questions/8482268/java-keylistener-not-called
	 * 
	 * Tried using key listener but ran into focusable issue due to using JPanel
	 * Issue was resolved using key binding instead.
	 */
	
	private void setKeyBindings() {
		ActionMap actionMap = getActionMap();
		int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = getInputMap(condition);
		
		// Adding pressed and released actions for each of the arrow keys
		String pVkLeft = "LEFT_PRESSED";
		String rVkLeft = "LEFT_RELEASED";
		
		String pVkRight = "RIGHT_PRESSED";
		String rVkRight = "RIGHT_RELEASED";
		
		String pVkUp = "UP_PRESSED";
		String rVkUp = "UP_RELEASED";
		
		String pVkDown = "DOWN_PRESSED";
		String rVkDown = "DOWN_RELEASED";
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), pVkLeft);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), rVkLeft);
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), pVkRight);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), rVkRight);
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), pVkUp);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), rVkUp);
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), pVkDown);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), rVkDown);
		
		actionMap.put(pVkLeft, new KeyAction(pVkLeft));
		actionMap.put(rVkLeft, new KeyAction(rVkLeft));
		
		actionMap.put(pVkRight, new KeyAction(pVkRight));
		actionMap.put(rVkRight, new KeyAction(rVkRight));
		
		actionMap.put(pVkUp, new KeyAction(pVkUp));
		actionMap.put(rVkUp, new KeyAction(rVkUp));
		
		actionMap.put(pVkDown, new KeyAction(pVkDown));
		actionMap.put(rVkDown, new KeyAction(rVkDown));
	}
	
	private class KeyAction extends AbstractAction {
		public KeyAction(String actionCommand) {
	       putValue(ACTION_COMMAND_KEY, actionCommand);
	    }

		// Will run when one of the arrow keys is pressed. 
	    @Override
	    public void actionPerformed(ActionEvent actionEvt) {
	    	if(playerTurn) {	 	    		
	    		switch(actionEvt.getActionCommand()) {
		    		
	    			case "LEFT_PRESSED":
						playerMoves.set(currPMoveIdx, Moves.LEFT);
						pColor = true;
						repaint();		
					break;
	    		
	    			case "RIGHT_PRESSED":
						playerMoves.set(currPMoveIdx, Moves.RIGHT);
						pColor = true;
						repaint();			
					break;
					
	    			case "UP_PRESSED":
						playerMoves.set(currPMoveIdx, Moves.UP);
						pColor = true;
						repaint();		
					break;
					
	    			case "DOWN_PRESSED":
						playerMoves.set(currPMoveIdx, Moves.DOWN);
						pColor = true;
						repaint();		
					break;
					
	    			case "LEFT_RELEASED":
	    				if (simonMoves.get(currPMoveIdx) == Moves.LEFT) {
							currPMoveIdx++;
							pColor = false;
							repaint();
						} else {
							gameOver();
						}
    				break;
    				
	    			case "RIGHT_RELEASED":
	    				if (simonMoves.get(currPMoveIdx) == Moves.RIGHT) {
							currPMoveIdx++;
							pColor = false;
							repaint();
						} else {
							gameOver();
						}
    				break;
    				
	    			case "UP_RELEASED":
	    				if (simonMoves.get(currPMoveIdx) == Moves.UP) {
							currPMoveIdx++;
							pColor = false;
							repaint();
						} else {
							gameOver();
						}
    				break;
    				
	    			case "DOWN_RELEASED":
	    				if (simonMoves.get(currPMoveIdx) == Moves.DOWN) {
							currPMoveIdx++;
							pColor = false;
							repaint();
						} else {
							gameOver();
						}
    				break;
    				
	    		}
	    		
	    		
	    		if(currPMoveIdx == simonMoves.size()) {
	    			repaint();
	    			ActionListener delay = new ActionListener() {
	    				public void actionPerformed(ActionEvent evt) {
	    					roundNum = simonMoves.size()+1;
	    	    			System.out.println("ROUND"+ roundNum);
	    					simonMove();
	    				}
	    			};
	    			Timer timer = new Timer(ROUND_DELAY, delay);
	    			timer.setRepeats(false);
	    			timer.start();		
	    		}
	    	}
	       
	    }
	}
	
//----------------------------------------------------------------

	void playerMove() {
		playerMoves = new ArrayList<Moves>();
		for(Moves e: simonMoves) {
			playerMoves.add(Moves.NONE);
		}
		currPMoveIdx = 0;
		playerTurn = true;
	}
	
	void simonMove() {
		playerTurn = false;
		simonMoves.add(RandomMove());
		displaySimonMoves();
	}
	
	void displaySimonMoves() {
		sTimer = 300;
		colorTimer = 0;
		currSMoveIdx = 0;
		repaint();
	}
	
	private Moves RandomMove() {
		Random random = new Random();		
		int num = random.nextInt(4)+1;
		switch(num) {
		case 1:
			return Moves.LEFT;
		case 2:
			return Moves.UP;
		case 3:
			return Moves.RIGHT;
		case 4:
			return Moves.DOWN;
		default: return Moves.DOWN;
		}
	}
	
	void gameOver() {
		gameOver = true;
		highScore = roundNum;
		repaint();
		System.out.println("GAME OVER!");
		// Wait (Maybe Disable movements)
		ActionListener delay = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				reset();
			}
		};
		Timer timer = new Timer(1500, delay);
		timer.setRepeats(false);
		timer.start();	
		//System.exit(0);
	}
	
	void reset() {
		simonMoves = new ArrayList<Moves>();
		sTimer = 0;
		playerTurn = false;
		gameOver = false;
		roundNum = 1;
		
		simonMove();
	}

	
}



