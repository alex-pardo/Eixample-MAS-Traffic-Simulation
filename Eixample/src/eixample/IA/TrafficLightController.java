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

package eixample.IA;

import java.awt.Color;

import eixample.agents.trafficLights.TrafficLight;
import eixample.utils.PriorityTrafficLightQueue;

/**
 * Traffic light controller (for centralized IA)
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class TrafficLightController {

	PriorityTrafficLightQueue trafficLights = null;

	/**
	 * Constructor
	 */
	public TrafficLightController() {
		trafficLights = new PriorityTrafficLightQueue();
	}

	/**
	 * Adds a traffic light to control
	 * @param t
	 */
	public void addTrafficLight(TrafficLight t) {
		if (t.getState().equals(Color.red)) {
			trafficLights.put(t);
		}

	}

	/**
	 * Schedule method, has to be scheduled when the object is created.
	 * Recommended: start at 1, interval 1
	 * 
	 * Checks the priority queue and decides which traffic light could be set to green 
	 */
	public void centralIAController() {
		int size = trafficLights.getSize();
		for (int i = 0; i < size / 2; i++) {
			if (trafficLights.get(i).getAdjacent() == null) {
				TrafficLight tmp = trafficLights.get(i);
				tmp.allowStateChange();
				trafficLights.remove(tmp);
			} else if (trafficLights.get(i).getAdjacent().getGreenTicks() == 0) {
				TrafficLight tmp = trafficLights.get(i);
				tmp.allowStateChange();
				trafficLights.remove(tmp);
			} else {
				if (trafficLights.get(i).getWaitingCar() > 200) {
					if (trafficLights.get(i).getAdjacent().getWaitingCar() < trafficLights
							.get(i).getWaitingCar()) {
						TrafficLight tmp = trafficLights.get(i);
						tmp.getAdjacent().setRedState();
						tmp.allowStateChange();
						trafficLights.remove(tmp);
					}
				}
			}
		}	
	}
}
