package com.roxtr.iot.facerec;

import static org.bytedeco.javacpp.opencv_contrib.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.Scanner;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;

// import org.opencv.core.Core;

public class NewFaceRecognizer {

	public static void main(String[] args) {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Loader.load(org.bytedeco.javacpp.opencv_core.class);

		String trainingDir = null; // args[0];
		String targetImg = null; // args[1];

		trainingDir = "data/imgs/All1";
		
		// Get file cnt: 
    	int fileCnt = (new File(trainingDir)).list().length;
    	System.out.println("fileCnt: "+fileCnt);
    	targetImg = trainingDir + "/" + (fileCnt + 1) + ".jpg";

		File root = new File(trainingDir);

		FilenameFilter imgFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".pgm")
						|| name.endsWith(".png");
			}
		};

		File[] imageFiles = root.listFiles(imgFilter);

		MatVector images = new MatVector(imageFiles.length);

		Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
		IntBuffer labelsBuf = labels.getIntBuffer();

		int counter = 0;
		String[] names = new String[imageFiles.length];
		for (File image : imageFiles) {
			Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

			int label = Integer.parseInt(image.getName().split("\\.")[0]);
			names[counter] = image.getName();
			images.put(counter, img);

			labelsBuf.put(counter, label);

			counter++;
		}

		// FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
		// FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
		FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

		faceRecognizer.train(images, labels);

		Scanner reader = new Scanner(System.in);
		try {
			while(true) {
				if (Utils.captureAndSaveFaceFromWebcam(targetImg)) {
					System.out.println("Comparing " + targetImg + " ...");
					Mat testImage = imread(targetImg, CV_LOAD_IMAGE_GRAYSCALE);
					
					int predictedLabel = faceRecognizer.predict(testImage);
		
					System.out.println("Predicted label: " + predictedLabel);
				} else {
					System.out.println("No face deteced from the webcam..");
				}
				
				System.out.println("\n*********** \nPress any key for Next capture..");
				reader.next();
				fileCnt++;
				targetImg = trainingDir + "/" + (fileCnt + 1) + ".jpg";
				
				Utils.close();
			}
		} catch (Exception ex) {
			
		} finally {
			reader.close();
		}

	}

}
