package model;
import model.Colour;
import view.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;



public class Model extends Observable 
{
	private Colour[] fields;
	public static final int DIM =4; 
	private static final String[] NUMBERING =   
	{" 0 |  1 |  2 |  3 ", "  4 |  5 |  6 |  7 ", "  8 |  9 | 10 | 11 ", "12 | 13 | 14 | 15", 
	" 16 | 17 | 18 | 19 ", " 20 | 21 | 22 | 23 ", " 24 | 25 | 26 | 27 ", "28 | 29 | 30 | 31",
	" 32 | 33 | 34 | 35 ", " 36 | 37 | 38 | 39 ", " 40 | 41 | 42 | 43 ", "44 | 45 | 46 | 47",
	" 48 | 49 | 50 | 51 ", " 52 | 53 | 54 | 55 ", " 56 | 57 | 58 | 59 ", "60 | 61 | 62 | 63"};
	private static final String LINE = "-------+-------+-------+-------"; 
	private static final String DELIM = "     ";
	int playerTurn;// indicate which player's turn
    
	
	private int status;// indicate the game status
	private int winType; // indicate the winning type 
	private int fieldJustMovedIn;
	private int winPos1; // indicate the winning position
	private int winPos2;
	private int winPos3;
	private int winPos4;


	private int mode;// indicate the mode of game, 0 for PVP, 1 for PVE, 2 for EVE
		
	// -- Constructors -----------------------------------------------	
	public Model()  
    {
    	fields=new Colour[DIM * DIM *DIM];
    	for (int i=0;0 <= i & i < DIM*DIM*DIM;i=i+1)
    	{
    		fields[i]=Colour.EMPTY;
    	}
    	playerTurn=0;//player 0 play first
    	status=0;//in this status, no field is occupied
    	mode=0;//player vs player
    	setChanged();
    }
	 // -- Queries -----------------------------------------------
	public int getDimension()
    {
    	//System.out.println("getDimenstion()"+DIM);
    	return DIM;
    }
    public int getTurn()
    {
    	return playerTurn;
    }
    public int getStatus()
    {
    	return status;
    }
	public int getWinType() {
		return winType;
	}
	public int getMode() {
		return mode;
	}
    public model.Colour getField(int i) 
    {
    	return fields[i];
    }
    public model.Colour getField(int x, int y, int z) //getChangedValue
    {
    	return getField(index(x,y,z));     
    }
    public int getFieldJustMovedIn()
    {
    	return fieldJustMovedIn;
    }

	public void setTurn(int t) {
		playerTurn = t;
		setChanged();
		notifyObservers();
	}

	public void setStatus(int s) {
		status = s;
		setChanged();
		notifyObservers();
	}

	public void changeMode() {
		if (mode == 0) {
			mode=1;//change from 0 to 1. means change from PVP to PVE
		} else if(mode==1) {
			mode=2;//change from 1 to 0. means change from PVE to PVP
		}else if(mode==2){
			mode=0;
		}
		setChanged();
		notifyObservers();
	}
		 // -- Commands -----------------------------------------------

    public void newGame() 
    {
    	for (int i=0;i < (DIM*DIM*DIM); i=i+1)
    	{
    		fields[i] = Colour.EMPTY;
    	}
    	playerTurn = 0;
		//blank = 9;
		status = 0;
		setChanged();
		notifyObservers();
    }
	    
    public void setFieldDirectly(int i, Colour color)
    {
    	fields[i]=color;
    }
	
    public void playerMove(int fieldNumber, int thinkingTime) //public void playerMove(int field, Color color)
    {
		if (status == 0) //no body has made any movement 
		{
			return;
		}
		if (status == 5 || status == 6 ||status == 7) //red win or blue win or draw
		{
			return;
		}
		int oldTurn = playerTurn;
		System.out.println("when player start moving, playerTurn="+ playerTurn);
		if (playerTurn == 0)
		{
			System.out.println("red turn");
			setField (fieldNumber, model.Colour.Red,1);//change player from 0 to 1
		}else //if (playerTurn == 1)
		{
			System.out.println("blue turn");
			setField (fieldNumber, model.Colour.Blue,-1);//change player from 1 to 0
    	}
		win(oldTurn);
		
    	setChanged();
    	notifyObservers();

    	startAIThread(thinkingTime);
	}
    
