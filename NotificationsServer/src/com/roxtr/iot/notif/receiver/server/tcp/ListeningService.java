package com.roxtr.iot.notif.receiver.server.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.roxtr.iot.notif.common.DesktopNSConstants;
import com.roxtr.iot.notif.common.DesktopNSMessage;
import com.roxtr.iot.notif.receiver.NotificationManager;

public class ListeningService extends Thread implements DesktopNSConstants {
	public static void main(String[] args) {
		ListeningService.getInstace().start();
	}

	private static ListeningService instance = new ListeningService();
	public static ListeningService getInstace() {
		return instance;
	}
	private ListeningService() {
	}
	
	
	private static final CharSequence PASS = "ytrewq";
	
	//private static List<String> EOC_MSG_TYPES = null;
	private  static NotificationManager sNtfnMgr = null;
	
	static {
		// EOC_MSG_TYPES = Arrays.asList((new String [] {"EXIT", "QUIT", "CLOSE", "BYE"}));
		sNtfnMgr = NotificationManager.getInstance();
	}
	
	private ServerSocket providerSocket;
	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public void run() {
		try {
			providerSocket = new ServerSocket(SERVER_PORT, BLOCKLOG);
			while(true) {
				System.out.println("Waiting for connection..");
				connection = providerSocket.accept();
				
				String clientHost = connection.getInetAddress().getHostName();
				String clientIP = connection.getInetAddress().getHostAddress();
				
				System.out.println("Connection received from: "
						+ clientHost +"("+clientIP+")");
	
				// get Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				_sendBacktoClient("Connection successful");
				
				DesktopNSMessage message = null;
				try {
					message = (DesktopNSMessage) in.readObject();
					_handleMessage(clientHost, clientIP, message);
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			
			// Closing connection
			
			try {
				if(in != null)
					in.close();
				if(out != null)
					out.close();
				if(providerSocket != null)
					providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*private boolean _isEndOfComm(DesktopNSMessage message) {
		if(message == null)
			return true;
		
		if(message.getType() != null) {
			String msgType = message.getType().toUpperCase();
			if(EOC_MSG_TYPES.contains(msgType)) {
				return true;
			}
		}
		return false;
	}*/

	private void _handleMessage(String clientHost, String clientIP, DesktopNSMessage message) {
		String pass = message.getPassword();
		if(pass == null || !pass.equals(PASS)) {
			_sendBacktoClient("Password incorrect!");
			return;
		}
		
		sNtfnMgr.addToQueue(message);
	}

	private void _sendBacktoClient(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
