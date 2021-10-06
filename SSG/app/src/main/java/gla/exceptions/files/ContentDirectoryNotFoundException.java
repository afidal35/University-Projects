package gla.exceptions.files;

import gla.folders.contentfolder.ContentFolder;
import java.io.FileNotFoundException;

/**
 * Throw if content directory not exist.
 */
public class ContentDirectoryNotFoundException extends FileNotFoundException {

  public ContentDirectoryNotFoundException(ContentFolder content) {
    super("Content directory: " + content.toPath().toString() + " not found");
  }
}
