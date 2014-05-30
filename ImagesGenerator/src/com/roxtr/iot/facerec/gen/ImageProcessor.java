package com.roxtr.iot.facerec;

import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imwrite;

import java.io.File;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.Mat;

import com.roxtr.iot.Constants;

public class ImageProcessor implements Constants {

	private static String TGT_DIR = ImageGenerator.TGT_DIR;
	static {
		Loader.load(org.bytedeco.javacpp.opencv_core.class);
	}

	public static void main(String[] args) {
		
		String opt = "2";
		if(args.length >= 1) {
			opt = args[0];
		}
		if(args.length >= 2) {
			TGT_DIR = args[1];
		}
		
		if("1".equals(opt)) {
			process();
		}
		if("2".equals(opt)) {
			rename();
		}
	}
	
	private static void rename() {
		
		File picsDir = new File(TGT_DIR);
		(new File(TGT_DIR + "/indexed")).mkdirs();
		
		int index = 90;
		for(File eachFile : picsDir.listFiles()) {
			eachFile.renameTo(new File(TGT_DIR + "/" + index + ".jpg"));
			index++;
		}
	}
	
	public static void process() {
    	(new File(TGT_DIR + "/faces")).mkdirs();
    	
    	File picsDir = new File(TGT_DIR + "/raw");
    	
    	int index = 76;
     	for(String eachFile : picsDir.list()) {
 			String fileName = TGT_DIR + "/raw/" + eachFile;
 			
 			System.out.println("Processing: "+fileName);
 			Mat srcImg = imread(fileName);
 			
 			// Utils.displayImg(srcImg, ".jpg");
 			
 			Mat justFaceImg = UtilsJavaCv.cropFace(srcImg);
 			if(justFaceImg != null) {
 				justFaceImg = UtilsJavaCv.processImg(justFaceImg);
     			imwrite(TGT_DIR + "/faces/" + index + ".jpg", justFaceImg);
     			index++;
 			}
     	} 
	}
}
