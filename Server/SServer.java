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
			
			switch(sentence.charAt(0)){
				case 'A':
				{
					String retData = data.generalSearch();
					double packets = getNumOfPacket(retData.length());
					
					sendData = new byte[64000];
					String strNum = "" + packets;
					sendData = strNum.getBytes;
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
					serverSocket.send(sendPacket); //Sending
					
					
					InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
					int port = receivePacket.getPort(); //Getting client port.
					if(packets > 1){
						for(double i=0; i < (packets - 1.0); i++){
							//sendData = new byte[64000];
							sendData = retData.substring((int)(64000*i),(int)(64000*(i+1))).getBytes();
							
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
							serverSocket.send(sendPacket); //Sending
						}
						//sendData = new byte[64000];
						sendData = retData.substring((int)(64000*(i+1)));
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
						serverSocket.send(sendPacket); //Sending
					} else {
						//sendData = new byte[64000];
						sendData = retData.getBytes; //Data we sending.
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
						serverSocket.send(sendPacket); //Sending
					}
					break;
				}
				default:
				{
					sendData = new byte[64000];
					InetAddress IPAddress = receivePacket.getAddress(); // Client IPAdress.
					int port = receivePacket.getPort(); //Getting client port.
					
					//TEST String
					String test = "Error";
					//Get data
					sendData = test.getBytes(); //Data we sending.
					//Get data
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //Creating packet to send data
					serverSocket.send(sendPacket); //Sending
					break;
				}
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
	
	private double getNumOfPacket(int length){
		return Math.ceil(length/64000);
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