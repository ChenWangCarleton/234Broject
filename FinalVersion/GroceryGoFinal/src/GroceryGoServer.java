
//import java.util.Scanner;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class GroceryGoServer {

	/* UDP datagram packets and sockets used to send / receive. */
	private static ServerSocket receiveSocket;
	// private DatagramSocket sendPacket;

	private int threadNumber;
	private ClientConnection clientConnection;
	private String alldata; 
	/// private boolean running;

	private GroceryGoServer() {
		
		
		System.out.println("Please wait.Loading data...");
		alldata = "";

		ArrayList<String> source = new ArrayList<>();
		source.add("LoblawsWithBr	and.json");
		source.add("IndependentWithBrand.json");
		source.add("WalmartWithBrand.json");
		String target="/Users/Hanad/Documents/workspace/GroceryGoFinal/sample/Main.json";
		UserSearch us=new UserSearch(source,target);
	
	try {
		alldata = us.generalSearch();
	} catch (IOException e1) {
		e1.printStackTrace();
	}

	}

	public void receiveRequest() {
		System.out.println("Server Started.");
		threadNumber = 0;
		try {
			receiveSocket = new ServerSocket(60800);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {

			Socket connectionSocket = null;
			try {
				System.out.println("Waiting For Request");
				connectionSocket = receiveSocket.accept();
			} catch (IOException e1) {
				e1.printStackTrace();
			}


			threadNumber++;
			clientConnection = new ClientConnection(connectionSocket, threadNumber,alldata);
			clientConnection.start();


		}
	}


	public static void main(String args[]) throws IOException {
		GroceryGoServer server = new GroceryGoServer();
		server.receiveRequest();

	}

}