/*
	This file is part of Eixample.

    Eixample is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation version 3.

    Eixample is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Eixample.  If not, see <http://www.gnu.org/licenses/>.
    
    Authors: David Sanchez Pinsach and Alex Pardo Fernandez

 */

package eixample.agents.vehicles;

import java.awt.Color;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import eixample.agents.trafficLights.TrafficLight;
import eixample.main.DataController;
import eixample.map.Junction;

/**
 * Car agent Type: reactive agent: greedy with manhattan distance as heuristic
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class Car implements IVehicle {
	private Context<Object> context;
	private Grid<Object> grid;
	private ContinuousSpace<Object> space;
	private GridPoint gp;
	private int posX;
	private int posY;

	private int direction;
	private boolean arrived_to_junction = false;
	private boolean disapear = false;

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int WEST = 2;
	public static final int EAST = 3;

	private boolean acc = false;
	private boolean breaking = false;
	private boolean stop = true;
	private boolean isTurning = false;
	private int break_ticks = 0;
	private int stopped_ticks = 0;
	private int acc_ticks = 0;
	private int turning_ticks = 0;
	private int add = 0;
	private int ticks_to_destiny = 0;

	private int carril; // carril 1 mes al nord/oest i 3 mes al sud/est

	private int stopped_by = 0; // 1 = car, 2 = traffic light
	private final int CAR = 1;
	private final int TL = 2;

	private Car front_car = null;
	private TrafficLight front_light = null;

	private Junction origen = null;
	private Junction dest = null;

	private DataController controller = null;

	/**
	 * Constructor
	 * 
	 * @param carril
	 * @param x_dest
	 * @param y_dest
	 * @param net
	 * @param controller
	 */
	public Car(int carril, int x_dest, int y_dest, Network<Object> net,
			DataController controller) {
		this.carril = carril;
		accelerate();
		Iterable<Object> nodes = net.getNodes();

		for (Object j : nodes) {
			try {
				if (x_dest == ((Junction) j).getX()
						&& y_dest == ((Junction) j).getY()) {
					dest = (Junction) j;
				}
			} catch (ClassCastException e) {
			}
		}
		direction = EAST;
		this.controller = controller;
	}

	// DISPLAY PURPOSES
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int isAtDestination() {
		if (disapear)
			return 0;
		else
			return 1;
	}

	public boolean isJunction() {
		if (getJunction(posX, posY) != null) {
			return true;
		}
		return false;
	}

	public int[] getDest() {
		int[] tmp = new int[2];
		tmp[0] = dest.getX();
		tmp[1] = dest.getY();
		return tmp;
	}

	public int getDestId() {
		return dest.getId();
	}

	public int getTicksToDestiny() {
		return ticks_to_destiny;
	}

	public boolean isFrontCar() {
		return (front_car != null);
	}

	public int getFrontCarStatus() {
		if (isFrontCar()) {
			if (front_car.isBreaking()) {
				return 1;
			} else if (front_car.isAtDestination() == 0) {
				return 2;
			}
		} else {
			return -1;
		}
		return 3;
	}

	public int getStoppedBy() {
		return stopped_by;
	}

	public boolean isStop() {
		return stop;
	}

	public boolean isBreaking() {
		if (disapear) {
			return false;
		}
		return (breaking || stop);
	}

	public boolean isTurning() {
		return isTurning;
	}

	public int getCarril() {
		return carril;
	}

	public boolean arrivedJunction() {
		return arrived_to_junction;
	}

	// END DISPLAY PURPOSES

	/**
	 * returns the junction at (x,y) if there is one
	 * 
	 * @param x
	 * @param y
	 * @return Junction
	 */
	@SuppressWarnings("unchecked")
	private Junction getJunction(int x, int y) {
		Context<Object> subcontext = ContextUtils.getContext(this)
				.getSubContext("eixample_subcontext");
		Network<Object> net = ((Network<Object>) subcontext
				.getProjection("eixample network"));
		Iterable<Object> nodes = net.getNodes();

		for (Object j : nodes) {
			try {
				switch (direction) {
				case EAST:
					if (carril == 1) {
						if (x + 2 == ((Junction) j).getX()
								&& y - 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else if (carril == 3) {
						if (x - 2 == ((Junction) j).getX()
								&& y + 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else {
						if (x == ((Junction) j).getX()
								&& y == ((Junction) j).getY()) {
							return (Junction) j;
						}
					}
					break;
				case WEST:
					if (carril == 3) {
						if (x - 2 == ((Junction) j).getX()
								&& y + 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else if (carril == 1) {
						if (x + 2 == ((Junction) j).getX()
								&& y - 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else {
						if (x == ((Junction) j).getX()
								&& y == ((Junction) j).getY()) {
							return (Junction) j;
						}
					}
					break;
				case NORTH:
					if (carril == 3) {
						if (x - 2 == ((Junction) j).getX()
								&& y + 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else if (carril == 1) {
						if (x + 2 == ((Junction) j).getX()
								&& y - 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else {
						if (x == ((Junction) j).getX()
								&& y == ((Junction) j).getY()) {
							return (Junction) j;
						}
					}
					break;
				case SOUTH:
					if (carril == 1) {
						if (x + 2 == ((Junction) j).getX()
								&& y - 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else if (carril == 3) {
						if (x - 2 == ((Junction) j).getX()
								&& y + 2 == ((Junction) j).getY()) {
							return (Junction) j;
						}
					} else {
						if (x == ((Junction) j).getX()
								&& y == ((Junction) j).getY()) {
							return (Junction) j;
						}
					}
					break;
				}

			} catch (ClassCastException e) {

			}
		}
		return null;
	}

	/**
	 * Returns wether there is a Junction at (x,y) or not
	 * 
	 * @param x
	 * @param y
	 * @return boolean
	 */
	private boolean isJunction(int x, int y) {
		if (getJunction(x, y) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recomended: start at 1, interval 1
	 * 
	 * Checks the state of the agent
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void checkState() {
		if (!disapear) {
			ticks_to_destiny++;
			int x, y;
			context = ContextUtils.getContext(this);
			grid = (Grid<Object>) context.getProjection("grid");
			space = (ContinuousSpace<Object>) context.getProjection("space");

			gp = grid.getLocation(this);
			posX = gp.getX();
			posY = gp.getY();
			x = posX;
			y = posY;

			if (isJunction(posX, posY)) {
				arrived_to_junction = true;
				// isCarAtJunction(posX, posY);
				planPath(dest);
			} else {
				arrived_to_junction = false;
			}

			if (!stop && !breaking) {
				seeFrontCar();
				seeTrafficLight();
			} else {
				if (stopped_by == CAR) {
					waitingFrontCar();
				} else if (stopped_by == TL) {
					waitingTrafficLight();
				}
			}
			add = 0;
			if (stop) { // check if curr. state is stopped
				add = 0;
				stopped_ticks++;
			}
			if (stopped_ticks > 150) {
				accelerate();
			}
			if (breaking) { // check if is breaking or has to
				add = 1;
				break_ticks++;
				if (break_ticks > 1) {
					breaking = false;
					stop = true;
					add = 0;
				}
			}
			if (isTurning) { // check if is turning
				add = 1;
				turning_ticks++;
			}
			if (acc) { // check if curr. state is accelerating
				if (acc_ticks < 2) { // check if we are at full speed or not
					add = 1;
					acc_ticks++;
				} else {
					add = 1;

				}
			}
			if (turning_ticks > 4 && isTurning) {
				isTurning = false;
				turning_ticks = 0;
			}
		}
	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recomended: start at 1, interval 1
	 * 
	 * Performs the action of going foward if needed
	 */
	@SuppressWarnings("unchecked")
	public void step() {
		if (!disapear) {
			context = ContextUtils.getContext(this);
			grid = (Grid<Object>) context.getProjection("grid");
			space = (ContinuousSpace<Object>) context.getProjection("space");
			int x = posX;
			int y = posY;
			switch (direction) {
			case NORTH:
				y += add;
				break;
			case SOUTH:
				y -= add;
				break;
			case WEST:
				x -= add;
				break;
			case EAST:
				x += add;
				break;
			}
			forward(x, y);
		}
	}

	/**
	 * Moves the agent to (x,y)
	 */
	public void forward(int x, int y) {
		grid.moveTo(this, x, y);
		space.moveTo(this, x, y);
	}

	/**
	 * Brakes the car
	 */
	private void brake() {
		stop = false;
		breaking = true;
		acc = false;
		break_ticks = 0;
		stopped_ticks = 0;
	}

	/**
	 * Accelerates the car if it was breaking or stopped
	 */
	public void accelerate() {
		stop = false;
		breaking = false;
		acc = true;
		acc_ticks = 0;
		stopped_by = 0;
		stopped_ticks = 0;
	}

	/**
	 * Registers the front car in order to avoid collisions
	 */
	@SuppressWarnings("unchecked")
	private void registerFrontCar() {
		grid = (Grid<Object>) context.getProjection("grid");
		Iterable<Object> objects = null;
		front_car = null;
		switch (direction) {
		case NORTH:
			objects = grid.getObjectsAt(posX, posY + 3);
			break;
		case SOUTH:
			objects = grid.getObjectsAt(posX, posY - 3);
			break;
		case WEST:
			objects = grid.getObjectsAt(posX - 3, posY);
			break;
		case EAST:
			objects = grid.getObjectsAt(posX + 3, posY);
			break;
		}
		if (objects != null) {
			for (Object j : objects) {
				try {
					front_car = (Car) j;
				} catch (ClassCastException e) {
				}
			}
		}
	}

	/**
	 * Checks the state of the front car
	 */
	public void seeFrontCar() {
		if (front_car != null) {
			if (front_car.isBreaking()) {
				brake();
				stopped_by = CAR;
			}
			if (front_car.isTurning()) {
				registerFrontCar();
			}
		} else {
			registerFrontCar();
		}
	}

	/**
	 * Checks the state of the traffic light
	 */
	@SuppressWarnings("unchecked")
	public void seeTrafficLight() {
		grid = ((Grid<Object>) context.getProjection("grid"));
		Iterable<Object> objects = null;
		switch (direction) {
		case NORTH:
			if (carril == 1) {
				objects = grid.getObjectsAt(posX + 2, posY + 3);
			} else if (carril == 3) {
				objects = grid.getObjectsAt(posX - 2, posY + 3);
			} else {
				objects = grid.getObjectsAt(posX, posY + 3);
			}
			break;
		case SOUTH:
			if (carril == 1) {
				objects = grid.getObjectsAt(posX + 2, posY - 3);
			} else if (carril == 3) {
				objects = grid.getObjectsAt(posX - 2, posY - 3);
			} else {
				objects = grid.getObjectsAt(posX, posY - 3);
			}
			break;
		case WEST:
			if (carril == 1) {
				objects = grid.getObjectsAt(posX - 3, posY - 2);
			} else if (carril == 3) {
				objects = grid.getObjectsAt(posX - 3, posY + 2);
			} else {
				objects = grid.getObjectsAt(posX - 3, posY);
			}
			break;
		case EAST:
			if (carril == 1) {
				objects = grid.getObjectsAt(posX + 3, posY - 2);
			} else if (carril == 3) {
				objects = grid.getObjectsAt(posX + 3, posY + 2);
			} else {
				objects = grid.getObjectsAt(posX + 3, posY);
			}
			break;
		}

		if (objects != null) {
			for (Object j : objects) {
				try {
					if (((TrafficLight) j).getState() == Color.red) {
						brake();
						stopped_by = TL;
						front_light = (TrafficLight) j;
					}
				} catch (ClassCastException e) {

				}
			}
		}
	}

	/**
	 * Checks the state of the front car if the agent has stopped to avoid a
	 * collision
	 */
	private void waitingFrontCar() {

		if (front_car != null) {
			switch (direction) {
			case NORTH:
				if (front_car.getPosY() < posY) {
					accelerate();
				}
				break;
			case SOUTH:
				if (front_car.getPosY() > posY) {
					accelerate();
				}
				break;
			case EAST:
				if (front_car.getPosX() > posX) {
					accelerate();
				}
				break;
			case WEST:
				if (front_car.getPosX() < posX) {
					accelerate();
				}
				break;
			}

			if (!front_car.isBreaking() || front_car.isAtDestination() == 0) {
				accelerate();
			}
			if (front_car.isTurning()) {
				accelerate();
				registerFrontCar();
			}
		} else {
			registerFrontCar();
			if (front_car == null) {
				accelerate();
			}
		}
	}

	/**
	 * Checks the state of the traffic light that caused the stopping of the
	 * agent
	 */
	private void waitingTrafficLight() {
		if (front_light.getState() == Color.green) {
			accelerate();
		}
	}

	/**
	 * Decides which direction has the agent to take
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void planPath(Junction dest) {
		// Junction origen = null;
		boolean trobat = false;
		if (origen == null) {
			origen = getJunction(posX, posY);
			if (origen != null) {
				trobat = true;
			} else {
				switch (direction) {
				case NORTH:
					origen = getJunction(posX, posY - 1);
					break;
				case SOUTH:
					origen = getJunction(posX, posY + 1);
					break;
				case EAST:
					origen = getJunction(posX - 1, posY);
					break;
				case WEST:
					origen = getJunction(posX + 1, posY);
					break;
				}
				if (origen != null) {
					trobat = true;
				}
			}
		} else {
			trobat = true;
		}
		Context<Object> subcontext = ContextUtils.getContext(this)
				.getSubContext("eixample_subcontext");
		Network<Object> net = (Network<Object>) subcontext
				.getProjection("eixample network");
		if (origen.equals(dest)) {
			disapear = true;
			breaking = false;
			stop = false;
			acc = false;
			context.remove(this);
			controller.addTime(ticks_to_destiny);
			disapear = true;

		} else {
			Iterable<Object> nodes = net.getNodes();
			if (trobat) {
				double min = Double.POSITIVE_INFINITY;
				Junction min_junction = null;
				Iterable<Object> succ = net.getSuccessors(origen);
				for (Object j : succ) {
					try {
						Junction tmp = (Junction) j;
						double tmp_dist = manhattanDist(tmp.getX(), tmp.getY(),
								dest.getX(), dest.getY());
						if (min > tmp_dist) {
							min = tmp_dist;
							min_junction = tmp;
						} else if (min == tmp_dist) {
							if (Math.random() < 0.5) {
								min = tmp_dist;
								min_junction = tmp;
							}
						}
					} catch (ClassCastException e) {

					}
				}
				if (origen.getX() > min_junction.getX()) {
					direction = WEST;
				} else if (origen.getX() < min_junction.getX()) {
					direction = EAST;
				} else if (origen.getY() < min_junction.getY()) {
					direction = NORTH;
				} else if (origen.getY() > min_junction.getY()) {
					direction = SOUTH;
				}
				isTurning = true;
				origen = null;
			}
		}
	}

	/**
	 * Manhattan distance
	 * 
	 * @param x_orig
	 * @param y_orig
	 * @param x_dest
	 * @param y_dest
	 * @return Double
	 */
	private double manhattanDist(int x_orig, int y_orig, int x_dest, int y_dest) {
		int incr_x = Math.abs(x_dest - x_orig);
		int incr_y = Math.abs(y_dest - y_orig);
		return (incr_x + incr_y) * 40.0;
	}

	// SETTERS AND GETTERS
	public void setOrigen(Junction origen) {
		this.origen = origen;
	}

	public void setX(int x) {
		posX = x;
	}

	public void setY(int y) {
		posY = y;
	}

	public void setDest(Junction dest, int dir) {
		this.dest = dest;
		planPath(dest);
	}

	public int getDirection() {
		return direction;
	}

	public int getOldDirection() {
		return direction;
	}

	public int getFrontCarX() {
		if (front_car != null) {
			return front_car.getPosX();
		}
		return 0;
	}

	public int getFrontCarY() {
		if (front_car != null) {
			return front_car.getPosY();
		}
		return 0;
	}

	// END SETTERS AND GETTERS

}
