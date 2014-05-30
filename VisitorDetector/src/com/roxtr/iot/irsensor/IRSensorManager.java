package com.roxtr.iot.irsensor;

import java.util.Date;

import org.bytedeco.javacpp.opencv_core.Mat;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.roxtr.iot.Constants;
import com.roxtr.iot.facerec.RasPiFaceRecognizer;
import com.roxtr.iot.facerec.UtilsJavaCv;

/**
 * Continuously send IR Tx signal and wait for IR Rx Signal
 * 
 */
public class IRSensorManager implements Constants {
	
	private static RasPiFaceRecognizer sFaceRecognizer;
	private static volatile boolean sFaceRecRunning = false;
	static {
		sFaceRecognizer = RasPiFaceRecognizer.getInstance();
		sFaceRecognizer.train();
	}

	public static void main(String[] args) {
		
		if(args.length >= 1) {
			String dnServerIp = args[0];
			System.out.println("Setting DnServerIp: "+dnServerIp);
			RasPiFaceRecognizer.setDnServerHost(dnServerIp);
		} else {
			System.err.println("You need to specify the Desktop Notification Server IP as argument");
			System.exit(1);
		}
		
		System.out.println("Staring IR Sensor Listening..");
		final GpioController mGpio = GpioFactory.getInstance();

		// Keep Pin 01 HIGH -> Send IR Signal continuously:
		mGpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "IRTx", PinState.HIGH);

		// Make Pin 02 as Input Pin -> Wait for IR Signal:
		final GpioPinDigitalInput mIRRxPin = mGpio.provisionDigitalInputPin(
				RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

		// create and register gpio pin listener
		mIRRxPin.addListener(new GpioPinListenerDigital() {
			@Override
			synchronized public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {
				
				// display pin state on console
				if(!sFaceRecRunning) {
					System.out.println(" --> GPIO PIN2 STATE CHANGE: "
							+ event.getPin() + " = " + event.getState());
				} else {
					// if its already running.. return
					return;
				}
				if(event.getState() == PinState.HIGH) {
					if(!sFaceRecRunning) {
						sFaceRecRunning = true;
						(new Thread(new Runnable() {
							@Override
							public void run() {
								String tgtImgFile = IMGS_DIR + "/"+(new Date()).getTime()+".jpg";
								System.out.println("Capturing from webcam.. cheeese!!");
								Mat tgtImgMat = UtilsJavaCv.captureAndSaveFaceFromWebcam(tgtImgFile);
								if(sFaceRecognizer.recognizeThisImg(tgtImgMat)) {
									// Person is a recognized person:
								}
								UtilsJavaCv.close();
								sFaceRecRunning = false;
							}
						})).start();
					}
				}
				
			}

		});

		System.out.println("Waiting for IR Signal..");

		try {
			// keep program running until user aborts (CTRL-C)
			for (;;) {
				Thread.sleep(500);
			}
		} catch (InterruptedException ex) {

		} finally {
			System.out.println("Shutting down GPIO..");
			mGpio.shutdown();
		}
	}
}
