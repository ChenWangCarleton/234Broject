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

        // int packetNumber = 1;
        String text = "";
        try {
            byte[] fileData = new byte[64000];

            while (true) {

                try {

                    // sendReceiveSocket.setSoTimeout(5000);
                    receivePacket = new DatagramPacket(fileData, fileData.length);

                    sendReceiveSocket.receive(receivePacket);

                    byte[] data = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());

                    String modifiedSentence = new String(data); // Turn data
                    // into STRING

                    text += modifiedSentence;

                    // out.write(fileData, 4, receivePacket.getLength() - 4);
                    byte dataACKPacket[] = { 0, 4, receivePacket.getData()[2], receivePacket.getData()[3] };
                    sendPacket = new DatagramPacket(dataACKPacket, dataACKPacket.length, serverAddress, receivePacket.getPort());
                    sendReceiveSocket.send(sendPacket);

                } catch (SocketTimeoutException e) {
                    // Send the ACK packet via send/receive socket.
                    sendReceiveSocket.send(sendPacket);

                }

                // packetNumber++;

                if (receivePacket.getLength() < 64000) {
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