package com.roxtr.iot.facerec;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

//
//Detects faces in an image, draws boxes around them, and writes the results
//to "faceDetection.png".
//
class DetectFaceDemo {
public void run() {
 System.out.println("\nRunning DetectFaceDemo");

 // Create a face detector from the cascade file in the resources
 // directory.
 CascadeClassifier faceDetector = new CascadeClassifier("D:/personal-workspace/OpenCV/lbpcascade_frontalface.xml");
 Mat image = Highgui.imread("D:/personal-workspace/OpenCV/pic2.jpg");

 // Detect faces in the image.
 // MatOfRect is a special container class for Rect.
 MatOfRect faceDetections = new MatOfRect();
 faceDetector.detectMultiScale(image, faceDetections);

 System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

 // Draw a bounding box around each face.
 for (Rect rect : faceDetections.toArray()) {
     Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
     Mat imCorp = image.submat(rect);
   Highgui.imwrite(new Integer(rect.x).toString()+".jpg", imCorp);
 
 }

 // Save the visualized detection.
 String filename = "pic2new.jpg";
 System.out.println(String.format("Writing %s", filename));
 Highgui.imwrite(filename, image);
}
}


public class HelloOpenCV {
  public static void main(String[] args) {
    System.out.println("Hello, OpenCV");

    // Load the native library.
    System.loadLibrary("opencv_java249");
    new DetectFaceDemo().run();
  }
}
