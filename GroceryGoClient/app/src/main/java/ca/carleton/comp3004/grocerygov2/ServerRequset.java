package ca.carleton.comp3004.grocerygov2;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AbdullrhmanAljasser on 11/23/2017.
 */

public class ServerRequset {

    private static final int SERVER_RECV_PORT = 60800;
    private Socket clientSocket;
    private InetAddress serverAddress;

    public ServerRequset() throws UnknownHostException {
        //this.clientSocket = bind();
        serverAddress = InetAddress.getByName("174.115.82.38");
        try {
            clientSocket = new Socket(serverAddress, SERVER_RECV_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String initialize(String type) throws UnknownHostException {
        try {
            return sendRequest(SERVER_RECV_PORT, type);
        } catch (IOException e) {
            return null;
        }
    }

    private String sendRequest(int sendPort, String productID) throws IOException {
        //serverAddress = InetAddress.getByName("172.17.215.107");
        //clientSocket = new Socket(serverAddress, SERVER_RECV_PORT);
        byte[] msg = new byte[516], productIDbyte;
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

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // sendPacket = new DatagramPacket(msg, msg.length, serverAddress,
        // sendPort);

        outToServer.write(msg);

        // clientSocket.send(sendPacket);

        // data = new byte[4];
        // receivePacket = new DatagramPacket(data, data.length);
        //
        // clientSocket.receive(receivePacket);
        // System.out.println("ack received");

        return receive();

    }

    private String receive() {

        //int PacketNumber = 1;

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
                String modifiedSentence = new String(data, "UTF-8"); // Turn																		// datas												// STRIN
                text += modifiedSentence;

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        System.out.println(text.length());
        //Log.e("Testing: ", "At ServerRequest" + text);
        return text;
    }

    private Socket bind() {
        Socket socket = null;
        try {
            serverAddress = InetAddress.getByName("174.115.82.38");
            clientSocket = new Socket(serverAddress, SERVER_RECV_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    //Functions to translate data recived by server.
    public ArrayList<Product> readGetAll(String jsonStr) throws  Exception{
        //	ArrayList<itemForGetAll> result=new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Product> result = mapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Product.class));
        return result;
    }

    public Product readSearchID(String jsonStr) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        Product result = mapper.readValue(jsonStr, Product.class);
        return result;
    }

    public ArrayList<Product> readProcess(String jsonStr) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Product> result = mapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Product.class));
        return result;
    }

    public Product getItem(int id) throws UnknownHostException {

        try {
            return readSearchID(initialize(""+id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
