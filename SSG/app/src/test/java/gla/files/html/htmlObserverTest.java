package gla.files.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.files.markdown.MarkdownFile;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class htmlObserverTest {
  @TempDir
  Path tempDir;

  @Test
  void testNotifyCreate()
      throws WrongFileExtensionException, IOException, NotSameNameException, FileNotParseException {
    HtmlFile htmlFile = new HtmlFile(tempDir.resolve("file.html"));

    Path markdownPath = Files.writeString(tempDir.resolve("file.md"), "test-html simple");
    MarkdownFile markdownFile = new MarkdownFile(markdownPath, true);
    HtmlObj htmlObjExpected = markdownFile.toMarkdownObj().toHtmlObj();

    HtmlObserver observer = htmlFile.toObserver(markdownFile);

    assertFalse(htmlFile.exists());
    observer.onCreated();
    assertTrue(htmlFile.exists());
    String htmlActualContent = Files.readString(htmlFile.toPath());
    assertEquals(htmlObjExpected.toString(), htmlActualContent);
  }


  @Test
  void testNotifyDelete()
      throws WrongFileExtensionException, IOException, NotSameNameException, FileNotParseException {
    HtmlFile htmlFile = new HtmlFile(tempDir.resolve("file.html"));

    Path markdownPath = Files.writeString(tempDir.resolve("file.md"), "test-html simple");
    MarkdownFile markdownFile = new MarkdownFile(markdownPath);

    HtmlObserver observer = htmlFile.toObserver(markdownFile);

    observer.onCreated();
    assertTrue(htmlFile.exists());
    observer.onDeleted();
    assertFalse(htmlFile.exists());
  }


  @Test
  void testNotifyModifiedAndCreate()
      throws WrongFileExtensionException, IOException, NotSameNameException, FileNotParseException {
    HtmlFile htmlFile = new HtmlFile(tempDir.resolve("file.html"));

    Path markdownPath = Files.writeString(tempDir.resolve("file.md"), "test-html simple");
    MarkdownFile markdownFile = new MarkdownFile(markdownPath, true);
    HtmlObj htmlObjExpected = markdownFile.toMarkdownObj().toHtmlObj();

    HtmlObserver observer = htmlFile.toObserver(markdownFile);

    assertFalse(htmlFile.exists());
    observer.onModified();
    assertTrue(htmlFile.exists());
    String htmlActualContent = Files.readString(htmlFile.toPath());
    assertEquals(htmlObjExpected.toString(), htmlActualContent);
  }


  @Test
  void testNotifyModifiedIsDraft()
      throws WrongFileExtensionException, IOException, NotSameNameException, FileNotParseException {
    HtmlFile htmlFile = new HtmlFile(tempDir.resolve("file.html"));

    TemplateFolder templateFolder = new TemplateFolder(tempDir);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve("default.html"));

    Path markdownPath = Files.writeString(tempDir.resolve("file.md"), "test-html simple");
    MarkdownFile markdownFile = new MarkdownFile(markdownPath, true).setTemplateFolder(templateFolder);
    HtmlObj htmlObjExpected = markdownFile.toMarkdownObj().toHtmlObj();

    HtmlObserver observer = new HtmlObserver(htmlFile, markdownFile);

    assertFalse(htmlFile.exists());
    observer.onModified();
    assertTrue(htmlFile.exists());
    String htmlActualContent = Files.readString(htmlFile.toPath());
    assertEquals(htmlObjExpected.toString(), htmlActualContent);

    // change to draft
    Files.writeString(markdownPath,
        "+++\n"
        + "draft = true\n"
        + "+++\n"
        + "# Draft");

    markdownFile.parse(true);

    observer.onModified();
    assertFalse(htmlFile.exists());
  }


}
