package util;

import algorithm.PSO;

public class Path {
	public int n; // number of line segment
	public double pointy[];
	public Point points[];
	public double distance;
	double R;

	public Path(int number) {
		this.n = number;
		this.pointy = new double[n];
		this.points = new Point[n];
	}

	public Path(int number, double R, double[] pointy, Point[] points) {
		this.n = number;
		this.pointy = pointy;
		this.points = points;
		this.R = R;

		this.distance = 0;
		this.distance += Math.hypot(PSO.startPoint.x - points[0].x, PSO.startPoint.y - points[0].y);
		for (int i = 0; i < points.length - 1; i++) {
			distance += Math.hypot(points[i + 1].x - points[i].x, points[i + 1].y - points[i].y);
		}
		this.distance += Math.hypot(PSO.endPoint.x - points[points.length - 1].x,
				PSO.endPoint.y - points[points.length - 1].y);
	}

	public void distance() {
		this.distance = 0;
		this.distance += Math.hypot(PSO.startPoint.x - points[0].x, PSO.startPoint.y - points[0].y);
		for (int i = 0; i < points.length - 1; i++) {
			distance += Math.hypot(points[i + 1].x - points[i].x, points[i + 1].y - points[i].y);
		}
		this.distance += Math.hypot(PSO.endPoint.x - points[points.length - 1].x,
				PSO.endPoint.y - points[points.length - 1].y);
	}

	public static Point convertPointToPoint(double pointy, double pointx, Point start, Point end) {
		double x, y, temp1, temp2, phi;
		temp1 = end.x - start.x;
		temp2 = end.y - start.y;
		phi = Math.atan(temp2 / temp1);
		x = Math.cos(phi) * pointx - Math.sin(phi) * pointy + start.x;
		y = Math.sin(phi) * pointx + Math.cos(phi) * pointy + start.y;
		return new Point(x, y);
	}

	public double smooth(Point point1, Point point2, Point point3) {
		double a, b, c1, c2;
		a = Math.hypot(point2.x - point1.x, point2.y - point1.y);
		b = Math.hypot(point3.x - point2.x, point3.y - point2.y);
		c1 = (point2.x - point1.x) * (point3.x - point2.x);
		c2 = (point2.y - point1.y) * (point3.y - point2.y);
		return Math.PI - 1 / Math.cos((c1 + c2) / (a * b));
	}

	public double pathSmooth() {
		double smooth = 0;
		if (points.length == 1) {
			return smooth(PSO.startPoint, points[0], PSO.endPoint);
		} else if (points.length == 2) {
			smooth += smooth(PSO.startPoint, points[0], points[1]);
			smooth += smooth(points[0], points[1], PSO.endPoint);
			return smooth / 2;
		} else {
			smooth += smooth(PSO.startPoint, points[0], points[1]);
			for (int i = 1; i < points.length - 1; i++) {
				smooth += smooth(points[i - 1], points[i], points[i + 1]);
			}
			if (!points[points.length - 1].isEquals(PSO.endPoint)) {
				smooth += smooth(points[points.length - 2], points[points.length - 1], PSO.endPoint);
				return smooth / points.length;
			} else
				return smooth / (points.length - 1);
		}
	}

	// Neu o ngoai canh AB thi tich vo huong AS va AB < 0, tuong tu
	public static double p2sDistance(Point p1, Point p2, Point S) {
		Point p1S = new Point(S.x - p1.x, S.y - p1.y);
		Point p2S = new Point(S.x - p2.x, S.y - p2.y);
		Point p1p2 = new Point(p2.x - p1.x, p2.y - p1.y);
		if (p1S.x * p1p2.x + p1S.y * p1p2.y <= 0) {
			return p1.distanceFrom(S);
		} else if (-p1p2.x * p2S.x + -p1p2.y * p2S.y <= 0) {
			return p2.distanceFrom(S);
		} else {
			// |SH| = |AS.AB|/|AB|
			return Math.abs(p1S.x * p1p2.x + p1S.y * p1p2.y) / Math.sqrt(p1p2.x * p1p2.x + p1p2.y * p1p2.y);
		}
	}

	public double pathSafety(Graph g) {
		double[] dis = new double[n + 1];
		double d, safety = 0;

		// distance from a line segment to an obstacle vertice
		for (int i = 0; i <= n; i++) {
			dis[i] = Double.POSITIVE_INFINITY;
			for (int j = 0; j != g.obstacleNumber; j++) {
				for (int k = 0; k != g.obstacles[j].cornerNumber; k++) {
					if (i == 0) {
						d = p2sDistance(PSO.startPoint, points[0], g.obstacles[j].points[k]);
					} else if (i == n) {
						d = p2sDistance(PSO.endPoint, points[n - 1], g.obstacles[j].points[k]);
					} else {
						d = p2sDistance(points[i], points[i - 1], g.obstacles[j].points[k]);
					}
					if (d < dis[i]) {
						dis[i] = d;
					}
				}
			}
		}
		safety = dis[0];
		for (int i = 1; i <= n; i++) {
			if (safety > dis[i]) {
				safety = dis[i];
			}
		}

		for (int i = 0; i < g.obstacleNumber; i++) {
			for (int j = 0; j < g.obstacles[i].cornerNumber; j++) {
				if (j == g.obstacles[i].cornerNumber - 1) {
					for (int k = 0; k < n; k++) {
						d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], points[k]);
						if (d < safety) {
							safety = d;
						}
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], PSO.startPoint);
					if (d < safety) {
						safety = d;
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], PSO.endPoint);
					if (d < safety) {
						safety = d;
					}
				} else {
					for (int k = 0; k < n; k++) {
						d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], points[k]);
						if (d < safety)
							safety = d;
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], PSO.startPoint);
					if (d < safety)
						safety = d;
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], PSO.endPoint);
					if (d < safety)
						safety = d;
				}
			}
		}

		return Math.exp(-safety);
	}

	public boolean collision(Graph g, Point endPoint) {
		for (int i = 0; i < n - 1; i++) {
			if (g.isIntersectLine(points[i], points[i + 1])) {
				return true;
			}
		}
		return g.isIntersectLine(points[n - 1], endPoint);
	}

	public boolean compare(Path that) {
		if (this.points[0] == null) {
			return false;
		} else if (that.points[0] == null) {
			return true;
		} else if (this.distance <= that.distance) {
			return true;
		}
		return false;
	}

}