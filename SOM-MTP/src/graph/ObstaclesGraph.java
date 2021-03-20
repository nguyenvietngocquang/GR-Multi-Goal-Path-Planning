package graph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import util.Point;

public class ObstaclesGraph {

	public ArrayList<Point> points;

	public ObstaclesGraph(ArrayList<Point> points) {
		this.points = points;
	}

	public static ArrayList<ObstaclesGraph> getObstacles(String path_file) {
		ArrayList<ObstaclesGraph> obstacles = new ArrayList<ObstaclesGraph>();

		try {
			System.setIn(new FileInputStream(path_file));
		} catch (FileNotFoundException e) {
			System.err.println("File " + path_file + " Not Found");
			return null;
		}

		Scanner sc = new Scanner(System.in);
		ArrayList<Point> points = null;

		while (sc.hasNext()) {
			String s = sc.nextLine();
			if (s.equals("-1")) {
				if (points != null && points.size() != 0)
					obstacles.add(new ObstaclesGraph(points));
				points = new ArrayList<>();
				continue;
			}

			String temp[] = s.split("\\s+");
			points.add(new Point(Double.parseDouble(temp[0]), Double.parseDouble(temp[1])));
		}
		sc.close();
		return obstacles;
	}

	public static void main(String[] args) {
		ArrayList<ObstaclesGraph> obstacles = ObstaclesGraph.getObstacles("obstacles.txt");
		for (ObstaclesGraph obstacle : obstacles) {
			for (Point point : obstacle.points) {
				System.out.print("(" + point.x + ", " + point.y + ")\t");
			}
			System.out.println();
		}
	}
}
