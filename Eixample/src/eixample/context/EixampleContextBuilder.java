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

package eixample.context;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import eixample.main.DataController;
import eixample.main.Main;
import eixample.map.CheckPoint;
import eixample.map.Map;

/**
 * Eixample Context Builder
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class EixampleContextBuilder implements ContextBuilder<Object> {

	private int h_streets = 6;
	private int v_streets = 6;

	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		context.setId("eixample");

		// Get parameters
		Parameters params = RunEnvironment.getInstance().getParameters();
		h_streets = (Integer) params.getValue("h_streets");
		v_streets = (Integer) params.getValue("v_streets");

		int rows = (40 * (h_streets - 1)) + (h_streets * 6);
		int columns = (40 * (v_streets - 1)) + (v_streets * 6);

		// Create space world
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), rows,
				columns);

		// Create grid world
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, rows, columns));

		// TODO He fet una clase encarregada de construir el mapa de la
		// simulacio

		EixampleSubcontext subcontext = new EixampleSubcontext();
		context.addSubContext(subcontext);

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"eixample network", subcontext, true);
		netBuilder.buildNetwork();

		// Create space world
		ContinuousSpaceFactory spaceFactory2 = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space2 = spaceFactory.createContinuousSpace(
				"space_subcontext", subcontext,
				new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), rows,
				columns);

		// Create grid world
		GridFactory gridFactory2 = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid2 = gridFactory.createGrid("grid_subcontext",
				subcontext, new GridBuilderParameters<Object>(
						new WrapAroundBorders(), new SimpleGridAdder<Object>(),
						true, rows, columns));

		DataController controller = new DataController(0, context);
		context.add(controller);
		// Build the map of simulation with homes and traffic lights
		Map map = new Map(context, subcontext);
		map.build();
		ArrayList<CheckPoint> checkPointList = map.getListCheckPoints();
		for (CheckPoint cp : checkPointList) {
			context.add(cp);
		}
		controller.setCheckPointList(checkPointList);

		// Call the main and this creates the agent car
		Main main = new Main(context, subcontext, checkPointList);
		context.add(main);

		return context;
	}
}
