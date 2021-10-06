package gla.watchdog;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.Watchdog;
import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.html.HtmlFile;
import gla.files.markdown.MarkdownFile;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.contentfolder.ContentFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WatchdogTestContentFolder {
  @TempDir
  Path tmpPath;

  @Test
  void testUpdateMdFile()
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ContentFolder contentFolder = inputFolder.getContentFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    MarkdownFile indexMd = contentFolder.getMarkdownMemo("index.md");
    String mdContent = indexMd.getContent();
    HtmlFile indexHtml = indexMd.toHtmlFile(outputFolder);
    String htmlContent = indexHtml.getContent();

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(indexMd.toPath(),
                                                    "**test update**", APPEND)));

    assertNotEquals(mdContent, indexMd.getContent());
    assertNotEquals(htmlContent, indexHtml.getContent());
  }

  @Test
  void testUpdateMdFileToDraft()
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ContentFolder contentFolder = inputFolder.getContentFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    final String MD_NAME = "CommonMark.md";

    WatchdogHelper.copyOtherResource("CommonMarkNoDraft.md",
        contentFolder.toPath().resolve(MD_NAME));

    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    MarkdownFile markdownFile = contentFolder.getMarkdownMemo(MD_NAME);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertFalse(markdownFile.isDraft());
    assertTrue(htmlFile.exists());

    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource("CommonMarkDraft.md",
            contentFolder.toPath().resolve(MD_NAME)));

    assertTrue(markdownFile.isDraft());
    assertFalse(htmlFile.exists());
  }

  @Test
  void testUpdateMdFileRemoveDraft()
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {

    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ContentFolder contentFolder = inputFolder.getContentFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    final String MD_NAME = "CommonMark.md";
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    MarkdownFile markdownFile = contentFolder.getMarkdownMemo(MD_NAME);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertTrue(markdownFile.isDraft());
    assertFalse(htmlFile.exists());

    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource("CommonMarkNoDraft.md",
            contentFolder.toPath().resolve(MD_NAME))
        );

    assertFalse(markdownFile.isDraft());
    assertTrue(htmlFile.exists());
  }


  @ParameterizedTest
  @ValueSource(strings={"newFileSimple.md", "newFileTemplate.md"})
  void testAddMdFileSimple(String newResource)
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {

    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ContentFolder contentFolder = inputFolder.getContentFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);
    final String mdName = "newFile.md";
    final String htmlName = "newFile.html";

    assertFalse(contentFolder.contains(mdName));
    assertFalse(outputFolder.contains(htmlName));

    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource(newResource, contentFolder.toPath().resolve(mdName)));

    assertTrue(contentFolder.contains(mdName));
    assertTrue(outputFolder.contains(htmlName));
    MarkdownFile markdownFile = contentFolder.getMarkdownMemo(mdName);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);
    assertTrue(markdownFile.exists());
    assertTrue(htmlFile.exists());
  }

  @Test
  void testRemoveFile()
      throws IOException, WrongFileExtensionException, InterruptedException, FileNotParseException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ContentFolder contentFolder = inputFolder.getContentFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    final String MD_NAME = "index.md";

    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    MarkdownFile markdownFile = contentFolder.getMarkdownMemo(MD_NAME);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);
    assertTrue(markdownFile.exists());
    assertTrue(htmlFile.exists());

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.delete(markdownFile.toPath())));

    assertFalse(markdownFile.exists());
    assertFalse(htmlFile.exists());

    }
}
