package main;
import net.*;

import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	public final static int bufferSize = 1025, serverPort = 23456, lossProbability = 50;
	public final static byte[] serverAdress = {(byte)192, (byte)168, (byte)1, (byte)101};
	public final static File inputFile = new File("./GET.txt");
	
	public static void main(String[] args) throws Exception {
		MySocket socket = new MySocket(
				InetAddress.getByName("localhost"), serverPort, lossProbability, bufferSize);
		
		System.out.println("Connection accepted: " + socket.getSocketAddress());
		// if a packet takes more to arrive, consider it as lost

		PrintStream serverOut = new PrintStream(socket.getOutputStream());
		Scanner serverIn = new Scanner(socket.getInputStream());
		Scanner sc = new Scanner(new FileReader(inputFile));
		try {
			while(sc.hasNextLine()){
				serverOut.println(sc.nextLine()); // send something
				serverOut.flush(); // force sending, even if the buffer is not full
			}
			socket.setSoTimeout(MySocket.SO_TIMEOUT << 4);
			System.out.println(serverIn.nextLine()); // print whatever was received
		} finally {
			socket.close();
			sc.close();
			serverIn.close();
		}
	}
}
