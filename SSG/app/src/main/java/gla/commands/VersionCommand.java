package gla.commands;

import gla.utils.VersionPropertiesReader;
import picocli.CommandLine.Command;

/**
 * Ssg version command class.
 */
@Command(
    name = "version",
    description = "Show current app version"
)
public class VersionCommand implements Runnable {

  @Override
  public void run() {
    try {
      VersionPropertiesReader versionReader = new VersionPropertiesReader();
      String version = versionReader.getVersion();
      System.out.println("ssg version " + version);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}

