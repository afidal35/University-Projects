package gla.files.template;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.observer.FileWatchable;
import gla.utils.Printer;
import java.io.IOException;
import java.util.function.Consumer;


/**
 * Template file watchable.
 */
public class TemplateWatchable extends FileWatchable {
  private final TemplateFile templateFile;

  /**
   * Constructor.
   * Warning: The constructor not check if file exist.
   *
   * @param templateFile template file to used.
   */
  public TemplateWatchable(TemplateFile templateFile) {
    super(templateFile);
    this.templateFile = templateFile;
    applyDep(watch -> watch.addObserver(this));
  }


  @Override
  public void onCreated() {
    Printer.detectedCreationOn(getFileInfo());
    onModified();
  }


  @Override
  public void onModified() {
    Printer.detectedChangeOn(getFileInfo());
    try {
      applyDep(watch -> watch.removeObserver(this));
      templateFile.parse(true);
      applyDep(watch -> watch.addObserver(this));

    } catch (WrongFileExtensionException e) {
      System.err.println(e); //TODO add error message include error
    } catch (TemplateNotFoundException e) {
      System.err.println(e); // TODO add error template file include not found.
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO printer ??
    }
    notifyModified();
  }

  @Override
  public void onDeleted() {
    Printer.detectedDeleteOn(getFileInfo());
    getFileInfo().updateLastFileTimeNow();
    notifyModified();
  }

  private void applyDep(Consumer<FileWatchable> func) {
    templateFile.getDependencies().stream()
        .map(dep -> {
          try {
            return dep.getWatchable();
          } catch (IOException | WrongFileExtensionException e) {
            throw new RuntimeException(e);
          }
        })
        .forEach(func);
  }
}
