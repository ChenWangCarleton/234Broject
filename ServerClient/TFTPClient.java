
 

import java.io.*;
import java.net.*;
import java.util.*;

public class TFTPClient {
	
	private static final int SERVER_RECV_PORT = 60700;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	

	public TFTPClient() {
		sendReceiveSocket = bind();
	}


	private void sendAndReceive() throws UnknownHostException {
		int sendPort;
		//System.out.println("Client: Started");
		//System.out.println("Client: initializing Server");
		sendPort = SERVER_RECV_PORT;
        int clientConnectionPort = -1;
        InetAddress serverAddress = InetAddress.getByName("99.249.57.222");
	 
	 try {
		clientConnectionPort = sendRequest(serverAddress, sendPort, "all");
	} catch (IOException e) {
		
		//System.out.println("error");
	}
	 
	 receive(serverAddress, clientConnectionPort);
		
	
		
	}


	private int sendRequest(InetAddress serverAddress, int sendPort, String productID) throws IOException{
		byte[] msg = new byte[516], 
				fn, 
				md, 
				data; 
		String filename;
		int len;
	//	System.out.println("Client: creating packet ");
		filename = "   "; 
		fn = filename.getBytes(); 		
		byte opCode;
		if(productID.equals("all")){
			opCode = 1;
		} else{
			opCode = 2;
		}

		msg[fn.length+2] = 0; 
		md = productID.getBytes();
		len = fn.length+md.length+4; 
		msg[len-1] = 0; 
		msg[1] = opCode; 

		
		sendPacket   = new DatagramPacket(msg, len,serverAddress,sendPort );
		sendReceiveSocket.send(sendPacket);
		
		System.out.println("Client: Packet Sent ");
	
		data = new byte[4];
		receivePacket = new DatagramPacket(data, data.length);
		
		sendReceiveSocket.receive(receivePacket);
		System.out.println("Client: Packet received ");
        return receivePacket.getPort(); 
	}



	private String receive(InetAddress serverAddress, int sendPort){

		System.out.println("\nCommencing file transfer: ALL" );
		
		String text  = "";
	//	File newFile = new File("buffer.txt");
		try {
//			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
			byte[] fileData = new byte[516];
		
			
			while(true){
				receivePacket = new DatagramPacket(fileData, fileData.length);
				
				sendReceiveSocket.receive(receivePacket);

				
				String modifiedSentence = new String(receivePacket.getData()); //Turn data into STRING

				text+=modifiedSentence;
				
				
		//		out.write(fileData, 4, receivePacket.getLength() - 4);
				byte dataACKPacket[] = {0, 4, receivePacket.getData()[2], receivePacket.getData()[3]};
				
				sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress, sendPort);
				sendReceiveSocket.send(sendPacket);

			
				
			 System.out.println("Client: Packet sent inside receive.\n");
				
			if(receivePacket.getLength() < 516){
					break;
				}
			}
		}
//			out.close();
		catch (IOException ioe){
			
			return " ";
		}
			
//		String text  = "";
//		try{
//	 		Scanner fileScanner = new Scanner(newFile);
//	 		while (fileScanner.hasNextLine()) {
//	 		    text += fileScanner.nextLine();	 		
//	 		}
//	 		fileScanner.close();
//	 		}catch(IOException e){
//	 			System.out.println("scanner error");
//	 		}
//		
//		System.out.println("File transfer is complete.");
		
		System.out.println(text);
		return text; 
	}

 
    private DatagramSocket bind() {
 	   DatagramSocket socket = null;
 	   
 	   try {
          	//socket = new DatagramSocket(60700,InetAddress.getByName("172.17.192.168"));
         	socket = new DatagramSocket();
 	   } catch (SocketException se) {
 		   //System.exit(1);
           
 	   }
 	   
 	   return socket;
    }
    

	
	public static void main(String args[]){
		TFTPClient c = new TFTPClient();
		try {
			c.sendAndReceive();
		} catch (UnknownHostException e) {
			//  System.exit(1);
		}
	}
}