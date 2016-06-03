package SpaceBattle;

//Import the directories for spacebattle
import java.awt.*;
import java.util.*;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.commands.*;
import ihs.apcs.spacebattle.games.*;

public class CombatShip extends BasicSpaceship {
	
	//Current height + width of the world
	private int width;
	private int height;
	
	//Counter
	private int current = 0;
	
	//Current object to interact with
	private ObjectStatus closestEntity = null;
	
	//Handle bools
	private boolean playerHandle = false;
	private boolean planetHandle = false;
	private boolean blackHoleHandle = false;
	private boolean asteroidHandle = false;
	
	public static void main(String[] args) {
		TextClient.run("10.136.34.115", new CombatShip());
	}

	//Registers the ship with the server and sets the current width and height of the world
	public RegistrationData registerShip(int numImages, int worldWidth, int worldHeight) {
		width = worldWidth;
		height = worldHeight;
		return new RegistrationData("Nick", new Color(255, 255, 255), 9);
	}

	//Determine the ship's next command
	public ShipCommand getNextCommand(BasicEnvironment env) {
		
		//Status of the ship
		ObjectStatus ship = env.getShipStatus();
		
		//Heal the ship if health is low
		if(ship.getHealth() <= 50) {
			return new RepairCommand((int) (100 - ship.getHealth()));
		}
		
		//Interact with another player's ship by rotating, flying away & firing at it
		if(playerHandle) {
			switch(current) {
				case 0:
					current++;
					return new RotateCommand(ship.getPosition().getAngleTo(closestEntity.getPosition()) - ship.getOrientation());
					
				case 1:
					current++;
					return new ThrustCommand('F', 2.5, 1.0);
					
				case 2:
					current++;
					return new FireTorpedoCommand('F');
					
				case 3:
					current++;
					return new FireTorpedoCommand('F');
					
				case 4:
					current = 0; playerHandle = false;
					return new FireTorpedoCommand('F');
			}
		}
		
		//Move away from an incoming planet object
		if(planetHandle) {
			switch(current) {
				case 0:
					current++;
					return new RotateCommand(ship.getPosition().getAngleTo(closestEntity.getPosition()) + 180);
					
				case 1:
					current = 0; planetHandle = false;
					return new ThrustCommand('B', 2.5, 1.0);
			}
		}
		
		//Warp away from a black hole (Thrust can not escape it)
		if(blackHoleHandle) {
			switch(current) {
			case 0:
				current++;
				return new RotateCommand(ship.getPosition().getAngleTo(closestEntity.getPosition()) + 180);
				
			case 1:
				current++; blackHoleHandle = false;
				return new WarpCommand(250);
			}
		}
		
		//Fly away from an asteroid then attempt to destroy it
		if(asteroidHandle) {
			switch(current) {
			case 0:
				current++;
				return new ThrustCommand('B', 1.0, 1.0);
				
			case 1:
				current++;
				return new RotateCommand(ship.getPosition().getAngleTo(closestEntity.getPosition()) - ship.getOrientation());
				
			case 2:
				current++;
				return new FireTorpedoCommand('F');
				
			case 3:
				current++;
				return new FireTorpedoCommand('F');
				
			case 4:
				current = 0; asteroidHandle = false;
				return new FireTorpedoCommand('F');
			}
		}
		
		//Determine the closest entity
		switch(current) {
		
			//Scan the radar
			case 0:
				current++;
				return new RadarCommand(5);
			
			//Find the closest object to the player
			case 1:
				current = 0;
				
				if(env.getRadar() != null && env.getRadar().size() > 0) {
					closestEntity = env.getRadar().get(0);
					for(ObjectStatus obj : env.getRadar()) {
						if(obj.getPosition().getDistanceTo(ship.getPosition()) < closestEntity.getPosition().getDistanceTo(ship.getPosition())) {
							closestEntity = obj;
							if(closestEntity.getType().equals("Ship")) {playerHandle = true; return new IdleCommand(.1);}
						}
					}
				
					//Determine the type of the object, initiating it's handle sequence
					String entType = closestEntity.getType();
					if(entType.equals("Ship")) {playerHandle = true;}
					if(entType.equals("Planet")) {planetHandle = true;}
					if(entType.equals("BlackHole")) {blackHoleHandle = true;}
					if(entType.equals("Asteroid")) {asteroidHandle = true;}
					
					//Immediately attempt to block a torpedo as soon as it's detected
					if(entType.equals("Torpedo") && Math.abs((int)ship.getPosition().getDistanceTo(closestEntity.getPosition())) < 150){
						return new RaiseShieldsCommand(ship.getPosition().getDistanceTo(closestEntity.getPosition()) / 35);
					}
				}
			
				//Idle before executing the handle
				return new IdleCommand(.1);
		}
		
		//Default to return an idle command if all else fails
		return new IdleCommand(.5);
	}

}
