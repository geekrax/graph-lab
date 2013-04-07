// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.graphgenerator;

import graphlab.graph.event.GraphEvent;
import graphlab.graph.graph.*;
import graphlab.graph.ui.GTabbedGraphPane;
import graphlab.graph.ui.GraphRectRegionSelect;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.plugin.PluginMethods;
import graphlab.plugins.graphgenerator.core.GraphGeneratorInterface;
import graphlab.plugins.graphgenerator.core.SimpleGeneratorInterface;
import graphlab.extensions.generators.*;
import graphlab.plugins.main.core.CorePluginMethods;
import graphlab.plugins.main.core.actions.vertex.AddVertex;

import java.awt.*;

/**
 * @author azin azadi

 */
public class GraphGenerator implements PluginMethods {

    /**
     * first request a GUI rectangular region from user and then generates and adds a graph from gen to current working graph
     *
     * @param gen
     * @param blackboard
     */
    public static void generateInRectangularBounds(final GraphGeneratorInterface gen, final BlackBoard blackboard) {
//        long
        blackboard.setData(AddVertex.DISABLE, true);

        //select a region and then generate the graph
        GTabbedGraphPane.showNotificationMessage("<HTML><BODY><Center><b>Please select a region to put the generated graph.</b></center></body></html>", blackboard, true);
//        JOptionPane.showMessageDialog(null, "Please select a region to put the generated graph");
        new GraphRectRegionSelect(blackboard) {

            public void onMouseMoved(GraphEvent data) {
                //do nothing;
            }

            public void onDrop(GraphEvent data) {
//                final blackboard blackboard = blackboard;
                new Thread() {
                    public void run() {
                        synchronized (gv) {
                            generateGraph(gen, blackboard, rect);
                            GTabbedGraphPane.hideNotificationMessage(blackboard);
                        }
                    }
                }.start();
            }
        }.startSelectingRegion();
    }

    /**
     * generates and adds a graph from gen to current working graph
     *
     * @param gen
     * @param blackboard
     */
    public static void generateGraph(final GraphGeneratorInterface gen, final BlackBoard blackboard, final Rectangle rect) {
        //                            g.view.ignoreUpdates = true;
//                            blackboard blackboard = g.blackboard;
        final AbstractGraphRenderer ren = blackboard.getData(AbstractGraphRenderer.EVENT_KEY);
        if (rect.width < 5)
            rect.width = 250;
        if (rect.height < 5)
            rect.height = 250;
        new Thread() {
            public void run() {
                ren.ignoreRepaints(new Runnable() {
                    public void run() {
                        generateGraphInRect(blackboard, gen, rect);
                        blackboard.setData(AddVertex.DISABLE, false);
                    }
                });

            }
        }.start();
    }

    public static GraphModel generateGraphInRect(BlackBoard blackboard, GraphGeneratorInterface gen, Rectangle rect) {
        GTabbedGraphPane.showNotificationMessage("Generating Graph...", blackboard, true);
        GraphModel gg = CorePluginMethods.getGraph(blackboard);
        GraphModel g = gen.generateGraph();
        gg.addSubGraph(g, rect);

        GTabbedGraphPane.showTimeNotificationMessage("The Graph generated Completely: Vertices:" + g.getVerticesCount() + ", Edges:" + g.getEdgesCount(), blackboard, 4000, true);
        return g;

    }

    /**
     * generates and return a graph from the given interface, not showing it on GUI
     */
    public static GraphModel getGraph(boolean isDirected, SimpleGeneratorInterface gi) {
        GraphModel ret = new GraphModel(isDirected);
        gi.setWorkingGraph(ret);
        Vertex[] vertices = gi.getVertices();
        Point[] pos = gi.getVertexPositions();
        Edge[] edges = gi.getEdges();
        for (Vertex v : vertices)
            ret.insertVertex(v);
        for (int i = 0; i < vertices.length; i++)
            vertices[i].setLocation(new GraphPoint(pos[i].x, pos[i].y));
        for (Edge e : edges)
            ret.insertEdge(e);
        return ret;
    }

    //________________    Graph Generators    _______________
    /**
     * @see graphlab.extensions.generators.CircleGenerator#generateCircle(int)
     */
    public static GraphModel generateCircle(int n) {
        return CircleGenerator.generateCircle(n);
    }

    /**
     * @see graphlab.extensions.generators.CmnGenerator#generateCmn(int,int)
     */
    public static GraphModel generateCmn(int m, int n) {
        return CmnGenerator.generateCmn(m, n);
    }

    /**
     * @see graphlab.extensions.generators.GeneralizedPetersonGenerator#generateGeneralizedPeterson(int,int)
     */
    public static GraphModel generateGeneralizedPeterson(int n, int k) {
        return GeneralizedPetersonGenerator.generateGeneralizedPeterson(n, k);
    }

    /**
     * @see graphlab.extensions.generators.CompleteGraphGenerator#generateCompleteGraph(int)
     */
    public static GraphModel generateCompleteGraph(int n) {
        return CompleteGraphGenerator.generateCompleteGraph(n);
    }

    /**
     * @see graphlab.extensions.generators.KenserGraphGenerator#generateKenserGraph(int,int)
     */
    public static GraphModel generateKenserGraph(int n, int d) {
        return KenserGraphGenerator.generateKenserGraph(n, d);
    }


    /**
     * @see graphlab.extensions.generators.KmnGenerator#generateKmn(int,int)
     */
    public static GraphModel generateKmn(int m, int n) {
        return KmnGenerator.generateKmn(m, n);
    }

    /**
     * @see graphlab.extensions.generators.PathGenerator#generatePath(int)
     */
    public static GraphModel generatePath(int n) {
        return PathGenerator.generatePath(n);
    }

    /**
     * @see graphlab.extensions.generators.PmnGenerator#generatePmn(int,int)
     */
    public static GraphModel generatePmn(int m, int n) {
        return PmnGenerator.generatePmn(m, n);
    }

    /**
     * @see graphlab.extensions.generators.RandomGenerator#generateRandomGraph(int)
     */
    public static GraphModel generateRandomGraph(int n, int e) {
        return RandomGenerator.generateRandomGraph(n, e);
    }

    /**
     * @see graphlab.extensions.generators.StarGenerator#generateStar(int)
     */
    public static GraphModel generateStar(int n) {
        return StarGenerator.generateStar(n);
    }

    /**
     * @see graphlab.extensions.generators.TreeGenerator#generateTree(int,int)
     */
    public static GraphModel generateTree(int depth, int degree) {
        return TreeGenerator.generateTree(depth, degree);
    }

    /**
     * @see graphlab.extensions.generators.WheelGenerator#generateWheel(int)
     */
    public static GraphModel generateWheel(int n) {
        return WheelGenerator.generateWheel(n);
    }


}

