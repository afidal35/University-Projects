package gla.folders.templatefolder;

import gla.folders.grouper.WatcherGrouper;
import gla.observer.Observer;

/**
 * Template watcher grouper use to watch template grouper folders.
 */
class TemplateWatcherGrouper extends WatcherGrouper {
  private final TemplateFolderGrouper templateFolderGrouper;

  public TemplateWatcherGrouper(
      TemplateFolderGrouper templateFolderGrouper) {
    super(templateFolderGrouper.getGrouper());
    this.templateFolderGrouper = templateFolderGrouper;
  }

  @Override
  public Observer getObserverDefault(String fileName) {
    TemplateFolder folder = (TemplateFolder) templateFolderGrouper.getGrouper().getDefaultFolder();
    return folder.getTemplateFileWatchable(fileName);
  }

  @Override
  public Observer getObserverTheme(String fileName) {
    TemplateFolder folder = (TemplateFolder) templateFolderGrouper.getGrouper().getThemeFolder();
    return folder.getTemplateFileWatchable(fileName);
  }
}
