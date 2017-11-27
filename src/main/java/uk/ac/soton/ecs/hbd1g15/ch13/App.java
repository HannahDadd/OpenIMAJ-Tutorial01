package uk.ac.soton.ecs.hbd1g15.ch13;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.model.EigenImages;

/**
 * OpenIMAJ Face recognition 101: Eigenfaces
 *
 */
public class App {
	public static void main(String[] args) {
		try {
			// Load in the dataset
			VFSGroupDataset<FImage> dataset = new VFSGroupDataset<FImage>(
					"zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);

			// Split into a training and a testing dataset
			// Exercise 2- Reducing the number of training images makes the images more
			// blurred and less clear
			int nTraining = 3;
			int nTesting = 5;
			GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining,
					0, nTesting);
			GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
			GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();

			// Pass the list of images for testing and dimensions of features (how many
			// eigenvectors correspond to eigenvalues)
			List<FImage> basisImages = DatasetAdaptors.asList(training);
			int nEigenvectors = 100;
			EigenImages eigen = new EigenImages(nEigenvectors);
			eigen.train(basisImages);

			// First 12 vectors
			List<FImage> eigenFaces = new ArrayList<FImage>();
			for (int i = 0; i < 12; i++) {
				eigenFaces.add(eigen.visualisePC(i));
			}
			DisplayUtilities.display("EigenFaces", eigenFaces);

			// Build database of features from training images
			Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
			for (final String person : training.getGroups()) {
				final DoubleFV[] fvs = new DoubleFV[nTraining];

				for (int i = 0; i < nTraining; i++) {
					final FImage face = training.get(person).get(i);
					fvs[i] = eigen.extractFeature(face);
				}
				features.put(person, fvs);
			}

			// Exercise 1- Take a feature from a face in training set and build a face from
			// it
			Random rnd = new Random();
			int i = rnd.nextInt(training.getGroups().size());
			FImage image = eigen.reconstruct(features.get(training.getGroups().toArray()[i])[0]);
			DisplayUtilities.display("Exercise 1", image.normalise());

			// Loop through each image and estimate which person they belong to
			double correct = 0, incorrect = 0;
			for (String truePerson : testing.getGroups()) {
				for (FImage face : testing.get(truePerson)) {
					DoubleFV testFeature = eigen.extractFeature(face);
					String bestPerson = null;
					double minDistance = Double.MAX_VALUE;
					for (final String person : features.keySet()) {
						// Extract feature with smallest distance
						for (final DoubleFV fv : features.get(person)) {
							double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);
							if (distance < minDistance) {
								minDistance = distance;
								bestPerson = person;
							}
						}
					}

					// Exercise 3- Add a threshold that distance between the query face and closest
					// database face must be greater than
					// Threshold was decided based on the accuracy
					double threshold = 17;
					if(minDistance>threshold) {
						minDistance = Double.MAX_VALUE;
						bestPerson = "unknown";
					}
					System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);
					if (truePerson.equals(bestPerson))
						correct++;
					else
						incorrect++;
				}
			}
			System.out.println("Accuracy: " + (correct / (correct + incorrect)));
		} catch (FileSystemException e) {
			e.printStackTrace();
		}

	}
}
