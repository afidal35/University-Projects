package gla.exceptions.files;

/**
 * Exception class pointing a wrong file extension.
 */
public class WrongFileExtensionException extends Exception {

  /**
   * Constructor of exception with 2 args.
   *
   * @param actualExtension actual extension
   * @param expectedExtension expected extension
   */
  public WrongFileExtensionException(String actualExtension, String expectedExtension) {

    super("Wrong file extension [."
        + actualExtension + "], expected [."
        + expectedExtension + "].");
  }

  /**
   * Constructor of exception with one arg.
   *
   * @param expectedExtension Extension expected
   */
  public WrongFileExtensionException(String expectedExtension) {
    super("Expected ." + expectedExtension);
  }
}