package gla.files.template;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.files.FileInfo;
import gla.files.html.HtmlFile;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.FileWatchable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Template class Help to use template with Markdown object.
 * Observer: Markdown
 * ObservableFile: TemplateFile
 */
public class TemplateFile extends FileInfo {

  public static final String DEFAULT_FILE_NAME = "default.html";
  private final TemplateFolder templateFolder;
  private TemplateObj templateObj = null;
  private TemplateWatchable templateWatcher = null;
  private ContentFolder contentFolder = null;



  /**
   * Constructor with pathFile.
   *
   * @param templateFolder template folder used
   * @param path of file
   * @throws WrongFileExtensionException if is not html extension
   */
  public TemplateFile(TemplateFolder templateFolder, Path path)
      throws WrongFileExtensionException {
    super(path, HtmlFile.EXTENSION);
    this.templateFolder = templateFolder;
  }

  /**
   * Constructor with pathFile.
   *
   * @param templateFolder template folder used
   * @param name           name of file
   * @throws WrongFileExtensionException if is not html extension
   */
  public TemplateFile(TemplateFolder templateFolder, String name)
      throws WrongFileExtensionException {
    super(templateFolder.toPath().resolve(name), HtmlFile.EXTENSION);
    this.templateFolder = templateFolder;
  }


  /**
   * Constructor with pathFile.
   *
   * @param templateFolder template folder used
   * @param name           name of file
   * @param parse          parse
   * @throws IOException               error on IO
   * @throws TemplateNotFoundException if path of template not exist
   * @throws WrongFileExtensionException ""
   */
  public TemplateFile(TemplateFolder templateFolder, String name, boolean parse)
      throws WrongFileExtensionException, IOException {
    this(templateFolder, name);
    if (parse) {
      this.parse();
    }
  }

  /**
   * Parse template object. if is not exit
   *
   * @return this to chain it
   * @throws IOException error to load template
   * @throws WrongFileExtensionException ""
   */
  public TemplateFile parse() throws IOException, WrongFileExtensionException {
    return parse(false);
  }

  /**
   * Parse template object. if is not exit.
   * And add to this to all template dependency list observer.
   *
   * @param force force to reparse file
   * @return this to chain it
   * @throws IOException error to load template
   * @throws WrongFileExtensionException ""
   */
  public TemplateFile parse(boolean force) throws IOException, WrongFileExtensionException {
    if (force || templateObj == null) {
      throwIfNotExist();
      templateObj = new TemplateObj(templateFolder, contentFolder, Files.readString(toPath()));
      if (templateObj.hasListFiles()) {
        updateLastFileTimeNow();
      } else {
        getDependencies().forEach(this::updateLastFileTime);
      }
    }
    return this;
  }

  /**
   * Set content folder to use with list file.
   *
   * @param contentFolder content folder
   * @return this object
   * @throws WrongFileExtensionException ""
   * @throws IOException ""
   */
  public TemplateFile setContentFolder(ContentFolder contentFolder)
      throws IOException, WrongFileExtensionException {
    boolean forceParse = contentFolder != null
        && (this.contentFolder == null || this.contentFolder.equals(contentFolder));

    this.contentFolder = contentFolder;
    parse(forceParse);
    return this;
  }

  /**
   * Get all dependency of template file.
   *
   * @return Set Template dependencies
   */
  public Set<TemplateFile> getDependencies() {
    return templateObj.getDependency();
  }

  /**
   * Getter template Folder.
   *
   * @return template folder
   */
  public TemplateFolder getTemplateFolder() {
    return templateFolder;
  }


  /**
   * Get template object.
   *
   * @return template object.
   */
  public TemplateObj toTemplateObj() {
    return templateObj;
  }

  /**
   * has list of files to replace.
   *
   * @return boolean
   */
  public boolean hasListFiles() {
    return toTemplateObj().hasListFiles();
  }

  /**
   * List of path replace with list files.
   *
   * @return list of name dir
   */
  public List<String> getListFile() {
    return toTemplateObj().getListFile();
  }

  /**
   * Get template watchable.
   *
   * @return FileWatchable
   * @throws WrongFileExtensionException ""
   * @throws IOException ""
   */
  public FileWatchable getWatchable() throws IOException, WrongFileExtensionException {
    if (templateWatcher == null) {
      templateWatcher = new TemplateWatchable(this.parse());
    }
    return templateWatcher;
  }

  @Override
  public void create() throws IOException {
    throw new RuntimeException("Cannot create a template file ");
  }

  @Override
  public void delete() throws IOException {
    throw new RuntimeException("Cannot delete a template file");
  }

  @Override
  public String getContent() {
    return templateObj.toString();
  }

  @Override
  public String toString() {
    return "TemplateFile{"
        + "path=" + toPath()
        + ", templateFolder=" + templateFolder
        + ", templateObj=" + templateObj
        + '}';
  }

  @Override
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exists()) {
      throw new TemplateNotFoundException(this);
    }
  }
}





