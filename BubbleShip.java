package SpaceBattle;

//Import the classes and methods from all of spacebattle
import java.awt.Color;
import java.util.*;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.commands.*;
import ihs.apcs.spacebattle.games.*;

public class BubbleShip implements Spaceship<KingOfTheBubbleGameInfo> {
	
	//Instance vars and counter
	private int width;
	private int height;
	private int current = 0;
	Point bubble = null;
	
	public static void main(String[] args) {
		TextClient.run("10.136.34.115", new BubbleShip());
	}
	
	//Create the ship on the server and set the current width and height of the world
	public RegistrationData registerShip(int numImages, int width, int height) {
		this.width = width;
		this.height = height;
		
		return new RegistrationData("The Meme Machine Mk4", new Color(255, 255, 255), 9);
	}

	//Return a command to the ship
	public ShipCommand getNextCommand(Environment<KingOfTheBubbleGameInfo> env) {
		
		//Current status of the ship (velocity, position, etc)
		ObjectStatus ship = env.getShipStatus();
		
		switch(current) {
		
			//Find the closest Bubble and rotate to face it
			case 0:
				current++;
				bubble = env.getGameInfo().getBubblePositions().get(0);
				
				for(Point obj : env.getGameInfo().getBubblePositions()) {
					if(ship.getPosition().getDistanceTo(obj) < ship.getPosition().getDistanceTo(bubble)) {
						bubble = obj;
					}
				}
				
				return new RotateCommand(ship.getPosition().getAngleTo(bubble) - ship.getOrientation());
				
			/*Go to the nearest Bubble, but if the ship is currently inside a bubble
			 * then simply reset the ship until the bubble disappears
			 */
			case 1:
				
				if(ship.getPosition().getDistanceTo(bubble) < 2.0) {
					current = 0;
					return new IdleCommand(.5);
				}
				
				current++;
				
				if(ship.getPosition().getDistanceTo(bubble) < 400) {
					return new WarpCommand(ship.getPosition().getDistanceTo(bubble));
				}
				
				return new ThrustCommand('B', ship.getPosition().getDistanceTo(bubble)/50, 1.0);
			
			//Idle the ship until it's done traveling to the bubble
			case 2:
				current++;
				return new IdleCommand(ship.getPosition().getDistanceTo(bubble)/50 + .01);
			
			//Stop the ship inside the bubble and keep it there
			case 3:
				current = 0;
				return new BrakeCommand(.00);
		
		}
		
		//Default to return an idle command if no other commands execute
		return new IdleCommand(.5);
	}

	//Do nothing when the ship is destroyed
	public void shipDestroyed(String destroyedBy) {return;}	
}
