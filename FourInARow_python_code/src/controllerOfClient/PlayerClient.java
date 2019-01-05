package controllerOfClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import controllerOfServer.ThreadOfClient;

public class PlayerClient
{
	String ip; 
	int port;
	Socket clientSocket;
	String data;
	private Scanner scanner = new Scanner(System.in);
	/**
	 * @param ip	: IP address of server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public PlayerClient() throws UnknownHostException, IOException
	{
		p("log in as a client");
		p("Please input the IP address of server:");
		ip = scanner.nextLine();

		p("Please input the port number:"); 
		boolean boo = false;
		while(boo == false)
		{
			String str = read();
			int PortInt = 0;
			try
			{
				PortInt = Integer.parseInt(str);
				port = PortInt;
				boo = true;
			}catch(Exception e){
				System.out.println("The port you entered was invalid, please input another port: ");
			}
		}

		try {
			this.clientSocket = new Socket(ip,port);//(ip, port);
			System.out.println("Successfully connected to the server.");
		}catch (IOException e) {
			System.out.println("Error while connecting to " + ip + " | " + port);
		}
	
	}
	public Socket getClientSocket()
	{
		return this.clientSocket;
	}
	public void p(String s) {
		System.out.println(s);
		return;
	}
	public String read() {
		String i = "";
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
			i = bReader.readLine();
		} catch(Exception e) {p("Error while reading");}
		return i;
	}
	public static void main(String args[]) throws UnknownHostException, IOException
	{ 
		//A new client is made
		PlayerClient playerClient = new PlayerClient();
		//Two threads are started for listening to and writing to servers
		
		new ThreadOfServer(playerClient.getClientSocket()).start();
		//System.out.println("new ClientThread(player.getClientSocket()).start();");
		new ThreadOfClient(playerClient.getClientSocket()).start();
		//System.out.println("new ServerThread(player.getClientSocket()).start();");
	}
	
	
}