package gla.files.staticfile;

import gla.folders.OutputFolder;
import gla.observer.FileObserver;
import gla.utils.Printer;
import java.io.IOException;

/**
 * Static file in output folder.
 */
public class StaticFileObserver extends FileObserver {
  /**
   * Constructor Static file in input.
   *
   * @param outputFolder    output folder of file.
   * @param staticFileInput static file to copy
   */
  public StaticFileObserver(OutputFolder outputFolder, StaticFile staticFileInput) {
    super(new StaticFileOutput(outputFolder, staticFileInput));
  }

  private StaticFileOutput getStaticFile() {
    return (StaticFileOutput) getFileInfo();
  }

  private void create() {
    try {
      StaticFile staticFile = getStaticFile();
      staticFile.getStaticFolder().create();
      staticFile.create();
    } catch (IOException e) {
      Printer.errorOnCreate(getFileInfo());
    }
  }

  @Override
  public void onCreated() {
    create();
    Printer.staticFileProcess(false, getStaticFile());
  }

  @Override
  public void onModified() {
    create();
    Printer.staticFileProcess(true, getStaticFile());
  }

  @Override
  public void onDeleted() {
    try {
      getFileInfo().delete();
      Printer.fileHasBeenDeleted(getStaticFile());
    } catch (IOException e) {
      Printer.errorOnDelete(getStaticFile());
      //throw new RuntimeException(e);
    }
  }
}
