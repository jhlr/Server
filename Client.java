import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	public final static int port = 8080, bufferSize = 1025;
	public final static int probability = 30;
	public final static byte[] serverAdress = {(byte)192, (byte)168, (byte)1, (byte)101};
	
	public static void main(String[] args) throws Exception {
		MySocket socket = new MySocket(InetAddress.getByAddress(serverAdress), port, probability, bufferSize);
		PrintStream out = new PrintStream(socket.getOutputStream());
		Scanner sc = new Scanner(socket.getInputStream());
		out.println("hahaha");
		System.out.println(sc.nextLine());
		socket.close();
	}
}
