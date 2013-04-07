// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
/**
 * @author Hooman Mohajeri Moghaddam - added image load capability.
 *         Mohsen Mansouryar > added Loop support
 */
package graphlab.graph.graph;

import graphlab.graph.event.*;
import graphlab.graph.old.ArrowHandler;
import graphlab.graph.old.GShape;
import graphlab.graph.old.GStroke;
import graphlab.platform.Application;
import graphlab.platform.StaticUtils;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.preferences.lastsettings.StorableOnExit;
import graphlab.platform.preferences.lastsettings.UserModifiableProperty;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;


public class FastRenderer extends AbstractGraphRenderer implements VertexListener, EdgeListener, GraphControlListener, StorableOnExit {
    {
        SETTINGS.registerSetting(this, "Graph Drawings");
    }

    long lastpaintTime;
    @UserModifiableProperty(displayName = "Default Vertex Radius", obeysAncestorCategory = false, category = "Rendering Options")
    public static Integer defaultVertexRadius = 10;
    public int vertexRadius = defaultVertexRadius;
    private BlackBoard blackboard;

    private static GStroke markedStroke = GStroke.strong;
    @UserModifiableProperty(displayName = "Default Edge Stroke")
    public static GStroke defaultStroke = GStroke.solid;
    @UserModifiableProperty(displayName = "Default Vertex Shape")
    public static GShape defaultVertexShape = GShape.OVAL;
    @UserModifiableProperty(displayName = "Default Vertex Color")
    public static Color defaultVertexColor = new Color(116, 196, 255);
    @UserModifiableProperty(displayName = "Default Vertex Stroke")
    public static GStroke defaultBorderStroke = GStroke.dashed;
    @UserModifiableProperty(displayName = "Default Size of Vertices")
    public static Dimension defaultShapeDimension = new Dimension(20, 20);
    @UserModifiableProperty(displayName = "Default Edge Color")
    public static Color defaultEdgeColor = new Color(249, 117, 46);

    boolean drawVertexLabels;
    boolean drawEdgeLabels;
    boolean isGraphDirected;
    boolean isEdgesCurved;
    boolean isDirected;

    /**
     * a cached version of  GraphModel's zoomFactor
     */
    double zoomFactor;

