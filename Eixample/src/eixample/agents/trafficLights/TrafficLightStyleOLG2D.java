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

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

/**
 * Setup style (colors) for traffic light
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 * @see {@link DefaultStyleOGL2D}
 * 
 */
public class TrafficLightStyleOLG2D extends DefaultStyleOGL2D {

	@Override
	public Color getColor(final Object agent) {
		TrafficLight trafficLight = (TrafficLight) agent;
		return trafficLight.getState();
	}

	@Override
	public float getScale(final Object agent) {
		return 2;
	}
}
