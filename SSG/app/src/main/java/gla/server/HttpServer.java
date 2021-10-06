package gla.server;

import gla.folders.OutputFolder;
import gla.utils.Printer;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

/**
 * HTTPServer Class.
 */
public class HttpServer {

  public static String loopBackAddress = "http://127.0.0.1:";
  private final OutputFolder outputFolder;
  private final int port;

  public HttpServer(int port, Path path) {
    this.outputFolder = new OutputFolder(path);
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  /**
   * Launch Server HTTP in a specific .
   */
  public void launch() {
    try {
      ServerSocket serverConnect = new ServerSocket(port);
      Printer.startingServer(port, loopBackAddress);
      while (true) {
        HttpHandler myServer = new HttpHandler(serverConnect.accept(), outputFolder.toPath());
        Printer.connectionOpened();
        Thread thread = new Thread(myServer);
        thread.start();
      }
    } catch (IOException e) {
      Printer.connectionError();
      System.err.println(e.getMessage());
    }
  }

}
