package gla.files.staticfile;

import gla.folders.OutputFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Represent Static folder output.
 */
public class StaticFileOutput extends StaticFile {
  private final StaticFile staticFileInput;

  /**
   * Constructor.
   *
   * @param outputFolder output folder used
   * @param staticFileInput static file input to refer
   */
  public StaticFileOutput(OutputFolder outputFolder, StaticFile staticFileInput) {
    super(outputFolder.getStaticFolder(), staticFileInput.getFileName());
    this.staticFileInput = staticFileInput;
  }

  @Override
  public String getContent() throws IOException {
    return staticFileInput.getContent();
  }

  @Override
  public String toString() {
    return "StaticFileObserver{"
        + "staticFileInput=" + staticFileInput
        + '}';
  }

  @Override
  public void create() throws IOException {
    Files.copy(staticFileInput.toPath(), toPath(), StandardCopyOption.REPLACE_EXISTING);
    updateLastFileTime();
  }
}
