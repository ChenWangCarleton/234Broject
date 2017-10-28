package ca.comp3004.grocerygo.grocerygo;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by AbdullrhmanAljasser on 10/28/2017.
 */

public class TFTPClient {
    private static final int SERVER_RECV_PORT = 60800;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;

    public TFTPClient() {
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendAndReceive() throws UnknownHostException {
        int clientConnectionPort = 0;
        InetAddress serverAddress = InetAddress.getByName("172.17.49.153");

        try {
            clientConnectionPort = sendRequest(serverAddress, SERVER_RECV_PORT, "all");
        } catch (IOException e) {
            e.printStackTrace();
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

        Log.e("Client","Sent");

        data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length);

        sendReceiveSocket.receive(receivePacket);
        Log.e("Client","Received");
        return receivePacket.getPort();
    }

    private String receive(InetAddress serverAddress, int sendPort){
        int packetNumber = 1;
        String text  = "";
        try {
            byte[] fileData = new byte[64000];


            while(true){

                //try{


                    //sendReceiveSocket.setSoTimeout(5000);

                    receivePacket = new DatagramPacket(fileData, fileData.length);



                    sendReceiveSocket.receive(receivePacket);


                    String modifiedSentence = new String(receivePacket.getData()); //Turn data into STRING

                    Log.e("Client",""+modifiedSentence);

                    text+=modifiedSentence;


                    //		out.write(fileData, 4, receivePacket.getLength() - 4);
                    byte dataACKPacket[] = {0, 4, receivePacket.getData()[2], receivePacket.getData()[3]};

                    sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress, sendPort);
                    sendReceiveSocket.send(sendPacket);
                /*}catch(SocketTimeoutException e){
                    // Send the ACK packet via send/receive socket.
                    sendReceiveSocket.send(sendPacket);

                }*/
                Log.e("Client", "Packet sent."+packetNumber);

                packetNumber++;

                if(receivePacket.getLength() < 64000){

                    System.out.println("break.\n");


                    break;
                }
            }
        }
        catch (IOException ioe){

            return "in here ";
        }
        Log.e("Client",""+text);
        return text;
    }


}