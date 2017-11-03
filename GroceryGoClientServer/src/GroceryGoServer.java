
//import java.util.Scanner;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class GroceryGoServer {

	/* Types of requests we can receive. */
	public static enum Request {
		READ, WRITE, ERROR
	};

	/* Constants for read/write responses */
	public static final byte[] READ_RESP = { 0, 3, 0, 1 };
	public static final byte[] WRITE_RESP = { 0, 4, 0, 0 };

	/* Main server port. */
	private static final int SERVER_RECV_PORT = 60800;

	/* Constants for packet sizes. */
	private static final int TFTP_DATA_SIZE = 516;
	// private static final int TFTP_ACK_SIZE = 4;

	/* UDP datagram packets and sockets used to send / receive. */
	private DatagramPacket receivePacket;
	private static DatagramSocket receiveSocket;
	private static boolean nextIteration = false;
	// private DatagramSocket sendPacket;

	private int threadNumber;
	private ClientConnection clientConnection;
	/// private boolean running;

	private GroceryGoServer() {

	}

	public void receiveRequest() {
		System.out.println("Server Started.");
		receiveSocket = GroceryGoServer.bind(SERVER_RECV_PORT);

		while (true) {

			// UserSearch getAllSearch = new UserSearch();
			String getAllData = "";
//			 try {
//			 getAllData = getAllSearch.generalSearch();
//			 } catch (IOException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			 }
//			

			threadNumber = 0;

			byte[] data = new byte[TFTP_DATA_SIZE];
			receivePacket = new DatagramPacket(data, data.length);

			System.out.println("Server: Waiting for packet.");

			/* Block until a datagram packet is received from receiveSocket. */

			try {
				receiveSocket.setSoTimeout(0);

				receiveSocket.receive(receivePacket);
				
				System.out.println("The port is "+receivePacket.getPort());
				
				

			} catch (IOException e) {
				
				e.printStackTrace();
				

			}

			threadNumber++;
			clientConnection = new ClientConnection(threadNumber, receivePacket, data, getAllData);
			clientConnection.start();
			
			

			while (!nextIteration){
				
				
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//System.out.println("infinite");
				
			}
			nextIteration = false; 
			
		//	break;
		}

	}

	/**
	 * Returns a DatagramSocket bound to port selected by the host. Terminates
	 * the TFTPErrorSimulator if a SocketException occurs.
	 * 
	 * @return DatagramSocket bound to a port.
	 */
	public static DatagramSocket bind() {
		// Create new socket reference
		DatagramSocket socket = null;

		// Attempt to bind socket
		try {
			socket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		// Return bound socket
		return socket;
	}

	/**
	 * Returns a DatagramSocket bound to a provided port. Terminates the
	 * TFTPErrorSimulator if a SocketException occurs.
	 * 
	 * @param port
	 *            number to bind to socket.
	 * @return DatagramSocket bound to a port.
	 */
	public static DatagramSocket bind(int port) {
		// Create new socket reference
		DatagramSocket socket = null;

		// Attempt to bind socket to port
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		// Return bound socket
		return socket;
	}

	/**
	 * Sends a given DatagramPacket to a host through a provided DatagramSocket.
	 * Terminates TFTPErrorSimulator if IOException occurs.
	 * 
	 * @param socket
	 *            to send through.
	 * @param packet
	 *            to send.
	 */
	public static void sendPacketToHost(DatagramPacket packet, DatagramSocket socket) {
		//receiveSocket.setSoTimeout(5000);
		try {
			
			receiveSocket.send(packet);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void finished() {

		nextIteration = true;
	}

	public static void receivePacketFromHost(DatagramPacket packet, DatagramSocket socket) throws SocketTimeoutException {
		// Block until packet receives

		try {
			
		
			receiveSocket.setSoTimeout(2000);
			receiveSocket.receive(packet);
			
		

		} catch (IOException e) {
			if (e instanceof SocketTimeoutException){
				
				throw new SocketTimeoutException();
				
//			e.printStackTrace();
//			System.exit(1);
		}
			
			else{
				System.exit(1);
				
			}
	}
	}
	public static void main(String args[]) throws IOException {
		GroceryGoServer server = new GroceryGoServer();
		server.receiveRequest();

	}

}