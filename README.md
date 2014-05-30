Java-IoT-Challenge
==================

The goal of this project is to:

- Train the RasPi Visitor Detector with known 
  - Capture the images of the known persons 
  - Detect the faces from the images. 
  - Process all the faces and store them. 

- If a person comes to your door step:
  - Automatically capture the photo using the IR proximity sensors 
  - Then detect the face of the person using JavaCv and OpenCv
  - Recognize the person:
    - If the person is in the known list:
      - Open the door automatically by sending signal to Servo Motor which is connected to door lock. 
      - Send Desktop to the remote owner with the recognized Name of the person. 
    - If the person is unknown: 
      - Ring the Bell automatically. 
      - Send a desktop notification to the remote owner, mentioning there is an unknown person at your door step. 
      
The source code contains 2 individual projects:
- Training Images Generator: 
  - For capture, process and storing the images. 
- Desktop Notification System: 
  - For receiving the notifications from the RasPi Visitor Detector 
- RasPi Face Recognition System: 
  - For recognizing the face and do appropriate action. 

Please find more information about individual projects in their respective folders. 
