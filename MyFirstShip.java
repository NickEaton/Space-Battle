package SpaceBattle;

import java.awt.*;
import java.util.*;

import ihs.apcs.spacebattle.*;
import ihs.apcs.spacebattle.Point;
import ihs.apcs.spacebattle.commands.*;

public class MyFirstShip extends BasicSpaceship {
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
	
	public static void main(String[] args) {
		TextClient.run("10.136.35.81", new MyFirstShip());
	}
	
	public RegistrationData registerShip(int numImages, int worldWidth, int worldHeight) {
		this.width = worldWidth;
		this.height = worldHeight;
		p = new Point(this.width, this.height);
		return new RegistrationData("The Meme Machine", new Color(255, 0, 0), 9);
	}
	
	private double getThrustDuration(double distance) {
		return distance/60;
	}
	public ShipCommand getNextCommand(BasicEnvironment env) {
		ObjectStatus ship = env.getShipStatus();
		
		if(MoveBehaviorLine) { //Straight Line Movement
			double rad = env.getShipStatus().getMovementDirection();
			if(rad<0) {return new RotateCommand(Math.abs(rad));}
			if(rad>0) {return new RotateCommand(-1*(rad));}
			return new ThrustCommand('F', 100, 1.0);
		}
		
		if(GoToPoint) {
			if(NStop) {NStop = false; return new IdleCommand(1);}
			
			if(ship.getPosition().getAngleTo(p) - ship.getOrientation() <= 1) {
				return new ThrustCommand('B', 5, 1.0);
			}
			
			NStop = true;
			return new RotateCommand(ship.getPosition().getAngleTo(p) - ship.getOrientation());	
		}
		
		if(Pause) {
			Pause = false;
			return new IdleCommand(val);
		}
		
		if(Break) {
			Break = false;
			return new BrakeCommand(0.0);
		}
		
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
		
		if(GoToCenter) {
			System.out.println(ship.getPosition().getAngleTo(new Point(width/2, height/2))+" "+ship.getOrientation());
			int pos = ship.getPosition().getAngleTo(new Point(width/2, height/2));
			if(pos <0) pos = 360+pos;
			if(pos - Math.round(ship.getOrientation()) <= 1 && pos - Math.round(ship.getOrientation()) >= -1) {
				Break = true;
				Pause = true;
				val = 8;
				return new ThrustCommand('B', getThrustDuration(ship.getPosition().getDistanceTo(new Point(width/2, height/2))), 1.0);
			}
			Pause = true;
			val = .5;
			return new RotateCommand(ship.getPosition().getAngleTo(new Point(width/2, height/2)) - ship.getOrientation());
		}
		
		return new IdleCommand(.5);
	}
}
