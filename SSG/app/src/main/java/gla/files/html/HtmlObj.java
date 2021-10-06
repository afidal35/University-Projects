package gla.files.html;

import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Html Object.
 */
public class HtmlObj {

  public static final String DOCTYPE = "<!doctype html>\n";
  private final String content;

  public HtmlObj(String content) {
    this.content = content;
  }

  /**
   * get raw html content.
   *
   * @return String.
   */
  public String getRawContent() {
    return content;
  }

  /**
   * getter Jsoup document.
   *
   * @return Document Jsoup
   */
  public Document toJsoup() {
    String normalisedContent = normaliseHtml(DOCTYPE + getRawContent());
    return Jsoup.parse(normalisedContent);
  }

  /**
   * Html content beautify.
   *
   * @return html in string
   */
  @Override
  public String toString() {
    return toJsoup().toString();
  }

  /**
   * Content html normalized.
   *
   * @return normalized string
   */
  public String normalisedString() {
    return normaliseHtml(toString());
  }

  /**
   * Normalize an html string (remove newline char and white space).
   *
   * @param content content to normalised
   * @return normalize string
   */
  public static String normaliseHtml(String content) {
    return content
        .replaceAll("[\\s+]?\n+[\\s+]?", "") // remove newline chars
        .replaceAll("(>)(\\s+)(<)", "$1$3") // remove white space between tags
        .toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HtmlObj htmlObj = (HtmlObj) o;
    return normalisedString().equals(htmlObj.normalisedString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(normalisedString());
  }
}
