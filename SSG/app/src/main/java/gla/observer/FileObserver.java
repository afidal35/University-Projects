package gla.observer;

import gla.files.FileInfo;

/**
 * File observer.
 */
public abstract class FileObserver implements Observer {
  private final FileInfo fileInfo;

  /**
   * Constructor file observer.
   *
   * @param fileInfo file to observe
   */
  public FileObserver(FileInfo fileInfo) {
    this.fileInfo = fileInfo;
  }

  /**
   * Getter file info.
   *
   * @return File info
   */
  protected FileInfo getFileInfo() {
    return fileInfo;
  }

  /**
   * Check if a file have been modified after another one by comparing last modified dates.
   *
   * @param other the other file to compare with
   * @return true if this is older then other
   */
  public boolean hasBeenModifiedAfter(FileInfo other) {
    return fileInfo.hasBeenModifiedAfter(other);
  }

  /**
   * Check if a file have been modified after another one by comparing last modified dates.
   *
   * @param other the other Observer file to compare
   * @return true if this is older then other
   */
  public boolean hasBeenModifiedAfter(FileObserver other) {
    return hasBeenModifiedAfter(other.getFileInfo());
  }

  /**
   * Update last update with system without force.
   *
   * @param dependency FileInfo dependency
   */
  public void updateLastFileTime(FileInfo dependency) {
    fileInfo.updateLastFileTime(dependency);
  }


  /**
   * Update last update with system without force.
   *
   * @param dependency FileObserver dependency
   */
  public void updateLastFileTime(FileObserver dependency) {
    updateLastFileTime(dependency.getFileInfo());
  }
}
