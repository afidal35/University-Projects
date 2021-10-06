package gla.folders.templatefolder;


import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.template.TemplateFile;
import gla.files.toml.SiteToml;
import gla.folders.ThemeFolder;
import gla.folders.grouper.Grouper;
import gla.folders.grouper.WatcherGrouper;
import gla.observer.Watcher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Link input template file with theme templates.
 */
public class TemplateFolderGrouper extends TemplateFolder {
  private final Grouper grouper;
  private WatcherGrouper watcher = null;

  /**
   * Constructor with only default templates.
   *
   * @param siteToml site toml used to create grouper
   */
  public TemplateFolderGrouper(SiteToml siteToml) {
    this.grouper = new Grouper(siteToml, siteToml.getInputFolder().getTemplateFolder());
    setTheme(siteToml.getThemeFolder());
  }

  /**
   * Theme setter.
   *
   * @param themeFolder theme use to set template (can be null)
   * @return TemplateFolderGrouper
   */
  public TemplateFolderGrouper setTheme(ThemeFolder themeFolder) {
    TemplateFolder templateFolder = (themeFolder != null) ? themeFolder.getTemplateFolder() : null;
    grouper.setThemeFolder(templateFolder);
    if (watcher != null) {
      watcher.setThemeWatcher(templateFolder);
    }
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
  public List<TemplateFile> listTemplateFileTheme() {
    return grouper.listFileNameThemeFiltered().stream()
        .map(name -> {
          try {
            return getTemplateMemo(name);
          } catch (WrongFileExtensionException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
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
  public Watcher getWatcher() {
    if (watcher == null) {
      watcher = new TemplateWatcherGrouper(this);
    }
    return watcher;
  }

  @Override
  public TemplateFile getTemplate(String fileName)
      throws IOException, WrongFileExtensionException {
    TemplateFolder folderToUse = (TemplateFolder) grouper.getFolderToUsed(fileName);
    Path pathFile = folderToUse.toPath().resolve(fileName);
    TemplateFile templateFile = new TemplateFile(this, pathFile).parse();
    grouper.updateDateWithSiteToml(templateFile);
    return templateFile;
  }

  @Override
  public synchronized FileInfo getFileInfoMemo(String fileName)
      throws WrongFileExtensionException {
    try {
      TemplateFolder folderToUse = (TemplateFolder) grouper.getFolderToUsed(fileName);
      return folderToUse.getFileInfoMemo(fileName, getTemplate(fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
