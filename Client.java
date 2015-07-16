
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	public final static int bufferSize = 1025, serverPort = 23456, lossProbability = 0;
	//public final static byte[] serverAdress = {(byte)192, (byte)168, (byte)1, (byte)101};
	
	public static void main(String[] args) throws Exception {
		MySocket socket = new MySocket(
				InetAddress.getByName("localhost"), serverPort, lossProbability, bufferSize);
		
		System.out.print("Connection accepted: ");
		System.out.println(socket.getSocketAddress());
		socket.setSoTimeout(300); // if a packet takes more to arrive, consider it as lost
		
		PrintStream serverOut = new PrintStream(socket.getOutputStream());
		Scanner serverIn = new Scanner(socket.getInputStream());
		
		serverOut.println("abacate"); // send something
		serverOut.flush(); // force sending, even if the buffer is not full
		System.out.println(serverIn.nextLine()); // print whatever was received
		
		socket.close();
		serverIn.close();
	}
}
