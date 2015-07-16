import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MyServerSocket implements Closeable {

	private final int probability, buffersize;
	private final DatagramSocket socket;
	
	public MyServerSocket(int port, int probability, int buffersize) 
	throws SocketException {
		this.probability = probability;
		this.buffersize = buffersize;
		this.socket = new DatagramSocket(port);
	}

	public MySocket accept() throws SocketException, IOException {
		DatagramPacket packet = new DatagramPacket(new byte[2], 1);
		socket.receive(packet);
		return new MySocket(packet.getSocketAddress(), probability, buffersize, false);
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

}
