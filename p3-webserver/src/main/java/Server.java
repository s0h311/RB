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
      ServerSocket serverSocket = new ServerSocket(3333);
      while (active) {
        Socket connectionSocket = serverSocket.accept();
        System.err.println("========================");
        System.err.println("New Request");
        System.err.println("========================\n");

        new Worker(connectionSocket).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
