import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Worker extends Thread {

  private Socket socket;
  private Server server;

  private BufferedReader input;
  private DataOutputStream output;
  private final String CRLF = "\r\n";
  private boolean active;

  public Worker(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
    active = true;
  }

  @Override
  public void run() {
    begin();
  }

  private void begin() {
    try {
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      output = new DataOutputStream(socket.getOutputStream());
      String line;
      String filePath = "";
      while (!socket.isClosed()) {
        while ((line = input.readLine()) != null && !line.isEmpty()) {
          System.err.println(line);
          validateLine(line);

          if (line.toLowerCase().contains("get")) {
            filePath = getFilePath(line);
          }
        }

        if (filePath.isEmpty()) {
          filePath = "/index.html";
        }
        
        Path currentWorkingDir = Paths.get("").toAbsolutePath();

        withStatusCode("200 OK");
        withContentType("text/html");
        withFilePayload(
            currentWorkingDir + "\\p3-webserver\\src\\main\\resources\\assets\\" + filePath);

        stopConnection();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getFilePath(String request) {
    String path = request.split(" ")[1];
    try {
      if (path.contains("exit")) {
        stopConnection();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return path.substring(1);
  }

  private boolean validateLine(String line) {
    if (!line.toLowerCase().contains("user-agent") || line.toLowerCase().contains("firefox")) {
      return true;
    }
    withStatusCode("406");
    withContentType("text/plain");
    withConnection("keep-alive");
    withPayload("406 Not Acceptable, only Firefox is allowed");
    stopConnection();
    return false;
  }

  private void write(String message) {
    try {
      output.flush();
      if (!socket.isClosed()) {
        output.writeBytes(message + CRLF);
      } else {
        input.close();
        output.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void withStatusCode(String statusCode) {
    write("HTTP/1.0 " + statusCode);
  }

  private void withContentType(String contentType) {
    write("Content-Type: " + contentType);
  }

  private void withContentLength(long contentLength) {
    write("Content-Length: " + contentLength);
  }

  private void withConnection(String connectionType) {
    write("Connection: " + connectionType);
  }

  private void withPayload(String payload) {
    long contentLength = payload.getBytes().length;
    withContentLength(contentLength);
    withCRLF();
    write(payload);
    withCRLF();
  }

  private void withFilePayload(String path) {
    File file = new File(path);
    withContentLength(file.length() + 2);
    withCRLF();
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {

      output.flush();
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        output.write(dataBuffer, 0, bytesRead);
      }
      input.close();
      output.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void withCRLF() {
    write("");
  }

  private void stopConnection() {
    active = false;
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //interrupt();
  }
}
