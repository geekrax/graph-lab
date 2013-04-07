// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.visualization.hierarchical;

import graphlab.graph.atributeset.GraphAttrSet;
import graphlab.graph.graph.Edge;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.Vertex;
import graphlab.platform.core.AbstractAction;
import graphlab.platform.core.BlackBoard;
import graphlab.plugins.visualization.corebasics.animator.GeneralAnimator;
import graphlab.ui.UIUtils;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Rouzbeh Ebrahimi
 */
public class BendedTrees extends AbstractAction {
    String event = UIUtils.getUIEventKey("BendedTrees");
    public Vector<Vertex> visitedVertices = new Vector<Vertex>();
    public HashMap<Vertex, Point2D> vertexPlaces = new HashMap<Vertex, Point2D>();
    public HashMap<Edge, Vector<Point2D>> edgeBendPoints = new HashMap<Edge, Vector<Point2D>>();
    public Vector<Vertex> children = new Vector<Vertex>();
    public GraphModel graph;

    /**
     * constructor
     *
     * @param bb the blackboard of the action
     */
    public BendedTrees(BlackBoard bb) {
        super(bb);
        listen4Event(event);
    }

    private Vertex findAppropriateRoot(GraphModel g) {
        Vertex root = g.getAVertex();
        Iterator<Vertex> ei = g.iterator();
        for (; ei.hasNext();) {
            Vertex e = ei.next();
            root = findHigherVertex(e, root);
        }
        return root;
    }

    private Vertex findHigherVertex(Vertex v1, Vertex v2) {
        Vector<Vertex> t1 = new Vector<Vertex>();
        Vector<Vertex> t2 = new Vector<Vertex>();
        t1.add(v1);
        t2.add(v2);
        if (BFS(new Vector<Vertex>(), t1, 0) > BFS(new Vector<Vertex>(), t2, 0)) {
            return v1;
        } else {
            return v2;
        }
    }

    private int BFS(Vector<Vertex> marked, Vector<Vertex> currentLevel, int maxLevel) {
        marked.addAll(currentLevel);
        Vector<Vertex> nextLevel = new Vector<Vertex>();
        for (Vertex v : currentLevel) {
            v.setMark(true);
            Iterator<Edge> em = g.edgeIterator(v);
            for (; em.hasNext();) {
                Edge e = em.next();
                Vertex v2 = e.source;
                if (!marked.contains(v2)) {
                    nextLevel.add(v2);
                }
            }
        }
        maxLevel++;
        if (nextLevel.size() != 0) {
            return BFS(marked, nextLevel, maxLevel);
        } else {
            return maxLevel;
        }
    }

    static GraphModel g;

    public void performAction(String eventName, Object value) {
        visitedVertices = new Vector<Vertex>();
        vertexPlaces = new HashMap<Vertex, Point2D>();
        children = new Vector<Vertex>();
        edgeBendPoints = new HashMap<Edge, Vector<Point2D>>();
        g = ((GraphModel) (blackboard.getData(GraphAttrSet.name)));
        try {
            this.graph = g;
            Vertex root = findAppropriateRoot(g);
            visitedVertices.add(root);
            locateAllVertices(visitedVertices, 800, 50);
            reshapeAllEdges();
            GeneralAnimator t = new GeneralAnimator(vertexPlaces, edgeBendPoints, g, blackboard);
            t.start();
        } catch (NullPointerException e) {
            System.out.println("Graph is Empty");
//            ExceptionHandler.catchException(e);
        }

    }

    public Vector<Vertex> findNextLevelChildren(Vector<Vertex> currentLevelVertices) {
        Vector<Vertex> newChildren = new Vector<Vertex>();
        for (Vertex v : currentLevelVertices) {
            Iterator<Edge> e = g.edgeIterator(v);
            for (; e.hasNext();) {
                Edge ed = e.next();
                Vertex dest = ed.source;
                if (!visitedVertices.contains(dest)) {
                    newChildren.add(dest);
                }
            }
        }
        return newChildren;
    }

    public void reshapeAllEdges() {
        Iterator<Edge> ei = graph.edgeIterator();
        for (; ei.hasNext();) {
            Edge e = ei.next();
            Point2D d1 = vertexPlaces.get(e.target);
            Point2D d2 = vertexPlaces.get(e.source);
            Vector<Point2D> bendPoints = new Vector<Point2D>();
            bendPoints.add(new Point2D.Double(d1.getX(), d1.getY() - 15));
            bendPoints.add(new Point2D.Double(d2.getX(), d2.getY() + 15));
            edgeBendPoints.put(e, bendPoints);
        }
    }

    public void locateAllVertices(Vector<Vertex> currentLevelVertices, int width, int currentLevelHeight) {
        int currentLevelCount = currentLevelVertices.size();
        int i = 0;

        Vector<Vertex> nextLevel = findNextLevelChildren(currentLevelVertices);
        int nextLevelCount = nextLevel.size();
        int horizontalDist = width / (currentLevelCount + nextLevelCount);


        for (Vertex v : currentLevelVertices) {
            if (nextLevelCount != 0) {
                Point2D.Double newPoint = new Point2D.Double(horizontalDist * (i + 1) + width / (nextLevelCount + currentLevelCount), currentLevelHeight);
                vertexPlaces.put(v, newPoint);
                i += g.getInDegree(v);
            } else {
                Point2D.Double newPoint = new Point2D.Double(horizontalDist * (i) + width / (currentLevelCount), currentLevelHeight);
                vertexPlaces.put(v, newPoint);
                i++;
            }
        }

        if (!nextLevel.isEmpty()) {
            visitedVertices.addAll(nextLevel);
            locateAllVertices(nextLevel, width, currentLevelHeight + 30);
        } else {
            return;
        }
    }
}
