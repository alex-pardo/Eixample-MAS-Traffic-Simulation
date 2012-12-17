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
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.collections.IndexedIterable;
import eixample.IA.TrafficLightController;
import eixample.agents.trafficLights.TrafficLight;
import eixample.agents.vehicles.Car;
import eixample.main.DataController;

/**
 * Map of the simulation. Build all elements that you see in the repast simulation
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class Map {
	private Context<Object> context;
	private Context<Object> subcontext;
	private Grid<Object> grid;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid_subcontext;
	private ContinuousSpace<Object> space_subcontext;
	private ArrayList<CheckPoint> checkPointsList;

	private int h_streets; //Number of horizontal streets
	private int v_streets; //Number of vertical streets

	/**
	 * Constructor
	 * @param context Context of the simulation
	 * @param subcontext SubContext of the simulation
	 */
	public Map(Context<Object> context, Context<Object> subcontext) {
		this.context = context;
		this.subcontext = subcontext;
		grid = (Grid<Object>) context.getProjection("grid");
		space = (ContinuousSpace<Object>) context.getProjection("space");
		grid_subcontext = (Grid<Object>) subcontext
				.getProjection("grid_subcontext");
		space_subcontext = (ContinuousSpace<Object>) subcontext
				.getProjection("space_subcontext");

		// Get parameters of the simulation
		Parameters params = RunEnvironment.getInstance().getParameters();
		h_streets = (Integer) params.getValue("h_streets");
		v_streets = (Integer) params.getValue("v_streets");
		checkPointsList = new ArrayList<CheckPoint>();
	}

	/**
	 * Method to build map of the simulation
	 */
	public void build() {
		buildNetwork();
		buildStreets();
		buildTrafficLights();
		setTrafficLightAdjacencies();
	}

	/**
	 * Method to build the junction network
	 */
	private void buildNetwork() {
		Network<Object> net = ((Network<Object>) subcontext
				.getProjection("eixample network"));
		ArrayList<Junction> nodes = new ArrayList<Junction>();
		CheckPoint checkPoint;
		Junction j;
		int x = 2;
		int y = 2;
		// build network rows
		int last_index = -1;
		for (int iter = 0; iter < h_streets; iter += 2) {
			j = new Junction(last_index + 1, space_subcontext, grid_subcontext);
			subcontext.add(j);
			j.move(x, y);
			nodes.add(j);
			last_index++;
			// System.out.println((columns%40)/6);
			for (int i = 1; i < v_streets; i++) {
				x += 46;
				j = new Junction(last_index + 1, space_subcontext,
						grid_subcontext);
				subcontext.add(j);
				j.move(x, y);
				nodes.add(j);
				net.addEdge(nodes.get(last_index), j);
				// Create a new check point and add to the list
				checkPoint = new CheckPoint(nodes.get(last_index), j, context,
						Car.EAST);
				checkPointsList.add(checkPoint);

				// System.out.println("adding edge("+last_index+","+(last_index+1)+")");
				last_index++;
			}
			if (h_streets % 2 != 0 && iter == h_streets - 1) {
				continue;
			}
			x = 2;
			y += 46;
			j = new Junction(last_index + 1, space_subcontext, grid_subcontext);
			last_index++;
			subcontext.add(j);
			j.move(x, y);
			nodes.add(j);
			// last_index++;
			for (int i = 1; i < v_streets; i++) {
				x += 46;
				j = new Junction(last_index + 1, space_subcontext,
						grid_subcontext);
				subcontext.add(j);
				j.move(x, y);
				nodes.add(j);
				net.addEdge(j, nodes.get(last_index));
				// Create a new check point and add to the list
				checkPoint = new CheckPoint(j, nodes.get(last_index), context,
						Car.WEST);
				checkPointsList.add(checkPoint);
				last_index++;
			}
			y += 46;
			x = 2;
		}

		// build network columns
		int dif = v_streets; // difference between rows

		for (int c = 0; c < v_streets; c++) {
			for (int r = 0; r < h_streets - 1; r++) {
				if (c % 2 == 1) {
					net.addEdge(nodes.get(c + (r * dif)),
							nodes.get(c + ((r + 1) * dif)));
					// Create a new check point and add to the list
					checkPoint = new CheckPoint(nodes.get(c + (r * dif)),
							nodes.get(c + ((r + 1) * dif)), context, Car.NORTH);
					checkPointsList.add(checkPoint);
				} else {
					net.addEdge(nodes.get(c + ((r + 1) * dif)),
							nodes.get(c + (r * dif)));
					// Create a new check point and add to the list
					checkPoint = new CheckPoint(nodes.get(c + ((r + 1) * dif)),
							nodes.get(c + (r * dif)), context, Car.SOUTH);
					checkPointsList.add(checkPoint);
				}
			}
		}

		for (CheckPoint cp : checkPointsList) {
			context.add(cp);
		}
	}

	public void buildStreets() {
		int x = 0;
		int y = 0;
		int rows = (40 * (h_streets - 1)) + (h_streets * 6);
		int columns = (40 * (v_streets - 1)) + (v_streets * 6);
		// build row streets
		while (y < rows) {
			for (int i = 0; i < 3; i++) {
				for (int pos = 0; pos < columns; pos++) {
					HRoad road = new HRoad();
					context.add(road);
					grid.moveTo(road, x, y);
					space.moveTo(road, x, y);
					x++;
				}
				y += 2;
			}
			y += 40;
		}
		y = 0;
		x = 0;

		// build columns streets
		while (x < columns) {
			for (int i = 0; i < 3; i++) {
				for (int pos = 0; pos < rows; pos++) {
					VRoad road = new VRoad();
					context.add(road);
					grid.moveTo(road, x, y);
					space.moveTo(road, x, y);
					y++;
				}
				x += 2;
			}
			x += 40;
		}

	}

	/**
	 * Method to build and put in the map the traffic lights
	 */
	public void buildTrafficLights() {
		IndexedIterable<Object> iterator = context
				.getObjects(new DataController().getClass());
		DataController dataController = (DataController) iterator.get(0);

		int rows = (40 * (h_streets - 1)) + (h_streets * 6);
		int columns = (40 * (v_streets - 1)) + (v_streets * 6);
		Schedule schedule = (Schedule) RunEnvironment.getInstance()
				.getCurrentSchedule();
		ScheduleParameters scheduleParams = ScheduleParameters.createRepeating(
				0, 1);

		// Get parameters of the simulation
		Parameters params = RunEnvironment.getInstance().getParameters();
		String ia = (String) params.getValue("ia");
		int distributedIA = 2;
		if (ia.equals("distributed")) {
			distributedIA = 0;
		} else if (ia.equals("centralized")) {
			distributedIA = 1;
		} else if (ia.equals("without")) {
			distributedIA = 2;
		}

		double alpha = (Double) params.getValue("alpha");
		if (alpha > 1) {
			alpha = 1;
		}

		// south
		for (int x = 2; x < columns; x += 92) {
			for (int y = 5; y < rows - 1; y += 46) {
				TrafficLight trafficLight = new TrafficLight(x, y, alpha);
				dataController.addTrafficLight(trafficLight);
				context.add(trafficLight);
				grid.moveTo(trafficLight, x, y);
				space.moveTo(trafficLight, x, y);
				if (distributedIA == 0) {
					schedule.schedule(scheduleParams, trafficLight,
							"distributedIA");
				} else if (distributedIA == 1) {
					schedule.schedule(scheduleParams, trafficLight,
							"centralizeIA");
				} else {
					schedule.schedule(scheduleParams, trafficLight, "withoutIA");
				}
				int min_dist = Integer.MAX_VALUE;
				for (CheckPoint cp : checkPointsList) {
					int dist = Math.abs(cp.getFinalJunction().getX() - x)
							+ Math.abs(cp.getFinalJunction().getY() - y);
					if (dist < min_dist && cp.getDirection() == Car.SOUTH) {
						trafficLight.setStreet(cp);
						min_dist = dist;
					}
				}
			}
		}
		// north
		for (int x = 48; x < columns; x += 92) {
			for (int y = 45; y < rows; y += 46) {
				TrafficLight trafficLight = new TrafficLight(x, y, alpha);
				dataController.addTrafficLight(trafficLight);
				context.add(trafficLight);
				grid.moveTo(trafficLight, x, y);
				space.moveTo(trafficLight, x, y);
				if (distributedIA == 0) {
					schedule.schedule(scheduleParams, trafficLight,
							"distributedIA");
				} else if (distributedIA == 1) {
					schedule.schedule(scheduleParams, trafficLight,
							"centralizeIA");
				} else {
					schedule.schedule(scheduleParams, trafficLight, "withoutIA");
				}
				int min_dist = Integer.MAX_VALUE;
				for (CheckPoint cp : checkPointsList) {
					int dist = Math.abs(cp.getFinalJunction().getX() - x)
							+ Math.abs(cp.getFinalJunction().getY() - y);
					if (dist < min_dist && cp.getDirection() == Car.NORTH) {
						trafficLight.setStreet(cp);
						min_dist = dist;
					}
				}
			}
		}
		// EAST
		for (int x = 45; x < columns; x += 46) {
			for (int y = 2; y < rows; y += 92) {
				TrafficLight trafficLight = new TrafficLight(x, y, alpha);
				dataController.addTrafficLight(trafficLight);
				context.add(trafficLight);
				grid.moveTo(trafficLight, x, y);
				space.moveTo(trafficLight, x, y);
				if (distributedIA == 0) {
					schedule.schedule(scheduleParams, trafficLight,
							"distributedIA");
				} else if (distributedIA == 1) {
					schedule.schedule(scheduleParams, trafficLight,
							"centralizeIA");
				} else {
					schedule.schedule(scheduleParams, trafficLight, "withoutIA");
				}
				int min_dist = Integer.MAX_VALUE;
				for (CheckPoint cp : checkPointsList) {
					int dist = Math.abs(cp.getFinalJunction().getX() - x)
							+ Math.abs(cp.getFinalJunction().getY() - y);
					if (dist < min_dist && cp.getDirection() == Car.EAST) {
						trafficLight.setStreet(cp);
						min_dist = dist;
					}
				}
			}
		}

		// west
		for (int x = 5; x < columns - 1; x += 46) {
			for (int y = 48; y < rows; y += 92) {
				TrafficLight trafficLight = new TrafficLight(x, y, alpha);
				dataController.addTrafficLight(trafficLight);
				context.add(trafficLight);
				grid.moveTo(trafficLight, x, y);
				space.moveTo(trafficLight, x, y);
				if (distributedIA == 0) {
					schedule.schedule(scheduleParams, trafficLight,
							"distributedIA");
				} else if (distributedIA == 1) {
					schedule.schedule(scheduleParams, trafficLight,
							"centralizeIA");
				} else {
					schedule.schedule(scheduleParams, trafficLight, "withoutIA");
				}
				int min_dist = Integer.MAX_VALUE;
				for (CheckPoint cp : checkPointsList) {
					int dist = Math.abs(cp.getFinalJunction().getX() - x)
							+ Math.abs(cp.getFinalJunction().getY() - y);
					if (dist < min_dist && cp.getDirection() == Car.WEST) {
						trafficLight.setStreet(cp);
						min_dist = dist;
					}
				}
			}
		}

		// Put the trafficlightcontroller if it's a centralize system
		if (distributedIA == 1) {
			TrafficLightController controller = new TrafficLightController();
			context.add(controller);
			grid.moveTo(controller, 0, 0);
			space.moveTo(controller, 0, 0);
			schedule.schedule(scheduleParams, controller, "centralIAController");
		}
	}

	/**
	 * Set traffic light adjacencies
	 */
	private void setTrafficLightAdjacencies() {
		IndexedIterable<Object> iterator = context
				.getObjects(new DataController().getClass());
		DataController dataController = (DataController) iterator.get(0);
		Object[] tls = dataController.getTrafficLights();
		for (Object t : tls) {
			int dist = Integer.MAX_VALUE;
			for (Object tmp : tls) {

				int tmp_dist = Math.abs(((TrafficLight) t).getX()
						- ((TrafficLight) tmp).getX())
						+ Math.abs(((TrafficLight) t).getY()
								- ((TrafficLight) tmp).getY());
				if (tmp_dist < dist && tmp_dist > 0) {
					dist = tmp_dist;
					((TrafficLight) t).setAdjacent(((TrafficLight) tmp));
				}

			}
			if (dist > 10) {
				((TrafficLight) t).setAdjacent(null);
			}
		}
	}

	/**
	 * Method to get the list of all checkPoints
	 * @return
	 */
	public ArrayList<CheckPoint> getListCheckPoints() {
		return checkPointsList;
	}
}
