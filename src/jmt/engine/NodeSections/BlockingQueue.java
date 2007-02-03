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

import jmt.common.exception.NetException;
import jmt.engine.NetStrategies.QueueGetStrategies.FCFSstrategy;
import jmt.engine.NetStrategies.QueueGetStrategy;
import jmt.engine.QueueNet.*;

import java.util.ListIterator;
import java.util.LinkedList;

/**
 * This class implements the queue of a region input station, that is
 * the node which controls the access to a blocking region.
 *
 * @author Stefano Omini
 *
 * Modifief by Bertoli Marco 31-03-2006 (my birthday!!!)
 */
public class BlockingQueue extends InputSection {

    public static final boolean DEBUG = false;

	/** Property Identifier: infinite. */
	public static final int PROPERTY_ID_INFINITE = 0x0101;
	/** Property Identifier: drop.*/
	public static final int PROPERTY_ID_DROP = 0x0102;
	/** Property Identifier: size.*/
	public static final int PROPERTY_ID_SIZE = 0x0103;
	/** Property Identifier: Waiting request.*/
	public static final int PROPERTY_ID_WAITING_REQUESTS = 0x0104;
	/** Property Identifier: Queue get strategy.*/
	public static final int PROPERTY_ID_GET_STRATEGY = 0x0105;
	/** Property Identifier: Queue put strategy.*/
	public static final int PROPERTY_ID_PUT_STRATEGY = 0x0106;

    //coolStart is true if there are no waiting jobs when the queue is started
	private boolean coolStart;

    private boolean infinite = true;

    private int size;

	private QueueGetStrategy getStrategy;

    private BlockingRegion blockingRegion;

    /** For each class, true if jobs in excess must be dropped */
    protected boolean[] classDrop;

    /** For each class, true if jobs in excess must be dropped */
    protected double[] classWeights = null;

    private int droppedJobs;
    private int[] droppedJobsPerClass;

    //job info list of the owner node: used to remove job after drop
    private JobInfoList jobsList_node;


	/**
     * Creates a new instance of infinite BlockingQueue.
	 */
	public BlockingQueue(BlockingRegion myRegion) {
		
        super(true);

        coolStart = true;
        getStrategy = new FCFSstrategy();
        //infinite queue
        this.size = -1;
        //sets the blocking region owner of this blocking queue
        this.blockingRegion = myRegion;

	}



	protected void nodeLinked(NetNode node) {
		// Sets netnode dependent properties

		//if (jobsList == null)
		jobsList = new JobInfoList(getJobClasses().size(), true);

        jobsList_node = this.getOwnerNode().getJobInfoList();

        //copies drop properties from blocking region
        //and initializes dropped jobs
        droppedJobs = 0;
        droppedJobsPerClass = new int[node.getJobClasses().size()];
        this.classDrop = new boolean[node.getJobClasses().size()];

        for (int i = 0; i < droppedJobsPerClass.length; i++) {
            //copies drop properties from blocking region
            this.classDrop[i] = blockingRegion.getClassDrop(i);
            //initializes dropped jobs
            droppedJobsPerClass[i] = 0;
        }


        return;
	}


    public int getIntSectionProperty(int id) throws NetException {
		switch (id) {
			case PROPERTY_ID_SIZE:
				return size;
			default:
				return super.getIntSectionProperty(id);
		}
	}

    /**
     * Gets, if exists, the first job in queue that is not class blocked
     * @return the first job in queue that is not class blocked; null otherwise.
     */
    private Job getFirstNotBlockedJob() {

        if (jobsList == null)
			return null;

        LinkedList list = (LinkedList) jobsList.getJobList();

		ListIterator iterator = list.listIterator();

        JobInfo jobInfo;
        Job job;
        JobClass jobClass;

		while (iterator.hasNext()) {
			jobInfo = (JobInfo) iterator.next();
			job = jobInfo.getJob();
            jobClass = job.getJobClass();

            if (!blockingRegion.isBlocked(jobClass)) {
				//this job is not class blocked
                return job;
            }
		}
		return null;
    }

