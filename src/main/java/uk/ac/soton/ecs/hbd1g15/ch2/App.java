package uk.ac.soton.ecs.hbd1g15.ch2;

import java.io.IOException;
import java.net.URL;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;

/**
 * OpenIMAJ Processing first image
 *
 */
public class App {
    public static void main( String[] args ) {
    	
    	try {    		
    		// Read greyscale or coloured image
    		MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
    		
    		// Create a single display to show all the images in this tutorial
    		DisplayUtilities.createNamedWindow("frame", "Tutorial 2");
    		
    		// Display the image
    		DisplayUtilities.displayName(image, "frame");
    		DisplayUtilities.displayName(image.getBand(0), "frame");
    		
    		// Seet all the blue and green pixels to black
    		// Clone image to preserve it
    		MBFImage clone = image.clone();
    		clone.getBand(1).fill(0f);
    		clone.getBand(2).fill(0f);
    		DisplayUtilities.displayName(clone, "frame");
    		
    		// Run the Canny Edge Detection Algorithm on the image
    		DisplayUtilities.displayName(image.processInplace(new CannyEdgeDetector()), "frame");
    		
    		// Draw a speach bubble on the image
    		image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
    		image.drawShape(new Ellipse(700f, 450f, 20f, 10f, 0f), 10, RGBColour.RED);
    		image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
    		image.drawShape(new Ellipse(650f, 425f, 25f, 12f, 0f), 10, RGBColour.RED);
    		image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
    		image.drawShape(new Ellipse(600f, 380f, 30f, 15f, 0f), 10, RGBColour.RED);
    		image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
    		image.drawShape(new Ellipse(500f, 300f, 100f, 70f, 0f), 10, RGBColour.RED);
    		image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
    		image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
    		DisplayUtilities.displayName(image, "frame");
		} catch (IOException e) {
			// URL not found
			e.printStackTrace();
		}
    	
    	
    }
}
