package gla.exceptions.html;

import gla.files.html.HtmlFile;

/**
 * Error dump HtmlObject without set html object.
 */
public class HtmlObjectNotSet extends HtmlException {

  public HtmlObjectNotSet(HtmlFile htmlFile) {
    super("Cannot dump html file: " + htmlFile.getFileName() + "without set html object");
  }
}
