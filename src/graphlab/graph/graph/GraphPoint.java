// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.graph.graph;

import graphlab.platform.lang.FromStringProvider;
import graphlab.platform.StaticUtils;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Scanner;

public class GraphPoint extends Point2D.Double implements Serializable, FromStringProvider {
    static {
        StaticUtils.setFromStringProvider(GraphPoint.class.getName(), new GraphPoint());
    }

    private static final long serialVersionUID = -1000000001L;

    public GraphPoint() {
        super();
    }

    public GraphPoint(GraphPoint p) {
        this.x = p.x;
        this.y = p.y;
    }

    public GraphPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * multiplies x and y by p (x=x*p, y=y*p)
     *
     * @param p
     */
    public void multiply(double p) {
        x = x * p;
        y = y * p;
    }

    /**
     * adds this with dp (x=x+dp.x, y=y+dp.y)
     *
     * @param p
     */
    public void add(GraphPoint dp) {
        x = x + dp.x;
        y = y + dp.y;
    }

    /**
     * adds this with dp (x=x+dx.x, y=y+dy)
     *
     * @return this
     */
    public GraphPoint add(double dx, double dy) {
        x = x + dx;
        y = y + dy;
        return this;
    }


    public String toString() {
        return x + " , " + y;
    }

    public double distance(GraphPoint pt) {
        double PX = pt.getX() - this.getX();
        double PY = pt.getY() - this.getY();
        return Math.sqrt(PX * PX + PY * PY);
    }

    public GraphPoint fromString(String data) {
        data = data.replace(',', ' ');
        Scanner _ = new Scanner(data);
        return new GraphPoint(_.nextDouble(), _.nextDouble());
    }
}
