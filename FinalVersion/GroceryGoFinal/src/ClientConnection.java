
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.List;
import java.io.*;
import java.net.*;

public class ClientConnection extends Thread {
	/* Types of requests that we can receive. */
	public static enum Request {
		All, ONE, MULTIPLE;
	};

	public static enum OPCODE {
		All(1), ONE(2), MULTIPLE(3);

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
	private int packetNumber;
	private int threadNumber;
	private String dataforAll;

	private DataOutputStream outToClient;
	private Socket connectionSocket;

	public ClientConnection(Socket out, int number, String alldata) {
		threadNumber = number;
		connectionSocket = out;
		dataforAll = alldata;
	}

	@Override
	public void run() {

		System.out.println("ClientConnectionThread number: Request Number is "+threadNumber);

		DataInputStream inFromClient = null;
		try {
			inFromClient = new DataInputStream(connectionSocket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		byte[] data = new byte[516];
		try {
			inFromClient.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

			
		try {

			outToClient = new DataOutputStream(connectionSocket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Request req = getRequest(data);
		

		int len = data.length;

		int j = 0;

		for (j = 2; j < len; j++) {
			if (data[j] == 0)
				break;
		}

		byte[] productIDbyte = Arrays.copyOfRange(data, 2, j);
		String productID = new String(productIDbyte); // Turn data

		if (productID.contains(",")) {
			req = Request.MULTIPLE;
		}
		System.out.println("req " + req);

		if (req == Request.All) {
			System.out.println(req + " request received.");
		} else if (req == Request.ONE) {
			System.out.println(req + " request received.");
		} else if (req == Request.MULTIPLE) {
			System.out.println(req + " request received.");
		} else {
			System.out.println(req + " this is an error request received.");

		}

		if (req == Request.All) {

			System.out.println("ClientConnection: Sending All");
			sendAll(outToClient);

		} else if (req == Request.ONE) {

			System.out.println("ClientConnection: Sending Product ID:" + productID);

			sendOne(productID, outToClient);

		} else if (req == Request.MULTIPLE) {

			System.out.println("ClientConnection: Sending Multiple Products");

			sendMultiple(productID, outToClient);

		}
		// sendSocket.close();

	}

	private void sendAll(DataOutputStream outToClient) {

		String data = dataforAll;

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

			byte packetData[] = { 0 };

			try {
				packetData = new byte[dataSubstring.getBytes("UTF-8").length];

				System.arraycopy(dataSubstring.getBytes("UTF-8"), 0, packetData, 0,
						dataSubstring.getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			sendPacketToHost(outToClient, packetData);
			System.out.println("data packet sent is " + packetNumber);
			packetNumber++;

		}

		try {
			outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("File transfer completed successfully");

	}

	private void sendOne(String productID, DataOutputStream outToClient) {

		System.out.println("in here");
		String data = "";

		ArrayList<String> source = new ArrayList<>();
		source.add("LoblawsWithBrand.json");
		source.add("IndependentWithBrand.json");
		source.add("WalmartWithBrand.json");
		String target = "/Users/Hanad/Documents/workspace/GroceryGoFinal/sample/Main.json";
		UserSearch us = new UserSearch(source, target);

		
		int productIDint = Integer.parseInt(productID);

		try {// searchbyid
			data = us.searchByID(productIDint);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\nCommencing file transfer...\n");

		int dataSize = data.length();
		packetNumber = 1;
		int dataPosition = 0;
		String dataSubstring = data;

		if (dataPosition + 64000 < dataSize) {
			dataSubstring = data.substring(dataPosition, dataPosition + 64000);
		} else {
			dataSubstring = data.substring(dataPosition, data.length());
		}
		dataPosition += 64000;

		byte packetData[] = { 0 };

		try {
			packetData = new byte[dataSubstring.getBytes("UTF-8").length];

			System.arraycopy(dataSubstring.getBytes("UTF-8"), 0, packetData, 0, dataSubstring.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		sendPacketToHost(outToClient, packetData);
		System.out.println("Data packet sent number: " + packetNumber);

		packetNumber++;
		/* Clear data from block. */

		try {
			outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("File transfer completed successfully");

	}

	private void sendMultiple(String IDs, DataOutputStream outToClient) {

		ArrayList<String> multipleProducts = new ArrayList<String>(Arrays.asList(IDs.split(",")));

		ArrayList<Integer> multipleProductsInt = new ArrayList<Integer>();

		for (int i = 0; i < multipleProducts.size(); i++) {

			multipleProductsInt.add(Integer.parseInt(multipleProducts.get(i)));

		}

		String data = "";

		ArrayList<String> source = new ArrayList<>();
		source.add("LoblawsWithBrand.json");
		source.add("IndependentWithBrand.json");
		source.add("WalmartWithBrand.json");
		String target = "/Users/Hanad/Documents/workspace/GroceryGoFinal/sample/Main.json";
		UserSearch us = new UserSearch(source, target);

		try {// searchbyid
			data = us.searchByID(multipleProductsInt);
		} catch (Exception e) {
			e.printStackTrace();
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

			byte packetData[] = { 0 };

			try {
				packetData = new byte[dataSubstring.getBytes("UTF-8").length];

				System.arraycopy(dataSubstring.getBytes("UTF-8"), 0, packetData, 0,
						dataSubstring.getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			sendPacketToHost(outToClient, packetData);
			System.out.println("Data packet sent number: " + packetNumber);
			packetNumber++;

		}

		try {
			outToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("File transfer completed successfully");

	}

	public static void sendPacketToHost(DataOutputStream output, byte[] data) {
		// receiveSocket.setSoTimeout(5000);
		try {

			output.write(data, 0, data.length);
			output.flush();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private Request getRequest(byte[] data) {

		Request req = null;

		if (data[1] == OPCODE.All.value())
			req = Request.All; // could be read
		else if (data[1] == OPCODE.ONE.value())
			req = Request.ONE; // could be write
		else if (data[1] == OPCODE.MULTIPLE.value()) {
			req = Request.MULTIPLE; // could be write

		}

		return req;

	}
}
