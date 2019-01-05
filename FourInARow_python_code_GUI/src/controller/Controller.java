package controller;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JTextField;
import model.*;
import view.*;

public class Controller implements ActionListener 
{
	Model model;
	PlayerView view;
	public Controller(Model m, PlayerView v)
	{
		this.model=m;
		this.view=v; 
		model.addObserver(view);//The view observes the model
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}