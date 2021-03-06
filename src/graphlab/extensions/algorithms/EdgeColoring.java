package graphlab.extensions.algorithms;

import graphlab.graph.graph.Edge;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.Vertex;
import graphlab.library.algorithms.util.EventUtils;
import graphlab.platform.core.BlackBoard;
import graphlab.plugins.algorithmanimator.core.GraphAlgorithm;
import graphlab.plugins.algorithmanimator.extension.AlgorithmExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * author: Rakshit majithiya
 */

public class EdgeColoring extends GraphAlgorithm implements AlgorithmExtension {
    public EdgeColoring(BlackBoard blackBoard) {
        super(blackBoard);
    }

    @Override
    public void doAlgorithm() {
    	
    	
     	step("Edge Coloring of Graph");
        GraphModel g = graphData.getGraph();
        
        Vector <Integer> availableColors = new Vector<Integer>();
        int maxcolor = 1;
        Iterator<Edge> edges = g.edgeIterator();
        Edge currentEdge;
        while(edges.hasNext()){
        	currentEdge = edges.next();
        	l(currentEdge.getId());
        }
        edges = g.edgeIterator();
        step("NOW EDGE COLORING");
        while(edges.hasNext()){
        	currentEdge = edges.next();
        	

			highlightMe(currentEdge,true);
			l("---------------------------- EDGE "+currentEdge.source.getId()+ " -> "+ currentEdge.target.getId()+ " ---------------------------- ");
        	EventUtils.algorithmStep(this, "Current Edge : " + currentEdge.source.getId() + " -> "+ currentEdge.target.getId());
            boolean isAvailable = true;
        	Vector<Edge> neighbours = new Vector<Edge>();
        	neighbours = this.getNeighbours(g, currentEdge);
        	
        	
        	for(int i = 1; i<= maxcolor; i++)
        	{


        		for (Edge neighbour : neighbours)
        		{	
        			highlightMe(neighbour,true);
        			currentEdge.setColor(i);
        			
        			EventUtils.algorithmStep(this, "Comparing neighbour : " + neighbour.source.getId() + " -> " + neighbour.target.getId());
        			l("Comparing neighbour : " + neighbour.source.getId() + " -> " + neighbour.target.getId());
        			
        			if(neighbour.getColor() == i)
        			{
        				isAvailable = false;
        				highlightMe(neighbour,false);
        				currentEdge.setColor(0);
        				break;
        			}
        			else
        				isAvailable=true;
        			currentEdge.setColor(0);
        			
        			highlightMe(neighbour,false);
        		}
        		if(isAvailable){
        			l("Available color " + i);
        			availableColors.add(i);
        			isAvailable = true;
        			break;
        		}
        	}
        	if(!isAvailable){
        		l("new color " + (maxcolor+1));
        		maxcolor++;
        		availableColors.add(maxcolor);
        	}
        	
        	currentEdge.setColor(Collections.min(availableColors));
        	currentEdge.setWeight(Collections.min(availableColors));
        	currentEdge.setShowWeight(true);
        	
        	availableColors.removeAllElements();
        	

			highlightMe(currentEdge,false);
			l("---------------------------- ----------------------------\n\n\n\n");
        }
        
        step("EDGE CHROMATIC NUMBER : " + maxcolor);
    }

    private void highlightMe(Edge neighbour,boolean yesno) {
		neighbour.setMark(yesno);
		neighbour.source.setMark(yesno);
		neighbour.target.setMark(yesno);
	}

	@Override
    public String getName() {
        return "Edge Coloring of Graph";
    }

    @Override
    public String getDescription() {
        return "This algorithm will color the edges using fuzzy edge coloring algorithm";
    }
    private int getMaxDegree(GraphModel g){
    	Vertex[] vertices = g.getVertexArray();
    	int max = g.getDegree(g.getVertex(0));
        for (Vertex vertex : vertices) {
        	int mydegree = g.getDegree(vertex); 
        	if(mydegree> max)
        		max = mydegree;
		}
        return max;
    }
    private Vector<Edge> getNeighbours(GraphModel g, Edge e){
    	Vector<Edge> result = new Vector<Edge>();
    	for(Edge sourceEdge : g.edges(e.source))
    		if(!result.contains(sourceEdge) & !e.getId().equals(sourceEdge.getId()))
    			result.add(sourceEdge);
    	for(Edge destEdge : g.edges(e.target))
    		if(!result.contains(destEdge) & !e.getId().equals(destEdge.getId())) 
    			result.add(destEdge);
    	return result;
    }
    
    private void edgeColoring(GraphModel g){
    	for(Edge currentEdge: g.edges())
        {
        	this.getNeighbours(g, currentEdge);
        }
    }
    private void l(String msg){
    	System.out.println("DEBUG : " + msg);
    }
    
}