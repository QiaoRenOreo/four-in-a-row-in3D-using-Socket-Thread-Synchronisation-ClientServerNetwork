package controllerOfClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class PlayerDetails implements Runnable {

	private Socket playerSocket;
	
	private static ArrayList<String> humanVsHumanName, humanVsAiName, aiVsAiName;
	private static ArrayList<Socket> humanVsHumanSocket, humanVsAiSocket, aiVsAiSocket;
	private static ArrayList<Player> waitingPlayers;
	PrintWriter	out = null;
	BufferedReader bufferedReader = null;
	
	public PlayerDetails(Socket socket, 
			ArrayList<String> pToPName, //person to person
			ArrayList<String> pToEName, //person to environment
			ArrayList<String> eToEName, //environment to environment
			ArrayList<Socket> pToPSocket,
			ArrayList<Socket> pToESocket,
			ArrayList<Socket> eToESocket,
			ArrayList<Player> waitPlayers) {
		this.playerSocket =     socket;
		this.humanVsHumanName = pToPName;
		this.humanVsAiName =    pToEName;
		this.aiVsAiName =       eToEName;
		this.humanVsHumanSocket = pToPSocket;
		this.humanVsAiSocket =     pToESocket;
		this.aiVsAiSocket =        eToESocket;
		this.waitingPlayers = waitPlayers;
		System.out.println("player details has created."
				+"player socket:"+socket);
	}
	

	
	public void run() {
		try {
			//Writer is made for sending data on client socket
			/*PrintWriter*/	out = new PrintWriter(playerSocket.getOutputStream(), true);
			System.out.println("playerSocket.getOutputStream() is created");
			
			//Reader is made for reading data from client
			/*BufferedReader*/ bufferedReader = new BufferedReader(
					new InputStreamReader(playerSocket.getInputStream()));
			out.println("Would you like to play a human-vs-human game? if yes, enter 0");
			out.println("Would you like to play a human-vs-AI game?    if yes, enter 1");
			out.println("Would you like to play an AI-vs-AI game?      if yes, enter 2");
			
			String data = bufferedReader.readLine();
			
			int mode = Integer.parseInt(data);
			
			if (mode == 0) { //human-vs-human game
			
				out.println("mode=0,Enter your name");
				data = bufferedReader.readLine().trim(); 
				System.out.println("name=" + data);
				
				synchronized (humanVsHumanName) {
					System.out.println("humanVsHumanName.size()=" + 
										humanVsHumanName.size());
					registerAPlayer(humanVsHumanName,
									humanVsHumanSocket,
									data, 
									playerSocket); 
					checkWaitingList(0, humanVsHumanName, humanVsHumanSocket);
					if (humanVsHumanName.size() == 2) {
						humanVsHumanName.notifyAll();
						System.out.println("notifyAll()");
					}
				}
			} else if (mode == 1) { //human vs AI game
				out.println("mode=1, Enter your name");
				data = bufferedReader.readLine();
				synchronized (humanVsAiName) {
					registerAPlayer(humanVsAiName,
							humanVsAiSocket,
							data, 
							playerSocket); 
					checkWaitingList(0, humanVsAiName, humanVsAiSocket);
					if (humanVsAiName.size() == 2) {
						humanVsAiName.notifyAll();
						System.out.println("notifyAll()");
					}	
				}
				
			} else { //mode ==2  AI-vs-AI
			
				out.println("mode=2, Enter your name");
				data = bufferedReader.readLine();
				System.out.println("name=" + data);
				synchronized (aiVsAiName) {
					registerAPlayer(aiVsAiName,
							aiVsAiSocket,
							data, 
							playerSocket); 
					checkWaitingList(0, aiVsAiName, aiVsAiSocket);					
					if (aiVsAiName.size() == 2) {
						aiVsAiName.notifyAll();
						System.out.println("notifyAll()");
					}	
				}
			}
		} catch (IOException e) {
			System.out.println("this program made an exception.");
			e.printStackTrace();
		}
	}
	public void registerAPlayer(ArrayList<String> nameList, 
			ArrayList<Socket> socketList, 
			String name, Socket socket) {
		nameList.add(name); //Name of player is added in list
		socketList.add(socket); //Socket of player is added in a list
		
	}
	public void checkWaitingList(int chosenMode,
								ArrayList<String> nameList, 
								ArrayList<Socket> socketList) {
		if (waitingPlayers.size() != 0) {
			for (int i = 0; i < waitingPlayers.size(); i = i + 1) {
				if (waitingPlayers.get(i).getMode() == chosenMode) {
					registerAPlayer(nameList, 
									socketList, 
									waitingPlayers.get(i).getName(), 
									waitingPlayers.get(i).getSocket());
					waitingPlayers.remove(i);
					System.out.println("find a waiting player"
									+ " who wants to play the game");
					break; 
				} // end of if (waitingPlayers.get(i).getMode() == chosenMode)
			} // end of for (int i = 0; i < waitingPlayers.size(); i = i + 1) 
		} // end of if (waitingPlayers.size() != 0)
	}
}
	 
