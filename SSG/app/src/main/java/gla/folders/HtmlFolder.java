package gla.folders;


import gla.exceptions.files.WrongFileExtensionException;
import gla.files.html.HtmlFile;
import java.nio.file.Path;
import java.util.List;

/**
 * Abstract class for folder contain html files. (like output or templates)
 */
public abstract class HtmlFolder extends FileInfoFolder {

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public HtmlFolder(Path path) {
    super(path);
  }

  /**
   * List path of html files.
   *
   * @return List Html path
   */
  public List<Path> listHtmlPath() {
    return listPathWithExtension(HtmlFile.EXTENSION);
  }


  /**
   * List html file name.
   *
   * @return List Html path
   */
  public List<String> listFileNameHtml() {
    return listNameFilesWithExtension(HtmlFile.EXTENSION);
  }

}
