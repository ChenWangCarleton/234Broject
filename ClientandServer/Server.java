// TFTPServer.java
// This class is the server side of a simple TFTP server based on
// UDP/IP. The server receives a read or write packet from a client and
// sends back the appropriate response without any actual file transfer.
// One socket (69) is used to receive (it stays open) and another for each response. 

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
   private static final int serverReceivePort = 6900;
   // UDP datagram packets and sockets used to send / receive
   private DatagramPacket receivePacket;
   private DatagramSocket receiveSocket;
   
   public Server()
   {
      try {
         // Construct a datagram socket and bind it to port 69
         // on the local host machine. This socket will be used to
         // receive UDP Datagram packets.
         receiveSocket = new DatagramSocket(serverReceivePort);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      }
   }

   public void receiveAndSend() throws Exception
   {

	   while(true){
	   
      byte[] data;
          
     
      data = new byte[100];
      receivePacket = new DatagramPacket(data, data.length);

	  System.out.println("\nServer: Waiting for packet.........\n");
	  try {
	       receiveSocket.receive(receivePacket);
	   } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	   }
	
	         // Process the received datagram.
	   System.out.println("Server: Packet received:");
	   System.out.println("From host: " + receivePacket.getAddress());
	   System.out.println("Host port: " + receivePacket.getPort());
	   int len = receivePacket.getLength();
	   System.out.println("Length: " + len);
	   }
   }

   public static void main( String args[] ) throws Exception
   {
      Server c = new Server();
      c.receiveAndSend();
   }
}


