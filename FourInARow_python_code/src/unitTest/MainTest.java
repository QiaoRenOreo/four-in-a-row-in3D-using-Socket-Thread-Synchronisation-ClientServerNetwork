package unitTest;

import java.io.IOException;

import controllerOfClient.PlayerClient;
import controllerOfServer.GameServer;

public class MainTest {
	static GameServer gameeServer;
	static PlayerClient playerA;
	static PlayerClient playerB;
	static PlayerClient playerC;
	static PlayerClient playerD;
	static PlayerClient playerE;
	static PlayerClient playerF;
	String[] args = {""};
	public static void main(String[] args) throws IOException {
		gameeServer = new GameServer();
		gameeServer.main(args);
		playerA = new PlayerClient();
		playerA.main(args);
		playerB = new PlayerClient();
		playerB.main(args);
		playerC = new PlayerClient();
		playerC.main(args);
		playerD = new PlayerClient();
		playerD.main(args);
		playerE = new PlayerClient();
		playerE.main(args);
		playerF = new PlayerClient();
		playerF.main(args);
	}
	
	
	
}
