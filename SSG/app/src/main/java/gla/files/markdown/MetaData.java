package gla.files.markdown;


import gla.files.toml.TomlObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class Help to get metaData of Markdown file.
 */
public class MetaData {

  private static final Pattern PATTERN = Pattern.compile(
      "^(\\+\\+\\+\\n(.*\\n)?\\+\\+\\+\\n?)", Pattern.DOTALL);

  private final TomlObject tomlObject;
  private final int endMetaData;

  /**
   * Constructor of MetaData for a Markdown file.
   *
   * @param contentMarkdown markdown content to parse
   */
  public MetaData(String contentMarkdown) {
    Matcher matcher = PATTERN.matcher(contentMarkdown);
    String content = "";
    if (matcher.find()) {
      if (matcher.group(2) != null) {
        content = matcher.group(2);
      }
      endMetaData = matcher.end();
    } else {
      endMetaData = 0;
    }
    tomlObject = new TomlObject(content);
  }

  /**
   * Get toml object to used.
   *
   * @return toml object
   */
  public TomlObject toTomlObject() {
    return tomlObject;
  }

  /**
   * getter position of MetaData end.
   *
   * @return int
   */
  public int end() {
    return endMetaData;
  }

  @Override
  public String toString() {
    return "MetaData{"
        + "endMetaData=" + endMetaData
        + ", toml=" + tomlObject
        + '}';
  }

}
