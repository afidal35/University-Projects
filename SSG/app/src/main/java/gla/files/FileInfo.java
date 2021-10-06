package gla.files;

import gla.exceptions.files.WrongFileExtensionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * Use to implement incremental.
 */
public abstract class FileInfo {
  public static final FileTime DEFAULT_TIME_MINIMAL = FileTime.fromMillis(Long.MIN_VALUE);
  private final Path path;
  private FileTime lastUpdate;

  /**
   * FileInfo constructor with a specified path.
   *
   * @param path the path representing the file
   */
  public FileInfo(Path path) {
    this.path = path;
    updateLastFileTime(true);
  }


  /**
   * FileInfo constructor with a specified path.
   *
   * @param path      the path representing the file
   * @param extension extension to check
   * @throws WrongFileExtensionException ""
   */
  protected FileInfo(Path path, String extension) throws WrongFileExtensionException {
    this(path);
    if (!getExtension().equals(extension)) {
      throw new WrongFileExtensionException(getExtension(), extension);
    }
  }

  /**
   * Get a file extension.
   *
   * @return String representing the file extension if it exists, empty string otherwise
   */
  public String getExtension() {
    int dotIndex = getFileName().lastIndexOf('.');
    return (dotIndex == -1 || dotIndex == 0) ? "" : getFileName().substring(dotIndex + 1);
  }

  /**
   * Getter for path.
   *
   * @return path
   */
  public Path toPath() {
    return path;
  }

  /**
   * Getter File.
   *
   * @return File of FileInfo
   */
  public File toFile() {
    return path.toFile();
  }

  /**
   * Getter for file name.
   *
   * @return String name of file
   */
  public String getFileName() {
    return path.getFileName().toString();
  }

  /**
   * Check if a file exists.
   *
   * @return true if it exists, false otherwise
   */
  public boolean exists() {
    return path.toFile().exists();
  }


  /**
   * Throw if file not exist.
   *
   * @throws FileNotFoundException if file not exist.
   */
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exists()) {
      throw new FileNotFoundException(toPath().toString());
    }
  }

  /**
   * Get content of file.
   *
   * @return String content.
   * @throws IOException ""
   */
  public String getContent() throws IOException {
    return Files.readString(toPath());
  }

  /**
   * Create object with content in toString.
   *
   * @throws IOException error on creation
   */
  public void create() throws IOException {
    Files.writeString(toPath(), getContent());
    updateLastFileTime();
  }

  /**
   * Delete file.
   *
   * @throws IOException error on delete
   */
  public void delete() throws IOException {
    Files.delete(toPath());
    lastUpdate = DEFAULT_TIME_MINIMAL;
  }


  /**
   * Change a string extension representing a file.
   *
   * @param newExtension the new extension
   * @return String which has the new extension
   */
  public String nameWithNewExtension(String newExtension) {
    int dotIndex = getFileName().lastIndexOf('.');
    return (dotIndex == -1 || dotIndex == 0) ? ""
        : getFileName().substring(0, dotIndex + 1) + newExtension;
  }


  /**
   * Getter last update time.
   * This time
   *
   * @return get last time file update (null if file not exist)
   */
  public FileTime getLastUpdate() {
    return lastUpdate;
  }

  /**
   * Set last update time.
   *
   * @param lastUpdate new value.
   */
  protected void setLastUpdate(FileTime lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  /**
   * Update last time with one file.
   *
   * @param dependency dependency file
   */
  public void updateLastFileTime(FileInfo dependency) {
    if (dependency.hasBeenModifiedAfter(this)) {
      setLastUpdate(dependency.getLastUpdate());
    }
  }


  /**
   * Update with file time of file on system.
   *
   * @param force Force update if file time is null or smallest
   */
  public void updateLastFileTime(Boolean force) {
    FileTime newFileTime;
    try {
      newFileTime = Files.getLastModifiedTime(toPath());
    } catch (IOException e) {
      newFileTime = DEFAULT_TIME_MINIMAL;
    }
    if (force || newFileTime.compareTo(lastUpdate) > 0) {
      lastUpdate = newFileTime;
    }
  }


  /**
   * Update last update with system without force.
   */
  public void updateLastFileTime() {
    updateLastFileTime(false);
  }


  /**
   * Update last to time of now.
   */
  public void updateLastFileTimeNow() {
    lastUpdate = FileTime.fromMillis(System.currentTimeMillis());
  }

  /**
   * Check if a file have been modified after another one by comparing last modified dates.
   *
   * @param other the other file to compare with
   * @return true if this is older then other
   */
  public boolean hasBeenModifiedAfter(FileInfo other) {
    return getLastUpdate().compareTo(other.getLastUpdate()) >= 0;
  }


  @Override
  public String toString() {
    return "FileInfo{"
        + "path=" + path
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileInfo fileInfo = (FileInfo) o;
    return path.equals(fileInfo.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }
}
