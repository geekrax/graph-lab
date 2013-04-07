// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU Lesser General Public License (LGPL): http://www.gnu.org/licenses/
package graphlab.extensions.generators;

import graphlab.graph.graph.Edge;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.GraphPoint;
import graphlab.graph.graph.Vertex;
import graphlab.platform.parameter.Parameter;
import graphlab.platform.parameter.Parametrizable;
import graphlab.platform.StaticUtils;
import graphlab.platform.lang.CommandAttitude;
import graphlab.plugins.graphgenerator.core.PositionGenerators;
import graphlab.plugins.graphgenerator.core.extension.GraphGeneratorExtension;

import java.awt.*;

@CommandAttitude(name = "generate_banana_tree", abbreviation = "_g_banana", description = "generates a Banana graph")
public class BananaTreeGenerator implements GraphGeneratorExtension, Parametrizable {

    //the depth should be positive, and also if it is very large the
    //generated graph is too large to generate.
    @Parameter(description = "N")
    public int n = 3;
    @Parameter(description = "K")
    public int k = 3;      //num of each star vertices


    public String checkParameters() {
    	if( n<0 || k<0 )return " Both k & n must be positive!";
    	else
    		return null;    //the parameters are well defined.
    }

    public String getName() {
        return "Banana  tree";
    }

    public String getDescription() {
        return "generates a banana tree with n k-stars";
    }

    public GraphModel generateGraph() {
        return generateBananaTree(n, k);
    }

    public static GraphModel generateBananaTree(int n, int k) {
        //num of tree vertices
        int t = (n * k) + 1;
        GraphModel g = new GraphModel(false);
        Vertex root = new Vertex();
        g.insertVertex(root);
        root.setLocation(new GraphPoint(0, 0));
        Vertex curv = null;
        //generating edges and setting positions
        Point[] fR = PositionGenerators.circle(3000, 0, 0, n);
        Vertex[] firstDepth = new Vertex[n];

        //generating first level vertices
        for (int i = 0; i < n; i++) {
            Point center = fR[i];
            curv = new Vertex();
            setloc(curv, center);
            firstDepth[i] = curv;
            g.insertVertex(curv);
            g.insertEdge(new Edge(root, curv));
        }

        //generating second level vertices
        for (int i = 0; i < n; i++) {
            Point center = fR[i];
            Vertex centerv = firstDepth[i];
            Point[] sR = PositionGenerators.circle(1000, center.x, center.y, k - 1);
            for (int j = 0; j < k - 1; j++) {
                curv = new Vertex();
                g.insertVertex(curv);
                setloc(curv, sR[j]);
                g.insertEdge(new Edge(centerv, curv));
            }
        }
        return g;
    }

    private static void setloc(Vertex vv, Point gp) {
        vv.setLocation(new GraphPoint(gp.x, gp.y));
    }


    public static void main(String[] args) {
        graphlab.platform.Application.main(args);
        StaticUtils.loadSingleExtension(BananaTreeGenerator.class);

    }
}