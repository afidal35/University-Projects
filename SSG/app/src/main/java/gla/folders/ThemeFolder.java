package gla.folders;

import gla.exceptions.files.ThemeFolderNotExistException;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.nio.file.Path;

/**
 * Object represent Theme folder.
 */
public class ThemeFolder extends Folder {

  public static final String DEFAULT_FOLDER_NAME = "themes";
  private final TemplateFolder templateFolder;
  private final StaticFolder staticFolder;

  /**
   * Constructor of theme folder.
   *
   * @param path      Path of input directory
   * @param themeName name of theme in directory to select
   */
  public ThemeFolder(Path path, String themeName) {
    super(path.resolve(DEFAULT_FOLDER_NAME).resolve(themeName));
    staticFolder = new StaticFolder(toPath());
    templateFolder = new TemplateFolder(toPath());
  }

  /**
   * Constructor of theme folder.
   *
   * @param inputFolder input folder used
   * @param themeName   name of theme in directory to select
   */
  public ThemeFolder(InputFolder inputFolder, String themeName) {
    this(inputFolder.toPath(), themeName);
  }


  /**
   * Getter static folder.
   *
   * @return Static folder
   */
  public StaticFolder getStaticFolder() {
    return staticFolder;
  }

  /**
   * Getter template folder.
   *
   * @return template folder
   */
  public TemplateFolder getTemplateFolder() {
    return templateFolder;
  }

  @Override
  public void throwIfNotExist() throws ThemeFolderNotExistException {
    if (!exist()) {
      throw new ThemeFolderNotExistException(this);
    }
  }
}
