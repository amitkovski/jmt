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
  
package jmt.gui.common.routingStrategies;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: orsotronIII
 * Date: 7-lug-2005
 * Time: 15.25.12
 * To change this template use Options | File Templates.
 */
public class ProbabilityRouting extends RoutingStrategy{

    public ProbabilityRouting(){
        description ="It is possible to define the routing probability for each connected station. " +
            "If the sum of the probabilities is different from 1, all the values will be " +
            "scaled to sum 1.";
    }

    private HashMap probabilities = new HashMap();

    public String getName() {
        return "Probabilities";
    }

    public HashMap getValues() {
        return probabilities;
    }

    public Object clone(){
        ProbabilityRouting pr = new ProbabilityRouting();
        pr.probabilities = new HashMap(probabilities);
        return pr;
    }


    public String getClassPath() {
        return "jmt.engine.NetStrategies.RoutingStrategies.EmpiricalStrategy";
    }

    /**
     * Returns true if the routing strategy is dependent from the state of
     * the model
     * @return  true if the routing strategy is dependent from the state of
     * the model
     *
     * Author: Francesco D'Aquino
     */
    public boolean isModelStateDependent() {
        return false;
    }
}