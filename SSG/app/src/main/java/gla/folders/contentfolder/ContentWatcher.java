package gla.folders.contentfolder;

import gla.folders.OutputFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.Observer;
import gla.observer.Watcher;

/**
 * Content watcher used to watch a content file.
 */
class ContentWatcher extends Watcher {
  private final ContentFolder contentFolder;
  private final TemplateFolder templateFolder;
  private final OutputFolder outputFolder;

  public ContentWatcher(ContentFolder contentFolder,
                        TemplateFolder templateFolder,
                        OutputFolder outputFolder) {
    super(contentFolder);
    this.contentFolder = contentFolder;
    this.templateFolder = templateFolder;
    this.outputFolder = outputFolder;
  }

  @Override
  public Observer getObserver(String fileName) {
    return contentFolder.getMarkdownWatchable(fileName, templateFolder, outputFolder);
  }
}
