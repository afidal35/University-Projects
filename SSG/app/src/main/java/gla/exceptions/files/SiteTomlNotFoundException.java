package gla.exceptions.files;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Exception raised when the site.toml file is missing while building site.
 */
public class SiteTomlNotFoundException extends FileNotFoundException {

  public SiteTomlNotFoundException(Path path) {
    super("Cannot build, necessary file [site.toml] not found inside [" + path.toString() + "].");
  }
}