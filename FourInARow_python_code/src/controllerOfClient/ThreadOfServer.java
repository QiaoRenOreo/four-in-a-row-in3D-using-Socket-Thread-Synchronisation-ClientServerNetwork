package controllerOfClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ThreadOfServer extends Thread 
{
	private Socket playerSocket;//socket of player
	private String name;
	
	/**
	 * @param socket	: Socket of player(Client)
	 */
	public ThreadOfServer(Socket socket) 
	{
		this.playerSocket = socket;
	}

	public void run() 
	{
		try {
			//New writer and reader for client is created for a particular socket
			String message = null; 
			PrintWriter printWriter = new PrintWriter(playerSocket.getOutputStream(), true);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			//System.out.println("thread of client");
			//It runs and reads from server and writes to player 
			while ((message = bufferedReader.readLine()) != null) 
			{
				printWriter.println(message);
				System.out.println("thread of server ");
			}
			playerSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
