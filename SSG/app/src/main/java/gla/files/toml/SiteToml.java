package gla.files.toml;

import gla.exceptions.files.SiteTomlNotFoundException;
import gla.files.FileInfo;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.ThemeFolder;
import gla.folders.staticfolder.StaticFolderGrouper;
import gla.folders.templatefolder.TemplateFolderGrouper;
import gla.observer.Watcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Object represent Site Toml file.
 */
public class SiteToml extends FileInfo {
  public static final String EXTENSION = "toml";
  public static final String FILE_NAME = "site.toml";
  private static final String GENERAL_THEME = "general.theme";

  private final Map<OutputFolder, SiteTomlObserver> watchableMap = new HashMap<>();
  private final InputFolder inputFolder;
  private final StaticFolderGrouper staticFolder;
  private final TemplateFolderGrouper templateFolder;

  private TomlObject tomlObject;
  private ThemeFolder themeFolder = null;


  /**
   * Site Toml file.
   *
   * @param inputFolder input folder
   * @throws IOException if error to load site.toml
   */
  public SiteToml(InputFolder inputFolder) throws IOException {
    super(inputFolder.toPath().resolve(FILE_NAME));
    throwIfNotExist();
    this.inputFolder = inputFolder;
    updateTable();
    this.staticFolder = new StaticFolderGrouper(this);
    this.templateFolder = new TemplateFolderGrouper(this);
  }

  /**
   * Getter Toml object.
   *
   * @return Toml object parse.
   */
  public TomlObject toTomlObject() {
    return tomlObject;
  }

  /**
   * Return key in table.
   *
   * @return String theme (null if not exist)
   */
  public String getThemeName() {
    return tomlObject.get(GENERAL_THEME, null);
  }

  /**
   * True is a theme is set.
   *
   * @return boolean
   */
  public boolean hasTheme() {
    return getThemeName() != null;
  }

  /**
   * Getter theme folder.
   *
   * @return Theme folder
   */
  public ThemeFolder getThemeFolder() {
    return themeFolder;
  }

  /**
   * Getter Static folder grouper.
   *
   * @return static folder
   */
  public StaticFolderGrouper getStaticFolder() {
    return staticFolder;
  }

  /**
   * Getter template folder grouper.
   *
   * @return template folder
   */
  public TemplateFolderGrouper getTemplateFolder() {
    return templateFolder;
  }


  /**
   * Update table.
   *
   * @throws IOException error IO to reload table.
   */
  public void updateTable() throws IOException {
    tomlObject = new TomlObject(toPath());
    themeFolder = hasTheme() ? inputFolder.getTheme(getThemeName()) : null;
    if (themeFolder != null) {
      themeFolder.throwIfNotExist();
    }
  }

  /**
   * Update theme.
   */
  public void updateTheme() {
    staticFolder.setTheme(themeFolder);
    templateFolder.setTheme(themeFolder);
  }


  /**
   * Update date of theme static and template file used.
   */
  public void updateUpdateTime() {
    Consumer<FileInfo> dateUpdater = file -> file.updateLastFileTime(this);
    getStaticFolder().listStaticFileTheme().forEach(dateUpdater);
    getTemplateFolder().listTemplateFileTheme().forEach(dateUpdater);
  }


  /**
   * Update table and theme.
   *
   * @throws IOException error IO to reload table.
   */
  public void update() throws IOException {
    updateTable();
    updateTheme();
  }

  /**
   * Getter input folder.
   *
   * @return Inputfolder used
   */
  public InputFolder getInputFolder() {
    return inputFolder;
  }

  /**
   * Get fileWatchable.
   *
   * @param outputFolder output folder to update
   * @return Toml site watcher
   */
  public SiteTomlObserver getObserver(OutputFolder outputFolder) {
    try {
      if (!watchableMap.containsKey(outputFolder)) {
        watchableMap.put(outputFolder, new SiteTomlObserver(this, outputFolder));
      }
      return watchableMap.get(outputFolder);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get watcher.
   *
   * @param outputFolder output folder to update
   * @return Toml site watcher
   */
  public Watcher getWatcher(OutputFolder outputFolder) {
    return new SiteTomlWatcher(this, outputFolder);
  }


  @Override
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exists()) {
      throw new SiteTomlNotFoundException(toPath());
    }
  }


  @Override
  public void create() throws IOException {
    throw new RuntimeException("Cannot create site.toml file.");
  }

  @Override
  public void delete() throws IOException {
    throw new RuntimeException("Cannot delete site.toml file");
  }

  @Override
  public String toString() {
    return "SiteToml{"
        + "path=" + toPath()
        + ", table=" + toTomlObject()
        + '}';
  }

}
