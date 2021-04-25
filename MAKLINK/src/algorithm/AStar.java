package algorithm;

import java.util.Collections;
import java.util.LinkedList;

import util.Graph;
import util.Line;
import util.Point;

public class AStar {
	public LinkedList<Point> path = new LinkedList<Point>();
	public double length;

	// Store graph into weighted array
	public double[][] GraphArray(LinkedList<Point> points, LinkedList<Line> lines) {
		double graph[][] = new double[points.size()][points.size()];

		for (Line line : lines) {
			int a = line.firstPoint.indexInSet(points);
			int b = line.secondPoint.indexInSet(points);
			graph[a][b] = line.getLength();
			graph[b][a] = line.getLength();
		}

		for (int i = 0; i < points.size(); i++)
			for (int j = 0; j < points.size(); j++) {
				if (i == j)
					graph[i][j] = 0;
				else if (graph[i][j] <= 0)
					graph[i][j] = -1;
			}

		return graph;
	}

	public AStar(Graph myGraph, LinkedList<Point> points, LinkedList<Line> linesMAKLINK, Point start, Point end) {
		double[][] graph = GraphArray(points, linesMAKLINK);
		double[] distance = new double[points.size()]; // Shortest Distance from start
		for (int i = 0; i < points.size(); i++) {
			if (i != start.indexInSet(points))
				distance[i] = -1;
			else
				distance[i] = 0;
		}
		int n = points.size();
		double[] h_value = new double[points.size()]; // heuristic value
		int[] visited = new int[points.size()];
		Point[] prev = new Point[points.size()]; // previous point
		LinkedList<Point> open = new LinkedList<Point>();

		for (int i = 0; i < n; i++) {
			h_value[i] = points.get(i).distanceFrom(end);
			visited[i] = 0;
		}

		open.addFirst(start);
		while (visited[end.indexInSet(points)] == 0 || open.size() != 0) {
			Point current = open.removeFirst();
			int m = current.indexInSet(points);
			for (int i = 0; i < n; i++) {
				if (i == m || visited[i] == 1)
					continue;
				double d = distance[i];
				if ((d == -1 && graph[m][i] > 0) || (d > 0 && graph[m][i] > 0 && graph[m][i] + distance[m] < d)) {
					Point p = points.get(i);
					distance[i] = graph[m][i] + distance[m];
					prev[i] = current;
					p.distance = distance[i] + h_value[i];
					open.addLast(p);
				}
			}
			visited[current.indexInSet(points)] = 1;
			Collections.sort(open, (a, b) -> a.distance < b.distance ? -1 : 1);
		}

		Point previous = end;
		while (previous.isEquals(start) == false) {
			path.addFirst(previous);
			previous = prev[previous.indexInSet(points)];
		}
		path.addFirst(start);
		length = distance[end.indexInSet(points)];

		// Develop A* algorithm
		int i = path.size() - 1;
		int check = 0;

		// From end
		while (i > 1) {
			for (int j = 0; j < i - 1; j++) {
				Line line = new Line(path.get(i), path.get(j));
				if (line.isIntersectGraph(myGraph)) {
					if (j != i - 2)
						continue;
					else {
						i = j;
						break;
					}
				} else {
					for (int k = 0; k < i - 1 - j; k++)
						path.remove(j + 1);
					i = j;
					check = 1;
					break;
				}
			}
		}

		// From beginning
		i = 0;
		int j = path.size() - 1;
		while (j > 1) {
			for (j = path.size() - 1; j > i; j--) {
				Line line = new Line(path.get(i), path.get(j));
				if (line.isIntersectGraph(myGraph)) {
					if (j != 1)
						continue;
					else {
						i = j;
						break;
					}
				} else {
					for (int k = 0; k < j - 1 - i; k++)
						path.remove(i + 1);
					check = 1;
					break;
				}
			}
		}

		if (check == 1) {
			length = 0;
			for (int k = 0; k < path.size() - 1; k++) {
				length += path.get(k).distanceFrom(path.get(k + 1));
			}
		}
	}

}
