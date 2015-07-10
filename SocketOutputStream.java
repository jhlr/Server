import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class SocketOutputStream extends OutputStream {
	private int i;
	private final DatagramSocket socket;
	private final DatagramPacket packet;
	
	public SocketOutputStream(DatagramSocket socket, DatagramPacket packet) {
		super();
		this.packet = packet;
		this.socket = socket;
		i = 0;
	}
	
	@Override
	public void write(int b) throws IOException {
		byte[] buffer = packet.getData();
		i++;
		buffer[i] = (byte) b;
		if(i >= buffer.length - 1) {
			this.flush();
		}
	}
	
	@Override
	public void flush() throws IOException {
		// packet.getData()[0] = 
		socket.send(packet);
		i = 0;
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}
