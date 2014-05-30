Training Images Generator
========================

- To Capture images of known persons, by executing: 
  - Main Class: com.roxtr.iot.facerec.gen.ImageGenerator <webcamURL> <targetImgsDir>
    - Webcam URL will be genarally vfw://0
    - This will genrate images in <targetImgsDir>/raw folder.
    
- Once the images are captured, verify them and process by executing: 
  - Main Class: com.roxtr.iot.facerec.gen.ImageProcessor <targetImgsDir> 
    - This will create the processed images in <targetImgsDir>/faces 
