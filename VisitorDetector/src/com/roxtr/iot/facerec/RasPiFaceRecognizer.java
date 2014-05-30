package com.roxtr.iot.facerec;

import static org.bytedeco.javacpp.opencv_contrib.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Scanner;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;

import com.roxtr.iot.Constants;
import com.roxtr.iot.notif.client.DNSender;

// import org.opencv.core.Core;

/**
 * FaceRec [imgFile] [captureFromWebcam]
 * 
 * captureFromWebcam: 1 -> Yes (Default) captureFromWebcam: 0 -> No
 * 
 */
public class RasPiFaceRecognizer implements Constants {

	private static final double THRESHOLD = 5000;
	private static final String TRAINING_IMG_DIR = IMGS_DIR + "/Training";

	private static boolean sCaptureFromWebcam = true;
	private static String sDnServerIp;
	
	private FaceRecognizer mRecognizer = null;
	private String[] mNamesMap = null;
	
	private static RasPiFaceRecognizer sInstance = new RasPiFaceRecognizer();
	
	static {
		Loader.load(org.bytedeco.javacpp.opencv_core.class);
	}
	
	public static void setDnServerHost(String dnServerIp) {
		sDnServerIp = dnServerIp;
	}

	private RasPiFaceRecognizer() {
	}
	public static RasPiFaceRecognizer getInstance() {
		return sInstance;
	}

	public void train() {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Get file cnt:
		System.out.println("Training the data..");
		File root = new File(TRAINING_IMG_DIR);
		
		// Get number of images to train:
		int numOfImgs = 0, numOfPersons = 0;
		File[] listOfDirs = root.listFiles();
		for(File eachDir : listOfDirs) {
			if(eachDir.isDirectory()) {
				numOfImgs += eachDir.list().length;
			}
			numOfPersons++;
		}
		
		mNamesMap = new String[numOfPersons];
		
		MatVector images = new MatVector(numOfImgs);
		Mat labels = new Mat(numOfImgs, 1, CV_32SC1);
		IntBuffer labelsBuf = labels.getIntBuffer();

		int counter = 0, personId = 0;
		for(File eachDir : listOfDirs) {
			if(eachDir.isDirectory()) {
				String personName = eachDir.getName();
				for(File image : eachDir.listFiles()) {
					System.out.println(counter + " - > " + image.getPath());
					Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

					mNamesMap[personId] = personName;

					images.put(counter, img);
					labelsBuf.put(counter, personId);
					counter++;
				}
				personId++;
			}
		}
	
		mRecognizer = createFisherFaceRecognizer();
		// FaceRecognizer rec = createEigenFaceRecognizer();
		// FaceRecognizer rec = createLBPHFaceRecognizer();

		mRecognizer.train(images, labels);
		System.out.println("Training complete!");
	}
	
	public boolean recognizeThisImg(Mat tgtImgMat) {
		if (tgtImgMat != null && !tgtImgMat.empty()) {
			
			IntBuffer label = tgtImgMat.getIntBuffer();
			DoubleBuffer conf = tgtImgMat.getDoubleBuffer();
			mRecognizer.predict(tgtImgMat, label, conf);
			int personId = label.get();
			double confDbl = conf.get();
			
			String personName = mNamesMap[personId];
			System.out.println("createFisherFaceRecognizer -> personId: "
					+ personId + "; personName: " + personName + "; confidence: " + confDbl);
			System.out.println("Sending notification..");
			
			if (Double.compare(confDbl, THRESHOLD) < 0) {
				System.out.println("Match Found!!");
				// Send desktop notifications: 
				(new DNSender(sDnServerIp)).sendDN(
						"Known person at your doorstep!", 
						"'"+personName + "' is at your door step!");
				return true;
			} else {
				System.out.println("Match not found..");
				(new DNSender(sDnServerIp)).sendDN(
						"Uknown person at your doorstep", 
						"Unknow person waiting at your door! ");
			}
		} else {
			System.out.println("No face deteced in the img given!");
		}

		return false;
	}

	private String getTgtImgFileFromArgs(String[] args) {
		String targetImg = null;
		// Target ImageFile: 
		if (args.length >= 1) {
			targetImg = args[0];
			sCaptureFromWebcam = false;
		} else {
			targetImg = IMGS_DIR + "/"+(new Date()).getTime()+".jpg";
		}
		return targetImg;
	}
	
	public static Mat processTgtImg(String targetImg) {
		Mat testImage = imread(targetImg, CV_LOAD_IMAGE_GRAYSCALE);
		testImage = UtilsJavaCv.cropFace(testImage);

		if (testImage != null) {
			resize(testImage, testImage, new Size(IMG_SIZE, IMG_SIZE));
			equalizeHist(testImage, testImage);
		}
		return testImage;
	}
	
	public static void main(String[] args) {

		RasPiFaceRecognizer recognizer = RasPiFaceRecognizer.getInstance();
		recognizer.train();
		
		Mat tgtImgMat = null;
		String tgtImgFile = recognizer.getTgtImgFileFromArgs(args);
		System.out.println("Comparing "+tgtImgFile);
		
		Scanner keyboard = new Scanner(System.in);
		try {
			while(true) {
				if(sCaptureFromWebcam) {
					System.out.println("Capturing from webcam.. cheeese!!");
					tgtImgMat = UtilsJavaCv.captureAndSaveFaceFromWebcam(tgtImgFile);
				} else {
					tgtImgMat = processTgtImg(tgtImgFile);
				}
				
				recognizer.recognizeThisImg(tgtImgMat);
				UtilsJavaCv.close();
				
				System.out.println("Press any key for next capture..");
				keyboard.nextLine();
			}
		} finally {
			keyboard.close();
		}
	}

}
