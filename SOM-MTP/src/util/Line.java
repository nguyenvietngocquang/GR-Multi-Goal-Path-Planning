package util;

import java.math.BigDecimal;
import java.util.LinkedList;

public class Line {
	public Point firstPoint;
	public Point secondPoint;

	public Line(Point firstPoint, Point secondPoint) {
		this.firstPoint = firstPoint;
		this.secondPoint = secondPoint;
	}

	public void printLine() {
		System.out.println(
				"(" + firstPoint.x + ", " + firstPoint.y + ") - (" + secondPoint.x + ", " + secondPoint.y + ") ");
	}

	public double getLength() {
		return Math.hypot(firstPoint.x - secondPoint.x, firstPoint.y - secondPoint.y);
	}

	// Return if this line intersect with line in the middle of the segment
	public boolean isIntersectLineInMiddle(Line line) {
		double a = subFloat(firstPoint.y, secondPoint.y); // a = firstPoint.y - secondPoint.y
		double b = subFloat(secondPoint.x, firstPoint.x); // b = secondPoint.x - firstPoint.x
		double c = subFloat(multiFloat(-a, firstPoint.x), multiFloat(b, firstPoint.y));
		// c = -a * firstPoint.x - b * firstPoint.y

		double aLine = subFloat(line.firstPoint.y, line.secondPoint.y); // aLine = line.firstPoint.y -
																		// line.secondPoint.y
		double bLine = subFloat(line.secondPoint.x, line.firstPoint.x); // bLine = line.secondPoint.x -
																		// line.firstPoint.x
		double cLine = subFloat(multiFloat(-aLine, line.firstPoint.x), multiFloat(bLine, line.firstPoint.y));
		// cLine = -aLine * line.firstPoint.x - bLine * line.firstPoint.y

		// 4 points on same line
		// (a * line.firstPoint.x + b * line.firstPoint.y + c == 0) &&
		// (a * line.secondPoint.x + b * line.secondPoint.y + c == 0)
		if ((addFloat(multiFloat(a, line.firstPoint.x), multiFloat(b, line.firstPoint.y), c) == 0)
				&& (addFloat(multiFloat(a, line.secondPoint.x), multiFloat(b, line.secondPoint.y), c) == 0)) {
			if ((firstPoint.distanceFrom(line.firstPoint) >= getLength()
					&& firstPoint.distanceFrom(line.secondPoint) >= this.getLength())
					|| (secondPoint.distanceFrom(line.firstPoint) >= this.getLength()
							&& secondPoint.distanceFrom(line.secondPoint) >= this.getLength()))
				return false;
			return true;
		}

		// ((a * line.firstPoint.x + b * line.firstPoint.y + c) * (a *
		// line.secondPoint.x + b * line.secondPoint.y + c) < 0) &&
		// ((aLine * firstPoint.x + bLine * firstPoint.y + cLine) * (aLine *
		// secondPoint.x + bLine * secondPoint.y + cLine) < 0)
		if ((multiFloat(addFloat(multiFloat(a, line.firstPoint.x), multiFloat(b, line.firstPoint.y), c),
				addFloat(multiFloat(a, line.secondPoint.x), multiFloat(b, line.secondPoint.y), c)) < 0)
				&& (multiFloat(addFloat(multiFloat(aLine, firstPoint.x), multiFloat(bLine, firstPoint.y), cLine),
						addFloat(multiFloat(aLine, secondPoint.x), multiFloat(bLine, secondPoint.y), cLine)) < 0))
			return true;

		return false;
	}

	public boolean isIntersectSet(LinkedList<Line> lines) {
		for (Line line : lines)
			if (line.isIntersectLineInMiddle(this))
				return true;
		return false;
	}

	public boolean isInSet(LinkedList<Line> lines) {
		for (Line line : lines)
			if ((firstPoint.isEquals(line.firstPoint) && secondPoint.isEquals(line.secondPoint))
					|| (secondPoint.isEquals(line.firstPoint) && firstPoint.isEquals(line.secondPoint)))
				return true;
		return false;
	}

	public int indexInSet(LinkedList<Line> lines) {
		int id = 0;
		for (Line line : lines) {
			if ((firstPoint.isEquals(line.firstPoint) && secondPoint.isEquals(line.secondPoint))
					|| (secondPoint.isEquals(line.firstPoint) && firstPoint.isEquals(line.secondPoint)))
				return id;
			id++;
		}
		return -1;
	}

	// To find orientation of ordered triplet (p, q, r).
	// 0 --> p, q and r are collinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	public int orientation(Point p, Point q, Point r) {
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
		if (val == 0) {
			return 0; // collinear
		}
		return (val > 0) ? 1 : 2; // clock or counterclock wise
	}

	// true if line segment this and 'p1p2' intersect
	public boolean isIntersectLine(Point p1, Point p2) {
		// Find the four orientations needed for general and special cases
		int o1 = orientation(firstPoint, secondPoint, p1);
		int o2 = orientation(firstPoint, secondPoint, p2);
		int o3 = orientation(p1, p2, firstPoint);
		int o4 = orientation(p1, p2, secondPoint);

		// General case
		if (o1 != o2 && o3 != o4) {
			return true;
		}

		// Special Cases
		// p1, q1 and p2 are collinear and p2 lies on segment p1q1
		if (o1 == 0 && p1.onSegment(firstPoint, secondPoint)) {
			return true;
		}

		// p1, q1 and p2 are collinear and q2 lies on segment p1q1
		if (o2 == 0 && p2.onSegment(firstPoint, secondPoint)) {
			return true;
		}

		// p2, q2 and p1 are collinear and p1 lies on segment p2q2
		if (o3 == 0 && firstPoint.onSegment(p1, p2)) {
			return true;
		}

		// p2, q2 and q1 are collinear and q1 lies on segment p2q2
		if (o4 == 0 && secondPoint.onSegment(p1, p2)) {
			return true;
		}

		// Or else
		return false;
	}

	public boolean isIntersectObstacle(Obstacle obstacle) {
		int i = 0;
		do {
			int next = (i + 1) % obstacle.cornerNumber;
			if (isIntersectLine(obstacle.points[i], obstacle.points[next])) {
				return true;
			}
			i = next;
		} while (i != 0);
		return false;
	}

	public boolean isIntersectGraph(Graph g) {
		for (int i = 0; i < g.obstacleNumber; i++) {
			if (isIntersectObstacle(g.obstacles[i])) {
				return true;
			}
		}
		return false;
	}

	public int countIntersectObstacle(Obstacle obstacle) {
		int i = 0, count = 0;
		do {
			int next = (i + 1) % obstacle.cornerNumber;
			if (isIntersectLine(obstacle.points[i], obstacle.points[next])) {
				count++;
			}
			i = next;
		} while (i != 0);
		return count;
	}

	public int countIntersectGraph(Graph g) {
		int result = 0;
		for (int i = 0; i < g.obstacleNumber; i++) {
			result += countIntersectObstacle(g.obstacles[i]);
		}
		return result;
	}

	// Addition of real numbers function
	public double addFloat(double a, double b, double c) {
		return BigDecimal.valueOf(a).add(BigDecimal.valueOf(b).add(BigDecimal.valueOf(c))).doubleValue();
	}

	// Subtraction of real numbers function
	public double subFloat(double a, double b) {
		return BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue();
	}

	// Multiplication of real numbers function
	public double multiFloat(double a, double b) {
		return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).doubleValue();
	}

}
