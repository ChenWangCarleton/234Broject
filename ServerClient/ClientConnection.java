

import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class ClientConnection extends Thread {
	/* Types of requests that we can receive. */
	public static enum Request {All, ONE, DATA, ACK, ERROR};
	
	

	
	public static enum OPCODE {
		All(1), ONE(2), DATA(3), ACK(4), ERROR(5);

	private final int id;
	OPCODE(int id) {this.id = id;}
	public int value() {return id;}
}
	
	
	
	
	
	//Constants for write/read responses
	public static final byte[] ONE_RESP = {0, 4, 0, 0};
	public static final byte[] All_RESP = {0, 3, 0, 1};
	

	private static final int TFTP_DATA_PACKET_SIZE = 516;
	private static final int TFTP_ACK_PACKET_SIZE = 4;
    
	
    /*Client connection attributes*/
    private DatagramPacket receivedPacket, sendPacket;
	private DatagramSocket sendSocket;
	private int packetNumber;
	private int threadNumber;
	private byte[] data;
	private InetAddress clientAddress;

    
	public ClientConnection(int number, DatagramPacket packet, byte[] packetData) {
		threadNumber = number;
		receivedPacket = packet;
        data = packetData;

	}

    @Override
    public void run() {
        
        System.out.println("ClientConnectionThread number: " + threadNumber + " processing request."); 
        

        Request req = getRequest(data);
        System.out.println("req "+req);
        
        System.out.println("Request.All "+ Request.All);
        
      //  String filename = "", 
    //    String ID;
        int len = receivedPacket.getLength();
        int j = 0, k = 0;
        
        if (!isError(req)) {
            for(j = 2; j < len; j++) {
                if (data[j] == 0) break;
            }
            
          //  filename = new String(data, 2, j-2);
           
            for(k = j+1; k < len; k++) { 
                if (data[k] == 0) break;
            }
            
           // ID = new String(data, j, k-j-1);
        }
        
        
    	byte[] response = new byte[4];
        if (req == Request.All) { 
            response = All_RESP;
           System.out.println(req + " request received."); 
        } else if (req == Request.ONE) {
            response = ONE_RESP;
           System.out.println(req + " request received."); 
        } else {
             System.out.println(req + " this is an error request received."); 
            try {
                throw new Exception("Invalid request");
            } catch (Exception e) {
                System.exit(1);
            }
        }
        
        sendPacket = new DatagramPacket(response, response.length,
                                        receivedPacket.getAddress(), receivedPacket.getPort());
        len = sendPacket.getLength();
        
        sendSocket = TFTPServer.bind();
        
        System.out.println("receivedPacket.getPort()"+receivedPacket.getPort());
        
        TFTPServer.sendPacketToHost(sendSocket, sendPacket);
       
        
		clientAddress = receivedPacket.getAddress();

		
		System.out.println("clientAddress"+clientAddress);
        
		
    	String filename2  = "GeneralSearch.json";

		
        File file = new File(filename2);

        if (req == Request.All) {
        	
        	
        	
        	file = new File(filename2);
        	
            System.out.println("ClientConnection: Read Request."); 
          //  try {
                if(file.exists() && !file.isDirectory()) {
                    sendAll(sendSocket, clientAddress, file, receivedPacket.getPort());
                } else {
                    System.out.println("ClientConnection: Can't read file. Does not exist on server.");
                   // System.exit(1);
                }

        } else if (req == Request.ONE) {
        	//System.out.println("ClientConnection: Write Request."); 
         //   try {
    			sendOne(sendSocket, clientAddress, file, receivedPacket.getPort());

        }
        
        /* We're finished with this socket, so close it. */
        sendSocket.close(); 
        
    }
    

    
    private void sendAll(DatagramSocket socket, InetAddress address, File file, int sendPort){
        
		System.out.println("\nCommencing file transfer...\n" ); 
		byte[] fileData = new byte[512];
		boolean empty = true;
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			int n;
			packetNumber = 1;

			while ((n = in.read(fileData,0,512)) != -1){
                empty = false;
				/* Build a byte array to properly format packets. */
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.reset();
                
				/* Packet number is an int, need to turn into 2 bytes. Shift first one 8 bits right. */
				byte byteA = (byte) (packetNumber >>> 8);
				byte byteB = (byte) (packetNumber);
				try {
					stream.write(0);
					stream.write(3);
					stream.write(byteA);
					stream.write(byteB);
					stream.write(fileData);
				} catch (IOException e) {
					System.out.println("Problem with output stream");
					e.printStackTrace();
				}
                
                /* Create byte array to send in packet from stream. */
				byte packetData[] = stream.toByteArray();
				stream.close(); 

				if(n < 512){ /* Last block of data. */
			//		try { /* Size is only size of actual data. (n+4) */
						sendPacket = new DatagramPacket(packetData, n+4, clientAddress, sendPort);

				} else{
				//	try {
						sendPacket = new DatagramPacket(packetData, TFTP_DATA_PACKET_SIZE, clientAddress, sendPort);

				}
			

				/* Send the datagram packet to the server via the send/receive socket. */
				TFTPServer.sendPacketToHost(socket, sendPacket);

				

				/*  
                    Construct a DatagramPacket for receiving packets up
                    to 4 bytes long (the length of the byte array). 
                */

				byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
				receivedPacket = new DatagramPacket(data, data.length);

				
				System.out.println("Client Connection: packet sent.");
				TFTPServer.receivePacketFromHost(socket, receivedPacket);
				
				
					
				packetNumber++; 
                /* Clear data from block. */
				fileData = new byte[TFTP_DATA_PACKET_SIZE];
			}
			in.close();
		}catch(IOException ioe){
			System.out.println("Issue with transfer"
					+ "File transfer could not be completed.");
			return;
		}
        
         /* If last packet was 516 bytes, send one more. */
		if((sendPacket.getLength() == 516) || empty){
            
			/* Build a byte array to properly format packets. */
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.reset();
            
			/* Packet number is an int, need to turn into 2 bytes. Shift first one 8 bits right. */
			byte byteA = (byte) (packetNumber >>> 8);
			byte byteB = (byte) (packetNumber);
			stream.write(0);
			stream.write(3);
			stream.write(byteA);
			stream.write(byteB);
            
            /* Create byte array to send in packet from stream. */
			byte packet[] = stream.toByteArray();
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
//			try{
//		 		Scanner fileScanner = new Scanner(file);
//		 		String text  = "";
//		        
//		 		while (fileScanner.hasNextLine()) {
//
//		 		    text += fileScanner.nextLine();
//
//		 		
//		 		}
//		 		fileScanner.close();
//		 		System.out.println(text);
//
//		 		
//		 		}catch(IOException e){
//		 			
//		 			
//		 			System.out.println("scanner could not open");
//		 		}
		 		

			
		//	try {
				sendPacket = new DatagramPacket(packet, 4, clientAddress, sendPort);
		
            
			/* Send the datagram packet to the server via the send/receive socket. */
			TFTPServer.sendPacketToHost(socket, sendPacket);


			/* 
                Construct a DatagramPacket for receiving packets up
                to 4 bytes long (the length of the byte array).
            */

			byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
			receivedPacket = new DatagramPacket(data, data.length);

			TFTPServer.receivePacketFromHost(socket, receivedPacket);

			
		
		}

		System.out.println("File transfer completed successfully");
	}
    
    
    
 private void sendOne(DatagramSocket socket, InetAddress address, File file, int sendPort){
        
		System.out.println("\nCommencing file transfer...\n" ); 
		byte[] fileData = new byte[512];
		boolean empty = true;
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			int n;
			packetNumber = 1;

			while ((n = in.read(fileData,0,512)) != -1){
                empty = false;
				/* Build a byte array to properly format packets. */
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.reset();
                
				/* Packet number is an int, need to turn into 2 bytes. Shift first one 8 bits right. */
				byte byteA = (byte) (packetNumber >>> 8);
				byte byteB = (byte) (packetNumber);
				try {
					stream.write(0);
					stream.write(3);
					stream.write(byteA);
					stream.write(byteB);
					stream.write(fileData);
				} catch (IOException e) {
					System.out.println("Problem with output stream");
					e.printStackTrace();
				}
                
                /* Create byte array to send in packet from stream. */
				byte packetData[] = stream.toByteArray();
				stream.close(); 

				if(n < 512){ /* Last block of data. */
			//		try { /* Size is only size of actual data. (n+4) */
						sendPacket = new DatagramPacket(packetData, n+4, clientAddress, sendPort);

				} else{
				//	try {
						sendPacket = new DatagramPacket(packetData, TFTP_DATA_PACKET_SIZE, clientAddress, sendPort);

				}
			

				/* Send the datagram packet to the server via the send/receive socket. */
				TFTPServer.sendPacketToHost(socket, sendPacket);

				

				/*  
                    Construct a DatagramPacket for receiving packets up
                    to 4 bytes long (the length of the byte array). 
                */

				byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
				receivedPacket = new DatagramPacket(data, data.length);

				
				System.out.println("Server: Waiting for packet.");
				TFTPServer.receivePacketFromHost(socket, receivedPacket);
				
				
					
				packetNumber++; 
                /* Clear data from block. */
				fileData = new byte[TFTP_DATA_PACKET_SIZE];
			}
			in.close();
		}catch(IOException ioe){
			System.out.println("Issue with transfer"
					+ "File transfer could not be completed.");
			return;
		}
        
         /* If last packet was 516 bytes, send one more. */
		if((sendPacket.getLength() == 516) || empty){
            
			/* Build a byte array to properly format packets. */
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.reset();
            
			/* Packet number is an int, need to turn into 2 bytes. Shift first one 8 bits right. */
			byte byteA = (byte) (packetNumber >>> 8);
			byte byteB = (byte) (packetNumber);
			stream.write(0);
			stream.write(3);
			stream.write(byteA);
			stream.write(byteB);
            
            /* Create byte array to send in packet from stream. */
			byte packet[] = stream.toByteArray();
			try {
				stream.close();
			} catch (IOException e) {
	 			System.out.println("Stream does not close");
			}
			
			
			try{
		 		Scanner fileScanner = new Scanner(file);
		 		String text  = "";
		        
		 		while (fileScanner.hasNextLine()) {

		 		    // get the token **once** and assign it to a local variable
		 		    text += fileScanner.nextLine();

		 		
		 		}
		 		fileScanner.close();
		 		System.out.println(text);

		 		
		 		}catch(IOException e){
		 			
		 			
		 			System.out.println("scanner could not open");
		 		}
		 		

			sendPacket = new DatagramPacket(packet, 4, clientAddress, sendPort);
			TFTPServer.sendPacketToHost(socket, sendPacket);



			byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
			receivedPacket = new DatagramPacket(data, data.length);

			TFTPServer.receivePacketFromHost(socket, receivedPacket);

			
		
		}

		System.out.println("File transfer completed successfully");
	}
    
    private boolean isError(Request req) {
    	return (req == Request.ERROR);
    }
            

    private Request getRequest(byte[] data) {
        
    	Request req;
    	    	
        
    	if (data[0]!=0) 
    		req = Request.ERROR; // bad
        else if (data[1] == OPCODE.All.value()) 
        	req = Request.All; // could be read
        else if (data[1] == OPCODE.ONE.value()) 
        	req = Request.ONE; // could be write
        else if (data[1] == OPCODE.DATA.value())
        	req = Request.DATA;  // could be data
        else if (data[1] == OPCODE.ACK.value())
    		req = Request.ACK; // could be ack
        else 
        	req = Request.ERROR;
    	
    	return req;
        
    }
}