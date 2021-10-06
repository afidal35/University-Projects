package gla.folders.grouper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.ResourcesHelper;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.template.TemplateFile;
import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.folders.templatefolder.TemplateFolderGrouper;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class TemplateGrouperTest {
  @TempDir
  Path tmpPath;

  private SiteToml helperInit(String folder) throws IOException {
    Path path = ResourcesHelper.copyResourcesFolder("/grouper/" + folder, tmpPath);
    return new InputFolder(path).getSiteToml();
  }

  @Test
  void testTemplateGrouper() throws IOException, WrongFileExtensionException {
    SiteToml siteToml = helperInit("templateGrouper");
    TemplateFolderGrouper templateFolder = siteToml.getTemplateFolder();
    assertNotNull(templateFolder.getGrouper());
    assertEquals(1, templateFolder.listTemplateFileTheme().size());
    assertTrue(templateFolder.themeIsSet());
    assertThrows(RuntimeException.class, templateFolder::toPath);
    assertEquals(2, templateFolder.listFiles().size());
    assertTrue(templateFolder.exist());
//    TemplateFile templateFile = templateFolder.getTemplateMemo("default.html");
//    assertTrue(siteToml.getLastUpdate().compareTo(templateFile.getLastUpdate()) >= 0);

  }

  @Test
  void testWatcherObjectExist() throws IOException {
    SiteToml siteToml = helperInit("templateGrouper");
    TemplateFolderGrouper templateFolder = siteToml.getTemplateFolder();
    TemplateFolder templateFolderInput = siteToml.getInputFolder().getTemplateFolder();

    assertNotNull(templateFolder.getWatcher());
    assertNotNull(templateFolderInput.getWatcher());

    assertNotNull(templateFolder.getTemplateFileWatchable("default.html"));
    assertNotNull(templateFolderInput.getTemplateFileWatchable("default.html"));

    assertNull(templateFolder.getTemplateFileWatchable("notExist.html"));
    assertNull(templateFolderInput.getTemplateFileWatchable("notExist.html"));
  }
}
