package uk.ac.soton.ecs.hbd1g15.ch14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.time.Timer;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;
import org.openimaj.util.parallel.partition.RangePartitioner;

/**
 * OpenIMAJ Parallel Processing
 *
 */
public class App {
	public static void main(String[] args) {
		// Test parallel processing
		Parallel.forIndex(0, 10, 1, new Operation<Integer>() {
			public void perform(Integer i) {
				System.out.println(i);
			}
		});
		try {
			// Use first 8 groups of Caltech db
			VFSGroupDataset<MBFImage> allImages = Caltech101.getImages(ImageUtilities.MBFIMAGE_READER);
			GroupedDataset<String, ListDataset<MBFImage>, MBFImage> images = GroupSampler.sample(allImages, 8, false);

			// Time how long it takes
			Timer t1 = Timer.timer();

			// Build average image for each group
			final List<MBFImage> output = new ArrayList<MBFImage>();
			final ResizeProcessor resize = new ResizeProcessor(200);

			for (ListDataset<MBFImage> clzImages : images.values()) {
				final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
				// Loop through images in group, feed pationed variant to feed thread a
				// collection of images
				// Range partition will break code into as many equally sized chunks as possible
				Parallel.forEachPartitioned(new RangePartitioner<MBFImage>(clzImages),
						new Operation<Iterator<MBFImage>>() {
							public void perform(Iterator<MBFImage> it) {
								MBFImage tmpAccum = new MBFImage(200, 200, 3);
								MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
								while (it.hasNext()) {
									// Re-sample, normalise and draw in centre of white image
									final MBFImage i = it.next();
									tmp.fill(RGBColour.WHITE);
									final MBFImage small = i.process(resize).normalise();
									final int x = (200 - small.getWidth()) / 2;
									final int y = (200 - small.getHeight()) / 2;
									tmp.drawImage(small, x, y);
									tmpAccum.addInplace(tmp);
								}
								// Add result to accumulator
								synchronized (current) {
									current.addInplace(tmpAccum);
								}
							}
						});
				// Divide accumulated image by number of samples used to create it
				current.divideInplace((float) clzImages.size());
				output.add(current);
			}
			DisplayUtilities.display("Images", output);
			System.out.println("Time: " + t1.duration() + "ms");
			
			Timer t2 = Timer.timer();
			final List<MBFImage> outputTwo = new ArrayList<MBFImage>();

			// Exercise 1- Parallelise the outer loop
			// Parallelising the outer loop is slower than the inner loop
			Parallel.forEach(images.values(), new Operation<ListDataset<MBFImage>>() {
				public void perform(ListDataset<MBFImage> clzImages) {
					final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
					for (MBFImage i : clzImages) {
						MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
						tmp.fill(RGBColour.WHITE);

						MBFImage small = i.process(resize).normalise();
						int x = (200 - small.getWidth()) / 2;
						int y = (200 - small.getHeight()) / 2;
						tmp.drawImage(small, x, y);

						current.addInplace(tmp);
					}
					synchronized (current) {
						current.divideInplace((float) clzImages.size());
						outputTwo.add(current);
					}
				}
			});
			DisplayUtilities.display("Images", outputTwo);
			System.out.println("Time: " + t2.duration() + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}