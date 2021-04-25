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

	public static ArrayList<ObstaclesGraph> getObtacles(String path_file) {
		ArrayList<ObstaclesGraph> obtacles = new ArrayList<ObstaclesGraph>();

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
					obtacles.add(new ObstaclesGraph(points));
				points = new ArrayList<>();
				continue;
			}

			String temp[] = s.split("\\s+");
			points.add(new Point(Double.parseDouble(temp[0]), Double.parseDouble(temp[1])));
		}
		sc.close();
		return obtacles;
	}

	public static void main(String[] args) {
		ArrayList<ObstaclesGraph> obtacles = ObstaclesGraph.getObtacles("obtacles.txt");
		for (ObstaclesGraph obtacle : obtacles) {
			for (Point p : obtacle.points) {
				System.out.print("(" + p.x + ", " + p.y + ") , ");
			}
			System.out.println();
		}
	}
}
