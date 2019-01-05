package model;
/**
 * This is a interface of the game
 */


public interface ModelInterface {
	
	public boolean isEmptyField (int i);//public boolean checkIfPiecedCanBeDroppedIn(int column);
    public void setField(int i, Color color);//dropPieces
    boolean doesGameOver();
    public boolean isItaDraw();
    public String toString();
    
}