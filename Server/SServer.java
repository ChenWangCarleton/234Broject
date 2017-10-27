import java.io.*;
import java.net.*;

public class SServer {
	private static DatagramSocket serverSocket;
	private byte[] receiveData;
	private byte[] sendData;
	private SServer() throws Exception {
		this.serverSocket = new DatagramSocket(60700);
		this.receiveData = new byte[64000];
		this.sendData = new byte[64000];
	}
	public void listen() throws Exception {
		while(true){
			// *** Retrevial of data ***
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket); //Received data from client.
			String sentence = new String(receivePacket.getData()); //Retreving the data.
			System.out.println("Received: " + sentence);
			
			
			// *** The start of sending back ***
			InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
			int port = receivePacket.getPort(); //Getting client port.
			String capitalizedSentence = sentence.toUpperCase(); //Sending back msg upper case
            sendData = capitalizedSentence.getBytes(); //Turn data into byte.
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
            serverSocket.send(sendPacket); //Sending
		}
	}
	public static void main(String args[]) throws Exception {
		SServer server = new SServer();
		try{
			server.listen();
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}
}