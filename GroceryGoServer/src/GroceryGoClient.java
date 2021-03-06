
import java.io.*;
import java.net.*;
import java.util.*;

public class GroceryGoClient {

	private static final int SERVER_RECV_PORT = 60800;
	private Socket clientSocket;
	private InetAddress serverAddress;

	public GroceryGoClient() {
		clientSocket = bind();
	}

	private void initialize(String type) throws UnknownHostException {
		try {
			sendRequest(SERVER_RECV_PORT, type);
		} catch (IOException e) {

		}

	}
	
	//Purpose: Send reuqest to Server for ALl products or one product
	private String sendRequest(int sendPort, String productID) throws IOException {
		byte[] msg = new byte[516], productIDbyte;
		byte opCode;


		if (productID.equals("all")) {
			opCode = 1;
		}
		else{
			opCode = 2;
		}

		// Build byte array for packet
		productIDbyte = productID.getBytes();
		msg[0] = 0;
		msg[productIDbyte.length + 2] = 0;
		msg[1] = opCode; 
		System.arraycopy(productIDbyte, 0, msg, 2, productIDbyte.length); // Copy
																			// into
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());


		outToServer.write(msg);


		return receive();

	}

	
	//Purpose: Where the client receives information from the Server.  
	private String receive() {	
		

		DataInputStream inFromServer = null;
		try {

			inFromServer = new DataInputStream(clientSocket.getInputStream());

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String text = "";
		int length;

		byte[] fileData = new byte[64000];
		byte[] data;

		try {
			while ((length = inFromServer.read(fileData)) != -1) {

				data = Arrays.copyOfRange(fileData, 0, length);
				String modifiedSentence = new String(data, "UTF-8"); // Turn
				text += modifiedSentence;

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		System.out.println(text.length());
		return text;
	}

	//Purpose:Create Socket bound to Servers IP Address and port 60800
	private Socket bind() {
		Socket socket = null;
		try {
			serverAddress = InetAddress.getByName("174.115.82.38");
			socket = new Socket(serverAddress, SERVER_RECV_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}

	public static void main(String args[]) {
		GroceryGoClient c = new GroceryGoClient();
		try {

			String type = "all,"; // This is the variable for the type of request
								// that is going to be sent
			// for every product -- > 'all'
			// for specific ID --> [ID wanted]

			c.initialize(type);
		} catch (UnknownHostException e) {
		}
	}
}