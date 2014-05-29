package com.roxtr.iot.facerec;

import static org.bytedeco.javacpp.opencv_highgui.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_highgui.VideoCapture;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class UtilsJavaCv implements Constants {

	static {
		Loader.load(org.bytedeco.javacpp.opencv_core.class);
	}
	private static VideoCapture sVideo = null;

	public static void displayImg(Mat matImg) {
		try {
			displayImg(toBufferedImage(matImg));
		} catch (IOException e) {
			log("Unable to display image: " + e.getMessage());
		}
	}

	public static void displayImg(Image awtImg) {
		ImageIcon icon = new ImageIcon(awtImg);
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(awtImg.getWidth(null) + 50, awtImg.getHeight(null) + 50);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static BufferedImage toBufferedImage(Mat matImg)
			throws IOException {
		return matImg.getBufferedImage();
	}

	public static Mat captureFaceFromWebcam() {

		Mat srcImg = takePic();

		// Debug:
		// displayImg(srcImg, ".jpg");

		srcImg = cropFace(srcImg);
		if (srcImg != null) {
			srcImg = processImg(srcImg);
		}

		return srcImg;
	}

	public static boolean captureAndSaveFaceFromWebcam(String fileName) {
		Mat webcamImg;
		webcamImg = captureFaceFromWebcam();
		if (webcamImg != null) {
			imwrite(fileName, webcamImg);
			return true;
		} else {
			System.out.println("Failed to capture face from webcam: ");
		}

		return false;
	}

	public static Mat takePic() {
		Mat img = new Mat();
		if (sVideo == null) {
			sVideo = new VideoCapture();
			sVideo.open(0);
		}
		sVideo.retrieve(img);
		log("Got the image from webcam! " + "[" + img.arrayWidth() + "x"
				+ img.arrayHeight() + "]");
		
		displayImg(img);

		return img;
	}

	public static Mat cropFace(Mat img) {
		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(CLASIFIER);

		// Detect faces in the image.
		Rect rect = new Rect();
		faceDetector.detectMultiScale(img, rect);

		/*
		 * log("Detected " + faceDetections.toArray().length + " faces");
		 * if(faceDetections.toArray().length == 0) {
		 * log("No faces detected from the image taken!"); return null; } //
		 * Detect only 1 face: Rect rect = faceDetections.toArray()[0];
		 */

		// Crop the image:
		log("rec: " + rect.x() + "; " + rect.y() + "; " + rect.width() + "; "
				+ rect.height());
		return new Mat(img, rect);
	}

	public static Mat processImg(Mat img) {
		// Resize to 100x100:
		resize(img, img, new Size(IMG_SIZE, IMG_SIZE));

		// To grayscale:
		cvtColor(img, img, COLOR_RGB2GRAY);

		// Equalize:
		equalizeHist(img, img);

		return img;
	}

	public static void close() {
		sVideo.release();
		sVideo = null;
	}

	public static void log(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) {
		System.out.println("Cheeeeese...");
		Mat webcamImg = captureFaceFromWebcam();
		if(webcamImg != null) {
			imwrite("data/imgs/webcam.png", webcamImg);
		} else {
			System.out.println("Unable to dectect face from webcam..");
		}
	}
}
