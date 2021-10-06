package gla.files.toml;

import gla.folders.OutputFolder;
import gla.observer.Observer;
import gla.observer.Watcher;
import java.nio.file.Path;

class SiteTomlWatcher extends Watcher {
  private final SiteTomlObserver siteTomlObserver;
  private final SiteToml siteToml;

  SiteTomlWatcher(SiteToml siteToml, OutputFolder outputFolder) {
    super(siteToml.getInputFolder());
    this.siteToml = siteToml;
    siteTomlObserver = siteToml.getObserver(outputFolder);
  }

  @Override
  public Path toPath() {
    return siteToml.toPath();
  }

  @Override
  public Observer getObserver(String fileName) {
    if (!fileName.equals(SiteToml.FILE_NAME)) {
      return null;
    }
    return siteTomlObserver;
  }
}
