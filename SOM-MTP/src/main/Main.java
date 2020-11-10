package main;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.AStar;
import algorithm.SOM;
import graph.GraphDivision;
import graph.MakeWeightedGraph;
import gui.GUIRobotics;
import util.Graph;
import util.Line;
import util.Neuron;
import util.Obstacle;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		Scanner scan = new Scanner(new File("visit.txt"));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		LinkedList<Point> result = new LinkedList<Point>();
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();

		try {
			SOM som = new SOM(graph, pointsToVisit, 2);
			for (Point point : pointsToVisit) {
				gui.canvas.drawPoint(point, Color.GREEN);
			}
			for (Neuron neuron : som.ring.neurons) {
				gui.canvas.drawPoint(neuron);
				gui.canvas.drawLine(neuron, neuron.next, Color.BLACK);
			}

//			// Ve do thi
//			System.setIn(new FileInputStream("neurons.txt"));
//			Scanner sc = new Scanner(System.in);
//
//			sc.nextLine();
//			String string = sc.nextLine();
//			while (!string.equals("-1")) {
//
//				String numbers[] = string.replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").split("\\s+");
//
//				ArrayList<Point> points = new ArrayList<>();
//				for (int i = 0; i < numbers.length / 2; i++) {
//					points.add(new Point(Double.parseDouble(numbers[2 * i]), Double.parseDouble(numbers[2 * i + 1])));
//				}
//
//				for (int i = 0; i < points.size() / 2; i++) {
//					gui.canvas.drawLinesWithMiddle(points.get(2 * i), points.get(2 * i + 1));
//				}
//
//				string = sc.nextLine();
//			}
//			sc.close();

//			GraphDivision graphDivision = new GraphDivision(graph, pointsToVisit, som.ring.neurons);
//
//			MakeWeightedGraph makeGraph = new MakeWeightedGraph(graph, pointsToVisit, graphDivision.midPoints,
//					som.ring.neurons, graphDivision.MAKLINK);
//
//			for (int i = 0; i < som.ring.size(); i++) {
//				gui.canvas.drawLine(som.ring.get(i), som.ring.get(i).next, Color.BLACK);
//			}
//			for (Line line : graphDivision.MAKLINK) {
//				gui.canvas.drawLine(line.firstPoint, line.secondPoint, Color.BLUE);
//			}

			// Luu cac canh phan ra moi truong
			File f = new File("maklink.txt");
			FileWriter fw = new FileWriter(f);
			fw.write("lines\n");
			for (Line line : som.graphDivision.lines) {
				fw.write("(" + line.firstPoint.x + ", " + line.firstPoint.y + ") (" + line.secondPoint.x + ", "
						+ line.secondPoint.y + ")\n");
			}
			fw.write("-1");
			fw.close();

//			ArrayList<Obstacle.Point> points = new ArrayList<>();
//			for (Point pt : result) {
//				Obstacle.Point point = new Obstacle.Point(pt.x, pt.y);
//				points.add(point);
//			}
//
//			f = new File("path.txt");
//			fw = new FileWriter(f);
//			fw.write("Path to follow:\n");
//			for (Point p : result) {
//				fw.write("(" + p.x + ", " + p.y + ") ");
//			}
//			fw.write("\n-1");
//			fw.close();
//			gui.canvas.drawLines(points, pointsToVisit);
//			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("End!");
	}
}
