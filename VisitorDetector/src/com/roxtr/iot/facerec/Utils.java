package com.roxtr.iot.facerec;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Utils implements Constants {
	
	public static void displayImg(Mat matImg, String imgFormat) {
		try {
			displayImg(toBufferedImage(matImg, imgFormat));
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
	
	public static Mat toMat(BufferedImage webcamImg) {
		byte[] data = ((DataBufferByte) webcamImg.getRaster().getDataBuffer()).getData();
		Mat image = new Mat(webcamImg.getHeight(), webcamImg.getWidth(), CvType.CV_8UC3);
		image.put(0, 0, data);
		
		return image;
	}
	
	
	public static BufferedImage toBufferedImage(Mat matImg, String imgFormat) throws IOException {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(imgFormat, matImg, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		return ImageIO.read(in);
	}
	
	public static Mat captureFaceFromWebcam() throws IOException {
		
		Mat srcImg = takePic();
		
		// Debug:
		// displayImg(srcImg, ".jpg");
		
		srcImg = cropFace(srcImg);
		if(srcImg != null) {
			srcImg = processImg(srcImg);
		}
		
		return srcImg;
	}
	
	public static boolean captureAndSaveFaceFromWebcam(String fileName) 
			{
		Mat webcamImg;
		try {
			webcamImg = captureFaceFromWebcam();
			if(webcamImg != null) {
				Highgui.imwrite(fileName, webcamImg);
				return true;
			}
		} catch (IOException e) {
			System.out.println("Failed to capture face from webcam: "+e.getMessage());
		}
		
		return false;
	}
	
	private static VideoCapture sVideo = null;
	public static Mat takePic() {
		Mat img = new Mat();
		if(sVideo == null) {
			sVideo = new VideoCapture();
			sVideo.open(0);
		}
		sVideo.retrieve(img);
		log("Got the image from webcam! " +
				"["+ img.width() + "x" +img.height() +"]");
		
		return img;
	}
	
	public static Mat cropFace(Mat img) {
		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(CLASIFIER);

		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(img, faceDetections);

		log("Detected " + faceDetections.toArray().length
				+ " faces");
		if(faceDetections.toArray().length == 0) {
			log("No faces detected from the image taken!");
			return null;
		}
		// Detect only 1 face: 
		Rect rect = faceDetections.toArray()[0];
		
		// Crop the image: 
		log("rec: "+rect.x + "; " + rect.y  + "; " + rect.width  + "; " + rect.height);
		return new Mat(img, rect);
	}

	public static Mat processImg(Mat img) {
		// Resize to 100x100:
		Imgproc.resize(img, img, new Size(IMG_SIZE, IMG_SIZE));

		// To grayscale: 
	    Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);

		// Equalize:
	    Imgproc.equalizeHist(img, img);
	    
	    return img;
	}
	
	public static void close() {
		sVideo.release();
		sVideo = null;
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
}
