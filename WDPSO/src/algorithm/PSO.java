package algorithm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import util.Graph;
import util.Line;
import util.Obstacle;
import util.Path;
import util.Point;

public class PSO {
	public final int NP = 100; // population size
	public final int Nmax = 10; // maximum of non-dominated archive size
	public final int IT = 200;
	public double V_MAX;
	public double V_MIN;
	public double maxAngle;
	public double minAngle;
	public Graph graph;
	public Path particles[] = new Path[NP];
	public int numR; // number of R in map
	public double R; // radius
	public static Point startPoint;
	public static Point endPoint;
	public double vValue[][];
	public Path pBest[] = new Path[NP];
	public Path gBest;
	Random random = new Random();
	public Path NaParticles[] = new Path[Nmax];
	public Path NbParticles[] = new Path[Nmax];
	public static double r1, r2;
	public static double r, pm;
	public static final double c1 = 2, c2 = 2;
	public double AB;
	public static double w;
	public static final double wMax = 0.9, wMin = 0.2;
	static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	static DecimalFormat df = (DecimalFormat) nf;
	public LinkedList<Point> result = new LinkedList<Point>();
	public double length;

	public PSO(int R, Point start, Point end, Graph graph) {
		startPoint = start;
		endPoint = end;
		this.R = R;
		this.graph = graph;
		this.AB = Math.hypot(end.x - start.x, end.y - start.y);
		this.numR = (int) (AB / R);
		if (this.AB == this.numR * this.R) {
			this.numR--;
		}
	}

	public void initialize(int numR) {
		for (int i = 0; i < NP; i++) {
			double angles[] = new double[numR];
			Point points[] = new Point[numR];
			for (int j = 0; j < numR; j++) {
				do {
					angles[j] = random.nextDouble() * ((maxAngle - minAngle) + 1) + minAngle;
					points[j] = Path.convertPointToPoint(angles[j], (j + 1) * R, startPoint, endPoint);
				} while (!points[j].inCoordinate());
			}
			particles[i] = new Path(numR, R, angles, points);
			particles[i].distance();
		}
	}

	public void getVelocity() {
		vValue = new double[NP][numR];
		for (int i = 0; i < NP; i++) {
			for (int j = 0; j < numR; j++) {
				vValue[i][j] = random.nextDouble() * (V_MAX - V_MIN + 1) + V_MIN;
			}
		}
		return;
	}

	public Path mutation(Path path) {
		int x1 = random.nextInt(Nmax);
		int x2 = random.nextInt(Nmax);
		double f = random.nextDouble();
		for (int i = 0; i < numR; i++) {
			path.angles[i] += f * (NaParticles[x1].angles[i] - NaParticles[x2].angles[i]);
			if (path.angles[i] > maxAngle) {
				path.angles[i] = maxAngle;
			} else if (path.angles[i] < minAngle) {
				path.angles[i] = minAngle;
			}
			path.points[i] = Path.convertPointToPoint(path.angles[i], (i + 1) * R, startPoint, endPoint);
		}
		path.distance();
		return path;
	}

	// single-objective
	public void initializeNaNb() {
		for (int i = 0; i < Nmax; i++) {
			NaParticles[i] = new Path(numR);
			NbParticles[i] = new Path(numR);
		}
		for (int i = 0; i < NP; i++) {
			if (pathCollision(particles[i]) == false) {
				NaParticles = addArchive(particles[i], NaParticles);
			} else {
				NbParticles = addArchive(particles[i], NbParticles);
			}
		}
	}

