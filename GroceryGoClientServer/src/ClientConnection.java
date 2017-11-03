
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class ClientConnection extends Thread {
	/* Types of requests that we can receive. */
	public static enum Request {
		All, ONE, DATA, ACK, ERROR
	};

	public static enum OPCODE {
		All(1), ONE(2), DATA(3), ACK(4), ERROR(5);

		private final int id;

		OPCODE(int id) {
			this.id = id;
		}

		public int value() {
			return id;
		}
	}

	// Constants for write/read responses
	public static final byte[] ONE_RESP = { 0, 4, 0, 0 };
	public static final byte[] All_RESP = { 0, 3, 0, 1 };

	/* Client connection attributes */
	private DatagramPacket receivedPacket, sendPacket;
	private DatagramSocket sendSocket;
	private int packetNumber;
	private int threadNumber;
	private byte[] data;
	private String AllData;
	private InetAddress clientAddress;

	public ClientConnection(int number, DatagramPacket packet, byte[] packetData, String getAllData) {
		threadNumber = number;
		receivedPacket = packet;
		data = packetData;
		AllData = getAllData;
	}

	@Override
	public void run() {

		System.out.println("ClientConnectionThread number: " + threadNumber + " processing request.");

		Request req = getRequest(data);
		System.out.println("req " + req);

		int len = receivedPacket.getLength();

		int j = 0;

		for (j = 2; j < len; j++) {
			if (data[j] == 0)
				break;
		}

		byte[] productIDbyte = Arrays.copyOfRange(data, 2, j);
		String productID = new String(productIDbyte); // Turn data
		
		
		byte[] test = Arrays.copyOfRange(data, 0, len);
		String teststring = new String(test); // Turn data
		
		System.out.println("test"+teststring);
		


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

		sendPacket = new DatagramPacket(response, response.length, receivedPacket.getAddress(),receivedPacket.getPort());

		sendSocket = GroceryGoServer.bind();

		System.out.println("Received Packet Port: " + receivedPacket.getPort());

		GroceryGoServer.sendPacketToHost(sendPacket, sendSocket);

		clientAddress = receivedPacket.getAddress();

		System.out.println("ClientAddress " + clientAddress);

		if (req == Request.All) {

			System.out.println("ClientConnection: Sending All");
			sendAll(sendSocket, clientAddress, AllData, receivedPacket.getPort());

		} else if (req == Request.ONE) {
			
			System.out.println("ClientConnection: Sending Product ID:"+productID);

			sendOne(sendSocket, clientAddress, productID, receivedPacket.getPort());

		}
		//sendSocket.close();

	}

	private void sendAll(DatagramSocket socket, InetAddress address, String AllData, int sendPort) {

		File file = new File("/Users/Hanad/Documents/workspace/GroceryGoClientServer/src/GeneralSearch.json");

		String data = "";
		try {
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine()) {
				data += fileScanner.nextLine();
			}
			fileScanner.close();
		} catch (IOException e) {
			System.out.println("scanner error");
		}

		System.out.println("\nCommencing file transfer...\n");

		int dataSize = data.length();
		packetNumber = 1;
		int dataPosition = 0;
		String dataSubstring = "";
		while (dataPosition < dataSize) {

			if (dataPosition + 64000 < dataSize) {
				dataSubstring = data.substring(dataPosition, dataPosition + 64000);
			} else {
				dataSubstring = data.substring(dataPosition, data.length());
			}
			dataPosition += 64000;
			

			/* Create byte array to send in packet from stream. */
			

			/*
			 * Send the datagram packet to the server via the send/receive
			 * socket.
			 */
			byte packetData[] = new byte[dataSubstring.getBytes().length+2];
			byte byteA = (byte) (packetNumber >>> 8);
			byte byteB = (byte) (packetNumber);
			packetData[0] = byteA;
			packetData[1] = byteB;
			//packetData = Arrays.copyOfRange(dataSubstring.getBytes(), 0, dataSubstring.getBytes().length);
			System.arraycopy(dataSubstring.getBytes(), 0, packetData, 2, dataSubstring.getBytes().length); // Copy
			
			

//			System.out.println("packetData length is "+packetData.length);
//			System.out.println("packetData length is "+packetData.length);
//
//			

			
		//	byte packetData[] = dataSubstring.getBytes();
			
			
			sendPacket = new DatagramPacket(packetData, packetData.length, clientAddress, sendPort);
			GroceryGoServer.sendPacketToHost(sendPacket, sendSocket);
			
			System.out.println("data packet sent is "+packetNumber);

		//	System.out.println("Client Connection:"+packetNumber+" packet sent.");



			/*
			 * Construct a DatagramPacket for receiving packets up to 4 bytes
			 * long (the length of the byte array).
			 */

			byte[] dataReceive = new byte[4];
			receivedPacket = new DatagramPacket(dataReceive, dataReceive.length);
			//System.out.println("waiting on port "+sendSocket.getLocalPort());
			

			
			
			
			
			while (true) {
				try {
					GroceryGoServer.receivePacketFromHost(receivedPacket, sendSocket);

					int ackPacketNumber = (((int) (receivedPacket.getData()[0] & 0xFF)) << 8)
							+ (((int) receivedPacket.getData()[1]) & 0xFF);

					System.out.println("ackPacketNumber is " + ackPacketNumber);

					if (ackPacketNumber < packetNumber) {

						// System.out.println("The packet numbers do not match.
						// Resending data packet: "+packetNumber);

						System.out.println("duplicate ack packet received");
						continue;

						// throw new SocketTimeoutException();

					}

				} catch (SocketTimeoutException e) {
					System.out.println("     										Timed out.Resending data packet: "
							+ packetNumber);

					GroceryGoServer.sendPacketToHost(sendPacket, sendSocket);
					continue;
				}
				break;
			}
			
			packetNumber++;
			/* Clear data from block. */
		}
//		byte[] dataReceive = new byte[4];
//		receivedPacket = new DatagramPacket(dataReceive, dataReceive.length);
//		//System.out.println("waiting on port "+sendSocket.getLocalPort());
//		
//
//		
//		//last ack packet
//		try {
//			GroceryGoServer.receivePacketFromHost(receivedPacket,sendSocket);
//		} catch (SocketTimeoutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			

		System.out.println("File transfer completed successfully");
		GroceryGoServer.finished();
	}

	private void sendOne(DatagramSocket socket, InetAddress address, String productID, int sendPort) {

		String data = "";

		int productIDint = Integer.parseInt(productID);

		UserSearch us = new UserSearch();

		try {
			data = us.searchByID(productIDint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(data.length());

		/// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^The Data

		System.out.println("\nCommencing file transfer...\n");

		int dataSize = data.length();
		packetNumber = 1;
		int dataPosition = 0;
		String dataSubstring = "";

		if (dataPosition + 64000 < dataSize) {
			dataSubstring = data.substring(dataPosition, dataPosition + 64000);
		} else {
			dataSubstring = data.substring(dataPosition, data.length());
		}
		dataPosition += 64000;
		
		byte packetData[] = new byte[dataSubstring.getBytes().length+2];
		byte byteA = (byte) (packetNumber >>> 8);
		byte byteB = (byte) (packetNumber);
		packetData[0] = byteA;
		packetData[1] = byteB;
		//packetData = Arrays.copyOfRange(dataSubstring.getBytes(), 0, dataSubstring.getBytes().length);
		System.arraycopy(dataSubstring.getBytes(), 0, packetData, 2, dataSubstring.getBytes().length); // Copy
		


		/* Create byte array to send in packet from stream. */
	//	byte packetData[] = dataSubstring.getBytes();

		sendPacket = new DatagramPacket(packetData, packetData.length, clientAddress, sendPort);

		/*
		 * Send the datagram packet to the server via the send/receive socket.
		 */
		GroceryGoServer.sendPacketToHost(sendPacket,sendSocket);

		System.out.println("recieved packet number" + packetNumber);

		/*
		 * Construct a DatagramPacket for receiving packets up to 4 bytes long
		 * (the length of the byte array).
		 */

		byte[] dataReceive = new byte[4];
		receivedPacket = new DatagramPacket(dataReceive, dataReceive.length);

		System.out.println("Client Connection: packet sent.");
		
		//System.out.println("waiting on port "+sendPacket.getPort());
		
		
		while (true) {
			try {
				GroceryGoServer.receivePacketFromHost(receivedPacket, sendSocket);

				int ackPacketNumber = (((int) (receivedPacket.getData()[0] & 0xFF)) << 8)
						+ (((int) receivedPacket.getData()[1]) & 0xFF);

				System.out.println("ackPacketNumber is " + ackPacketNumber);

				if (ackPacketNumber < packetNumber) {

					// System.out.println("The packet numbers do not match.
					// Resending data packet: "+packetNumber);

					System.out.println("duplicate ack packet received");
					continue;

					// throw new SocketTimeoutException();

				}

			} catch (SocketTimeoutException e) {
				System.out.println("     										Timed out.Resending data packet: "
						+ packetNumber);

				GroceryGoServer.sendPacketToHost(sendPacket, sendSocket);
				continue;
			}
			break;
		}
		

		

		packetNumber++;
		/* Clear data from block. */

		System.out.println("File transfer completed successfully");
		GroceryGoServer.finished();

	}

	private Request getRequest(byte[] data) {

		Request req;

		if (data[0] != 0)
			req = Request.ERROR; // bad
		else if (data[1] == OPCODE.All.value())
			req = Request.All; // could be read
		else if (data[1] == OPCODE.ONE.value())
			req = Request.ONE; // could be write
		else if (data[1] == OPCODE.ACK.value())
			req = Request.ACK; // could be ack
		else
			req = Request.ERROR;

		return req;

	}
}