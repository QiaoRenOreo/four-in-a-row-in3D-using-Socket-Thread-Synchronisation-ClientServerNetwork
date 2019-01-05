package controllerOfServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import controllerOfClient.Player;
import controllerOfClient.*;

public class GameServer  {
	private ServerSocket serverSocket; 
	private int port; //Port to which connect to
	private Scanner scanner = new Scanner(System.in);
	private ArrayList<String> humanVsHumanName;
	private ArrayList<String> humanVsAiName; 
	private ArrayList<String> aiVsAiName;
	//For storing details of names of players
	private boolean serverAlive;
	
	private ArrayList<Socket> humanVsHumanSocket;
	private ArrayList<Socket>	humanVsAiSocket;
	private ArrayList<Socket>	aiVsAiSocket; 
	//For storing details of sockets of players
	
	private ArrayList<Player> waitingPlayers; 
	//a list of players. These players are abandoned by other existed players

	public GameServer() throws IOException {
		System.out.println("to start a server, please enter a port number:");

		boolean canServerListenOnThePort = createServer();
		while (canServerListenOnThePort == false) {
			canServerListenOnThePort = createServer();
		}
		this.humanVsHumanName =   new ArrayList<String>();
		this.humanVsAiName =      new ArrayList<String>();
		this.aiVsAiName =         new ArrayList<String>();
		this.humanVsHumanSocket = new ArrayList<Socket>();
		this.humanVsAiSocket =     new ArrayList<Socket>();
		this.aiVsAiSocket =        new ArrayList<Socket>();
		this.waitingPlayers =    new ArrayList<Player>();
	}
	
	public boolean createServer() {
		boolean boo = false;
		while (boo == false) {
			String str = read();
			int portInt = 0;
			try {
				portInt = Integer.parseInt(str);
				port = portInt;
				boo = true;
			} catch (NumberFormatException e) {
				boo = false;
				System.out.println("The port you entered was invalid, "
						+ "please input another port: ");
			}
		}
		try {
			this.serverSocket = new ServerSocket(port);
			System.out.println("serverSocket has been created ");
			return true;
		} catch (IOException e) {
			System.out.println("server can not be created");
			System.out.println("please enter a new port number");
			return false;
		}
	}
	
	public void start() throws IOException {
		while (true) {
			new Thread(
					new PlayerDetails(
											serverSocket.accept(), 
											this.humanVsHumanName,
											this.humanVsAiName,
											this.aiVsAiName,
											this.humanVsHumanSocket,
											this.humanVsAiSocket,
											this.aiVsAiSocket,
											this.waitingPlayers)
					).start();
		}
	}
	public String read() {
		String i = "";
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
			i = bReader.readLine();
		} catch (IOException e) {
			p("Error while reading");
		}
		return i;
	}

	public boolean doesServerAlive() {
		return serverAlive;
	}
	public void p(String s) {
		System.out.println(s);
		return;
	}
	public void gameStarters() {		
		System.out.println("gameStarters() is running");
		new Thread(new StarterHumanVsHuman(
								humanVsHumanName,
								humanVsHumanSocket,
								waitingPlayers)).start();
		new Thread(new StarterHumanVsAi(
								humanVsAiName, 
								humanVsAiSocket,
								waitingPlayers)).start();
		new Thread(new StarterAiVsAi(
								aiVsAiName,
								aiVsAiSocket,
								waitingPlayers)).start();
	}
    public boolean checkConnected(Socket socket, String name)    {
        System.out.println(name + ".isClosed():" + socket.isClosed());
        System.out.println(name + ".isConnected():" + socket.isConnected());
        if (socket.isClosed() == false && socket.isConnected() == true) {
            System.out.println("is connected");
        	return true;
		}
        p("not connected");
        return false;
    }

	public static void main(String[] args) throws IOException {
		GameServer gameServer = new GameServer();
		gameServer.gameStarters();
		gameServer.start();
	}
}
