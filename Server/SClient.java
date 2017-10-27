import java.io.*;
import java.net.*;

public class SClient {
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private byte[] sendData;
	private byte[] receiveData;
	
	public SClient() throws Exception {
		this.clientSocket = new DatagramSocket(); //Client socket (Only 1 needed)
		this.IPAddress = InetAddress.getByName("99.249.57.222"); //Server IP Address
		this.receiveData = new byte[64000]; //Amount expect to receive
		this.sendData = new byte[64000]; 
	}
	
	public void sendReq(String reqType) throws Exception {
		sendData = reqType.getBytes(); //Return the request type (reqType)
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 60700); //Establishing request message (With IP and port)
		clientSocket.send(sendPacket); //Send request
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); //Create packet for received message
		clientSocket.receive(receivePacket); //Wait and ready to receive
		String receivedPackets = new String(receivePacket.getData()); //Turn data into STRING
		int expectedPackets = Integer.parseInt(receivedPackets.substring(0,1));
		for(int i=0;i<expectedPackets;i++){
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); //Create packet for received message
			clientSocket.receive(receivePacket); //Wait and ready to receive
			String data = new String(receivePacket.getData()); //Turn data into STRING
			
			System.out.println(data);
		}
		
		clientSocket.close(); //Close socket.
	}
	public static void main(String args[]) throws Exception {
		SClient client = new SClient();
		try{
			client.sendReq("A");
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}
}