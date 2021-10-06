package gla;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.staticfolder.StaticFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HtmlSiteBuilderTest {

  // Constructors tests
  @TempDir
  Path tempDir;
  Path pathInput;

  void helpCreateContents() throws IOException {
    Path content_path = Files.createDirectories(pathInput.resolve("content"));
    Files.createFile(content_path.resolve("index.md"));

    String contentDraft = "+++\n"
        + "draft = true\n"
        + "+++\n"
        + "No a draft :(";
    Files.writeString(content_path.resolve("draft.md"), contentDraft);

    String contentSpecialTemplate = "+++\n"
        + "template = \"template_theme.html\""
        + "+++\n"
        + "Use template theme";
    Files.writeString(content_path.resolve("theme.md"), contentSpecialTemplate);

  }

  InputFolder helperMakeTheme() throws IOException {
    pathInput = tempDir.resolve("input");
    helpCreateContents();

    Path pathTemplate = Files.createDirectories(pathInput.resolve("templates"));
    Files.createFile(pathTemplate.resolve("template_default.html"));
    Files.createFile(pathTemplate.resolve("template_override.html"));
    Files.createFile(pathTemplate.resolve("default.html"));

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
  void testThemeBuild() throws IOException {
    InputFolder inputFolder = helperMakeTheme();
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));


    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder).setRebuild(true);

    assertFalse(outputFolder.exist());

    assertDoesNotThrow(builder::build);

    assertTrue(outputFolder.exist());
    assertTrue(outputFolder.getStaticFolder().exist());
    assertTrue(outputFolder.contains("index.html"));
    assertTrue(outputFolder.contains("theme.html"));
    assertFalse(outputFolder.contains("draft.html"));
    assertTrue(outputFolder.getStaticFolder().contains("static_theme.css"));
    assertTrue(outputFolder.getStaticFolder().contains("static_override.css"));
    assertTrue(outputFolder.getStaticFolder().contains("static_default.css"));
  }

  @Test
  void testDeleteStaticFile() throws IOException {
    InputFolder inputFolder = helperMakeTheme();
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    StaticFolder outputStatic = outputFolder.getStaticFolder();
    outputStatic.create();

    Path fileRm1 = Files.createFile(outputStatic.toPath().resolve("staticRm1.css"));
    Path fileRm2 = Files.createFile(outputStatic.toPath().resolve("staticRm1.png"));

    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder).setRebuild(true);
    assertDoesNotThrow(builder::build);

    assertFalse(fileRm1.toFile().exists());
    assertFalse(fileRm2.toFile().exists());
  }

  @Test
  void testDeleteHtmlFile() throws IOException {
    InputFolder inputFolder = helperMakeTheme();
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    outputFolder.create();

    Path fileRm1 = Files.createFile(outputFolder.toPath().resolve("htmlRm1.html"));
    Path fileRm2 = Files.createFile(outputFolder.toPath().resolve("htmlRm2.html"));

    assertTrue(fileRm1.toFile().exists());
    assertTrue(fileRm2.toFile().exists());

    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder).setRebuild(true);
    assertDoesNotThrow(builder::build);

    assertFalse(fileRm1.toFile().exists());
    assertFalse(fileRm2.toFile().exists());
  }

}
