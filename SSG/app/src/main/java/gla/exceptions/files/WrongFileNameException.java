package gla.exceptions.files;

import java.nio.file.Path;

/**
 * Exception raised when a file has the wrong name.
 */
public class WrongFileNameException extends RuntimeException {

  public WrongFileNameException(Path found, String format) {
    super("Wrong file name [" + found.toString() + "], expected format : [" + format + "].");
  }
}