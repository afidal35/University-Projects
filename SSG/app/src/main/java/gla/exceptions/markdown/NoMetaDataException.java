package gla.exceptions.markdown;

/**
 * Exception if no MetaData in file Markdown.
 */
public class NoMetaDataException extends Exception {

  public NoMetaDataException() {
    super("No metaData found");
  }

}
