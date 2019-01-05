package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import model.*;
import model.Colour;
import controller.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ToolBarView extends JPanel implements Observer/*, ViewInterface*/
{

	private static final long serialVersionUID = 1L;
	final static int SELECT_TURN = 0;
	final static int FIRST_MOVE = 1;
	final static int NUM_MOVES = 2;
	final static int INDEX_OUT_OF_RANGE = 3;
	final static int NO_EMPTY_FIELD = 4; //no empty fields in this z axis
	final static int RED_WIN = 5;
	final static int BLUE_WIN = 6;
	final static int DRAW = 7;
	
	private Model model;

	private JButton newGameButton;
	private JButton chooseMode;
	private JButton getHintButton;
	private JButton quitGameButton;
	
	private JLabel label;// the message of the toolbar
	private JLabel showHint;
	boolean running;

	//-----------constructor: ToolBarView extends JPanel implements Observer------------------------
	public ToolBarView(Model m) 
	{
		model = m;// set the model
		
		running=true;
		Font curFont;
		
		// create UI
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.LEFT, 20, 50));
	
		newGameButton = new JButton("New Game");
		curFont = newGameButton.getFont();
		newGameButton.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		newGameButton.setPreferredSize(new Dimension(110, 50));
		newGameButton.setFocusable(false);
		this.add(newGameButton);//add button to JPanel

		// the button to change game mode
		chooseMode = new JButton("human player vs human player");
		curFont = chooseMode.getFont();
		chooseMode.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		chooseMode.setPreferredSize(new Dimension(270, 50));
		chooseMode.setFocusable(false);
		this.add(chooseMode);	
		
		getHintButton = new JButton("Get Hint");
		curFont = getHintButton.getFont();
		getHintButton.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		getHintButton.setPreferredSize(new Dimension(110, 50));
		getHintButton.setFocusable(false);
		this.add(getHintButton);
		
		showHint=new JLabel ("hint");
		curFont = showHint.getFont();
		showHint.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		this.add(showHint);
		
		quitGameButton = new JButton("Quit Game");
		curFont = quitGameButton.getFont();
		quitGameButton.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		quitGameButton.setPreferredSize(new Dimension(110, 50));
		quitGameButton.setFocusable(false);
		this.add(quitGameButton);

		// the label used to display the game info message
		label = new JLabel("Select which player starts ", JLabel.CENTER);
		curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 14));
		label.setAlignmentX(CENTER_ALIGNMENT);
		this.add(label);
		label.setPreferredSize(new Dimension(500, 50));
	
		// register the controllers
		this.registerController();
	}
	/* the controller part */

	
	void registerController() 
	{
		System.out.println("registerController");
		
		this.newGameButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("newGameButton.addMouseListener");
				model.newGame();
			}
		});

		this.chooseMode.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (model.getStatus() == 0) {
					//System.out.println("status=0 chooseMode.addMouseListener");
					model.changeMode();
				}
			}
		});
			
		this.getHintButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int suggestedFieldToMove=0;
				if (model.getMode()!=2)
				{	
					if (model.getTurn()==0)
					{
						suggestedFieldToMove=model.bestFieldToMove(Colour.Red);
					}else if (model.getTurn()==1)
					{
						suggestedFieldToMove=model.bestFieldToMove(Colour.Blue);
					}
					showHint.setText("you can move to: "+suggestedFieldToMove);
				}
			}
		});
		
		this.quitGameButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("quitGameButton.addMouseListener");
				running = false;
			}
		});
	}
	
	// Observer interface
	@Override
	public void update(Observable o, Object arg) {
		// update the button indicate current mode
		if (model.getMode() == 0) {
			chooseMode.setText("human player vs human player");
		} else if (model.getMode() == 1) {
			chooseMode.setText("human player vs computer");
		}else if (model.getMode() == 2) {
			chooseMode.setText("computer vs computer");
		}
		int gameStatus = model.getStatus();

		// set different message content according to the game status
		switch (gameStatus) 
		{
			case SELECT_TURN:
				this.label.setText("Select which player starts ");
				break;
			case FIRST_MOVE:
				this.label.setText("Change which player starts, or make first move.");
				break;
			case NUM_MOVES:
				//int moves = 9 - model.getBlanks();
				this.label.setText("moves");
				break;
			case INDEX_OUT_OF_RANGE:
				this.label.setText("INDEX_OUT_OF_RANGE");
				break;
			case NO_EMPTY_FIELD:
				this.label.setText("NO_EMPTY_FIELD");
				break;
			case RED_WIN:
				this.label.setText("RED Wins");//O win
				break;
			case BLUE_WIN:
				this.label.setText("BLUE Wins"); //X win
				break;
			case DRAW:
				this.label.setText("Game over, no winner");
				break;
	
			default:
				break;
		}
	}

}