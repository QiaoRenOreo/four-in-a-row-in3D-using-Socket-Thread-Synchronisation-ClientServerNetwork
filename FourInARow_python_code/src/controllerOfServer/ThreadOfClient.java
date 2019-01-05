package controllerOfServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ThreadOfClient extends Thread {
	
	private Socket playerSocket; //Socket of player
	
	/**
	 * @param socket	: Socket of player(Client) is passed
	 */
	public ThreadOfClient(Socket socket){
		this.playerSocket = socket;
	}
	 
	public void run(){
		String message = null;
		try {
			
			//New reader for client is created for a particular 
			BufferedReader BR = new BufferedReader
					(new InputStreamReader(playerSocket.getInputStream()));
			//System.out.println("thread of server");
			//It runs and reads from server and writes to player 
			while((message = BR.readLine()) != null){
				System.out.println(message);
				System.out.println("thread of client ");

			}
			playerSocket.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
}