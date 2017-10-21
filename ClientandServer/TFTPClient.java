// TFTPClient.java

 

import java.io.*;
import java.net.*;
import java.util.*;

public class TFTPClient {
	public static enum OPCODE {
		RRQ(1), WRQ(2), DATA(3), ACK(4), ERROR(5);

		private final int id;
		OPCODE(int id) {this.id = id;}
		public int value() {return id;}
	}
	
	// we can run in normal (send directly to server) or test
	// (send to simulator) mode
	public static enum Mode {NORMAL, TEST};
	public static enum Verbose { ON, OFF };
	public static enum RW {RRQ, WRQ };
	
	//Constants for packet type sizes
	//TODO for iteration 2: add error packet sizes
	private static final int TFTP_DATA_PACKET_SIZE = 516;
	private static final int TFTP_ACK_PACKET_SIZE = 4;
	
	private static final int SERVER_RECV_PORT = 6900;


	private static RW readWrite;

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	
	// Declaring static Scanners for file and user input 
	private Scanner fileScanner = new Scanner( System.in );
	private Scanner responseScanner = new Scanner( System.in );
	private String filePath;
	private static InetAddress serverAddress;


	public TFTPClient() {
		sendReceiveSocket = bind();
	}

	/**
	 * Initializes connection to host or server, sends a RRQ or WRQ
	 * Then initializes data transfer
	 * 
	 * @param file to read or write
	 */
	private void sendAndReceive(File file) {
		int sendPort;
		System.out.println("Client: Started");
		System.out.println("Client: initializing Server");
		sendPort = SERVER_RECV_PORT;

		// Send a read or a write request (depends on user selection)
        int clientConnectionPort = -1;
        clientConnectionPort = sendReadOrWriteRequest(sendPort);
       
       
		if (readWrite == RW.RRQ){
			//try { // Receive a file from server
				receiveFile(serverAddress, file, clientConnectionPort);
		//	} catch (UnknownHostException e) {
			//	e.printStackTrace();
		//	}
		} else{
			//try { // Send entire file to server
				sendFile(serverAddress, file, clientConnectionPort);
		}
	}

	/**
	 * Function to send an RRQ or WRQ.
	 * Waits for response
	 * 
	 * @param sendPort of host or simulator
	 */
	private int sendReadOrWriteRequest(int sendPort){
		byte[] msg = new byte[TFTP_DATA_PACKET_SIZE], // message we send
				fn, // filename as an array of bytes
				md, // mode as an array of bytes
				data; // reply as array of bytes
		String filename, mode; // filename and mode as Strings
		int len;
	
		System.out.println("Client: creating packet ");
		
		filename = filePath; 
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

		//try {
			sendPacket = new DatagramPacket(msg, len, serverAddress, sendPort); // Construct packet to send to host

			
		// Send the datagram packet to the server via the send/receive socket.
		sendPacketToHost(sendReceiveSocket, sendPacket);
		
		System.out.println(sendPacket.getAddress());
		
		System.out.println("Client: Packet sent.\n");
		
		// Construct a DatagramPacket for receiving packets up
		// to 4 bytes long (the length of the byte array).
		data = new byte[TFTP_ACK_PACKET_SIZE];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Client: Waiting for acknowledgement packet from server.");
		receivePacketFromHost(sendReceiveSocket, receivePacket);		

        return receivePacket.getPort(); // represents the client connection's port
	}


