
public class Server {
	public final static int port = 8080, bufferSize = 1025;
	public final static int probability = 30;
	public final static byte[] clientAdress = {(byte)192, (byte)168, (byte)1, (byte)103};

	public static void main(String[] arguments) throws Throwable {
		MyServerSocket welcomeSocket = new MyServerSocket(port);
		try {
			while (true) {
				MySocket connectionSocket = welcomeSocket.accept();
				HttpRequest request = new HttpRequest(connectionSocket);
				Thread thread = new Thread(request);
				thread.setDaemon(true);
				thread.start();
			}
		} catch(Exception e) {
			return;
		} finally {
			welcomeSocket.close();
		}
	}
}
