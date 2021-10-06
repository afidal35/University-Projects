package gla.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HTTPServerTest {

  @TempDir
  File tempDir;

  @Test
  public void testPort() {
    HttpServer httpServer = new HttpServer(Integer.parseInt("8080"),tempDir.toPath());

    assertEquals(8080,httpServer.getPort());
    assertNotEquals(0,httpServer.getPort());
  }

}
