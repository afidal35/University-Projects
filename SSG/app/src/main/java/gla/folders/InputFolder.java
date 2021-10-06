package gla.folders;

import gla.exceptions.files.InputDirectoryNotFoundException;
import gla.files.toml.SiteToml;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Represent Input folder.
 */
public class InputFolder extends Folder {
  public static final String DEFAULT_FOLDER_NAME = ".";
  private final ContentFolder contentFolder;
  private final TemplateFolder templateFolder;
  private final StaticFolder staticFolder;
  private final SiteToml siteToml;

  /**
   * Input folder constructor.
   *
   * @param path Path of folder
   * @throws IOException - input directory not exist
   *                     - content directory not exist
   *                     - index.md not exist in content
   *                     - site.toml file not exit
   *                     - theme folder invalid
   */
  public InputFolder(Path path) throws IOException {
    super(path);
    this.throwIfNotExist();

    contentFolder = new ContentFolder(path);
    contentFolder.throwIfNotExist();

    staticFolder = new StaticFolder(path);
    templateFolder = new TemplateFolder(path);

    contentFolder.throwIfIndexNotExist();
    siteToml = new SiteToml(this);
  }


  /**
   * Getter content folder.
   *
   * @return ContentFolder
   */
  public ContentFolder getContentFolder() {
    return contentFolder;
  }

  /**
   * Getter Template folder (use an TemplateFolderGrouper help to implement theme).
   *
   * @return Template folder interface
   */
  public TemplateFolder getTemplateFolder() {
    return templateFolder;
  }

  /**
   * Getter static folder. (use an StaticInput to help to implement theme).
   *
   * @return StaticFolder interface
   */
  public StaticFolder getStaticFolder() {
    return staticFolder;
  }

  /**
   * Getter theme folder.
   *
   * @param name name of theme.
   * @return theme folder used.
   */
  public ThemeFolder getTheme(String name) {
    return new ThemeFolder(this, name);
  }

  /**
   * Getter theme folder (use site.toml file).
   *
   * @return theme folder used.
   */
  public ThemeFolder getTheme() {
    return siteToml.getThemeFolder();
  }

  /**
   * Getter site toml.
   *
   * @return site toml file.
   */
  public SiteToml getSiteToml() {
    return siteToml;
  }

  /**
   * If directory not exist throw this exception.
   *
   * @throws FileNotFoundException throw if not exist
   */
  @Override
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exist()) {
      throw new InputDirectoryNotFoundException(this);
    }
  }
}