	public void gBestSelection(int it) {
		double CD[] = new double[Nmax];
		double bestCD;
		int bestCDid, maxa = 0, maxb = 0;
		boolean NaNull = true, NbNull = true;
		for (int i = 0; i < NaParticles.length; i++) {
			if (NaParticles[i].points[0] != null) {
				NaNull = false;
				maxa++;
			}
		}
		for (int i = 0; i < NbParticles.length; i++) {
			if (NbParticles[i].points[0] != null) {
				NbNull = false;
				maxb++;
			}
		}

		if (NbNull == true) {
			for (int i = 0; i < maxa; i++) {
				CD[i] = NaParticles[i].distance;
			}
			bestCD = CD[0];
			bestCDid = 0;
			for (int i = 1; i < maxa; i++) {
				if (CD[i] < bestCD) {
					bestCD = CD[i];
					bestCDid = i;
				}
			}
			for (int i = 0; i < numR; i++) {
				gBest.angles[i] = NaParticles[bestCDid].angles[i];
				gBest.points[i] = new Point(NaParticles[bestCDid].points[i].x, NaParticles[bestCDid].points[i].y);
				gBest.distance = NaParticles[bestCDid].distance;
			}
		} else if (NaNull == true) {
			for (int i = 0; i < maxb; i++) {
				CD[i] = NbParticles[i].distance;
			}
			bestCD = CD[0];
			bestCDid = 0;
			for (int i = 1; i < maxb; i++) {
				if (CD[i] < bestCD) {
					bestCD = CD[i];
					bestCDid = i;
				}
			}
			for (int i = 0; i < numR; i++) {
				gBest.angles[i] = NbParticles[bestCDid].angles[i];
				gBest.points[i] = new Point(NbParticles[bestCDid].points[i].x, NbParticles[bestCDid].points[i].y);
				gBest.distance = NbParticles[bestCDid].distance;
			}
		} else {
			double ps = 0.5 - 0.5 * it / IT;
			double r = random.nextDouble();
			if (ps < r) {
				for (int i = 0; i < maxa; i++) {
					CD[i] = NaParticles[i].distance;
				}
				bestCD = CD[0];
				bestCDid = 0;
				for (int i = 1; i < maxa; i++) {
					if (CD[i] < bestCD) {
						bestCD = CD[i];
						bestCDid = i;
					}
				}
				for (int i = 0; i < numR; i++) {
					gBest.angles[i] = NaParticles[bestCDid].angles[i];
					gBest.points[i] = new Point(NaParticles[bestCDid].points[i].x, NaParticles[bestCDid].points[i].y);
					gBest.distance = NaParticles[bestCDid].distance;
				}
			} else {
				for (int i = 0; i < maxb; i++) {
					CD[i] = NbParticles[i].distance;
				}
				bestCD = CD[0];
				bestCDid = 0;
				for (int i = 1; i < maxb; i++) {
					if (CD[i] < bestCD) {
						bestCD = CD[i];
						bestCDid = i;
					}
				}
				for (int i = 0; i < numR; i++) {
					gBest.angles[i] = NbParticles[bestCDid].angles[i];
					gBest.points[i] = new Point(NbParticles[bestCDid].points[i].x, NbParticles[bestCDid].points[i].y);
					gBest.distance = NbParticles[bestCDid].distance;
				}
			}
		}
	}

	// Tra lai rank cua cac phan tu
	public int[] particleRank(Path[] particles) {
		int length = particles.length;
		int rank[] = new int[length];
		double Nobj[] = new double[length];
		int count;

		for (int i = 0; i < length; i++) {
			if (particles[i].points[0] == null) {
				Nobj[i] = Double.POSITIVE_INFINITY;
			} else {
				Nobj[i] = particles[i].distance;
			}
		}

		for (int i = 0; i < length; i++) {
			count = 0;
			for (int j = 0; j < length; j++) {
				if (j != i && Nobj[j] >= Nobj[i]) {
					count++;
				}
			}
			rank[i] = length - count - 1;
			for (int j = 0; j < i; j++) {
				if (rank[j] == rank[i]) {
					rank[i]++;
				}
			}
		}
		return rank;
	}

	public double[] crowdingDistance(Path[] particles) {
		int length = particles.length;
		double CD[] = new double[length];
		double[] dis = new double[length];
		int particleRank[] = particleRank(particles);
		int indexRank[] = indexRank(particleRank);
		int count = 0;

		for (int i = 0; i < length; i++) {
			CD[i] = 0;
			if (particles[i].points[0] != null) {
				dis[i] = particles[i].distance;
			} else {
				count++;
			}
		}

		for (int i = 0; i < length; i++) {
			if (particleRank[i] == 0 || particleRank[i] == length - count - 1) {
				CD[i] = Double.POSITIVE_INFINITY;
			}
			if (particles[i].points[0] == null) {
				CD[i] = 0;
			} else if (particleRank[i] != 0 && particleRank[i] != length - count - 1) {
				CD[i] += (dis[indexRank[particleRank[i] + 1]] - dis[indexRank[particleRank[i] - 1]])
						/ (dis[indexRank[length - count - 1]] - dis[indexRank[0]]);
			}
		}

		return CD;
	}

