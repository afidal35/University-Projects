package gla.files.toml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.ResourcesHelper;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.observer.Observer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TomlWatchableTest {
  @TempDir
  Path tmpPath;

  private SiteToml helperInit() throws IOException {
    Path path = ResourcesHelper.copyResourcesFolder("/toml", tmpPath);
    return new InputFolder(path).getSiteToml();
  }

  @Test
  void testNoThemeError() throws IOException {
    SiteToml siteToml = helperInit();
    OutputFolder outputFolder = new OutputFolder(tmpPath.resolve("_output"));
    outputFolder.create();

    Observer siteTomlObserver = siteToml.getObserver(outputFolder);
    assertThrows(RuntimeException.class, siteTomlObserver::onCreated);
    assertThrows(RuntimeException.class, siteTomlObserver::onDeleted);
  }

  @Test
  void testAddTheme() throws IOException {
    SiteToml siteToml = helperInit();
    OutputFolder outputFolder = new OutputFolder(tmpPath.resolve("_output"));
    outputFolder.create();

    Observer siteTomlObserver = siteToml.getObserver(outputFolder);

    assertFalse(siteToml.hasTheme());
    assertFalse(siteToml.getStaticFolder().themeIsSet());
    assertFalse(siteToml.getTemplateFolder().themeIsSet());

    Files.writeString(siteToml.toPath(), "[general]\ntheme = \"theme1\"");

    siteTomlObserver.onModified();
    assertTrue(siteToml.hasTheme());
    assertEquals("theme1", siteToml.getThemeName());
    assertTrue(siteToml.getStaticFolder().themeIsSet());
    assertTrue(siteToml.getTemplateFolder().themeIsSet());
  }


  @Test
  void testChangeTheme() throws IOException {
    SiteToml siteToml = helperInit();
    OutputFolder outputFolder = new OutputFolder(tmpPath.resolve("_output"));
    outputFolder.create();
    Files.writeString(siteToml.toPath(), "[general]\ntheme = \"theme1\"");
    siteToml.update();
    assertEquals("theme1", siteToml.getThemeName());
    assertTrue(siteToml.hasTheme());
    assertTrue(siteToml.getStaticFolder().themeIsSet());
    assertTrue(siteToml.getTemplateFolder().themeIsSet());

    Files.writeString(siteToml.toPath(), "[general]\ntheme = \"theme2\"");
    Observer siteTomlObserver = siteToml.getObserver(outputFolder);
    siteTomlObserver.onModified();

    assertTrue(siteToml.hasTheme());
    assertEquals("theme2", siteToml.getThemeName());
    assertTrue(siteToml.getStaticFolder().themeIsSet());
    assertTrue(siteToml.getTemplateFolder().themeIsSet());
  }


  @Test
  void testRemoveTheme() throws IOException {
    SiteToml siteToml = helperInit();
    OutputFolder outputFolder = new OutputFolder(tmpPath.resolve("_output"));
    outputFolder.create();
    Files.writeString(siteToml.toPath(), "[general]\ntheme = \"theme2\"");
    siteToml.update();
    assertEquals("theme2", siteToml.getThemeName());
    assertTrue(siteToml.hasTheme());
    assertTrue(siteToml.getStaticFolder().themeIsSet());
    assertTrue(siteToml.getTemplateFolder().themeIsSet());

    Files.writeString(siteToml.toPath(), "[general]\n");
    Observer siteTomlObserver = siteToml.getObserver(outputFolder);
    siteTomlObserver.onModified();

    assertFalse(siteToml.hasTheme());
    assertFalse(siteToml.getStaticFolder().themeIsSet());
    assertFalse(siteToml.getTemplateFolder().themeIsSet());
  }
}
