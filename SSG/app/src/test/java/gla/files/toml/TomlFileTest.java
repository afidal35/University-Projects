package gla.files.toml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gla.exceptions.files.ThemeFolderNotExistException;
import gla.exceptions.templates.NoMedataWithKeyException;
import gla.folders.InputFolder;
import gla.folders.ThemeFolder;
import gla.folders.contentfolder.ContentFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TomlFileTest {

  @TempDir
   Path tempDir;

  @Test
  void testTomlSiteGetTheme()
      throws IOException, NoMedataWithKeyException {
    Path tomlPath = tempDir.resolve(SiteToml.FILE_NAME);
    String content =
        "[general]\n"
            + "title = \"general title\"\n"
            + "author = \"GLA-H\"\n"
            + "theme = \"themeDir\"\n";
    Files.writeString(tomlPath, content);

    ContentFolder contentFolder = new ContentFolder(tempDir);
    contentFolder.create();
    Files.createFile(contentFolder.toPath().resolve("index.md"));
    Files.createDirectories(tempDir.resolve(ThemeFolder.DEFAULT_FOLDER_NAME).resolve("themeDir"));
    InputFolder inputFolder = new InputFolder(tempDir);

    SiteToml siteToml = inputFolder.getSiteToml();

    assertEquals(siteToml.toPath(), tomlPath);
    assertEquals(siteToml.toTomlObject().get("general.title"), "general title");
    assertEquals(siteToml.getThemeName(), "themeDir");

    assertThrows(RuntimeException.class, siteToml::create);
    assertThrows(RuntimeException.class, siteToml::delete);

    assertNotNull(siteToml.toString());
  }


  @Test
  void testTomlThemeNotExist() throws IOException {
    String content =
        "[general]\n"
            + "theme = \"themeNotExist\"\n";
    Files.writeString(tempDir.resolve(SiteToml.FILE_NAME), content);

    ContentFolder contentFolder = new ContentFolder(tempDir);
    contentFolder.create();
    Files.createFile(contentFolder.toPath().resolve("index.md"));

    assertThrows(ThemeFolderNotExistException.class, () -> new InputFolder(tempDir));

  }


}
