package util;

import java.util.LinkedList;

public class Point {
	public double x;
	public double y;
	public double distance;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void printPoint() {
		System.out.println("(" + this.x + ", " + this.y + ")");
	}

	public boolean isEquals(Point point) {
		return (point.x == this.x && point.y == this.y);
	}

	public double distanceFrom(Point point) {
		return Math.hypot(point.x - this.x, point.y - this.y);
	}

	// Return if angle b.this.c is greater than 180 or not
	public boolean greaterThan180(Point b, Point c, Obstacle obstacle) {
		Point point1 = new Point((this.x + b.x) / 2, (this.y + b.y) / 2);
		Point point2 = new Point((this.x + c.x) / 2, (this.y + c.y) / 2);
		Line line = new Line(point1, point2);
		return (line.countIntersectObstacle(obstacle) == 2);
	}

	public boolean isDuplicate(LinkedList<Point> points) {
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).x == this.x && points.get(i).y == this.y)
				return true;
		}
		return false;
	}

	public int indexInSet(LinkedList<Point> points) {
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).x == this.x && points.get(i).y == this.y)
				return i;
		}
		return -1;
	}

	public boolean isOnSegment(Line line) {
		return distanceFrom(line.firstPoint) > 0 && distanceFrom(line.secondPoint) > 0
				&& (distanceFrom(line.firstPoint) + distanceFrom(line.secondPoint) == line.getLength());
	}

	// Given three collinear points this, p, q, check this lies on line pq?
	public boolean onSegment(Point p, Point q) {
		if (this.x <= Math.max(p.x, q.x) && this.x >= Math.min(p.x, q.x) && this.y <= Math.max(p.y, q.y)
				&& this.y >= Math.min(p.y, q.y)) {
			return true;
		}
		return false;
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

	// true if this point lies inside the polygons
	public boolean isIntersectObstacle(Obstacle obstacle) {
		// There must be at least 3 vertices in polygon[]
		if (obstacle.cornerNumber < 3) {
			return false;
		}

		// Create a point for line segment from p to infinite
		Point extreme = new Point(10000, this.y);

		// Count intersections of the above line with sides of polygon
		int count = 0, i = 0;
		do {
			int next = (i + 1) % obstacle.cornerNumber;

			// Check if the line 'p' to 'extreme' intersects with the line 'poly.point[i]'
			// to 'poly.point[next]'
			Line line = new Line(obstacle.points[i], obstacle.points[next]);
			if (line.isIntersectLine(this, extreme)) {
				// If the point 'p' is collinear with line 'i-next', if it does return true
				if (orientation(obstacle.points[i], this, obstacle.points[next]) == 0) {
					return onSegment(obstacle.points[i], obstacle.points[next]);
				}
				count++;
			}
			i = next;
		} while (i != 0);

		// Return true if count is odd, false otherwise
		return (count % 2 == 1);
	}

	public boolean isIntersectGraph(Graph g) {
		for (int i = 0; i < g.obstacleNumber; i++) {
			if (isIntersectObstacle(g.obstacles[i])) {
				return true;
			}
		}
		return false;
	}

	// Check if is the end point of the line
	public boolean isEndPointOfLine(Line line) {
		if (this.isEquals(line.firstPoint) || this.isEquals(line.secondPoint))
			return true;
		return false;
	}

}
