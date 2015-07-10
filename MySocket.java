import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MySocket implements java.io.Closeable {

	private final int prob;
	final InetSocketAddress address;
	private final int bufferSize;

	public final InputStream in;
	public final OutputStream out;


	public MySocket(InetAddress address, int port, int prob, int bufferSize) 
	throws SocketException {
		this(new InetSocketAddress(address, port), prob, bufferSize);
	}
	
	public MySocket(InetSocketAddress address, int prob, int bufferSize)
			throws java.net.SocketException {
		this.bufferSize = bufferSize;
		this.address = address;
		this.prob = prob;
		DatagramSocket socket = new DatagramSocket(address.getPort());
		socket.connect(address);
		this.in = new SocketInputStream(socket, new DatagramPacket(
				new byte[bufferSize], bufferSize, address));
		this.out = new SocketOutputStream(socket, new DatagramPacket(
				new byte[bufferSize], bufferSize, address));
	}

	public void close() throws java.io.IOException {
		in.close();
		out.close();
	}

	private boolean random() {
		int rand = new java.util.Random().nextInt(100);
		return rand <= this.prob;
	}

	public InputStream getInputStream() {
		return in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public DatagramPacket newPacket() {
		return new DatagramPacket(new byte[bufferSize], bufferSize, address);
	}

}
