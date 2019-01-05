package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.Colour;
import model.Model;

import java.util.*;


public class PlayerView extends JPanel implements Observer/*, ViewInterface*/
{
	private Model model;
	private static final long serialVersionUID = 1L;
	
	private JRadioButton player1;
	private JRadioButton player2;
	private ButtonGroup group;
	private JLabel labelAIThinkingTime=new JLabel ("set computer's thinking time");
	private JTextField AIthinkingTime=new JTextField (10);
	int thinkingTime=1;
	
	public PlayerView (Model m)
	{
		super(); 
		
		model=m;
		// create UI
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.LEFT, 80, 0));

		// radio button of the player O
		player1 = new JRadioButton("Player Red");
		player1.setPreferredSize(new Dimension(200, 100));
		Font curFont = player1.getFont();
		player1.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 22));
		player1.setFocusable(false);

		// radio button of the player X
		player2 = new JRadioButton("Player Blue");
		player2.setPreferredSize(new Dimension(200, 100));
		player2.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 22));
		player2.setFocusable(false);

		// create the radio button group
		group = new ButtonGroup();
		group.add(player1);
		group.add(player2);

		this.add(player1);
		this.add(player2);
		this.add(labelAIThinkingTime);
		this.add(AIthinkingTime);
		
		// register the controller
		this.registerController();
	}
	
	public int getAIThinkingTimeFromUser()
	{
		thinkingTime=Integer.parseInt(AIthinkingTime.getText());
		System.out.println("in PlayerView, read thinkingTime="+thinkingTime);
		return thinkingTime;
	}

	void registerController() 
	{
		this.player1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt) 
			{
				// if game not start, set the player turn and change status to first move
				//System.out.println("player1:model.getStatus(): "+model.getStatus());
				if (model.getStatus() == 0) { 
					player1.setSelected(true);
					model.setStatus(1);
					model.setTurn(0);
				}

 				if (model.getStatus() == 1) 
				{
					model.setTurn(0);

					// if in PVE mode, let AI move immediately for simplicity
					if (model.getMode() == 2) // emulate the AI thinking process
					{
						try{
							getAIThinkingTimeFromUser();
							player1.setText("AI thinking...");
	
							// delay for AI thinking process
							new Thread(new Runnable() 
							{
								public void run() {
									try {
										Thread.sleep(thinkingTime);
									} catch (InterruptedException ignore) {
										System.out.println("there is an InterruptedException");
									}
									model.AIMove(Colour.Red,1);
								}
							}).start();
						}catch (NumberFormatException ex)
						{	
							System.out.println("please enter an integer to set the AI thinking time");
						}
					}
				}

				// if other status disable the selection
				if (model.getStatus() != 0 || model.getStatus() != 1) {
					if (model.getTurn() == 0) {
						player1.setSelected(true);
					} else {
						player2.setSelected(true);
					}
				}
			}
		}
	);

		this.player2.addActionListener(	new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt) 
			{
				// if game not start, set the player turn and change status to
				// first move
				//System.out.println("player2:model.getStatus(): "+model.getStatus());

				if (model.getStatus() == 0) {
					model.setStatus(1);
					model.setTurn(1);
				}

				// if status is first move, only set the player turn
				if (model.getStatus() == 1) 
				{
					model.setTurn(1);

					// if in PVE mode, let AI move immediately for simplicity
					if ((model.getMode() == 1)||(model.getMode() == 2)) // emulate the AI thinking process
					{
						try{
							getAIThinkingTimeFromUser();
							player2.setText("AI thinking...");
	
							// delay for AI thinking process
							new Thread(new Runnable() 
							{
								public void run() {
									try {
										Thread.sleep(thinkingTime);
									} catch (InterruptedException ignore) {
										System.out.println("there is an InterruptedException");
									}
									model.AIMove(Colour.Blue,-1);
								}
							}).start();
						}catch (NumberFormatException ex)
						{	
							System.out.println("please enter an integer to set the AI thinking time");
						}
					}
				}

				// if other status disable the selection
				if (model.getStatus() != 0 || model.getStatus() != 1) {
					if (model.getTurn() == 0) {
						player1.setSelected(true);
					} else {
						player2.setSelected(true);
					}
				}
			}
		});
	}

	// Observer interface
	@Override
	public void update(Observable o, Object arg) {
		// change the player name according to the mode
		if (model.getMode() == 0) {
			player1.setText("Player Red");
			player2.setText("Player Blue");
		} else if (model.getMode() == 1){
			player1.setText("Player Red");
			player2.setText("AI Blue");
		}else if (model.getMode() == 2){
			player1.setText("AI Red");
			player2.setText("AI Blue");
		}

		// if new game, clear the player turn
		if (model.getStatus() == 0) {
			group.clearSelection();
		}

		// if game starts, update the player turn
		if (model.getStatus() == 2) 
		{
			if (model.getTurn() == 0) 
			{
				if (model.getMode() == 2)
					player1.setText("AI thinking...");
				player1.setSelected(true);
			} else //if (model.getTurn() == 1) 
			{
				if ((model.getMode() == 1)||(model.getMode() == 2))
					player2.setText("AI thinking...");// emulate the AI thinking process
				player2.setSelected(true);
			}
		}
	}
}
