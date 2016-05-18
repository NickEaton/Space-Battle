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
	private Point p = new Point(50, 50);
	private int c = 0;
	private double val = .5;
	private double time = .5;
	private double time2 = 5.5;
	private boolean GoToPoint = false;
	private boolean NStop = false;
	private boolean MoveBehaviorLine = false;
	private boolean Pause = false;
	private boolean Break = false;
	private boolean Rotate = false;
	private boolean GoToCenter = true;
	private boolean DrawShapes = false;
	private boolean DrawSquare = false;
	private boolean DrawStar = false;
	private boolean DrawSpiral = false;
	private boolean DrawInverseSpiral = false;
	private boolean DrawOctoSquare = false;
	
	private int current = 0;
	private int ssc = 0;
	private int sscs = 0;
	
	//Join the server with the IP address as the first string parameter, and another parameter of the new Ship
	public static void main(String[] args) {
		TextClient.run("10.136.43.171", new MyFirstShip());
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
	
	//Private method to set the next point
	private void setPoint() {
		if(ssc == 2) {
			ssc = 0;
			p = new Point(100, 50);
		}
		
		if(ssc == 1) {
			ssc++;
			p = new Point(100, 100);
		}
		
		if(ssc == 0) {
			ssc++;
			p = new Point(50, 100);
		}
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
		
		//Draw shapes in space
		if(DrawShapes) {
			
			//Draw a square
			if(DrawSquare) {
				
				if(ssc < 4) {
					switch(current) {
						case 0:
							current++;
							return new DeployLaserBeaconCommand();
							
						case 1:
							current++;
							return new RotateCommand(90);
							
						case 2:
							current++;
							return new ThrustCommand('B', 3.0, 1.0);
							
						case 3:
							current++;
							return new IdleCommand(2.0);
							
						case 4:
							current++;
							return new BrakeCommand(.0);
							
						case 5:
							current = 0; ssc++;
							return new DeployLaserBeaconCommand();
					}
				}
				
				DrawSquare = false; ssc = 0;
			}
			
			if(sscs == 0) {sscs++; return new RotateCommand(72);} 
			if(sscs == 1) {sscs++; return new ThrustCommand('B', 7.5, 1.0);}
			if(sscs == 2) {sscs++; return new IdleCommand(7.5);}
			if(sscs == 3) {sscs++; return new BrakeCommand(.00);}
			
			//Draw a star
			if(DrawStar) {
				
				if(ssc < 5) {
					switch(current) {
						case 0:
							current++;
							return new DeployLaserBeaconCommand();
						
						case 1:
							current++;
							return new RotateCommand(210);
							
						case 2:
							current++;
							return new ThrustCommand('B', 5.0, 1.0);
						
						case 3:
							current ++;
							return new IdleCommand(2.0);
							
						case 4:
							current++;
							return new BrakeCommand(.0);
						
						case 5:
							current++;
							return new IdleCommand(.5);
							
						case 6:
							current = 0; ssc++;
							return new DeployLaserBeaconCommand();
					}
				}
				
				DrawStar = false; ssc = 0;
			}
			
			if(sscs == 4) {sscs++; return new RotateCommand(72);} 
			if(sscs == 5) {sscs++; return new ThrustCommand('B', 7.5, 1.0);}
			if(sscs == 6) {sscs++; return new IdleCommand(7.5);}
			if(sscs == 7) {sscs++; return new BrakeCommand(.00);}
			
			//Draw a spiral
			if(DrawSpiral) {
				
				if(ssc < 10) {
					switch(current) {
						case 0:
							current++;
							return new DeployLaserBeaconCommand();
							
						case 1:
							current++;
							return new ThrustCommand('B', time, 1.0);
							
						case 2:
							current++;
							return new IdleCommand(time);
							
						case 3:
							current++;
							return new BrakeCommand(.0);
							
						case 4:
							current++;
							return new IdleCommand(2.0);
							
						case 5:
							current++; time += .5;
							return new DeployLaserBeaconCommand();
							
						case 6:
							current++;
							return new RotateCommand(130/time);
							
						case 7:
							time +=.2; current = 0; ssc++;
							return new IdleCommand(1.0);
					}
				}
				
				DrawSpiral = false; ssc = 0;
			}
			
			if(sscs == 7) {sscs++; return new RotateCommand(72);} 
			if(sscs == 8) {sscs++; return new ThrustCommand('B', 7.5, 1.0);}
			if(sscs == 9) {sscs++; return new IdleCommand(7.5);}
			if(sscs == 10) {sscs++; return new BrakeCommand(.00);}
			
			//Draw an Inverse Spiral
			if(DrawInverseSpiral) {
				
				if(ssc < 5) {
					switch(current) {
						case 0:
							current++;
							return new DeployLaserBeaconCommand();
						case 1:
							current++;
							return new ThrustCommand('B', time2, 1.0);
						case 2:
							current++;
							return new IdleCommand(time2);
						case 3:
							current++;
							return new BrakeCommand(.00);
						case 4:
							current++;
							return new IdleCommand(2.0);
						case 5:
							current++; time2 -= .5;
							return new DeployLaserBeaconCommand();
						case 6:
							current++;
							return new RotateCommand(130/time2);
						case 7:
							current = 0; time2 -= .2;
							return new IdleCommand(time2);
					}
				}
				
				DrawInverseSpiral = false; ssc = 0;
			}
			
			if(sscs == 10) {sscs++; return new RotateCommand(72);} 
			if(sscs == 11) {sscs++; return new ThrustCommand('B', 7.5, 1.0);}
			if(sscs == 12) {sscs++; return new IdleCommand(7.5);}
			if(sscs == 13) {sscs++; return new BrakeCommand(.00);}
			
			//Draw an Octo-Square
			if(DrawOctoSquare) {
				
				if(ssc < 4) {
					if(c < 8) {
						switch(current){
							case 0:
								current++;
								return new DeployLaserBeaconCommand();
							case 1:
								current++;
								return new ThrustCommand('B', 5.0, 1.0);
							case 2:
								current++;
								return new IdleCommand(5.0);
							case 3:
								current++;
								return new BrakeCommand(.00);
							case 4:
								current++;
								return new DeployLaserBeaconCommand();
							case 5:
								current = 0; c++; 
								return new RotateCommand(45);
						}
					}
						
					if(c == 8) {
						c++;
						return new RotateCommand(90);
					}
					
					if(c == 9) {
						c++;
						return new ThrustCommand('B', 5.0, 1.0);
					}
					
					if(c == 10) {
						c++;
						return new IdleCommand(5.0);
					}
					
					if(c == 11) {
						c++;
						return new BrakeCommand(.00);
					}
					
					switch(current) {
						case 0:
							current++;
							return new DeployLaserBeaconCommand();
						case 1:
							current++;
							return new ThrustCommand('B', 3.5, 1.0);
						case 2:
							current++;
							return new IdleCommand(3.5);
						case 3:
							current++;
							return new BrakeCommand(.00);
						case 4:
							current++;
							return new DeployLaserBeaconCommand();
						case 5:
							current = 0; ssc++;
							return new RotateCommand(90);
					}
				}
				
				DrawOctoSquare = false; ssc = 0;
			}
		}	
		
		//Returns an idle command if none of the other behaviors are active
		return new IdleCommand(.5);
	}
}