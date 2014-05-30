package com.roxtr.iot.notif.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.roxtr.iot.notif.common.DesktopNSConstants;
import com.roxtr.iot.notif.common.DesktopNSMessage;

public class DNSender implements DesktopNSConstants {
	
	private String mServerIp = null;
	public DNSender(String serverIp) {
		mServerIp = serverIp;
	}

	public void sendDN(String title, String message) {
		/*NotificationManager.getInstance().
			showNotification("cool", "<html>Actual message......<br/>asdasd\\n" +
					"<a href=\"http://google.com\">asdasd</a><br/>asdsad</html>");
		
		NotificationManager.getInstance().
			showNotification("cool asdas", "<html>nEXT message......<br/>asdasd\\n" +
					"<a href=\"http://google.com\">asdasd</a><br/>asdsad</html>");*/
		
		ObjectInputStream objInputStream = null;
		ObjectOutputStream objOutputStream = null;
		Socket clientSocket = null;
	    try {
	    	clientSocket = new Socket(mServerIp, SERVER_PORT);
		    //clientSocket.setSoTimeout(60 * 1000);
	        objOutputStream = new ObjectOutputStream(
	        		clientSocket.getOutputStream());

        	DesktopNSMessage request = new DesktopNSMessage("RasPi client", 
        			"ytrewq", title, message);
            objOutputStream.writeObject(request);
            objOutputStream.flush();
            objInputStream = new ObjectInputStream(clientSocket.getInputStream());
            String rsp = (String) objInputStream.readObject();
            
            System.out.println("Rsp: "+rsp);
	    } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try { 
		    	if (objOutputStream != null) {
	                objOutputStream.close();
	            }
	            if (objInputStream != null) {
	                objInputStream.close();
	            }
	            if(clientSocket != null) {
			        clientSocket.close();
			        clientSocket = null;
	            }
			} catch (IOException e) { } 	
	    }

	}
	
	public static void main(String[] args) {
		(new DNSender("192.168.1.7")).sendDN("Test title", "Test message.... asfsaf asdf asdf ");
	}
}
