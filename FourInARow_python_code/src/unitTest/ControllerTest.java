package unitTest;
import java.net.Socket;
import java.util.ArrayList;

import org.junit.Before;

import controllerOfClient.Player;
import controllerOfServer.Controller;
import model.Color;
import model.Model;
import viewOfClient.View;
public class ControllerTest 
{
	private Controller controller;
	private Model model;
	@Before  public void setUp() {    
		Model m=new Model();
		View v=new View();
/*		ArrayList<Player>waitPlayers=ArrayList<Player>();
		Player[0]=new Player("alice", Color.Red , Socket socket, 0);
		Player[1]=new Player("bob", Color.Blue, Socket socket, 0);
		controller = new Controller(m, v, 0, Player[] playerL, waitPlayers); */ 
	}
}
