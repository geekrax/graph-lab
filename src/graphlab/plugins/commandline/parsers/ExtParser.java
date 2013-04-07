// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphlab.plugins.commandline.parsers;

/**
 * @author Mohammad Ali Rostami
 * @email ma.rostami@yahoo.com
 */

public interface ExtParser {
    String getName();

    String parse(String line);
}
