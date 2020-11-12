package graph;

import java.util.*;

import util.Graph;
import util.Line;
import util.Neuron;
import util.Point;

public class GraphDivision {
	public LinkedList<Point> midPoints = new LinkedList<Point>();
	public LinkedList<Line> MAKLINK = new LinkedList<Line>();
	public LinkedList<Line> lines = new LinkedList<Line>(); // Dung de luu tru cac doan da noi de lay trung diem
	public LinkedList<Point> allPoints = new LinkedList<Point>();
	public LinkedList<Point> visitPoints = new LinkedList<Point>();

	// return if m, n same side or not with line p1p2
	public boolean SameSide(Point p1, Point p2, Point m, Point n) {
		double a = p1.y - p2.y;
		double b = p2.x - p1.x;
		double c = -a * p1.x - b * p1.y;

		// 4 points on same line
		if (Math.abs(a * m.x + b * m.y + c) <= Double.MIN_NORMAL
				&& Math.abs(a * n.x + b * n.y + c) <= Double.MIN_NORMAL)
			return false;

		if ((a * m.x + b * m.y + c) * (a * n.x + b * n.y + c) < 0
				|| Math.abs((a * m.x + b * m.y + c) * (a * n.x + b * n.y + c)) <= Double.MIN_NORMAL)
			return false;
		return true;
	}

	// Check if angle between obstacles satisfied not greater than 180
	public boolean AngleCheck(Graph G, int i, int k, Point p) {
		Point x = G.obstacles[i].points[k];
		Point y = p;
		Point z = G.obstacles[i].points[(k + 1) % (G.obstacles[i].cornerNumber)];
		Point t = G.obstacles[i].points[(k + G.obstacles[i].cornerNumber - 1) % (G.obstacles[i].cornerNumber)];

		return SameSide(x, y, z, t) == false;
	}

