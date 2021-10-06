package gla.exceptions.files;

import gla.folders.ThemeFolder;
import java.io.FileNotFoundException;

/**
 * Throw if theme folder set not exist.
 */
public class ThemeFolderNotExistException extends FileNotFoundException {
  public ThemeFolderNotExistException(ThemeFolder themeFolder) {
    super("Theme folder: " + themeFolder.toPath() + " not exist");
  }
}
