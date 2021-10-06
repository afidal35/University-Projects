package gla.folders.staticfolder;

import gla.folders.OutputFolder;
import gla.observer.Observer;
import gla.observer.Watcher;

/**
 * Static Folder watcher.
 */
class StaticWatcher extends Watcher {
  private final StaticFolder staticFolder;
  private final OutputFolder outputFolder;

  public StaticWatcher(StaticFolder staticFolder, OutputFolder outputFolder) {
    super(staticFolder.toPath());
    this.staticFolder = staticFolder;
    this.outputFolder = outputFolder;
  }

  @Override
  public Observer getObserver(String fileName) {
    return staticFolder.getStaticFileWatchable(fileName, outputFolder);
  }
}
