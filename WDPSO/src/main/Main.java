package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.PSO;
import algorithm.TSPBaB;
import graph.GraphDivision;
import graph.MakeWeightedGraph;
import gui.GUIRobotics;
import util.Graph;
import util.Point;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		long time = System.currentTimeMillis();
		double length = 0;

		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(600, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> pointsToVisit = readPointData("visit.txt");

		ArrayList<Point> result = new ArrayList<Point>();

		try {
			GraphDivision graphDivision = new GraphDivision(graph, pointsToVisit);
			MakeWeightedGraph makeGraph = new MakeWeightedGraph(graph, pointsToVisit, graphDivision.midPoints,
					graphDivision.MAKLINK);

			TSPBaB TSP = new TSPBaB(makeGraph.weights, pointsToVisit);
			for (int i = 0; i < TSP.answer.size(); i++) {
				// Start PSO
				PSO pso;
				if (i != TSP.answer.size() - 1) {
					pso = new PSO(10, TSP.answer.get(i), TSP.answer.get(i + 1), graph);
				} else {
					pso = new PSO(10, TSP.answer.get(i), TSP.answer.get(0), graph);
				}
				pso.run();
				length += pso.length;
				result.add(TSP.answer.get(i));
				for (int j = 0; j < pso.result.size(); j++) {
					result.add(pso.result.get(j));
				}
				// End PSO
			}
			result.add(TSP.answer.get(0));
			System.out.println("Length:\t" + length);

			gui.canvas.drawLines(result, pointsToVisit);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}

		time = System.currentTimeMillis() - time;
		System.out.println("Time:\t" + time / 1000F + "s");
		System.out.println("Done!");
	}

	public static LinkedList<Point> readPointData(String filename) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();

		return pointsToVisit;
	}
}
