/*
 * UDPClient.java
 *
 * Version 2.1
 * Vorlesung Rechnernetze HAW Hamburg
 * Autor: M. Huebner (nach Kurose/Ross)
 * Zweck: UDP-Client Beispielcode:
 *        UDP-Socket erzeugen, einen vom Benutzer eingegebenen
 *        String in ein UDP-Paket einpacken und an den UDP-Server senden,
 *        den String in Grossbuchstaben empfangen und ausgeben
 *        Nach QUIT beenden, bei SHUTDOWN den Serverthread beenden
 */

import java.io.IOException;
import java.net.*;

public class UDPClient {
  public final int SERVER_PORT = 547;
  public final String HOSTNAME = "ff02::1:2%4";
  public final int BUFFER_SIZE = 1024;
  public final String CHARSET = "IBM-850"; // "UTF-8"
  public final String MESSAGE_TYPE = "01";
  public final String TRANSACTION_ID = "777777";
  public final String OPTION_CODE = "0001";
  public final String OPTION_LEN = "000a";
  public final String DUID_TYPE = "0003";
  public final String HARDWARE_TYPE = "0006";
  public final String LINK_LAYER_ADDRESS = "38F9D3635E48";
  private DatagramSocket clientSocket;
  private boolean serviceRequested = true;
  public InetAddress SERVER_IP_ADDRESS;

  public void startJob() {
    String sentence;
    try {
      SERVER_IP_ADDRESS = Inet6Address.getByName(HOSTNAME);
      clientSocket = new DatagramSocket(546);

      while (serviceRequested) {
        System.err.println("ENTER UDP-DATA: ");
        sentence = MESSAGE_TYPE + TRANSACTION_ID + OPTION_CODE + OPTION_LEN + DUID_TYPE + HARDWARE_TYPE + LINK_LAYER_ADDRESS;
        writeToServer(sentence);
        serviceRequested = false;
        readFromServer();
      }
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Connection aborted by server!");
    }
    System.err.println("UDP Client stopped!");
  }

  private void writeToServer(String sendString) throws IOException {
    byte[] sendData = ServiceCode.hexStringtoByteArray(sendString);
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, SERVER_IP_ADDRESS, SERVER_PORT);
    clientSocket.send(sendPacket);

    System.err.println("UDP Client has sent the message: " + sendString);
  }

  private String readFromServer() throws IOException {
    String receiveString = "";

    byte[] receiveData = new byte[BUFFER_SIZE];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, BUFFER_SIZE);

    clientSocket.receive(receivePacket);
    receiveString = ServiceCode.byteArraytoHexString(receivePacket.getData());

    System.err.println("UDP Client got from Server: " + receiveString);
    return receiveString;
  }

  public static void main(String[] args) throws SocketException {
    //ServiceCode.showNetwork();
    UDPClient myClient = new UDPClient();
    myClient.startJob();
  }
}
