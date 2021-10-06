package gla.folders.staticfolder;

import gla.folders.OutputFolder;
import gla.folders.grouper.WatcherGrouper;
import gla.observer.Observer;

/**
 * Class static watcher grouper, use to watch a static grouper.
 */
class StaticWatcherGrouper extends WatcherGrouper {

  private final StaticFolderGrouper staticFolderGrouper;
  private final OutputFolder outputFolder;

  public StaticWatcherGrouper(StaticFolderGrouper staticFolderGrouper,
                              OutputFolder outputFolder) {
    super(staticFolderGrouper.getGrouper());
    this.staticFolderGrouper = staticFolderGrouper;
    this.outputFolder = outputFolder;
  }

  @Override
  public Observer getObserverDefault(String fileName) {
    StaticFolder staticFolder = (StaticFolder) staticFolderGrouper.getGrouper().getDefaultFolder();
    return staticFolder.getStaticFileWatchable(fileName, outputFolder);
  }

  @Override
  public Observer getObserverTheme(String fileName) {
    StaticFolder staticFolder = (StaticFolder) staticFolderGrouper.getGrouper().getThemeFolder();
    return staticFolder.getStaticFileWatchable(fileName, outputFolder);
  }
}
