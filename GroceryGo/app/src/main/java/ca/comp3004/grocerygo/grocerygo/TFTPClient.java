package ca.comp3004.grocerygo.grocerygo;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

/**
 * Created by AbdullrhmanAljasser on 10/28/2017.
 */

public class TFTPClient {

    private static final int SERVER_RECV_PORT = 60800;

    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;

    public TFTPClient() {
        sendReceiveSocket = bind();
    }

    public ArrayList<itemForGetAll> initialize(String type) throws UnknownHostException {
        try {
            //readGetAll
            InetAddress serverAddress = InetAddress.getByName("192.168.0.10");

            return readGetAll(sendRequest(serverAddress, SERVER_RECV_PORT, type));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public mainItem getItem(String id) throws UnknownHostException {
        InetAddress serverAddress = InetAddress.getByName("192.168.0.10");

        try {
            return readSearchID(sendRequest(serverAddress, SERVER_RECV_PORT, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String sendRequest(InetAddress serverAddress, int sendPort, String productID) throws IOException {
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

        data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length);

        sendReceiveSocket.receive(receivePacket);

        return receive(serverAddress, receivePacket.getPort());
        //receive(serverAddress, SERVER_RECV_PORT);

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



                        dataPacketNumber  = (((int) (receivePacket.getData()[0] & 0xFF)) << 8) + (((int) receivePacket.getData()[1]) & 0xFF);



                        if (dataPacketNumber < ackPacketNumber) {



                            continue;

                        }


                    } catch (SocketTimeoutException e) {
                        // Send the ACK packet via send/receive socket.



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


                sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress,
                        receivePacket.getPort());
                sendReceiveSocket.send(sendPacket);


                ackPacketNumber++;



                if (receivePacket.getLength() < 64002) {

                    break;
                }
            }
        }
        catch (IOException ioe) {
            Log.e("Client", "Error");
        }

        Log.e("Client", ""+text.length());//Testing.

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

    public ArrayList<itemForGetAll> readGetAll(String jsonStr) throws  Exception{
        //	ArrayList<itemForGetAll> result=new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<itemForGetAll> result = mapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, itemForGetAll.class));
        return result;
    }

    public mainItem readSearchID(String jsonStr) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mainItem result=mapper.readValue(jsonStr, mainItem.class);
        return result;
    }
}