	public Path[] addArchive(Path path, Path[] particles) {
		int breakPoint = 100;

		for (int i = 0; i < Nmax; i++) {
			if (particles[i].points[0] == null) {
				breakPoint = i;
				break;
			}
		}

		if (breakPoint != 100) {
			for (int k = 0; k < numR; k++) {
				particles[breakPoint].angles[k] = path.angles[k];
				particles[breakPoint].points[k] = new Point(path.points[k].x, path.points[k].y);
				particles[breakPoint].distance = path.distance;
			}
			return particles;
		}

		int replace = -1;
		for (int i = 0; i < Nmax; i++) {
			if (path.compare(particles[i])) { // replace path with worst in Na
				if (replace == -1) {
					replace = i;
				} else if (replace != -1 && particles[replace].compare(particles[i])) {
					replace = i;
				}
			}
		}

		if (replace != -1) {
			for (int i = 0; i < numR; i++) {
				particles[replace].angles[i] = path.angles[i];
				particles[replace].points[i] = new Point(path.points[i].x, path.points[i].y);
				particles[replace].distance = path.distance;
			}
		}

		return particles;
	}

	public void run() {
		double angle = determinePathAngle();
//		if (angle >= 60) {
//			maxAngle = 90;
//		} else if (angle >= 30) {
//			maxAngle = 60;
//		} else {
//			maxAngle = 30;
//		}
		maxAngle = (int) (angle / 10) * 10 + 20;
		minAngle = -maxAngle;
		V_MAX = maxAngle;
		V_MIN = minAngle;
		initialize(numR);

		// initialize pBest
		for (int i = 0; i < NP; i++) {
			double angles[] = new double[numR];
			Point points[] = new Point[numR];
			for (int j = 0; j < numR; j++) {
				angles[j] = particles[i].angles[j];
				points[j] = new Point(particles[i].points[j].x, particles[i].points[j].y);
			}
			pBest[i] = new Path(numR, R, angles, points);
		}

		initializeNaNb();

		gBest = new Path(numR);
		gBestSelection(0);

		getVelocity();
		boolean pathColli, pBestColli;

		// PSO
		for (int i = 0; i < IT; i++) {
			// update particles
			w = wMax - ((wMax - wMin) * i / IT);
			for (int j = 0; j < NP; j++) {
				for (int k = 0; k < numR; k++) {
					r1 = random.nextDouble();
					r2 = random.nextDouble();
					vValue[j][k] = w * vValue[j][k] + r1 * c1 * (pBest[j].angles[k] - particles[j].angles[k])
							+ r2 * c2 * (gBest.angles[k] - particles[j].angles[k]);
					particles[j].angles[k] += vValue[j][k];
					if (particles[j].angles[k] > maxAngle) {
						particles[j].angles[k] = maxAngle;
					} else if (particles[j].angles[k] < minAngle) {
						particles[j].angles[k] = minAngle;
					}
					particles[j].points[k] = Path.convertPointToPoint(particles[j].angles[k], (k + 1) * R, startPoint,
							endPoint);
				}
				particles[j].distance();

				// mutate
				pathColli = pathCollision(particles[j]);
				if (pathColli) {
					double countColli = numberCollisions(particles[j]);
					r = random.nextDouble();
					pm = (Math.exp(countColli) - Math.exp(-countColli))
							/ (Math.exp(countColli) + Math.exp(-countColli));
					if (r < pm) {
						particles[j] = mutation(particles[j]);
					}
				}

				// pBest
				pathColli = pathCollision(particles[j]);
				pBestColli = pathCollision(pBest[j]);

				if (pBestColli && !pathColli) {
					for (int k = 0; k < numR; k++) {
						pBest[j].angles[k] = particles[j].angles[k];
						pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
					}
					pBest[j].distance = particles[j].distance;
					NaParticles = addArchive(particles[j], NaParticles);
				} else if (!pBestColli && !pathColli) {
					if (pBest[j].distance > particles[j].distance) {
						for (int k = 0; k < numR; k++) {
							pBest[j].angles[k] = particles[j].angles[k];
							pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
						}
						pBest[j].distance = particles[j].distance;
					}
					NaParticles = addArchive(particles[j], NaParticles);
				} else if (pathColli && pBestColli) {
					if (pBest[j].distance > particles[j].distance) {
						for (int k = 0; k < numR; k++) {
							pBest[j].angles[k] = particles[j].angles[k];
							pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
						}
						pBest[j].distance = particles[j].distance;
					}
					NbParticles = addArchive(pBest[j], NbParticles);
				}
			}

			// select gBest
			gBestSelection(i);
		}

		result.add(startPoint);
		for (int i = 0; i < numR; i++) {
			result.add(gBest.points[i]);
		}
		result.add(endPoint);

		length = 0;
		try {
			for (int k = 0; k < result.size() - 1; k++) {
				length += result.get(k).distanceFrom(result.get(k + 1));
			}
		} catch (Exception e) {
		}

		result.removeLast();
		result.removeFirst();
	}
	// end single-objective

