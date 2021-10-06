package gla.files.staticfile;

import gla.files.FileInfo;
import gla.folders.OutputFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.observer.FileWatchable;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent static file from static folder(s) input.
 * Observer: Input StaticFolder
 * ObservableFile: Ouput StaticFolder
 */
public class StaticFile extends FileInfo {

  private final StaticFolder staticFolder;
  private final Map<OutputFolder, StaticFileWatchable> watcherMap = new HashMap<>();

  /**
   * Constructor Static file in input.
   *
   * @param staticFolder static folder inside.
   * @param fileName     name of file
   */
  public StaticFile(StaticFolder staticFolder, String fileName) {
    super(staticFolder.toPath().resolve(fileName));
    this.staticFolder = staticFolder;
  }

  /**
   * Get watchable static file (usefull to copy static file).
   *
   * @param outputFolder output folder use to create copy
   * @return a watchable static file
   * @throws FileNotFoundException error this static file not exist
   */
  public FileWatchable getWatchable(OutputFolder outputFolder) throws FileNotFoundException {
    if (!watcherMap.containsKey(outputFolder)) {
      watcherMap.put(outputFolder, new StaticFileWatchable(this, outputFolder));
    }
    return watcherMap.get(outputFolder);
  }

  /**
   * Getter static folder.
   *
   * @return Static folder.
   */
  public StaticFolder getStaticFolder() {
    return staticFolder;
  }

  @Override
  public String toString() {
    return "StaticFile{"
        + "path=" + toPath()
        + ", staticFolder=" + staticFolder
        + ", Observer=" + super.toString()
        + '}';
  }
}