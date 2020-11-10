
import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileInputStream;
import java.lang.Math;

public class SOM {

	private static String lattice; // Topology of the map
	private static int dimNumber; // Number of dimensions
	private static int verticalDim; // Number of dimensions of the vertical
	private static int horizontalDim; // Number of dimensions of the horizontal
	private static int neuronNumber; // Number of output neurons
	private static double[][] weight; // Vector weight
	private static double[][] location; // Location of neuron
	private static int epochNumber; // Number of epochs
	private static double learningRate; // Learning rate
	private static int neighborhoodRadius; // Radius of neighborhood
	private static int vectorNumber; // Number of input vectors
	private static double[][] vector; // Vector input
	private static ArrayList<Integer>[] cluster; // Cluster of vectors

	public static void main(String[] args) throws Exception {
		readData("iris.txt");

		generateWeight();

		som();

		clustering();

		computeQE();
	}

	// Read data from file
	public static void readData(String filename) throws Exception {
		Scanner sc = new Scanner(new FileInputStream(filename));

		// Line 1: Topology of the map
		lattice = sc.nextLine();

		// Line 2: Number of dimensions
		dimNumber = sc.nextInt();

		// Line 3: Number of dimensions of the vertical
		verticalDim = sc.nextInt();

		// Line 4: Number of dimensions of the horizontal
		horizontalDim = sc.nextInt();

		neuronNumber = verticalDim * horizontalDim;

		// Line 5: Number of epochs
		epochNumber = sc.nextInt();

		// Line 6: Learning rate
		learningRate = sc.nextDouble();

		// Line 7: Radius of neighborhood
		neighborhoodRadius = sc.nextInt();

		// Line 8: Number of input vectors
		vectorNumber = sc.nextInt();

		// Next vectorNumber lines: Each line is one input vector
		vector = new double[vectorNumber][dimNumber];
		for (int i = 0; i < vectorNumber; i++) {
			for (int j = 0; j < dimNumber; j++) {
				vector[i][j] = sc.nextDouble();
			}
		}
	}

	// Generate randomly vector weight
	public static void generateWeight() {
		weight = new double[neuronNumber][dimNumber];
		location = new double[neuronNumber][2];
		int yCounter = 0;
		int xCounter = 0;
		double xValue = 0;
		double yValue = 0;
		boolean evenRow = false; // For hexagonal lattice, checking if the current row number is even or odd
		if (lattice.equalsIgnoreCase("rect")) { // rectangles
			for (int i = 0; i < neuronNumber; i++) {
				for (int j = 0; j < dimNumber; j++) {
					weight[i][j] = Math.random();
				}
				if (xCounter < horizontalDim) {
					location[i][0] = xCounter;
					location[i][1] = yCounter;
					xCounter++;
				} else {
					xCounter = 0;
					yCounter++;
					location[i][0] = xCounter;
					location[i][1] = yCounter;
					xCounter++;
				}
			}
		} else { // hexagonal
			for (int i = 0; i < neuronNumber; i++) {
				for (int j = 0; j < dimNumber; j++) {
					weight[i][j] = Math.random();
				}
				if (xCounter < horizontalDim) {
					location[i][0] = xValue;
					location[i][1] = yValue;
					xValue += 1.0;
					xCounter++;
				} else {
					xCounter = 0;
					yValue += 0.866;
					if (evenRow) {
						xValue = 0.0;
						evenRow = false;
					} else {
						xValue = 0.5;
						evenRow = true;
					}
					location[i][0] = xValue;
					location[i][1] = yValue;
					xValue += 1.0;
					xCounter++;
				}
			}
		}
	}

	// SOM algorithm
	public static void som() {
		for (int epoch = 0; epoch < epochNumber; epoch++) {
			// Update the learning rate
			double rate = learningRate * Math.exp(-1.0 * epoch / epochNumber);

			for (int i = 0; i < vectorNumber; i++) {
				// Find the smallest Euclidean distance
				double[] distance = computeDistance(vector[i]);
				double minDistance = distance[0];
				int position = 0;
				for (int j = 1; j < neuronNumber; j++) {
					if (distance[j] < minDistance) {
						minDistance = distance[j];
						position = j;
					}
				}

				// Update the vector weight
				for (int j = 0; j < neuronNumber; j++) {
					for (int k = 0; k < dimNumber; k++) {
						weight[j][k] = weight[j][k]
								+ rate * neighborhoodFunction(epoch, position, j) * (vector[i][k] - weight[j][k]);
					}
				}
			}
		}
	}

	// Clustering vector input
	public static void clustering() {
		cluster = new ArrayList[neuronNumber];
		for (int i = 0; i < neuronNumber; i++) {
			cluster[i] = new ArrayList<Integer>();
		}

		for (int i = 0; i < vectorNumber; i++) {
			// Find the smallest Euclidean distance
			double[] distance = computeDistance(vector[i]);
			double minDistance = distance[0];
			int position = 0;
			for (int j = 1; j < neuronNumber; j++) {
				if (distance[j] < minDistance) {
					minDistance = distance[j];
					position = j;
				}
			}
			cluster[position].add(i);
		}

		// Print out cluster
		int count = 1;
		System.out.println("--- CLUSTER ---");
		for (int i = 0; i < neuronNumber; i++) {
			if (!cluster[i].isEmpty()) {
				System.out.println(count + ". Cluster " + i + ":\t" + cluster[i].size());
				count++;
			}
		}
		System.out.println("\n");
	}

	// Compute quantization error
	public static void computeQE() {
		double sumDistance = 0;
		for (int i = 0; i < neuronNumber; i++) {
			if (!cluster[i].isEmpty()) {
				for (int j = 0; j < cluster[i].size(); j++) {
					double distance = 0;
					for (int k = 0; k < dimNumber; k++) {
						distance += Math.pow(vector[cluster[i].get(j)][k] - weight[i][k], 2);
					}
					sumDistance += Math.sqrt(distance);
				}
			}
		}
		System.out.println("Quantization Error = " + sumDistance / vectorNumber);
	}

	// Compute Euclidean distance
	public static double[] computeDistance(double[] currentVector) {
		double[] distance = new double[neuronNumber];
		for (int j = 0; j < neuronNumber; j++) {
			distance[j] = 0;
			for (int k = 0; k < dimNumber; k++) {
				distance[j] += Math.pow(currentVector[k] - weight[j][k], 2);
			}
			distance[j] = Math.sqrt(distance[j]);
		}
		return distance;
	}

	// Compute neighborhood function
	public static double neighborhoodFunction(int epoch, int winnerNeuron, int currentNeuron) {
		double radius = neighborhoodRadius * Math.exp(-1.0 * epoch / epochNumber);
		double distance = 0;
		for (int i = 0; i < 2; i++) {
			distance += Math.pow(location[winnerNeuron][i] - location[currentNeuron][i], 2);
		}
		return Math.exp(-distance / (2 * Math.pow(radius, 2)));
	}

}
