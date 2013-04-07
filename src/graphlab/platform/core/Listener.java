// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.platform.core;

/**
 * Listener is a kind of listener that the Black board use to notify the action, when their events occurs.
 *
 * @author Azin Azadi
 */
public interface Listener {

    /**
     * Event occured, Go and call the listeners to do the Job
     *
     * @param key
     */
    public void keyChanged(String key, Object value);


}