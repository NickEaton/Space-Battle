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
	private int numBaubles = 0;
	private Point base = null;
	private ObjectStatus bauble = null;
	
	
	//Behavior booleans
	private boolean WarpToBase = false;
	
	public static void main(String[] args) {
		TextClient.run("10.136.xx.xxx", new HuntShip());
	}
	
	public RegistrationData registerShip(int numImages, int width, int height) {
		this.width = width;
		this.height = height;
		return new RegistrationData("The Meme Machine Mk3", new Color(255, 255, 255), 9);
	}
	
	public ShipCommand getNextCommand(Environment<BaubleHuntGameInfo> env) {
		
		ObjectStatus ship = env.getShipStatus();
		base = env.getGameInfo().getHomeBasePosition();
		
		//Warp back to the home base
		if(WarpToBase) {
			switch(current) {
				case 0:
					return new RotateCommand(ship.getPosition().getAngleTo(base) - ship.getOrientation());
				
				case 1:
					WarpToBase = false;
					return new WarpCommand(ship.getPosition().getDistanceTo(base));
			}
		}
		
		switch(current) {
			case 0:
				current++;
				return new RadarCommand(5);
				
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
				
				current++;
				return new RotateCommand(ship.getPosition().getAngleTo(bauble.getPosition()));
				
			case 2:
				if(ship.getPosition().getDistanceTo(bauble.getPosition()) < 400) {
					current++;
					return new WarpCommand(ship.getPosition().getDistanceTo(bauble.getPosition()));
				}
				else {
					current++;
					return new ThrustCommand('B', ship.getPosition().getDistanceTo(bauble.getPosition())/50, 1.0);
				}
			
			case 3:
				current++;
				return new BrakeCommand(.00);
			
			case 4:
				if(ship.getNumberStored() >= 3) {
					current++;
					return new RotateCommand(ship.getPosition().getAngleTo(base) - ship.getOrientation());
				}
				
				current = 0;
				return new IdleCommand(.5);
				
			case 5:
				if(ship.getPosition().getDistanceTo(base) < 400) {
					current++;
					return new WarpCommand(ship.getPosition().getDistanceTo(base));
				}
				
				current++;
				return new ThrustCommand('B', ship.getPosition().getDistanceTo(base)/50, 1.0);
				
			case 6:
				current = 0;
				return new BrakeCommand(.00);
				
		}
		
		//default to return an idle command
		return new IdleCommand(.5);
	}
	
	public void shipDestroyed(String destroyedBy) {return;}
}
