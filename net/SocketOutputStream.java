package net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;


class SocketOutputStream extends OutputStream {

	private int i;
	public final int length;
	private byte part;
	private final MySocket master;
	private final DatagramPacket packet;

	public SocketOutputStream(MySocket master) {
		super();
		this.packet = master.newPacket();
		i = 0;
		part = -127;
		this.master = master;
		this.length = packet.getLength();
	}

	@Override
	public void write(int b) throws IOException {
		byte[] buffer = packet.getData();
		i++;
		buffer[i] = (byte) b;
		if(i >= length - 1) {
			this.flush(); // send
		}
	}

	@Override
	public void flush() throws IOException {
		int t = 0;
		if(i == 0){ return; }
		byte[] buffer = packet.getData();
		System.out.print("sending..");
		do{
			try {
				System.out.print(".");
				buffer[0] = part; // set header
				if(!master.random()) {
					packet.setLength(i+1);
					master.socket.send(packet); // send the packet
				}
				packet.setLength(1);
				master.socket.receive(packet);
			} catch(SocketTimeoutException e) { // no confirmation
				buffer[0] = -128; // continue
			}
			if(t++ > MySocket.TRY_NUMBER){
				master.unreachable();
			}
		}while(buffer[0] < part);
		System.out.println(" sent: " + (int) buffer[0]);
		java.util.Arrays.fill(buffer, (byte) 0);
		part++;
		i = 0;
	}

	@Override
	public void close() throws IOException {
		master.close();
	}
}

