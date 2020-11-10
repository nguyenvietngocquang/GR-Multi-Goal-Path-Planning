package algorithm;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import graph.GraphDivision;
import graph.MakeWeightedGraph;
import gui.GUIRobotics;
import util.Graph;
import util.Neuron;
import util.Point;
import util.Ring;

public class SOM {

	public Ring ring;
	public Graph graph;
	public GraphDivision graphDivision;
	public MakeWeightedGraph makeGraph;
	public LinkedList<Point> visit = new LinkedList<Point>();
	int epochs;
	double learningRate = 0.6;
	final double maxError = 0.001; // Maximal allowable error
	int n; // Number of nodes
	double G; // The gain parameter
	int m; // Number of neighbors

	Random rd = new Random();

	public SOM(Graph graph, LinkedList<Point> visit, int epochs) throws IOException {
		this.graph = graph;
		this.visit = visit;
		this.epochs = epochs;
		this.n = (int) (visit.size() * 3);
		this.G = 0.06 + 12.41 * n;
		this.m = (int) (0.2 * n);

		createRingOfNeurons();
		somAlgorithm();
		saveNeurons();
	}

	public void createRingOfNeurons() {
		this.ring = new Ring();

//		// Trung binh toa do x, y cua cac neuron
//		double meanX = 0, meanY = 0;
//		Neuron[] neurons = new Neuron[n];
//		for (int i = 0; i < n; i++) {
//			Neuron neuron;
//			do {
//				neuron = new Neuron((int) (rd.nextDouble() * 10000) / 100.0, (int) (rd.nextDouble() * 10000) / 100.0);
//			} while (neuron.isIntersectGraph(graph));
//			meanX += neuron.x;
//			meanY += neuron.y;
//			neurons[i] = neuron;
//		}
//		meanX /= n;
//		meanY /= n;
//
//		// Tinh cac goc qua arctan
//		double[] angles = new double[n];
//		for (int i = 0; i < n; i++) {
//			angles[i] = Math.atan2(neurons[i].y - meanY, neurons[i].x - meanX);
//		}
//		double[] temp = angles.clone();
//		// Sap xep theo goc
//		Arrays.sort(angles);
//
//		int position = indexOf(temp, angles[0]);
//		Neuron neuron = new Neuron(neurons[position].x, neurons[position].y);
//		ring.rightInsert(neuron, null);
//
//		for (int i = 1; i < n; i++) {
//			position = indexOf(temp, angles[i]);
//			neuron = new Neuron(neurons[position].x, neurons[position].y);
//			ring.rightInsert(neuron, ring.neurons.get(i - 1));
//		}

		Neuron[] neuron = new Neuron[24];
//		neuron[0] = new Neuron(38, 15);
//		neuron[1] = new Neuron(40, 16);
//		neuron[2] = new Neuron(42, 15);
//		neuron[3] = new Neuron(46, 15);
//		neuron[4] = new Neuron(48, 14);
//		neuron[5] = new Neuron(50, 15);
//		neuron[6] = new Neuron(52, 20);
//		neuron[7] = new Neuron(52, 22);
//		neuron[8] = new Neuron(52, 24);
//		neuron[9] = new Neuron(54, 28);
//		neuron[10] = new Neuron(54, 30);
//		neuron[11] = new Neuron(54, 33);
//		neuron[12] = new Neuron(54, 38);
//		neuron[13] = new Neuron(50, 40);
//		neuron[14] = new Neuron(50, 45);
//		neuron[15] = new Neuron(48, 50);
//		neuron[16] = new Neuron(48, 55);
//		neuron[17] = new Neuron(40, 55);
//		neuron[18] = new Neuron(36, 50);
//		neuron[19] = new Neuron(36, 45);
//		neuron[20] = new Neuron(34, 40);
//		neuron[21] = new Neuron(34, 35);
//		neuron[22] = new Neuron(36, 28);
//		neuron[23] = new Neuron(38, 20);
		
		neuron[0] = new Neuron(10, 100);
		neuron[1] = new Neuron(5, 80);
		neuron[2] = new Neuron(9, 60);
		neuron[3] = new Neuron(0, 40);
		neuron[4] = new Neuron(10, 30);
		neuron[5] = new Neuron(10, 15);
		neuron[6] = new Neuron(15, 0);
		neuron[7] = new Neuron(30, 2);
		neuron[8] = new Neuron(40, 8);
		neuron[9] = new Neuron(60, 5);
		neuron[10] = new Neuron(80, 6);
		neuron[11] = new Neuron(95, 5);
		neuron[12] = new Neuron(100, 10);
		neuron[13] = new Neuron(95, 25);
		neuron[14] = new Neuron(90, 40);
		neuron[15] = new Neuron(95, 60);
		neuron[16] = new Neuron(100, 80);
		neuron[17] = new Neuron(95, 95);
		neuron[18] = new Neuron(85, 100);
		neuron[19] = new Neuron(70, 90);
		neuron[20] = new Neuron(55, 98);
		neuron[21] = new Neuron(40, 100);
		neuron[22] = new Neuron(30, 100);
		neuron[23] = new Neuron(20, 98);
		
		
		ring.rightInsert(neuron[0], null);
		for (int i = 1; i < 24; i++) {
			ring.rightInsert(neuron[i], ring.get(i - 1));
		}
		
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
		gui.generateEnvironment("obstacles.txt");
		
		for (Neuron neuron1 : ring.neurons) {
			gui.canvas.drawPoint(neuron1);
			gui.canvas.drawLine(neuron1, neuron1.next, Color.BLACK);
		}

	}

