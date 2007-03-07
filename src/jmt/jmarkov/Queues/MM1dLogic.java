/**    
  * Copyright (C) 2006, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
  */
  
/*
 * Created on 25-mar-2004 by Ernesto
 *
 */
package jmt.jmarkov.Queues;

import jmt.jmarkov.Queues.Exceptions.NonErgodicException;

import java.util.Random;


/**
 * MMQueues
 * --------------------------------------
 * 25-mar-2004 - Queues/MM1dLogic.java
 * 
 * @author Ernesto
 */
public class MM1dLogic extends MM1Logic {
	
	private int max;
	
	private Random rnd;

	/**
	 * Inizializza la coda  
	 * @param lambda rappresenta la media del <i>processo di Poisson</i> che regola gli arrivi <b>[job/s]</b>
	 * @param s rappresenta la media del <i>processo di Poisson</i> che regola le esecuzioni <b>[ms]</b>
	 * @param max rappresenta le dimensioni del buffer
	 */
	public MM1dLogic(double lambda, double s, int max){
		super(lambda, s);
		this.max = max;
	}
	

	/* (non-Javadoc)
	 * @see Queues.QueueLogic#getStatusProbability(int)
	 */
	public double getStatusProbability(int status) throws NonErgodicException{
		double u = utilization();
		if ((status > max) && (max > 0)) return 0.0;
		return ((1.0 - u)/(1.0 - Math.pow(u, max + 1)) * (Math.pow(u, status)));
	}

	/* (non-Javadoc)
	 * @see Queues.QueueLogic#getMaxStates()
	 */
	public int getMaxStates() {
		return max;
	}

	public void setMaxStates(int max) {
			this.max = max;
		}
		
	public double mediaJobs() throws NonErgodicException{
		double u = utilization();
		return ((u / (1 - u)) - 
				(Math.pow(u, max + 1) * (max + 1)) / (1 - Math.pow(u, max + 1)));
		
	}
	
	public static double getNi(double Ui, double buffer){
		if ((Ui <= 1.0) && (Ui >= 0.0))
			return ((Math.pow(Ui, 2) / (1.0 - Ui)) -
				(Math.pow(Ui, buffer + 1)) / (1 - Math.pow(Ui, buffer + 1)));
		return Double.MAX_VALUE;
	}


}