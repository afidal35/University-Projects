package gla.files.staticfile;

import gla.folders.OutputFolder;
import gla.observer.FileWatchable;
import gla.utils.Printer;
import java.io.FileNotFoundException;

/**
 * Static file watch who make copy in output file.
 */
public class StaticFileWatchable extends FileWatchable {
  /**
   * Constructor.
   *
   * @param staticFileInput static file to watchable
   * @param outputFolder    output folder to copy.
   * @throws FileNotFoundException static input file not found
   */
  public StaticFileWatchable(StaticFile staticFileInput, OutputFolder outputFolder)
      throws FileNotFoundException {
    super(staticFileInput, true);
    addObserver(new StaticFileObserver(outputFolder, staticFileInput));
  }

  @Override
  public void onCreated() {
    Printer.detectedCreationOn(getFileInfo());
    notifyCreated();
  }

  @Override
  public void onModified() {
    Printer.detectedChangeOn(getFileInfo());
    notifyModified();
  }

  @Override
  public void onDeleted() {
    Printer.detectedDeleteOn(getFileInfo());
    notifyDelete();
  }
}
