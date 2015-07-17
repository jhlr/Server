package main;

import net.*;

public class Server {
	public final static int port = 23456, bufferSize = 1025, lossProbability = 50;
	public static void main(String[] arguments) throws Exception {
		MyServerSocket welcomeSocket = new MyServerSocket(port, lossProbability, bufferSize);
		MySocket connectionSocket;
		try {
			while (true) {
				try {
					connectionSocket = welcomeSocket.accept();
					
					System.out.println("Connection accepted: " + connectionSocket.getSocketAddress());
					
					HttpRequest request = new HttpRequest(connectionSocket);
					Thread thread = new Thread(request);
					thread.start();
					//thread.join();
					
				} catch(Exception e) {
					System.out.println();
					e.printStackTrace();
				}
			}
		} finally {
			welcomeSocket.close();
		}
	}
}
