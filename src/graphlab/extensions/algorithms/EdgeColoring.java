package graphlab.extensions.algorithms;

import graphlab.graph.graph.Edge;
import graphlab.graph.graph.GraphModel;
import graphlab.graph.graph.Vertex;
import graphlab.library.Path;
import graphlab.library.algorithms.util.EventUtils;
import graphlab.platform.core.BlackBoard;
import graphlab.plugins.algorithmanimator.core.GraphAlgorithm;
import graphlab.plugins.algorithmanimator.extension.AlgorithmExtension;
import graphlab.plugins.main.core.AlgorithmUtils;

import java.util.Random;
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

		Vector<Integer> alreadyColored = new Vector<Integer>();
		int currentColor = 1;
		int maxcolor = 1;
		for (Edge currentEdge : g.getEdges()) {
			Vector<Edge> neighbours = new Vector<Edge>();
			neighbours = this.getNeighbours(g, currentEdge);
			// System.out.println(currentEdge + " -> " + neighbours);

			for (int i = 1; i <= maxcolor; i++) {
				for (Edge edge : neighbours) {
					
					// System.out.println( edge.getId() +" : Color = " +
					// edge.getColor() + " & i = " +i + " max color = " +
					// maxcolor);
					if (edge.getColor() == i) {
						currentColor++;
						break;
					} else
						break;
				}
			}

			currentEdge.setColor(currentColor);
			if (currentColor > maxcolor)
				maxcolor = currentColor;
			currentColor = 1;
		}

		step("End");

	}

	@Override
	public String getName() {
		return "Edge Coloring of Graph";
	}

	@Override
	public String getDescription() {
		return "This algorithm will color the edges using fuzzy edge coloring algorithm";
	}

	private int getMaxDegree(GraphModel g) {
		Vertex[] vertices = g.getVertexArray();
		int max = g.getDegree(g.getVertex(0));
		for (Vertex vertex : vertices) {
			int mydegree = g.getDegree(vertex);
			if (mydegree > max)
				max = mydegree;
		}
		return max;
	}

	private Vector<Edge> getNeighbours(GraphModel g, Edge e) {
		Vector<Edge> result = new Vector<Edge>();
		for (Edge sourceEdge : g.edges(e.source))
			if (!result.contains(sourceEdge)
					& !e.getId().equals(sourceEdge.getId()))
				result.add(sourceEdge);
		for (Edge destEdge : g.edges(e.target))
			if (!result.contains(destEdge)
					& !e.getId().equals(destEdge.getId()))
				result.add(destEdge);
		return result;
	}

	private void edgeColoring(GraphModel g) {
		for (Edge currentEdge : g.edges()) {
			this.getNeighbours(g, currentEdge);
		}
	}

}