	/**
	 * Function to receive a data transfer
	 * 
	 * @param address InetAddress to communicate with
	 * @param file to save as
	 * @param sendPort Port to communicate with server
	 */
	private void receiveFile(InetAddress address, File file, int sendPort){
		// Commencement of file transfer

		System.out.println("\nCommencing file transfer: RRQ" );
		
		int i = 1;
		/* Rename the file if it already exists. */
		File newFile = file;
        while(newFile.exists()){
           newFile = new File(new String(" (" + i + ") ") + file.getName());
           i++;
        }
        file = newFile;
        
		// Construct a DatagramPacket for receiving packets up
		// to 516 bytes long (the length of the byte array).
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			byte[] fileData = new byte[TFTP_DATA_PACKET_SIZE];
			while(true){
				receivePacket = new DatagramPacket(fileData, fileData.length);

			
				System.out.println("Client: Waiting for packet.");
				
				
				receivePacketFromHost(sendReceiveSocket, receivePacket);
				
				out.write(fileData, 4, receivePacket.getLength() - 4);

				
			//	printPacketData(false, receivePacket, false);
				

				// Form ACK packet
				byte dataACKPacket[] = {0, 4, receivePacket.getData()[2], receivePacket.getData()[3]};

			//	try {
					sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress, sendPort);
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//					System.exit(1);
//				}

				
				//	printPacketData(true, sendPacket, true);
				

				// Send the ACK packet via send/receive socket.
				sendPacketToHost(sendReceiveSocket, sendPacket);

			
				System.out.println("Client: Packet sent.\n");
				
				
				// This means it's the last packet
				if(receivePacket.getLength() < TFTP_DATA_PACKET_SIZE){
					break;
				}
			}
			out.close();
		}catch(IOException ioe){
			System.out.println("Issue with transfer\n"
					+ "File transfer could not be completed.");
			return;
		}
		try{
	 		Scanner fileScanner = new Scanner(file);
	 		String text  = "";
	        
	 		while (fileScanner.hasNextLine()) {

	 		    // get the token **once** and assign it to a local variable
	 		    text += fileScanner.nextLine();

	 		
	 		}
	 		fileScanner.close();
			System.out.println("The text is "+text);

	 		
	 		}catch(IOException e){
	 			
	 			
	 			System.out.println("scanner could not open");
	 		}

		System.out.println("File transfer completed successfully");
	}

	/**
	 * Function to send a data transfer
	 * 
	 * @param address InetAddress to communicate with
	 * @param file to save as
	 * @param sendPort Port to communicate with server
	 */
	private void sendFile(InetAddress address, File file, int sendPort){
		// Commencement of file transfer
		System.out.println("\nCommencing file transfer: WRQ" );
		
		int packetNumber = 1;
		byte[] fileData = new byte[512]; 
		boolean empty = true;
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			int n;
			
			while ((n = in.read(fileData,0,512)) != -1){
				empty = false;
				// Build a byte array to properly format packets
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.reset();
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
				byte packet[] = stream.toByteArray(); // Create byte array to send in packet from stream
				stream.close(); 

				if(n < 512){ //
			//	try { 
						sendPacket = new DatagramPacket(packet, n+4, serverAddress, sendPort);

				} else{
				//	try {
						sendPacket = new DatagramPacket(packet, TFTP_DATA_PACKET_SIZE, serverAddress, sendPort);
				}
				

				// Send the datagram packet to the server via the send/receive socket.
				sendPacketToHost(sendReceiveSocket, sendPacket);

				System.out.println("Client: Packet sent.\n");

				byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
				receivePacket = new DatagramPacket(data, data.length);

				System.out.println("Client: Waiting for packet.");
				
				receivePacketFromHost(sendReceiveSocket, receivePacket);
				
				
				packetNumber++; 
				fileData = new byte[TFTP_DATA_PACKET_SIZE]; // Clear data from block
			}
			in.close();
		}catch(IOException ioe){
			System.out.println("Issue with transfer\n"
					+ "File transfer could not be completed.");
			return;
		}
		if((sendPacket.getLength() == 516) || empty){ // If last packet was 516 bytes, send one more
			// Build a byte array to properly format packets
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.reset();
			// Packet number is an int, need to turn into 2 bytes. Shift first one 8 bits right
			byte byteA = (byte) (packetNumber >>> 8);
			byte byteB = (byte) (packetNumber);
			stream.write(0);
			stream.write(3);
			stream.write(byteA);
			stream.write(byteB);
			byte packet[] = stream.toByteArray(); // Create byte array to send in packet from stream
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//	try {
				sendPacket = new DatagramPacket(packet, 4, serverAddress, sendPort);

			
			// Send the datagram packet to the server via the send/receive socket.
			sendPacketToHost(sendReceiveSocket, sendPacket);

				System.out.println("Client: Packet sent.\n");
			
			
			// Construct a DatagramPacket for receiving packets up
			// to 4 bytes long (the length of the byte array).

			byte[] data = new byte[TFTP_ACK_PACKET_SIZE];
			receivePacket = new DatagramPacket(data, data.length);

			System.out.println("Client: Waiting for packet.");
			
			receivePacketFromHost(sendReceiveSocket, receivePacket);
			
			
		}

		System.out.println("File transfer completed successfully");
	}


	/**
	/**
	 * Function to parse through user input and check against possible
	 * client commands
	 * 
	 * @param input string to parse
	 */
