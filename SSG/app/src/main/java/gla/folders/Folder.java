package gla.folders;

import gla.files.FileHelpers;
import gla.files.FileInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * Help to folder usage.
 */
public abstract class Folder {

  private final Path path;

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public Folder(Path path) {
    this.path = path;
  }

  /**
   * Getter path.
   *
   * @return Path of directory
   */
  public Path toPath() {
    return path;
  }

  /**
   * Getter File.
   *
   * @return File of directory
   */
  public final File toFile() {
    return toPath().toFile();
  }

  /**
   * True if Folder exist.
   *
   * @return boolean True if exist
   */
  public boolean exist() {
    return toFile().exists();
  }

  /**
   * create folder if not exists.
   *
   * @throws IOException if IO error on creation
   */
  public final void create() throws IOException {
    if (!exist()) {
      Files.createDirectories(toPath());
    }
  }

  /**
   * Delete a file inside a Folder if it exists.
   *
   * @throws IOException if IO error on deletion
   */
  public final void delete() throws IOException {
    FileUtils.deleteDirectory(toFile());
  }

  /**
   * List of all file in directory.
   *
   * @return File list
   */
  public List<File> listFiles() {
    if (!exist()) {
      return new ArrayList<>();
    }
    return Arrays.asList(toFile().listFiles());
  }

  /**
   * List Path in directory.
   *
   * @return List of String markdown
   */
  public final List<Path> listPath() {
    return listFiles().stream()
        .map(File::toPath)
        .collect(Collectors.toList());
  }

  /**
   * List Path with extension in folder.
   *
   * @param extension Extension search
   * @return List of paths
   */
  public final List<Path> listPathWithExtension(String extension) {
    return listPath().stream()
        .filter(path -> path.toString().endsWith("." + extension))
        .collect(Collectors.toList());
  }


  /**
   * List all file names.
   *
   * @return list of all files
   */
  public final List<String> listNameFiles() {
    return listPath().stream()
        .map(path -> path.getFileName().toString())
        .collect(Collectors.toList());
  }

  /**
   * List all file names with extension.
   *
   * @param extension Extension search
   * @return list of all files match
   */
  public final List<String> listNameFilesWithExtension(String extension) {
    return listPathWithExtension(extension).stream()
        .map(path -> path.getFileName().toString())
        .collect(Collectors.toList());
  }

  /**
   * True if Directory contain filename.
   *
   * @param fileName name of file research
   * @return True if contain
   */
  public boolean contains(String fileName) {
    return exist() && listNameFiles().stream()
        .anyMatch(filename -> filename.equals(fileName));
  }

  /**
   * True if Folder contains file.
   *
   * @param file to search for
   * @return boolean, true if the folder contains it, false otherwise
   */
  public final boolean contains(FileInfo file) {
    return contains(file.getFileName());
  }

  /**
   * If directory not exist throw this exception.
   *
   * @throws FileNotFoundException throw if not exist
   */
  public void throwIfNotExist() throws FileNotFoundException {
    if (!exist()) {
      throw new FileNotFoundException(toPath().toString());
    }
  }


  /**
   * List files in directory relative to "content".
   *
   * @param filePath - path relative to "content"
   *                 - If it is a Test, set path to the full path after the directory "resources"
   * @param rec      - search recursively or not
   * @return - list of files present in path directory
   * @throws FileNotFoundException ""
   */
  public List<String> listFilesName(String filePath, boolean rec)
      throws FileNotFoundException {
    File file = toPath().resolve(filePath).toFile();
    if (!file.exists()) {
      throw new FileNotFoundException("Cannot open file " + filePath);
    } else if (file.isFile()) {
      return new ArrayList<>(Collections.singleton(filePath));
    }
    List<String> res = listFilesName(file, rec, "");
    Collections.sort(res);
    return res;
  }

  /**
   * List file name depth first in specified file.
   *
   * @param baseFile - the directory or file to work on
   * @param rec  - search recursively or not
   * @return - list of files present in file directory
   */
  private static List<String> listFilesName(File baseFile, boolean rec, String parent) {
    List<String> res = new ArrayList<>();
    for (File file : Objects.requireNonNull(baseFile.listFiles())) {
      if (file.isDirectory() && rec) {
        res.addAll(listFilesName(file, true, parent + file.getName() + "/"));
      } else {
        res.add(parent + file.getName());
      }
    }
    return res;
  }
}
