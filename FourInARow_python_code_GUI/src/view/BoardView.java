package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Colour;
import model.Model;
import view.*;


public class BoardView extends JPanel implements Observer
{
	
	private JButton tiles[][];// an array of buttons
	private Model model;
	private Color foreColor;
	private Color backColor;
	PlayerView playerView;

	
	BoardView(Model m, PlayerView pView) 
	{
		model = m;
		playerView=pView;

		
		this.setLayout(new GridBagLayout());// a GridBagLayout with default constraints centres
		this.setBackground(Color.BLACK);

		JPanel p = new JPanel(new GridLayout(4, 16));//16 rows, 4 columns
		this.add(p, new GridBagConstraints());

		p.setMaximumSize(new Dimension(300, 300));

		tiles = new JButton[4][16];
		for (int i = 0; i < 4; i++) 
		{
			for (int j = 0; j < 16; j++) 
			{
				tiles[i][j] = new JButton();
				tiles[i][j].setPreferredSize(new Dimension(100, 100));//100,100
				Font curFont = tiles[i][j].getFont();
				tiles[i][j].setFont(new Font(curFont.getFontName(), curFont
						.getStyle(), 20));//22
				tiles[i][j].setFocusable(false);
				p.add(tiles[i][j]);
			}
		}
		
		foreColor = tiles[0][0].getForeground();
		backColor = tiles[0][0].getBackground();

		this.registerControllers();

	}
	private void registerControllers()
	{	

		for (int i = 0; i < 4; i++) 
		{
			for (int j = 0; j < 16; j++) 
			{
				int thei = i;
				int thej = j;
				this.tiles[thei][thej].addActionListener
				(
					new ActionListener() 
					{
						public void actionPerformed(ActionEvent evt) 
						{
							int fieldNumber=model.index(thej%4+1,thei+1,thej/4+1); // public int index(int x, int y, int z) 
							int thinkingTime=playerView.getAIThinkingTimeFromUser();
							model.playerMove(fieldNumber,thinkingTime);
						}
					}
				);
			}
		}
	}
	
	// Observer interface
	@Override
	public void update(Observable arg0, Object arg1) 
	{
		int fieldMovedIn = model.getFieldJustMovedIn();
		Colour changedToColor = model.getField(fieldMovedIn);
		//System.out.println("fieldMovedIn:"+fieldMovedIn+ "changedToColor:"+changedToColor);
		int fieldIndexi=(fieldMovedIn%16)/4;
		int fieldIndexj=(fieldMovedIn/16)*4+(fieldMovedIn%16)%4;
		
		if (changedToColor == Colour.Red) {
			tiles[fieldIndexi][fieldIndexj].setText("Red");
		} else if (changedToColor == Colour.Blue) {
			tiles[fieldIndexi][fieldIndexj].setText("Blue");
		}

		if (model.getStatus() == 0) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 16; j++) {
					tiles[i][j].setText("");
					tiles[i][j].setBackground(backColor);
					tiles[i][j].setForeground(foreColor);
					tiles[i][j].setOpaque(true);
				}
			}
		}

		// if anyone wins we need to highlight
		if (model.getStatus() == 4) {
			int winType = model.getWinType();
			//int winPos = model.getWinPos();

			switch (winType) {
			case 1: {
				System.out.println("winType=1: diagonal1_InXYPlane");
				break;
			}
			case 2: {
				System.out.println("winType=2 diagonal2_InXYPlane=succeed");
				break;
			}
			case 3: {
				System.out.println("winType=3 diagonal3_InZYPlane=succeed");
				break;
			}
			case 4: {
				System.out.println("winType=4 diagonal4=succeed");
				break;
			}
			case 5: {
				System.out.println("winType=5 diagonal5=succeed");
				break;
			}
			case 6: {
				System.out.println("winType=6 diagonal6=succeed");
				break;
			}
			case 7: {
				System.out.println("winType=7 diagonal7=succeed");
				break;
			}
			case 8: {
				System.out.println("winType=8 diagonal8=succeed");
				break;
			}
			case 9: {
				System.out.println("winType=9 diagonal9=succeed");
				break;
			}
			case 10: {
				System.out.println("winType=10 diagonal10=succeed");
				break;
			}
			case 11: {
				System.out.println("winType=11 fourInXaxis=succeed");
				break;
			}
			case 12: {
				System.out.println("winType=12 fourInYaxis=succeed");
				break;
			}
			case 13: {
				System.out.println("winType=13 fourInZaxis=succeed");
				break;
			}
			default:
				break;
			}
		}
	}
}
