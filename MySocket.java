import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class MySocket implements java.io.Closeable {

	final DatagramSocket socket;
	final SocketAddress address;
	private final int probability, bufferSize;

	private final InputStream in;
	private final OutputStream out;


	public MySocket(InetAddress destAddress, int destPort, int probability, int bufferSize) 
			throws SocketException, IOException {
		this(new InetSocketAddress(destAddress, destPort), probability, bufferSize, true);
	}
	
	public MySocket(SocketAddress destAddress, int probability, int bufferSize) 
			throws SocketException, IOException{
		this(destAddress, probability, bufferSize, true);
	}
	
	MySocket(SocketAddress destAddress, int probability, int bufferSize, boolean receive)
			throws SocketException, IOException {
		this.bufferSize = bufferSize;
		this.address = destAddress;
		this.probability = probability;

		this.socket = new DatagramSocket();
		DatagramPacket temp = new DatagramPacket(new byte[2], 1, destAddress);
		
		socket.send(temp);
		if(receive) { socket.receive(temp);	}
		this.in = new SocketInputStream(this);
		this.out = new SocketOutputStream(this);
	}
	
	public void setSoTimeout(int time) throws SocketException {
		socket.setSoTimeout(time);
	}

	public void close() throws java.io.IOException {
		socket.close();
	}

	boolean random() {
		int rand = new java.util.Random().nextInt(100);
		return rand < this.probability;
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

}
