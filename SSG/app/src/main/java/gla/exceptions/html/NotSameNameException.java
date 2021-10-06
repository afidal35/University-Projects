package gla.exceptions.html;

import gla.files.html.HtmlFile;
import gla.files.markdown.MarkdownFile;

/**
 * Error markdown file and html have different names.
 */
public class NotSameNameException extends HtmlException {

  public NotSameNameException(HtmlFile htmlFile, MarkdownFile markdownFile) {
    super("Cannot set a markdown " + markdownFile.getFileName() + " for file " + htmlFile);
  }
}
