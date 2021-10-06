package gla.server;

import gla.utils.Printer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Class for connecting client to Server.
 */
public class HttpHandler implements Runnable {

  static final File WEB_ROOT = new File(".");
  static final String DEFAULT_FILE = "index.html";
  static final String NOT_FOUND_FILE = errorNotFound();
  static final int NOT_FOUND_SIZE = NOT_FOUND_FILE.getBytes().length;

  private Socket connect;
  private Path folderFileRequested;

  public HttpHandler(Socket c, Path folderFileRequested) {
    this.connect = c;
    this.folderFileRequested = folderFileRequested;
  }

  /**
   * This methode return string of html page error 404.
   *
   * @Return string of html page error 404.
   */
  private static String errorNotFound() {
    return "<html>\n"
        + " <head></head>\n"
        + " <body>\n"
        + "  <h1>Error 404 Not Found !</h1>\n"
        + " </body>\n"
        + "</html>";
  }

  /**
   * This methode return the real path of the requestedfile of the local user.
   *
   * @param pathRequestedFile is the path asked by the client
   * @return the real path of the requestedfile of the local user
   */
  private String pathLocalRequestedFile(String pathRequestedFile) {
    String pathToOutPutFolder = folderFileRequested.toString() + "/";

    if (pathRequestedFile.endsWith("/")) {
      pathRequestedFile += (pathToOutPutFolder + DEFAULT_FILE);
    } else {
      String[] path = pathRequestedFile.split("/");
      pathRequestedFile = path[0] + pathToOutPutFolder + path[1];
    }
    System.out.println("filerequested : " + pathRequestedFile);

    return pathRequestedFile;
  }

  /**
   * Check the content type of the file.
   *
   * @param fileRequested to be checked
   * @return string to be added in the hearder request
   */
  private String getContentType(String fileRequested) {
    if (fileRequested.endsWith(".html")) {
      return "text/html";
    } else {
      return "text/plain";
    }
  }

  /**
   * Write http header requested.
   *
   * @param out         were the http header will be written
   * @param contentType specification on the body content
   * @param fileLength  length of the body
   */
  private void sendHttpHeader(PrintWriter out, String contentType, int fileLength, String code) {
    out.println(code);
    out.println("Server: Java HTTP Server : 1.0");
    out.println("Date: " + new Date());
    out.println("Content-type: " + contentType);
    out.println("Content-length: " + fileLength);

    //Blank line between headers and content
    out.println();
    out.flush();
  }

  /**
   * Processing a server request by reading client requested file and send to the server the
   * response.
   *
   * @param in      client request
   * @param out     response to the server
   * @param dataOut file to be send to the server
   * @throws IOException when IO errors
   */
  private void processRequest(BufferedReader in, PrintWriter out, BufferedOutputStream dataOut)
      throws IOException {
    //get the first line of the client's request
    StringTokenizer clientRequest = new StringTokenizer(in.readLine());
    String httpMethod = clientRequest.nextToken().toUpperCase();
    String fileRequested = clientRequest.nextToken();

    if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
      File file = new File(WEB_ROOT, pathLocalRequestedFile(fileRequested));
      int fileLength = (int) file.length();
      String contentType = getContentType(pathLocalRequestedFile(fileRequested));
      String code = "HTTP/1.1 200 OK";
      if (httpMethod.equals("GET")) {
        try {
          dataOut.write(new FileInputStream(file).readAllBytes(), 0, fileLength);
        } catch (FileNotFoundException e) {
          System.err.println("File not found");
          contentType = "text/html";
          fileLength = NOT_FOUND_SIZE;
          code = "HTTP/1.0 404 Not Found";
          dataOut.write(NOT_FOUND_FILE.getBytes(StandardCharsets.UTF_8), 0, fileLength);
        }
        sendHttpHeader(out, contentType, fileLength, code);
        dataOut.flush();
      }
    } else {
      Printer.methodNotSupported();
    }
  }

  @Override
  public void run() {
    try (
        PrintWriter out = new PrintWriter(connect.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        BufferedOutputStream dataOut = new BufferedOutputStream(connect.getOutputStream())
    ) {
      processRequest(in, out, dataOut);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Printer.connectionClosed();
    }
  }
}
