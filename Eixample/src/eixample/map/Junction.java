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

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/**
 * Junction of the network
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class Junction {
	int id;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int x = 0;
	private int y = 0;

	/**
	 * Constructor
	 * @param id Id of Junction
	 * @param space Space of this simulation
	 * @param grid  Grid of this simulation
	 */
	public Junction(int id, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.id = id;
		this.grid = grid;
		this.space = space;
		// TODO Afegim el vehicle al context

	}

	/**
	 * Method to move this junction
	 * @param x X position
	 * @param y Y position
	 */
	public void move(int x, int y) {
		space.moveTo(this, x, y);
		grid.moveTo(this, x, y);
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the id of this Junction
	 * @return Id of this Junction
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id of this Junction
	 * @param id Id of this Junction
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method to get x position
	 * @return X position
	 */
	public int getX() {
		return x;
	}

	/**
	 * Method to get y position
	 * @return Y position
	 */
	public int getY() {
		return y;
	}

	/**
	 * Method to compare this junction with other juncton
	 * @param j Junction to compare with this
	 * @return Boolean to determinate if the junctions are equals or not
	 */
	public boolean equals(Junction j) {
		return (j.getX() == this.getX() && j.getY() == this.getY());
	}

	/**
	 * Method toString to print the this object
	 */
	public String toString() {
		return "Junction PosX:" + x + " PosY:" + y;
	}
}
