package controllerOfServer;

import java.net.Socket;
import java.util.ArrayList;
import controllerOfClient.*;
import model.*;
import viewOfClient.*;
public class StarterAiVsAi implements Runnable  {
	private ArrayList<String> aiVsAIName;
	private ArrayList<Socket> aiVsAISocket;
	private ArrayList<Player> waitingPlayers;
	private Player[] playerList;
	private int mode;
	ArrayList<String> playerNames = new ArrayList<String>();
	ArrayList<Socket> playerSockets = new ArrayList<Socket>();
	
	public StarterAiVsAi(ArrayList<String> eToEName, 
								ArrayList<Socket> eToESocket,
								ArrayList<Player> waitPlayers) {
		p("AiVsAiStarter");
		this.aiVsAIName = eToEName;
		this.aiVsAISocket = eToESocket;
		this.mode = 2;
		this.playerList = new Player[2];
		this.waitingPlayers=waitPlayers;
		//p("constructor is finished");
	}
	
	public void run() {	
		while(true)
		{
			synchronized (aiVsAIName)
			{
				//p("synchronized, AIVsAIName.size()="+aiVsAIName.size());
				//If playerName2 has less than 2 players then, it goes to wait
				while(aiVsAIName.size()<2)
				{
					//p("when AIVsAIName.size()="+aiVsAIName.size()+"<2");
					try {
						aiVsAIName.wait();
						//p("waiting for AI to log in");
					} catch (InterruptedException e) {
						p("Exception in wait()");
						e.printStackTrace();
					}
				}
				
				//Copies details of all players in a local list and leaves the lock
				playerNames.add(aiVsAIName.get(0));
					System.out.println("playerNames.add "+aiVsAIName.get(0));
				playerNames.add(aiVsAIName.get(1));
					System.out.println("playerNames.add "+aiVsAIName.get(1));
				playerSockets.add(aiVsAISocket.get(0));
					System.out.println("playerSockets.add "+aiVsAISocket.get(0));
				playerSockets.add(aiVsAISocket.get(1));
					System.out.println("playerSockets.add "+aiVsAISocket.get(1));
				
				//Clears original lists
				aiVsAIName.clear();
				aiVsAISocket.clear();
				
			}
			
			//Object of model, view, players are created and is given to the
			//Controller
			Model model = new Model();
			View viewObj = new View();
			playerList[0] = new Player(playerNames.get(0),
										Color.Red, 
										playerSockets.get(0),
										mode);
			p(playerList[0]+"playerList[0] is created");
			
			playerList[1] = new Player(playerNames.get(1),
										Color.Blue, 
										playerSockets.get(1),
										mode);
			p(playerList[1]+"playerList[1] is created");
			
			//Local list of player details is also now cleared
			playerNames.clear();
			playerSockets.clear();
			
			//Object of controller is created
			Controller controller = new Controller( model, 
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

	public void p(String s)
	{
		System.out.println(s);
		return;
	}

}
