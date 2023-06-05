import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Worker extends Thread {

  private Socket socket;
  private BufferedReader input;
  private DataOutputStream output;
  private final String CRLF = "\r\n";

  public Worker(Socket socket) {
    this.socket = socket;
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
          filePath = line.toLowerCase().contains("get") ? getFilePath(line).toLowerCase() : filePath;
        }

        if (filePath.equals("time") || filePath.equals("date")) {
          handleRestRequest(filePath);
        } else {
          filePath = filePath.isEmpty() ? "/index.html" : filePath;
          Path currentWorkingDir = Paths.get("").toAbsolutePath();
          validatePath(currentWorkingDir + "\\p3-webserver\\src\\main\\resources\\assets\\" + filePath);

          withStatusCode("200 OK");
          withContentType(filePath);
          withFilePayload(currentWorkingDir + "\\p3-webserver\\src\\main\\resources\\assets\\" + filePath);
        }
        stopConnection();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      stopConnection();
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
    withError(406, "Not Acceptable, only Firefox is allowed");
    return false;
  }

  private boolean validatePath(String path) {
    if (!path.contains(".")) {
      withError(400, "Bad Request :/");
      return false;
    }
    File file = new File(path);
    if (!file.isFile()) {
      withError(404, "Not Found :/");
      return false;
    }
    return true;
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

  private void withContentType(String filePath) {
    try {
      String fileType = filePath.substring(filePath.indexOf("."));
      String contentType = switch (fileType) {
        case ".gif" -> "image/gif";
        case ".jpg" -> "image/jpeg";
        case ".ico" -> "image/x-icon";
        case ".pdf" -> "application/pdf";
        case ".html" -> "text/html";
        default -> "text/plain";
      };
      write("Content-Type: " + contentType);
    } catch (StringIndexOutOfBoundsException e) {
      withError(400, "Bad Request");
    }
  }

  private void withContentLength(long contentLength) {
    write("Content-Length: " + contentLength);
  }

  private void withPayload(String payload) {
    long contentLength = payload.getBytes().length;
    withContentLength(contentLength);
    withCRLF();
    write(payload);
    withCRLF();
  }

  private void withFilePayload(String path) {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))) {
      output.flush();

      withContentLength(Files.size(Path.of(path)));
      withCRLF();

      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        output.write(dataBuffer, 0, bytesRead);
      }
      input.close();
      output.close();
    } catch (FileNotFoundException fe) {
      withError(404, "Not Found :(");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void withCRLF() {
    write("");
  }

  private void stopConnection() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //interrupt();
  }

  private void handleRestRequest(String path) {
    DateTimeFormatter format =
        path.equals("time") ?
            DateTimeFormatter.ofPattern("HH:mm:ss") :
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    withStatusCode("200 OK");
    withContentType(".txt");
    withPayload(LocalDateTime.now().format(format));
  }

  private void withError(int statusCode, String message) {
    withStatusCode(statusCode + "");
    withContentType(".txt");
    withPayload("%s %s".formatted(statusCode, message));
    stopConnection();
  }
}
