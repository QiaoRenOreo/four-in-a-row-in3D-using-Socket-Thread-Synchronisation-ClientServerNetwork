package controllerOfServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import model.Model;
import viewOfClient.View;
import controllerOfClient.*;
import model.Color;
/**
 * 
 * @author QIAO
 *
 */
 
public class Controller implements Runnable {
	private Model model;
	private View view;
	private Player[] playerList;
	private ArrayList<Player> waitingPlayers;
	private int noOfPlayers;
	private int mode;
	private PrintWriter[] outPToP, outPToE, outEToE;
	private BufferedReader[] inPToP, inPToE, inEToE;
	private boolean isConnected = true;
	private boolean clientAlive;
	private boolean serverAlive;
	/**
	 * constructor communicates with mode and view.
	 * outPToP: an array of print writers. 
	 * 			stores the print writers for 2 players in a PtoP game
	 * outPToE: an array of print writers. 
	 * 			stores the print writers for 2 players in a PtoE game
	 * outEToE: an array of print writers. 
	 * 			stores the print writers for 2 players in a EtoE game
	 * inPToP: an array of Buffered Readers. 
	 * 			stores the Buffered Readers for 2 players in a PtoP game
	 * inPToE: an array of Buffered Readers. 
	 * 			stores the Buffered Readers for 2 players in a PtoE game
	 * inEToE: an array of Buffered Readers. 
	 * 			stores the Buffered Readers for 2 players in a etoE game
	 * @param m model
	 * @param v view
	 * @param theMode the mode of the game
	 * @param playerL the player list of the game
	 * @param waitPlayers the list of players 
	 * who were abandoned by its opponent 
	 * and wants to wait for another player 
	 */
	public Controller(Model m, 
			View v, 
			int theMode, 
			Player[] playerL, 
			ArrayList<Player> waitPlayers) 	{
		this.model = m;
		this.view = v;
		this.mode = theMode;
		this.playerList = playerL;
		this.noOfPlayers = 2;
		this.waitingPlayers = waitPlayers;
		
		this.outPToP = new PrintWriter[2];
		this.inPToP = new BufferedReader[2];
		
		this.outPToE = new PrintWriter[2];
		this.inPToE = new BufferedReader[2];
		
		this.outEToE = new PrintWriter[2];
		this.inEToE = new BufferedReader[2];
	}
    //@ invariant (outPToP.length==2)
    //@ invariant (inPToP.length==2)
    //@ invariant (outPToE.length==2)
	//@ invariant (inPToE.length==2)
    //@ invariant (outEToE.length==2)
	//@ invariant (inEToE.length==2)

	/**
	 * when controller starts, it executes the run method.
	 * the run method calls the play() method
	 * when the client crashes or player closes its window, error messages are given to the server
	 */
	public void run() {
		try {
			play();
		} catch (IOException e) {
			
			System.out.println("Game is over");
			System.exit(0);
		}
	}

