package gla.files.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.files.markdown.MarkdownFile;
import gla.folders.OutputFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class htmlFileTest {
  @TempDir
  Path tempDir;

  @Test
  void testHtmlDumpWithSimpleMarkdownAndDelete()
      throws WrongFileExtensionException, IOException, NotSameNameException, FileNotParseException {
    OutputFolder outputFolder = new OutputFolder(tempDir);

    Path markdownPath = Files.writeString(tempDir.resolve("file.md"), "test-html simple");
    MarkdownFile markdownFile = new MarkdownFile(markdownPath);

    HtmlFile htmlFile = new HtmlFile(outputFolder, "file.html", markdownFile);

    assertNotNull(htmlFile.toString());

    HtmlObj htmlObjExpected = markdownFile.toMarkdownObj().toHtmlObj();
    assertEquals(htmlObjExpected, htmlFile.toHtmlObj());

    assertFalse(htmlFile.exists());
    htmlFile.create();
    assertTrue(htmlFile.exists());

    String htmlActualContent = Files.readString(htmlFile.toPath());
    assertEquals(htmlObjExpected.toString(), htmlActualContent);

    htmlFile.delete();
    assertFalse(htmlFile.exists());
  }
}
