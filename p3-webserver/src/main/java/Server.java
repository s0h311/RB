import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  private boolean active;

  public static void main(String[] args) {
    Server server = new Server();
    server.start();
  }

  public Server() {
    active = true;
  }

  public void start() {
    try {
      ServerSocket serverSocket = new ServerSocket(42069);
      while (active) {
        Socket connectionSocket = serverSocket.accept();
        System.err.println("========================");
        System.err.println("New Client");
        System.err.println("========================\n");

        new Worker(connectionSocket, this).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