	public void somAlgorithm() {
		for (int epoch = 0; epoch < epochs; epoch++) {
			double error = 0;
			// Update the learning rate
			double rate = learningRate * Math.exp(-1.0 * epoch / epochs);

			// A set of inhibited neurons
			LinkedList<Neuron> inhibited = new LinkedList<Neuron>();

			// A random permutation of visit points
			Collections.shuffle(visit);

			this.graphDivision = new GraphDivision(graph, visit, ring.neurons);
//			this.makeGraph = new MakeWeightedGraph(graph, visit, graphDivision.midPoints, ring.neurons,
//					graphDivision.MAKLINK);

			for (int i = 0; i < visit.size(); i++) {
				// Find the smallest Euclidean distance
				int index = 0;
				while (inhibited.contains(ring.get(index))) {
					index++;
				}
				AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, ring.get(index),
						visit.get(i));
				double distance = findPath.length;
				int position = index;
				for (int j = 0; j < ring.size(); j++) {
					if (!inhibited.contains(ring.get(j))) {
						findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, ring.get(j),
								visit.get(i));
						if (findPath.length < distance) {
							distance = findPath.length;
							position = j;
						}
					}
				}
				inhibited.add(ring.get(position));

				// Update the winner neuron weight
				error = Math.max(error,
						updateWeight(ring.get(position), ring.get(position), visit.get(i), rate, epoch));

				// Update the neighbor neuron weight
				Neuron prevNeuron = ring.get(position);
				Neuron nextNeuron = ring.get(position);
				for (int j = 0; j < m; j++) {
					prevNeuron = prevNeuron.prev;
					nextNeuron = nextNeuron.next;
					error = Math.max(error, updateWeight(ring.get(position), prevNeuron, visit.get(i), rate, epoch));
					error = Math.max(error, updateWeight(ring.get(position), nextNeuron, visit.get(i), rate, epoch));
				}

				// Update graph division
				this.graphDivision = new GraphDivision(graph, visit, ring.neurons);
			}

			if (error <= maxError)
				break;
		}
	}

	public void saveNeurons() throws IOException {
		File f = new File("neurons.txt");
		FileWriter fw = new FileWriter(f);
		fw.write("List of neurons:\n");
		for (Neuron neuron : ring.neurons) {
			fw.write("(" + neuron.x + ", " + neuron.y + ")\n");
		}
		fw.write("-1");
		fw.close();
	}

	public int indexOf(double arr[], double value) {
		for (int i = 0; i < arr.length; i++) {
			if (value == arr[i])
				return i;
		}
		return -1;
	}

	// Compute neighborhood function
	public double neighborhoodFunction(int epoch, Neuron winnerNeuron, Neuron currentNeuron) {
		double radius = 4 * Math.exp(-1.0 * epoch / epochs);
		double distance = winnerNeuron.distanceFrom(currentNeuron);
		return Math.exp(-Math.pow(distance, 2) / (2 * Math.pow(radius, 2)));
	}

	// Update the neuron weight
	public double updateWeight(Neuron winnerNeuron, Neuron currentNeuron, Point visitPoint, double rate, int epoch) {
		AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, currentNeuron, visitPoint);
		// Distance to move
		double move = rate * neighborhoodFunction(epoch, winnerNeuron, currentNeuron) * findPath.length;
		double temp = move;

		int pathCount = findPath.path.size() - 1;
		int i = 0;
		while (i < pathCount) {
			Point Point1 = findPath.path.get(i);
			Point Point2 = findPath.path.get(i + 1);
			double length = Point1.distanceFrom(Point2);
			if (length > move) {
				// Update the coordinates
				double theta = Math.atan2(Point2.y - Point1.y, Point2.x - Point1.x);
				currentNeuron.x = Point1.x + move * Math.cos(theta);
				currentNeuron.y = Point1.y + move * Math.sin(theta);
				break;
			}
			move -= length;
			i++;
		}

		return temp;
	}

}
