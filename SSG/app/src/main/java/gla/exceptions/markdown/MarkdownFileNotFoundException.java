package gla.exceptions.markdown;

import gla.files.markdown.MarkdownFile;
import java.io.FileNotFoundException;

/**
 * Exception raised when the index.md file is missing while building site.
 */
public class MarkdownFileNotFoundException extends FileNotFoundException {

  public MarkdownFileNotFoundException(MarkdownFile markdownFile) {
    super("Corresponding markdown file for [" + markdownFile.toPath() + "] does not exists.");
  }
}