package gla.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.commons.io.IOUtils;

public class MockSocket extends Socket {

  private InputStream inputStream;
  private OutputStream outputStream;

  public MockSocket(String userInput, File file) throws IOException {
    super();
    inputStream = IOUtils.toInputStream(userInput,"UTF-8");
    outputStream = new FileOutputStream(file);
  }


  @Override
  public InputStream getInputStream() throws IOException {
    return inputStream;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return outputStream;
  }
}
