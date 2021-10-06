package gla.folders.staticfolder;

import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.staticfile.StaticFile;
import gla.folders.FileInfoFolder;
import gla.folders.OutputFolder;
import gla.observer.FileWatchable;
import gla.observer.Watcher;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Class to represent content folder.
 */
public class StaticFolder extends FileInfoFolder {
  public static final String NAME_DIR = "static";

  /**
   * Default constructor used to group folder.
   */
  protected StaticFolder() {
    super(null);
  }

  /**
   * Folder constructor.
   *
   * @param path Path of folder (without "static")
   */
  public StaticFolder(Path path) {
    super(path.resolve(NAME_DIR));
  }

  /**
   * Make a static file input.
   *
   * @param filename name of file.
   * @return FileInfo
   */
  @Override
  public FileInfo getFileInfo(String filename) {
    return getStaticFile(filename);
  }


  /**
   * To get static file contain in directory.
   *
   * @param filename name of file to get
   * @return StaticFile
   */
  public StaticFile getStaticFile(String filename) {
    return new StaticFile(this, filename);
  }


  /**
   * To get static file contain in directory (with memoization).
   *
   * @param filename name of file to get
   * @return StaticFile
   */
  public StaticFile getStaticFileMemo(String filename) {
    try {
      return (StaticFile) getFileInfoMemo(filename);
    } catch (WrongFileExtensionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * List all of static files.
   *
   * @return List
   */
  public List<StaticFile> listStaticFile() {
    try {
      return (List<StaticFile>) (List<?>) listFileInfos();
    } catch (WrongFileExtensionException e) {
      throw new RuntimeException(e); // impossible
    }
  }

  /**
   * Getter static file Watchable.
   *
   * @param fileName     name of file
   * @param outputFolder output folder used
   * @return File watchable, return null if error
   */
  public FileWatchable getStaticFileWatchable(String fileName, OutputFolder outputFolder) {
    try {
      return getStaticFileMemo(fileName).getWatchable(outputFolder);
    } catch (IOException e) {
      System.err.println("ERROR: get static - " + e);
      return null;
    }
  }

  /**
   * Get watching service.
   *
   * @param outputFolder outputFolder used to dump file.
   * @return Watching service.
   */
  public Watcher getWatcher(OutputFolder outputFolder) {
    return new StaticWatcher(this, outputFolder);
  }
}
