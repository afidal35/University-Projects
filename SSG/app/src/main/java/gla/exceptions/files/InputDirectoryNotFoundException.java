package gla.exceptions.files;

import gla.folders.InputFolder;
import java.io.FileNotFoundException;

/**
 * Trow if input directory not exist.
 */
public class InputDirectoryNotFoundException extends FileNotFoundException {

  public InputDirectoryNotFoundException(InputFolder input) {
    super("Input directory: " + input.toPath().toString() + " not found.");
  }

}