	public void startAIThread(int thinkingTime)
	{
	    new Thread
	    (
	    		new Runnable()
			    {
			        public void run()
			        {
			    		if (mode != 0 && status == 2)//if this is not human-vs-human
			    		{
				            try {
				                Thread.sleep(thinkingTime);//1500
				            }catch (InterruptedException exp){
				            	System.out.println("there is an InterruptedException");
				            }
			    			if (mode==1)//human vs computer
			    			{
			    				AIMove(Colour.Blue,-1);
			    			}
			    			else if (mode==2)//computer vs computer
			    			{
			    				if(playerTurn==0)//player0 (a computer) is going to move
			    				{
			    					AIMove(Colour.Red,1);
			    				}else if (playerTurn==1)//player1 (a computer) is going to move
			    				{
			    					AIMove(Colour.Blue,-1);
			    				}
			    			}
			    		}
			        }
			    }
	    ).start();		// delay for AI thinking process
	}
	
    public void setField (int i, Colour color,int goToNextPlayer)
    {
    	System.out.println("setField "+i+" to color: "+color);
		int remainder=i%(DIM*DIM);//use remainder to find which horizontal column this filed i is located.
		int changeToNextPlayer=goToNextPlayer;
		if (this.isField(i))
    	{
    		if (this.getField(remainder)==Colour.EMPTY){ //while loop to replace the 4 if-else statement
    			fields[remainder] = color;
    			playerTurn=playerTurn+changeToNextPlayer;
    			System.out.println("in Model, player has moved. new player turn="+playerTurn);
    			status=2;
    			fieldJustMovedIn=remainder;
    			//blank--; do i need this?
    		}else if (this.getField(remainder+DIM*DIM*1)==Colour.EMPTY){
    			fields[remainder+DIM*DIM*1] = color;
    			playerTurn=playerTurn+changeToNextPlayer;
    			System.out.println("in Model, player has moved. new player turn="+playerTurn);
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*1;
    			//blank--;
    		}else if (this.getField(remainder+DIM*DIM*2)==Colour.EMPTY){
    			fields[remainder+DIM*DIM*2] = color;
    			playerTurn=playerTurn+changeToNextPlayer;
    			System.out.println("in Model, player has moved. new player turn="+playerTurn);
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*2;
    			//blank--;
    		}else if (this.getField(remainder+DIM*DIM*3)==Colour.EMPTY){
    			fields[remainder+DIM*DIM*3] = color;
    			playerTurn=playerTurn+changeToNextPlayer;
    			System.out.println("in Model, player has moved. new player turn="+playerTurn);
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*3;
    			//blank--;
    		}else{
    			status=4;//all fields in this z axis have been occupied. player failed to set the field
    		}
    	}else{//do i need "else?" : if (!this.isField(i))
    		status=3;//field index is not in the range [0,DIM*3)
    	}
    }
    
    public void win (int turn)// judge if anyone wins
    {
    	if(this.isWinner(Colour.Red))
    	{
    		playerTurn = turn;//might be needed when asking the winner "do u want to play again". otherwise, useless
    		status=5;
    	}else if (this.isWinner(Colour.Blue))
    	{
    		playerTurn = turn; //might be needed when asking the winner "do u want to play again". otherwise, useless
    		status=6;
    	}else if (this.isFull())//draw
    	{
    		status=7;
    	}
    }
    
    
    /**
     * Returns true if the game is over. The game is over when there is a winner
     * or the whole student is full.
     * @return true if the game is over
     */

    public boolean gameOver() 
    {
    	return (this.isFull() || this.hasWinner()); // status=5(draw)|| status=4 (somebody wins)
    }
    
    public boolean hasWinner() 
    {
    	return (isWinner(Colour.Red)||isWinner(Colour.Blue));
    }

    public boolean isWinner(Colour color) 
    {
    	return ( this.fourInXaxis(color) || this.fourInYaxis(color) 
    			|| this.fourInZaxis(color) ||this.hasDiagonal(color)); 
    }


