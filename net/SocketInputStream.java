package net;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

class SocketInputStream extends InputStream {
	private int i;
	private byte part;
	public final int length;
	
	private final DatagramPacket packet;
	private final MySocket master;
	
	public SocketInputStream(MySocket master) throws IOException {
		super();
		this.packet = master.newPacket();
		this.length = packet.getLength();
		part = -127;
		this.master = master;
		i = length;
	}
	
	@Override
	public int read() throws IOException {
		byte[] buffer = packet.getData();
		if(i >= packet.getLength() - 1) {
			if(part > -127) {
				return -1;
			}
			request();
		}
		i++;
		return buffer[i];
	}
	
	private void request() throws IOException {
		int t = 0;
		byte[] buffer = packet.getData();
		java.util.Arrays.fill(buffer, (byte) 0);
		System.out.print("receiving..");
		do{
			System.out.print(".");
			try {
				master.socket.receive(packet);
				// Confirmation
				if(!master.random()) {
					packet.setLength(1); // No need to send the whole packet back
					master.socket.send(packet);
					packet.setLength(length);
				}
			} catch (SocketTimeoutException e) { // did not receive anything
				buffer[0] = -128; // continue
			}
			master.isConnected(t++);
		}while(buffer[0] < part); // Maybe the confirmation was not received in a previous packet
		System.out.println(" received: " + (int) buffer[0]);
		for(i=length-1; i >= 0 && buffer[i] == 0; i--) {}
		packet.setLength(i+1);
		part++;
		i = 0;
	}
	
	@Override
	public int available() throws IOException {
		return packet.getLength() - i;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void close() throws IOException {
		master.close();
	}
}
