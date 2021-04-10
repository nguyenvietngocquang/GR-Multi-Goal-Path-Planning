package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Graph {
	public int obstacleNumber;
	public Obstacle[] obstacles;

	public Graph() {
		obstacleNumber = 0;
		obstacles = new Obstacle[0];
	}

	public Graph(int n, String fileName) throws IOException {
		Random rd = new Random();
		final double maxArea = 200;
		double x, y;
		Obstacle[] obs = new Obstacle[n];
		for (int i = 0; i < n; i++) {
			int cornum = rd.nextInt(9);
			while (cornum < 3) {
				cornum = rd.nextInt(9);
			}
			obs[i] = new Obstacle(cornum);
			obs[i] = obs[i].convexHull(obs[i].points, obs[i].cornerNumber);
			while (obs[i].obstacleArea() > maxArea) {
				obs[i].resizeArea(2);
			}
			x = rd.nextInt(100);
			y = rd.nextInt(100);
			obs[i].moveObstacle(x, y);
			for (int j = 0; j < i; j++) {
				while (obs[i].isIntersectObstacle(obs[j])) {
					x = rd.nextInt(100);
					y = rd.nextInt(100);
					obs[i].moveObstacle(x, y);
					j = 0;
				}
			}
			obstacleNumber = n;
			obstacles = obs;
		}

		FileWriter obtaclesFile = new FileWriter(fileName);
		for (int i = 0; i < n; i++) {
			obtaclesFile.write("-1");
			for (int g = 0; g < obs[i].cornerNumber; g++) {
				obtaclesFile.write("\n");
				obtaclesFile.write(String.valueOf(obs[i].points[g].x));
				obtaclesFile.write(" ");
				obtaclesFile.write(String.valueOf(obs[i].points[g].y));
			}
			obtaclesFile.write("\n");
		}
		obtaclesFile.write("-1");
		obtaclesFile.close();
	}

	public Obstacle[] addPolygon(int n, Obstacle arr[], Obstacle x) {
		Obstacle[] temp = new Obstacle[n + 1];
		for (int i = 0; i < n; i++)
			temp[i] = arr[i];
		temp[n] = x;
		return temp;
	}

	public boolean isIntersectLine(Point firstPoint, Point secondPoint) {
		Line line = new Line(firstPoint, secondPoint);
		return line.isIntersectGraph(this);
	}

	public int countIntersectLine(Point firstPoint, Point secondPoint) {
		Line line = new Line(firstPoint, secondPoint);
		return line.countIntersectGraph(this);
	}

	public Graph(String filename) throws FileNotFoundException {
		File file = new File(filename);
		Scanner scan = new Scanner(file);
		int count_obs = 0;
		Obstacle[] obs = new Obstacle[0];
		int count_cor = 0;
		scan.nextDouble();
		Point[] point = new Point[10];
		while (scan.hasNextDouble()) {
			Double x = scan.nextDouble();
			if (x == -1) {
				for (int i = count_cor; i < 10; i++)
					point[i] = null;
				Point[] points_temp = new Point[count_cor];
				for (int i = 0; i < count_cor; i++)
					points_temp[i] = point[i];
				Obstacle obstacle = new Obstacle();
				obstacle.cornerNumber = count_cor;
				obstacle.points = points_temp;
				obs = addPolygon(count_obs, obs, obstacle);
				count_obs++;
				count_cor = 0;
			} else {
				double y = scan.nextDouble();
				Point pt = new Point(x, y);
				point[count_cor] = pt;
				count_cor++;
			}
		}
		scan.close();
		obstacleNumber = count_obs;
		obstacles = obs;
	}
}
