package gla.folders;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.folders.contentfolder.ContentFolder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FolderTest {

  @TempDir
  File tempDir;

  @Test
  void testFolderPathGetter() {
    ContentFolder folder = new ContentFolder(tempDir.toPath(), false);
    assertEquals(folder.toPath().toString(), tempDir.getPath());
  }

  @Test
  void testFolderExist() {
    ContentFolder folder = new ContentFolder(tempDir.toPath(), false);
    assertTrue(folder.exist());
  }

  @Test
  void testFolderNotExist() {
    ContentFolder folder = new ContentFolder(tempDir.toPath().resolve("noExistDir"), false);
    assertFalse(folder.exist());
  }

  @Test
  void testFolderCreateNotExist() {
    ContentFolder folder = new ContentFolder(tempDir.toPath().resolve("newDir"), false);
    assertFalse(folder.exist());
    assertDoesNotThrow(folder::create);
    assertTrue(folder.exist());
  }


  @Test
  void testFolderCreateAlreadyExist() throws IOException {
    Path pathDir = tempDir.toPath().resolve("newDir");
    ContentFolder folder = new ContentFolder(pathDir, false);
    Files.createDirectories(pathDir);

    assertTrue(folder.exist());
    assertDoesNotThrow(folder::create);
  }

  @Test
  void testFolderContain() throws IOException {
    ContentFolder folder = new ContentFolder(tempDir.toPath(), false);
    Files.createFile(folder.toPath().resolve("test.md"));

    assertTrue(folder.contains("test.md"));
    assertFalse(folder.contains("test.bad"));
  }

  @Test
  void testNotExist() {
    OutputFolder folder = new OutputFolder(tempDir.toPath().resolve("noExist"));
    assertThrows(FileNotFoundException.class, folder::throwIfNotExist);
  }


  @Test
  void testDelete() throws IOException {
    OutputFolder folder = new OutputFolder(tempDir.toPath().resolve("noExist"));
    folder.create();
    assertTrue(folder.toFile().exists());
    assertTrue(folder.exist());
    folder.delete();
    assertFalse(folder.exist());
  }


}
