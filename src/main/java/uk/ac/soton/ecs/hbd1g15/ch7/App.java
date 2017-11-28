package uk.ac.soton.ecs.hbd1g15.ch7;

import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.SUSANEdgeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleVideo;

/**
 * OpenIMAJ Processing Video
 *
 */
public class App {
	public static void main(String[] args) {
		try {
			// Video holds coloured frames which 
			Video<MBFImage> video = new XuggleVideo(new URL("http://static.openimaj.org/media/tutorial/keyboardcat.flv"));
			
			// Event driven way of applying canny edge detection to each frame in video
			VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
			display.addVideoListener(
			  new VideoDisplayListener<MBFImage>() {
			    public void beforeUpdate(MBFImage frame) {
			        //frame.processInplace(new CannyEdgeDetector());

					// Exercise 1- different processing operation
			        // Slightly slower with larger edge detection highlighted
			        frame.processInplace(new SUSANEdgeDetector());
			    }

			    public void afterUpdate(VideoDisplay<MBFImage> display) {
			    }
			  });
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
