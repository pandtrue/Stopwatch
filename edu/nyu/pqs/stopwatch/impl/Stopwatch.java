package edu.nyu.pqs.stopwatch.impl;

import java.util.List;
import java.util.ArrayList;
import edu.nyu.pqs.stopwatch.api.IStopwatch;

/**
 * Stopwatch Class is an thread-safe implementation of IStopwatch interface.
 * The stopwatch objects are created by the StopwatchFactory.
 * 
 * @author Tianyi Cong
 *
 */
public class Stopwatch implements IStopwatch {
  // Immutable id for stopwatch
	private final String id;
	// List to store the lap times
	private List<Long> lapTimes;
	// The time stopwatch start, initialized by 0.
	private long startTime = 0;
	// The time stopwatch takes the latest lap operation, initialized by 0.
	private long latestTime = 0;
	// The time between previous stop and start/lap operation before stop.
	private long previousLap = 0;
	// Mark the running status of stopwatch.
	private volatile boolean isRunning = false;
	
	/**
	 * Constructor of Stopwatch that initialize the List of lap times and 
	 * sets an id to the stopwatch object.
	 * 
	 * @param id ID of the stopwatch.
	 */
	protected Stopwatch(String id) {
		this.id = id;
		lapTimes = new ArrayList<Long>();
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public synchronized void start() throws IllegalStateException {
		if (isRunning) {
			throw new IllegalStateException("the stopwatch has already start to run!");
		}
		// startTime!=0 means this is not the first time hit start. So need to
		// save the last lap for the later use in case start it being hit again, 
		// then delete it from the list. 
		if (startTime != 0) {
			previousLap = lapTimes.get(lapTimes.size() - 1);
			lapTimes.remove(lapTimes.size() - 1);
		}
		//Set isRunning status to true when stopwatch starts.
		isRunning = true;
		startTime = System.currentTimeMillis();
	}

	@Override
	public synchronized void lap() throws IllegalStateException {
		if (!isRunning) {
			throw new IllegalStateException("the stopwatch is not isRunning!");
		}
		long currentTime = System.currentTimeMillis();
		// previousLap!=0 means this is the first lap after stop and then start the 
		// stopwatch again, we need to add the new lap time to the lap time which 
		// we save that value at the start procedure. Then reset the previousLap.
		if(previousLap != 0) {
			lapTimes.add(currentTime - startTime + previousLap);
			previousLap = 0;
		} 
		else {
			// Not any lap after start yet.
			if (latestTime == 0) {
				lapTimes.add(currentTime - startTime);
			}
			// At least one lap hit.
			else {
				lapTimes.add(currentTime - latestTime);
			}	
		}
		// update the latest time hit lap.
		latestTime = currentTime;
	}

	@Override
	public synchronized void stop() throws IllegalStateException {
		if (!isRunning) {
			throw new IllegalStateException("the stopwatch is not isRunning!");
		}
		long currentTime = System.currentTimeMillis();
		isRunning = false;
		// Same as the situation of lap. But without reset the previousLap since 
		// there might be another stop hit after start.
		if (previousLap != 0) {
			lapTimes.add(currentTime - startTime + previousLap);
		} 
		else {
			// Not any lap after start yet.
			if (latestTime == 0) {
				lapTimes.add(currentTime - startTime);
			}
			// At least one lap hit.
			else {
					lapTimes.add(currentTime - latestTime);	
			}
		}
	}

	@Override
	public synchronized void reset() {
		// Reset everything and empty the list.
		isRunning = false;
		startTime = 0;
		latestTime = 0;
		previousLap = 0;
		lapTimes.clear();
	}

	@Override
	public List<Long> getLapTimes() {
		synchronized(lapTimes) {
			return lapTimes;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Stopwatch)) {
			return false;
		}
		Stopwatch other = (Stopwatch) obj;
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		return 31 * result + (id == null ? 0 : id.hashCode());
	}
	
	@Override
	public String toString() {
		return "Stopwatch " + id;
	}
}