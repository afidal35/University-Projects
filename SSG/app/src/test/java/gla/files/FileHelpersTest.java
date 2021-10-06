package gla.files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileHelpersTest {

  @TempDir
  Path tmpPath;

  @AfterEach
  private void cleanTestDirectoryContent() {
    cleanUpDirectoryContent(tmpPath.toFile());
    assertTrue(Files.notExists(tmpPath));
  }

  // getFileExtension(String name) tests

  // Good

  @Test
  public void testGoodGetFileExtension_md() {
    assertEquals("md", FileHelpers.getFileExtension("index.md"));
  }

  @Test
  public void testGoodGetFileExtension_txt() {
    assertEquals("txt", FileHelpers.getFileExtension("index.txt"));
  }

  @Test
  public void testGoodGetFileExtension_html() {
    assertEquals("html", FileHelpers.getFileExtension("expected.html"));
  }

  // Bad

  @Test
  public void testBadGetFileExtension_NoDot() {
    assertEquals("", FileHelpers.getFileExtension("index"));
  }

  @Test
  public void testBadGetFileExtension_Empty() {
    assertEquals("", FileHelpers.getFileExtension(""));
  }

  @Test
  public void testBadGetFileExtension_DotLast() {
    assertEquals("", FileHelpers.getFileExtension("index."));
  }

  // substituteFileExtension(String fileName, String ext) tests

  // Good

  @Test
  public void testGoodSubstituteFileExtension_mdToHtml() {
    assertEquals("index.html",
        FileHelpers.substituteFileExtension("index.md", "html"));
  }

  @Test
  public void testGoodSubstituteFileExtension_mdToTxt() {
    assertEquals("index.txt",
        FileHelpers.substituteFileExtension("index.md", "txt"));
  }

  @Test
  public void testGoodSubstituteFileExtension_tarGzToTarTxt() {
    assertEquals("index.tar.txt",
        FileHelpers.substituteFileExtension("index.tar.gz", "txt"));
  }

  @Test
  public void testGoodSubstituteFileExtension_mdToH() {
    assertEquals("index.h",
        FileHelpers.substituteFileExtension("index.md", "h"));
  }

  // Bad

  @Test
  public void testBadSubstituteFileExtension_empty() {
    assertEquals("",
        FileHelpers.substituteFileExtension("", "html"));
  }

  @Test
  public void testBadSubstituteFileExtension_noDot() {
    assertEquals("", FileHelpers.substituteFileExtension("index", "html"));
  }

  @Test
  public void testBadSubstituteFileExtension_noFileName() {
    assertEquals("", FileHelpers.substituteFileExtension(".md", "html"));
  }



  // getSameExtensionFileList(String directoryName, List<File> files, String extension)

  // Good

  private boolean cleanUpDirectoryContent(File directoryName) {
    File[] allContents = directoryName.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        cleanUpDirectoryContent(file);
      }
    }
    return directoryName.delete();
  }

  private List<String> getFileNamesAsList(List<File> fileList) {
    List<String> fileNames = new ArrayList<>();
    for (File f : fileList) {
      fileNames.add(f.getName());
    }
    return fileNames;
  }


  private void createTestFiles(Path directory, String indexName, String menuName,
                               String testName) {
    Path index = directory.resolve(indexName);
    Path menu = directory.resolve(menuName);
    Path test = directory.resolve(testName);

    assertDoesNotThrow(
        () ->
        {
          Files.createDirectories(directory);
          Files.createFile(index);
          Files.createFile(menu);
          Files.createFile(test);
        }
    );
  }

  private void createSimpleTest_OneLevelDeep() {
    createTestFiles(tmpPath, "index.md", "menu.md", "test.md");
  }

  private void createMediumTest_TwoLevelsDeep() {
    createSimpleTest_OneLevelDeep();
    createTestFiles(tmpPath.resolve("site"), "index1.md",
        "menu1.md", "test1.md");
  }

  private void createHardTest_ThreeLevelsDeep() {
    createMediumTest_TwoLevelsDeep();
    createTestFiles(tmpPath.resolve("site").resolve("inside"),
        "index2.md", "menu2.txt", "test2.m");
  }

}