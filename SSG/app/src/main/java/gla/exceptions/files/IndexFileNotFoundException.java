package gla.exceptions.files;

import gla.folders.contentfolder.ContentFolder;

/**
 * Exception raised when the index.md file is missing while building site.
 */
public class IndexFileNotFoundException extends RuntimeException {

  public IndexFileNotFoundException(ContentFolder content) {
    super("Cannot build, necessary file [index.md] not found inside ["
        + content.toPath().toString() + "].");
  }
}