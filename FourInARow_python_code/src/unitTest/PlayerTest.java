package unitTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import controllerOfClient.Player;
import model.Color;
import model.Model;

public class PlayerTest { 
	Player player;
	ServerSocket serverSocket = null;
	Socket socket = null;
	@Before  public void setUp() throws UnknownHostException, IOException {    
		try {
			this.serverSocket = new ServerSocket(1111);
			System.out.println("serverSocket has been created ");
		} catch (IOException e) {
			System.out.println("server can not be created");
		}
		socket = serverSocket.accept(); //=new Socket ("localhost",1111);
		player = new Player("Beny", Color.Blue,  socket, 2);
	}


	@Test
	public void getColorTest() {
		assertEquals(player.getColor(), Color.Blue);
	}

	@Test
	public  void getNameTest()
	{
		assertEquals(player.getName(), "Beny");
	}
	@Test
	public  void getModeTest() {
		assertEquals(player.getMode(),2);
	}
	@Test
	public void getSocketTest()
	{
		assertEquals(player.getSocket(),socket);
	}
}
