package model;
import java.util.concurrent.ThreadLocalRandom;
import controllerOfClient.PlayerInterface;
/**
 * 
 * @author QIAO
 *
 */
public class Model implements ModelInterface  {

	//-----------------------------------
	public static final int DIM = 4;
	public PlayerInterface[] pToPArrayInterface = new PlayerInterface[2];
	public PlayerInterface[] pToEArrayInterface = new PlayerInterface[2];
	public PlayerInterface[] eToEArrayInterface = new PlayerInterface[2];
	
	private Color[] fields;
	private static final String[] NUMBERING = 
	{" 0 |  1 |  2 |  3 ", "  4 |  5 |  6 |  7 ", "  8 |  9 | 10 | 11 ", "12 | 13 | 14 | 15", 
	  " 16 | 17 | 18 | 19 ", " 20 | 21 | 22 | 23 ", " 24 | 25 | 26 | 27 ", "28 | 29 | 30 | 31",
	  " 32 | 33 | 34 | 35 ", " 36 | 37 | 38 | 39 ", " 40 | 41 | 42 | 43 ", "44 | 45 | 46 | 47",
	  " 48 | 49 | 50 | 51 ", " 52 | 53 | 54 | 55 ", " 56 | 57 | 58 | 59 ", "60 | 61 | 62 | 63"};
	private static final String LINE = "-------+-------+-------+-------"; 
	private static final String DELIM = "     ";
	private int status; // indicate the game status
	private int fieldJustMovedIn;
	private int winType;
    //@ invariant (DIM>0)
    //@ invariant (pToPArrayInterface.length==2)
    //@ invariant (pToEArrayInterface.length==2)
    //@ invariant (eToEArrayInterface.length==2)
	//@ invariant (fields.length==DIM*DIM*DIM)
	//@ invariant (status>0)
	//@ invariant (winType>0)
	//@ invariant (fieldJustMovedIn>=0 && fieldJustMovedIn<DIM*DIM*DIM)
	
	/**constructor of the class Model.
	 * @param	None
	 * @return None
	 */
	public Model() {
		initializeBoard();
	}

	/**initialize the board. remove all drops on the board. 
	 * @param	None
	 * @return none
	 */
	/*@ requires DIM >0
	 *  ensures (\forall int i; 0<=i && i<DIM*DIM*DIM; isEmptyField(fields[i])==true );
	 */
	public void initializeBoard() {
    	fields = new Color[DIM * DIM * DIM];
    	for (int i = 0; 0 <= i & i < DIM * DIM * DIM; i = i + 1) {
    		fields[i] = Color.EMPTY;
    	}
	}

	
	/**get color of a field based on the input x,y,z coordinaters.
	 * @param x coordinator x of the filed
	 * @param y coordinator y of the filed
	 * @param z coordinator z of the filed
	 * @return the color of this filed
	 */
	/*@ requires 1<=x && x<=4;
	 *  requires 1<=y && y<=4;
	 *  requires 1<=z && z<=4;
	 *  ensures \result==Color.EMPTY || \result==Color.Blue || \result==Color.Red;
	 */
    /*@ pure */public Color getField(int x, int y, int z) {
    	return getField(index(x, y, z));     
    }
    
	/**get the color of a field.
	 * @param	index of a field
	 * @return	color of this field
	 */
	/*@ requires i>=0 && i<=DIM*DIM*DIM
	 *  ensures \result==Color.EMPTY || \result==Color.Red || \result==Color.Blue
	 */
    /*@ pure */public Color getField(int i) {
    	return fields[i];
    }
    
	/**get field that has just been moved in by the player.
	 * @param None
	 * @return the field that has been moved in just now
	 */
	/*@ ensures (\result>=0 &&\result<DIM*DIM*DIM);
	 * */
    /*@ pure */public int getFieldJustMovedIn() {
    	return fieldJustMovedIn;
    }
    
    /**
     * get the dimension of the game board
     * @return the dimension
     */
    /*@ensures \result==DIM;
     * 
     */
    /*@ pure */public int getDimension()  {
    	return DIM;
    }
    /**
     * get the win type of the game, if the game wins
     * @return the win type
     */
    /*@ensures \result>0;
     * 
     */
    /*@ pure */public int getWinType()  {
    	return winType;
    }
	/**check whether or not a field is empty.
	 * @param	index of the field
	 * @return	true if the field is empty, false if the field is not empty 
	 */
	/*@ requires i>=0 && i<DIM*DIM*DIM.
	 *  ensures \result==(this.isField(i) && (this.getField(i) == Color.EMPTY))
	 */
	public boolean isEmptyField(int i) {
		return this.isField(i) && (this.getField(i) == Color.EMPTY);
	}
	
