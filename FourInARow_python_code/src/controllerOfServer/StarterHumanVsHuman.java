package controllerOfServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import controllerOfClient.*;
import model.*;
import viewOfClient.*;


public class StarterHumanVsHuman implements Runnable {
	private ArrayList<String> humanVsHumanName;
	private ArrayList<Socket> humanVsHumanSocket;
	private ArrayList<Player> waitingPlayers;
	private Player[] playerList;
	private int mode;
	ArrayList<String> playerNames = new ArrayList<String>();
	ArrayList<Socket> playerSockets = new ArrayList<Socket>();
  
	/**
	 * constructor of a human-vs-human controller.
	 * 
	 * @param humanVsHumanNameList: an arraylist that stores player's name
	 * @param humanVsHumanSocketList: an arraylist that stores player's socket
	 * @param waitPlayers: an arraylist that stores players who were abandoned by the opponent 
	 */
	public StarterHumanVsHuman(ArrayList<String> humanVsHumanNameList, 
								ArrayList<Socket> humanVsHumanSocketList, 
								ArrayList<Player> waitPlayers) {
		p("HumanVsHumanStarter");
		this.humanVsHumanName = humanVsHumanNameList;
		this.humanVsHumanSocket = humanVsHumanSocketList;
		this.mode = 0;
		this.playerList = new Player[2];
		this.waitingPlayers = waitPlayers;
		//p("constructor is finished");
	}
	
	/** 
	 * when number of players<2, wait for another player to join.
	 * when number of players=2, 
	 * 		1)copy the array humanVsHumanName to the array playerNames
	 * 		2)copy the array humanVsHumanSocket to the array playerSockets
	 * 		3)create two players based on the names from playerNames 
	 * 								and the sockets from playerSockets
	 * 		4)store these two players into the array PlayerList		
	 * 		5)clear the arrays: humanVsHumanName, humanVsHumanSocket
	 * 				so they are empty, they can be used to accept other pairs of client
	 * 		6)create an object of model, 
	 * 				an object of view, 
	 *				an object of controller
	 * 		7)start the controller
	 */
	public void run() {	
		while (true) {
			synchronized (humanVsHumanName) {

				while (humanVsHumanName.size() < 2) {
					//p("when humanVsHumanName.size()="+humanVsHumanName.size()+"<2");
					if (humanVsHumanName.size() == 1) {
						try {
							PrintWriter outPWaitingPlayer = new PrintWriter(
									humanVsHumanSocket.get(0).getOutputStream(),true);
							outPWaitingPlayer.println("please wait for another player to join");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} 
					try {
						
						humanVsHumanName.wait();
						p("waiting for another player");
					} catch (InterruptedException e) {
						p("Exception in wait()");
						e.printStackTrace();
					}
				}
				
				playerNames.add(humanVsHumanName.get(0));
				p("playerNames.add " + humanVsHumanName.get(0));
				
				playerNames.add(humanVsHumanName.get(1));
				p("playerNames.add " + humanVsHumanName.get(1));
				
				playerSockets.add(humanVsHumanSocket.get(0));
				p("playerSockets.add " + humanVsHumanSocket.get(0));
				
				playerSockets.add(humanVsHumanSocket.get(1));
				p("playerSockets.add " + humanVsHumanSocket.get(1));
				
				//Clears original lists
				humanVsHumanName.clear();
				humanVsHumanSocket.clear();
				
			}

			Model model = new Model();
			p("model is created");
				
			View viewObj = new View();
			p("view is created");
				
			playerList[0] = new Player(playerNames.get(0),
										Color.Red, 
										playerSockets.get(0),
										0);
			p("playerList[0]" + playerList[0].getName() + "is created");
			
			playerList[1] = new Player(playerNames.get(1),
										Color.Blue, 
										playerSockets.get(1),
										0);
			p("playerList[1]" + playerList[1].getName() + "is created");
			
			//Local list of player details is also now cleared
			playerNames.clear();
			playerSockets.clear();
			
			//Object of controller is created
			Controller controller = new Controller(model, 
														viewObj,
														mode, 
														playerList,
														waitingPlayers
														);
			p("controller is created");
			//Thread of controller started and this again goes to waiting for next 2 players
			new Thread(controller).start();
			p("new Thread(controller).start()");
		}
	}
	/**
	 * print message to system.out.
	 * @param s
	 */
	/*@
	 * requires s!=null
	 */
	public void p(String s) {
		System.out.println(s);
		return;
	}
}