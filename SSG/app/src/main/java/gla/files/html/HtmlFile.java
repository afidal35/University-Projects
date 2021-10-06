package gla.files.html;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.files.FileInfo;
import gla.files.markdown.MarkdownFile;
import gla.folders.OutputFolder;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Represent Html file to render. (use builder template)
 */
public class HtmlFile extends FileInfo {
  public static final String EXTENSION = "html";

  protected MarkdownFile markdownFile = null;
  private HtmlObj htmlObj = null;

  /**
   * Constructor.
   *
   * @param path path of html file.
   * @throws WrongFileExtensionException ""
   */
  public HtmlFile(Path path) throws WrongFileExtensionException {
    super(path, EXTENSION);
  }

  /**
   * Constructor htmFile.
   *
   * @param outputFolder output folder.
   * @param name name of file
   * @throws WrongFileExtensionException ""
   */
  public HtmlFile(OutputFolder outputFolder, String name) throws WrongFileExtensionException {
    this(outputFolder.toPath().resolve(name));
  }


  /**
   * Constructor htmFile.
   *
   * @param outputFolder output folder
   * @param name         name of file
   * @param markdownFile markdown file used
   * @throws WrongFileExtensionException ""
   * @throws IOException ""
   * @throws NotSameNameException ""
   */
  public HtmlFile(OutputFolder outputFolder, String name, MarkdownFile markdownFile)
      throws WrongFileExtensionException, IOException, NotSameNameException {
    this(outputFolder, name);
    setMarkdownFile(markdownFile);
  }

  /**
   * getter Markdown file.
   *
   * @return Markdown file used
   */
  public MarkdownFile getMarkdownFile() {
    return markdownFile;
  }

  /**
   * Set markdown file dependency for html file.
   *
   * @param markdownFile markdown file to set
   * @return HtmlFile
   * @throws NotSameNameException        if name of files is not same
   * @throws IOException                 error to parse markdown file
   * @throws WrongFileExtensionException error to parse markdown file.
   */
  public HtmlFile setMarkdownFile(MarkdownFile markdownFile)
      throws NotSameNameException, IOException, WrongFileExtensionException {
    if (!markdownFile.getHtmlFileName().equals(getFileName())) {
      throw new NotSameNameException(this, markdownFile);
    }
    this.markdownFile = markdownFile.parse();
    update();
    return this;
  }

  /**
   * Update html object inside.
   */
  public void update() {
    try {
      this.htmlObj = markdownFile == null
          ? null
          : markdownFile.toMarkdownObj().toHtmlObj();
    } catch (FileNotParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Getter HtmlObject.
   *
   * @return html object
   */
  public HtmlObj toHtmlObj() {
    return htmlObj;
  }

  /**
   * True if markdown if not set or markdown is draft.
   *
   * @return boolean
   */
  public boolean markdownIsDraft() {
    try {
      return markdownFile == null || markdownFile.isDraft();
    } catch (FileNotParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get observer html to observe in markdown Watchable.
   *
   * @param markdownFile markdown product this html file
   * @return HtmlObserver ""
   * @throws NotSameNameException  error to set markdown file
   * @throws FileNotParseException error to get html object in markdown
   * @throws IOException ""
   * @throws WrongFileExtensionException ""
   */
  public HtmlObserver toObserver(MarkdownFile markdownFile)
      throws FileNotParseException, NotSameNameException, IOException, WrongFileExtensionException {
    return new HtmlObserver(this, markdownFile);
  }

  @Override
  public String getContent() {
    return (htmlObj == null || markdownIsDraft())
        ? null
        : htmlObj.toString();
  }


  @Override
  public String toString() {
    return "HtmlFile{"
        + "path=" + toPath()
        + ", markdownFile=" + markdownFile
        + ", html=" + htmlObj
        + '}';
  }
}
