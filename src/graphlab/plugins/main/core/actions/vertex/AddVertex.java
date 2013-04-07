// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.main.core.actions.vertex;

import graphlab.graph.atributeset.GraphAttrSet;
import graphlab.graph.event.GraphEvent;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.GraphPoint;
import graphlab.graph.graph.SubGraph;
import graphlab.graph.graph.Vertex;
import graphlab.platform.core.AbstractAction;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.core.Listener;
import graphlab.plugins.main.select.ClearSelection;
import graphlab.plugins.main.select.Select;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Adds a vertex to the graph: listens for "graph select point" & ...
 *
 * @author azin azadi
 */

public class AddVertex extends AbstractAction {
    public final static String DISABLE = "AddVertex.Disable";

    public AddVertex(BlackBoard bb) {
        super(bb);
        listen4Event(GraphEvent.EVENT_KEY);
        blackboard.addListener(DISABLE, new Listener() {
            public void performJob(String name) {
                disable = (Boolean) blackboard.getData(DISABLE);
                if (disable)
                    disable();
                else
                    enable();
            }

            public void keyChanged(String key, Object value) {
                disable = (Boolean) blackboard.getData(DISABLE);
                if (disable)
                    disable();
                else
                    enable();
            }

            public boolean isEnable() {
                return true;
            }
        });
    }

    protected SubGraph sd;
    boolean disable = false;

    public void performAction(String key, Object value) {
        sd = Select.getSelection(blackboard);
        GraphModel g = blackboard.getData(GraphAttrSet.name);
        Boolean aBoolean = blackboard.getData(ClearSelection.lastTimeGraphWasClear);
        if (aBoolean == null)
            return;
        boolean b = (Boolean) aBoolean;
        if (b && (sd == null || (sd.vertices.size() == 0 && sd.edges.size() == 0))) {
            GraphEvent gpd = blackboard.getData(GraphEvent.EVENT_KEY);
            if (gpd.eventType != GraphEvent.CLICKED || gpd.mouseBtn != MouseEvent.BUTTON1) {
                return;
            }
            GraphModel graph = gpd.graph;

            Vertex v = doJob(graph, gpd.mousePos);
        }
        blackboard.setData(ClearSelection.lastTimeGraphWasClear, true);

    }

    /**
     * adds a vertex to the given location of graph
     *
     * @return the added vertex
     */
    public static Vertex doJob(GraphModel g, int x, int y) {
        Vertex v = new Vertex();
        Point p = v.getCenter();
        v.setLocation(new GraphPoint(x - p.x, y - p.y));
        g.insertVertex(v);
        return v;
    }

    /**
     * adds a vertex to the given location of graph
     *
     * @return the added vertex
     */
    public static Vertex doJob(GraphModel g, GraphPoint position) {
        Vertex v = new Vertex();
        v.setLocation(position);
        g.insertVertex(v);
        return v;
    }

    /**
     * adds a vertex to a random position of the graph
     * return the added vertex
     */
    public static Vertex addVertexToRandomPosition(GraphModel g) {
        Rectangle2D.Double b = g.getZoomedBounds();
        return doJob(g, (int) (b.getWidth() * Math.random()), (int) (b.getHeight() * Math.random()));
    }
}