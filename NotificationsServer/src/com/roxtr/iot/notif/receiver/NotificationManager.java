package com.roxtr.iot.notif.receiver;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import com.roxtr.iot.notif.common.DesktopNSMessage;
import com.roxtr.iot.notif.receiver.ui.NotificationWindow;

public class NotificationManager {
	private static NotificationManager instance = new NotificationManager();
	public static NotificationManager getInstance() {
		return instance;
	}
	private NotificationManager() { }
	
	private int notificationsCnt = 0;
	public void showNotification(String title, String message) {
		NotificationWindow window = new NotificationWindow(title, message);
		
		int screenWidth = getScreenResolution().width;
		int screenHeight = getScreenResolution().height;
		
		int positionX = (int) ((screenWidth - window.getWidth()) - 2);
		int positionY = (int) ((screenHeight - window.getHeight()) - 2 
				- notificationsCnt*(window.getHeight()+2));
        
        window.setLocation(positionX, positionY);
		window.setVisible(true);
		notificationsCnt++;
	}
	
	public void closeNotification() {
		if(notificationsCnt != 0)
			notificationsCnt--;
	}
	
	private static Rectangle getScreenResolution() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getMaximumWindowBounds();
    }
	public void addToQueue(DesktopNSMessage message) {
		// TODO: add the code for logging messages!
		
		//String clientName = message.getClientName();
		String title = message.getTitle();
		String msgStr = message.getMsgString();
		
		showNotification(title, msgStr);
		
	}
}
