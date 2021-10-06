package gla.utils;

import gla.files.FileInfo;
import java.util.concurrent.Callable;

/**
 * Callable to delete a file.
 */
public class UnusedFileDeleter implements Callable<FileInfo> {
  private final FileInfo fileInfo;

  /**
   * Constructor.
   *
   * @param fileInfo file to delete.
   */
  public UnusedFileDeleter(FileInfo fileInfo) {
    this.fileInfo = fileInfo;
  }

  @Override
  public FileInfo call() throws Exception {
    fileInfo.delete();
    Printer.fileHasBeenDeleted(fileInfo);
    return fileInfo;
  }
}
