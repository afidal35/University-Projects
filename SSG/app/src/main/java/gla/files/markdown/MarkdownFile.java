package gla.files.markdown;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.exceptions.markdown.MarkdownFileNotFoundException;
import gla.exceptions.markdown.NoMetaDataException;
import gla.files.FileInfo;
import gla.files.html.HtmlFile;
import gla.files.template.TemplateFile;
import gla.folders.OutputFolder;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.FileWatchable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to use Markdown file.
 * Observer: Template
 * ObservableFile: html
 */
public class MarkdownFile extends FileInfo {

  public static final String EXTENSION = "md";
  private final Map<OutputFolder, MarkdownFileWatchable> watcherMap = new HashMap<>();
  private final Map<OutputFolder, HtmlFile> htmlMap = new HashMap<>();
  private final ContentFolder contentFolder;
  private MarkdownObj markdownObj = null;
  private TemplateFolder templateFolder = null;

  /**
   * Markdown file to create.
   * Parse is false
   *
   * @param contentFolder content folder contain markdown
   * @param name name of file markdown
   * @throws WrongFileExtensionException If file not terminate by .md
   */
  public MarkdownFile(ContentFolder contentFolder, String name) throws WrongFileExtensionException {
    super(contentFolder.toPath().resolve(name), EXTENSION);
    this.contentFolder = contentFolder;
  }

  /**
   * Markdown file to create.
   * Parse is false
   *
   * @param pathFile Path of file to use
   * @throws WrongFileExtensionException If file not terminate by .md
   */
  public MarkdownFile(Path pathFile) throws WrongFileExtensionException {
    this(new ContentFolder(pathFile.getParent(), false),
        pathFile.getFileName().toString());
  }


  /**
   * Markdown file to create.
   *
   * @param pathFile Path of file to use
   * @param parse    if you need to parse file
   * @throws IOException                 If file not exist
   * @throws WrongFileExtensionException If file not terminate by .md
   */
  public MarkdownFile(Path pathFile, boolean parse)
      throws WrongFileExtensionException, IOException {
    this(pathFile);
    if (parse) {
      this.parse();
    }
  }

  /**
   * Parse markdown file. (no force parsing)
   *
   * @return MarkdownFile
   * @throws IOException                 error on to open file
   * @throws WrongFileExtensionException error on parsing template file if is set
   */
  public MarkdownFile parse() throws IOException, WrongFileExtensionException {
    return parse(false);
  }

  /**
   * Update object Markdown and try to set template file if folder is set.
   *
   * @param force - force rebuild
   * @return MarkdownFile
   * @throws IOException                 error on to open file
   * @throws WrongFileExtensionException error on parsing template file if is set
   */
  public MarkdownFile parse(boolean force) throws IOException, WrongFileExtensionException {
    if (force || markdownObj == null) {
      throwIfNotExist();
      this.markdownObj = new MarkdownObj(Files.readString(toPath()));
      try {
        updateTemplateFile();
      } catch (FileNotParseException e) {
        throw new RuntimeException();
      }
    }
    return this;
  }

  /**
   * Set template folder to use.
   *
   * @param templateFolder templateFolder
   * @return MarkdownFile
   * @throws IOException error on loading templates files
   * @throws WrongFileExtensionException ""
   */
  public MarkdownFile setTemplateFolder(TemplateFolder templateFolder)
      throws IOException, WrongFileExtensionException {
    this.templateFolder = templateFolder;
    try {
      updateTemplateFile();
    } catch (FileNotParseException e) {
      // ignore
    }
    return this;
  }

  /**
   * Getter watchable file.
   *
   * @param outputFolder output folder used.
   * @return watchable object
   * @throws IOException                 error to parse markdown or not exist
   * @throws WrongFileExtensionException error to load templates
   */
  public FileWatchable getWatchable(OutputFolder outputFolder)
      throws IOException, WrongFileExtensionException {
    if (!watcherMap.containsKey(outputFolder)) {
      watcherMap.put(outputFolder, new MarkdownFileWatchable(this, outputFolder));
    }
    return watcherMap.get(outputFolder);
  }

  /**
   * Update template used.
   *
   * @throws FileNotParseException if markdown file is not `parse`
   * @throws IOException           error on loading templates files
   * @throws WrongFileExtensionException ""
   */
  public void updateTemplateFile()
      throws FileNotParseException, IOException, WrongFileExtensionException {
    try {
      toMarkdownObj().setTemplateFile(templateFolder, contentFolder);
      TemplateFile templateFile = toMarkdownObj().getTemplateFile();
      if (templateFile != null) {
        updateLastFileTime(templateFile);
      }
    } catch (NoMetaDataException e) {
      // ignore
    }
  }

  /**
   * Getter HtmlFile object with template if is possible.
   *
   * @param outputFolder output folder used
   * @return HtmlFile to create
   * @throws FileNotParseException if file is not parse before
   */

  public HtmlFile toHtmlFile(OutputFolder outputFolder)
      throws FileNotParseException {
    try {
      if (!htmlMap.containsKey(outputFolder)) {
        HtmlFile htmlFile = outputFolder.getHtmlFile(getHtmlFileName());
        htmlFile.setMarkdownFile(this);
        htmlMap.put(outputFolder, htmlFile);
      }
      return htmlMap.get(outputFolder).setMarkdownFile(this);
    } catch (NotSameNameException | WrongFileExtensionException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Markdown object.
   *
   * @return MarkdownObject to used
   * @throws FileNotParseException ""
   */
  public MarkdownObj toMarkdownObj() throws FileNotParseException {
    if (markdownObj == null) {
      throw new FileNotParseException(this);
    }
    return markdownObj;
  }

  /**
   * Getter file HTML file name.
   *
   * @return String html file name
   */
  public String getHtmlFileName() {
    return nameWithNewExtension(HtmlFile.EXTENSION);
  }

  /**
   * Getter Draft Metadata (default=false).
   *
   * @return Boolean draft.
   * @throws FileNotParseException ""
   */
  public boolean isDraft() throws FileNotParseException {
    return toMarkdownObj().isDraft();
  }

  @Override
  public String getContent() {
    try {
      return toMarkdownObj().toString();
    } catch (FileNotParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return "MarkdownFile{"
        + "path=" + toPath()
        + ", markdownObj=" + markdownObj
        + ", templateFolder=" + templateFolder
        + ", templateFile=" + markdownObj.getTemplateFile()
        + ", observable=" + super.toString()
        + '}';
  }

  @Override
  public void create() throws IOException {
    throw new RuntimeException("Cannot create a markdown file ");
  }

  @Override
  public void delete() throws IOException {
    throw new RuntimeException("Cannot delete a markdown file");
  }

  @Override
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exists()) {
      throw new MarkdownFileNotFoundException(this);
    }
  }

}
