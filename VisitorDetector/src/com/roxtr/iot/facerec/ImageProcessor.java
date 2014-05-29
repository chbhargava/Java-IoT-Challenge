package com.roxtr.iot.facerec;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class ImageProcessor implements Constants {

	private static final String TGT_DIR = "data/imgs/kalli";

	public static void main(String[] args) {
		rename();
		//process();
	}
	
	private static void rename() {
		
		File picsDir = new File(TGT_DIR + "/faces");
		
		int index = 141;
		for(File eachFile : picsDir.listFiles()) {
			eachFile.renameTo(new File(TGT_DIR + "/" + index + ".jpg"));
			index++;
		}
	}
	
	private static void process() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	
    	(new File(TGT_DIR + "/faces")).mkdirs();
    	
    	File picsDir = new File(TGT_DIR + "/raw");
    	
    	int index = 76;
     	for(String eachFile : picsDir.list()) {
 			String fileName = TGT_DIR + "/raw/" + eachFile;
 			
 			System.out.println("Processing: "+fileName);
 			Mat srcImg = Highgui.imread(fileName);
 			
 			// Utils.displayImg(srcImg, ".jpg");
 			
 			Mat justFaceImg = Utils.cropFace(srcImg);
 			if(justFaceImg != null) {
 				justFaceImg = Utils.processImg(justFaceImg);
     			Highgui.imwrite(TGT_DIR + "/faces/" + index + ".jpg", justFaceImg);
     			index++;
 			}
     	} 
	}
}
