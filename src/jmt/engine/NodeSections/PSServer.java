/**    
  * Copyright (C) 2009, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

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

import java.util.PriorityQueue;

import jmt.common.exception.NetException;
import jmt.engine.NetStrategies.ServiceStrategy;
import jmt.engine.QueueNet.Job;
import jmt.engine.QueueNet.JobClass;
import jmt.engine.QueueNet.NetEvent;
import jmt.engine.QueueNet.NetMessage;
import jmt.engine.QueueNet.NetNode;
import jmt.engine.QueueNet.NetSystem;
import jmt.engine.QueueNet.NodeSection;
import jmt.engine.simEngine.RemoveToken;

/**
 * <p><b>Name:</b> PSServer</p> 
 * <p><b>Description:</b> 
 * This class implements a multi-class processor sharing server.
 * </p>
 * <p><b>Date:</b> 04/ott/2009
 * <b>Time:</b> 13.45.37</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class PSServer extends ServiceSection {
	/** Property Identifier:  Busy counter. */
	public static final int PROPERTY_ID_BUSY_COUNTER = 0x0101;
	/** Property Identifier:  Max jobs (number of servers). */
	public static final int PROPERTY_ID_MAX_JOBS = 0x0102;
	/** Property Identifier:  Service strategy. */
	public static final int PROPERTY_ID_SERVICE_STRATEGY = 0x0104;

	private int numberOfServers,
	/** This valiable is used to implement blocking.*/
	waitingAcks;

	private ServiceStrategy serviceStrategy[];

	private PriorityQueue<JobData> jobs;

	// A token to preempt the last message sent when a new job arrives
	private RemoveToken lastMessageSent;
	private double lastMessageSentTime;

	/** Creates a new instance of Server.
	 * @param serverNumber Number of jobs which can be served simultaneously.
	 * @param serviceStrategy Array of service strategies, one per class.
	 * @throws jmt.common.exception.NetException
	 */
	public PSServer(Integer serverNumber, ServiceStrategy serviceStrategy[]) throws jmt.common.exception.NetException {
		this.serviceStrategy = serviceStrategy;
		this.numberOfServers = serverNumber.intValue();
		jobs = new PriorityQueue<JobData>();
	}

	//FIXME remove this when development is finished
	@Deprecated
	public PSServer(Integer serverNumber, Integer numberOfVisitsPerClass[], ServiceStrategy serviceStrategy[])
			throws jmt.common.exception.NetException {
		this(serverNumber, serviceStrategy);
	}

	@Override
	public int getIntSectionProperty(int id) throws jmt.common.exception.NetException {
		switch (id) {
			case PROPERTY_ID_BUSY_COUNTER:
				return jobs.size();
			case PROPERTY_ID_MAX_JOBS:
				return numberOfServers;
			default:
				return super.getIntSectionProperty(id);
		}
	}

	@Override
	public Object getObject(int id, JobClass jobClass) throws jmt.common.exception.NetException {
		switch (id) {
			case PROPERTY_ID_SERVICE_STRATEGY:
				return serviceStrategy[jobClass.getId()];
			default:
				return super.getObject(id);
		}
	}

	@Override
	public Object getObject(int id) throws jmt.common.exception.NetException {
		switch (id) {
			case PROPERTY_ID_SERVICE_STRATEGY:
				return serviceStrategy;
			default:
				return super.getObject(id);
		}
	}

	@Override
	protected int process(NetMessage message) throws jmt.common.exception.NetException {
		switch (message.getEvent()) {
			case NetEvent.EVENT_JOB:
				Job job = message.getJob();

				if (isMine(message)) {
					// I sent the message, so it ended servicing
					updateServiceTimes(lastMessageSentTime);
					lastMessageSent = null;
					// forwards the completed jobs to the output section
					sendForward(jobs.poll().getJob(), 0.0);
					waitingAcks++;
				} else {
					// Check if a job is running
					if (lastMessageSent != null) {
						// We need to preempt the last message
						removeMessage(lastMessageSent);
						updateServiceTimes(lastMessageSentTime);
						lastMessageSent = null;
					}
					// Estimate the job service time, puts it in the queue and sends a message to itself
					// with the minimum service time of all the jobs to perform processing
					double serviceTime = serviceStrategy[job.getJobClass().getId()].wait(this);
					jobs.add(new JobData(job, serviceTime));

					// Sends an ACK to the input section as we will always accept new jobs.
					sendBackward(NetEvent.EVENT_ACK, message.getJob(), 0.0);
				}

				serviceJobs();

				break;

			case NetEvent.EVENT_ACK:
				//EVENT_ACK
				//If there are no jobs in the service section, message is not processed.
				if (waitingAcks == 0) {
					//it wasn't waiting for any job
					return NodeSection.MSG_NOT_PROCESSED;
				} else {
					// Avoid ACK to the input section, as we already sent ack
					waitingAcks--;
				}

				serviceJobs();

				break;

			default:
				return MSG_NOT_PROCESSED;
		}
		return MSG_PROCESSED;
	}

	/**
	 * Service jobs (send a message to itself), if it is not blocked.
	 */
	private void serviceJobs() throws NetException {
		// If all acks were received and there are jobs in internal queue, 
		// send in service the one with the minimum residual service time
		if (waitingAcks == 0 && jobs.size() > 0) {
			// Estimate wait time for the minimum job
			double waitTime;
			if (jobs.size() <= numberOfServers) {
				// No sharing in this case: each job is served by a processor
				waitTime = jobs.peek().getResidualServiceTime();
			} else {
				// Processors are shared among the jobs, so service time will be slow.
				waitTime = jobs.peek().getResidualServiceTime() * jobs.size() / numberOfServers;
			}

			lastMessageSent = sendMe(jobs.peek().getJob(), waitTime);
			lastMessageSentTime = NetSystem.getTime();
		}

	}

	/**
	 * Updates service times of all the jobs being served by the corresponding amount.
	 * All completed jobs will be sent forward.
	 * @param time the start time in which the job message was sent
	 * @param endedJob the job that was ended. May be null
	 * @throws NetException if something goes wrong.
	 */
	private void updateServiceTimes(double startTime) throws NetException {
		double serviceTime;

		if (jobs.size() <= numberOfServers) {
			// No sharing in this case: each job is served by a processor
			serviceTime = NetSystem.getTime() - startTime;
		} else {
			// Processors are shared among the jobs
			serviceTime = (NetSystem.getTime() - startTime) * numberOfServers / jobs.size();
		}

		for (JobData jd : jobs) {
			jd.performService(serviceTime);
		}
	}

	/**
	 * An inner data structure used to keep job and service time data.
	 * The data structure is ordered basing on service times.
	 */
	private static class JobData implements Comparable<JobData> {
		private Job job;
		private double residualServiceTime;

		/**
		 * Builds a new JobData data structure
		 * @param job the job
		 * @param residualServiceTime the residual service time
		 */
		public JobData(Job job, double residualServiceTime) {
			this.job = job;
			this.residualServiceTime = residualServiceTime;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(JobData o) {
			if (residualServiceTime < o.residualServiceTime) {
				return -1;
			} else if (residualServiceTime > o.residualServiceTime) {
				return 1;
			} else {
				return 0;
			}
		}

		/**
		 * @return the job
		 */
		public Job getJob() {
			return job;
		}

		/**
		 * @return the residual service time
		 */
		public double getResidualServiceTime() {
			return residualServiceTime;
		}

		/**
		 * Performs service, scaling the residual service time
		 * @param time the service time received
		 */
		public void performService(double time) {
			residualServiceTime = residualServiceTime - time;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Job id: " + job.getId() + " class: " + job.getJobClass().getName() + " residual time: " + residualServiceTime;
		}
	}

	/* (non-Javadoc)
	 * @see jmt.engine.QueueNet.NodeSection#nodeLinked(jmt.engine.QueueNet.NetNode)
	 */
	@Override
	protected void nodeLinked(NetNode node) {
		jobsList.setProcessorSharing(true);
		jobsList.setServerNumber(numberOfServers);
	}

}
