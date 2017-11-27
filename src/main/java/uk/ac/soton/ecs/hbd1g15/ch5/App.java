package uk.ac.soton.ecs.hbd1g15.ch5;

import java.io.IOException;
import java.net.URL;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicTwoWayMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;

/**
 * OpenIMAJ Compare images to each other using local feature detector, SIFT
 *
 */
public class App {
	public static void main(String[] args) {

		try {
			MBFImage target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));
			MBFImage query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));

			// Find the features in the image
			DoGSIFTEngine engine = new DoGSIFTEngine();
			LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
			LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

			// Take a given keypoint and find closest keypoint to find which keypoints match
			// Exercise 1- using the BasicTwoWayMatcher instead of the BasicMatcher with param 80
			LocalFeatureMatcher<Keypoint> matcher = new BasicTwoWayMatcher<Keypoint>();
			matcher.setModelFeatures(queryKeypoints);
			matcher.findMatches(targetKeypoints);

			// Draw matches between 2 images
			MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
			DisplayUtilities.display(basicMatches);

			// Use RANSAC to find Affine Transforms
			// Exercise 2- Use HomographyModel in consistent matcher
			RobustHomographyEstimator modelFitter = new RobustHomographyEstimator(5.0, 1500,
					new RANSAC.PercentageInliersStoppingCondition(0.5), HomographyRefinement.NONE);
			matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(8),
					modelFitter);
			matcher.setModelFeatures(queryKeypoints);
			matcher.findMatches(targetKeypoints);
			MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
					RGBColour.RED);
			DisplayUtilities.display(consistentMatches);

			// Draw polygon around where red lines are centred for query result
			target.drawShape(query.getBounds().transform(modelFitter.getModel().getTransform().inverse()), 3,
					RGBColour.BLUE);
			DisplayUtilities.display(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
