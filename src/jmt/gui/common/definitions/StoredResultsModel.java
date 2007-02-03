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
  
package jmt.gui.common.definitions;

import java.util.HashMap;
import java.util.Vector;

import jmt.engine.QueueNet.SimConstants;
import jmt.framework.gui.graph.MeasureValue;

/**
 * <p>Title: Result's Model data structure</p>
 * <p>Description: This class will store all values of measured loaded from a file.
 * It's used by <code>XMLResultsReader</code>.</p>
 * 
 * @author Bertoli Marco
 *         Date: 3-ott-2005
 *         Time: 14.10.50
 */
public class StoredResultsModel implements MeasureDefinition {
    protected Vector measures = new Vector();
    protected HashMap names = new HashMap();
    protected double pollingInterval = 1;
    private Vector queueLength = new Vector(),
    queueTime = new Vector(),
    residenceTime = new Vector(),
    responseTime = new Vector(),
    utilization = new Vector(),
    throughput = new Vector(),
    dropRate = new Vector(),
    systemResponseTime = new Vector(),
    systemThroughput = new Vector(),
    customerNumber = new Vector(),
    systemDropRate = new Vector();


    public Vector getMeasures() {
        return measures;
    }

    /**
     * Returns total number of measures
     * 
     * @return number of measures
     */
    public int getMeasureNumber() {
        return measures.size();
    }