//	public boolean userInput(String input){
//		if(input.equals("Q")){
//			System.out.println("Shutting Down...");
//			System.exit(1);
//		} 
//		return false; // This means user may have attempted to enter an actual file path
//	}

	/**
	 * Function to Start the UI
	 * Goes in a loop asking the use whether to RW, then asking for a file path and 
	 * once the transfer succeeded or failed, asks whether user would like to transfer
	 * another file.  
	 * 
	 */
	public void start(){
		
		try { // Initialize server address to local host
			serverAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("Please enter the IP address of the server (L for localhost):");
		while(true){ // Until the IP Address is valid, keeps requesting valid path
			String response = responseScanner.nextLine(); // Save input as filePath
			 if(response.toUpperCase().equals("L")){
				System.out.println("LocalHost selected");
				break;
			} try {
				serverAddress = InetAddress.getByName(response);
				break;
			} catch (UnknownHostException e) {
				System.out.println("Could not set address. Choose a different address:");
				continue;
			}
		}

			
		System.out.println("Welcome the TFTP client");
		while(true){ // Loops until user quits
			while(true){ // Until the user response valid, keep asking
				System.out.println("Would you like to read or write a file? (R/W) (H for help)");
				String response = responseScanner.nextLine().toUpperCase();
				if(response.equals("R")){
					readWrite = RW.RRQ; // Send directly to server
					System.out.println("Read selected. ");
					break; // break out of loop
				} else if(response.equals("W")){
					readWrite = RW.WRQ; // Send to simulator
					System.out.println("Write selected. ");
					break; // break out of loop
				} 
			}

			System.out.println("Please enter the path of the file:");
			while(true){ // Until the path entered is valid, keeps requesting valid path
				filePath = fileScanner.nextLine(); // Save input as filePath
				System.out.println("Please enter the path for the input file:");
				
				File file = new File(filePath); // loads file
				// Only check if file exists locally if write selected
				if(readWrite == RW.WRQ){
					if(file.exists()) { // Make sure file exists
						sendAndReceive(file);
						break; // break out of loop
					}
					System.out.println("File path is invalid. \nPlease enter a valid input path");
				} else{
					sendAndReceive(file);
					break; // break out of loop
				}
			}

			while(true){ // Until the user response valid, keep asking
				System.out.println("Would you like to transfer another file? (Y/N))");
				String response = responseScanner.nextLine().toUpperCase();
				if(response.equals("Y")){
					break; // break out of loop
				} else if(response.equals("N")){
					System.out.println("Shutting Down...");
					System.exit(1);
				} 
			}
		}
		}
	


	/**
     * Returns a DatagramSocket bound to a provided port. Terminates
     * the TFTPClient if a SocketException occurs.
     * 
     * @param port number to bind to socket.
     * @return DatagramSocket bound to a port.
     */
    @SuppressWarnings("unused")
	private DatagramSocket bind(int port) {
 	   //Create new socket reference
 	   DatagramSocket socket = null;
 	   
 	   //Attempt to bind socket to port
 	   try {
 		   socket = new DatagramSocket(port);
 	   } 
 	   catch (SocketException se) {
 		   se.printStackTrace();
 	       System.exit(1);
 	   }
 	   
 	   //Return bound socket
 	   return socket;
    }
    
    /**
     * Returns a DatagramSocket bound to port selected by the host. Terminates
     * the TFTPClient if a SocketException occurs.
     * 
     * @return DatagramSocket bound to a port.
     */
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
    
    /**
     * Sends a given DatagramPacket to a host through a provided DatagramSocket.
     * Terminates TFTPErrorSimulator if IOException occurs.
     * 
     * @param socket to send through.
     * @param packet to send.
     */
    public static void sendPacketToHost(DatagramSocket socket, DatagramPacket packet) {
 	   try {
            socket.send(packet);
 	   } catch (IOException e) {
 		   e.printStackTrace();
            System.exit(1);
 	   }
    }
    
//    public void sendOrReceive()
//    {
//       byte[] msg = new byte[100], // message we send
//              fn, // filename as an array of bytes
//              md, // mode as an array of bytes
//              data; // reply as array of bytes
//       String filename, mode; // filename and mode as Strings
//       int j, len, sendPort;
//       
//       // In the assignment, students are told to send to 23, so just:
//       // sendPort = 23; 
//       // is needed.
//       // However, in the project, the following will be useful, except
//       // that test vs. normal will be entered by the user.
//       
////       if (run==Mode.NORMAL) 
////          sendPort = 69;
////       else
//          sendPort = 6900;
//       
//       // sends 10 packets -- 5 reads, 5 writes, 1 invalid
//       for(int i=1; i<=11; i++) {
//
//          System.out.println("Client: creating packet " + i + ".");
//          
//          // Prepare a DatagramPacket and send it via sendReceiveSocket
//          // to sendPort on the destination host (also on this machine).
//
//          // if i even (2,4,6,8,10), it's a read; otherwise a write
//          // (1,3,5,7,9) opcode for read is 01, and for write 02
//          // And #11 is invalid (opcode 07 here -- could be anything)
//
//         msg[0] = 0;
//         if(i%2==0) 
//            msg[1]=1;
//         else 
//            msg[1]=2;
//            
//         if(i==11) 
//            msg[1]=7; // if it's the 11th time, send an invalid request
//
//         // next we have a file name -- let's just pick one
//         filename = "testString";
//         // convert to bytes
//         fn = filename.getBytes();
//         
//         // and copy into the msg
//         System.arraycopy(fn,0,msg,2,fn.length);
//         // format is: source array, source index, dest array,
//         // dest index, # array elements to copy
//         // i.e. copy fn from 0 to fn.length to msg, starting at
//         // index 2
//         
//         // now add a 0 byte
//         msg[fn.length+2] = 0;
//
//         // now add "octet" (or "netascii")
//         mode = "octet";
//         // convert to bytes
//         md = mode.getBytes();
//         
//         // and copy into the msg
//         System.arraycopy(md,0,msg,fn.length+3,md.length);
//         
//         len = fn.length+md.length+4; // length of the message
//         // length of filename + length of mode + opcode (2) + two 0s (2)
//         // second 0 to be added next:
//
//         // end with another 0 byte 
//         msg[len-1] = 0;
//
//         // Construct a datagram packet that is to be sent to a specified port
//         // on a specified host.
//         // The arguments are:
//         //  msg - the message contained in the packet (the byte array)
//         //  the length we care about - k+1
//         //  InetAddress.getLocalHost() - the Internet address of the
//         //     destination host.
//         //     In this example, we want the destination to be the same as
//         //     the source (i.e., we want to run the client and server on the
//         //     same computer). InetAddress.getLocalHost() returns the Internet
//         //     address of the local host.
//         //  69 - the destination port number on the destination host.
//         try {
//            sendPacket = new DatagramPacket(msg, len,
//                                InetAddress.getLocalHost(), sendPort);
//         } catch (UnknownHostException e) {
//            e.printStackTrace();
//            System.exit(1);
//         }
//
//         System.out.println("Client: sending packet " + i + ".");
//         System.out.println("To host: " + sendPacket.getAddress());
//         System.out.println("Destination host port: " + sendPacket.getPort());
//         len = sendPacket.getLength();
//         System.out.println("Length: " + len);
//         System.out.println("Containing: ");
//         for (j=0;j<len;j++) {
//             System.out.println("byte " + j + " " + msg[j]);
//         }
//         
//         // Form a String from the byte array, and print the string.
//         String sending = new String(msg,0,len);
//         System.out.println(sending);
//
//         // Send the datagram packet to the server via the send/receive socket.
//
//         try {
//            sendReceiveSocket.send(sendPacket);
//         } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//         }
//
//         System.out.println("Client: Packet sent.");
//
//         // Construct a DatagramPacket for receiving packets up
//         // to 100 bytes long (the length of the byte array).
//
//         data = new byte[100];
//         receivePacket = new DatagramPacket(data, data.length);
//
//         System.out.println("Client: Waiting for packet.");
//         try {
//            // Block until a datagram is received via sendReceiveSocket.
//            sendReceiveSocket.receive(receivePacket);
//         } catch(IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//         }
//
//         // Process the received datagram.
//         System.out.println("Client: Packet received:");
//         System.out.println("From host: " + receivePacket.getAddress());
//         System.out.println("Host port: " + receivePacket.getPort());
//         len = receivePacket.getLength();
//         System.out.println("Length: " + len);
//         System.out.println("Containing: ");
//         for (j=0;j<len;j++) {
//             System.out.println("byte " + j + " " + data[j]);
//         }
//         
//         System.out.println();
//
//       } // end of loop
//
//       // We're finished, so close the socket.
//       sendReceiveSocket.close();
//    }
    
    
    
    /**
     * Receives a DatagramPacket from a host through a provided DatagramSocket.
     * Terminates TFTPErrorSimulator if an exception occurs.
     * 
     * @param socket to receive the packet on.
     * @param packet to receive.
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
	
	public static void main(String args[]){
		TFTPClient c = new TFTPClient();
		c.start();
	}
}