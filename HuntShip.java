package SpaceBattle;

import java.awt.Color;
import java.util.*;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.commands.*;
import ihs.apcs.spacebattle.games.*;

public class HuntShip implements Spaceship<BaubleHuntGameInfo> {
	
	//instance vars
	private int width;
	private int height;
	private int current = 0;
	private double time = .5;
	private Point base = null;
	private ObjectStatus bauble = null;
	
	
	//Behavior boolean
	private boolean WarpToBase = false;
	
	public static void main(String[] args) {
		TextClient.run("10.136.34.115", new HuntShip());
	}
	
	//Register the ship on the server-side
	public RegistrationData registerShip(int numImages, int width, int height) {
		this.width = width;
		this.height = height;
		return new RegistrationData("The Meme Machine Mk3", new Color(255, 255, 255), 9);
	}
	
	//Method to move the ship back to the 'home base'
	public ShipCommand returnToBase(Environment<BaubleHuntGameInfo> env) {
		time = env.getShipStatus().getPosition().getDistanceTo(base)/50 + 1;
		
		if(env.getShipStatus().getPosition().getDistanceTo(base) < 400) {
			return new WarpCommand(env.getShipStatus().getPosition().getDistanceTo(base));
		}
		
		return new ThrustCommand('B', env.getShipStatus().getPosition().getDistanceTo(base)/50, 1.0);
	}
	
	//Get the ship's next command
	public ShipCommand getNextCommand(Environment<BaubleHuntGameInfo> env) {
		
		//Current status of the ship, and the current location of the ship's 'home base'
		ObjectStatus ship = env.getShipStatus();
		base = env.getGameInfo().getHomeBasePosition();
		
		//Debug to check the ship is functioning correctly through console output
		System.out.println(current);
	
		switch(current) {
		
			//Issue a max strength radar scan
			case 0:
				current++;
				time = .5;
				return new RadarCommand(5);
			
			//Find the best bauble to go to, and rotate the ship towards it
			case 1:
				current++;
				bauble = env.getRadar().get(0);
				
				for(ObjectStatus obj : env.getRadar()) {
					if(obj.getType().equals("Bauble") && obj.getValue() > 1) {
						if(obj.getPosition().getDistanceTo(ship.getPosition()) < bauble.getPosition().getDistanceTo(ship.getPosition())) {
							bauble = obj;
						}
					}
				}
				
				return new RotateCommand(ship.getPosition().getAngleTo(bauble.getPosition()) - ship.getOrientation());
				
			//Warp to the bauble if possible, else thrust towards it
			case 2:
				if(ship.getPosition().getDistanceTo(bauble.getPosition()) < 400) {
					current++;
					return new WarpCommand(ship.getPosition().getDistanceTo(bauble.getPosition()));
				}
				
				current++;
				time = ship.getPosition().getDistanceTo(bauble.getPosition())/50 + 1;
				return new ThrustCommand('B', time, 1.0);
			
			//Idle the ship while it's traveling / cooling down
			case 3:
				current++;
				return new IdleCommand(time);
			
			//Stop the ship on the bauble
			case 4:
				current++;
				return new BrakeCommand(.00);
			
			//If the ship has 3 or more bauble stored, return to the home base, else get more baubles
			case 5:
				if(ship.getNumberStored() >= 3) {
					current++;
					return new RotateCommand(ship.getPosition().getAngleTo(base) - ship.getOrientation());
				}
				
				current = 0;
				return new IdleCommand(.5);
			
			//Stop all movement
			case 6:
				current++;
				return new BrakeCommand(.00);
			
			//Face the home base
			case 7:
				current++;
				return new RotateCommand(ship.getPosition().getDistanceTo(base) - ship.getOrientation());
			
			//Either warp to the base or thrust to it
			case 8:
				current++;
				return returnToBase(env);
			
			//Wait while we warp or thrust
			case 9:
				current = 0;
				return new IdleCommand(time);
				
		}
		
		//default to return an idle command
		return new IdleCommand(.5);
	}
	
	//Do nothing in the case of the ship being destroyed
	public void shipDestroyed(String destroyedBy) {return;}
}
