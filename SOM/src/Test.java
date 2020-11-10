
import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileInputStream;
import java.lang.Math;

public class Test {

	private static int neuronNumber; // Number of output neurons
	private static double[][] weight; // Vector weight
	private static int epochNumber; // Number of epochs
	private static double learningRate; // Learning rate
	private static int neighborhoodRadius; // Radius of neighborhood
	private static int vectorNumber; // Number of input vectors
	private static double[][] vector; // Vector input
	private static ArrayList<Double[]> inhibit;

	public static void main(String[] args) throws Exception {
		readData("iris.txt");

		generateWeight();

		som();

		for (int i = 0; i < neuronNumber; i++) {
			System.out.println(weight[i][0] + "\t" + weight[i][1]);
		}
	}

	// Read data from file
	public static void readData(String filename) throws Exception {
		Scanner sc = new Scanner(new FileInputStream(filename));

		// Line 1: Number of epochs
		epochNumber = sc.nextInt();

		// Line 2: Learning rate
		learningRate = sc.nextDouble();

		// Line 3: Radius of neighborhood
		neighborhoodRadius = sc.nextInt();

		// Line 4: Number of input vectors
		vectorNumber = sc.nextInt();

		neuronNumber = vectorNumber;

		// Next vectorNumber lines: Each line is one input vector
		vector = new double[vectorNumber][2];
		for (int i = 0; i < vectorNumber; i++) {
			for (int j = 0; j < 2; j++) {
				vector[i][j] = sc.nextDouble();
			}
		}
	}

	// Generate randomly vector weight
	public static void generateWeight() {
		weight = new double[neuronNumber][2];
		for (int i = 0; i < neuronNumber; i++) {
			for (int j = 0; j < 2; j++) {
				weight[i][j] = Math.random() * 2;
			}
		}
	}

	// SOM algorithm
	public static void som() {
		for (int epoch = 0; epoch < epochNumber; epoch++) {
			inhibit = new ArrayList<Double[]>();
			for (int i = 0; i < neuronNumber; i++) {
				inhibit.add(new Double[2]);
				inhibit.get(i)[0] = weight[i][0];
				inhibit.get(i)[1] = weight[i][1];
			}

			// Update the learning rate
			double rate = learningRate * Math.exp(-1.0 * epoch / epochNumber);

			for (int i = 0; i < vectorNumber; i++) {
				// Find the smallest Euclidean distance
				double[] distance = computeDistance(vector[i], inhibit);
				double minDistance = distance[0];
				int position = 0;
				for (int j = 1; j < inhibit.size(); j++) {
					if (distance[j] < minDistance) {
						minDistance = distance[j];
						position = j;
					}
				}

				// Update the vector weight
				for (int j = 0; j < inhibit.size(); j++) {
					for (int k = 0; k < 2; k++) {
						weight[j][k] = weight[j][k]
								+ rate * neighborhoodFunction(epoch, position, j) * (vector[i][k] - weight[j][k]);
					}
				}

				inhibit.remove(position);
			}
		}
	}

	// Compute Euclidean distance
	public static double[] computeDistance(double[] currentVector, ArrayList<Double[]> inhibit) {
		double[] distance = new double[inhibit.size()];
		for (int j = 0; j < inhibit.size(); j++) {
			distance[j] = 0;
			for (int k = 0; k < 2; k++) {
				distance[j] += Math.pow(currentVector[k] - inhibit.get(j)[k], 2);
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
			distance += Math.pow(weight[winnerNeuron][i] - weight[currentNeuron][i], 2);
		}
		return Math.exp(-distance / (2 * Math.pow(radius, 2)));
	}

}
