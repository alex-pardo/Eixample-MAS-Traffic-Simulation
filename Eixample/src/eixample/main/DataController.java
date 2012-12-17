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

package eixample.main;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import eixample.agents.trafficLights.TrafficLight;
import eixample.map.CheckPoint;
import eixample.map.Junction;

/**
 * Data Controller for display purposes
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class DataController {

	private int number_of_cars = 0;
	private Integer total_cars = 0;
	private int total_time_mean = 0;
	private int initial_cars;
	private Context context;
	private ArrayList<CheckPoint> checkPointList;
	private ArrayList<TrafficLight> trafficLightList;
	public int id = 0;

	public DataController() {

	}

	public DataController(int total_cars, Context context) {
		this.total_cars = total_cars;
		initial_cars = total_cars;
		this.number_of_cars = 0;
		this.context = context;
		this.trafficLightList = new ArrayList<TrafficLight>();
		id = 0;
	}

	public void setCheckPointList(ArrayList<CheckPoint> cp) {
		this.checkPointList = cp;
	}

	public void setId(int x) {
		id += x;
	}

	public void addTrafficLight(TrafficLight t) {
		synchronized (trafficLightList) {
			trafficLightList.add(t);
		}
	}

	public Object[] getTrafficLights() {
		return trafficLightList.toArray();
	}

	/**
	 * Each car should add its arrival time when at destiny
	 * The method will stop the simulation when there where no more cars 
	 * @param ticks
	 */
	public synchronized void addTime(int ticks) {
		int tmp = ((total_time_mean * number_of_cars) + ticks);
		number_of_cars++;
		total_time_mean = tmp / number_of_cars;
		total_cars--;
		if (total_cars == 0) {
			Schedule schedule = (Schedule) RunEnvironment.getInstance()
					.getCurrentSchedule();
			schedule.executeEndActions();
			RunEnvironment.getInstance().endRun();

			double tick = RunEnvironment.getInstance().getCurrentSchedule()
					.getTickCount();
			System.out.println("------------------");
			System.out.println("     RESTULTS     ");
			System.out.println("------------------");
			System.out.println("Total ticks: " + tick);
			System.out.println("Average trip time: " + total_time_mean);
		}
	}


	public int getCars() {
		return total_cars;
	}

	public int getMean() {
		return total_time_mean;
	}

	public void setNumCarCheckPoint(Junction origen, Junction desti, int numCars) {
		boolean trobat = false;
		int i = 0;
		System.out.println("Origen " + origen + "Desti " + desti);
		while (!trobat && i < checkPointList.size()) {
			CheckPoint cp = checkPointList.get(i);
			if (cp.getFinalJunction().equals(desti)
					&& cp.getInitJunction().equals(origen)) {
				trobat = true;
			}
			i++;
		}
	}

	public int getNumCarsCheckPoint(Junction origen, Junction desti) {
		boolean trobat = false;
		int i = 0;
		while (!trobat && i < checkPointList.size()) {
			CheckPoint cp = checkPointList.get(i);
			if (cp.getFinalJunction() == desti
					&& cp.getInitJunction() == origen) {
				trobat = true;
			}
			i++;
		}
		return checkPointList.get(i - 1).getNumCars();
	}

	public void addCar() {
		synchronized (total_cars) {
			total_cars++;
		}
	}
}
