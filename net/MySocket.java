package net;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MySocket implements java.io.Closeable {

	public static boolean DEBUG = false; // shows connection messages
	
	/* TRY_NUMBER = number of times to send again before considering the packet as lost
	** SO_TIMEOUT = miliseconds to wait for packet confirmation
	*/ static public int SO_TIMEOUT = 2, TRY_NUMBER = 30;
	
	final DatagramSocket socket;
	final SocketAddress address;
	private final int lossProbability, bufferSize;

	private final InputStream in;
	private final OutputStream out;

	public MySocket(InetAddress destAddress, int destPort, int lossProbability, int bufferSize) 
			throws SocketException, IOException {
		this(new InetSocketAddress(destAddress, destPort), lossProbability, bufferSize, true);
	}
	
	public MySocket(SocketAddress destAddress, int lossProbability, int bufferSize) 
			throws SocketException, IOException{
		this(destAddress, lossProbability, bufferSize, true);
	}
	
	MySocket(SocketAddress destAddress, int lossProbability, int bufferSize, boolean receive)
			throws SocketException, IOException {
		this.bufferSize = bufferSize;
		this.lossProbability = lossProbability;

		socket = new DatagramSocket(); // get an available port
		
		/* give more time for the handshake
		** avoids multiple connections to the server
		*/ socket.setSoTimeout(SO_TIMEOUT << 4);
		DatagramPacket temp = new DatagramPacket(new byte[2], 1, destAddress);
		for(int i=0; i<TRY_NUMBER; i++) {
			if(!random()) { // handshake byte may be lost
				// send a byte from the newly created port		
				socket.send(temp); 
			}
			try {
				if(receive) { 
					// this will not happen if it was created from the ServerSocket
					socket.receive(temp);
					destAddress = temp.getSocketAddress();	
				}
				i = TRY_NUMBER; 
				// break, the connection was successful
			} catch (SocketTimeoutException e) {}
		}
		socket.setSoTimeout(SO_TIMEOUT);
		
		this.address = destAddress;
		this.in = new SocketInputStream(this);
		this.out = new SocketOutputStream(this);
	}
	
	public void setSoTimeout(int time) throws SocketException {
		// different from SO_TIMEOUT
		socket.setSoTimeout(time);
	}

	@Override
	public void close() throws java.io.IOException {
		socket.close();
	}

	boolean random() {
		int rand = new java.util.Random().nextInt(100);
		return rand < this.lossProbability;
	}
	
	public SocketAddress getSocketAddress() {
		return address;
	}

	public InputStream getInputStream() {
		return in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	DatagramPacket newPacket() {
		return new DatagramPacket(new byte[bufferSize], bufferSize, address);
	}
	
	void unreachable() throws IOException {
		if(DEBUG) System.out.println(" canceled.");
		throw new IOException("Unreachable");
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
