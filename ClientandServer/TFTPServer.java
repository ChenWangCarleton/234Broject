
//import java.util.Scanner;
import java.net.*;
import java.io.*;

public class TFTPServer {
    
	private static TFTPServer instance = null;
    
   // private ServerWaitThread serverWaitThread;
    private boolean initialized;
    
    
    /* Types of requests we can receive. */
    public static enum Request {READ, WRITE, ERROR};
	
    /* Constants for read/write responses */
    public static final byte[] READ_RESP = {0, 3, 0, 1};
    public static final byte[] WRITE_RESP = {0, 4, 0, 0};

    /* Main server port. */
    private static final int SERVER_RECV_PORT = 6900;

	/* Constants for packet sizes. */
    private static final int TFTP_DATA_SIZE = 516;
  //  private static final int TFTP_ACK_SIZE = 4;
    
  

    /* UDP datagram packets and sockets used to send / receive. */
    private DatagramPacket receivePacket;
    private DatagramSocket receiveSocket;
    //private DatagramSocket sendPacket;
    
    
    private int threadNumber;
    private ClientConnection clientConnection;
    ///private boolean running;
    
    private TFTPServer() { initialized = false; }

    /**
     * Returns the singleton instance of the TFTPErrorSimulator. If the 
     * TFTPErrorSimulator hasn't been instantiated yet, this function will
     * create a new instance and return a reference to it.
     * 
     * @return reference to TFTPErrorSimulator instance.
     */
    public static TFTPServer instanceOf() {
 	   //If instance not instantiated, instantiate.
 	   if (instance == null)
 		   instance = new TFTPServer();
 	   
 	   //Return reference to singleton instance.
 	   return instance;
    }
    
    public void receiveAndSendTFTP() {
        System.out.println("Server Started.");
        
        while (true) {
            
            if (!initialized) {      
            	
            	
            	
            	receiveSocket = TFTPServer.bind(SERVER_RECV_PORT);
                
                threadNumber = 0;
            	
                initialized = true;
                
            	  byte[] data;
                  data = new byte[TFTP_DATA_SIZE];
                  receivePacket = new DatagramPacket(data, data.length);

                  
                  System.out.println("Server: Waiting for packet."); 
                  

                  

                  /* Block until a datagram packet is received from receiveSocket. */
                  receivePacketFromHost(receiveSocket, receivePacket);
                  
                  threadNumber++;
                  clientConnection = new ClientConnection(threadNumber, receivePacket, data);
                  clientConnection.start();
                
            }
        
          
		   
        }
        
    }
    
    /**
     * Returns a DatagramSocket bound to port selected by the host. Terminates
     * the TFTPErrorSimulator if a SocketException occurs.
     * 
     * @return DatagramSocket bound to a port.
     */
    public static DatagramSocket bind() {
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
     * Returns a DatagramSocket bound to a provided port. Terminates
     * the TFTPErrorSimulator if a SocketException occurs.
     * 
     * @param port number to bind to socket.
     * @return DatagramSocket bound to a port.
     */
    public static DatagramSocket bind(int port) {
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
    
  
    
    public static void main(String args[]) {
    	TFTPServer server = TFTPServer.instanceOf();
    	server.receiveAndSendTFTP();
    }
    
}