	/**check whether or not a field is empty.
	 * @param	x,y,z coordinators of the field
	 * @return	true if the field is empty, false if the field is not empty 
	 */
	/*@ requires 1<=x && x<=4
	 * requires 1<=y && y<=4
	 * requires 1<=z && z<=4
	 * ensures \result==(this.isField(x,y,z) && (this.getField(x,y,z) == Color.EMPTY))
	 */
    public boolean isEmptyField(int x, int y, int z) {
    	return isEmptyField(index(x, y, z));
    }

	/**get the index of a field.
	 @param x coordinator x of the filed
	 @param y coordinator y of the filed
	 @param z coordinator z of the filed
	 * @return	index of this field
	 */
    public int index(int x, int y, int z) {
    	return (z - 1) * DIM * DIM + (y - 1) * DIM + (x - 1);
    }
    
    /**
     * check whether or not this field exists.
     * @param x
     * @param y
     * @param z
     * @return true if this field exist in the board. false otherwise
     */
    public boolean isField(int x, int y, int z) {
    	return (1 <= x && x <= DIM) && (1 <= y && y <= DIM) && (1 <= z && z <= DIM);
    }
    
    /**
     * check whether or not this field exists.
     * @param index index of a field
     * @return true if this field exist in the board. false otherwise
     */
    public boolean isField(int index) {
    	return 0 <= index && index < DIM*DIM*DIM;
    }

	/**drop a piece on a field, without checking the gravity.
	 * @param	index of the filed
	 * @param   a color
	 * @return	none
	 */
    /*@requires 0<i && i< DIM*DIM*DIM
     * ensures getField(i)==Color color;
     */
    public void setFieldDirectly(int i, Color color) {
    	fields[i] = color;
    }
    
	/**check the gravity and drop a piece on a field.
	 * @param	x coordinator of the field
	 * @param   y coordinator of the field
	 * @param   z coordinator of the field
	 * @return	none
	 */
    /*@ requires 
     *  ensures getField(i)==Color color;
     */
    public void setField(int x, int y, int z, Color color) {
    	setField(index(x, y, z), color);
    }
    
	/**check the gravity and drop a piece on a field.
	 * @param	index of the filed
	 * @param   a color
	 * @return	none
	 */
    /*@ requires i>0 && i<DIM*DIM*DIM
     *  ensures getField(i)==Color color;
     */
    public void setField(int i, Color color)
    {
    	System.out.println("setField "+i+" to color: "+color);
    	int remainder=i%(DIM*DIM);//use remainder to find which horizontal column this filed i is located.
    	if (this.isField(i))
    	{
    		if (this.getField(remainder)==Color.EMPTY){
    			fields[remainder] = color;
    			//System.out.println("in Model, player has moved. new player turn=");
    			status=2;
    			fieldJustMovedIn=remainder;
    		}else if (this.getField(remainder+DIM*DIM*1)==Color.EMPTY){
    			fields[remainder+DIM*DIM*1] = color;
    			//System.out.println("in Model, player has moved. new player turn=");
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*1;
    		}else if (this.getField(remainder+DIM*DIM*2)==Color.EMPTY){
    			fields[remainder+DIM*DIM*2] = color;
    			//System.out.println("in Model, player has moved. new player turn=");
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*2;
    		}else if (this.getField(remainder+DIM*DIM*3)==Color.EMPTY){
    			fields[remainder+DIM*DIM*3] = color;
    			//System.out.println("in Model, player has moved. new player turn=");
    			status=2;
    			fieldJustMovedIn=remainder+DIM*DIM*3;
    		}    		
    	} 			    	
    	
    }
    /**
     * check whether or not the game is over
     *@return true if somebody wins or there is a draw. false otherwise.
     */
    /*
     * @see model.ModelInterface#doesGameOver()
     */
    public boolean doesGameOver() 
    {
    	if(this.isWinner(Color.Red)) {
    		status=5;
    	}else if (this.isWinner(Color.Blue)) {
    		status=6;
    	}else if (this.isFull()) //draw
    	{
    		status=7;
    	}
    	if ((status==5) || (status==6) || (status==7))
    	{
    		return true;
    	}
    	return false;
    }

