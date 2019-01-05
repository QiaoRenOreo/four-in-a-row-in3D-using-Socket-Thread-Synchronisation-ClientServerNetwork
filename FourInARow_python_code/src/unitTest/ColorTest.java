package unitTest;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import model.Color;
import model.Model;
public class ColorTest 
{
	Color color;
	@Before  public void setUp() {        
		color = Color.Red;  
	}
	@Test
	public void otherTest() {

		assertEquals(color.other(), Color.Blue);
		
	}
}
