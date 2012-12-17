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

import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

/**
 * Eixample SubContext (Network)
 * 
 * @author Alex Pardo Fernandez and David Sanchez Pinsach alexpardo.5@gmail.com
 *         and sdividis@gmail.com
 * 
 */
public class EixampleSubcontext extends DefaultContext<Object> {

	Parameters p = RunEnvironment.getInstance().getParameters();
	public int test;

	public EixampleSubcontext() {
		super("eixample_subcontext");
	}
}