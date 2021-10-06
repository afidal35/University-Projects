package gla.folders.grouper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.ResourcesHelper;
import gla.files.staticfile.StaticFile;
import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.staticfolder.StaticFolderGrouper;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class StaticGrouperTest {
  @TempDir
  Path tmpPath;

  private SiteToml helperInit(String folder) throws IOException {
    Path path = ResourcesHelper.copyResourcesFolder("/grouper/" + folder, tmpPath);
    return new InputFolder(path).getSiteToml();
  }

  @Test
  void testStaticGrouper() throws IOException {
    SiteToml siteToml = helperInit("staticGrouper");
    StaticFolderGrouper staticFolder = siteToml.getStaticFolder();
    assertEquals(1, staticFolder.listStaticFileTheme().size());
    assertTrue(staticFolder.themeIsSet());
    assertThrows(RuntimeException.class, staticFolder::toPath);
    assertEquals(2, staticFolder.listFiles().size());
    assertTrue(staticFolder.exist());
//    StaticFile staticFile = staticFolder.getStaticFileMemo("static.css");
//    assertTrue(siteToml.hasBeenModifiedAfter(staticFile));
  }

  @Test
  void testWatcherObjectExist() throws IOException {
    SiteToml siteToml = helperInit("staticGrouper");
    StaticFolder staticFolder = siteToml.getStaticFolder();
    StaticFolder staticFolderInput = siteToml.getInputFolder().getStaticFolder();
    OutputFolder outputFolder = new OutputFolder(tmpPath.resolve("_output"));

    assertNotNull(staticFolder.getWatcher(outputFolder));
    assertNotNull(staticFolderInput.getWatcher(outputFolder));

    assertNotNull(staticFolder.getStaticFileWatchable("static.css", outputFolder));
    assertNotNull(staticFolderInput.getStaticFileWatchable("static.css", outputFolder));

    assertNull(staticFolder.getStaticFileWatchable("notExist.css", outputFolder));
    assertNull(staticFolderInput.getStaticFileWatchable("notExist.css", outputFolder));
  }
}
