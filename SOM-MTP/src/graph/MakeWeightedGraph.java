package graph;

import java.util.LinkedList;

import algorithm.AStar;
import util.Graph;
import util.Line;
import util.Neuron;
import util.Point;

public class MakeWeightedGraph {
	public double[][] neuronsToVisits;

	public MakeWeightedGraph(Graph myGraph, LinkedList<Point> visitPoints, LinkedList<Point> midPoints,
			LinkedList<Neuron> neurons, LinkedList<Line> lines) {
		this.neuronsToVisits = new double[neurons.size()][visitPoints.size()];
		for (int i = 0; i < neurons.size(); i++) {
			for (int j = 0; j < visitPoints.size(); j++) {
				AStar findPath = new AStar(myGraph, midPoints, lines, neurons.get(i), visitPoints.get(j));
				neuronsToVisits[i][j] = findPath.length;
			}
		}
	}

}
