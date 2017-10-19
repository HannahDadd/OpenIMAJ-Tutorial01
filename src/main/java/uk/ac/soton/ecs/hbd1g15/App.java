package uk.ac.soton.ecs.hbd1g15;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import java.net.*;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        // Load in imagae as an array of floats
        MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
        
        // Display loaded image and red channel
        DisplayUtilities.display(image);
        DisplayUtilities.display(image.getBand(0), "Red Channel");
        MBFImage clone = image.clone();

        // Go through each pixel and set all blue and green pixels to black
        for (int y=0; y<image.getHeight(); y++) {
            for(int x=0; x<image.getWidth(); x++) {
                clone.getBand(1).pixels[y][x] = 0;
                clone.getBand(2).pixels[y][x] = 0;
            }
        }
        DisplayUtilities.display(clone);
    }
}
