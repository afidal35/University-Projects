package gla.folders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.exceptions.files.WrongFileExtensionException;
import gla.files.template.TemplateFile;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TemplateFolderTest {

  @TempDir
  Path tempDir;


  @Test
  void testGetterTemplate()
      throws IOException, WrongFileExtensionException {
    TemplateFolder templateFolder = new TemplateFolder(tempDir);
    templateFolder.create();
    Path pathTemplate = Files.createFile(tempDir
        .resolve(TemplateFolder.NAME_DIR)
        .resolve("template.html"));

    assertTrue(templateFolder.containTemplate("template.html"));

    TemplateFile templateFile = templateFolder.getTemplate("template.html");
    assertEquals(pathTemplate, templateFile.toPath());
    assertEquals(1, templateFolder.listFileNameHtml().size());
    assertNotNull(templateFolder.getWatcher());
  }


}
