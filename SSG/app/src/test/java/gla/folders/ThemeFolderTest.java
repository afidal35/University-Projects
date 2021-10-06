package gla.folders;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.templates.TemplateNotFoundException;
import gla.files.template.TemplateFile;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ThemeFolderTest {

  @TempDir
  Path tempDir;

  @Test
  void testThemeNotExist() {
    ThemeFolder themeFolder = new ThemeFolder(tempDir, "my_theme");
    assertFalse(themeFolder.exist());
  }

  @Test
  void testThemeStatic() throws IOException {
    Path themePath = Files.createDirectories(tempDir.resolve(ThemeFolder.DEFAULT_FOLDER_NAME)
        .resolve("my_theme"));
    Files.createDirectories(themePath.resolve("static"));

    ThemeFolder themeFolder = new ThemeFolder(tempDir, "my_theme");
    assertEquals(themePath, themeFolder.toPath());
    assertTrue(themeFolder.exist());
    assertTrue(themeFolder.getStaticFolder().exist());
  }


  @Test
  void testThemeTemplate() throws IOException {
    Path themePath = Files.createDirectories(tempDir.resolve(ThemeFolder.DEFAULT_FOLDER_NAME)
        .resolve("my_theme"));
    Path templatePath = Files.createDirectories(themePath.resolve(
        TemplateFolder.NAME_DIR));
    Path templateFilePath = Files.createFile(templatePath.resolve("template.html"));

    ThemeFolder themeFolder = new ThemeFolder(tempDir, "my_theme");
    assertTrue(themeFolder.getTemplateFolder().exist());
    TemplateFolder templateFolder = themeFolder.getTemplateFolder();
    assertEquals(templatePath, themeFolder.getTemplateFolder().toPath());
    assertThrows(TemplateNotFoundException.class,
        () -> templateFolder.getTemplate("not_exist.html"));

    assertTrue(templateFolder.containTemplate("template.html"));
    TemplateFile template = assertDoesNotThrow(
        () -> templateFolder.getTemplate("template.html"));
    assertEquals(template.toPath(), templateFilePath);


  }

}
