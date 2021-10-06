package gla.commands;

import gla.server.HttpServer;
import java.nio.file.Path;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Ssg serve command class.
 */
@Command(
    name = "serve",
    description = "build project and launch it on a HTTP server for seeing the compilation's result"
)
public class SsgServeCommand extends SsgBuildCommand {

  @Option(names = {"--port"},
      description = "Deploy on specific port",
      defaultValue = "8080"
  )
  private String port;

  @Override
  public void run() {
    watch = true;
    super.run();
    try {
      super.join();
      HttpServer httpServer = new HttpServer(Integer.parseInt(port), Path.of(output));
      httpServer.launch();
    } catch (InterruptedException e) {
      System.err.println(e.getMessage());
    }
  }
}


