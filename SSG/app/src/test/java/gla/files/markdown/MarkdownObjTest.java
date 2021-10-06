package gla.files.markdown;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.markdown.NoMetaDataException;
import gla.exceptions.markdown.NoTemplateFileSetException;
import gla.exceptions.templates.NoMedataWithKeyException;
import gla.files.html.HtmlObj;
import gla.files.template.TemplateFile;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MarkdownObjTest {


  @TempDir
  Path templateDir;

  @Test
  public void testWithMetadataInfos() throws NoMetaDataException, NoMedataWithKeyException {
    String metaString =
        "+++\n"
            + "title = \"Test Metadata\"\n"
            + "date = 2021-03-05\n"
            + "author = \"bob eponge\"\n"
            + "draft = false\n"
            + "+++\n";
    String md_content = "# TEST SIMPLE";
    String content = metaString + md_content;
    HtmlObj htmlExpected = new MarkdownObj(md_content).toHtmlObjSimple();

    MarkdownObj markdownObj = new MarkdownObj(content);

    assertTrue(markdownObj.containsMetadata());
    MetaData metaData = markdownObj.getMetaData();

    assertEquals("Test Metadata", metaData.toTomlObject().get("title"));
    assertEquals("2021-03-05", metaData.toTomlObject().get("date"));
    assertEquals("bob eponge", metaData.toTomlObject().get("author"));
    assertEquals(markdownObj.toHtmlObjSimple(), htmlExpected);
    assertEquals(markdownObj.toString(), md_content);
  }

  @Test
  public void testDraft()
      throws NoMetaDataException, IOException, WrongFileExtensionException,
      NoTemplateFileSetException {
    TemplateFolder templateFolder = new TemplateFolder(templateDir);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve(TemplateFile.DEFAULT_FILE_NAME));
    String metaString =
        "+++\n"
            + "draft = true\n"
            + "+++\n";
    String md_content = "# TEST SIMPLE";
    String content = metaString + md_content;
    HtmlObj htmlExpected = new MarkdownObj(md_content).toHtmlObjSimple();

    MarkdownObj markdownObj = new MarkdownObj(content);
    markdownObj.setTemplateFile(templateFolder);

    assertTrue(markdownObj.containsMetadata());
    assertTrue(markdownObj.isDraft());
    assertEquals(markdownObj.toHtmlObjSimple(), htmlExpected);
    assertNull(markdownObj.toHtmlObjTemplate());
    assertNull(markdownObj.toHtmlObj());
  }

  @Test
  public void testTemplateWithNoMetadata()
      throws IOException {
    TemplateFolder templateFolder = new TemplateFolder(templateDir);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve("default.html"));

    String content = "# TEST TemplateNoMetada";

    MarkdownObj markdownObj = new MarkdownObj(content);

    assertFalse(markdownObj.containsMetadata());
    assertFalse(markdownObj.isDraft());
    assertDoesNotThrow(markdownObj::toHtmlObjSimple);
    assertThrows(NoMetaDataException.class, () -> markdownObj.setTemplateFile(templateFolder));
    assertDoesNotThrow(markdownObj::toHtmlObj);
  }

  @Test
  public void testTemplateEmpty()
      throws IOException, NoMetaDataException, WrongFileExtensionException,
      NoTemplateFileSetException {
    TemplateFolder templateFolder = new TemplateFolder(templateDir);
    templateFolder.create();
    Files.createFile(templateFolder.toPath().resolve("default.html"));

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

    MarkdownObj markdownObj = new MarkdownObj(content);

    assertTrue(markdownObj.containsMetadata());
    assertFalse(markdownObj.isDraft());
    assertEquals(htmlExpectedSimple, markdownObj.toHtmlObjSimple());
    markdownObj.setTemplateFile(templateFolder);
    assertEquals(htmlExpectedTemplate, markdownObj.toHtmlObjTemplate());
    assertEquals(htmlExpectedTemplate, markdownObj.toHtmlObj());
  }

}
