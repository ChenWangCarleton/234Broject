import UserSearch;
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
			UserSearch data = new UserSearch();
			System.out.println("Success!... Waiting for a client...\n");
			// *** Retrevial of data ***
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket); //Received data from client.
			String sentence = new String(receivePacket.getData()); //Retreving the data.
			System.out.println("Got client!");
			
			switch(sentence){
				case "GetAll":
					InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
					int port = receivePacket.getPort(); //Getting client port.
					
					//Get data
					sendData = data.generalSearch().getBytes; //Data we sending.
					//Get data
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
					serverSocket.send(sendPacket); //Sending
					break;
				default:
					InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
					int port = receivePacket.getPort(); //Getting client port.
					
					//TEST String
					String test = "test";
					//Get data
					sendData = test.getBytes(); //Data we sending.
					//Get data
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
					serverSocket.send(sendPacket); //Sending
					break;
				
			}
			
			
			/*
			InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
			int port = receivePacket.getPort(); //Getting client port.
			String capitalizedSentence = sentence.toUpperCase(); //Sending back msg upper case
            sendData = capitalizedSentence.getBytes(); //Turn data into byte.
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
            serverSocket.send(sendPacket); //Sending*/
		}
	}
	public static void main(String args[]) throws Exception {
		
		//Running server
		System.out.println("Starting server...\n");
		
		SServer server = new SServer();
		try{
			server.listen();
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}
}