	// Tra lai tap cac index tu cao den thap
	public int[] indexRank(int[] rank) {
		int length = rank.length;
		int index[] = new int[length];
		for (int i = 0; i < length; i++) {
			index[rank[i]] = i;
		}
		return index;
	}

	public boolean pathCollision(Path path) {
		for (int i = 0; i < numR; i++) {
			if (i == 0) {
				if (graph.isIntersectLine(startPoint, path.points[i])) {
					return true;
				}
				if (numR == 1) {
					return graph.isIntersectLine(endPoint, path.points[i]);
				}
			} else if (i == numR - 1) {
				if (graph.isIntersectLine(endPoint, path.points[i])
						|| graph.isIntersectLine(path.points[i], path.points[i - 1])) {
					return true;
				}
			} else if (i != 0) {
				if (graph.isIntersectLine(path.points[i], path.points[i - 1])) {
					return true;
				}
			}
		}
		return false;
	}

	public double numberCollisions(Path path) {
		double count = 0;
		for (int i = 0; i < numR; i++) {
			if (i == 0) {
				count += graph.countIntersectLine(startPoint, path.points[i]);
			} else if (i == numR - 1) {
				count += graph.countIntersectLine(endPoint, path.points[i]);
				count += graph.countIntersectLine(path.points[i], path.points[i - 1]);
			} else {
				count += graph.countIntersectLine(path.points[i], path.points[i - 1]);
			}
		}
		return count;
	}

	public double calculateAngle(Point b, Point a, Point c) { // calculate angle BAC
		// vectors
		Point v1 = new Point(b.x - a.x, b.y - a.y);
		Point v2 = new Point(c.x - a.x, c.y - a.y);
		double cos = (v1.x * v2.x + v1.y * v2.y) / (Math.hypot(v1.x, v1.y) * Math.hypot(v2.x, v2.y));
		return Math.toDegrees(Math.acos(cos));
	}

