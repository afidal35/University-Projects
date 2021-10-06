package gla.incremental;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import gla.HtmlSiteBuilder;
import gla.exceptions.files.WrongFileExtensionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TemplateTest {

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

  public String readFile(Path output) throws IOException {
    StringBuilder sb = new StringBuilder();

    BufferedReader br = new BufferedReader(new FileReader(output.toString()));
    String line;

    while ((line = br.readLine()) != null) {
      sb.append(line).append("\n");
    }

    sb.deleteCharAt(sb.length()-1);

    br.close();
    return sb.toString();
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
  public void deletedTemplateFolder() throws IOException {
    File templateDirInput = new File(input.resolve("templates").toString());
    File indexFileV1 = new File(output.resolve("index.html").toString());

    String[] entries = templateDirInput.list();
    for (String s : entries) {
      File currentFile = new File(templateDirInput.getPath(), s);
      currentFile.delete();
    }

    boolean isIndexDeleted = templateDirInput.delete();

    assertTrue(isIndexDeleted);
    assertFalse(templateDirInput.exists());
    assertDoesNotThrow(()->{
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });

    File indexFileV2 = new File(output.resolve("index.html").toString());

    byte[] indexFileV1byte = Files.readAllBytes(indexFileV1.toPath());
    byte[] indexFileV2byte = Files.readAllBytes(indexFileV2.toPath());

    assertTrue(Arrays.equals(indexFileV1byte, indexFileV2byte));

  }

  @Test
  public void deletedTemplateFolderModifyContent() throws IOException {
    File templateDirInput = new File(input.resolve("templates").toString());
    File indexMd = new File(input.resolve("content/index.md").toString());
    File indexFileHtml = new File(output.resolve("index.html").toString());
    File aboutFileHtml = new File(output.resolve("about.html").toString());

    String indexFileHtmlContent = readFile(indexFileHtml.toPath());
    String aboutFileHtmlContent = readFile(aboutFileHtml.toPath());

    String[] entries = templateDirInput.list();
    for (String s : entries) {
      File currentFile = new File(templateDirInput.getPath(), s);
      currentFile.delete();
    }
    boolean isIndexDeleted = templateDirInput.delete();

    try {
      Files.write(Paths.get(indexMd.toString()), "Version 2 !".getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      //exception handling left as an exercise for the reader
    }

    assertTrue(isIndexDeleted);
    assertFalse(templateDirInput.exists());
    assertDoesNotThrow(()->{
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(true);
      builder.build();
    });

    assertNotEquals(readFile(indexFileHtml.toPath()),indexFileHtmlContent);
    assertNotEquals(readFile(aboutFileHtml.toPath()),aboutFileHtmlContent);

  }


}
