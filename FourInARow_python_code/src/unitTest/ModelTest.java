package unitTest;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before; 
import org.junit.Test;
import controllerOfClient.PlayerInterface;
import model.Color;
import model.Model;

public class ModelTest {
	Model model;
    int dim;
	@Before  public void setUp() {        
		model = new Model();  
		dim = model.getDimension();
	}

	@Test
	public void initializeBoardTest() {
    	for (int i = 0; 0 <= i & i < dim * dim * dim; i = i + 1) {
    		assertEquals(model.getField(i), Color.EMPTY);
    	}
	}
	public void isEmptyFieldTest() {
		//model = new Model();  
		assertEquals(model.isEmptyField(0),true);
   		assertEquals(model.isEmptyField(63),true);
	}
	@Test
	public void setFieldDirectlyTest() {
		model.setFieldDirectly(15, Color.Blue);
		assertEquals(model.getField(15), Color.Blue);
		
	}
	@Test
	public void setFieldTest() {
		model.setField(62, Color.Blue);
		assertEquals(model.getField(14), Color.Blue);
		
	}
	public void isFieldTest() {
		model = new Model();
		
		assertFalse(model.isField(64));
		assertTrue(model.isField(0));
		assertTrue(model.isField(63));
		assertFalse(model.isField(-1));
	}
	@Test
	public void indexTest() {
		assertEquals(model.index(1, 1, 1),0);
		assertEquals(model.index(2, 3, 3),41);
	}
	@Test
	public void isWinnerTest()
	{
		model = new Model();  
		
		model.setField(0, Color.Blue);
		model.setField(5, Color.Blue);
		model.setField(10, Color.Blue);
		model.setField(15, Color.Blue);
		
		model.setField(1, Color.Red);
		model.setField(2, Color.Red);
		model.setField(3, Color.Red);
		
		assertTrue(model.isWinner(Color.Blue));
		assertFalse(model.isWinner(Color.Red));
		assertTrue(model.hasWinner());
	}

	@Test
	public void fourInXaxisTest()
	{
		model = new Model();  
		
		model.setField(0, Color.Blue);
		model.setField(1, Color.Blue);
		model.setField(2, Color.Blue);
		model.setField(3, Color.Blue);
		
		assertTrue(model.fourInXaxis(Color.Blue));
	}
	@Test
	public void fourInYaxisTest()
	{
		model = new Model();  
		
		model.setField(0, Color.Blue);
		model.setField(4, Color.Blue);
		model.setField(8, Color.Blue);
		model.setField(12, Color.Blue);
		
		assertTrue(model.fourInYaxis(Color.Blue));
	}
	@Test
	public void fourInZaxisTest()
	{
		model = new Model();  
		
		model.setField(0, Color.Blue);
		model.setField(16, Color.Blue);
		model.setField(32, Color.Blue);
		model.setField(48, Color.Blue);
		
		assertTrue(model.fourInZaxis(Color.Blue));
	}
	@Test
	public void diagonal1_InXYPlaneTest()
	{
		model = new Model();  
		
		model.setField(12, Color.Blue);
		model.setField(9, Color.Blue);
		model.setField(6, Color.Blue);
		model.setField(3, Color.Blue);
		
		assertTrue(model.diagonal1_InXYPlane(Color.Blue));
	}
	@Test
	public void diagonal2_InXYPlaneTest()
	{
		model = new Model();  
		
		model.setField(0, Color.Blue);
		model.setField(5, Color.Blue);
		model.setField(10, Color.Blue);
		model.setField(15, Color.Blue);
		
		assertTrue(model.diagonal2_InXYPlane(Color.Blue));
	}
	
	@Test
	public void bestFieldToMoveTest()
	{
		model = new Model();  
		model.setField(0, Color.Blue);
		model.setField(5, Color.Blue);
		model.setField(10, Color.Blue);
		
		assertEquals(model.bestFieldToMove(Color.Red),15);
		
		model = new Model();  
		model.setField(0, Color.Blue);
		model.setField(5, Color.Blue);
		model.setField(10, Color.Blue);
		
		assertEquals(model.bestFieldToMove(Color.Blue),15);
	}

	
}
