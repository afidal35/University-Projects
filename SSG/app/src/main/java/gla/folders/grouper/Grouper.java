package gla.folders.grouper;

import gla.files.FileInfo;
import gla.files.toml.SiteToml;
import gla.folders.FileInfoFolder;
import gla.folders.Folder;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Group folder from default and theme.
 */
public class Grouper extends Folder {

  private final SiteToml siteToml;
  private final FileInfoFolder defaultFolder;
  private FileInfoFolder themeFolder = null;

  /**
   * Folder constructor.
   *
   * @param siteToml site toml.
   * @param defaultFolder default folder used
   */
  public Grouper(SiteToml siteToml, FileInfoFolder defaultFolder) {
    super(null);
    this.siteToml = siteToml;
    this.defaultFolder = defaultFolder;
  }

  /**
   * True if theme folder != null.
   *
   * @return boolean
   */
  public boolean themeFolderIsSet() {
    return themeFolder != null;
  }

  /**
   * Getter default folder.
   *
   * @return default folder
   */
  public FileInfoFolder getDefaultFolder() {
    return defaultFolder;
  }

  /**
   * Getter theme folder.
   *
   * @return theme folder.
   */
  public FileInfoFolder getThemeFolder() {
    return themeFolder;
  }

  /**
   * Theme setter.
   *
   * @param themeFolder theme use to set template (can be null)
   */
  public void setThemeFolder(FileInfoFolder themeFolder) {
    this.themeFolder = themeFolder;
  }

  public FileInfoFolder getFolderToUsed(String fileName) {
    return defaultFolder.contains(fileName) || !templateFolderContain(fileName)
        ? defaultFolder : themeFolder;
  }

  @Override
  public Path toPath() {
    throw new RuntimeException("Cannot get path of InputStaticFolder, "
        + "please get static folder contain");
  }


  @Override
  public List<File> listFiles() {
    List<File> files = new ArrayList<>();
    if (themeFolderIsSet()) {
      files.addAll(
          themeFolder.listFiles().stream()
              .filter(file -> !defaultFolder.contains(file.getName()))
              .collect(Collectors.toList()));
    }
    if (defaultFolder.exist()) {
      files.addAll(defaultFolder.listFiles());
    }
    return files;
  }

  @Override
  public boolean contains(String fileName) {
    return defaultFolder.contains(fileName) || templateFolderContain(fileName);
  }

  @Override
  public boolean exist() {
    return defaultFolder.exist() || themeFolderIsSet() && themeFolder.exist();
  }

  /**
   * True is template folder is set and contain file.
   *
   * @param fileName filename search
   * @return boolean
   */
  public boolean templateFolderContain(String fileName) {
    return themeFolderIsSet() && themeFolder.contains(fileName);
  }


  /**
   * Get list of name file used in theme.
   *
   * @return List of file infos
   */
  public List<String> listFileNameThemeFiltered() {
    if (!themeFolderIsSet()) {
      return new ArrayList<>();
    }
    return themeFolder.listNameFiles().stream()
        .filter(name -> !defaultFolder.contains(name))
        .collect(Collectors.toList());
  }

  public void updateDateWithSiteToml(FileInfo fileInfo) {
    fileInfo.updateLastFileTime(siteToml);
  }
}
