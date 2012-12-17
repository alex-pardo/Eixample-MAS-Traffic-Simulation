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

package eixample.agents.trafficLights;

import java.awt.Color;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import eixample.IA.TrafficLightController;
import eixample.agents.vehicles.Car;
import eixample.map.CheckPoint;

/**
 * Traffic Light Agent
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class TrafficLight {
	private Color state;
	private int greenTicks;
	public int redTicks;
	public int waitChangeTicks;
	private int waiting_cars = 0;
	private boolean first = true;

	private int x;
	private int y;

	// alpha*waiting_cars + (1-alpha)*waiting_ticks
	public double alpha = 0.5;

	private TrafficLight adjacent = null;

	private CheckPoint street = null;

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param alpha
	 */
	public TrafficLight(int x, int y, double alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}

	/**
	 * returns x position
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * returns y position
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * returns alpha value
	 * 
	 * @return
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * returns the color
	 * 
	 * @return {@link Color}
	 */
	public Color getState() {
		return state;
	}

	/**
	 * sets the color of the traffic light
	 * 
	 * @param c
	 *            {@link Color}
	 */
	public void setState(Color c) {
		state = c;
	}

	/**
	 * returns the number of waiting cars
	 * 
	 * @return
	 */
	public int getWaitingCar() {
		return waiting_cars;
	}

	/**
	 * sets the adjacent traffic light of the agent
	 * 
	 * @param t
	 */
	public void setAdjacent(TrafficLight t) {
		adjacent = t;
	}

	/**
	 * returns the adjacent traffic light of the agent
	 * 
	 * @return
	 */
	public TrafficLight getAdjacent() {
		return adjacent;
	}

	// DISPLAY PURPOSES
	public int getAdjacentX() {
		if (adjacent == null) {
			return -1;
		}
		return adjacent.getX();
	}

	public int getAdjacentY() {
		if (adjacent == null) {
			return -1;
		}
		return adjacent.getY();
	}

	public String getStreet() {
		return street.toString();
	}

	public int getRedTicks() {
		return redTicks;
	}

	public int getGreenTicks() {
		return greenTicks;
	}

	// END DISPLAY PURPOSES

	/**
	 * Sets the street of the agent
	 * 
	 * @param street
	 */
	public void setStreet(CheckPoint street) {
		this.street = street;
		if (street.getDirection() == Car.NORTH
				|| street.getDirection() == Car.SOUTH) {
			state = Color.red;
			greenTicks = 130;
		} else {
			state = Color.green;
			redTicks = 130;
			waitChangeTicks = 130;
		}
	}

	/**
	 * Cahnges the agent to red
	 */
	public void setRedState() {
		greenTicks = 0;
	}

	/**
	 * allows the agent to change the state
	 */
	public void allowStateChange() {
		waitChangeTicks = 10;
		greenTicks = waiting_cars / 2;
		if (greenTicks < 40) {
			greenTicks = 40;
		}
		waiting_cars = 0;
	}

	/**
	 * sets teh agent to green
	 */
	private void setGreenState() {
		state = Color.green;
	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recomended: start at 1, interval 1
	 * 
	 * No IA
	 */
	public void withoutIA() {

		if (redTicks < 120) {
			state = Color.red;
			redTicks++;
			waitChangeTicks = 0;
			greenTicks = 0;
		} else {
			if (greenTicks >= 120) {
				redTicks = 0;
			}
			if (waitChangeTicks < 20) {
				waitChangeTicks++;
			} else {
				state = Color.green;
				greenTicks++;
			}
		}

	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recomended: start at 1, interval 1
	 * 
	 * NOT IMPLEMENTED YET, uses No IA
	 */
	public void distributedIA() {
		withoutIA();
	}

	private void initialize() {
		Random random = new Random();
		int num = random.nextInt(2);
		if (num == 0) {
			state = Color.green;
			greenTicks = (int) Math.random() * 50 + 5;
		} else {
			state = Color.red;
		}
		redTicks = 0;
		waitChangeTicks = 0;
	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recomended: start at 1, interval 1
	 * 
	 * Centralized IA
	 */
	@SuppressWarnings("unchecked")
	public void centralizeIA() {
		if (first) {
			initialize();
			first = false;
		}
		if (greenTicks <= 0) {
			state = Color.red;
			Context<Object> context = ContextUtils.getContext(this);
			IndexedIterable<Object> iterator = context
					.getObjects(new TrafficLightController().getClass());
			TrafficLightController controller = (TrafficLightController) iterator
					.get(0);
			waiting_cars = street.getNumCars(); // Save in traffic the number of
												// the car of this street
			controller.addTrafficLight(this);
			redTicks++;
		} else {
			if (waitChangeTicks > 0) {
				waitChangeTicks--;
				if (waitChangeTicks == 0) {
					setGreenState();
				}
			} else {
				greenTicks--;
			}

		}
	}

}
