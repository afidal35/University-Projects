package gla.watchdog;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.Watchdog;
import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.html.HtmlFile;
import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class WatchdogTestSiteTomlFile {

  @TempDir
  Path tmpPath;

  @Test
  void testUpdateTheme()
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    SiteToml siteToml = inputFolder.getSiteToml();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    String themeName = siteToml.getThemeName();
    HtmlFile indexHtml = outputFolder.getHtmlFile("index.html");
    String contentHtml = indexHtml.getContent();


    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource("siteTheme2.toml", siteToml.toPath()));

    assertNotEquals(themeName, siteToml.getThemeName());
    assertNotEquals(contentHtml, indexHtml.getContent());
  }


  @Test
  void testSameTheme() throws InterruptedException, IOException, WrongFileExtensionException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    SiteToml siteToml = inputFolder.getSiteToml();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    String themeName = siteToml.getThemeName();
    HtmlFile indexHtml = outputFolder.getHtmlFile("index.html");
    String contentHtml = indexHtml.getContent();


    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(siteToml.toPath(), "\n", APPEND)));

    assertEquals(themeName, siteToml.getThemeName());
    assertEquals(contentHtml, indexHtml.getContent());
  }

  @Test
  void testRemoveTheme() throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    SiteToml siteToml = inputFolder.getSiteToml();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    WatchdogHelper.copyOtherResource("newTemplate.html",
        inputFolder.getTemplateFolder().toPath().resolve("default.html"));
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    assertTrue(siteToml.hasTheme());
    assertTrue(outputFolder.getStaticFolder().contains("style.css"));

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(siteToml.toPath(), "")));

    assertFalse(siteToml.hasTheme());
    assertFalse(outputFolder.getStaticFolder().contains("style.css"));
  }

  @Test
  void testAddTheme() throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    SiteToml siteToml = inputFolder.getSiteToml();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    WatchdogHelper.copyOtherResource("newTemplate.html",
        inputFolder.getTemplateFolder().toPath().resolve("default.html"));
    Files.writeString(siteToml.toPath(), "");
    siteToml.update();

    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    assertFalse(siteToml.hasTheme());
    assertFalse(outputFolder.getStaticFolder().contains("style.css"));

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(siteToml.toPath(),
            "[general]\ntheme = \"mini\"\n")));

    assertTrue(siteToml.hasTheme());
    assertTrue(outputFolder.getStaticFolder().contains("style.css"));
  }
}
