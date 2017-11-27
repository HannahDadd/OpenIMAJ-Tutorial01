package uk.ac.soton.ecs.hbd1g15.ch4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

/**
 * OpenIMAJ Extract numerical representations from images
 *
 */
public class App {
    public static void main( String[] args ) {
    	try {
			// Different images will have different histogram values
			URL[] imageURLs = new URL[] {
			   new URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist1.jpg" ),
			   new URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist2.jpg" ), 
			   new URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist3.jpg" ) 
			};
			
			// HistogramModel assumes image has been normalised, histogram will also be normalised
			List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
			
			// Construct histogram with no. of bins required
			HistogramModel model = new HistogramModel(4, 4, 4);
			
			for( URL u : imageURLs ) {
			    model.estimateModel(ImageUtilities.readMBF(u));
			    
			    // Histogram must be cloned for multiple images or they will all be the same object
			    histograms.add(model.histogram.clone());
			}
			
			// Exercise 1- find the most similar histograms
			int[] mostSimilar = new int[2];
			double leastDistance = 100000;
			
			// Compare all the histograms to each other
			for( int i = 0; i < histograms.size(); i++ ) {
			    for( int j = i; j < histograms.size(); j++ ) {
			    	// Compare all histograms to each other using Euclidean distance measure
			        double distance = histograms.get(i).compare( histograms.get(j), DoubleFVComparison.EUCLIDEAN );
			        
			        // If their is less distance between them they are more similar
			        if(distance<leastDistance) {
			        	leastDistance = distance;
			        	mostSimilar[0] = i;
			        	mostSimilar[1] = j;
			        }
			    }
			}
			DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[0]));
			DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[1]));
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
