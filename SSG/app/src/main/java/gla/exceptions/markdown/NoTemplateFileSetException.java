package gla.exceptions.markdown;

/**
 * Exception if no template file is set in Markdown object.
 */
public class NoTemplateFileSetException extends Exception {
  public NoTemplateFileSetException() {
    super("No template file is set.");
  }
}
