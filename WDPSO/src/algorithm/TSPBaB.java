package algorithm;

import java.util.LinkedList;

import util.Point;

public class TSPBaB {
	private int n, index = 1;
	private double cmin, can = 0;
	public double cost;
	private int test[], visited[], result[];
	public LinkedList<Point> answer = new LinkedList<Point>();
	private double[][] graph;

	public TSPBaB(double[][] graph, LinkedList<Point> points) {
		n = points.size();
		cost = 0;
		this.graph = graph;

		// make cost the biggest length in graph, cmin is min
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				if (graph[i][j] > cost)
					cost = graph[i][j];
			}

		cmin = cost;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				if (graph[i][j] < cmin && graph[i][j] > 0)
					cmin = graph[i][j];
			}
		cost *= n;

		// Check if visited
		visited = new int[n];
		visited[0] = 1;
		for (int i = 1; i < n; i++)
			visited[i] = 0;

		result = new int[n];
		for (int i = 0; i < n; i++)
			result[i] = 0;

		test = new int[n + 1];
		for (int i = 0; i < n + 1; i++)
			test[i] = 0;

		Try(1);
		for (int i = 0; i < n; i++) {
			answer.addLast(points.get(result[i]));
		}
	}

	// Branch and Bound
	private void Try(int i) {
		for (int j = 1; j < n; j++)
			if (visited[j] == 0) {
				test[index] = j;
				index++;
				visited[j] = 1;
				can += graph[test[i - 1]][test[i]];
				if (i == n - 1)
					takeResult(can, test, i);
				else if (can + (n - i) * cmin < cost)
					Try(i + 1);
				can -= graph[test[i - 1]][test[i]];
				visited[j] = 0;
				test[index] = 0;
				index--;
			}
	}

	// take results if satisfied
	private void takeResult(double can, int[] test, int k) {
		if (can <= cost || k == 1) {
			cost = can;
			for (int i = 0; i < n; i++)
				this.result[i] = test[i];
		}
	}

}
