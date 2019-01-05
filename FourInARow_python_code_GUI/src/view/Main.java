package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.*;
import view.*;
import view.ToolBarView;
import view.PlayerView;

public class Main 
{
	public static void main (String[] args) 
	{
		
		JFrame frame = new JFrame("Four-In-A-Row");

		// create Model and initialize it
		Model model = new Model();

		// create toolbarView
		ToolBarView toolbarView = new ToolBarView(model);
		model.addObserver(toolbarView);
		toolbarView.setPreferredSize(new Dimension(600, 150));

		// create playerView
		PlayerView playerView = new PlayerView(model);
		model.addObserver(playerView);
		playerView.setPreferredSize(new Dimension(600, 150));

		// create boardView, tell it about model (and controller)
		BoardView boardView = new BoardView(model,playerView);
		// tell Model about View.
		model.addObserver(boardView);
		boardView.setPreferredSize(new Dimension(600, 600));
		
		// create the window
		JPanel p = new JPanel(new BorderLayout(3, 1));
		frame.getContentPane().add(p);
		p.add(toolbarView, BorderLayout.PAGE_START);
		p.add(boardView, BorderLayout.CENTER);
		p.add(playerView, BorderLayout.PAGE_END);

		frame.setPreferredSize(new Dimension(600, 800));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

