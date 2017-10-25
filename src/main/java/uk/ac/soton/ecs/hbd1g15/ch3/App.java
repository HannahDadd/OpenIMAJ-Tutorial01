package uk.ac.soton.ecs.hbd1g15.ch3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;

/**
 * OpenIMAJ Clustering, Segmentation and Connected Components
 *
 */
public class App {
    public static void main( String[] args ) {
    	try {
			MBFImage input = ImageUtilities.readMBF(new URL("http://media.beliefnet.com/~/media/photos-with-attribution/entertainment/movies/jack-sparrow_credit-walt-disney-studios.jpg"));
			
			// Apply colour space transform
			input = ColourSpace.convert(input, ColourSpace.CIE_Lab);
			
			// Construct K-Mean clustering algorithm
			// 2- no. of clusters, can add second param for no. of iterations
			FloatKMeans cluster = FloatKMeans.createExact(2);
			
			// Turn picture into dobule array of floating point vectors
			float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);
			
			// Group pixels in requested no. of classes
			FloatCentroidsResult result = cluster.cluster(imageData);
			float[][] centroids = result.centroids;
			
			// Classification: assign each pixel to its centroid made in KMean algorithm
			HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();
			for (int y=0; y<input.getHeight(); y++) {
			    for (int x=0; x<input.getWidth(); x++) {
			        float[] pixel = input.getPixelNative(x, y);
			        int centroid = assigner.assign(pixel);
			        input.setPixelNative(x, y, centroids[centroid]);
			    }
			}
			
			// Convert it back to RGB to display it
			input = ColourSpace.convert(input, ColourSpace.RGB);
			DisplayUtilities.display(input);
			
			// Each sets of pixels, representing a segment called connected component, needs to be put together
			GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
			List<ConnectedComponent> components = labeler.findComponents(input.flatten());
			
			// Draw image with component no. on its
			int i = 0;
			for (ConnectedComponent comp : components) {
			    if (comp.calculateArea() < 50) 
			        continue;
			    input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
			}
			DisplayUtilities.display(input);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
}