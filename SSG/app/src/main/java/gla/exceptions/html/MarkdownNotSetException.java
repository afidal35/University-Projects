package gla.exceptions.html;

import gla.files.html.HtmlFile;

/**
 * Cannot dump a markdown with different name.
 */
public class MarkdownNotSetException extends HtmlException {
  public MarkdownNotSetException(HtmlFile htmlFile) {
    super("Cannot Dump Html file: " + htmlFile.getFileName() + " markdown is not set.");
  }
}
