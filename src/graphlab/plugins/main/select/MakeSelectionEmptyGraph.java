// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphlab.plugins.main.select;

import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.Vertex;
import graphlab.plugins.main.GraphData;

import java.util.HashSet;

/**
 * @author Azin Azadi
 */
public class MakeSelectionEmptyGraph extends MakeSelectionComplementGraph {
    public String getName() {
        return "Make Selection Empty";
    }

    public String getDescription() {
        return "Make the selected subgraph an empty graph";
    }

    public void action(GraphData gd) {
        if (gd.select.isSelectionEmpty())
            return;
        HashSet<Vertex> V = gd.select.getSelectedVertices();
        //add undo data
        GraphModel G = gd.getGraph();

        for (Vertex v1 : V) {
            for (Vertex v2 : V) {
//                if (v1.getId() < v2.getId()) {
//                    if (G.isEdge(v1, v2))
//                        G.insertEdge(new Edge(v1, v2));
//                    else
                G.removeAllEdges(v1, v2);
//                }
            }
        }
        gd.select.clearSelection();
    }
}
