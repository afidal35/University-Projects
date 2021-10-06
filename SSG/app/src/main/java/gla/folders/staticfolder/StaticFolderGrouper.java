package gla.folders.staticfolder;

import gla.files.FileInfo;
import gla.files.staticfile.StaticFile;
import gla.files.toml.SiteToml;
import gla.folders.OutputFolder;
import gla.folders.ThemeFolder;
import gla.folders.grouper.Grouper;
import gla.folders.grouper.WatcherGrouper;
import gla.observer.Watcher;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Static folder default and static folder theme.
 */
public class StaticFolderGrouper extends StaticFolder {
  private final Grouper grouper;
  Map<OutputFolder, WatcherGrouper> watcherMap = new HashMap<>();

  /**
   * Constructor with only default static folder.
   *
   * @param siteToml site toml used to create grouper
   */
  public StaticFolderGrouper(SiteToml siteToml) {
    grouper = new Grouper(siteToml, siteToml.getInputFolder().getStaticFolder());
    setTheme(siteToml.getThemeFolder());
  }

  /**
   * Theme setter.
   *
   * @param themeFolder theme use to set template (can be null)
   * @return StaticFolderGrouper
   */
  public StaticFolderGrouper setTheme(ThemeFolder themeFolder) {
    StaticFolder staticThemeFolder = (themeFolder != null) ? themeFolder.getStaticFolder() : null;
    grouper.setThemeFolder(staticThemeFolder);
    watcherMap.values().forEach(wg -> wg.setThemeWatcher(staticThemeFolder));
    return this;
  }

  /**
   * getter grouper used.
   *
   * @return grouper
   */
  public Grouper getGrouper() {
    return grouper;
  }

  /**
   * List static file use in theme.
   *
   * @return list of static file.
   */
  public List<StaticFile> listStaticFileTheme() {
    return grouper.listFileNameThemeFiltered().stream()
        .map(this::getStaticFileMemo)
        .collect(Collectors.toList());
  }

  /**
   * True if theme folder != null.
   *
   * @return boolean
   */
  public boolean themeIsSet() {
    return grouper.themeFolderIsSet();
  }


  @Override
  public Path toPath() {
    return grouper.toPath();
  }

  @Override
  public List<File> listFiles() {
    return grouper.listFiles();
  }

  @Override
  public boolean contains(String fileName) {
    return grouper.contains(fileName);
  }

  @Override
  public boolean exist() {
    return grouper.exist();
  }

  @Override
  public Watcher getWatcher(OutputFolder outputFolder) {
    if (!watcherMap.containsKey(outputFolder)) {
      watcherMap.put(outputFolder, new StaticWatcherGrouper(this, outputFolder));
    }
    return watcherMap.get(outputFolder);
  }

  @Override
  public StaticFile getStaticFile(String fileName) {
    StaticFolder staticFolder = (StaticFolder) grouper.getFolderToUsed(fileName);
    StaticFile staticFile = staticFolder.getStaticFileMemo(fileName);
    grouper.updateDateWithSiteToml(staticFile);
    return staticFile;
  }

  @Override
  public synchronized FileInfo getFileInfoMemo(String fileName) {
    StaticFolder staticFolderUse = (StaticFolder) grouper.getFolderToUsed(fileName);
    return staticFolderUse.getFileInfoMemo(fileName, getStaticFile(fileName));
  }
}