    public boolean isFull() {
    	int i=0;
    	while (i >= 0 && i < DIM*DIM*DIM)
    	{
    		if (this.getField(i) != Colour.EMPTY)
    		{
    			i=i+1;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	//System.out.println("board isFull() =true");
        return true;
    }
    public void setField(int x, int y, int z, Colour color, int goToNextPlayer) 
    {
    	setField(index(x,y,z), color, goToNextPlayer);
    }

    public int index(int x, int y, int z) 
    {
    	//System.out.println("index is: "+ ( (z-1)*DIM*DIM+(y-1)*DIM+(x-1) )  );
    	return (z-1)*DIM*DIM+(y-1)*DIM+(x-1);
    }

    public boolean isField(int index)
    {
    	return (0 <= index && index< DIM*DIM*DIM);
    }


    public boolean isField(int x, int y, int z) 
    {
    	return ((1 <= x && x <= DIM) && (1 <= y && y <=DIM) &&(1 <= z && z <= DIM));
    }
    

    public boolean isEmptyField(int i) 
    {
    	return (this.isField(i)&&(this.getField(i) == Colour.EMPTY));
    }


    public boolean isEmptyField(int x, int y, int z) 
    {
    	return isEmptyField( index(x,y,z) );
    }
    
    public boolean hasDiagonal(Colour color) 
    {
    	return  (diagonal1_InXYPlane(color)||diagonal2_InXYPlane(color)||diagonal3_InZYPlane(color)
    			||diagonal4_InZYPlane(color)||diagonal5_InXZPlane(color)||diagonal6_InXZPlane(color)
    			||diagonal7_InXYZSpace(color)||diagonal8_InXYZSpace(color)||diagonal9_InXYZSpace(color)
    			||diagonal10_InXYZSpace(color));
    }
    public Model deepCopy() 
    {
    	Model board2=new Model();
    	for (int i=0; i < DIM * DIM* DIM;i=i+1)
    	{
    		board2.setFieldDirectly(i, this.getField(i));
    	}
        return board2;
    }
    public String toString() 
	{
        String s = "";
        for (int k=0;k<DIM; k=k+1)
        {
        	//System.out.println("print board to string,k="+k);
        	s=s+"horizontal level: "+(k+1)+"\n";
	        for (int i = 0; i < DIM; i++) //i=0,1,2,3
	        {
	        	//System.out.println("i before="+i);
	            String row = "";
	            for (int j = 0; j < DIM; j++) //j=0,1,2,3
	            {
	                row = row + " " + getField(j+1,i+1,k+1).toString() + " "; // getField(i+1, j+1).toString()
	                if (j < DIM) // j=0,1,2
	                {
	                    row = row + "|";
	                }
	            }
	            s = s + row + DELIM + NUMBERING[k*DIM+i]; // i * 2=0,2,4,6
	            if (i < DIM ) //i=0,1,2,3
	            {
	                s = s + "\n" + LINE + "\n"; //s = s + "\n" + "-------+-------+-------+-------"+ “\n�?
	            }
	            //System.out.println("i after="+i);
	        }
	        //System.out.println("print board to string,k="+k);
	        s=s+"\n";
        }
        return s;
	}

    
    public boolean fourInXaxis(Colour color) 
    {
    	for (int z=1; z<=DIM; z=z+1)
    	{
    		for (int y=1; y<=DIM; y=y+1)
    		{
	    		int colorCount=0;
	    		//in a fixed horizontal plane (z is locked)
	    		//(1,1,1) (2,1,1) (3,1,1) (4,1,1)
	    		//(1,2,1) (2,2,1) (3,2,1) (4,2,1)
	    		//(1,3,1) (2,3,1) (3,3,1) (4,3,1)
	    		//(1,4,1) (2,4,1) (3,4,1) (4,4,1)
	    		
	    		//(1,1,2) (2,1,2) (3,1,2) (4,1,2)
	    		
	    		for (int x=1; x<=DIM; x=x+1)
	    		{
	        		if (getField(x,y,z)==color)
	        		{
	        			colorCount=colorCount+1;
	        		}
	        		else//this Row which is not fully occupied by m
	        		{
	        			break;//go out of the for loop of x
	        		}
	    		}
	    		if (colorCount==DIM)
	    		{
	    			winType=11;
	    			System.out.println("fourInXaxis=succeed");
	    			return true;
	    		}
    		}//end of loop y
    	}//end of loop z
    	//System.out.println("fourInXaxis=fail");
    	return false;
    }

    /**
     * Checks whether there is a column which is full and only contains the mark
	 * @param m:  the mark of interest
     * @return true if there is a row controlled by m
     */
    /*@ pure */
    public boolean fourInYaxis(Colour color) 
    {
    	for (int z=1; z<=DIM; z=z+1)
    	{	    		
    		for (int x=1; x<=DIM; x=x+1)
    		{
	    		int colorCount=0;
	    		//(1,1,1) (1,2,1) (1,3,1) (1,4,1)
	    		for (int y=1; y<=DIM; y=y+1)
	    		{
	        		if (getField(x,y,z)==color)
	        		{
	        			colorCount=colorCount+1;
	        		}
	        		else//this Row which is not fully occupied by m
	        		{
	        			break;//go out of the for loop of y
	        		}
	    		}
	    		if (colorCount==DIM)
	    		{
	    			winType=12;
	    			System.out.println("fourInYaxis=succeed");
	    			return true;
	    		}
    		}//end of loop x
    	}//end of loop z
    	//System.out.println("fourInYaxis=fail");
    	return false;
    }

    public boolean fourInZaxis(Colour color) 
    {
    	for (int x=1; x<=DIM; x=x+1)
    	{	    		
    		for (int y=1; y<=DIM; y=y+1)
    		{
	    		int colorCount=0;
	    		//smallest loop: for z: (1,1,1) (1,1,2) (1,1,3) (1,1,4)
	    		//bigger loop:   for y: (1,2,1) (1,2,2) (1,2,3) (1,2,4)
	    		for (int z=1; z<=DIM; z=z+1)
	    		{
	        		if (getField(x,y,z)==color)
	        		{
	        			colorCount=colorCount+1;
	        		}
	        		else//this Row which is not fully occupied by m
	        		{
	        			break;//go out of the for loop of y
	        		}
	    		}
	    		if (colorCount==DIM)
	    		{
	    			winType=13;
	    			System.out.println("fourInZaxis=succeed");
	    			return true;
	    		}
    		}//end of loop y
    	}//end of loop x
    	//System.out.println("fourInZaxis=fail");
    	return false;
    }
    
    public boolean diagonal1_InXYPlane(Colour color)//check four diagnals
    {
    	for (int z=1;z<=DIM;z=z+1)
    	{
	    	//index(1,4,1),index(2,3,1),index(3,2,1), index(4,1,1)
    		int colorCount=0;
    		for (int p=1;p<DIM+1;p=p+1)
	    	{
	    		if (getField(index(p,DIM+1-p,z))==color)
	    		{
	    			
	    			colorCount=colorCount+1;
	    			//as long as there is a field that is not m, exist the loop.
	    			//check the other diagonal
	    		}else//this Row which is not fully occupied by m
        		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=1;
    			System.out.println("diagonal1_InXYPlane=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal1_InXYPlane=fail");
    	return false;
    }
    
    public boolean diagonal2_InXYPlane(Colour color)
    {
    	for (int z=1;z<=DIM;z=z+1)
    	{
	    	//index(1,1,1),index(2,2,1),index(3,3,1), index(4,4,1)
    		int colorCount=0;
    		for (int p=1;p<DIM+1;p=p+1)
	    	{
	    		if (getField(index(p,p,z))==color)
	    		{
	    			colorCount=colorCount+1;
	    		}		        		else//this Row which is not fully occupied by m
        		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=2;
    			System.out.println("diagonal2_InXYPlane=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal2_InXYPlane=fail");
    	return false;
    	
    }
    
    public boolean diagonal3_InZYPlane(Colour color)
    {
    	for (int x=1;x<=DIM;x=x+1)
    	{
	    	//index(1,1,4),index(1,2,3),index(1,3,2), index(1,4,1)
    		//index(2,1,4),index(2,2,3),index(2,3,2), index(2,4,1)
    		//index(3,1,4),index(3,2,3),index(3,3,2), index(3,4,1)
    		//index(4,1,4),index(4,2,3),index(4,3,2), index(4,4,1)
    		
    		int colorCount=0;
    		for (int p=1;p<DIM+1;p=p+1)
	    	{
	    		if (getField(index(x,p,DIM+1-p))==color){
	    			colorCount=colorCount+1;
	    		}else//this Row which is not fully occupied by m
	    		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=3;
    			System.out.println("diagonal3_InZYPlane=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal3_InZYPlane=fail");
    	return false;
    }
    
    public boolean diagonal4_InZYPlane(Colour color)
    {
    	//index(1,1,1),index(1,2,2),index(1,3,3), index(1,4,4)
		//index(2,1,1),index(2,2,2),index(2,3,3), index(2,4,4)
    	//index(3,1,1),index(3,2,2),index(3,3,3), index(3,4,4)
    	//index(4,1,1),index(4,2,2),index(4,3,3), index(4,4,4)
    	for (int x=1;x<=DIM;x=x+1)
    	{	    		
    		int colorCount=0;
    		for (int p=1;p<=DIM;p=p+1)
	    	{
	    		if (getField(index(x,p,p))==color){
	    			colorCount=colorCount+1;
	    		}else//this Row which is not fully occupied by m
	    		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=4;
    			System.out.println("diagonal4=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal4=fail");
    	return false;	    	
    }
    
    public boolean diagonal5_InXZPlane(Colour color)
    {
    	//index(1,1,4),index(2,1,3),index(3,1,2), index(4,1,1)
    	//index(1,2,4),index(2,2,3),index(3,2,2), index(4,2,1)
    	//index(1,3,4),index(2,3,3),index(3,3,2), index(4,3,1)
    	//index(1,4,4),index(2,4,3),index(3,4,2), index(4,4,1)
    	
    	for (int y=1;y<=DIM;y=y+1)
    	{	    		
    		int colorCount=0;
    		for (int p=1;p<=DIM;p=p+1)
	    	{
	    		if (getField(index(p,y,DIM+1-p))==color){
	    			colorCount=colorCount+1;
	    		}else//this Row which is not fully occupied by m
	    		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=5;
    			System.out.println("diagonal5=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal4=fail");
    	return false;
    }
    
    public boolean diagonal6_InXZPlane(Colour color)
    {
    	//index(1,1,1),index(2,1,2),index(3,1,3), index(4,1,4)
    	//index(1,2,1),index(2,2,2),index(3,2,3), index(4,2,4)
    	//index(1,3,1),index(2,3,2),index(3,3,3), index(4,3,4)
    	//index(1,4,1),index(2,4,2),index(3,4,3), index(4,4,4)
    	
    	for (int y=1;y<=DIM;y=y+1)
    	{	    		
    		int colorCount=0;
    		for (int p=1;p<=DIM;p=p+1)
	    	{
	    		if (getField(index(p,y,p))==color){
	    			colorCount=colorCount+1;
	    		}else//this Row which is not fully occupied by m
	    		{
        			break;//go out of the for loop of y
        		}
	    	}
    		if (colorCount==DIM)
    		{
    			winType=6;
    			System.out.println("diagonal6=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal6=fail");
    	return false;
    }
    
    public boolean diagonal7_InXYZSpace(Colour color)
    {
    	//diagnal 7 index(1,1,4),index(2,2,3),index(3,3,2), index(4,4,1)	    		
		int colorCount=0;
		for (int p=1;p<=DIM;p=p+1)
    	{
    		if (getField(index(p,p,DIM+1-p))==color){
    			colorCount=colorCount+1;
    		}else//this Row which is not fully occupied by m
    		{
    			break;//go out of the for loop of y
    		}
    	}
		if (colorCount==DIM)
		{
			winType=7;
			System.out.println("diagonal7=succeed");
			return true;
		}
    	//System.out.println("diagonal7=fail");
    	return false;
    }
    
    public boolean diagonal8_InXYZSpace(Colour color)
    {
    	//diagnal 8 index(4,4,4),index(3,3,3),index(2,2,2), index(1,1,1)
		int colorCount=0;
		for (int p=1;p<=DIM;p=p+1)
    	{
    		if (getField(index(p,p,p))==color){
    			colorCount=colorCount+1;
    		}else//this Row which is not fully occupied by m
    		{
    			break;//go out of the for loop of y
    		}
    	}
		if (colorCount==DIM)
		{
			winType=8;
			System.out.println("diagonal8=succeed");
			return true;
		}
		//System.out.println("diagonal8=fail");
		return false;
    	
    }
    public boolean diagonal9_InXYZSpace(Colour color)
    {
    	//diagnal 9 index(1,4,4),index(2,3,3),index(3,2,2), index(4,1,1)
		int colorCount=0;
		for (int p=1;p<=DIM;p=p+1)
    	{
    		if (getField(index(DIM+1-p,p,p))==color){
    			colorCount=colorCount+1;
    		}else//this Row which is not fully occupied by m
    		{
    			break;//go out of the for loop of y
    		}
    	}
		if (colorCount==DIM)
		{
			winType=9;
			System.out.println("diagonal9=succeed");
			return true;
		}
	//System.out.println("diagonal7=fail");
	return false;
    }
    public boolean diagonal10_InXYZSpace(Colour color)
    {
    	//diagnal10 index(4,1,4),index(3,2,3),index(2,3,2), index(1,4,1)
		int colorCount=0;
		for (int p=1;p<=DIM;p=p+1)
    	{
    		if (getField(index(DIM+1-p,p,DIM+1-p))==color){
    			colorCount=colorCount+1;
    		}else//this Row which is not fully occupied by m
    		{
    			break;//go out of the for loop of y
    		}
    	}
		if (colorCount==DIM)
		{
			winType=10;
			System.out.println("diagonal10=succeed");
			return true;
		}
		//System.out.println("diagonal10=fail");
	return false;
    }
    
    public String getCurrentBoard() 
	{
        String s = "";
        for (int k=0;k<DIM; k=k+1)
        {
        	//System.out.println("print board to string,k="+k);
        	s=s+"horizontal level: "+(k+1)+"\n";
	        for (int i = 0; i < DIM; i++) //i=0,1,2,3
	        {
	        	//System.out.println("i before="+i);
	            String row = "";
	            for (int j = 0; j < DIM; j++) //j=0,1,2,3
	            {
	                row = row + " " + getField(j+1,i+1,k+1).toString() + " "; // getField(i+1, j+1).toString()
	                if (j < DIM) // j=0,1,2
	                {
	                    row = row + "|";
	                }
	            }
	            s = s + row + DELIM + NUMBERING[k*DIM+i]; // i * 2=0,2,4,6
	            if (i < DIM ) //i=0,1,2,3
	            {
	                s = s + "\n" + LINE + "\n"; //s = s + "\n" + "-------+-------+-------+-------"+ “\n�?
	            }
	            //System.out.println("i after="+i);
	        }
	        //System.out.println("print board to string,k="+k);
	        s=s+"\n";
        }
        return s;
	}
    
	public int[] emptyFieldsArray ()
	{
		System.out.println("start collecting empty fields to array");
		/*System.out.println(b.getDimension());*/
		int dim=this.getDimension();
		/*System.out.println("dimension="+b.getDimension());*/
		int arrayLength=dim*dim;
		int[] emptyFieldArray=new int[arrayLength];
		int arrayIndex=0;
		for (arrayIndex=0;arrayIndex<emptyFieldArray.length;arrayIndex=arrayIndex+1 )
		{
			emptyFieldArray[arrayIndex]=-1;
			//System.out.println("emptyFieldArray["+arrayIndex+"]: "+emptyFieldArray[arrayIndex]);
		}
		
		arrayIndex=0;
		for (int y=1;y<=dim;y=y+1)//b.getDimension()
		{
			//System.out.println("y= "+ y);
			
			for (int x=1;x<=dim;x=x+1)//b.getDimension()
			{
				//System.out.println("x= "+ x);
		    	if (/*this.*/ isEmptyField(x,y,1)  )
		    	{
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 1);
		    		arrayIndex=arrayIndex+1;
		    	}else if (/*this.*/isEmptyField(x,y,2)){
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 2);
		    		arrayIndex=arrayIndex+1;
		    	}else if (/*this.*/isEmptyField(x,y,3)){
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 3);
		    		arrayIndex=arrayIndex+1;
		    	}else if (/*this.*/isEmptyField(x,y,4)){
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 4);
		    		arrayIndex=arrayIndex+1;
		    	}		    	
		    }
		}
		for (arrayIndex=0;arrayIndex<arrayLength;arrayIndex++)
		{
			System.out.print("emptyfield"+arrayIndex+":"+emptyFieldArray[arrayIndex]+" ");
		}
		System.out.println("\n");
		return emptyFieldArray;
	}
	public int actualLengthOfEmptyArray (int [] array) 
	{
		int filledLength=array.length;
		for (int j=0; j<array.length;j=j+1)
		{
			if (array[j]==-1)
			{
				filledLength=j-1;
				break;
			}
		}
		return filledLength;
	}
	
	public int directWin (int[]array,Colour color)//means there is an empty field that causes a direct win
	{
		int suggestedFieldToMove;
		System.out.println("check direct Win");
		int filledLength=actualLengthOfEmptyArray (array);
		for (int arrayIndex=0; arrayIndex<filledLength; arrayIndex=arrayIndex+1)
		{
			this.setFieldDirectly(array[arrayIndex], color);			
			if (this.isWinner(color))
			{
				System.out.println("computer ("+color+") can win directly");
				suggestedFieldToMove=array[arrayIndex];
				this.setFieldDirectly(array[arrayIndex], Colour.EMPTY);
				return suggestedFieldToMove;			
			}else{
				this.setFieldDirectly(array[arrayIndex], Colour.EMPTY);
			}
		}
		return -1;//-1 means none of the empty fields can result in a direct win.  
	}
	
	public int opponentWin (int[]array, Colour color) 
	{
		int suggestedFieldToMove;
		System.out.println("check opponent Win");
		int filledLength=actualLengthOfEmptyArray (array);
		for (int arrayIndex=0; arrayIndex<filledLength; arrayIndex=arrayIndex+1)
		{
			//System.out.println("arrayIndex="+arrayIndex);
			this.setFieldDirectly(array[arrayIndex], color.other());
			//System.out.println("array["+arrayIndex+"]="+array[arrayIndex]+" "+color);
			//System.out.println("assume putting"+color+"to field"+array[arrayIndex]+": "+b.toString());
			if (this.isWinner(color.other()) )
			{
				System.out.println("opponent ("+color.other()+") can win in field "+array[arrayIndex]);					
				suggestedFieldToMove=array[arrayIndex];
				this.setFieldDirectly(array[arrayIndex], Colour.EMPTY);
				return suggestedFieldToMove;
				//means there is an empty field that causes the opponent to win
			}else{//if (! b.isWinner(color.other()) ), do the following: 
				this.setFieldDirectly(array[arrayIndex], Colour.EMPTY);
			}
		}
		
		return -1;//-1 means none of the empty fields can cause the opponent to win
	}
	
	public int bestFieldToMove (model.Colour color)
	{
		int oppWin,directlyWin;
		int [] emptyFields=emptyFieldsArray ();
		int filledLength=actualLengthOfEmptyArray (emptyFields);
		//If the middle field in the bottom plane is empty, this field is selected;
		int middleField=index(2,2,1);//there is no middle field. I assume the filed (2,2,1) is the default middle filed 
		
		if (isEmptyField(middleField))
		{
			System.out.println("have decided: middle field");
			return middleField;
		}
		//If there is a field that guarantees a direct win, this field is selected.
		else if((directlyWin=directWin (emptyFields, color))!=-1 )
		{
			System.out.println("have decided: directWin in field"+directlyWin);
			return directlyWin;
		}
		//If there is a field with which the opponent could win, this field is selected.
		else if((oppWin =opponentWin(emptyFields, color))!=-1)
		{
			System.out.println("have decided: opponentWin in field"+oppWin);
			return oppWin;
		}
		//If none of the cases above applies, a random field is selected.
		else
		{
			int randomNum = ThreadLocalRandom.current().nextInt(0, filledLength);
			System.out.println("have decided: random"+randomNum);
			return emptyFields[randomNum];
		}
	}
	
	public void AIMove(Colour color,int goToNextPlayer) 
	{
		int changedToNextPlayer=goToNextPlayer;
		fieldJustMovedIn=bestFieldToMove(color);
		int oldTurn = playerTurn;
		setField (fieldJustMovedIn, color, changedToNextPlayer);	
		System.out.println("AI has moved, new player turn="+playerTurn);
		status = 2;
		win(oldTurn);
		setChanged();
		notifyObservers();
	}
	    

}
