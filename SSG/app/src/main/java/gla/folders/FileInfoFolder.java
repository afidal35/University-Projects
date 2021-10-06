package gla.folders;

import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represent class contain fileInfo object.
 */
public abstract class FileInfoFolder extends Folder {

  protected final Map<String, FileInfo> map = new HashMap<>();

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public FileInfoFolder(Path path) {
    super(path);
  }

  /**
   * To get File info object contain in folder.
   *
   * @param fileName name of file.
   * @return FileInfo
   * @throws WrongFileExtensionException ""
   */
  public abstract FileInfo getFileInfo(String fileName) throws WrongFileExtensionException;

  /**
   * To get object contain in file with memoization. use methode fileInfo if is not memoized.
   *
   * @param fileName name of file to get
   * @return FileInfo object
   * @throws WrongFileExtensionException ""
   */
  public synchronized FileInfo getFileInfoMemo(String fileName)
      throws WrongFileExtensionException {
    if (!map.containsKey(fileName)) {
      map.put(fileName, getFileInfo(fileName));
    }
    return map.get(fileName);
  }

  /**
   * To get object contain in file with memoization. use methode fileInfo if is not memoized.
   *
   * @param fileName name of file to get.
   * @param fileInfo file info to insert if not exit
   * @return Fileinfo
   */
  public synchronized FileInfo getFileInfoMemo(String fileName, FileInfo fileInfo) {
    if (!map.containsKey(fileName)) {
      map.put(fileName, fileInfo);
    }
    return map.get(fileName);
  }

  /**
   * Get all fileInfo contain in directory.
   *
   * @return File infos.
   * @throws WrongFileExtensionException ""
   */
  protected List<FileInfo> listFileInfos() throws WrongFileExtensionException {
    List<FileInfo> fileInfos = new ArrayList<>();
    for (File file : listFiles()) {
      if (file.isFile()) {
        fileInfos.add(getFileInfoMemo(file.getName()));
      }
    }
    return fileInfos;
  }
}
