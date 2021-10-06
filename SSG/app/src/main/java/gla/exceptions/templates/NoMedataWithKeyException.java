package gla.exceptions.templates;

/**
 * Exception if key in metadata cannot be found.
 */
public class NoMedataWithKeyException extends TemplateException {

  public NoMedataWithKeyException(String key) {
    super("Connot found MetaData key \"" + key + "\".");
  }
}
