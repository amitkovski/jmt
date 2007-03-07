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
  
package jmt.engine.NodeSections;

import jmt.engine.QueueNet.Job;
import jmt.engine.QueueNet.NodeSection;

/**
 * This abstract class implements a generic output section of a NetNode.
 * @author Francesco Radaelli
 */
public abstract class OutputSection extends PipeSection {

	/** Creates a new instance of outputSection.
	 */
	public OutputSection() {
		super(NodeSection.OUTPUT);
	}

	/** Creates a new instance of outputSection
	 *  @param Auto  Auto refresh of the jobsList attribute.
	 */
	public OutputSection(boolean Auto) {
		super(NodeSection.OUTPUT, Auto);
	}

	/** Sends a job to the service section.
	 * @param Job Job to be sent.
	 * @param Delay Scheduling delay.
	 * @throws jmt.common.exception.NetException
	 */
	protected void sendBackward(Job Job, double Delay)
	        throws jmt.common.exception.NetException {
		send(Job, Delay, NodeSection.SERVICE);
	}

	/** Sends a message to the service section.
	 * @param Event Message tag.
	 * @param Delay Scheduling delay.
	 * @throws jmt.common.exception.NetException
	 */
	protected void sendBackward(int Event, Object Data, double Delay)
	        throws jmt.common.exception.NetException {
		send(Event, Data, Delay, NodeSection.SERVICE);
	}
}