package edu.nyu.pqs.stopwatch.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.pqs.stopwatch.api.IStopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for IStopwatch objects.
 * It maintains references to all created IStopwatch objects and provides a
 * convenient method for getting a list of those objects.
 *
 */
public class StopwatchFactory {
	// List to store the Stopwatch objects.
	private static List<IStopwatch> watchList = new ArrayList<IStopwatch>();
	// Set to store Stopwatch IDs, use for checking duplicate.
	private static Set<String> idList = new HashSet<String>();
	
	/**
	 * Creates and returns a new IStopwatch object
	 * @param id The identifier of the new object
	 * @return The new IStopwatch object
	 * @throws IllegalArgumentException if <code>id</code> is empty, null, or already taken
	 */
	public synchronized static IStopwatch getStopwatch(String id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("ID cannot be null!");
		} else if (id == "") {
			throw new IllegalArgumentException("ID cannot be empty!");
		} 
		else {
			// Check whether ID exist in the set, if not, add it into the list.
			if (!idList.add(id)) {
				throw new IllegalArgumentException("ID has already exist!");
			} 
			else {
				Stopwatch sw = new Stopwatch(id);
				watchList.add(sw);
				return sw;
			}
		}
	}

	/**
	 * Returns a list of all created stopwatches
	 * @return a List of al creates IStopwatch objects.  Returns an empty
	 * list oi no IStopwatches have been created.
	 */
	public static List<IStopwatch> getStopwatches() {
		return watchList;
	}
}
