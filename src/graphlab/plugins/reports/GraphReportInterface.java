// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.reports;

import graphlab.plugins.main.GraphData;

/**
 * @author azin azadi

 */
public interface GraphReportInterface<t> {
    public t calculate(GraphData gd);

    /**
    * return the category of report like: connectivity, general, coloring, ...
    **/
    public String getCategory();
}
