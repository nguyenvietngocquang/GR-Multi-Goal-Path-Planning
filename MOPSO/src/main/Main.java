package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.PSO;
import gui.GUIRobotics;
import util.Graph;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		long time = System.currentTimeMillis();

		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(600, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> pointsToVisit = readPointData("visit.txt");

		ArrayList<Point> result = new ArrayList<Point>();

		try {
			PSO pso = new PSO(20, pointsToVisit.get(0), pointsToVisit.get(1), graph);
			pso.run();
			result.add(pointsToVisit.get(0));
			for (int j = 0; j < pso.result.size(); j++) {
				result.add(pso.result.get(j));
			}
			result.add(pointsToVisit.get(1));

			gui.canvas.drawLines(result, pointsToVisit);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}

		time = System.currentTimeMillis() - time;
		System.out.println("Time:\t" + time + " ms");
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
