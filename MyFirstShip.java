package SpaceBattle;

import java.awt.*;
import java.util.*;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.Point;
import ihs.apcs.spacebattle.commands.*;

//Code for a client spacecraft
public class MyFirstShip extends BasicSpaceship {
	
	//private fields for manipulating ship movement
	private int width;
	private int height;
	private Point p = new Point(0, 0);
	private int c = 0;
	private double val = .5;
	private boolean GoToPoint = false;
	private boolean NStop = false;
	private boolean MoveBehaviorLine = false;
	private boolean Pause = false;
	private boolean Break = false;
	private boolean Rotate = false;
	private boolean GoToCenter = true;
	
	//Join the server with the IP address as the first string parameter, and another parameter of the new Ship
	public static void main(String[] args) {
		TextClient.run("10.136.35.81", new MyFirstShip());
	}
	
	//Register the ship and get basic map data
	public RegistrationData registerShip(int numImages, int worldWidth, int worldHeight) {
		this.width = worldWidth;
		this.height = worldHeight;
		p = new Point(this.width, this.height);
		return new RegistrationData("The Meme Machine", new Color(255, 0, 0), 9);
	}
	
	//Private method used to get to the middle of the map
	private double getThrustDuration(double distance) {
		return distance/60;
	}
	
	//Returns a new command for the ship to accept
	public ShipCommand getNextCommand(BasicEnvironment env) {
		
		//Get the current status (location, velocity, etc) of the ship
		ObjectStatus ship = env.getShipStatus();
		
		//Moves the ship in a straight line to the right
		if(MoveBehaviorLine) { //Straight Line Movement
			double rad = env.getShipStatus().getMovementDirection();
			if(rad<0) {return new RotateCommand(Math.abs(rad));}
			if(rad>0) {return new RotateCommand(-1*(rad));}
			return new ThrustCommand('F', 100, 1.0);
		}
		
		//Goes to a specific point in space, indicated by the private field 'p'
		if(GoToPoint) {
			if(NStop) {NStop = false; return new IdleCommand(1);}
			
			if(ship.getPosition().getAngleTo(p) - ship.getOrientation() <= 1) {
				return new ThrustCommand('B', 5, 1.0);
			}
			
			NStop = true;
			return new RotateCommand(ship.getPosition().getAngleTo(p) - ship.getOrientation());	
		}
		
		//Returns an idle command of time val
		if(Pause) {
			Pause = false;
			return new IdleCommand(val);
		}
		
		//Breaks the ship to a stop
		if(Break) {
			Break = false;
			return new BrakeCommand(0.0);
		}
		
		//Rotates the ship to each corner using a switch-case statement
		if(Rotate) {
			switch(c) {
				case 1:
					c++; Pause = true;
					return new RotateCommand(ship.getPosition().getAngleTo(new Point(0, 0)) - ship.getOrientation());
				case 2:
					c++; Pause = true;
					return new RotateCommand(ship.getPosition().getAngleTo(new Point(width, 0)) - ship.getOrientation());
				case 3:
					c++; Pause = true;
					return new RotateCommand(ship.getPosition().getAngleTo(new Point(width, height)) - ship.getOrientation());
				case 4:
					c = 0; Pause = true;
					return new RotateCommand(ship.getPosition().getAngleTo(new Point(0, height)) - ship.getOrientation());
			}
		}
		
		//Moves the ship to the center of the map, and attempts to keep it there
		if(GoToCenter) {
			
			//Prints data for debugging
			System.out.println(ship.getPosition().getAngleTo(new Point(width/2, height/2))+" "+ship.getOrientation());
			
			//Get the angle to the center of the map
			int pos = ship.getPosition().getAngleTo(new Point(width/2, height/2));
			
			//Fix the coordinates as they use different systems
			if(pos <0) pos = 360+pos;
			
			//Thrusts the ship forward and then idles if the angles match
			if(pos - Math.round(ship.getOrientation()) <= 1 && pos - Math.round(ship.getOrientation()) >= -1) {
				Break = true;
				Pause = true;
				val = 8;
				return new ThrustCommand('B', getThrustDuration(ship.getPosition().getDistanceTo(new Point(width/2, height/2))), 1.0);
			}
			
			//Else rotates the ship so it's facing the center
			Pause = true;
			val = .5;
			return new RotateCommand(ship.getPosition().getAngleTo(new Point(width/2, height/2)) - ship.getOrientation());
		}
		
		//Returns an idle command if none of the other behaviors are active
		return new IdleCommand(.5);
	}
}