    GraphControl control;
    private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 0, 0);

    public void setGraph(GraphModel g) {
        super.setGraph(g);
        for (Vertex v : g) {
            v.setVertexListener(this);
        }
        for (Iterator<Edge> ie = g.edgeIterator(); ie.hasNext();) {
            Edge e = ie.next();
            e.setEdgeListener(this);
        }
    }

    public FastRenderer(GraphModel g, BlackBoard blackboard) {
        super();
        this.blackboard = blackboard;
        lastpaintTime = System.currentTimeMillis();
        setGraph(g);
        control = new GraphControl(g, this, blackboard);
        control.setListener(this);
        setFocusable(true);
        setBackground(Color.white);
        setLayout(null);
        setBorder(null);
        updateGraphBounds();
    }

    boolean qpbc = true;
    public boolean forceQuickPaint = false;

    public void render(Graphics2D gg, Boolean drawExtras) {

        this.zoomFactor = getGraph().getZoomFactor();

        boolean quickPaint = forceQuickPaint || ((getGraph().getVerticesCount() + getGraph().getEdgesCount()) >= 500);
        /*this is for , if we want to the graph has a transparency over it's background image (if it has any one).
        // Get and install an AlphaComposite to do transparent drawing
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

        g.fillRect(100, 100, 100, 100);               // Start drawing with it

        */
//        if (qpbc != quickPaint) {
//            qpbc = quickPaint;
        if (quickPaint) {
            gg.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            gg.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            gg.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_SPEED);
            gg.setRenderingHint(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_DISABLE);
        } else {
            gg.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            gg.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            gg.setRenderingHint(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_ENABLE);
        }
        try {
            // Read from a file
        	 GraphModel gr = getGraph();

            gg.drawImage(gr.getBackgroundImage(),0,0,(int)(gr.getBackgroundImage().getWidth()*zoomFactor),(int)(gr.getBackgroundImage().getHeight()*zoomFactor),null);

        }
        catch(Exception e)
        {
        	
        }
       
       
        paintGraph(gg, drawExtras);
    }

    public void paintGraph(Graphics gg, Boolean drawExtras) {
        if (ignoreRapaints)
            return;
        if (updateBounds) {
            doUpdateGraphBounds();
        }
        if (isUpdated) {
            isUpdated = false;
            return;
        }
        //System.out.println("repaint");

        GraphModel graph = getGraph();
        gg.setFont(graph.getFont());
        boolean quickPaint = forceQuickPaint || (graph.getVerticesCount() + graph.getEdgesCount() >= 1000);
        drawVertexLabels = graph.isDrawVertexLabels();
        drawEdgeLabels = graph.isDrawEdgeLabels();
        isGraphDirected = graph.isDirected();
        isEdgesCurved = graph.isEdgesCurved();
        isDirected = graph.isDirected();

        if (minx < 0) {
            control.minx = minx;
        } else {
            control.minx = 0;
        }
        if (miny < 0) {
            control.miny = miny;
        } else
            control.miny = 0;

        if (quickPaint) {
            fastpaintGraph(gg, drawExtras);
        } else {
            nicepaintGraph(gg, drawExtras);
        }
    }

    public void nicepaintGraph(Graphics gg, Boolean drawExtras) {
        lastpaintTime = System.currentTimeMillis();
        Iterator<Edge> ie = getGraph().edgeIterator();
        try {
            while (ie.hasNext()) {
                Edge e = ie.next();
                paint((Graphics2D) gg, e, getGraph(), drawExtras);
            }
        } catch (Exception ex) {
            System.err.println("(FastPaint: Paint Error:");
            ex.printStackTrace();
            try {
                Thread.sleep(100);
                ie = getGraph().lightEdgeIterator();
                while (ie.hasNext()) {
                    Edge e = ie.next();
                    paint((Graphics2D) gg, e, getGraph(), drawExtras);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                StaticUtils.addExceptiontoLog(e1, Application.blackboard);
            }
//            repaint();
        }
        gg.setColor(Color.darkGray);

        try {
            for (Vertex v : getGraph()) {
                GraphPoint l = v.getLocation();
                gg.setColor(GraphModel.getColor(v.getColor()));
                String s = v.getLabel();
                if (s == null)
                    s = "";
                int dl = s.length() * 4;
                paint((Graphics2D) gg, v, zm(l.x) - v.getCenter().x, zm(l.y) - v.getCenter().y, zm(l.x) - dl, zm(l.y) + vertexRadius / 2, drawExtras);
            }
        } catch (Exception e) {
            repaint();
        }
    }

    public void fastpaintGraph(Graphics g, Boolean drawExtras) {
        Graphics2D gg = (Graphics2D) g;
        lastpaintTime = System.currentTimeMillis();
        Iterator<Edge> ie = getGraph().lightEdgeIterator();
        try {
            while (ie.hasNext()) {
                Edge e = ie.next();
                GraphPoint l, r;
                l = e.source.getLocation();
                r = e.target.getLocation();
                int ci = e.getColor();
                Color c;
                if (ci == 0)
                    c = defaultEdgeColor;
                else
                    c = GraphModel.getColor(ci);

                if (e.getMark()) {
                    c = Color.darkGray;
                }
                if (e.isSelected())
                    c = c.darker();

                gg.setColor(c);
                if (!e.isLoop()) {
                    gg.drawLine((int) (l.x * zoomFactor), (int) (l.y * zoomFactor), (int) (r.x * zoomFactor), (int) (r.y * zoomFactor));
                } else {
                   //Drawing loops
                   gg.drawOval(
                            zm(e.getLoopCenter().x - e.getLoopWidth()/2),
                            zm(e.getLoopCenter().y - e.getLoopWidth()/2),
                            zm(e.getLoopWidth()),
                            zm(e.getLoopWidth()));
                }
                if (isGraphDirected && !e.isLoop()) {
                    double angle = e.getAngle();

                    double t = Math.atan2(vertexRadius * Math.sin(angle), vertexRadius * Math.cos(angle));

                    GraphPoint loc = e.target.getLocation();
                    int x2 = (int) (loc.x + (vertexRadius / 2) * Math.cos(t));
                    int y2 = (int) (loc.y + (vertexRadius / 2) * Math.sin(t));

                    gg.translate(x2 * zoomFactor, y2 * zoomFactor);
                    gg.rotate(angle + Math.PI);

                    gg.drawPolyline(new int[]{-5, 0, -5}, new int[]{-4, 0, 4}, 3);
//                    ArrowHandler.defaultArrow.paintArrow(g, 15, 15);
                    gg.rotate(-angle - Math.PI);
                    gg.translate(-x2 * zoomFactor, -y2 * zoomFactor);
                }
                if (drawEdgeLabels) {
                    gg.setColor(c.darker().darker());
                    String s = e.text;
                    if (s != null) {
                        int dl = (s.length() / 2) * 5;
                        gg.drawString(s, (int) ((((l.x + r.x) / 2) * zoomFactor) - dl), (int) (((l.y + r.y) / 2) * zoomFactor));
                    }
                }
            }
        } catch (Exception e) {
            repaint();
        }
        gg.setColor(Color.darkGray);

        try {
            for (Vertex v : getGraph()) {
                GraphPoint l = v.getLocation();
                int ci = v.getColor();
                Color c;
                if (ci == 0)
                    c = defaultVertexColor;
                else
                    c = GraphModel.getColor(ci);

                if (v.isSelected())
                    c = c.darker();
                if (v.getMark())
                    c = Color.gray;

                gg.setColor(c);
                gg.fillOval((int) (l.x * zoomFactor) - vertexRadius / 2, (int) (l.y * zoomFactor) - vertexRadius / 2, vertexRadius, vertexRadius);

                if (drawVertexLabels) {
                    String s = v.getLabel();
                    if (s != null) {
                        int dl = (s.length() + 1 / 2) * 4;
                        gg.setColor(c.darker().darker());
                        GraphPoint ll = v.getLabelLocation();
                        gg.drawString(s, (int) ((l.x - dl + ll.x) * zoomFactor), (int) ((l.y + vertexRadius / 2 + ll.y) * zoomFactor));
                    }
                }
                gg.setColor(Color.DARK_GRAY);
            }
        } catch (Exception e) {
            repaint();
        }
    }


    public void paint(Graphics2D g, Edge model, GraphModel graph, Boolean drawExtras) {

            Color color;
            if (model.getColor() == 0)
                color = defaultEdgeColor;
            else
                color = GraphModel.getColor(model.getColor());
            BasicStroke stroke = model.getStroke().stroke;
            GraphPoint src = model.source.getLocation();
            GraphPoint trg = model.target.getLocation();

            if (model.isSelected()) {
                //            color = selectedColor;
                color = color.darker().darker();
            }
            if (model.getMark()) {
                //todo: omid: mark is boolean so we should have isMarked() in java definition.
//                color = markedColor;
                stroke = markedStroke.stroke;
            }
            Color c1, c2;
            c1 = color;
            c2 = color.darker().darker();

            double edgeLeft = Math.min(src.x, trg.x);
            double edgeTop = Math.min(src.y, trg.y);
            int d = GraphControl.EDGE_CURVE_CPNTROL_BOX_DIAMETER;
            GraphPoint ctrlPnt = model.getCurveControlPoint();
            double edgecenterx = (src.x + trg.x) / 2;
            int ctrlPntViewX = zm(edgecenterx + ctrlPnt.x);
            double edgecentery = (src.y + trg.y) / 2;
            int ctrlPntViewY = zm(edgecentery + ctrlPnt.y);
            int x1z = zm(src.x);
            int y1z = zm(src.y);
            int x2z = zm(trg.x);
            int y2z = zm(trg.y);
            if (!model.isLoop()) {
                if (isEdgesCurved && drawExtras) {
                    //draw control boxes
                    g.setColor(c1.darker());
                    g.fillOval(ctrlPntViewX - d / 2, ctrlPntViewY - d / 2, d, d);
                    //draw the edge line
                    g.setColor(c1);
                    g.setStroke(stroke);
                    if (ctrlPnt.x == 0 && ctrlPnt.y == 0) {
                        g.drawLine(x1z, y1z, x2z, y2z);
                    } else {
                        //the control point of the curve is put another place so that the curve hits the real control point.
                        QuadCurve2D.Double curve = new QuadCurve2D.Double(x1z, y1z, (4 * ctrlPntViewX - x1z - x2z) / 2, (4 * ctrlPntViewY - y1z - y2z) / 2, x2z, y2z);
                        g.draw(curve);
                    }

                } else {

                    g.setColor(c1);
                    g.setStroke(stroke);
                    g.drawLine(x1z, y1z, x2z, y2z);
                }

            } else {
            
                    if (isEdgesCurved && drawExtras) {
                        ctrlPntViewX = zm(ctrlPnt.x + src.x);
                        ctrlPntViewY = zm(ctrlPnt.y + src.y);
                        //draw control boxes
                        g.setColor(c1.darker());
                        g.fillOval(ctrlPntViewX - d / 2, ctrlPntViewY - d / 2, d, d);
                    }

                    // Drawing a circle as the loop
                    Ellipse2D.Double loop = new Ellipse2D.Double(
                            zm(model.getLoopCenter().x - model.getLoopWidth()/2),
                            zm(model.getLoopCenter().y - model.getLoopWidth()/2),
                            zm(model.getLoopWidth()),
                            zm(model.getLoopWidth()));

                    g.setColor(c1);
                    g.setStroke(stroke);
                    g.draw(loop);
            }

            if (isDirected) {
                if (!model.isLoop()) ArrowHandler.paint(g, graph, model, zoomFactor);
            }

            if (drawEdgeLabels) {
                if (model.text != null) {
                    g.setColor(c2);
                    //            int w = getWidth();
                    //            int h = getHeight();
                    GraphPoint ll = model.getLabelLocation();
                    if (isEdgesCurved)
                        g.drawString(model.text, ctrlPntViewX + zm(ll.x) + 2 * d, ctrlPntViewY + zm(ll.y) + 2 * d);
                    else
                        g.drawString(model.text, zm(edgecenterx + ll.x), zm(edgecentery + ll.y));
                }
            }
        }


    public void paint(Graphics2D g, Vertex model, int x, int y, int labelx, int labely, Boolean drawExtras) {
        GraphPoint size = model.getSize();
        int w = (int) size.x;
        int h = (int) size.y;
        Color color;
        if (model.getColor() == 0)
            color = defaultVertexColor;
        else
            color = GraphModel.getColor(model.getColor());
        BasicStroke borderStroke = model.shapeStroke.stroke;

        if (model.isSelected()) {
//                color = selectedColor;
            color = color.darker().darker();
        }
        if (model.getMark()) {
//            color = markedColor;
            borderStroke = markedStroke.stroke;
        }
        Color c2;
        c2 = color.darker().darker();

        g.setColor(color);
        g.setStroke(borderStroke);
        model.shape.fill(g, x, y, w, h);
//		if (!vertex.g.view.animation){
        g.setColor(c2);
//        if (model.showBorder)
        model.shape.draw(g, x, y, w - 1, h - 1);
//            if (labelSize.width == 0)
//                updateLabelSize();
//            g.drawString(model.getLabel(), w / 2 - labelSize.width / 2, h / 2 + labelSize.height / 2);  //dirty formula
        if (drawVertexLabels) {
            GraphPoint ll = model.getLabelLocation();
            String l = model.getLabel();
            if (l != null) {
                g.drawString(l, (int) (labelx + ll.x), (int) (labely + ll.y));  //dirty formula
            }
        }
    }

    public void vertexAdded(Vertex v) {
        if (v.getLabel() == null)
            v.setLabel(v.getId() + "");
        v.setVertexListener(this);
        isGraphChanged = true;
        repaint();
    }

    public void vertexRemoved(Vertex v) {
        isGraphChanged = true;
        repaint();
    }

    public void edgeAdded(Edge e) {
        e.setLabel(e.getId());
        e.setEdgeListener(this);
        isGraphChanged = true;
        repaint();
    }

    public void edgeRemoved(Edge e) {
        isGraphChanged = true;
        repaint();
    }

    public void repaintGraph() {
        updateGraphBounds();
        super.repaintGraph();
    }

    public void graphCleared() {
        updateGraphBounds();
        isGraphChanged = true;
        repaint();
    }


    public void repaint(Vertex src) {
        isGraphChanged = true;
        updateGraphBounds();
        repaint();
    }

    public void updateSize(Vertex src, GraphPoint newSize) {
        updateGraphBounds();
        isGraphChanged = true;
        repaint();
    }

    public void updateLocation(Vertex src, GraphPoint newLocation) {
        updateGraphBounds();
        isGraphChanged = true;
        repaint();
    }

    public void repaint(Edge src) {
        isGraphChanged = true;
        repaint();
    }

    public void updateBounds(Rectangle r, Edge src) {
        isGraphChanged = true;
//        repaint();
    }


    //graph control listener
    public void ActionPerformed(GraphEvent event) {
        blackboard.setData(GraphEvent.EVENT_KEY, event);
    }

    public void ActionPerformed(VertexEvent event) {
        blackboard.setData(VertexEvent.EVENT_KEY, event);
    }

    public void ActionPerformed(EdgeEvent event) {
        blackboard.setData(EdgeEvent.EVENT_KEY, event);
    }

    boolean updateBounds = false;

    private void updateGraphBounds() {
        updateBounds = true;
    }

    boolean isUpdated = false;

    private void doUpdateGraphBounds() {
        String s = ((String) blackboard.getData("MoveSelected.moving"));
        if (s != null && s.equals("yes")) {
            return;
        }
//        if (!updateBounds)
//            return;
//        updateBounds = false;
        Rectangle2D.Double prvb = bounds;
        bounds = getGraph().getZoomedBounds();
//        control.graphBounds = bounds;
//        if (!bounds.equals(prvb)) {

        int dx = 0;
        int dy = 0;
        if (bounds.x < 0) {
            if (bounds.x < prvb.x)

//                            System.out.println("x<0");
//                if (prvb.x >= 0) {
//                    dx = (int) -bounds.x;
//                } else {
                dx = (int) (prvb.x - bounds.x);
//                }
        }
        if (bounds.y < 0) {
            if (bounds.y < prvb.y)

//                            System.out.println("y<0");
//                if (prvb.y >= 0) {
//                    dy = (int) -bounds.y;
//                } else {
                dy = (int) (prvb.y - bounds.y);
//                }
        }

        int dw = 0, dh = 0;
        Dimension vvr = getSize();
        if (bounds.x + bounds.width > vvr.width) {
            dw = (int) (prvb.x + prvb.width - bounds.x + bounds.width);
        }
        if (bounds.y + bounds.height > vvr.height) {
            dh = (int) (prvb.y + prvb.height - bounds.y + bounds.height);
        }
        minx -= dx;
        miny -= dy;

        vvr.width += dx + dw;
        vvr.height += dy + dh;

        setIgnoreRepaint(true);
        ignoreRapaints = true;
        Rectangle vrect = getVisibleRect();
        vrect.x += dx;
        vrect.y += dy;

        setPreferredSize(vvr);
        revalidate();
        if (dx != 0 || dy != 0) {
            scrollRectToVisible(vrect);
            isUpdated = true;
        }

        revalidate();

        ignoreRapaints = false;
        setIgnoreRepaint(false);
//                System.out.println("size: " + vvr);
//                System.out.println("rect: " + vrect);
//                System.out.println("bounds: " + bounds);
//            System.out.println("1");

//commented for debugging purposes
//        } else {
//            if (!(bounds.x < 0 || bounds.y < 0)) {
////                            System.out.println("2");
//                Rectangle b = bounds.getBounds();
//                b.width += Math.abs(b.x);
//                b.height += Math.abs(b.y);
//                setPreferredSize(b.getSize());
//                revalidate();
//            }
//        }
        updateBounds = false;
        ignoreRapaints = false;
//        System.out.println("finished");
    }

    public void calculateSize() {
        Rectangle b = bounds.getBounds();
        if (bounds.x >= 0 && bounds.y >= 0) {
            b.width += Math.abs(b.x);
            b.height += Math.abs(b.y);
        } else {
            if (bounds.x < 0 && bounds.y >= 0) {

            }
        }
    }

    public int zm(double v) {
        return (int) (v * zoomFactor);
    }
}
