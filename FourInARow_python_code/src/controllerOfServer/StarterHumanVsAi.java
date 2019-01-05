package controllerOfServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import controllerOfClient.*;
import model.*;
import viewOfClient.*;
public class StarterHumanVsAi implements Runnable  {
	private ArrayList<String> humanVsAIName;
	private ArrayList<Socket> humanVsAISocket;
	private ArrayList<Player> waitingPlayers;
	private Player[] playerList;
	private int mode; 
	ArrayList<String> playerNames = new ArrayList<String>();
	ArrayList<Socket> playerSockets = new ArrayList<Socket>();
	

	public StarterHumanVsAi(ArrayList<String> pToEName, 
								ArrayList<Socket> pToESocket,
								ArrayList<Player> waitPlayers) {
		p("human vs ai starter");
		this.humanVsAIName = pToEName;
		this.humanVsAISocket = pToESocket;
		this.mode = 1;
		this.playerList = new Player[2];
		this.waitingPlayers = waitPlayers;
		//p("constructor is finished");
	}
	
	
	public void run() {	
		while (true) {
			synchronized (humanVsAIName) {
				
				while (humanVsAIName.size() < 2) {
					//p("when humanVsHumanName.size()="+humanVsHumanName.size()+"<2");
					if (humanVsAIName.size() == 1) {
						try {
							PrintWriter outPWaitingPlayer = new PrintWriter(
									humanVsAISocket.get(0).getOutputStream(),true);
							outPWaitingPlayer.println("please wait for another player to join");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} 
					try {
						humanVsAIName.wait();
						p("waiting for AI to log in");
					} catch (InterruptedException e) {
						p("Exception in wait()");
						e.printStackTrace();
					}
				}
				
				playerNames.add(humanVsAIName.get(0));
					//System.out.println("playerNames.add "+humanVsAIName.get(0));
				playerNames.add(humanVsAIName.get(1));
					//System.out.println("playerNames.add "+humanVsAIName.get(1));
				playerSockets.add(humanVsAISocket.get(0));
					//System.out.println("playerSockets.add "+humanVsAISocket.get(0));
				playerSockets.add(humanVsAISocket.get(1));
					//System.out.println("playerSockets.add "+humanVsAISocket.get(1));
				
				//Clears original lists
				humanVsAIName.clear();
				humanVsAISocket.clear();
				
			}
			
			Model model = new Model();
			p("model is created");
			View viewObj = new View();
			p("view is created");
			playerList[0] = new Player(playerNames.get(0),
										Color.Red, 
										playerSockets.get(0),
										mode);
			p(playerList[0] + "playerList[0] is created");
			
			playerList[1] = new Player(playerNames.get(1),
										Color.Blue, 
										playerSockets.get(1),
										mode);
			p(playerList[1] + "playerList[1] is created");
			
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
			new Thread(controller).start();
			p("new Thread(controller).start()");
		}
	}

	public void p(String s) {
		System.out.println(s);
		return;
	}
}
