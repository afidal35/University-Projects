package gla.files.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.FileInfo;
import gla.files.html.HtmlFile;
import gla.folders.OutputFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.FileWatchable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MarkdownWatchableTest {
  @TempDir
  Path tmpPath;

  private MarkdownFile helperInitMdTemplate() throws IOException, WrongFileExtensionException {
    String mdContent = "+++\n" +
        "template=\"default.html\"\n" +
        "+++\n" +
        "# Test watchable md";
    Path mdPath = Files.writeString(tmpPath.resolve("index.md"), mdContent);
    TemplateFolder templateFolder = new TemplateFolder(tmpPath);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve("default.html"));

    return new MarkdownFile(mdPath).setTemplateFolder(templateFolder).parse();
  }

  @Test
  void testOnCreatedNotExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertFalse(htmlFile.exists());
    assertEquals(FileInfo.DEFAULT_TIME_MINIMAL, htmlFile.getLastUpdate());

    watchable.onCreated();

    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.getLastUpdate().compareTo(markdownFile.getLastUpdate()) >= 0);
  }


  @Test
  void testOnCreatedAlreadyExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);

    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);
    htmlFile.create();
    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.hasBeenModifiedAfter(markdownFile));
    FileTime fileTimeHtml = htmlFile.getLastUpdate();

    assertEquals(fileTimeHtml, htmlFile.getLastUpdate());
    watchable.onCreated();

    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.hasBeenModifiedAfter(markdownFile));
    assertTrue(htmlFile.getLastUpdate().compareTo(fileTimeHtml) >= 0);
  }


  @Test
  void testOnModifiedNotExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertFalse(htmlFile.exists());
    assertEquals(FileInfo.DEFAULT_TIME_MINIMAL, htmlFile.getLastUpdate());

    watchable.onModified();

    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.getLastUpdate().compareTo(markdownFile.getLastUpdate()) >= 0);
  }


  @Test
  void testOnModifiedAlreadyExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertFalse(htmlFile.exists());
    htmlFile.create();
    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.getLastUpdate().compareTo(markdownFile.getLastUpdate()) >= 0);
    FileTime fileTimeHtml = htmlFile.getLastUpdate();

    watchable.onModified();

    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.hasBeenModifiedAfter(markdownFile));
    assertTrue(htmlFile.hasBeenModifiedAfter(markdownFile));
  }


  @Test
  void testOnDeleteNotExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);
    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);

    assertFalse(htmlFile.exists());
    assertEquals(FileInfo.DEFAULT_TIME_MINIMAL, htmlFile.getLastUpdate());

    watchable.onDeleted();
    assertFalse(htmlFile.exists());
  }


  @Test
  void testOnDeleteAlreadyExist() throws IOException, WrongFileExtensionException, FileNotParseException {
    MarkdownFile markdownFile = helperInitMdTemplate();
    OutputFolder outputFolder = new OutputFolder(tmpPath);
    FileWatchable watchable = markdownFile.getWatchable(outputFolder);

    HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);
    htmlFile.create();
    assertTrue(htmlFile.exists());

    assertTrue(htmlFile.hasBeenModifiedAfter(markdownFile));
    assertNotEquals(FileInfo.DEFAULT_TIME_MINIMAL, htmlFile.getLastUpdate());
    watchable.onDeleted();
    assertFalse(htmlFile.exists());
    assertEquals(FileInfo.DEFAULT_TIME_MINIMAL, htmlFile.getLastUpdate());

  }
}
