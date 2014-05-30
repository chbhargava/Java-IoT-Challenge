Training Images Generator
========================

- To Capture images of known persons, by executing: 
  - Main Class: com.roxtr.iot.facerec.gen.ImageGenerator &lt;webcamURL&gt; &lt;targetImgsDir&gt;
    - Webcam URL will be genarally vfw://0
    - This will genrate images in &lt;targetImgsDir&gt;/raw folder.
    
- Once the images are captured, verify them and process by executing: 
  - Main Class: com.roxtr.iot.facerec.gen.ImageProcessor &lt;targetImgsDir&gt;
    - This will create the processed images in &lt;targetImgsDir&gt;/faces 
