package gla.folders;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.HtmlSiteBuilder;
import gla.exceptions.files.ContentDirectoryNotFoundException;
import gla.exceptions.files.IndexFileNotFoundException;
import gla.exceptions.files.InputDirectoryNotFoundException;
import gla.exceptions.files.SiteTomlNotFoundException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.template.TemplateFile;
import gla.folders.templatefolder.TemplateFolder;
import gla.folders.templatefolder.TemplateFolderGrouper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class InputFolderTest {
  @TempDir
  File tempDir;

  @Test
  void testFolderInputNotExist() {
    assertThrows(InputDirectoryNotFoundException.class,
        () -> new InputFolder(tempDir.toPath().resolve("input")));
  }

  @Test
  void testContentNotExist() {
    assertThrows(ContentDirectoryNotFoundException.class, () -> new InputFolder(tempDir.toPath()));
  }

  @Test
  void testsiteTomlNotExist() throws IOException {
    Files.createDirectories(tempDir.toPath().resolve("content"));
    Files.createFile(tempDir.toPath().resolve("content").resolve("index.md"));
    assertThrows(SiteTomlNotFoundException.class, () -> new InputFolder(tempDir.toPath()));
  }

  @Test
  void testsiteIndexMdNotExist() throws IOException {
    Files.createDirectories(tempDir.toPath().resolve("content"));
    Files.createFile(tempDir.toPath().resolve("site.toml"));
    assertThrows(IndexFileNotFoundException.class, () -> new InputFolder(tempDir.toPath()));
  }

  InputFolder helperInitDirectoryInput()
      throws IOException {
    Path pathInput = tempDir.toPath().resolve("input");
    Path content_path = Files.createDirectories(pathInput.resolve("content"));
    Files.createFile(content_path.resolve("index.md"));
    Files.createFile(pathInput.resolve("site.toml"));
    return new InputFolder(pathInput);
  }

  @Test
  void testCopyStaticToOutput()
      throws IOException {
    InputFolder inputFolder = helperInitDirectoryInput();

    assertFalse(inputFolder.getTemplateFolder().exist());

    Path staticInputPath = Files.createDirectories(inputFolder.toPath().resolve("static"));
    Path staticFile1 = Files.createFile(staticInputPath.resolve("static1.css"));
    Path staticFile2 = Files.createFile(staticInputPath.resolve("static2.png"));

    OutputFolder outputFolder = new OutputFolder(tempDir.toPath().resolve("_output"));
    outputFolder.create();

    assertFalse(outputFolder.getStaticFolder().exist());

    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder);
    assertDoesNotThrow(builder::build);

    assertTrue(outputFolder.getStaticFolder().exist());
    assertTrue(outputFolder.getStaticFolder().contains(staticFile1.getFileName().toString()));
    assertTrue(outputFolder.getStaticFolder().contains(staticFile2.getFileName().toString()));
  }


  @Test
  void testGetContentIndex()
      throws IOException {
    InputFolder inputFolder = helperInitDirectoryInput();
    assertTrue(inputFolder.getContentFolder().contains("index.md"));
  }


  @Test
  void testInputTemplate()
      throws IOException, WrongFileExtensionException {
    InputFolder inputFolder = helperInitDirectoryInput();
    Path pathTemplate = Files.createDirectories(inputFolder.toPath().resolve("templates"));
    Path templatePath = Files.createFile(pathTemplate.resolve("template.html"));

    TemplateFolder templateFolder = inputFolder.getTemplateFolder();
    assertTrue(templateFolder.exist());
    TemplateFile templateFile = templateFolder.getTemplate("template.html");
    assertEquals(templatePath, templateFile.toPath());
    assertTrue(templateFolder.containTemplate("template.html"));
  }

  InputFolder helperMakeTheme() throws IOException {
    Path pathInput = tempDir.toPath().resolve("input");
    Path content_path = Files.createDirectories(pathInput.resolve("content"));
    Files.createFile(content_path.resolve("index.md"));

    Path pathTemplate = Files.createDirectories(pathInput.resolve("templates"));
    Files.createFile(pathTemplate.resolve("template_default.html"));
    Files.createFile(pathTemplate.resolve("template_override.html"));

    Path pathStatic = Files.createDirectories(pathInput.resolve("static"));
    Files.createFile(pathStatic.resolve("static_default.css"));
    Files.createFile(pathStatic.resolve("static_override.css"));

    Path pathTheme = Files.createDirectories(pathInput.resolve("themes").resolve("testTheme"));
    Path pathTemplateTheme = Files.createDirectories(pathTheme.resolve("templates"));
    Files.createFile(pathTemplateTheme.resolve("template_theme.html"));
    Files.createFile(pathTemplateTheme.resolve("template_override.html"));

    Path pathStaticTheme = Files.createDirectories(pathTheme.resolve("static"));
    Files.createFile(pathStaticTheme.resolve("static_theme.css"));
    Files.createFile(pathStaticTheme.resolve("static_override.css"));

    String content =
        "[general]\n"
            + "title = \"general title\"\n"
            + "author = \"GLA-H\"\n"
            + "theme = \"testTheme\"\n";
    Path tomlPath = pathInput.resolve("site.toml");
    Files.writeString(tomlPath, content);

    return new InputFolder(pathInput);
  }

  @Test
  void testInputTemplateWithTheme()
      throws IOException, WrongFileExtensionException {

    InputFolder inputFolder = helperMakeTheme();
    TemplateFolderGrouper templateFolder = inputFolder.getSiteToml().getTemplateFolder();
    assertTrue(templateFolder.exist());
    assertTrue(templateFolder.containTemplate("template_default.html"));
    assertTrue(templateFolder.containTemplate("template_theme.html"));

    Path pathThemeTemplate = inputFolder.toPath().resolve("themes")
        .resolve("testTheme").resolve("templates");
    assertEquals(pathThemeTemplate.resolve("template_theme.html"),
        templateFolder.getTemplate("template_theme.html").toPath());

    assertEquals(inputFolder.getTemplateFolder().toPath().resolve("template_override.html"),
        templateFolder.getTemplate("template_override.html").toPath());


    assertEquals(1, templateFolder.listTemplateFileTheme().size());
  }


  @Test
  void testInputStaticWithTheme()
      throws IOException{

    InputFolder inputFolder = helperMakeTheme();
    OutputFolder outputFolder = new OutputFolder(tempDir.toPath().resolve("_output"));

    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder);
    assertDoesNotThrow(builder::build);

    assertTrue(outputFolder.getStaticFolder().exist());
    assertTrue(outputFolder.getStaticFolder().contains("static_theme.css"));
    assertTrue(outputFolder.getStaticFolder().contains("static_default.css"));
    assertTrue(outputFolder.getStaticFolder().contains("static_override.css"));

  }
}