	/**
	 * control the game based on 3 cases: 
	 * mode ==0 (human vs human)|| mode==1 (human vs AI)|| mode==2 (AI vs AI)
	 * 
	 * if human vs human, do the following: 
	 * 		1)copy two players into model so that the model can access to them
	 * 		2)create inputstream and outputstream of each player. 
	 * 		so they can give input to console. 
	 * 		they also can be informed by the server about what's going on
	 * 		3)game starts. the player can input EXIT to quit the game, 
	 * 		or a number to drop a piece, or HINT to get a hint
	 * 		4)the server reacts to player's input
	 * 		5)if a new piece has been droped, 
	 * 		new board is shown to both palyers
	 * 		check whether anybody wins or draw or game should continue
	 * 		6)if one player wants to exit the game, delete this player from the game. 
	 * 		asks whether the other player wants to exit or not. 
	 * 		if yes, this player is moved into waitingPlayer list. 
	 * 		if not, delete this player from the game.
	 * 		7) after the game is over, 
	 * 		ask two players whether they want to start a new game or not,
	 * 		if both of them want to play a new game, then start a new game (in same mode)
	 * 		if only one of them wants to play a new game, move this player to waitingPlayer list
	 * 		and delete the other player
	 * 		if none of them want to play a new game, delete both players from the game
	 * 
	 * if human vs AI, do the following: 
	 * 		process is same as above. 
	 * 		the only difference is that during the game, 
	 * 		AI do not give EXIT or hint command. AI drop a piece directly.
	 * 
	 * if human vs AI, do the following: 
	 * 		process is similar as above. 
	 * 		but there is no hint, no EXIT during the game. 
	 * 		because of the time, restarting a new game is not implemented yet
	 * @throws IOException
	 */
	public void play() throws IOException  {
		p("controller: mode=" + mode);
		if (mode == 0) { //person to person
		
			boolean newGamePtoP = true;
			while (newGamePtoP) {
				modelGetPlayersAndCreateInOut(model.pToPArrayInterface, outPToP, inPToP, 0);
				
				outPToP[0].println(model.printBoard());
				outPToP[1].println(model.printBoard());
				boolean runningPtoP = true;
		
				while (runningPtoP) { 
					p("start while loop, runningPtoP=" + runningPtoP);
					for (int index = 0; index < 2; index++)  {
						if (runningPtoP) {
							p("controller: while loop beginning, index=" + index);
							outPToP[index].println(model.pToPArrayInterface[index].getName() 
													+ ", enter a field number, "
													+ "or HINT to get a hint, "
													+ "or EXIT to exit the game.");
							printWaitingMessagePtoP(index);
						
							String line = inPToP[index].readLine();
							boolean isLineValidPtoP = false;
							while (isLineValidPtoP == false) {
								outPToP[index].println("your input has been read");
								if (line.startsWith("EXIT"))  {
									runningPtoP = false;
									newGamePtoP = false;
							    	exit(model.pToPArrayInterface, index, outPToP, inPToP);
									isLineValidPtoP = true;
									outPToP[index].println("runningPtoP=" + runningPtoP);
							    } else if (line.startsWith("HINT"))    {
							    	int suggestedField = model.bestFieldToMove(
							    			model.pToPArrayInterface[index].getColor());
							    	outPToP[index].println("you can drop a piece on the filed: "
							    			+ suggestedField + ". enter a filed: ");
							    	line = inPToP[index].readLine();
							    }  else if (isInteger(line)) {
							    	int data = Integer.parseInt(line);
							    	if (!model.isField(data)) {
							    		outPToP[index].println(
							    				"input number should be in range from 0 to 63. "
							    				+ "enter another field: ");
							    		line = inPToP[index].readLine();
							    	} else if (model.getField(data) != Color.EMPTY) {
							    		outPToP[index].println(
							    				"this field has been occupied. "
							    				+ "enter another filed: ");
							    		line = inPToP[index].readLine();
							    	} else if (model.isEmptyField(data))  {
										model.setField(data, 
												model.pToPArrayInterface[index].getColor());
										outPToP[0].println(model.printBoard());
										outPToP[1].println(model.printBoard());
										isLineValidPtoP = true;
									}
								} else {
									outPToP[index].println("please give a valid input");
									line = inPToP[index].readLine();
								}
							} // end of loop while (isLineValid==false)

							if (model.doesGameOver()) { 
								printGameResult(index, model.pToPArrayInterface, outPToP);
								boolean willingnessPtoPThisPlayer =
										startNewGameOrNot(index, outPToP, inPToP, 
												model.pToPArrayInterface);
								boolean willingnessPtoPOpponent =
										startNewGameOrNot(opponentIndex(index), outPToP, inPToP,
												model.pToPArrayInterface);
								if (willingnessPtoPThisPlayer == true 
										&& willingnessPtoPOpponent == true) {
									outPToP[index].println("a new game starts");
									outPToP[opponentIndex(index)].println("a new game starts");
									runningPtoP = false;
									newGamePtoP = true;
								} else if (willingnessPtoPThisPlayer == false 
										&& willingnessPtoPOpponent == true) {
									exit(model.pToPArrayInterface, index, outPToP, inPToP);
									newGamePtoP = false;
								} else if (willingnessPtoPThisPlayer == true 
										&& willingnessPtoPOpponent == false) {
									exit(model.pToPArrayInterface, 
											opponentIndex(index), outPToP, inPToP);
									newGamePtoP = false;
								} else {
									exit(model.pToPArrayInterface, 
											index, outPToP, inPToP);
									exit(model.pToPArrayInterface, 
											opponentIndex(index), outPToP, inPToP);
									newGamePtoP = false;
								}
								model.initializeBoard();
							} //end of: if (model.doesGameOver()) 
						} // end of if (runningPtoP)
	
					} // end of: for (int index = 0; index < 2; index++) 
					
				} //end of: while (runningPtoP)
			} //end of: while (newGame==true)
		} //end of : if (mode == 0)


			
		if (mode == 1) { //person to AI 
		
			boolean newGamePtoE = true;
			int thinkingTimeAI = 1500; //default number
			while (newGamePtoE) {
				modelGetPlayersAndCreateInOut(model.pToEArrayInterface, outPToE, inPToE, 1);

				outPToE[0].println(model.pToEArrayInterface[0].getName()
						+ ", you are human. \n");
				outPToE[0].println("waiting for "
						+ model.pToEArrayInterface[1].getName()
						+ " to set an AI thinking time");
				
				outPToE[1].println(model.pToEArrayInterface[1].getName() 
						+ ", you are AI.");
				outPToE[1].println("Set an AI thinking time (Give an integer):");
				thinkingTimeAI = Integer.parseInt(inPToE[1].readLine());
				
				outPToE[0].println(model.printBoard());
				
				boolean runningPtoE = true;
				while (runningPtoE) {
					for (int index = 0; index < 2; index++)  {
						if (runningPtoE) {
							p("controller: while loop, index=" + index);
							if (index == 0) { //human 's turn
							
								outPToE[index].println("\n" 
									+ model.pToEArrayInterface[index].getName() 
									+ ", Enter a field number,"	
									+ "or HINT to get a hint, "
									+ "or EXIT to exit the game.");
								
								outPToE[1].println("waiting for "
									+ model.pToEArrayInterface[index].getName()
									+ " to drop a piece");
								
								String line = inPToE[index].readLine();
								boolean isLineValidPtoE = false;
								while (isLineValidPtoE == false) {
									outPToE[index].println("your input has been read");
									if (line.startsWith("EXIT")) {
										runningPtoE = false;
										newGamePtoE = false;
								    	exit(model.pToEArrayInterface, index, outPToE, inPToE);
										isLineValidPtoE = true;
										outPToE[index].println("runningPtoE=" + runningPtoE);
									} else if (line.startsWith("HINT"))   {
								    	int suggestedField = model.bestFieldToMove(
								    			model.pToEArrayInterface[index].getColor());
								    	outPToE[index].println(
								    			"you can drop a piece on the filed: "
								    			+ suggestedField + ". enter a filed: ");
								    	line = inPToE[index].readLine();
								    } else if (isInteger(line)) {
								    	int data = Integer.parseInt(line);
								    	
								    	if (!model.isField(data)) {
								    		outPToE[index].println(
								    				"input number should be in range from 0 to 63. "
								    				+ "enter another field: ");
								    		line = inPToE[index].readLine();
								    	} else if (model.getField(data) != Color.EMPTY) {
								    		outPToE[index].println(
								    				"this field has been occupied. "
								    				+ "enter another filed: ");
								    		line = inPToE[index].readLine();
								    	} else if (model.isEmptyField(data))  {
											model.setField(data, 
													model.pToEArrayInterface[index].getColor());
											isLineValidPtoE = true;
										} else {
											outPToE[index].println("please give a valid input");
											line = inPToE[index].readLine();
										}
									} //end of while (isLineValid==false)
								} // end of if (index==0)
							} else { //if (index==1) means AI's turn 
								outPToE[0].println("waiting for " 
													+ model.pToEArrayInterface[index].getName()
													+ " to drop a piece");
					            try {
					                Thread.sleep(thinkingTimeAI); //1500
					                System.out.println("thinkingTimeAI=" + thinkingTimeAI);
					            } catch (InterruptedException exp) {
					            	System.out.println("there is an InterruptedException");
					            }
								model.startAIThread(thinkingTimeAI, 
											model.pToEArrayInterface[index].getColor());
								//pToEArrayInterface[index].getColor()
							} // end of if (index==1)
							
							//both index=0 and index =1 should execute the following:
							outPToE[0].println(model.printBoard());
							outPToE[1].println(model.printBoard());

							if (model.doesGameOver())  {
								printGameResult(index, model.pToEArrayInterface, outPToE);
								boolean startOrNotThisPlayer = startNewGameOrNot(
										index, outPToE, inPToE, model.pToEArrayInterface);
								boolean startOrNotOpponent = startNewGameOrNot(
										opponentIndex(index), outPToE, inPToE, 
										model.pToEArrayInterface);
								if (startOrNotThisPlayer == true && startOrNotOpponent == true) {
									outPToE[index].println("a new game starts");
									outPToE[opponentIndex(index)].println("a new game starts");
									runningPtoE = false;
									newGamePtoE = true;
								} else if (startOrNotThisPlayer == false 
										&& startOrNotOpponent == true)  {
									exit(model.pToEArrayInterface, index, outPToE, inPToE);
									newGamePtoE = false;
								} else if (startOrNotThisPlayer == true 
										&& startOrNotOpponent == false) {
									exit(model.pToEArrayInterface, opponentIndex(index),
											outPToE, inPToE);
									newGamePtoE = false;
								}							
								model.initializeBoard();
							} //end of if (model.doesGameOver()) 
						} //end of if (runningPtoE)

					} // end of: for (int index = 0; index < 2; index++) 
				} // end of while (runningPtoE)
			} //end of while (newGame)
		} //end of if (mode==1)
			
			
		if (mode == 2) { //person to AI
			boolean newGameEtoE=true;
			while (newGameEtoE) {
				int thinkingTimeAI0 = 100;
				int thinkingTimeAI1 = 100;
				
				modelGetPlayersAndCreateInOut(model.eToEArrayInterface, outEToE, inEToE, 2);
				
				outEToE[0].println(model.eToEArrayInterface[0].getName() + ", you are AI.");
				outEToE[0].println("Set an AI thinking time (Give an integer):");
				thinkingTimeAI0 = Integer.parseInt(inEToE[0].readLine());
				
				outEToE[1].println(model.eToEArrayInterface[1].getName() + ", you are AI.");
				outEToE[1].println("Set an AI thinking time (Give an integer):");
				thinkingTimeAI1 = Integer.parseInt(inEToE[1].readLine());
				boolean runningEtoE = true;
				while (runningEtoE) {
					for (int index = 0; index < noOfPlayers; index++)  {
						if (runningEtoE) {
							if (index == 0) { //AI_0's turn
								p("AI_0 's turn");
								outEToE[1].println("waiting for "
													+ model.eToEArrayInterface[index].getName()
													+ " to drop a piece");
					            try {
					                Thread.sleep(thinkingTimeAI0);
					                System.out.println("thinkingTimeAI0=" + thinkingTimeAI0);
					            } catch (InterruptedException exp) {
					            	System.out.println("there is an InterruptedException");
					            }
								model.AIMove(model.eToEArrayInterface[index].getColor());
								p("AI_0 made a move");
							 
							} else { //if (index==1) means AI_1's turn 
								p("AI_1 's turn");
								outEToE[0].println("waiting for "
													+ model.eToEArrayInterface[index].getName()
													+ " to drop a piece");
					            try {
					                Thread.sleep(thinkingTimeAI1);
					                p("thinkingTimeAI0=" + thinkingTimeAI1);
					            } catch (InterruptedException exp) {
					            	p("there is an InterruptedException");
					            }
								model.AIMove(model.eToEArrayInterface[index].getColor());
								p("AI_1 made a move");
							} //either AI_0 made a move or AI_1 made a move
							outEToE[0].println(model.printBoard());
							outEToE[1].println(model.printBoard());

							if (model.doesGameOver())  {
								printGameResult(index, model.eToEArrayInterface, outEToE);
								
								boolean willingnessEtoEThisPlayer =
										startNewGameOrNot(index, outEToE, inEToE, 
												model.eToEArrayInterface);
								boolean willingnessEtoEOpponent =
										startNewGameOrNot(opponentIndex(index), outEToE, inEToE,
												model.eToEArrayInterface);
								if (willingnessEtoEThisPlayer == true 
										&& willingnessEtoEOpponent == true) {
									outEToE[index].println("a new game starts");
									outEToE[opponentIndex(index)].println("a new game starts");
									runningEtoE = false;
									newGameEtoE = true;
								} else if (willingnessEtoEThisPlayer == false 
										&& willingnessEtoEOpponent == true) {
									exit(model.eToEArrayInterface, index, outEToE, inEToE);
									newGameEtoE = false;
								} else if (willingnessEtoEThisPlayer == true 
										&& willingnessEtoEOpponent == false) {
									exit(model.eToEArrayInterface, 
											opponentIndex(index), outEToE, inEToE);
									newGameEtoE = false;
								} else {
									exit(model.eToEArrayInterface, 
											index, outEToE, inEToE);
									exit(model.eToEArrayInterface, 
											opponentIndex(index), outEToE, inEToE);
									newGameEtoE = false;
								}
								model.initializeBoard();
																
							} //end of if (model.doesGameOver())
						} //end of if (runningEtoE)

					} // end of: for (int index = 0; index < 2; index++) 
				} //while (runningEtoE)
			} // end of while (newGameEtoE)
			System.exit(0);
		} // end of if (mode == 2)
	} // end of play ()
	
