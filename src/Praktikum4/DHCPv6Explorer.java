package Praktikum4;
import java.io.*;

import java.net.*;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class DHCPv6Explorer{
    public final int SERVER_PORT = 547; //DHCPv6 Server Port
    public final int CLIENT_PORT = 546; //DHCPv6 Client Port
    public final int BUFFER_SIZE = 80;
    public final String CHARSET = "IBM-850";
    public final String IPv6 = "FF020000000000000000000000010002"; //Multicast adresse --> FF02::1

    public final String MSG_TYPE = "01"; //Solicid Message
    public final String TRANSACTION_ID = "000001";
    public final String DUID = "0001";
    public final String HARDWARE_TYPE = "0006"; //IEEE
    public final String HARDWARE_ADDRESS = "D8F8830C5201"; //MAC
    public final String CLIENT_OPTION = "0001";
    public final String OPTION_LEN = "000A";
    public String payload = MSG_TYPE + TRANSACTION_ID + CLIENT_OPTION + OPTION_LEN + DUID + HARDWARE_TYPE + HARDWARE_ADDRESS;
    public byte[] arr = hexStringtoByteArray(payload);

    public int scope_id = 15;
    public String host = "DHCPv6";

    private MulticastSocket multicastSocket;
    private boolean serviceRequested = true;
    private Inet6Address SERVER_IP_ADDRESS;

    public void startJob() throws SocketException, UnknownHostException
    {
        //showNetwork();
        Scanner inFromUser;

        try
        {
            //Server Adresse ermitteln
            SERVER_IP_ADDRESS = Inet6Address.getByAddress(host, hexStringtoByteArray(IPv6), scope_id);
            System.out.println(SERVER_IP_ADDRESS);


            multicastSocket = new MulticastSocket(CLIENT_PORT);

            inFromUser = new Scanner(System.in, CHARSET);

            while (serviceRequested)
            {
                if (inFromUser.nextLine().startsWith("quit"))
                {
                    serviceRequested = false;
                }
                else
                {
                    System.out.println("sending SOLICIT Request");
                    writeToServer();
                    readFromServer();
                }
            }
            multicastSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Connection aborted");
        }

        System.out.println("Client stopped!");
    }


    private void writeToServer() throws IOException
    {
        DatagramPacket sendPacket = new DatagramPacket(arr, arr.length,
                SERVER_IP_ADDRESS, SERVER_PORT);

        multicastSocket.send(sendPacket);

        System.out.println("Client sent message: " + byteArraytoHexString(arr));
    }

    private void readFromServer() throws IOException
    {
        byte[] receiveData = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, BUFFER_SIZE);
        System.out.println("Listening for Advertise");


        multicastSocket.receive(receivePacket);
        System.out.println("Advertise received");
        System.out.println("---------------------------------");
        System.out.println("Adress:" + receivePacket.getAddress());
        System.out.println("Port:" + receivePacket.getPort());
        System.out.println("Length:" + receivePacket.getLength());
        System.out.println("Data:" + byteArraytoHexString(receivePacket.getData()));
    }

    private String byteArraytoHexString(byte[] byteArray)
    {
        String hex = "";
        if (byteArray != null) {
            for (int i = 0; i < byteArray.length; ++i) {
                hex = hex + String.format("%02X", byteArray[i]);
            }
        }
        return hex;
    }


    private byte[] hexStringtoByteArray(String hex)
    {
        byte[] val = new byte[hex.length() / 2];
        for (int i = 0; i < val.length; i++)
        {
            int index = i * 2;
            int num = Integer.parseInt(hex.substring(index, index + 2), 16);
            val[i] = (byte) num;
        }
        return val;
    }


    public static void main(String[] args) throws SocketException, UnknownHostException
    {
        DHCPv6Explorer myClient = new DHCPv6Explorer();
        myClient.startJob();
    }

    private void showNetwork() throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            System.out.println("\nDisplay Name = " + ni.getDisplayName());
            System.out.println(" Name = " + ni.getName());
            System.out.println(" Scope ID = " + ni.getIndex());
            System.out.println(" Hardware (LAN) Address = " + byteArraytoHexString(ni.getHardwareAddress()));

            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            Iterator<InterfaceAddress> it = list.iterator();

            while (it.hasNext()) {
                InterfaceAddress ia = it.next();
                System.out
                        .println(" Adress = " + ia.getAddress() + " with Prefix-Length " + ia.getNetworkPrefixLength());
            }
        }
    }
}