	public double determinePathAngle() {
		LinkedList<Obstacle> collide = new LinkedList<Obstacle>();
		LinkedList<Point> up = new LinkedList<Point>();
		LinkedList<Point> down = new LinkedList<Point>();
		double a = startPoint.y - endPoint.y;
		double b = endPoint.x - startPoint.x;
		double c = -a * startPoint.x - b * startPoint.y;

		for (int i = 0; i != graph.obstacleNumber; i++) {
			Obstacle obstacle = graph.obstacles[i];
			Line line = new Line(startPoint, endPoint);
			if (line.isIntersectObstacle(obstacle)) {
				collide.add(obstacle);
			}
		}

		for (Obstacle obstacle : collide) {
			for (Point p : obstacle.points) {
				if (a * p.x + b * p.y + c >= 0) {
					up.add(p);
				} else if (a * p.x + b * p.y + c < 0) {
					down.add(p);
				}
			}
		}

		double maxUp = 0, maxDown = 0;
		double disUp = 0, disDown = 0;
		for (Point p : up) {
			if (p.distanceFrom(startPoint) > disUp) {
				disUp = p.distanceFrom(startPoint);
			}
			double angle = calculateAngle(p, startPoint, endPoint);
			if (angle > maxUp) {
				maxUp = angle;
			}
		}
		for (Point p : down) {
			if (p.distanceFrom(startPoint) > disDown) {
				disDown = p.distanceFrom(startPoint);
			}
			double angle = calculateAngle(p, startPoint, endPoint);
			if (angle > maxDown) {
				maxDown = angle;
			}
		}

		if (maxUp * disUp < maxDown * disDown) {
			if (disUp > AB) {
				this.numR = (int) (disUp / R) + 1;
			}
			return maxUp;
		} else {
			if (disDown > AB) {
				this.numR = (int) (disDown / R) + 1;
			}
			return maxDown;
		}
	}

//	// multi-objective
//	public void initializeNaNb() {
//		for (int i = 0; i < Nmax; i++) {
//			NaParticles[i] = new Path(numR);
//			NbParticles[i] = new Path(numR);
//		}
//		for (int i = 0; i < NP; i++) {
//			if (pathCollision(particles[i]) == false) {
//				addArchive(particles[i], NaParticles);
//			} else {
//				addArchive(particles[i], NbParticles);
//			}
//		}
//	}
//
//	public void gBestSelection(int it) {
//		double[] CD = new double[Nmax];
//		double bestCD;
//		int bestCDid;
//		boolean NaNull = true, NbNull = true;
//		for (int i = 0; i != NaParticles.length; i++) {
//			if (NaParticles[i].points[0] != null) {
//				NaNull = false;
//				break;
//			}
//		}
//		for (int i = 0; i != NbParticles.length; i++) {
//			if (NbParticles[i].points[0] != null) {
//				NbNull = false;
//				break;
//			}
//		}
//
//		// select the best CD particle as Gbest
//		if (NaNull == true) {
//			CD = crowdingDistance(NbParticles);
//			bestCD = CD[0];
//			bestCDid = 0;
//			for (int i = 0; i != Nmax; i++) {
//				if (CD[i] > bestCD && CD[i] > 0) {
//					bestCD = CD[i];
//					bestCDid = i;
//				}
//			}
//			for (int i = 0; i != numR; i++) {
//				gBest.angles[i] = NbParticles[bestCDid].angles[i];
//				gBest.points[i] = new Point(NbParticles[bestCDid].points[i].x, NbParticles[bestCDid].points[i].y);
//			}
//			gBest.distance = NbParticles[bestCDid].distance;
//		} else if (NbNull == true) {
//			CD = crowdingDistance(NaParticles);
//			bestCD = CD[0];
//			bestCDid = 0;
//			for (int i = 0; i != Nmax; i++) {
//				if (CD[i] > bestCD && CD[i] > 0) {
//					bestCD = CD[i];
//					bestCDid = i;
//				}
//			}
//			for (int i = 0; i != numR; i++) {
//				gBest.angles[i] = NaParticles[bestCDid].angles[i];
//				gBest.points[i] = new Point(NaParticles[bestCDid].points[i].x, NaParticles[bestCDid].points[i].y);
//			}
//			gBest.distance = NaParticles[bestCDid].distance;
//		} else {
//			double ps = 0.5 - 0.5 * it / IT;
//			double r = random.nextDouble();
//			if (ps < r) {
//				CD = crowdingDistance(NaParticles);
//				bestCD = CD[0];
//				bestCDid = 0;
//				for (int i = 0; i != Nmax; i++) {
//					if (CD[i] > bestCD && CD[i] > 0) {
//						bestCD = CD[i];
//						bestCDid = i;
//					}
//				}
//				for (int i = 0; i != numR; i++) {
//					gBest.angles[i] = NaParticles[bestCDid].angles[i];
//					gBest.points[i] = new Point(NaParticles[bestCDid].points[i].x, NaParticles[bestCDid].points[i].y);
//				}
//				gBest.distance = NaParticles[bestCDid].distance;
//			} else {
//				CD = crowdingDistance(NbParticles);
//				bestCD = CD[0];
//				bestCDid = 0;
//				for (int i = 0; i != Nmax; i++) {
//					if (CD[i] > bestCD && CD[i] > 0) {
//						bestCD = CD[i];
//						bestCDid = i;
//					}
//				}
//				for (int i = 0; i != numR; i++) {
//					gBest.angles[i] = NbParticles[bestCDid].angles[i];
//					gBest.points[i] = new Point(NbParticles[bestCDid].points[i].x, NbParticles[bestCDid].points[i].y);
//				}
//				gBest.distance = NbParticles[bestCDid].distance;
//			}
//		}
//	}
//
//	// Tra lai rank cua cac phan tu
//	public int[] particleRank(Path[] particles, int type) {
//		int len = particles.length;
//		int[] rank = new int[len];
//		double[] obj = new double[len];
//		int count;
//		// Sap xep cac particle theo tieu chi
//		if (type == 1) {
//			for (int i = 0; i != len; i++) {
//				if (particles[i].points[0] != null) {
//					obj[i] = particles[i].distance;
//				} else {
//					obj[i] = Double.POSITIVE_INFINITY;
//				}
//			}
//		} else if (type == 2) {
//			for (int i = 0; i != len; i++) {
//				if (particles[i].points[0] != null) {
//					obj[i] = particles[i].pathSafety(graph);
//				} else {
//					obj[i] = Double.POSITIVE_INFINITY;
//				}
//			}
//		} else if (type == 3) {
//			for (int i = 0; i != len; i++) {
//				if (particles[i].points[0] != null) {
//					obj[i] = particles[i].pathSmooth();
//				} else {
//					obj[i] = Double.POSITIVE_INFINITY;
//				}
//			}
//		}
//
//		for (int i = 0; i != len; i++) {
//			count = 0;
//			for (int j = 0; j != len; j++) {
//				if (j != i && obj[j] >= obj[i]) {
//					count++; // Dem so luong particle te hon obj[i]
//				}
//
//			}
//			rank[i] = len - count - 1;
//			for (int k = 0; k != i; k++) {
//				if (rank[k] == rank[i]) {
//					rank[i] += 1;
//				}
//			}
//		}
//		return rank;
//	}
//
//	public double[] crowdingDistance(Path[] particles) {
//		int len = particles.length;
//		double[] CD = new double[len];
//		double[] dis = new double[len];
//		double[] safety = new double[len];
//		double[] smooth = new double[len];
//		int[] rankDistance = new int[len];
//		int[] rankSafety = new int[len];
//		int[] rankSmooth = new int[len];
//		int[] rerankDistance = new int[len];
//		int[] rerankSafety = new int[len];
//		int[] rerankSmooth = new int[len];
//		rankDistance = particleRank(particles, 1);
//		rankSafety = particleRank(particles, 2);
//		rankSmooth = particleRank(particles, 3);
//		rerankDistance = indexRank(rankDistance);
//		rerankSafety = indexRank(rankSafety);
//		rerankSmooth = indexRank(rankSmooth);
//		for (int i = 0; i != len; i++) {
//			CD[i] = 0;
//			if (particles[i].points[0] != null) {
//				dis[i] = particles[i].distance;
//				safety[i] = particles[i].pathSafety(graph);
//				smooth[i] = particles[i].pathSmooth();
//			}
//		}
//		int index = 0; // Tinh so phan tu null
//		for (int i = 0; i != len; i++) {
//			if (particles[i].points[0] == null) {
//				index++;
//			}
//		}
//
//		for (int i = 0; i != len; i++) {
//			if (rankDistance[i] == 0 || rankDistance[i] == (len - 1 - index)) {
//				CD[i] += Double.POSITIVE_INFINITY;
//			}
//			if (rankSafety[i] == 0 || rankSafety[i] == (len - 1 - index)) {
//				CD[i] += Double.POSITIVE_INFINITY;
//			}
//			if (rankSmooth[i] == 0 || rankSmooth[i] == (len - 1 - index)) {
//				CD[i] += Double.POSITIVE_INFINITY;
//			}
//
//			if (particles[i].points[0] == null) {
//				CD[i] = 0;
//			} else if (rankDistance[i] != 0 && rankDistance[i] != (len - 1 - index) && rankSmooth[i] != 0
//					&& rankSmooth[i] != (len - 1 - index) && rankSafety[i] != 0 && rankSafety[i] != (len - 1 - index)) {
//				CD[i] = CD[i] + (dis[rerankDistance[rankDistance[i] + 1]] - dis[rerankDistance[rankDistance[i] - 1]])
//						/ (dis[rerankDistance[len - 1 - index]] - dis[rerankDistance[0]]);
//				CD[i] = CD[i] + (safety[rerankSafety[rankSafety[i] + 1]] - safety[rerankSafety[rankSafety[i] - 1]])
//						/ (safety[rerankSafety[len - 1 - index]] - safety[rerankSafety[0]]);
//				CD[i] = CD[i] + (smooth[rerankSmooth[rankSmooth[i] + 1]] - smooth[rerankSmooth[rankSmooth[i] - 1]])
//						/ (smooth[rerankSmooth[len - 1 - index]] - smooth[rerankSmooth[0]]);
//			}
//		}
//		return CD;
//	}
//
//	public void addArchive(Path par, Path[] NaParticles) {
//		boolean dominate = false, dominated = false;
//		int breakPoint = 100;
//		Path[] newPar = new Path[Nmax + 1];
//		double[] CD = new double[Nmax + 1];
//		double worstCD;
//		int worstCDid;
//		boolean checkNull = true; // if NaParticles null
//		for (int i = 0; i != NaParticles.length; i++) {
//			if (NaParticles[i].points[0] != null) {
//				checkNull = false;
//				break;
//			}
//		}
//
//		if (checkNull == true) { // if Na null, add to archive
//			for (int i = 0; i != numR; i++) {
//				NaParticles[0].angles[i] = par.angles[i];
//				NaParticles[0].points[i] = new Point(par.points[i].x, par.points[i].y);
//			}
//			NaParticles[0].distance = par.distance;
//		} else {
//			int replace = 0;
//			for (int i = 0; i != Nmax; i++) {
//				if (NaParticles[i].points[0] == null) {
//					breakPoint = i;
//				} else if (checkDominate(NaParticles[i], par) == true) {
//					dominated = true;
//					break;
//				} else if (checkDominate(par, NaParticles[i]) == true) {
//					dominate = true;
//					NaParticles[i] = new Path(numR);
//					replace = i;
//				}
//			}
//
//			// Neu khong bi dominated thi them vao
//			if (!dominated) {
//				if (dominate) { // Neu co phan tu bi par dominate
//					for (int j = 0; j != numR; j++) {
//						NaParticles[replace].angles[j] = par.angles[j];
//						NaParticles[replace].points[j] = new Point(par.points[j].x, par.points[j].y);
//
//					}
//					NaParticles[replace].distance = par.distance;
//				} else {
//					if (breakPoint != 100) { // Na not full, them truc tiep
//						for (int j = 0; j != numR; j++) {
//							NaParticles[breakPoint].angles[j] = par.angles[j];
//							NaParticles[breakPoint].points[j] = new Point(par.points[j].x, par.points[j].y);
//
//						}
//						NaParticles[breakPoint].distance = par.distance;
//					} else {
//						for (int j = 0; j != Nmax; j++) {
//							newPar[j] = new Path(numR);
//							for (int k = 0; k != numR; k++) {
//								newPar[j].angles[k] = NaParticles[j].angles[k];
//								newPar[j].points[k] = new Point(NaParticles[j].points[k].x, NaParticles[j].points[k].y);
//							}
//							newPar[j].distance = NaParticles[j].distance;
//						}
//						newPar[Nmax] = new Path(numR);
//						for (int j = 0; j != numR; j++) {
//							newPar[Nmax].angles[j] = par.angles[j];
//							newPar[Nmax].points[j] = new Point(par.points[j].x, par.points[j].y);
//
//						}
//						newPar[Nmax].distance = par.distance;
//
//						CD = crowdingDistance(newPar);
//						// find worst CD to remove
//						worstCD = CD[0];
//						worstCDid = 0;
//						for (int i = 0; i != Nmax + 1; i++) {
//							if (CD[i] < worstCD) { // ??? vi sao thay CD thap nhat
//								worstCD = CD[i];
//								worstCDid = i;
//							}
//						}
//						// thay the Na particle co CD thap nhat
//						if (worstCDid != Nmax) {
//							for (int i = 0; i != numR; i++) {
//								NaParticles[worstCDid].angles[i] = par.angles[i];
//								NaParticles[worstCDid].points[i] = new Point(par.points[i].x, par.points[i].y);
//							}
//							NaParticles[worstCDid].distance = par.distance;
//						}
//					}
//				}
//			}
//		}
//	}
//
//	public boolean compare(Path particle1, Path particle2) {
//		if (particle1.points[0] == null)
//			return false;
//		else if (particle2.points[0] == null)
//			return true;
//		else if (particle1.distance <= particle2.distance && particle1.pathSafety(graph) <= particle2.pathSafety(graph)
//				&& particle1.pathSmooth() <= particle2.pathSmooth())
//			return true;
//		else if (particle1.distance > particle2.distance && particle1.pathSafety(graph) > particle2.pathSafety(graph)
//				&& particle1.pathSmooth() > particle2.pathSmooth())
//			return false;
//		else {
//			double f = random.nextDouble();
//			if (f <= 0.5)
//				return true;
//			return false;
//		}
//	}
//
//	public boolean checkDominate(Path particle1, Path particle2) {
//		if (particle2.points[0] == null) {
//			return true;
//		} else if (particle1.distance <= particle2.distance
//				&& particle1.pathSafety(graph) <= particle2.pathSafety(graph)
//				&& particle1.pathSmooth() <= particle2.pathSmooth()) {
//			return true;
//		} else
//			return false;
//	}
//
//	public void run() {
//		double angle = determinePathAngle();
////		if (angle >= 60) {
////			maxAngle = 90;
////		} else if (angle >= 30) {
////			maxAngle = 60;
////		} else {
////			maxAngle = 30;
////		}
//		maxAngle = (int) (angle / 10) * 10 + 20;
//		minAngle = -maxAngle;
//		V_MAX = maxAngle;
//		V_MIN = minAngle;
//		initialize(numR);
//
//		for (int i = 0; i != NP; i++) {
//			pBest[i] = new Path(numR);
//			for (int j = 0; j != numR; j++) {
//				pBest[i].angles[j] = particles[i].angles[j];
//				pBest[i].points[j] = new Point(particles[i].points[j].x, particles[i].points[j].y);
//			}
//			pBest[i].distance = particles[i].distance;
//		}
//
//		initializeNaNb();
//
//		gBest = new Path(numR);
//		gBestSelection(0);
//
//		getVelocity();
//		boolean parColli, pBestColli;
//
//		// PSO
//		for (int i = 0; i != IT; i++) {
//			w = wMax - ((wMax - wMin) * i / IT);
//			for (int j = 0; j != NP; j++) {
//				for (int k = 0; k != numR; k++) {
//					r1 = random.nextDouble();
//					r2 = random.nextDouble();
//
//					vValue[j][k] = w * vValue[j][k] + c1 * r1 * (pBest[j].angles[k] - particles[j].angles[k])
//							+ r2 * c2 * (gBest.angles[k] - particles[j].angles[k]);
//					particles[j].angles[k] += vValue[j][k];
//
//					if (particles[j].angles[k] > maxAngle) {
//						particles[j].angles[k] = maxAngle;
//					} else if (particles[j].angles[k] < minAngle) {
//						particles[j].angles[k] = minAngle;
//					}
//
//					particles[j].points[k] = Path.convertPointToPoint(particles[j].angles[k], (k + 1) * R, startPoint,
//							endPoint);
//				}
//				particles[j].distance();
//
//				// MUTATE IF COLLIDE
//				parColli = pathCollision(particles[j]);
//				if (parColli == true) {
//					double numcolli = numberCollisions(particles[j]);
//					r = random.nextDouble();
//					pm = (Math.exp(numcolli) - Math.exp(-numcolli)) / (Math.exp(numcolli) + Math.exp(-numcolli));
//					if (r < pm) {
//						particles[j] = mutation(particles[j]);
//					}
//				}
//
//				parColli = pathCollision(particles[j]);
//				pBestColli = pathCollision(pBest[j]);
//				if (parColli && pBestColli) {
//					if (compare(particles[j], pBest[j])) {
//						for (int k = 0; k != numR; k++) {
//							pBest[j].angles[k] = particles[j].angles[k];
//							pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
//						}
//						pBest[j].distance = particles[j].distance;
//						addArchive(pBest[j], NbParticles);
//					}
//				} else if (!parColli && !pBestColli) {
//					if (compare(particles[j], pBest[j])) {
//						for (int k = 0; k != numR; k++) {
//							pBest[j].angles[k] = particles[j].angles[k];
//							pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
//						}
//						pBest[j].distance = particles[j].distance;
//						addArchive(pBest[j], NaParticles);
//					}
//				} else if (!parColli && pBestColli) {
//					for (int k = 0; k != numR; k++) {
//						pBest[j].angles[k] = particles[j].angles[k];
//						pBest[j].points[k] = new Point(particles[j].points[k].x, particles[j].points[k].y);
//					}
//					pBest[j].distance = particles[j].distance;
//					addArchive(pBest[j], NaParticles);
//				}
//
//			}
//
//			for (int j = 0; j < Nmax; j++) {
//				System.out.print("\nNa #" + j + ": ");
//				if (NaParticles[j].points[0] != null) {
//					for (int k = 0; k < numR; k++) {
//						System.out.print("(" + df.format(NaParticles[j].points[k].x) + ", "
//								+ df.format(NaParticles[j].points[k].y) + ")");
//					}
//					System.out.print(NaParticles[j].distance + " " + NaParticles[j].pathSafety(graph) + " "
//							+ NaParticles[j].pathSmooth());
//				}
//			}
//
//			gBestSelection(i);
//
//			System.out.println("\nEpochs " + i + " Best Value: " + gBest.distance + ", " + gBest.pathSafety(graph)
//					+ ", " + gBest.pathSmooth());
//			System.out.print("Epochs " + i + " Best Particles: (");
//			for (int j = 0; j < numR; j++) {
//				System.out.print("(" + df.format(gBest.points[j].x) + ", " + df.format(gBest.points[j].y) + ") ");
//			}
//			System.out.println();
//		}
//
//		result.add(startPoint);
//		for (int i = 0; i < numR; i++) {
//			result.add(gBest.points[i]);
//		}
//		result.add(endPoint);
//		length = gBest.distance;
//
//		result.removeLast();
//		result.removeFirst();
//	}
//	// end multi-objective

}
