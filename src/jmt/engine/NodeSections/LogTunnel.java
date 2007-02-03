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

import jmt.engine.QueueNet.NetMessage;
import jmt.engine.QueueNet.NetNode;
import jmt.engine.QueueNet.NodeSection;
import jmt.engine.log.JSimLogger;

/**
* This class implements a "tunnel" service section. Every message
* sent from the input section is forwarded to the output section and every
* message sent the from output section is backwarded to the intput section.
* @author Francesco Radaelli
*/
public class LogTunnel extends ServiceSection {
	JSimLogger log;
	/** Creates a new instance of ServiceTunnel.
	 */
	public LogTunnel() {
		super();
       log = JSimLogger.getLogger();
	}


	/** Creates a new instance of ServiceTunnel.
	 */
	public LogTunnel(boolean auto) {
		super(auto);
   }


   public void NodeLinked(NetNode node) {

   }
   //end NEW


	protected int process(NetMessage message) throws jmt.common.exception.NetException {

       if (isMyOwnerNode(message.getSource())) {
			if (message.getSourceSection() == NodeSection.INPUT) {
				sendForward(message.getEvent(), message.getData(), 0.0);

				log.info("PIPPO");
			}
			if (message.getSourceSection() == NodeSection.OUTPUT) {
				sendBackward(message.getEvent(), message.getData(), 0.0);

               //log.write(NetLog.LEVEL_ALL, message.getJob(), this, NetLog.ACK_JOB);
			}
			return MSG_PROCESSED;
		} else
			return MSG_NOT_PROCESSED;
	}
}