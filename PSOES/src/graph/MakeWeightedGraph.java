package graph;

import java.util.LinkedList;

import algorithm.AStar;
import util.Graph;
import util.Line;
import util.Point;

public class MakeWeightedGraph {
	public double[][] weights;

	public MakeWeightedGraph(Graph myGraph, LinkedList<Point> visitPoints, LinkedList<Point> midPoints,
			LinkedList<Line> lines) {
		this.weights = new double[visitPoints.size()][visitPoints.size()];
		for (int i = 0; i < visitPoints.size(); i++) {
			for (int j = 0; j < visitPoints.size(); j++) {
				AStar findPath = new AStar(myGraph, midPoints, lines, visitPoints.get(i), visitPoints.get(j));
				weights[i][j] = findPath.length;
			}
		}
	}

}
