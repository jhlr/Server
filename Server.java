
public class Server {
	public final static int port = 23456, bufferSize = 1025;
	public final static int lossProbability = 0;
	
	public static void main(String[] arguments) throws Exception {
		MyServerSocket welcomeSocket = new MyServerSocket(port, lossProbability, bufferSize);
		try {
			while (true) {
				MySocket connectionSocket = welcomeSocket.accept();
				
				System.out.print("Connection accepted: ");
				System.out.println(connectionSocket.getSocketAddress());
				
				HttpRequest request = new HttpRequest(connectionSocket);
				Thread thread = new Thread(request);
				thread.start();
				thread.join(); ///////////////////////////
			}
		} finally {
			welcomeSocket.close();			
		}
	}
}
