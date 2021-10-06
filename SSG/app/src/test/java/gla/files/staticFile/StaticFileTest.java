package gla.files.staticFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.ResourcesHelper;
import gla.files.staticfile.StaticFile;
import gla.files.staticfile.StaticFileOutput;
import gla.folders.OutputFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.observer.Observer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StaticFileTest {
  @TempDir
  Path tempDir;

  @Test
  void testOutpoutStaticFile() throws IOException {
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    outputFolder.create();
    StaticFolder staticFolderInput = new StaticFolder(tempDir);
    staticFolderInput.create();
    Files.writeString(staticFolderInput.toPath().resolve("static_copy.css"), "static content");
    StaticFile staticFileInput = new StaticFile(staticFolderInput, "static_copy.css");
    StaticFileOutput staticFileOutput = new StaticFileOutput(outputFolder, staticFileInput);

    assertEquals("static content", staticFileOutput.getContent());
    assertNotNull(staticFileOutput.toString());
    assertNotNull(staticFileInput.toString());

  }
  @Test
  void testCreateCopyStaticFile() throws IOException {
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    outputFolder.create();
    StaticFolder staticFolderInput = new StaticFolder(tempDir);
    staticFolderInput.create();
    Files.createFile(staticFolderInput.toPath().resolve("static_copy.css"));
    StaticFile staticFile = new StaticFile(staticFolderInput, "static_copy.css");

    Observer observer = staticFile.getWatchable(outputFolder);

    assertFalse(outputFolder.getStaticFolder().exist());
    observer.onCreated();
    assertTrue(outputFolder.getStaticFolder().exist());
    assertTrue(outputFolder.getStaticFolder().contains("static_copy.css"));
  }



  @Test
  void testModifyOutputNotExist() throws IOException {
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    outputFolder.create();
    StaticFolder staticFolderInput = new StaticFolder(tempDir);
    staticFolderInput.create();
    Files.createFile(staticFolderInput.toPath().resolve("static_copy.css"));
    StaticFile staticFile = new StaticFile(staticFolderInput, "static_copy.css");

    Observer observer = staticFile.getWatchable(outputFolder);

    assertFalse(outputFolder.getStaticFolder().exist());
    observer.onModified();
    assertTrue(outputFolder.getStaticFolder().exist());
    assertTrue(outputFolder.getStaticFolder().contains("static_copy.css"));
    assertNotNull(staticFile.toString());
  }

  @Test
  void testDeleteCopyStaticFile() throws IOException {
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    StaticFolder staticFolderInput = new StaticFolder(tempDir);
    staticFolderInput.create();
    Files.createFile(staticFolderInput.toPath().resolve("static_copy.css"));
    StaticFile staticFile = new StaticFile(staticFolderInput, "static_copy.css");

    StaticFolder staticFolderOutput = outputFolder.getStaticFolder();
    staticFolderOutput.create();
    Files.createFile(staticFolderOutput.toPath().resolve("static_copy.css"));

    Observer observer = staticFile.getWatchable(outputFolder);

    assertTrue(outputFolder.getStaticFolder().contains("static_copy.css"));
    observer.onDeleted();
    assertFalse(outputFolder.getStaticFolder().contains("static_copy.css"));
  }

  @Test
  void testCopyStaticFileNoAscii() throws IOException {
    Path pathStatic = ResourcesHelper.getResourcePath("/staticFile/");
    StaticFolder staticFolder = new StaticFolder(pathStatic);
    StaticFile staticFile = staticFolder.getStaticFile("favicon.ico");
    OutputFolder outputFolder = new OutputFolder(tempDir);
    outputFolder.getStaticFolder().create();
    StaticFileOutput staticFileOutput = new StaticFileOutput(outputFolder, staticFile);
    assertDoesNotThrow(staticFileOutput::create);
  }
}
