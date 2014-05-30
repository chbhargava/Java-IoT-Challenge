RasPi Visitor Detector:
=======================

- The is main module of the project, which runs on RasPi. 

- Place the training images in: "data/imgs/Training/<nameOfThePerson>" directory. 

- GPIO Setup:
  - Pin1 (output) is connected to IR Transmitter. 
  - Pin2 (input) is connected to IR Receiver (TSOP 1738)
  - Pin3 (output) is connected to servo motor which unlocks the door. 
  - Pin4 (output) is connected to Calling bell ringer. 
  
- This can be started by: 
  - sudo ant -DnsServerIp=<NotificationsServerIP>
  - Why running as root:
    - As we are using pi4J for controlling GPIO, wiringPi needs root permissions. 
  - This will read all the folders under "data/imgs/Training" directory and process and save the images recursively. 
  - When a person comes to your door step:
    - It will automatically detects and tries to recognize the person. 
    - If the person is known:
      - It will send signal to the servo motor and also sends notification to the owner desktop with the recognized person name. 
    - If the person is unknown:
      - It will send singal to calling bell and also sends notification to the owner desktop. 
