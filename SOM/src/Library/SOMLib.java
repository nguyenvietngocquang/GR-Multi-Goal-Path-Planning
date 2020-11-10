package Library;

import java.io.File;

import net.sf.javaml.clustering.SOM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class SOMLib {

	public static void main(String[] args) throws Exception {
		Dataset data = FileHandler.loadDataset(new File("iris.data"), 4, ",");

		SOM som = new SOM(10, 10, SOM.GridType.HEXAGONAL, 10000, 0.1, 6, SOM.LearningType.EXPONENTIAL,
				SOM.NeighbourhoodFunction.GAUSSIAN);

		Dataset[] result = som.cluster(data);

		for (int i = 0; i < result.length; i++) {
			System.out.println("Cluster " + (i + 1) + ":\t" + result[i].size());
		}

	}

}
