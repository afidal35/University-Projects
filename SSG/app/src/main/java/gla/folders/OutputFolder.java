package gla.folders;

import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.html.HtmlFile;
import gla.folders.staticfolder.StaticFolder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent output folder.
 */
public class OutputFolder extends HtmlFolder {

  public static final String DEFAULT_FOLDER_NAME = "_output";
  private final StaticFolder staticFolder;

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public OutputFolder(Path path) {
    super(path);
    staticFolder = new StaticFolder(path);
  }

  /**
   * To get File info object contain in folder.
   *
   * @param fileName name of file.
   * @return FileInfo
   */
  @Override
  public FileInfo getFileInfo(String fileName) throws WrongFileExtensionException {
    return new HtmlFile(this, fileName);
  }

  /**
   * Getter StaticFolder.
   *
   * @return StaticFolder used
   */
  public StaticFolder getStaticFolder() {
    return staticFolder;
  }

  /**
   * Get html file and keep it in memory.
   *
   * @param fileName name of file to get.
   * @return HtmlFile
   * @throws WrongFileExtensionException ""
   * @throws IOException ""
   */
  public HtmlFile getHtmlFile(String fileName) throws WrongFileExtensionException, IOException {
    return (HtmlFile) getFileInfoMemo(fileName);
  }


  /**
   * List of HtmlFile already exist.
   *
   * @return list of html files
   */
  public List<HtmlFile> listHtmlFile() {
    return listFileNameHtml().stream()
        .map(name -> {
          try {
            return getHtmlFile(name);
          } catch (WrongFileExtensionException | IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());
  }


}
