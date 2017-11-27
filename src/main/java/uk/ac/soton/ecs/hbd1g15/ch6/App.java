package uk.ac.soton.ecs.hbd1g15.ch6;

import java.util.Map.Entry;

import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.dataset.BingImageDataset;
import org.openimaj.image.dataset.FlickrImageDataset;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.BingAPIToken;
import org.openimaj.util.api.auth.common.FlickrAPIToken;

/**
 * OpenIMAJ Image Datasets
 *
 */
public class App {
    public static void main( String[] args ) {
    	try {
    		// Excercies 2- other sources supported for building datasets: Zip files, local disk or HTTP server
			// Create image data set from images held in a zip file
			VFSListDataset<FImage> faces = 
					new VFSListDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
			DisplayUtilities.display("ATT faces", faces);
			
			// Maintain the associations between the directories and images inside them
			VFSGroupDataset<FImage> groupedFaces = 
					new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
			
			// Have to loop through keys, generated from the directory names of images to display them
			for (final Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()) {
				
				// Exercise 1- Display a random image of each face from the dataset
				DisplayUtilities.display(entry.getValue().getRandomInstance());
			}
			
			// Exercise 3- Bing image dataset
			BingAPIToken bingToken = DefaultTokenFactory.get(BingAPIToken.class);
			BingImageDataset<FImage> famousOne =
					BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "Prince Harry", 10);
			BingImageDataset<FImage> famousTwo =
					BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "Jane Austen", 10);
			BingImageDataset<FImage> famousThree =
					BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "JK Rolling", 10);
			// Exercise 4- Make data set of famous people
			MapBackedDataset.of(famousOne, famousTwo, famousThree);
			DisplayUtilities.display("Dogs", famousThree);
			
			// Flickr finds tagged images, can dynamically construct a dataset from a Flickr search
			FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);
			FlickrImageDataset<FImage> cats = 
					FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
			DisplayUtilities.display("Cats", cats);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
