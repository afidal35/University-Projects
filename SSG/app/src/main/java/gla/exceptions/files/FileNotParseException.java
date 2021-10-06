package gla.exceptions.files;

import gla.files.FileInfo;

/**
 * If file is not parsed.
 */
public class FileNotParseException extends Exception {
  public FileNotParseException(FileInfo fileInfo) {
    super("Please parse File " + fileInfo.getFileName());
  }
}
