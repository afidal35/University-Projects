package gla.folders.grouper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.ResourcesHelper;
import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.staticfolder.StaticFolderGrouper;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GrouperTest {
  @TempDir
  Path tmpPath;
  private SiteToml helperInit(String folder) throws IOException {
    Path path = ResourcesHelper.copyResourcesFolder("/grouper/" + folder, tmpPath);
    return new InputFolder(path).getSiteToml();
  }

  @Test
  void TestNoThemeFile() throws IOException {
    SiteToml siteToml = helperInit("noTheme");
    StaticFolder staticFolderInput = siteToml.getInputFolder().getStaticFolder();
    StaticFolderGrouper staticFolder = siteToml.getStaticFolder();
    Grouper grouper = staticFolder.getGrouper();

    assertFalse(grouper.themeFolderIsSet());
    assertNull(grouper.getThemeFolder());
    assertEquals(staticFolderInput, grouper.getDefaultFolder());

    assertThrows(RuntimeException.class, grouper::toPath);
    assertEquals(1, grouper.listFiles().size());
    assertEquals(0, grouper.listFileNameThemeFiltered().size());
    assertTrue(grouper.contains("static.css"));
    assertFalse(grouper.templateFolderContain("static_copy.css"));
    assertTrue(grouper.exist());

    staticFolderInput.delete();
    assertFalse(grouper.exist());
  }


  @Test
  void TestWithThemeFile() throws IOException {
    SiteToml siteToml = helperInit("staticGrouper");
    StaticFolder staticFolderInput = siteToml.getInputFolder().getStaticFolder();
    StaticFolderGrouper staticFolder = siteToml.getStaticFolder();
    Grouper grouper = staticFolder.getGrouper();

    grouper.setThemeFolder(siteToml.getThemeFolder().getStaticFolder());

    assertTrue(grouper.themeFolderIsSet());
    assertNotNull(grouper.getThemeFolder());
    assertEquals(staticFolderInput, grouper.getDefaultFolder());

    assertThrows(RuntimeException.class, grouper::toPath);
    assertEquals(2, grouper.listFiles().size());
    assertEquals(1, grouper.listFileNameThemeFiltered().size());
    assertTrue(grouper.contains("static.css"));
    assertTrue(grouper.contains("static2.css"));
    assertTrue(grouper.templateFolderContain("static.css"));
    assertTrue(grouper.templateFolderContain("static2.css"));
    assertTrue(grouper.exist());

    staticFolderInput.delete();
    assertTrue(grouper.exist());
    assertEquals(2, grouper.listFiles().size());
    assertEquals(2, grouper.listFileNameThemeFiltered().size());
    assertTrue(grouper.contains("static.css"));
    assertTrue(grouper.contains("static2.css"));
    assertTrue(grouper.templateFolderContain("static.css"));
    assertTrue(grouper.templateFolderContain("static2.css"));

    grouper.setThemeFolder(null);
    assertFalse(grouper.themeFolderIsSet());
  }

}
