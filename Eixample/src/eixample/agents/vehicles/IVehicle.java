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

import eixample.map.Junction;

/**
 * Car Agent Interface
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public interface IVehicle {
	int getPosX();

	int getPosY();

	int isAtDestination();

	boolean isJunction();

	int[] getDest();

	int getDestId();

	int getTicksToDestiny();

	void checkState();

	void step();

	void forward(int x, int y);

	void accelerate();

	void seeFrontCar();

	void seeTrafficLight();

	int getStoppedBy();

	boolean isStop();

	boolean isBreaking();

	boolean isTurning();

	int getCarril();

	void planPath(Junction dest);

	void setOrigen(Junction origen);

	void setX(int x);

	void setY(int y);

	void setDest(Junction dest, int dir);

	int getDirection();

	int getOldDirection();

	boolean arrivedJunction();
}
