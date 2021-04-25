package main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.SOM;
import gui.GUIRobotics;
import util.Graph;
import util.Neuron;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(600, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		LinkedList<Point> pointsToVisit = readPointData("visit.txt");

		try {
			SOM som = new SOM(graph, pointsToVisit, 50, "random");
			// Hien thi ket qua
			showResult(pointsToVisit, som, gui);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

	public static LinkedList<Point> readPointData(String filename) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.add(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();
		return pointsToVisit;
	}

	public static void showResult(LinkedList<Point> pointsToVisit, SOM som, GUIRobotics gui) {
		// Ve do thi
		for (Point point : pointsToVisit) {
			gui.canvas.drawPoint(point, Color.RED);
		}
		LinkedList<Point> neurons = new LinkedList<Point>();
		for (Neuron neuron : som.inhibited) {
			neurons.add(neuron);
		}
		gui.canvas.drawLines(som.path, neurons);
	}
}
