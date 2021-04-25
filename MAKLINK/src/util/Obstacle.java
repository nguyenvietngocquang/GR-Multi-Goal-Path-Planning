package util;

import java.util.*;

public class Obstacle {
	public int cornerNumber;
	public Point[] points;

	public Obstacle() {
		cornerNumber = 0;
		points = new Point[0];
	}

	public Obstacle(int n) {
		cornerNumber = n;
		points = new Point[n];
		Random rd = new Random();
		double x, y;
		for (int i = 0; i < cornerNumber; i++) {
			x = rd.nextInt(100);
			y = rd.nextInt(100);
			Point point = new Point(x, y);
			points[i] = point;
		}
	}

	public double obstacleArea() {
		double area = 0.0;
		int j = cornerNumber - 1;
		for (int i = 0; i < cornerNumber; i++) {
			area += (points[j].x + points[i].x) * (points[j].y - points[i].y);
			j = i;
		}
		return Math.abs(area / 2.0);
	}

	public void resizeArea(int n) {
		for (int i = 0; i < cornerNumber; i++) {
			points[i].x = points[i].x / 2;
			points[i].y = points[i].y / 2;
		}
	}

	public void moveObstacle(double x, double y) {
		Random rd = new Random();
		double moveX = x - points[0].x;
		double moveY = y - points[0].y;
		for (int i = 0; i < cornerNumber; i++) {
			points[i].x = points[i].x + moveX;
			points[i].y = points[i].y + moveY;
		}
		for (int i = 0; i < cornerNumber; i++) {
			if (points[i].x < 0 || points[i].x > 100 || points[i].y < 0 || points[i].y > 100) {
				moveObstacle(rd.nextInt(100), rd.nextInt(100));
			}
		}
	}

	private void resize() {
		Point[] temp = new Point[2 * cornerNumber + 1];
		for (int i = 0; i <= cornerNumber; i++)
			temp[i] = points[i];
		points = temp;
	}

	public void addPoint(Point point) {
		if (cornerNumber >= points.length - 1) {
			resize();
		}
		points[cornerNumber++] = point;
		points[cornerNumber] = points[0];
	}

	public Point[] removePoint() {
		points[cornerNumber - 1] = points[0];
		Point[] temp = new Point[points.length - 1];
		for (int i = 0, k = 0; i < points.length - 1; i++) {
			temp[k++] = points[i];
		}
		cornerNumber--;
		return temp;
	}

	public int size() {
		return cornerNumber;
	}

	public static float orientation(Point p, Point q, Point r) {
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
		if (val == 0)
			return 0; // collinear
		return (val > 0) ? 1 : 2; // clock or counterclock wise
	}

	public Obstacle convexHull(Point points[], int cornerNumber) {
		if (cornerNumber < 3)
			return null;
		Vector<Point> hull = new Vector<Point>();
		Obstacle tempPoints = new Obstacle();

		// Find the leftmost point
		int l = 0;
		for (int i = 1; i < cornerNumber; i++)
			if (points[i].x < points[l].x)
				l = i;

		// Start from leftmost point, keep moving counterclockwise until reach the start
		// point again. This loop runs O(h) times where h is number of points in result
		// or output.
		int p = l, q;
		do {
			// Add current point to result
			hull.add(points[p]);

			// Search for a point 'q' such that orientation(p, x, q) is counterclockwise for
			// all points 'x'. The idea is to keep track of last visited most
			// counterclockwise point in q. If any point 'i' is more counterclockwise than
			// q, then update q.
			q = (p + 1) % cornerNumber;

			for (int i = 0; i < cornerNumber; i++) {
				// If i is more counterclockwise than current q, then update q
				if (orientation(points[p], points[i], points[q]) == 2)
					q = i;
			}

			// Now q is the most counterclockwise with respect to p. Set p as q for next
			// iteration, so that q is added to result 'hull'
			p = q;
		} while (p != l); // While we don't come to first point

		for (Point point : hull) {
			tempPoints.addPoint(point);
		}
		tempPoints.cornerNumber = hull.size();
		return tempPoints;
	}

	public boolean isIntersectObstacle(Obstacle obstacle) {
		int i = 0;
		do {
			int next = (i + 1) % this.cornerNumber;
			Line line = new Line(this.points[i], this.points[next]);
			if (line.isIntersectObstacle(obstacle)) {
				return true;
			}
			i = next;
		} while (i != 0);
		i = 0;
		do {
			int next = (i + 1) % obstacle.cornerNumber;
			Line line = new Line(obstacle.points[i], obstacle.points[next]);
			if (line.isIntersectObstacle(this)) {
				return true;
			}
			i = next;
		} while (i != 0);
		return false;
	}
}
