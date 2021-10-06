package gla.watchdog;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.Watchdog;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.files.html.HtmlFile;
import gla.files.html.HtmlObj;
import gla.files.template.TemplateFile;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.ThemeFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.folders.templatefolder.TemplateFolderGrouper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WatchdogTestTemplateFolder {

  @TempDir
  Path tmpPath;

  @ParameterizedTest
  @ValueSource(strings = {"default.html", "menu.html"})
  void testModifyTemplate(String templateName)
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    TemplateFolderGrouper templateFolder = inputFolder.getSiteToml().getTemplateFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    HtmlFile indexHtml = outputFolder.getHtmlFile("index.html");
    HtmlObj htmlObjCopy = new HtmlObj(indexHtml.getContent());
    TemplateFile templateFile = templateFolder.getTemplateMemo(templateName);
    String contentTemplate = templateFile.getContent();

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(templateFile.toPath(),
            "\ntest update", APPEND)));

    assertNotEquals(contentTemplate, templateFile.getContent());
    assertNotEquals(htmlObjCopy, indexHtml.toHtmlObj());
  }


  void helperAddFile(InputFolder inputFolder, TemplateFolder dest)
      throws IOException, WrongFileExtensionException, InterruptedException {
    final String fileName = "newTemplate.html";
    TemplateFolderGrouper templateFolder = inputFolder.getSiteToml().getTemplateFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    HtmlFile indexHtml = outputFolder.getHtmlFile("index.html");
    HtmlObj htmlObjCopy = new HtmlObj(indexHtml.getContent());
    assertThrows(TemplateNotFoundException.class,
        () -> templateFolder.getTemplate(fileName).exists());

    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource(fileName, dest.toPath().resolve(fileName)));

    assertTrue(templateFolder.getTemplate(fileName).exists());
    assertEquals(htmlObjCopy, indexHtml.toHtmlObj());
  }

  @Test
  void testAddDefaultTemplateNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    helperAddFile(inputFolder, inputFolder.getTemplateFolder());
  }

  @Test
  void testAddTemplateThemeNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    helperAddFile(inputFolder, themeFolder.getTemplateFolder());
  }

  @Test
  void testReplaceDefaultTemplate()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    WatchdogHelper.copyOtherResource("newTemplate.html",
        themeFolder.getTemplateFolder().toPath().resolve("default.html"));
    TemplateFolderGrouper templateFolder = inputFolder.getSiteToml().getTemplateFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    TemplateFile defaultTemplate = templateFolder.getTemplateMemo("default.html");
    assertTrue(defaultTemplate.exists());
    HtmlFile indexHtml = outputFolder.getHtmlFile("index.html");
    HtmlObj htmlObjCopy = new HtmlObj(indexHtml.getContent());

    WatchdogHelper.execWatchdog(watchdog, () -> assertDoesNotThrow(
        () -> Files.delete(defaultTemplate.toPath())));

    assertFalse(defaultTemplate.exists());
    TemplateFile newDefaultTemplate = templateFolder.getTemplateMemo("default.html");
    assertTrue(newDefaultTemplate.exists());
    assertNotEquals(htmlObjCopy, indexHtml.toHtmlObj());

  }

}
