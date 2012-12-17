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

package eixample.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eixample.agents.trafficLights.TrafficLight;

/**
 * Priority traffic light queue structure
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class PriorityTrafficLightQueue {

	private ArrayList<TrafficLight> queue;

	/**
	 * Method to compare to trafficlights
	 */
	private Comparator comp = new Comparator<TrafficLight>() {

		@Override
		public int compare(TrafficLight o1, TrafficLight o2) {
			double alpha = o1.getAlpha();
			if ((alpha * (o1.getWaitingCar() + (1 - alpha) * o1.redTicks)) > (alpha * (o2
					.getWaitingCar() + (1 - alpha) * o2.redTicks))) {
				return -1;
			} else if ((alpha * (o1.getWaitingCar() + (1 - alpha) * o1.redTicks)) < (alpha * (o2
					.getWaitingCar() + (1 - alpha) * o2.redTicks))) {
				return 1;
			}
			return 0;
		}
	};

	/**
	 * Constructor
	 */
	public PriorityTrafficLightQueue() {
		queue = new ArrayList<TrafficLight>();
	}

	/**
	 * Synchronized method to put new traffic light to structure
	 * @param t Trafficlight
	 */
	public synchronized void put(TrafficLight t) {
		synchronized (queue) {
			if (!queue.contains(t)) {
				queue.add(t);
				sort();
			}
		}
	}

	/**
	 * Method to sort structure
	 */
	private void sort() {
		Collections.sort(queue, comp);
	}

	/**
	 * Method to get one traffic light in the position in the structure of this index
	 * @param index
	 * @return Traffic light
	 */
	public TrafficLight get(int index) {
		return queue.get(index);
	}

	/**
	 * Method to remove traffic light
	 * @param t Traffic light to remove
	 */
	public void remove(TrafficLight t) {
		synchronized (queue) {
			queue.remove(t);
			sort();
		}
	}

	/**
	 * Get the size of the structure
	 * @return
	 */
	public int getSize() {
		return queue.size();
	}

}
