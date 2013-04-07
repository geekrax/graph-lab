// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.main.core.actions.vertex;

import graphlab.graph.atributeset.GraphAttrSet;
import graphlab.graph.event.VertexEvent;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.GraphPoint;
import graphlab.graph.graph.SubGraph;
import graphlab.platform.core.AbstractAction;
import graphlab.platform.core.BlackBoard;
import graphlab.plugins.main.select.Select;

/**
 * @author Ruzbeh
 */
public class VertexMoveEvent extends AbstractAction {
    public VertexMoveEvent(BlackBoard bb) {
        super(bb);
//        listen4Event(VertexDropData.event);
//        listen4Event(VertexMouseDraggingData.event);
        listen4Event(VertexEvent.EVENT_KEY);
    }

    GraphPoint oldPos;
    double x1;
    double y1;
    GraphModel g;

    public void performAction(String eventName, Object value) {
        g = blackboard.getData(GraphAttrSet.name);
        VertexEvent ve = blackboard.getData(VertexEvent.EVENT_KEY);
        //while resizing the vertex it shouldn't move it
        if (ve.eventType == VertexEvent.DRAGGING_STARTED) {
            vdd = blackboard.getData(VertexEvent.EVENT_KEY);
            drag();
        }
        if (ve.eventType == VertexEvent.DROPPED) {
            vdrod = blackboard.getData(VertexEvent.EVENT_KEY);
            drop();
        } else if (ve.eventType == VertexEvent.DRAGGING) {
            dragging();
        }
    }

    //it preserves the vertex selection consistant. if the vertex was selected before move it should be after and vice versa
    private boolean isVertexSelected;

    private void dragging() {
        VertexEvent vmd = blackboard.getData(VertexEvent.EVENT_KEY);
        GraphPoint _ = new GraphPoint(vmd.mousePos);
        GraphPoint loc = vmd.v.getLocation();
        _.x += loc.x - x1;
        _.y += loc.y - y1;
        vmd.v.setLocation(_);
        //System.out.println(vmd.me.getX()  - x1);
    }

    VertexEvent vdrod;

    private void drop() {
        VertexMoveData vmd = new VertexMoveData();
        vmd.v = vdd.v;
        GraphPoint _ = new GraphPoint(vdrod.mousePos);
        GraphPoint loc = vmd.v.getLocation();
        _.x += vdrod.mousePos.x + loc.x - x1;
        _.y += vdrod.mousePos.y + loc.y - y1;
        vmd.newPosition = _;
        blackboard.setData(VertexMoveData.EVENT_KEY, vmd);
        //it negatives the status of the vertex so , it will be negatived cause a vertex click will be fired after and the selection status will be negatived again, it's just a hack!
        //todo: the hack may cause problems for some one working with selections
        SubGraph SubGraph = Select.getSelection(blackboard);
        if (vmd.newPosition.x != oldPos.x && vmd.newPosition.y != oldPos.y) {
            if (isVertexSelected) {
                SubGraph.vertices.remove(vmd.v);
            } else
                SubGraph.vertices.add(vmd.v);
            blackboard.setData(Select.EVENT_KEY, SubGraph);
        }

    }

    VertexEvent vdd;

    private void drag() {
        oldPos = vdd.v.getLocation();// new Point(vdd.v.view.getX(), vdd.v.view.getY());
        //store the original position
        x1 = vdd.mousePos.x;
        y1 = vdd.mousePos.y;
        isVertexSelected = ((SubGraph) Select.getSelection(blackboard)).vertices.contains(vdd.v);
    }
}

