package com.roxtr.iot.notif.receiver.audio;

import java.io.InputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

@SuppressWarnings("restriction")
public class NotificationSound extends Thread {

	public static synchronized void play() {
		(new NotificationSound()).start();
	}
	
	private NotificationSound() {
		super();
	}

	@Override
	public void run() {

		try {
			InputStream in = getClass().getResourceAsStream("alert.wav");
			AudioStream audioStream = new AudioStream(in);

			AudioPlayer.player.start(audioStream);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		NotificationSound.play();
	}
}
