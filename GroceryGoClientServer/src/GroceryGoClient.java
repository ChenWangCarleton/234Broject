
import java.io.*;
import java.net.*;
import java.util.*;

public class GroceryGoClient {

	private static final int SERVER_RECV_PORT = 60800;

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;

	public GroceryGoClient() {
		sendReceiveSocket = bind();
	}

	private void initialize(String type) throws UnknownHostException {
		try {
			InetAddress serverAddress = InetAddress.getByName("174.114.84.112");
			sendRequest(serverAddress, SERVER_RECV_PORT, type);
		} catch (IOException e) {

		}

	}

	private void sendRequest(InetAddress serverAddress, int sendPort, String productID) throws IOException {
		byte[] msg = new byte[516], productIDbyte, data;
		byte opCode;

		// System.out.println(productID);

		if (productID.equals("all")) {
			opCode = 1;
		} else {

			opCode = 2;
		}

		// Build byte array for packet
		productIDbyte = productID.getBytes();
		msg[0] = 0;
		System.arraycopy(productIDbyte, 0, msg, 2, productIDbyte.length); // Copy
																			// into
																			// the
																			// msg
		msg[productIDbyte.length + 2] = 0;
		msg[1] = opCode; // Add in opcode

		sendPacket = new DatagramPacket(msg, msg.length, serverAddress, sendPort);
		
		sendReceiveSocket.send(sendPacket);
		System.out.println("request sent");


		data = new byte[4];
		receivePacket = new DatagramPacket(data, data.length);

		sendReceiveSocket.receive(receivePacket);
		System.out.println("ack received");


		receive(serverAddress, receivePacket.getPort());

	}

	private String receive(InetAddress serverAddress, int sendPort) {

		int ackPacketNumber = 1;
		int dataPacketNumber  = 0;
		String text = "";
		try {
			byte[] fileData = new byte[64002];

			while (true) {

				
				while(true) {
				try {

					 sendReceiveSocket.setSoTimeout(2000);
					receivePacket = new DatagramPacket(fileData, fileData.length);
					sendReceiveSocket.receive(receivePacket);
					//System.out.println("datapacket received");
					
					

					dataPacketNumber  = (((int) (receivePacket.getData()[0] & 0xFF)) << 8) + (((int) receivePacket.getData()[1]) & 0xFF);
					
				//	System.out.println("receivedPacketNumber is "+dataPacketNumber);
					
					
					if (dataPacketNumber < ackPacketNumber) {

						// System.out.println("was waiting for data packet"+ackPacketNumber);

					//	System.out.println("Duplicate data packet recieved.");

						// throw new SocketTimeoutException();
						continue;

					}


				} catch (SocketTimeoutException e) {
					// Send the ACK packet via send/receive socket.

					System.out.println("timeout");

					System.out.println("Resending ack packet: " + ackPacketNumber);

					sendReceiveSocket.send(sendPacket);
					continue;

					}
				break;
					
				}
					
					
					byte[] data = Arrays.copyOfRange(receivePacket.getData(), 2, receivePacket.getLength());

					
					
					String modifiedSentence = new String(data); // Turn datas																// into STRIN
					text += modifiedSentence;
					
					
					byte dataACKPacket[] = new byte[4];
					byte byteA = (byte) (ackPacketNumber >>> 8);
					byte byteB = (byte) (ackPacketNumber);
					dataACKPacket[0] = byteA;
					dataACKPacket[1] = byteB;

					// out.write(fileData, 4, receivePacket.getLength() - 4);
					// byte dataACKPacket[] = { 0, 4, receivePacket.getData()[2],
					// receivePacket.getData()[3] };
					sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress,
							receivePacket.getPort());
					sendReceiveSocket.send(sendPacket);
					// System.out.println("ack sent"+receivePacket.getPort());

				//	System.out.println("ack packet data is " + ackPacketNumber);

					ackPacketNumber++;


				// packetNumber++;

				if (receivePacket.getLength() < 64002) {
					
				//	System.out.println("break");
					break;
				}
			}
		}

		catch (IOException ioe) {

		}
		
		
//		File file = new File("/Users/Hanad/Documents/workspace/GroceryGoClientServer/src/GeneralSearch.json");
//
//		String testgood = "";
//		try {
//			Scanner fileScanner = new Scanner(file);
//			while (fileScanner.hasNextLine()) {
//				testgood += fileScanner.nextLine();
//			}
//			fileScanner.close();
//		} catch (IOException e) {
//			System.out.println("scanner error");
//		}




		return text;
	}

	private DatagramSocket bind() {
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();
		} catch (SocketException se) {
			// System.exit(1);

		}

		return socket;
	}

	public static void main(String args[]) {
		GroceryGoClient c = new GroceryGoClient();
		try {

			String type = "all"; // This is the variable for the type of request
									// that is going to be sent
			// for every product -- > 'all'
			// for specific ID --> [ID wanted]

			c.initialize(type);
		} catch (UnknownHostException e) {
		}
	}
}