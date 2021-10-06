package gla.folders.contentfolder;

import gla.exceptions.files.ContentDirectoryNotFoundException;
import gla.exceptions.files.IndexFileNotFoundException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.markdown.MarkdownFile;
import gla.folders.FileInfoFolder;
import gla.folders.OutputFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.FileWatchable;
import gla.observer.Watcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ContentFolder contains markdown files.
 */
public class ContentFolder extends FileInfoFolder {

  public static final String DEFAULT_FOLDER_NAME = "content";
  public static final String MARKDOWN_EXTENSION = "md";
  public static final String INDEX_FILE_NAME = "index." + MARKDOWN_EXTENSION;

  /**
   * create content directory.
   *
   * @param path         path of file
   * @param isContentDir if true "path/content" else "path"
   */
  public ContentFolder(Path path, boolean isContentDir) {
    super(isContentDir ? path.resolve(DEFAULT_FOLDER_NAME) : path);
  }

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public ContentFolder(Path path) {
    this(path, true);
  }

  /**
   * To get File info object contain in folder.
   *
   * @param fileName name of file.
   * @return FileInfo
   */
  @Override
  public FileInfo getFileInfo(String fileName) throws WrongFileExtensionException {
    return new MarkdownFile(this, fileName);
  }

  /**
   * List Path markdown.
   *
   * @return List of Path markdown
   */
  public List<Path> listMarkdownPath() {
    return listPathWithExtension(MARKDOWN_EXTENSION);
  }


  /**
   * List filename markdown.
   *
   * @return List of String markdown
   */
  public List<String> listMarkdownFileName() {
    return listNameFilesWithExtension(MARKDOWN_EXTENSION);
  }

  /**
   * Get Markdown object.
   *
   * @param fileName Name of file to get markdown.
   * @return Markdown object
   * @throws IOException throw if cannot load markdown file.
   * @throws WrongFileExtensionException ""
   */
  public MarkdownFile getMarkdown(String fileName) throws WrongFileExtensionException, IOException {
    return new MarkdownFile(this, fileName).parse();
  }

  /**
   * Get Markdown object (with memoization).
   *
   * @param fileName Name of file to get markdown.
   * @return Markdown object
   * @throws IOException throw if cannot load markdown file.
   * @throws WrongFileExtensionException ""
   */
  public MarkdownFile getMarkdownMemo(String fileName)
      throws WrongFileExtensionException, IOException {
    return ((MarkdownFile) getFileInfoMemo(fileName)).parse();
  }

  /**
   * Get all Markdown objects.
   *
   * @return List of Markdown objects
   * @throws WrongFileExtensionException ""
   * @throws IOException ""
   */
  public List<MarkdownFile> listMarkdownFile() throws IOException, WrongFileExtensionException {
    List<MarkdownFile> markdownFiles = (List<MarkdownFile>) (List<?>) listFileInfos();
    for (MarkdownFile markdownFile : markdownFiles) {
      markdownFile.parse();
    }
    return markdownFiles;
  }

  /**
   * If directory not exist throw this exception.
   *
   * @throws FileNotFoundException throw if not exist
   */
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exist()) {
      throw new ContentDirectoryNotFoundException(this);
    }
  }

  /**
   * throw exception if index.md not exist
   *
   * @throws IndexFileNotFoundException if index.md not exist
   */
  public void throwIfIndexNotExist() throws IndexFileNotFoundException {
    if (!contains(INDEX_FILE_NAME)) {
      throw new IndexFileNotFoundException(this);
    }
  }

  /**
   * Build a markdown watchable.
   *
   * @param fileName name of markdown file
   * @param templateFolder template folder used
   * @param outputFolder output folder used
   * @return FileWatchable
   */
  public FileWatchable getMarkdownWatchable(String fileName,
                                            TemplateFolder templateFolder,
                                            OutputFolder outputFolder) {
    try {
      return (getMarkdownMemo(fileName))
          .setTemplateFolder(templateFolder)
          .getWatchable(outputFolder);

    } catch (WrongFileExtensionException | IOException e) {
      return null;
    }
  }

  /**
   * Get watching service.
   *
   * @param templateFolder templateFolder used.
   * @param outputFolder outputFolder used.
   * @return Watching service.
   */
  public Watcher getWatcher(TemplateFolder templateFolder, OutputFolder outputFolder) {
    return new ContentWatcher(this, templateFolder, outputFolder);
  }
}
