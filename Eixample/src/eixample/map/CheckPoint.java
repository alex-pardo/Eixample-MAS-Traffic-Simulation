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

package eixample.map;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import eixample.agents.vehicles.Car;
import eixample.agents.vehicles.IVehicle;

/**
 * Checkpoint to count the number of the cars between into two Junctions
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class CheckPoint {
	private Junction initJunction;
	private Junction finalJunction;
	private Integer inCars = 0;
	private Integer outCars = 0;
	private Context context;
	private int direction;
	private ArrayList<IVehicle> vehicles_at_street;

	/**
	 * Constructor
	 * @param initJunction Initial Junction
	 * @param finalJunction Final Junction
	 * @param context Context of the simulation
	 * @param direction One direction (north, south, west, east)
	 */
	public CheckPoint(Junction initJunction, Junction finalJunction,
			Context context, int direction) {
		this.initJunction = initJunction;
		this.finalJunction = finalJunction;
		this.context = context;
		vehicles_at_street = new ArrayList<IVehicle>();
		this.direction = direction;
	}

	/**
	 * Empty constructor
	 */
	public CheckPoint() {

	}

	/**
	 * Method to get the direction of this CheckPoint
	 * @return Direction
	 */
	public int getDirection() {
		return direction;
	}

	// Check each step the system how many has in cars
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void checkInput() {

		Grid<Object> grid = (Grid<Object>) context.getProjection("grid");
		Iterable<Object> objects = null;
		switch (direction) {
		case Car.NORTH:
			objects = grid.getObjectsAt(initJunction.getX(),
					initJunction.getY() + 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() + 2,
					initJunction.getY() + 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() - 2,
					initJunction.getY() + 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {
					System.out.println(e.getMessage());
				}
			}
			break;
		case Car.SOUTH:
			objects = grid.getObjectsAt(initJunction.getX(),
					initJunction.getY() - 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {
				}
			}
			objects = grid.getObjectsAt(initJunction.getX() + 2,
					initJunction.getY() - 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() - 2,
					initJunction.getY() - 6);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		case Car.EAST:
			objects = grid.getObjectsAt(initJunction.getX() + 6,
					initJunction.getY());
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() + 6,
					initJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() + 6,
					initJunction.getY() + 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		case Car.WEST:
			objects = grid.getObjectsAt(initJunction.getX() - 6,
					initJunction.getY());
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() - 6,
					initJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(initJunction.getX() - 6,
					initJunction.getY() + 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getDirection() == direction) {
						synchronized (inCars) {
							inCars++;
						}
					}
				} catch (ClassCastException e) {
				}
			}
			break;
		}
	}

	// Check each step the system how many has out cars
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void checkOutput() {
		Grid<Object> grid = (Grid<Object>) context.getProjection("grid");
		Iterable<Object> objects = null;
		switch (direction) {
		case Car.NORTH:
			objects = grid.getObjectsAt(finalJunction.getX(),
					finalJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() + 2,
					finalJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() - 2,
					finalJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		case Car.SOUTH:
			objects = grid.getObjectsAt(finalJunction.getX(),
					finalJunction.getY() + 3);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() + 2,
					finalJunction.getY() + 3);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() - 2,
					finalJunction.getY() + 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		case Car.EAST:
			objects = grid.getObjectsAt(finalJunction.getX() - 2,
					finalJunction.getY());
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() - 2,
					finalJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() - 2,
					finalJunction.getY() + 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		case Car.WEST:
			objects = grid.getObjectsAt(finalJunction.getX() + 2,
					finalJunction.getY());
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() + 2,
					finalJunction.getY() - 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			objects = grid.getObjectsAt(finalJunction.getX() + 2,
					finalJunction.getY() + 2);
			for (Object o : objects) {
				try {
					IVehicle c = (IVehicle) o;
					if (c.getOldDirection() == direction && !c.isBreaking()) {
						synchronized (outCars) {
							outCars++;
						}
					}
				} catch (ClassCastException e) {

				}
			}
			break;
		}
	}

	/**
	 * Get the initial junction of this checkpoint
	 * @return Initial junction
	 */
	public Junction getInitJunction() {
		return initJunction;
	}

	/**
	 * Set the initial junction of this checkpoint
	 * @param initJunction Initial Junction
	 */
	public void setInitJunction(Junction initJunction) {
		this.initJunction = initJunction;
	}

	/**
	 * Get the final junction of this checkpoint
	 * @return Final junction
	 */
	public Junction getFinalJunction() {
		return finalJunction;
	}

	/**
	 * Set the final junction of this checkpoint
	 * @param finalJunction Initial Junction
	 */
	public void setFinalJunction(Junction finalJunction) {
		this.finalJunction = finalJunction;
	}

	/**
	 * Method to return the number of the car in checkpoint
	 * @return The number of the cars
	 */
	public int getNumCars() {
		if (outCars > inCars) {
			inCars = 0;
			outCars = 0;
		}
		return inCars - outCars;
	}

	/**
	 * Method toString to print the this object
	 */
	public String toString() {
		String dir = "";
		switch (direction) {
		case Car.NORTH:
			dir = "north";
			break;
		case Car.SOUTH:
			dir = "south";
			break;
		case Car.EAST:
			dir = "east";
			break;
		case Car.WEST:
			dir = "west";
			break;
		}
		return "Init:" + initJunction + " Final:" + finalJunction
				+ " Num cars:" + getNumCars() + "Direction:" + dir;
	}
}