    /**
     * Returns the station name of a given measure
     * 
     * @param measureIndex index of the measure
     * @return station name
     */
    public String getStationName(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).stationName;
    }

    /**
     * Returns the class name of a given measure
     * 
     * @param measureIndex index of the measure
     * @return class name
     */
    public String getClassName(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).className;
    }

    /**
     * Returns the alpha of a given measure
     * 
     * @param measureIndex index of the measure
     * @return alpha
     */
    public double getAlpha(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).alpha;
    }

    /**
     * Returns the precision of a given measure
     * 
     * @param measureIndex index of the measure
     * @return precision
     */
    public double getPrecision(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).precision;
    }

    /**
     * Returns number of analized samples for a given measure
     * 
     * @param measureIndex index of the measure
     * @return number of analized samples
     */
    public int getAnalizedSamples(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).analyzedSamples;
    }

    /**
     * Returns the name of a given measure
     * 
     * @param measureIndex index of the measure
     * @return name of the measure
     */
    public String getName(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).name;
    }

    /**
     * Returns the node type of a given measure
     * 
     * @param measureIndex index of the measure
     * @return name of the measure
     */
    public String getNodeType(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).nodeType;
    }

    /**
     * Returns the vector of Temporary values of a given measure. Each element of the vector
     * is an instance of <code>Value</code> interface.
     * 
     * @param measureIndex index of the measure
     * @return vector of termporary values until now
     */
    public Vector getValues(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).values;
    }

    /**
     * Returns the state of a measure, that can be MEASURE_IN_PROGRESS, MEASURE_NO_SAMPLES,
     * MEASURE_FAILED, MEASURE_SUCCESS
     * 
     * @param measureIndex index of the measure
     * @return measure state
     */
    public int getMeasureState(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).state;
    }

    /**
     * Returns the type of a measure
     * 
     * @param measureIndex index of the measure
     * @return measure type
     */
    public int getMeasureType(int measureIndex) {
        return ((Measure)measures.get(measureIndex)).type;
    }

    /**
     * Returns an array with the measureIndex of every queue length measure
     * @return an array with measures' index
     */
    public int[] getQueueLengthMeasures() {
        int[] tmp = new int[queueLength.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)queueLength.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every throughput measure
     * @return an array with measures' index
     */
    public int[] getThroughputMeasures() {
        int[] tmp = new int[throughput.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)throughput.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every queue time measure
     * @return an array with measures' index
     */
    public int[] getQueueTimeMeasures() {
        int[] tmp = new int[queueTime.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)queueTime.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every residence time measure
     * @return an array with measures' index
     */
    public int[] getResidenceTimeMeasures() {
        int[] tmp = new int[residenceTime.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)residenceTime.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every response time measure
     * @return an array with measures' index
     */
    public int[] getResponseTimeMeasures() {
        int[] tmp = new int[responseTime.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)responseTime.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every drop rate measure
     * @return an array with measures' index
     */
    public int[] getDropRateMeasures() {
        int[] tmp = new int[dropRate.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)dropRate.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every system drop rate measure
     * @return an array with measures' index
     */
    public int[] getSystemDropRateMeasures() {
        int[] tmp = new int[systemDropRate.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)systemDropRate.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every utilization measure
     * @return an array with measures' index
     */
    public int[] getUtilizationMeasures() {
        int[] tmp = new int[utilization.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)utilization.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every system response time measure
     *
     * @return an array with measures' index
     */
    public int[] getSystemResponseTimeMeasures() {
        int[] tmp = new int[systemResponseTime.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)systemResponseTime.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every system throughput measure
     *
     * @return an array with measures' index
     */
    public int[] getSystemThroughputMeasures() {
        int[] tmp = new int[systemThroughput.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)systemThroughput.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns an array with the measureIndex of every customer number measure
     *
     * @return an array with measures' index
     */
    public int[] getCustomerNumberMeasures() {
        int[] tmp = new int[customerNumber.size()];
        for (int i=0; i<tmp.length; i++)
            tmp[i] = ((Integer)customerNumber.get(i)).intValue();
        return tmp;
    }

    /**
     * Returns if simulation has finished, so results are fixed
     * 
     * @return true iff simulation has finished
     */
    public boolean isSimulationFinished() {
        return true;
    }

    /**
     * Returns simulation polling interval. This is the time elapsed between two temp values.
     * 
     * @return simulation polling interval in seconds
     */
    public double getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Returns current simulation progress time
     * @return current progress time
     */
    public double getProgressTime() {
        return 1.0;
    }

// --- Methods to populate data structure ----------------------------------------------------------------
    /**
     * Adds a new measure into this data structure.
     * @param name measure name
     * @param stationName reference station name
     * @param className reference class name
     * @param alpha measure alpha
     * @param precision measure precision
     * @param analyzedSamples number of analyzed samples
     * @param state state of the measure
     * @param type type of the measure
     */
    public void addMeasure(String name, String stationName, String className,
                       double alpha, double precision, int analyzedSamples, int state, int type, String nodeType) {
        Measure tmp = new Measure(name, stationName, className, alpha,precision, analyzedSamples, state, type, nodeType);
        measures.add(tmp);
        names.put(name, tmp);
        // Adds measure index to the right Vector
        switch(type) {
            case SimConstants.QUEUE_TIME:
                queueTime.add(new Integer(measures.size()-1));
                break;
            case SimConstants.RESIDENCE_TIME:
                residenceTime.add(new Integer(measures.size()-1));
                break;
            case SimConstants.RESPONSE_TIME:
                responseTime.add(new Integer(measures.size()-1));
                break;
            case SimConstants.UTILIZATION:
                utilization.add(new Integer(measures.size()-1));
                break;
            case SimConstants.THROUGHPUT:
                throughput.add(new Integer(measures.size()-1));
                break;
            case SimConstants.QUEUE_LENGTH:
                queueLength.add(new Integer(measures.size()-1));
                break;
            case SimConstants.SYSTEM_RESPONSE_TIME:
                systemResponseTime.add(new Integer(measures.size()-1));
                break;
            case SimConstants.SYSTEM_JOB_NUMBER:
                customerNumber.add(new Integer(measures.size()-1));
                break;
            case SimConstants.SYSTEM_THROUGHPUT:
                systemThroughput.add(new Integer(measures.size()-1));
                break;
            case SimConstants.DROP_RATE:
                dropRate.add(new Integer(measures.size()-1));
                break;
            case SimConstants.SYSTEM_DROP_RATE:
                systemDropRate.add(new Integer(measures.size()-1));
                break;
        }
    }

    /**
     * Adds a new sample to specified measure
     * @param measureName name of the measure
     * @param meanValue mean value of the sample
     * @param upperBound upper bound of the sample
     * @param lowerBound lower bound of the sample
     */
    public void addMeasureSample(String measureName, double meanValue, double upperBound, double lowerBound) {
        Measure tmp = (Measure)names.get(measureName);
        tmp.addSample(meanValue, upperBound, lowerBound);
    }

    /**
     * Sets measure polling interval
     * @param interval polling interval
     */
    public void setPollingInterval (double interval) {
        this.pollingInterval = interval;
    }
// -------------------------------------------------------------------------------------------------------

// --- Inner Classes--------------------------------------------------------------------------------------
    /**
     * Inner class to store parameters of each measure
     */ 
    protected class Measure {
        public String name, 
            stationName, 
            className, nodeType;
        public Vector values;
        public double alpha,
            precision;
        public int analyzedSamples,
            state,
            type;
        /**
         * Construct a new Neasure object
         * @param name measure name
         * @param stationName reference station name
         * @param className reference class name
         * @param alpha measure alpha
         * @param precision measure precision
         * @param analyzedSamples number of analyzed samples
         * @param state state of the measure
         * @param type type of the measure
         */
        public Measure(String name, String stationName, String className,
                       double alpha, double precision, int analyzedSamples, int state, int type, String nodeType) {
            this.name = name;
            this.stationName = stationName;
            this.className = className;
            this.alpha = alpha;
            this.precision  = precision;
            this.analyzedSamples = analyzedSamples;
            this.state = state;
            this.type = type;
            values = new Vector();
            this.nodeType = nodeType;
        }

        /**
         * Adds a new sample to current measure
         * @param meanValue mean value of the sample
         * @param upperBound upper bound of the sample
         * @param lowerBound lower bound of the sample
         */
        public void addSample(double meanValue, double upperBound, double lowerBound) {
            MeasureValueImpl val = new MeasureValueImpl(meanValue, upperBound, lowerBound);
            values.add(val);
        }
    }

    /**
     * Inner class that implements Value interface
     */
    public class MeasureValueImpl implements MeasureValue {
        private double mean, upper, lower;
        /**
         * Creates a new MeasureValue object
         * @param meanValue mean value of the sample
         * @param upperBound sample upper bound
         * @param lowerBound sample lower bound
         */
        public MeasureValueImpl(double meanValue, double upperBound, double lowerBound) {
            mean = meanValue;
            upper = upperBound;
            lower = lowerBound;
        }
        public double getUpperBound() {
            return upper;
        }

        public double getLowerBound() {
            return lower;
        }

        public double getMeanValue() {
            return mean;
        }
    }
// -------------------------------------------------------------------------------------------------------

// --- Useless methods -----------------------------------------------------------------------------------
    /**
     * This feature is not required as loaded measures are static
     * @param listener listener to be set or null to unset previous one
     */
    public void setProgressTimeListener(MeasureDefinition.ProgressTimeListener listener) {
    }
    
    /**
     * This feature is not required as loaded measures are static
     * @param measureIndex index of the measure that this listener should listen
     * @param listener     listener to add or null to remove old one.
     */
    public void addMeasureListener(int measureIndex, MeasureDefinition.MeasureListener listener) {
    }
// -------------------------------------------------------------------------------------------------------
}
