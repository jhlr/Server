import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class SocketInputStream extends InputStream {
	private int i;
	private final DatagramSocket socket;
	private final DatagramPacket packet;
	
	private byte[] markBuffer;
	private int j;
	private boolean reset;
	private int markBufferMax;
	
	public SocketInputStream(DatagramSocket socket, DatagramPacket packet) {
		super();
		this.packet = packet;
		this.socket = socket;
		markBuffer = null;
		reset = false;
		i = packet.getLength();
	}
	
	@Override
	public int read() throws IOException {
		if(reset) {
			if(j >= markBufferMax) {
				reset = false;
				byte result = markBuffer[j];
				markBuffer = null;
				return result;
			}
			j++;
			return markBuffer[j];
		}
		
		byte[] buffer = packet.getData();
		if(i >= packet.getLength()-1) {
			socket.receive(packet);
			// buffer[0] is the header
			i = 0;
		}
		i++;
		if(markBuffer != null) {
			if(j < markBuffer.length) {
				markBuffer[j] = buffer[i];
				j++;
			} else {
				markBuffer = null;
				j = 0;
			}
		}
		return buffer[i];
	}
	
	@Override
	public int available() throws IOException {
		return packet.getLength() - i - 1;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		markBuffer = new byte[readlimit];
		markBufferMax = 0;
		j = 0;
		reset = false;
	}
	
	@Override
	public boolean markSupported() { return true; }
	
	@Override
	public synchronized void reset() throws IOException {
		markBufferMax = j - 1;
		j = -1;
		reset = true;
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
