package main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import algorithm.AStar;
import algorithm.SOM;
import graph.GraphDivision;
import util.Graph;
import util.Point;

public class Test {
	static Random rd = new Random();

	public static void main(String[] args) throws IOException {
//		Graph graph = new Graph("obstacles.txt");
//		Scanner scan = new Scanner(new File("visit.txt"));
//		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
//		LinkedList<Point> result = new LinkedList<Point>();
//		double x = scan.nextDouble();
//		while (x != -1) {
//			double y = scan.nextDouble();
//			pointsToVisit.addLast(new Point(x, y));
//			x = scan.nextDouble();
//		}
//		scan.close();
//		SOM som = new SOM(graph, pointsToVisit, 15);
//		GraphDivision graphDivision = new GraphDivision(graph, pointsToVisit, som.ring.neurons);
//		AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, pointsToVisit.get(0),
//				pointsToVisit.get(1));
//		for (int i = 0; i < findPath.path.size(); i++) {
//			findPath.path.get(i).printPoint();
//		}
	}

}