	/**
	 * print the result of the game: draw or some win 
	 * @param index
	 * @param playersInModel
	 */
	/*@
	 * requires index==0||index==1
	 * requires playersInModel!=null
	 */
	public void printGameResult(int index, 
			PlayerInterface[]playersInModel,
			PrintWriter[]prOut) {
		if ((model.getStatus() == 5) || (model.getStatus() == 6))  {
			prOut[0].println(playersInModel[index].getName() + " Won the Game!!");
			prOut[1].println(playersInModel[index].getName() + " Won the Game!!");
		} 	else if (model.getStatus() == 7)  {
			prOut[0].println("It is a draw !");
			prOut[1].println("It is a draw !");
		}
	}
	/**
	 * print message the opponent. tell it to wait for this player to drop a piece
	 * @param index player's turn
	 */
	/*@
	 * requires index==0||index==1
	 */
	public void printWaitingMessagePtoP(int index) 	{
		if (index == 0) 	{
			outPToP[1].println("waiting for "
						+ model.pToPArrayInterface[index].getName()
						+ " to drop a piece");
		} else { //if (index==1)
			outPToP[0].println("waiting for "
						+ model.pToPArrayInterface[index].getName()
						+ " to drop a piece");
		}
	}
	/**
	 * check whether a string is a integer or not an integer
	 * @param input
	 * @return true means is a integer. false otherwise
	 */
	/*@
	 * requires input!=null
	 */
    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }  catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * print a message in system.out
     * @param s
     */
    /*@
     * requires s!=null
     */
	public void p(String s) {
		System.out.println(s);
		return;
	}
	/**
	 * delete a player from the game
	 * @param playersInModel
	 * @param index
	 * @throws IOException
	 */
	/*@requires playersInModel!=null
	 * requires index==0||index==1
	 * ensures playersInModel[index]==null && playerList[index]==null
	 */
	public void deleatePlayer(PlayerInterface[]playersInModel, int index) 
			throws IOException {
	    try {
	    	playersInModel[index].getSocket().close();
	        System.out.println("The player exited!");
	    } catch (IOException e) { 
	    	System.out.println("failed to close the socket"); 
	    }
	    
		model.initializeBoard();
		
	}
	
	/**
	 * get the index of the opponent
	 * @param index
	 * @return 0 or 1
	 */
	/*@requires index==0||index==1
	 * ensures \result!=index && (\result==0||\result==1)
	 */
	public int opponentIndex(int index) {
		if (index == 0) {
			return 1;
		}
		return 0;
	}
	/**
	 * if one player wants to exit the game, delete this player from the game. 
	 * 		asks whether the other player wants to exit or not. 
	 * 		if yes, this player is moved into waitingPlayer list. 
	 * 		if not, delete this player from the game.
	 * @param playersInModel
	 * @param index
	 * @param prOut
	 * @param brIn
	 * @throws IOException
	 */
	/*@requires playersInModel!=null
	 * requires index==0||index==1
	 * requires prOut!=null
	 * requires brIn!=null
	 */
	public void exit(PlayerInterface[]playersInModel, 
						int index, 
						PrintWriter[]prOut, 
						BufferedReader[]brIn) 
						throws IOException {
		if (playersInModel[opponentIndex(index)] != null) {
			prOut[opponentIndex(index)].println(
					playersInModel[index].getName()
					+ " exited the game. do you want to wait or not? "
					+ "enter WAIT to wait for another player. enter EXIT to exit the game.");
			String answer = brIn[opponentIndex(index)].readLine();
			
			if (answer.startsWith("EXIT")) {
				//deleatePlayer(playersInModel, opponentIndex(index));
				System.out.println("The other player also exited!");
				
			} else if (answer.startsWith("WAIT")) {
				waitingPlayers.add(playerList[opponentIndex(index)]);
				System.out.println(playerList[opponentIndex(index)].getName()
						+ " has been added to waiting player list");
				model.initializeBoard();
				outPToP[opponentIndex(index)].println(
						"please wait. A new game will start when another player comes in. ");
			} else {
				prOut[opponentIndex(index)].println("invalid answer");
			}
		}
		deleatePlayer(playersInModel, index);
	}
	
	/**
	 * ask a player: do you want to start a new game or not
	 * @param playerIndex
	 * @param prOut
	 * @param brIn
	 * @param playersInModel
	 * @return true if the player wants to restart a new game. false otherwise
	 * @throws IOException
	 */
	/*@
	 * requires playerIndex==0||playerIndex==1
	 * requires prOut!=null
	 * requires brIn!=null
	 * requires playersInModel!=null
	 */
	public boolean startNewGameOrNot(int playerIndex, 
									PrintWriter[]prOut, 
									BufferedReader[]brIn, 
									PlayerInterface[]playersInModel) 
			throws IOException {
		prOut[playerIndex].println(
				"do you want to play another new game (in the same mode)? "
				+ "enter Y or N. Y means yes. N means no");
		String answer = brIn[playerIndex].readLine();
		if (answer.equals("Y")) {
			return true;
		} else if (answer.equals("N")) {
			deleatePlayer(playersInModel, playerIndex);		
			return false;
		} else {
			prOut[playerIndex].println("invalid input. you have exited the game");
		}
		return false;
	}
	
	/**
	 * this method does two things:
	 * 1)copy two players into model so that the model can access to them
	 * 2)create inputstream and outputstream of each player. 
	 * 		so they can give input to console. 
	 * @param playersInModel
	 * @param prOut array of print writer
	 * @param brIn array of buffered reader
	 * @param mode
	 * @throws IOException
	 */
	/*@
	 * requires playersInModel!=null
	 * requires prOut!=null
	 * requires brIn!=null
	 * requires mode==0||mode==1||mode==2
	 */
	public void modelGetPlayersAndCreateInOut(
			PlayerInterface[]playersInModel, 
			PrintWriter[]prOut, 
			BufferedReader[]brIn, 
			int mode
	) throws IOException {
		for (int index = 0; index < noOfPlayers; index++) {
			playersInModel[index] = playerList[index];
			p("get playerList=" + playerList[index].getName());
			
			try { 
				prOut[index] = new PrintWriter(
						playersInModel[index].getSocket().getOutputStream(), true);
				if (mode == 0) {
					p("outPToP[index=" + index + "]");	
				} else if (mode == 1) {
					p("outPToE[index=" + index + "]");	
				} else {
					p("outEToE[index=" + index + "]");	
				}
			} catch (IOException e) {
				p("fail to create PrintWriter Out of socket, in mode " 
					+ mode + " of player " + index);
			}

			try {	//create BufferedReader for the player 0 and 1
				brIn[index] = new BufferedReader(
						new InputStreamReader(playersInModel[index].getSocket().getInputStream()));
				if (mode == 0) {
					p("inPToP[index=" + index + "]");
				} else if (mode == 1) {
					p("inPToE[index=" + index + "]");
				} else  {
					p("inEToE[index=" + index + "]");
				}
			} catch (IOException e) {
				p("fail to create BufferedReader In of socket, in mode "
						+ mode + " of player " + index);
			}

		} //finish creating out and in 

	}	
	
	/**
	 * check whether the client wants to disconnect or not
	 * @param br
	 * @throws IOException 
	 */
	/*@
	 * requires br!=null
	 * requires s!=null
	 */
	public boolean closeConnection(BufferedReader br, Socket s) throws IOException {
	    if (br.read() == -1) {  
	        isConnected = false;  
	        br.close();  
	        s.close();  
	        return true;  
	    }  
	    return false;
	}
	/**
	 * br.ready()returns true if data is readable, otherwise false  
	 * @param br
	 * @return
	 * @throws IOException 
	 */
	public boolean checkConnection(BufferedReader br) throws IOException {
	    int c;
		try {  
	        if(br.ready()) {
		        while(br.ready())//keep reading until the data has been fully read  
		        {  
		            if((c=br.read())!=-1)  
		            {  
		                //read input   
		            }     
		        } 
	        } 
	        return true;
	    }catch (IOException e)    {  
	            p("disconnection detected ");
	            return false;
	    }
	}
	
	/**
	 * get connection status of server
	 * @return
	 */
	/*@
	 * ensures \result==serverAlive;
	 */
	public boolean doesServerAlive() {
		return serverAlive;
	}
	/**
	 * get connection status of client
	 * @return
	 */
	/*@
	 * ensures \result==clientAlive;
	 */
	public boolean doesClientAlive() {
		return clientAlive;
	}
	/**
	 * whether a message can be send successfully or not
	 * @param s
	 * @param message
	 * @return true means successfully sent. false means fail
	 * @throws IOException
	 */
	public boolean doesSendSucceed(Socket s, String message) throws IOException	{
		try {
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			out.println(message);
			return true; 
		} catch (IOException e) {
			return false;
		}
	}
	/**
	 * check whether the server is closed or not.
	 * urgent data need to be sent every time before sending the actual data
	 * when the urget data is received successfully, this means that the server is alive
	 * the disadvantage of this method is that it will slow down the speed of communication
	 * @param s
	 * @return true means the server is close. false mean the server is open
	 * @throws IOException
	 */
	public void isServerClose(Socket s) throws IOException {
		try {
			s.sendUrgentData(0);
			serverAlive=true;
		} catch (IOException e) {
			serverAlive=false;
		}
	}

}