package gla.incremental;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import gla.HtmlSiteBuilder;
import gla.exceptions.files.IndexFileNotFoundException;
import gla.exceptions.files.SiteTomlNotFoundException;
import gla.exceptions.files.WrongFileExtensionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileMdTest {

  @TempDir
  File tempDir;

  Path input;
  Path output;

  public void printFolder(Path path) throws IOException {
    System.out.println("File in folder : " + path + " ---");
    Files.walk(path)
        .filter(Files::isRegularFile)
        .forEach(System.out::println);
    System.out.println("---");
  }

  @BeforeEach
  public void before() throws InterruptedException, IOException, WrongFileExtensionException {
    input = Files.createDirectory(tempDir.toPath().resolve("input"));
    output = Files.createDirectory(tempDir.toPath().resolve("output"));

    String inputFolder = "src/test/resources/incremental/templates2";
    try {
      FileUtils.copyDirectory(new File(inputFolder), new File(input.toString()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
        .setRebuild(false);
    builder.build();
  }

  @Test
  public void deletedSiteToml() {
    File siteTomlFile = new File(input.resolve("site.toml").toString());
    boolean isSiteTomlDeleted = siteTomlFile.delete();

    assertTrue(isSiteTomlDeleted);
    assertFalse(siteTomlFile.exists());
    assertThrows(SiteTomlNotFoundException.class,()->{
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });
  }

  @Test
  public void deletedFileIndexMd() {
    File indexFile = new File(input.resolve("content/index.md").toString());
    boolean isIndexDeleted = indexFile.delete();

    assertTrue(isIndexDeleted);
    assertFalse(indexFile.exists());
    assertThrows(IndexFileNotFoundException.class,()->{
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });
  }

}
