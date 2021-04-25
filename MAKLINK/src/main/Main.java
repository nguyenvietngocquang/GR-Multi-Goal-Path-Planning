package main;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.AStar;
import graph.GraphDivision;
import gui.GUIRobotics;
import util.Graph;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(600, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> pointsToVisit = readPointData("visit.txt");
		GraphDivision graphDivision = new GraphDivision(graph, pointsToVisit);

		long time = System.currentTimeMillis();

		try {
			AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, pointsToVisit.get(0),
					pointsToVisit.get(4));
			System.out.println("Length:\t" + findPath.length);
			
			gui.canvas.drawLines(findPath.path, pointsToVisit);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}

		time = System.currentTimeMillis() - time;
		System.out.println("Time:\t" + time / 1000F + "s");

		System.out.println("End!");
	}

	public static LinkedList<Point> readPointData(String filename) throws IOException {
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
