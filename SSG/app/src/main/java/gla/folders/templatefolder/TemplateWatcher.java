package gla.folders.templatefolder;

import gla.observer.Observer;
import gla.observer.Watcher;

/**
 * Tempalate watcher use to watch template folder.
 */
class TemplateWatcher extends Watcher {
  private final TemplateFolder templateFolder;

  public TemplateWatcher(TemplateFolder templateFolder) {
    super(templateFolder.toPath());
    this.templateFolder = templateFolder;
  }

  @Override
  public Observer getObserver(String fileName) {
    return templateFolder.getTemplateFileWatchable(fileName);
  }
}