    /**
     * get the status of this game
     * @return status of this game
     */
    /*@ensures \result==status && (\result==2 || \result==5|| \result==6 ||\result==7)
     * 
     */
    /*@ pure */public int getStatus()
    {
    	return status;
    }
    /**
     * check whether or not there is a winner
     * @return true if there is a winner. false if nobody wins
     */
    /*@
     * ensures \result==(getStatus()==5||getStatus()==6)
     */
    public boolean hasWinner() 
    {
    	return (isWinner(Color.Red)||isWinner(Color.Blue));
    }

    /**
     * check whether a player has won the game
     * @param color of the player
     * @return true if this player has won. false otherwise
     */
    /*@
     * requires color ==Color.Red ||color==Color.Blue
     * ensures \result==(getWinType()>=11 && getWinType()<=13)
     */
    public boolean isWinner(Color color) 
    {
    	return ( this.fourInXaxis(color) || this.fourInYaxis(color) 
    			|| this.fourInZaxis(color) ||this.hasDiagonal(color)); 
    }
    /**
     * check whether or not all the fields are fully occupied.
     * @return true if the board is fully occupied. false if the board has empty fields
     */
    /*@ensures \result==!(\exist i; int i>= 0 && i < DIM * DIM * DIM;getField(i)==Color.EMPTY)
     */
    public boolean isFull() {
    	int i = 0;
    	while (i >= 0 && i < DIM * DIM * DIM) 	{
    		if (this.getField(i) != Color.EMPTY) {
    			i = i + 1;
    		} else {
    			return false;
    		}
    	}
        return true;
    }
    /**
     * check whether or not there is a draw in the game
     */
    /*@
     * ensures \result==(getStatus()==7)
     */
    public boolean isItaDraw()
    {		
    	boolean bool =this.isFull();
    	if (bool==false)
    	{
    		return false;
    	}
		System.out.println("It's a Draw!! ");
    	return true;
    }
    
