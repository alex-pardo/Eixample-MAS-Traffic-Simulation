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
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.collections.IndexedIterable;
import eixample.agents.vehicles.Car;
import eixample.agents.vehicles.IVehicle;
import eixample.agents.vehicles.NoDestinyCar;
import eixample.map.CheckPoint;
import eixample.map.Junction;

/**
 * Main class
 * Adds cars to the simulations
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class Main {
	private Context<Object> context;
	private Context<Object> subcontext;
	private ArrayList<Junction> listExternalJunction;
	private ArrayList<Junction> listInternalJunction;
	private int maxCars = 2000;
	private int numCars = 0;
	private int maxDummyCars = 0;
	private int numDummyCars = 0;
	private DataController controller;
	private boolean internal;

	/**
	 * Constructor
	 * @param context
	 * @param subcontext
	 * @param checkPointList
	 */
	public Main(Context<Object> context, Context<Object> subcontext,
			ArrayList<CheckPoint> checkPointList) {
		this.context = context;
		this.subcontext = subcontext;
		createListJunction();
		Parameters params = RunEnvironment.getInstance().getParameters();

		maxCars = (Integer) params.getValue("num_cars");
		maxDummyCars = (Integer) params.getValue("num_dummy_cars");
		String in_out = (String) params.getValue("kind");
		internal = in_out.equals("in");

		IndexedIterable<Object> iterator = context
				.getObjects(new DataController().getClass());
		controller = (DataController) iterator.get(0);
	}

	/**
	 * Adds a car each step until the number of max cars i reached
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		if (numCars < maxCars) {
			numCars++;
			addCar((int) Math.round(Math.random() * 2) + 1);
			controller.addCar();
		}
		if (numDummyCars < maxDummyCars) {
			numDummyCars++;
			addDummyCar((int) Math.round(Math.random() * 2) + 1);
			controller.addCar();
		}
	}


	/**
	 * Adds a car to the simulation
	 * @param carril
	 */
	@SuppressWarnings("unchecked")
	public void addCar(int carril) {
		Network<Object> net = ((Network<Object>) subcontext
				.getProjection("eixample network"));
		int x, y;
		Junction j = null;
		if (internal) {
			j = startIntJunction();
		} else {
			j = startExtJunction();
		}
		x = j.getX();
		y = j.getY();
		if (carril == 1) {
			x = x - 2;
			y = y + 2;
		} else if (carril == 3) {
			x = x + 2;
			y = y - 2;
		}
		Car c = new Car(carril, x, y, net, controller);
		addVehicle(c, x, y);
		Schedule schedule = (Schedule) RunEnvironment.getInstance()
				.getCurrentSchedule();
		double tick = RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();
		ScheduleParameters params = ScheduleParameters.createRepeating(tick, 1);
		c.setOrigen(j);
		c.setX(x);
		c.setY(y);
		c.setDest(finalJunction(j), Car.EAST); // Add junction dest
		schedule.schedule(params, c, "checkState");
		schedule.schedule(params, c, "step");

	}

	/**
	 * Adds a dummy car to the simulation
	 * @param carril
	 */
	@SuppressWarnings("unchecked")
	public void addDummyCar(int carril) {
		Network<Object> net = ((Network<Object>) subcontext
				.getProjection("eixample network"));
		int x, y;
		Junction j = startExtJunction();
		x = j.getX();
		y = j.getY();
		if (carril == 1) {
			x = x - 2;
			y = y + 2;
		} else if (carril == 3) {
			x = x + 2;
			y = y - 2;
		}
		NoDestinyCar c = new NoDestinyCar(carril, x, y, net);
		addVehicle(c, x, y);
		Schedule schedule = (Schedule) RunEnvironment.getInstance()
				.getCurrentSchedule();
		double tick = RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();
		ScheduleParameters params = ScheduleParameters.createRepeating(tick, 1);
		c.setOrigen(j);
		c.setX(x);
		c.setY(y);
		c.setDest(finalJunction(j), Car.EAST); // Add junction dest
		schedule.schedule(params, c, "checkState");
		schedule.schedule(params, c, "step");

	}

	/**
	 * Creates two lists with internal and external junctions
	 */
	@SuppressWarnings("unchecked")
	private void createListJunction() {
		// Get parameters
		Parameters params = RunEnvironment.getInstance().getParameters();
		int h_streets = (Integer) params.getValue("h_streets");

		int max_y_junction = (46 * (h_streets - 1)) + 2, max_x_junction = (46 * (h_streets - 1)) + 2, min_y_junction = 2, min_x_junction = 2;
		Junction junction;
		Network<Object> net = ((Network<Object>) subcontext
				.getProjection("eixample network"));
		Iterable<Object> nodes = net.getNodes();
		listExternalJunction = new ArrayList<Junction>();
		listInternalJunction = new ArrayList<Junction>();
		for (Object j : nodes) {
			junction = (Junction) j;
			if ((junction.getX() == min_x_junction)
					|| (junction.getY() == min_y_junction)
					|| (junction.getX() == max_x_junction)
					|| (junction.getY() == max_y_junction)) {
				listExternalJunction.add((Junction) j);
			} else {
				listInternalJunction.add((Junction) j);
			}
		}
	}

	/**
	 * picks a random junction from the external list
	 * @return
	 */
	private Junction startExtJunction() {
		Random random = new Random();
		int x = random.nextInt(listExternalJunction.size());
		return listExternalJunction.get(x);
	}

	/**
	 * picks a random junction from the internal list
	 * @return
	 */
	private Junction startIntJunction() {
		Random random = new Random();
		int x = random.nextInt(listInternalJunction.size());
		return listInternalJunction.get(x);
	}

	/**
	 * Obtains a final junction
	 * @param junction
	 * @return
	 */
	private Junction finalJunction(Junction junction) {
		Junction final_junction = null;
		int x, y;
		boolean corner = false;
		x = junction.getX();
		y = junction.getY();
		ArrayList<Junction> listFinalJunction = new ArrayList<Junction>();
		// Mirem si es una cantonada o no
		if ((x == 2 || x == 140) && (y == 2 || y == 140)) {
			corner = true;
		}

		for (Junction j : listExternalJunction) {
			// Cas que l'inici es una cantonada
			if (corner) {
				if ((j.getX() != x) && (j.getY() != y)) {
					listFinalJunction.add(j);
				}
				// Cas que estas un junction intermitja
			} else {
				if (x != j.getX()) {
					if (y != j.getY()) {
						listFinalJunction.add(j);
					}
				} else {
					if (y != j.getY()) {
						listFinalJunction.add(j);
					}
				}
			}
		}
		final_junction = listFinalJunction
				.get((int) (Math.random() * listFinalJunction.size()));
		return final_junction;
	}

	/**
	 * Adds a car
	 * @param vehicle
	 * @param x
	 * @param y
	 */
	@SuppressWarnings("unchecked")
	public void addVehicle(IVehicle vehicle, int x, int y) {
		// int x, y;
		Grid<Object> grid = (Grid<Object>) context.getProjection("grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context
				.getProjection("space");
		// TODO Afegim el vehicle al context
		context.add(vehicle);
		space.moveTo(vehicle, x, y);
		grid.moveTo(vehicle, x, y);

	}
}
