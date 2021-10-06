package gla.folders.templatefolder;


import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.template.TemplateFile;
import gla.folders.HtmlFolder;
import gla.observer.FileWatchable;
import gla.observer.Watcher;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Represent Folder Template in InputFolder.
 */
public class TemplateFolder extends HtmlFolder {

  public static final String NAME_DIR = "templates";

  /**
   * Default constructor used to group folder.
   */
  protected TemplateFolder() {
    super(null);
  }

  /**
   * Folder constructor. Warn: add "template to path"
   *
   * @param path Path of folder
   */
  public TemplateFolder(Path path) {
    super(path.resolve(NAME_DIR));
  }

  /**
   * Create a Template File.
   *
   * @param fileName name of file.
   * @return FileInfo
   */
  @Override
  public FileInfo getFileInfo(String fileName) throws WrongFileExtensionException {
    try {
      return getTemplate(fileName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * True if contain the template.
   *
   * @param fileName name template search
   * @return boolean
   */
  public boolean containTemplate(String fileName) {
    return contains(fileName);
  }

  /**
   * Template getter. (not parse)
   *
   * @param fileName name of template file
   * @return Template object
   * @throws IOException if error to load template
   * @throws WrongFileExtensionException wrong template file extension
   */
  public TemplateFile getTemplate(String fileName) throws IOException, WrongFileExtensionException {
    return new TemplateFile(this, fileName).parse();
  }

  /**
   * Template getter. Use memoization
   *
   * @param fileName name of template file
   * @return Template object
   * @throws WrongFileExtensionException wrong template file extension
   */
  public TemplateFile getTemplateMemo(String fileName)
      throws WrongFileExtensionException {
    return (TemplateFile) getFileInfoMemo(fileName);
  }

  /**
   * Get template file watchable.
   *
   * @param fileName name of file
   * @return File watchable
   */
  public FileWatchable getTemplateFileWatchable(String fileName) {
    if (!contains(fileName) && !map.containsKey(fileName)) {
      return null;
    }
    try {
      return getTemplateMemo(fileName).getWatchable();
    } catch (WrongFileExtensionException | IOException e) {
      System.err.println("ERROR get template: " + e);
      return null;
    }
  }

  /**
   * Get watching service.
   *
   * @return Watching service.
   */
  public Watcher getWatcher() {
    return new TemplateWatcher(this);
  }

}
