package gla.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Helper class for files and directories manipulations.
 */
public class FileHelpers {

  /**
   * Get a file extension.
   *
   * @param name the file name
   * @return String representing the file extension if it exists, empty string otherwise
   */
  public static String getFileExtension(String name) {
    int dotIndex = name.lastIndexOf('.');
    return (dotIndex == -1 || dotIndex == 0) ? "" : name.substring(dotIndex + 1);
  }

  /**
   * Change a string extension representing a file.
   *
   * @param name         the file name
   * @param newExtension the new extension
   * @return String which has the new extension
   */
  public static String substituteFileExtension(String name, String newExtension) {
    int dotIndex = name.lastIndexOf('.');
    return (dotIndex == -1 || dotIndex == 0) ? ""
        : name.substring(0, dotIndex + 1) + newExtension;
  }

  /**
   * Get the grandParent of a path.
   *
   * @param path the path
   * @return Path, grandParent path
   */
  public static Path getGrandParent(Path path) {
    Path parent = path.getParent();
    return parent.getParent();
  }

}