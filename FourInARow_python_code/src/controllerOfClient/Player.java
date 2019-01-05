package controllerOfClient;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import model.Color;

public class Player implements PlayerInterface 
{	
	private String name;
	private Color color;
	private Socket playerSocket;
	private int mode;
	private boolean clientAlive;
	
	public Player(String name, Color c, Socket socket, int choosenMode)
	{
		this.name = name;
		this.color = c;
		this.playerSocket = socket;
		this.mode=choosenMode;
		this.clientAlive=true;
	}
	
	public boolean doesClientAlive() {
		return clientAlive;
	}
	public Socket getSocket()
	{
		return playerSocket;
	}

	public Color getColor() {
		return this.color;
	}


	public String getName()
	{
		return name;
	}

	@Override
	public int getMode() {
		return mode;
	} 
	public void closeConnection() throws IOException {
		try {
			playerSocket.close();
		} catch (IOException e) {
			System.out.println("can nto close client's socket");
		}
	}
	
}