	public GraphDivision(Graph myGraph, LinkedList<Point> points, LinkedList<Neuron> neurons) {
		this.visitPoints = points;

		for (int i = 0; i < myGraph.obstacleNumber; i++) {
			for (int k = 0; k < myGraph.obstacles[i].cornerNumber; k++) {

				LinkedList<Line> list = new LinkedList<Line>(); // Danh sach cac doan thang noi dinh

				// Create the lines to the working space boundary walls (0-100, 0-100)
				Point a = new Point(myGraph.obstacles[i].points[k].x, 0);
				Point b = new Point(myGraph.obstacles[i].points[k].x, 100);
				Point c = new Point(0, myGraph.obstacles[i].points[k].y);
				Point d = new Point(100, myGraph.obstacles[i].points[k].y);

				Line[] arr = new Line[4];
				arr[0] = new Line(myGraph.obstacles[i].points[k], a);
				arr[1] = new Line(myGraph.obstacles[i].points[k], b);
				arr[2] = new Line(myGraph.obstacles[i].points[k], c);
				arr[3] = new Line(myGraph.obstacles[i].points[k], d);

				// Add to list
				for (int m = 0; m < 4; m++) {
					Line line = new Line(arr[m].firstPoint, arr[m].secondPoint);
					if (line.countIntersectGraph(myGraph) == 2) {
						list.addLast(arr[m]);
					}
				}

				// Check the lines to another obstacles
				for (int j = 0; j < myGraph.obstacleNumber; j++) {
					if (i == j)
						continue;

					for (int l = 0; l < myGraph.obstacles[j].cornerNumber; l++) {
						Line line = new Line(myGraph.obstacles[i].points[k], myGraph.obstacles[j].points[l]);
						if (line.countIntersectGraph(myGraph) == 4) {
							list.addFirst(line);
						}
					}
				}

				// Arrange lines by length ascending order
				Collections.sort(list, (m, n) -> m.getLength() < n.getLength() ? -1 : 1);

				Line[] candidates = new Line[2];
				for (int m = 0; m < list.size(); m++) {
					if (list.get(m).isInSet(lines) && AngleCheck(myGraph, i, k, list.get(m).secondPoint))
						break;
					else if (list.get(m).isIntersectSet(lines) == false) {
						if (AngleCheck(myGraph, i, k, list.get(m).secondPoint)) {
							Line lineToCheck = list.get(m);
							lines.addFirst(lineToCheck);
							Point midPoint = new Point((lineToCheck.firstPoint.x + lineToCheck.secondPoint.x) / 2.0,
									(lineToCheck.firstPoint.y + lineToCheck.secondPoint.y) / 2.0);
							midPoints.addLast(midPoint);
							break;
						}

						Point left = myGraph.obstacles[i].points[(k + 1) % myGraph.obstacles[i].cornerNumber];

						if (candidates[0] == null && candidates[1] == null) {
							if (list.get(m).firstPoint.greaterThan180(list.get(m).secondPoint, left,
									myGraph.obstacles[i])) {
								candidates[1] = list.get(m);
							} else {
								candidates[0] = list.get(m);
							}
						} else if (!(candidates[0] != null && candidates[1] != null)) {
							if (list.get(m).firstPoint.greaterThan180(list.get(m).secondPoint, left,
									myGraph.obstacles[i])) {
								if (candidates[1] == null)
									candidates[1] = list.get(m);
							} else {
								if (candidates[0] == null)
									candidates[0] = list.get(m);
							}
						}

						if (candidates[0] != null && candidates[1] != null) {
							if (list.get(m).firstPoint.greaterThan180(list.get(m).secondPoint, left,
									myGraph.obstacles[i])) {
								if (candidates[0].firstPoint.greaterThan180(candidates[0].secondPoint,
										list.get(m).secondPoint, myGraph.obstacles[i]) == false)
									candidates[1] = list.get(m);
							} else {
								if (candidates[1].firstPoint.greaterThan180(candidates[1].secondPoint,
										list.get(m).secondPoint, myGraph.obstacles[i]) == false)
									candidates[0] = list.get(m);
							}

							if (candidates[0].firstPoint.greaterThan180(candidates[0].secondPoint,
									candidates[1].secondPoint, myGraph.obstacles[i]) == false) {
								for (int n = 0; n < 2; n++) {
									lines.addLast(candidates[n]);
									Point midPoint = new Point(
											(candidates[n].firstPoint.x + candidates[n].secondPoint.x) / 2.0,
											(candidates[n].firstPoint.y + candidates[n].secondPoint.y) / 2.0);
									midPoints.addLast(midPoint);
								}
								break;
							}
						}

					}
				}
			}
		}

		// Add points to visit in MAKLINK
		for (Point point : points) {
			if (!point.isDuplicate(midPoints))
				midPoints.add(point);
		}

		allPoints = (LinkedList<Point>) midPoints.clone();

		// Add neurons in MAKLINK
		for (Neuron neuron : neurons) {
			allPoints.add(neuron);
		}

		// Create MAKLINK graph
		for (int i = 0; i < allPoints.size() - 1; i++) {
			for (int j = i + 1; j < allPoints.size(); j++) {
				Line line = new Line(allPoints.get(i), allPoints.get(j));
				if (line.countIntersectGraph(myGraph) == 0) {
					int check = 1;

					// check if intersect with existed lines
					for (Line k : MAKLINK) {
						if (line.isIntersectLineInMiddle(k)) {
							check = 0;
							break;
						}
					}

					for (Point p : points) {
						if (p.isOnSegment(line)) {
							check = 0;
							break;
						}
					}

					for (Neuron n : neurons) {
						if (n.isOnSegment(line)) {
							check = 0;
							break;
						}
					}

					for (Line l : lines) {
						if (line.isIntersectLineInMiddle(l)) {
							check = 0;
							break;
						}
					}

					if (check == 1) {
						MAKLINK.addLast(line);
					}
				}
			}
		}
	}

	public void updateMaklink(Graph myGraph, Point newNeuron, LinkedList<Neuron> neurons) {
		// Remove old neurons
		for (int i = 0; i < MAKLINK.size(); i++) {
			if (newNeuron.isEndPointOfLine(MAKLINK.get(i))) {
				MAKLINK.remove(i);
			}
		}

		// Update MAKLINK with new neurons
		for (int i = 0; i < allPoints.size(); i++) {
			if (!newNeuron.isEquals(allPoints.get(i))) {
				Line line = new Line(newNeuron, allPoints.get(i));
				if (!line.isInSet(MAKLINK) && line.countIntersectGraph(myGraph) == 0) {
					int check = 1;

					// check if intersect with existed lines
					for (Line k : MAKLINK) {
						if (line.isIntersectLineInMiddle(k)) {
							check = 0;
							break;
						}
					}

					for (Point p : visitPoints) {
						if (p.isOnSegment(line)) {
							check = 0;
							break;
						}
					}

					for (Neuron n : neurons) {
						if (n.isOnSegment(line)) {
							check = 0;
							break;
						}
					}

					for (Line l : lines) {
						if (line.isIntersectLineInMiddle(l)) {
							check = 0;
							break;
						}
					}

					if (check == 1) {
						MAKLINK.addLast(line);
					}
				}
			}
		}
	}

}
