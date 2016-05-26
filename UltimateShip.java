package SpaceBattle;

import java.awt.*;
import java.util.*;
import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.Point;
import ihs.apcs.spacebattle.commands.*;

//An advanced ship utilizing the 'Advanced' commands (Warp, Radar, etc.)
public class UltimateShip extends BasicSpaceship {
	
	//Current width and height of the map
	private int width;
	private int height;
	
	//Counter variables
	private int current = 0;
	private int c = 0;
	private double time = .5;
	
	//Radar variables
	ObjectStatus bauble = null;
	ArrayList<ObjectStatus> radarSweep = null;
	
	//Booleans determining ship behavior
	private boolean WarpToMiddle = false;
	private boolean GoToBaubles = true;
	private boolean AttackShip = false;
	
	public static void main(String[] args) {
		TextClient.run("10.136.43.69", new UltimateShip());
	}
	
	public RegistrationData registerShip(int numImages, int worldWidth, int worldHeight) {
		
		//Set the instance variables of width and height to the current world width and world height respectively
		this.width = worldWidth;
		this.height = worldHeight;
		
		//Register the ship on the server
		return new RegistrationData("The Meme Machine Mk2", new Color(255, 255, 255), 9);
	}

	public ShipCommand getNextCommand(BasicEnvironment env) {
		
		//The current status (health, location, speed, etc) of the ship
		ObjectStatus ship = env.getShipStatus();
		
		//warp to the middle of the map
		if(WarpToMiddle) {
			
			//Switch-case which warps the ship to the middle of the map
			switch(current) {
				case 0:
					current++; 
					int val = ship.getPosition().getAngleTo(new Point(width/2, height/2));
					if(val <= 0) {val = 360 + val;}
					return new RotateCommand(val - ship.getOrientation());
					
				case 1:
					current++; 
					double dist = ship.getPosition().getDistanceTo(new Point(width/2, height/2));
					if(dist >= 400.0) {
						return new WarpCommand(400);
					}
					return new WarpCommand(dist);
					
				case 2:
					current = 0;
					WarpToMiddle = false;
					return new IdleCommand(.5);
			}
		}
		
		//Attack the closest ship
		if(AttackShip) {
			
			//Quick switch case to rotate and fire at the nearest ship
			switch(c) {
			
				//Rotate the ship to the nearest enemy ship
				case 0:
					c++;
					
					//Instantiate through the scanned objects and search for a ship to fire at
					for(ObjectStatus obj : radarSweep) {
						if(obj.getType().equals("Ship")) {
							return new RotateCommand(ship.getPosition().getAngleTo(obj.getPosition()));
						}
					}
					
					//if no ship object can be found, stop the attack sequence
					c = 0;
					AttackShip = false;
					break;
				
				case 1:
					
					//Reset the counter and quick the attack behavior then fire a torpedo at a ship
					c = 0;
					AttackShip = false;
					return new FireTorpedoCommand('B');
			}
		}
		
		//Locate and move towards Baubles on the map, using a radar scan to find them
		if(GoToBaubles) {
			
			//Switch-case statement to determine the next appropriate command
			switch(current) {
			
				//Get a radar sweep of the current map
				case 0:
					current++; 
					return new RadarCommand(5);
					
				/*Determine if the ship should move to a bauble, fire at another ship, or warp away from a threat (Black hole, 
				 * Planet, Asteroid, etc.) If nothing is scanned, return idle and warp to the middle and scan the radar again.
				 * Else, rotate to face the bauble, fire at an enemy ship or warp/thrust away.
				*/
				case 1:
					if(env.getRadar() != null) {
						for(ObjectStatus obj : env.getRadar()) {
							
							radarSweep = env.getRadar();					
							bauble = obj;
							
							//Check for a bauble
							if(obj.getType().equals("Bauble") && obj.getValue() > 1) {
								current++;
								return new RotateCommand(ship.getPosition().getAngleTo(obj.getPosition()) - ship.getOrientation());
							}
							
							//Shoot at an approaching ship 33% of the time (so the ship doesn't exclusively fire torpedoes)
							if(obj.getType().equals("Ship") && new Random().nextInt(3) == 0) {
								current = 0;
								AttackShip = true;
								return new IdleCommand(.1); 
							}
							
							//Warp away from a black hole, planet, or asteroid
							if(obj.getType().equals("BlackHole") || obj.getType().equals("Planet") || obj.getType().equals("Asteroid")) {
								current = 0;
								WarpToMiddle = true;
								return new IdleCommand(.1);
							}
						}
					}
					
					//reset the count, warp the ship to the middle of the map, then get a new radar sweep
					current = 0; 
					WarpToMiddle = true;
					return new IdleCommand(.1);
					
				//Warp to the bauble if possible, else thrust towards it. Reset the ship if it is already at a bauble
				case 2:
					if(ship.getPosition().getDistanceTo(bauble.getPosition()) <= 5) {
						current = 0;
						return new IdleCommand(.5);
					}
					
					if(ship.getPosition().getDistanceTo(bauble.getPosition()) < 400) {
						time = 1.5;
						current++;
						return new WarpCommand(ship.getPosition().getDistanceTo(bauble.getPosition()));
					}
					
					time = ship.getPosition().getDistanceTo(bauble.getPosition())/50;
					current++;
					return new ThrustCommand('B', ship.getPosition().getDistanceTo(bauble.getPosition())/50, 1.0);
					
				//Idle while the ship goes to a bauble
				case 3:
					current++;
					return new IdleCommand(time);
					
				//Brake on top of the bauble
				case 4:
					current = 0;
					return new BrakeCommand(.00);
			}
		}
		
		//default to return an idle command
		return new IdleCommand(1.0);
	}

}
