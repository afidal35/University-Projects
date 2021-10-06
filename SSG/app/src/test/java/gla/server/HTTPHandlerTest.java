package gla.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HTTPHandlerTest {

  @TempDir
  File tempDir;
  Path output;

  @BeforeEach
  public void before() throws IOException {
    output = Files.createFile(tempDir.toPath().resolve("output"));
  }

  public String readHeader(Path output) throws IOException {
    StringBuilder sb = new StringBuilder();

    BufferedReader br = new BufferedReader(new FileReader(output.toString()));
    String line;
    while (!(line = br.readLine()).equals("")) {
      sb.append(line).append("\n");
    }

    br.close();
    return sb.toString();
  }

  public String readBody(Path output) throws IOException {
    StringBuilder sb = new StringBuilder();

    BufferedReader br = new BufferedReader(new FileReader(output.toString()));
    String line;
    while (!(line = br.readLine()).equals("")) {
      //Do nothing
    }

    while ((line = br.readLine()) != null) {
      sb.append(line).append("\n");
    }

    sb.deleteCharAt(sb.length()-1);

    br.close();
    return sb.toString();
  }

  @Test
  public void testContentTypeHtml() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("GET /index.html HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    String contenType = readHeader(output).split("Content-type: ")[1].split("\n")[0];
    assertEquals("text/html", contenType);
  }

  @Test
  public void testContentTypePlainText() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("GET /CommonMark.md HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    String contenType = readHeader(output).split("Content-type: ")[1].split("\n")[0];
    assertEquals("text/plain", contenType);
  }

  @Test
  public void testFileNotFound() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("GET /index HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    String notFound = HttpHandler.NOT_FOUND_FILE;

    String contenType = readHeader(output).split("Content-type: ")[1].split("\n")[0];
    String codeHeaders = readHeader(output).split("\n")[0];

    assertEquals("HTTP/1.0 404 Not Found", codeHeaders);
    assertEquals("text/html", contenType);
    assertEquals(notFound, readBody(output));
  }

  @Test
  public void testFileRoot() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("GET / HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    String index =
        "<!doctype html>\n"
            + "<html>\n"
            + " <head></head>\n"
            + " <body>\n"
            + "  <h1>index</h1>\n"
            + "  <p>this is the home page of the minimal site.</p>\n"
            + "  <p>your static site generator should support [[markdownmarkup]].</p>\n"
            + " </body>\n"
            + "</html>";

    String contenType = readHeader(output).split("Content-type: ")[1].split("\n")[0];
    String codeHeaders = readHeader(output).split("\n")[0];

    assertEquals("HTTP/1.1 200 OK", codeHeaders);
    assertEquals("text/html", contenType);
    assertEquals(index, readBody(output));
  }

  @Test
  public void testFile() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("GET /CommonMark.html HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    String index =
        "<!doctype html>\n"
            + "<html>\n"
            + " <head></head>\n"
            + " <body>\n"
            + "  <h1>common mark</h1>\n"
            + "  <p>common mark is the basic markup format adopted for the project.</p>\n"
            + "  <p>you should check that your implementation supports:</p>\n"
            + "  <ul>\n"
            + "   <li><p><em>italics</em> (using underscores) and <em>italics</em> (using stars),</p></li>\n"
            + "   <li><p><strong>bold</strong> (using double stars),</p></li>\n"
            + "   <li><p>bulleted lists,</p></li>\n"
            + "   <li><p>etc.</p></li>\n"
            + "  </ul>\n"
            + "  <p>the commonmark spec can be found <a href=\"http://spec.commonmark.org\">online</a>.</p>\n"
            + " </body>\n"
            + "</html>";

    String contenType = readHeader(output).split("Content-type: ")[1].split("\n")[0];
    String codeHeaders = readHeader(output).split("\n")[0];

    assertEquals("HTTP/1.1 200 OK", codeHeaders);
    assertEquals("text/html", contenType);
    assertEquals(index, readBody(output));
  }

  @Test
  public void testPostMethod() throws IOException, InterruptedException {
    MockSocket mockSocket = new MockSocket("POST / HTTP/1.1", output.toFile());
    HttpHandler myServer =
        new HttpHandler(mockSocket, Path.of("/src/test/resources/server/minimal/_output"));
    Thread thread = new Thread(myServer);
    thread.start();
    thread.join();

    assertEquals(0, output.toFile().length());
  }


}
