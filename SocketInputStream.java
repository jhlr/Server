import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

public class SocketInputStream extends InputStream {
	private int i;
	private byte part;
	public final int length;
	
	private final DatagramPacket packet;
	private final MySocket master;
	
	public SocketInputStream(MySocket master) {
		super();
		this.packet = master.newPacket();
		i = packet.getLength();
		this.length = packet.getLength();
		part = -127;
		this.master = master;
	}
	
	@Override
	public int read() throws IOException {
		byte[] buffer = packet.getData();
		if(i >= packet.getLength()-1) {
			java.util.Arrays.fill(buffer, (byte) 0);
			do{
				try {
					System.out.print("receiving...");
					packet.setLength(length);
					master.socket.receive(packet);
					// Confirmation
					if(master.random()) {
						packet.setLength(1); // No need to send the whole packet back
						master.socket.send(packet);
					}
				} catch (SocketTimeoutException e) { // did not receive anything
					buffer[0] = -128; // continue
				}
			}while(buffer[0] < part); // Maybe the confirmation was not received in a previous packet
			System.out.println(" received: " + (int) buffer[0]);
			part++;
			i = 0;
		}
		i++;
		return buffer[i];
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
