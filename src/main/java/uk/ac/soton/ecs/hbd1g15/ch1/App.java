package uk.ac.soton.ecs.hbd1g15.ch1;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        //Create an image
        MBFImage image = new MBFImage(1200,140, ColourSpace.RGB);

        //Fill the image with white
        image.fill(RGBColour.WHITE);
        		        
        //Render some test into the image
        // Exercise 1- Different font and different colour
        image.drawText("I was going to read something of Shakespeare's", 10, 60, HersheyFont.GOTHIC_ENGLISH, 50, RGBColour.RED);
        image.drawText("but he never reads anything of mine", 10, 120, HersheyFont.GOTHIC_ENGLISH, 50, RGBColour.RED);

        //Apply a Gaussian blur
        image.processInplace(new FGaussianConvolve(2f));
        
        //Display the image
        DisplayUtilities.display(image);
    }
}
