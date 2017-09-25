

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	
	
	public static enum RW {RRQ, WRQ };

	private DatagramPacket sendPacket;
	private DatagramSocket sendReceiveSocket;
	
	private static RW readWrite;
	private static final int TFTP_DATA_PACKET_SIZE = 516;
	
	private static final int SERVER_RECV_PORT = 6900;
	
	public Client() {
		sendReceiveSocket = bind();
	}
	
	private Scanner responseScanner = new Scanner( System.in );

	 // Initializes connection to host or server, sends a RRQ or WRQ
	private void sendAndReceive() {
		int sendPort = SERVER_RECV_PORT;
		System.out.println("Client: Started");

		// Send a read or a write request (depends on user selection)
		sendReadOrWriteRequest(sendPort);
     
	}
	
	
	
	 private DatagramSocket bind() {
	 	   //Create new socket reference
	 	   DatagramSocket socket = null;
	 	   
	 	   //Attempt to bind socket
	 	   try {
	          	socket = new DatagramSocket();
	 	   } catch (SocketException se) {
	 		   se.printStackTrace();
	 		   System.exit(1);
	 	   }
	 	   
	 	   //Return bound socket
	 	   return socket;
	    }
		private int sendReadOrWriteRequest(int sendPort){
			byte[] msg = new byte[TFTP_DATA_PACKET_SIZE], // message we send
					fn, // filename as an array of bytes
					md;
			String filename, mode; // filename and mode as Strings
			int len;
			System.out.println("Client: creating packet ");
				
			filename = "test"; 
			fn = filename.getBytes(); // convert to bytes

			byte opCode;
			if(readWrite == RW.RRQ){
				opCode = 1;
			} else{
				opCode = 2;
			}

			// Build byte array for packet
			System.arraycopy(fn,0,msg,2,fn.length); // Copy into the msg
			msg[fn.length+2] = 0; // Add 0 byte
			mode = "octet"; 
			md = mode.getBytes();
			System.arraycopy(md,0,msg,fn.length+3,md.length); // Copy mode into msg
			len = fn.length+md.length+4; // length of the message
			msg[len-1] = 0; // Add 0 byte
			msg[1] = opCode; // Add in opcode

			try {
				sendPacket = new DatagramPacket(msg, len, InetAddress.getLocalHost(), sendPort); // Construct packet to send to host
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			// Send the datagram packet to the server via the send/receive socket.
			sendPacketToHost(sendReceiveSocket, sendPacket);
			System.out.println("Client: Packet sent.\n");
			

	        return sendPacket.getPort(); // represents the client connection's port
		}
	 
		 public static void sendPacketToHost(DatagramSocket socket, DatagramPacket packet) {
		 	   try {
		            socket.send(packet);
		 	   } catch (IOException e) {
		 		   e.printStackTrace();
		            System.exit(1);
		 	   }
		    }
	 
		 /**
		     * Receives a DatagramPacket from a host through a provided DatagramSocket.
		*/
		    public static void receivePacketFromHost(DatagramSocket socket, DatagramPacket packet) {
		 	   //Block until packet receives
		 	   try {
		 		   socket.receive(packet);
		        } catch (IOException e) {
		            e.printStackTrace();
		            System.exit(1);
		        }
		    }
	 

	 public void start(){
			System.out.println("Welcome the client");
			while(true){ // Loops until user quits
				while(true){ // Until the user response valid, keep asking
					System.out.println("Would you like to read or write a file? (R/W)");
					String response = responseScanner.nextLine().toUpperCase();
					if(response.equals("R")){
						readWrite = RW.RRQ; 
						System.out.println("Read selected.");
						sendAndReceive();
						break; // break out of loop
					} else if(response.equals("W")){
						readWrite = RW.WRQ; // Send to simulator
						System.out.println("Write selected");
						sendAndReceive();
						break; // break out of loop
					}
				}
			}
		}	
	
	public static void main(String args[]){
		Client c = new Client();
		c.start();
	}
}
