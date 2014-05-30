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
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.roxtr.iot.Constants;

public class UtilsJavaCv implements Constants {

	static {
		Loader.load(org.bytedeco.javacpp.opencv_core.class);
	}
	// 0-default camera, 1 - next...so on
	private static OpenCVFrameGrabber sGrabber = new OpenCVFrameGrabber(0);

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

		srcImg = cropFace(srcImg);
		if (srcImg != null) {
			srcImg = processImg(srcImg);
		}

		return srcImg;
	}

	public static Mat captureAndSaveFaceFromWebcam(String fileName) {
		Mat webcamImg;
		webcamImg = captureFaceFromWebcam();
		if (webcamImg != null) {
			imwrite(fileName, webcamImg);
			return webcamImg;
		} else {
			System.out.println("Failed to capture face from webcam: ");
		}

		return webcamImg;
	}
	
	/*private static Mat takePicFsCam() {
		
	}*/

	public static Mat takePic() {
		return takePic(0);
	}
	
	public static Mat takePic(int camId) {
		
		Mat imgMat = null;
		try {
			if(sGrabber == null) {
				sGrabber = new OpenCVFrameGrabber(camId);
			}
            sGrabber.start();
            IplImage iplImg = sGrabber.grab();
            if (iplImg != null) {
                imgMat = new Mat(iplImg);
            }
        
			log("Got the image from webcam! " + "[" + imgMat.arrayWidth() + "x"
					+ imgMat.arrayHeight() + "]");
			
			// displayImg(img);
		} catch (Exception e) {
            e.printStackTrace();
        }

		return imgMat;
	}
	
	public static Mat cropFace(Mat img) {
		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(CLASIFIER);

		// Detect faces in the image.
		Rect rect = new Rect();
		faceDetector.detectMultiScale(img, rect);
		if(rect.width() == 0 || rect.height() == 0) {
			return null;
		}

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
		try {
			sGrabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}
		sGrabber = null;
	}

	public static void log(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) {
		System.out.println("Cheeeeese...");
		
		int vidId = 0;
		
		if(args.length >= 1) {
			vidId = Integer.parseInt(args[1]);
		}
		System.out.println("Using vidId: "+vidId);
		
		Mat webcamImg = takePic(vidId);
		if(webcamImg != null) {
			imwrite("data/imgs/webcam.png", webcamImg);
		} else {
			System.out.println("Unable to dectect face from webcam..");
		}
		
		close();
	}
}
