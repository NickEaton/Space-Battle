import java.awt.Color;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.commands.*;

/**
 * Example 'Dummy' Ship for Basic Connection with no Game.
 * 
 * 
 *
 */
public class MyFirstShip extends BasicSpaceship {

	public static void main(String[] args) {
		TextClient.run("127.0.0.1", new MyFirstShip());		
	}
	
	@Override
	public RegistrationData registerShip(int numImages, int worldWidth,	int worldHeight) {
		return new RegistrationData("Sample Ship", new Color(255, 255, 255), 0);
	}

	@Override
	public ShipCommand getNextCommand(BasicEnvironment env) {
		return new IdleCommand(0.5);
	}

}