    /** This method implements a blocking queue
	 * @param message message to be processed.
	 * @throws NetException
	 */
	protected int process(NetMessage message) throws NetException {
		Job job;
        Job notBlockedJob;

		switch (message.getEvent()) {

            case NetEvent.EVENT_START:

                //the input station receives the "start" event because it has no nodes in input
                //then it's marked as reference node

                //NEW
                //@author Stefano Omini

                //checks whether there are jobs in queue
                if (jobsList.size() == 0) {
                    //no jobs in queue
                    coolStart = true;
                    break;
                }

                //get the first job which is not blocked (if exists)

				notBlockedJob = getFirstNotBlockedJob();

                if (notBlockedJob == null) {
                    //all jobs in queue are class blocked
                    //nothing else to do
                    break;
                } else {
                    //get class
                    JobClass jobClass = notBlockedJob.getJobClass();
                    //forward job and increase occupation
                    forward(notBlockedJob);
                    blockingRegion.increaseOccupation(jobClass);
                    break;
                }


            //the BlockingQueue has almost the same behaviour with the events
            //EVENT_JOB_OUT_OF_REGION and EVENT_ACK

            case NetEvent.EVENT_JOB_OUT_OF_REGION:

                //checks whether there are jobs in queue
                if (jobsList.size() == 0) {
                    //no jobs in queue
                    coolStart = true;
                    break;
                }

                //search for the first job which is not blocked (if exists)
                // and forward it

				notBlockedJob = getFirstNotBlockedJob();

                if (notBlockedJob != null) {
                    JobClass jobClass = notBlockedJob.getJobClass();
                    forward(notBlockedJob);
                    blockingRegion.increaseOccupation(jobClass);
                    break;
                } else {
                    //all jobs in queue are blocked
                    //nothing else to do
                    break;
                }


            case NetEvent.EVENT_ACK:

                //checks whether there are jobs in queue
                if (jobsList.size() == 0) {
                    //no jobs in queue
                    coolStart = true;
                    break;
                }

                //search for the first job which is not blocked (if exists)
                // and forward it

				notBlockedJob = getFirstNotBlockedJob();

                if (notBlockedJob != null) {
                    JobClass jobClass = notBlockedJob.getJobClass();
                    forward(notBlockedJob);
                    blockingRegion.increaseOccupation(jobClass);
                    break;
                } else {
                    //all jobs in queue are blocked
                    //nothing else to do
                    break;
                }



            case NetEvent.EVENT_JOB:

                //EVENT_JOB

                job = message.getJob();

                //no ack must be sent to the source of message (the redirecting queue of a node
                //inside the region)

                JobClass jobClass = job.getJobClass();

                //this job has been received by an internal node
                //the region input station will have to send it back
                NetNode realDestination = message.getSource();

                //adds a JobInfo object after modifying the job with redirection informations
                job.setOriginalDestinationNode(realDestination);
                jobsList.add(new JobInfo(job));

                //checks whether the region is blocked for this class
                if (blockingRegion.isBlocked(jobClass)) {
                    //the region is already blocked

                    //the job must be dropped?
                    if (classDrop[jobClass.getId()] == true) {
                        //drop job
                        drop(job);
                        break;
                    }
                    //otherwise the job remains blocked in queue
                    break;
                }


                //the region is not blocked for this class
                //checks whether the queue is empty (coolstart = true) or not

				if (coolStart) {
                    // If coolStart is true, this job is sent immediately to the next
				    // section and coolStart set to false.

                    forward(job);
                    blockingRegion.increaseOccupation(jobClass);

					coolStart = false;

				} else {
                    //coolStart is false: this means that the jobs in queue are class blocked,
                    //otherwise they would have been already sent to the region till it would
                    //become globally or class blocked

                    //the job is sent to service section

                    //OLD
                    /* Job firstNotBlocked = getFirstNotBlockedJob();
                    if (firstNotBlocked != null) {
                        forward(firstNotBlocked);
                        if (DEBUG) {
                            System.out.println("SendJobIn");
                        }

                        blockingRegion.increaseOccupation(jobClass);

                    }
                    */
                    forward(job);
                    if (DEBUG) {
                        System.out.println("SendJobIn");
                    }

                    blockingRegion.increaseOccupation(jobClass);

				}
				break;

            default:
				return MSG_NOT_PROCESSED;
		}
		return MSG_PROCESSED;
	}

	private void forward(Job job) throws NetException {
		sendForward(job, 0.0);
	}

    /**
     * Drop the specified job, removing it from the job info lists and increases
     * the counters of dropped jobs
     * @param job the job to be dropped
     * @throws NetException
     */
    private void drop(Job job) throws NetException {

        int c = job.getJobClass().getId();
        droppedJobs++;
        droppedJobsPerClass[c]++;

        JobInfo jobInfo = jobsList.lookFor(job);
        if (jobInfo != null) {
            //removes job from the jobInfoList of the node section
            jobsList.removeAfterDrop(jobInfo);

            //removes job from the jobInfoList of the node
            this.getOwnerNode().getJobInfoList().removeAfterDrop(jobInfo);
        }

        if (jobsList_node == null) {
            jobsList_node = this.getOwnerNode().getJobInfoList();
        }
        JobInfo jobInfo_node = jobsList_node.lookFor(job);
        if (jobInfo_node != null) {
            //removes job from the jobInfoList of the node
            jobsList_node.removeAfterDrop(jobInfo_node);
        }
        // Removes job from global jobInfoList - Bertoli Marco
        getOwnerNode().getQueueNet().getJobInfoList().dropJob(job);
    }

    /**
     * Gets the number of dropped jobs of each class
     * @return the number of dropped jobs of each class
     */
    public int[] getDroppedJobsPerClass() {
        return droppedJobsPerClass;
    }

    /**
     * Gets the number of dropped jobs for the specified class
     * @param jobClassIndex the index of the job class
     * @return the number of dropped jobs for the specified class, -1 otherwise
     */
    public int getDroppedJobPerClass(int jobClassIndex) {
        return droppedJobsPerClass[jobClassIndex];
    }

    /**
     * Gets the number of dropped jobs
     * @return the number of dropped jobs
     */
    public int getDroppedJobs() {
        return droppedJobs;
    }
}
