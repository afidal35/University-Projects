package gla.files.staticfile;

import gla.folders.OutputFolder;
import java.util.concurrent.Callable;

/**
 * To copy a static file.
 */
public class StaticBuilder implements Callable<StaticFile> {

  private final StaticFile staticFile;
  private final OutputFolder outputFolder;
  private final Boolean incremental;

  /**
   * Constructor.
   *
   * @param staticFile static file to build
   * @param outputFolder output folder used
   * @param incremental build incremental
   */
  public StaticBuilder(StaticFile staticFile,
                       OutputFolder outputFolder,
                       Boolean incremental) {
    this.staticFile = staticFile;
    this.outputFolder = outputFolder;
    this.incremental = incremental;
  }

  @Override
  public StaticFile call() throws Exception {
    staticFile.getWatchable(outputFolder).build(incremental);
    return staticFile;
  }
}
