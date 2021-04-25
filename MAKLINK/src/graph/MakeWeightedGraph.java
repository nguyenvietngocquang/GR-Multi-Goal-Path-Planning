package graph;

import java.util.LinkedList;

import algorithm.AStar;
import util.Graph;
import util.Line;
import util.Point;

public class MakeWeightedGraph {
	LinkedList<Point> visitPoints;
	public double[][] array;

	public MakeWeightedGraph(Graph myGraph, LinkedList<Point> visitPoints, LinkedList<Point> midPoints,
			LinkedList<Line> linesMAKLINK) {
		this.visitPoints = visitPoints;
		int size = visitPoints.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j)
					array[i][j] = 0;
				else {
					AStar findPath = new AStar(myGraph, midPoints, linesMAKLINK, visitPoints.get(i),
							visitPoints.get(j));
					array[i][j] = findPath.length;
					array[j][i] = findPath.length;
				}
			}
		}
	}
}
