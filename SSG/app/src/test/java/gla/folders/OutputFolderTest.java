package gla.folders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.folders.staticfolder.StaticFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OutputFolderTest {

  @TempDir
  File tempDir;


  @Test
  void testListHtmlPath() throws IOException {
    OutputFolder folder = new OutputFolder(tempDir.toPath());
    Path html1 = Files.createFile(tempDir.toPath().resolve("index1.html"));
    Path html2 = Files.createFile(tempDir.toPath().resolve("index2.html"));
    Files.createFile(tempDir.toPath().resolve("index2.blob"));
    Path staticDir = Files.createDirectories(tempDir.toPath().resolve("static"));
    Files.createFile(staticDir.resolve("style.css"));

    HashSet<Path> expectedPath = new HashSet<>(Arrays.asList(html1, html2));

    assertEquals(expectedPath, new HashSet<>(folder.listHtmlPath()));
  }


  @Test
  void testHaveStatic() throws IOException {
    OutputFolder folder = new OutputFolder(tempDir.toPath());
    Files.createFile(tempDir.toPath().resolve("index1.html"));
    Path staticDir = Files.createDirectories(tempDir.toPath().resolve("static"));
    Files.createFile(staticDir.resolve("style.css"));

    assertTrue(folder.getStaticFolder().exist());
  }


  @Test
  void testNotHaveStatic() {
    OutputFolder folder = new OutputFolder(tempDir.toPath());

    assertFalse(folder.getStaticFolder().exist());
  }


  @Test
  void testCopyStaticFile() throws IOException {
    OutputFolder output = new OutputFolder(tempDir.toPath().resolve("_output"));
    output.create();

    // InputFolder input = new InputFolder(tempDir.toPath().resolve("input"));
    // input.create();

    StaticFolder staticInputFolder = new StaticFolder(tempDir.toPath().resolve("input"));
    staticInputFolder.create();

    Files.createFile(staticInputFolder.toPath().resolve("style1.css"));
    Files.createFile(staticInputFolder.toPath().resolve("style2.css"));

    // FIXME : test fix

    // Input directory not found

    // output.copyStaticFolderFrom(input);

    // assertTrue(output.getStaticFolder().contains("style1.css"));
    // assertTrue(output.getStaticFolder().contains("style2.css"));
  }
}
