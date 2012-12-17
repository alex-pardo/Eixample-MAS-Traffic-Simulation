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
import eixample.map.Junction;

/**
 * Car agent without destiny
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 *         SEE CAR FOR DOCUMENTATION
 * @see {@link Car}
 * 
 */
public class NoDestinyCar implements IVehicle {
	private Context<Object> context;
	private Grid<Object> grid;
	private ContinuousSpace<Object> space;
	private GridPoint gp;
	private int posX;
	private int posY;
	private Junction origen = null;
	private int direction;
	private int old_direction;
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

	private IVehicle front_car = null;
	private TrafficLight front_light = null;

	private Junction dest = null;

	public NoDestinyCar(int carril, int x_dest, int y_dest, Network<Object> net) {
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
		old_direction = EAST;
	}

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

	public boolean isJunction() {
		if (getJunction(posX, posY) != null) {
			return true;
		}
		return false;
	}

	private boolean isJunction(int x, int y) {
		if (getJunction(x, y) != null) {
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

	@SuppressWarnings({ "unused", "unchecked" })
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
				planPath(dest);
			} else {
				arrived_to_junction = false;
			}

			if (!stop && !breaking) {
				seeTrafficLight();
				seeFrontCar();
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
			if (stopped_ticks > 50) {
				accelerate();
			}
			if (breaking) { // check if is breaking or has to
				add = 1;
				break_ticks++;
				if (break_ticks > 1) {
					breaking = false;
					stop = true;
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

	public void forward(int x, int y) {
		grid.moveTo(this, x, y);
		space.moveTo(this, x, y);
	}

	private void brake() {
		stop = false;
		breaking = true;
		acc = false;
		break_ticks = 0;
		stopped_ticks = 0;
	}

	public void accelerate() {
		stop = false;
		breaking = false;
		acc = true;
		acc_ticks = 0;
		stopped_by = 0;
		stopped_ticks = 0;
	}

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
					front_car = (IVehicle) j;
				} catch (ClassCastException e) {

				}
			}
		}
	}

	public void seeFrontCar() {
		if (front_car != null) {
			if (front_car.isBreaking()) {
				switch (direction) {
				case NORTH:
					if (this.posX == front_car.getPosX()
							&& (front_car.getPosY() - this.posY) <= 3) {
						brake();
						stopped_by = CAR;
					}
					break;
				case SOUTH:
					if (this.posX == front_car.getPosX()
							&& (this.posY - front_car.getPosY()) <= 3) {
						brake();
						stopped_by = CAR;
					}
					break;
				case WEST:
					if ((this.posX - front_car.getPosX()) <= 3
							&& this.posY == front_car.getPosY()) {
						brake();
						stopped_by = CAR;
					}
					break;
				case EAST:
					if ((front_car.getPosX() - this.posX) <= 3
							&& this.posY == front_car.getPosY()) {
						brake();
						stopped_by = CAR;
					}
					break;
				}
			}
			if (front_car.isTurning()) {
				registerFrontCar();
			}
		} else {
			registerFrontCar();
		}
	}

	@SuppressWarnings("unchecked")
	public void seeTrafficLight() {
		grid = (Grid<Object>) context.getProjection("grid");
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

	private void waitingFrontCar() {
		if (front_car != null) {
			if (!front_car.isBreaking() || front_car.isAtDestination() == 0) {
				accelerate();
			}
			if (front_car.isTurning()) {
				accelerate();
				registerFrontCar();
			}
		} else {
			registerFrontCar();
		}
	}

	private void waitingTrafficLight() {
		if (front_light.getState() == Color.green) {
			accelerate();
		}
	}

	public int getStoppedBy() {
		return stopped_by;
	}

	public boolean isStop() {
		return stop;
	}

	public boolean isBreaking() {
		return (breaking || stop);
	}

	public boolean isTurning() {
		return isTurning;
	}

	public int getCarril() {
		return carril;
	}

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
			Iterable<Object> nodes = net.getNodes();
			for (Object j : nodes) {
				try {
					if (Math.random() < 0.7) {
						dest = (Junction) j;
					}
				} catch (ClassCastException e) {
				}
			}
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
				old_direction = direction;
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

	private double manhattanDist(int x_orig, int y_orig, int x_dest, int y_dest) {
		int incr_x = Math.abs(x_dest - x_orig);
		int incr_y = Math.abs(y_dest - y_orig);
		return (incr_x + incr_y) * 40.0;
	}

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

	@Override
	public int getOldDirection() {
		return old_direction;
	}

	@Override
	public boolean arrivedJunction() {
		return arrived_to_junction;
	}
}