    /**
     * check whether or not there are 4 pieces in a diagonal
     * @param color of a player
     * @return true if this player has four pieces in a diagonal
     */
    /*@ requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()>=1 && getWinType()<=10)
     */
    public boolean hasDiagonal(Color color)   {
    	return diagonal1_InXYPlane(color) 
    			|| diagonal2_InXYPlane(color) 
    			|| diagonal3_InZYPlane(color)
    			|| diagonal4_InZYPlane(color) 
    			|| diagonal5_InXZPlane(color) 
    			|| diagonal6_InXZPlane(color)
    			|| diagonal7_InXYZSpace(color) 
    			|| diagonal8_InXYZSpace(color) 
    			|| diagonal9_InXYZSpace(color)
    			|| diagonal10_InXYZSpace(color);
    }
    /**
     * check whether or not there are 4 pieces in a line which is parallel to x axis.
     * @param color of a player
     * @return true if this player has four pieces in the direction of X axis. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==11)
     */
   public boolean fourInXaxis(Color color) 
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
    		} //end of loop y
    	} //end of loop z
    	//System.out.println("fourInXaxis=fail");
    	return false;
    }


   /**
    * check whether or not there are 4 pieces in a line which is parallel to y axis.
    * @param color of a player
    * @return true if this player has four pieces in the direction of y axis. false otherwise
    */
   /*@requires color==Color.Red||color==Color.Blue
    * ensures \result==(getWinType()==12)
    */
    public boolean fourInYaxis(Color color)   {
    	for (int z = 1; z <= DIM; z = z + 1) 	{	    		
    		for (int x=1; x<=DIM; x=x+1) {
	    		int colorCount=0;
	    		//(1,1,1) (1,2,1) (1,3,1) (1,4,1)
	    		for (int y=1; y<=DIM; y=y+1) {
	        		if (getField(x,y,z)==color) {
	        			colorCount=colorCount+1;
	        		} else { //this Row which is not fully occupied by m
	        			break; //go out of the for loop of y
	        		}
	    		}
	    		if (colorCount == DIM) {
	    			winType = 12;
	    			System.out.println("fourInYaxis=succeed");
	    			return true;
	    		}
    		} //end of loop x
    	} //end of loop z
    	//System.out.println("fourInYaxis=fail");
    	return false;
    }
    /**
     * check whether or not there are 4 pieces in a line which is parallel to z axis.
     * @param color of a player
     * @return true if this player has four pieces in the direction of z axis. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==13)
     */
    public boolean fourInZaxis(Color color)    {
    	for (int x = 1; x <= DIM; x = x + 1) {	    		
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
    /**
     * check, on the xy plane, 
     * whether or not there are 4 pieces in the first-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==1)
     */
    public boolean diagonal1_InXYPlane(Color color)
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
    /**
     * check, on the xy plane, 
     * whether or not there are 4 pieces in the second-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==2)
     */
    public boolean diagonal2_InXYPlane(Color color)
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
    /**
     * check, on the zy plane, 
     * whether or not there are 4 pieces in the third-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==3)
     */
    public boolean diagonal3_InZYPlane(Color color)
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
	    		if (getField(index(x,p,DIM+1- p))==color){
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
    /**
     * check, on the zy plane, 
     * whether or not there are 4 pieces in the fourth-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==4)
     */
    public boolean diagonal4_InZYPlane(Color color)  {
    	for (int x = 1; x <= DIM; x = x + 1) {	    		
    		int colorCount = 0;
    		for (int p = 1; p <= DIM; p = p + 1) {
	    		if (getField(index(x, p, p)) == color) {
	    			colorCount = colorCount + 1;
	    		} else { //this Row which is not fully occupied by m
	    		
        			break; //go out of the for loop of y
        		}
	    	}
    		if (colorCount == DIM) {
    			winType = 4;
    			System.out.println("diagonal4=succeed");
    			return true;
    		}
    	}
    	//System.out.println("diagonal4=fail");
    	return false;	    	
    }
    /**
     * check, on the xz plane, 
     * whether or not there are 4 pieces in the fifth-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==5)
     */
    public boolean diagonal5_InXZPlane(Color color)
    {
    	
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
    /**
     * check, on the xz plane, 
     * whether or not there are 4 pieces in the sixth-direction diagonal  
     * need to check four diagonals. 
     * because there is one diagonal on each plane. 
     * they have the same direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==6)
     */
    public boolean diagonal6_InXZPlane(Color color)
    {
    	
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
    /**
     * check, on the xyz space, 
     * whether or not there are 4 pieces in the seventh-direction diagonal  
     * there is only one diagonal in this direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==7)
     */
    public boolean diagonal7_InXYZSpace(Color color)
    {
    	//diagnal 7 index(1,1,4),index(2,2,3),index(3,3,2), index(4,4,1)	    		
		int colorCount=0;
		for (int p=1;p<=DIM;p=p+1)
    	{
    		if (getField(index(p,p,DIM+1 - p))==color){
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
    /**
     * check, on the xyz space, 
     * whether or not there are 4 pieces in the 8th-direction diagonal  
     * there is only one diagonal in this direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==8)
     */
    public boolean diagonal8_InXYZSpace(Color color)
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
    /**
     * check, on the xyz space, 
     * whether or not there are 4 pieces in the 9th-direction diagonal  
     * there is only one diagonal in this direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==9)
     */
    public boolean diagonal9_InXYZSpace(Color color)
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
	return false;
    }
    /**
     * check, on the xyz space, 
     * whether or not there are 4 pieces in the 10th-direction diagonal  
     * there is only one diagonal in this direction
     * @param color of a player
     * @return true if there is. false otherwise
     */
    /*@requires color==Color.Red||color==Color.Blue
     * ensures \result==(getWinType()==10)
     */
    public boolean diagonal10_InXYZSpace(Color color)
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
    

	//------------------------artificial intelligence -----------------

    /**
     * start the AI thread. 
     * the thinking time is actually a sleeping time.
     * AI will firstly sleep for a certain amount of time.
     * After timeout, it will make a move
     * @param thinkingTime of AI
     * @param color of AI
     */
    /*@
     * requires color=Color.Red || color=Color.Blue
     * requires thinkingTime>0
     */
	public void startAIThread(int thinkingTime, Color color) {
		new Thread(
		    		new Runnable()   {
				        public void run() {
				            try {
				                Thread.sleep(thinkingTime);
				            } catch (InterruptedException exp) {
				            	System.out.println("there is an InterruptedException");
				            }
			    			AIMove(color);
			    		}
				    }
		    ).start();
	}
	/**
	 * AI moves to the "best field". 
	 * which field is the best is defined in the other method
	 * @param color of AI
	 */
	/*@
	 * requires color=Color.Red || color=Color.Blue;
	 */
	public void AIMove(Color color)  {
		fieldJustMovedIn=bestFieldToMove(color);
		System.out.println("bestFieldToMove="+fieldJustMovedIn);
		setFieldDirectly (fieldJustMovedIn, color);	
		System.out.println("AI has droped a piece to "+fieldJustMovedIn+" "+this.getField(fieldJustMovedIn));
		status = 2;
	}
	
	/**
	 * this method gives a decision on which field is the best to drop a piece
	 * if the middle field is empty, the best field to move is the middle field (2,2,1)
	 * If there is a field that guarantees a direct win, this field is selected.
	 * If there is a field with which the opponent could win, this field is selected.
	 * If none of the cases above applies, a random field is selected.
	 * @param color
	 * @return the best field to move
	 */
	/*@
	 * requires color==Color.Red || color==Color.Blue
	 * ensures \result>=0 && \result<DIM*DIM*DIM
	 */
	public int bestFieldToMove(Color color) {
		int oppWin, directlyWin;
		int[] emptyFields = emptyFieldsArray();
		int filledLength = actualLengthOfEmptyArray(emptyFields);
		//If the middle field in the bottom plane is empty, this field is selected;
		int middleField = index(2, 2, 1);
		//there is no middle field. I assume the filed (2,2,1) is the default middle filed 
		
		if (isEmptyField(middleField)) {
			System.out.println("have decided: middle field");
			return middleField;
		} else if ((directlyWin = directWin(emptyFields, color)) != -1 ) {
			//If there is a field that guarantees a direct win, this field is selected.
			System.out.println("have decided: directWin in field" + directlyWin);
			return directlyWin;
		} else if ((oppWin = opponentWin(emptyFields, color)) !=-1) { 
			//If there is a field with which the opponent could win, this field is selected.
			System.out.println("have decided: opponentWin in field" + oppWin);
			return oppWin;
		} else { 
			//If none of the cases above applies, a random field is selected.
			int randomNum = ThreadLocalRandom.current().nextInt(0, filledLength);
			System.out.println("have decided: random" + randomNum);
			return emptyFields[randomNum];
		}
	}
	/**
	 * this method collects all the empty fields that are possible to drop a piece on.
	 * the gravity is taken into account. 
	 * if lower field is empty, the upper field would not be seen as empty fields
	 * the array is initialized by all elements =-1
	 * this method helps AI and hint function to evaluate 
	 * which field is the best to move in
	 * @return an array
	 */
	/*@
	 * ensures \result.length==DIM*DIM;
	 */
	public int[] emptyFieldsArray() {
		System.out.println("start collecting empty fields to array");
		int dim = this.getDimension();
		int arrayLength = dim * dim;
		int[] emptyFieldArray = new int[arrayLength];
		int arrayIndex = 0;
		for (arrayIndex = 0; 
				arrayIndex < emptyFieldArray.length; 
				arrayIndex = arrayIndex + 1) {
			emptyFieldArray[arrayIndex] = -1;
			//System.out.println("emptyFieldArray["+arrayIndex+"]: "+emptyFieldArray[arrayIndex]);
		}
		
		arrayIndex = 0;
		
		/*the system out messages below are for displaying 
		 * the process of how to check empty fields. 
		 * if the server wants to see how the empty fields are found, 
		 * we can decomment the System.out.
		*/
		for (int y=1;y<=dim;y=y+1)
		{
			//System.out.println("y= "+ y);
			for (int x=1;x<=dim;x=x+1)
			{
				//System.out.println("x= "+ x);
		    	if (isEmptyField(x,y,1)  )
		    	{
		    		//System.out.println("find empty field="+this.index(x, y, 1));
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 1);
		    		arrayIndex=arrayIndex+1;
		    	}else if (isEmptyField(x,y,2))
		    	{
		    		//System.out.println("find empty field="+this.index(x, y, 2));
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 2);
		    		arrayIndex=arrayIndex+1;
		    	}else if (isEmptyField(x,y,3))
		    	{
		    		//System.out.println("find empty field="+this.index(x, y, 3));
		    		emptyFieldArray[arrayIndex]=this.index(x, y, 3);
		    		arrayIndex=arrayIndex+1;
		    	}else if (isEmptyField(x,y,4))
		    	{
		    		//System.out.println("find empty field="+this.index(x, y, 4));
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
	/**
	 * the length of the EmptyArray is fixed.
	 * but it might happen that some positions in the EmptyArray are empty (=-1)
	 * the actual length should exclude these empty positions
	 * @param array
	 * @return
	 */
	/*@
	 * requires array.length==DIM*DIM;
	 * ensures \result>0 && \result<=DIM*DIM
	 */
	public int actualLengthOfEmptyArray(int[] array)  {
		int filledLength = array.length;
		for (int j = 0; j < array.length; j = j + 1) {
			if (array[j] == -1) {
				filledLength = j - 1;
				break;
			}
		}
		return filledLength;
	}
	
	/**
	 * check whether or not if this player drop a piece, the player can win directly
	 * this is for attacking the opponent
	 * @param array
	 * @param color
	 * @return either the suggested field to move or -1 
	 * (-1 means none of the empty fields can result in a direct win. )
	 */
	/*@requires array.length==DIM*DIM 
	 * requires color==Color.Red || color==Color.Blue
	 * ensures \result>=-1 && \result<DIM*DIM*DIM;
	 */
	public int directWin(int[]array, Color color) {
		int suggestedFieldToMove;
		System.out.println("check direct Win");
		int filledLength = actualLengthOfEmptyArray(array);
		for (int arrayIndex = 0; arrayIndex < filledLength; arrayIndex = arrayIndex+1) {
			this.setFieldDirectly(array[arrayIndex], color);			
			if (this.isWinner(color)) {
				System.out.println("computer (" + color + ") can win directly");
				suggestedFieldToMove = array[arrayIndex];
				this.setFieldDirectly(array[arrayIndex], Color.EMPTY);
				return suggestedFieldToMove;			
			} else {
				this.setFieldDirectly(array[arrayIndex], Color.EMPTY);
			}
		}
		return -1; //-1 means none of the empty fields can result in a direct win.  
	}
	/**
	 * check whether or not if the opponent drop a piece, the opponent can win directly
	 * this is for defensing the opponent
	 * @param array
	 * @param color
	 * @return either the suggested field to move or -1 
	 * (-1 means none of the empty fields can result in the opponent win directly. )
	 */	
	/*@requires array.length==DIM*DIM 
	 * requires color==Color.Red || color==Color.Blue
	 * ensures ensures \result>=-1 && \result<DIM*DIM*DIM;
	 */
	public int opponentWin(int[]array, Color color)  {
		int suggestedFieldToMove;
		System.out.println("check opponent Win");
		int filledLength = actualLengthOfEmptyArray(array);
		for (int arrayIndex = 0; arrayIndex < filledLength; arrayIndex = arrayIndex+1)
		{
			this.setFieldDirectly(array[arrayIndex], color.other());
			if (this.isWinner(color.other())) {
				System.out.println("opponent (" 
								+ color.other() 
								+ ") can win in field " + array[arrayIndex]);					
				suggestedFieldToMove = array[arrayIndex];
				this.setFieldDirectly(array[arrayIndex], Color.EMPTY);
				return suggestedFieldToMove;
			} else { //if (! b.isWinner(color.other()) ), do the following: 
				this.setFieldDirectly(array[arrayIndex], Color.EMPTY);
			}
		}
		
		return -1; //-1 means none of the empty fields can cause the opponent to win
	}
	
	/**display the current board.
	 * @param	None
	 * @return	a String which displays the current board 
	 */
	public String printBoard() {
        String s = ""; //board is fields, board is string "s"
        for (int k = 0; k < DIM; k = k + 1) {
        	s = s + "horizontal level: " + (k + 1) + "\n";
	        for (int i = 0; i < DIM; i++)   {
	            String row = "";
	            for (int j = 0; j < DIM; j++)   {
	                row = row + " " + getField(j + 1, i + 1, k + 1).toString() + " ";
	                if (j < DIM)  { // j=0,1,2 
	                    row = row + "|";
	                }
	            }
	            s = s + row + DELIM + NUMBERING[k * DIM + i]; // i * 2=0,2,4,6
	            if (i < DIM) {
	                s = s + "\n" + LINE + "\n"; 
	            }
	        }
	        s = s + "\n";
        }
        return s;
	}
}