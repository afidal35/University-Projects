package gla.exceptions.templates;

import gla.files.template.TemplateFile;
import java.io.FileNotFoundException;

/**
 * Throw if template is not found.
 */
public class TemplateNotFoundException extends FileNotFoundException {
  public TemplateNotFoundException(TemplateFile templateFile) {
    super("Template file " + templateFile.getFileName() + " not found.");
  }
}
