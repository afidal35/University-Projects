package gla.files.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.markdown.MarkdownFileNotFoundException;
import gla.files.html.HtmlFile;
import gla.files.html.HtmlObj;
import gla.folders.OutputFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MarkdownFileTest {

  @TempDir
  Path dirTmp;

  @Test
  public void testTemplateFolderNotExist()
      throws Exception {
    TemplateFolder templateFolder = new TemplateFolder(dirTmp);
    OutputFolder outputFolder = new OutputFolder(dirTmp);
    String metaString =
        "+++\n"
            + "title = \"Test Metadata\"\n"
            + "date = 2021-03-05\n"
            + "author = \"bob eponge\"\n"
            + "draft = false\n"
            + "+++\n";
    String markdownContent = "# TEST SIMPLE";
    String content = metaString + markdownContent;

    HtmlObj htmlExpected = new MarkdownObj(content).toHtmlObjSimple();
    Path pathMd = Files.writeString(dirTmp.resolve("markdown.md"), content);

    MarkdownFile mdFile = new MarkdownFile(pathMd, true);
    MarkdownObj markdownObj = mdFile.toMarkdownObj();

    assertTrue(markdownObj.containsMetadata());

    assertEquals(pathMd, mdFile.toPath());
    assertEquals("markdown.md", mdFile.getFileName());
    assertEquals("markdown.html", mdFile.getHtmlFileName());
    assertFalse(mdFile.isDraft());
    assertEquals(markdownContent, mdFile.toMarkdownObj().toString());

    HtmlFile htmlFile = mdFile.toHtmlFile(outputFolder);
    assertEquals(htmlExpected, htmlFile.toHtmlObj());
    assertEquals(dirTmp.resolve("markdown.html"), htmlFile.toPath());

    mdFile.setTemplateFolder(templateFolder);
    htmlFile = mdFile.toHtmlFile(outputFolder);
    assertEquals(htmlExpected, htmlFile.toHtmlObj());
    assertEquals(dirTmp.resolve("markdown.html"), htmlFile.toPath());
    assertEquals(mdFile, new MarkdownFile(pathMd));
  }


  @Test
  public void TestTemplateHtmlProduct() throws Exception {
    TemplateFolder templateFolder = new TemplateFolder(dirTmp);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve("default.html"));
    OutputFolder outputFolder = new OutputFolder(dirTmp);

    String metaString =
        "+++\n"
            + "title = \"Test Metadata\"\n"
            + "date = 2021-03-05\n"
            + "author = \"bob eponge\"\n"
            + "draft = false\n"
            + "+++\n";
    String md_content = "# TEST SIMPLE";
    String content = metaString + md_content;
    HtmlObj htmlExpectedSimple = new MarkdownObj(md_content).toHtmlObjSimple();
    HtmlObj htmlExpectedTemplate = new HtmlObj("");

    Path pathMd = Files.writeString(dirTmp.resolve("markdown.md"), content);

    MarkdownFile mdFile = new MarkdownFile(pathMd, true);

    assertEquals(pathMd, mdFile.toPath());
    assertEquals("markdown.md", mdFile.getFileName());
    assertEquals(md_content, mdFile.getContent());
    assertEquals("markdown.html", mdFile.getHtmlFileName());
    assertFalse(mdFile.isDraft());

    HtmlFile htmlFile = mdFile.toHtmlFile(outputFolder);
    assertEquals(htmlExpectedSimple, htmlFile.toHtmlObj());
    assertEquals(dirTmp.resolve("markdown.html"), htmlFile.toPath());

    mdFile.setTemplateFolder(templateFolder);
    htmlFile = mdFile.toHtmlFile(outputFolder);
    assertEquals(htmlExpectedTemplate, htmlFile.toHtmlObj());
    assertEquals(dirTmp.resolve("markdown.html"), htmlFile.toPath());

  }

  @Test
  public void testWrongExtension() throws IOException {
    Path pathBadExtension = Files.createFile(dirTmp.resolve("markdown.bad"));
    assertThrows(WrongFileExtensionException.class, () -> new MarkdownFile(pathBadExtension, true));
  }


  @Test
  public void testFileNotParse() throws IOException, WrongFileExtensionException {
    Path pathFile = Files.createFile(dirTmp.resolve("markdown.md"));
    MarkdownFile markdownFile = new MarkdownFile(pathFile);
    assertThrows(FileNotParseException.class, markdownFile::toMarkdownObj);
  }


  @Test
  public void testCannotCreate() throws IOException, WrongFileExtensionException {
    Path pathFile = Files.createFile(dirTmp.resolve("markdown.md"));
    MarkdownFile markdownFile = new MarkdownFile(pathFile);
    assertThrows(RuntimeException.class, markdownFile::create);
  }


  @Test
  public void testCannotDelete() throws IOException, WrongFileExtensionException {
    Path pathFile = Files.createFile(dirTmp.resolve("markdown.md"));
    MarkdownFile markdownFile = new MarkdownFile(pathFile);
    assertThrows(RuntimeException.class, markdownFile::delete);
  }

  @Test
  public void testNotExist() {
    assertThrows(MarkdownFileNotFoundException.class,
        () -> new MarkdownFile(dirTmp.resolve("noExist.md"), true));
  }
}