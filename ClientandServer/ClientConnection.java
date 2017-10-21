

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
	public static enum Request {READ, WRITE, DATA, ACK, ERROR};
	
	
	  public static enum OPCODE {
	        RRQ(1), WRQ(2), DATA(3), ACK(4), ERROR(5);
	        private final int id;
	        OPCODE(int id) { this.id = id; }
	        public int value() { return id; }
	    }
	
	//Constants for write/read responses
	public static final byte[] WRITE_RESP = {0, 4, 0, 0};
	public static final byte[] READ_RESP = {0, 3, 0, 1};
	

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
        String filename = "", mode;
        int len = receivedPacket.getLength();
        int j = 0, k = 0;
        
        if (!isError(req)) {
            for(j = 2; j < len; j++) {
                if (data[j] == 0) break;
            }
            
            filename = new String(data, 2, j-2);
           
            for(k = j+1; k < len; k++) { 
                if (data[k] == 0) break;
            }
            
            mode = new String(data, j, k-j-1);
        }
        
        if (k != len - 1) { req = Request.ERROR; }
        
    	byte[] response = new byte[4];
        if (req == Request.READ) { 
            response = READ_RESP;
           System.out.println(req + " request received."); 
        } else if (req == Request.WRITE) {
            response = WRITE_RESP;
           System.out.println(req + " request received."); 
        } else {
             System.out.println(req + " request received."); 
            try {
                throw new Exception("Invalid request");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        sendPacket = new DatagramPacket(response, response.length,
                                        receivedPacket.getAddress(), receivedPacket.getPort());
        len = sendPacket.getLength();
        
        sendSocket = TFTPServer.bind();
        
        System.out.println("receivedPacket.getPort()"+receivedPacket.getPort());
        
        TFTPServer.sendPacketToHost(sendSocket, sendPacket);
        
      //  sendSocket.send(sendPacket);

        
		clientAddress = receivedPacket.getAddress();

		
		System.out.println("clientAddress"+clientAddress);
        
		
		
        if (req == Request.READ) {
            File file = new File(filename);
            System.out.println("ClientConnection: Read Request."); 
          //  try {
                if(file.exists() && !file.isDirectory()) {
                    sendFile(sendSocket, clientAddress, file, receivedPacket.getPort());
                } else {
                    System.out.println("ClientConnection: Can't read file. Does not exist on server.");
                    System.exit(1);
                }

        } else if (req == Request.WRITE) {
        	System.out.println("ClientConnection: Write Request."); 
         //   try {
    			receiveFile(sendSocket, clientAddress, filename, receivedPacket.getPort());

        }
        
        /* We're finished with this socket, so close it. */
        sendSocket.close(); 
        
    }
    
 	private void receiveFile(DatagramSocket socket, InetAddress address, String filename, int sendPort){


        System.out.println("ClientConnection: Commencing file transfer...\n" ); 
		/* Rename the file if it already exists. */
        int i = 1;
        
        File file = new File(filename);
		File newFile = file;
        while(newFile.exists()){
           newFile = new File(new String(" (" + i + ") ") + file.getName());
           i++;
        }
        file = newFile;
        
 		try {

 			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
 			while(true) {

 				byte[] fileData = new byte[TFTP_DATA_PACKET_SIZE];
 				receivedPacket = new DatagramPacket(fileData, 516);
 				TFTPServer.receivePacketFromHost(socket, receivedPacket);
             
                out.write(receivedPacket.getData(), 4, receivedPacket.getLength()-4);
 				packetNumber = ((receivedPacket.getData()[2] & 0xFF) << 8) + receivedPacket.getData()[3];
                
 				

 				
                /* Our response. */
 				byte dataACKPacket[] = {0, 4, receivedPacket.getData()[2], receivedPacket.getData()[3]};
 				sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, clientAddress, sendPort);

                          

                TFTPServer.sendPacketToHost(socket, sendPacket);   
                System.out.println("Server: Packet sent.\n"); 

 				/* This means it's the last packet. */
 				if(receivedPacket.getLength() < TFTP_DATA_PACKET_SIZE) { break; }

 			}
            
 			out.close();
 		} catch(IOException ioe){
            System.out.println("ClientConnection: Issue with transfer. File transfer could not be completed.");
            ioe.printStackTrace();
            return;
 		}
        
       System.out.println("File transfer completed successfully");
 	}
    
    private void sendFile(DatagramSocket socket, InetAddress address, File file, int sendPort){
        
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
				e.printStackTrace();
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
    
    private boolean isError(Request req) {
    	return (req == Request.ERROR);
    }
            
  
    
   
    
    private Request getRequest(byte[] data) {
        
    	Request req;
        
    	if (data[0]!=0) 
    		req = Request.ERROR; // bad
        else if (data[1] == OPCODE.RRQ.value()) 
        	req = Request.READ; // could be read
        else if (data[1] == OPCODE.WRQ.value()) 
        	req = Request.WRITE; // could be write
        else if (data[1] == OPCODE.DATA.value())
        	req = Request.DATA;  // could be data
        else if (data[1] == OPCODE.ACK.value())
    		req = Request.ACK; // could be ack
        else 
        	req = Request.ERROR;
    	
    	return req;
        
    }
}