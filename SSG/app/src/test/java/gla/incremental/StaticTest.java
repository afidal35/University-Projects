package gla.incremental;

import gla.HtmlSiteBuilder;
import gla.exceptions.files.WrongFileExtensionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StaticTest {

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
  public void deleteStaticFolder() {
    File staticDirInput = new File(input.resolve("static").toString());
    File staticDirOutput = new File(output.resolve("static").toString());

    String[] entries = staticDirInput.list();
    for (String s : entries) {
      File currentFile = new File(staticDirInput.getPath(), s);
      currentFile.delete();
    }
    staticDirInput.delete();

    assertDoesNotThrow(() -> {
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });
    assertFalse(staticDirOutput.exists());
  }

  @Test
  public void deleteStaticFiles() {
    File staticFile1Input = new File(input.resolve("static/testcopy1").toString());
    File staticFile1Output = new File(output.resolve("static/testcopy1").toString());

    File staticFile2Input = new File(input.resolve("static/testcopy2").toString());
    File staticFile2Output = new File(output.resolve("static/testcopy2").toString());

    staticFile1Input.delete();
    staticFile2Input.delete();

    assertDoesNotThrow(() -> {
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });
    assertFalse(staticFile1Output.exists());
    assertFalse(staticFile2Output.exists());
  }

  @Test
  public void addStaticFiles() throws IOException {
    File staticFile3Input = new File(input.resolve("static/testcopy3").toString());

    try (FileWriter fileWriter = new FileWriter(staticFile3Input)) {
      fileWriter.write("test3");
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertDoesNotThrow(() -> {
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });

    File staticFile3Output = new File(output.resolve("static/testcopy3").toString());
    assertTrue(staticFile3Output.exists());

    byte[] inputCopy3 = Files.readAllBytes(staticFile3Input.toPath());
    byte[] outputCopy3 = Files.readAllBytes(staticFile3Output.toPath());

    assertTrue(Arrays.equals(inputCopy3, outputCopy3));
  }

  @Test
  public void modifyStaticFiles() throws IOException {
    File staticFile1Input = new File(input.resolve("static/testcopy1").toString());
    String content = "test1.bis";

    try (FileWriter fileWriter = new FileWriter(staticFile1Input)) {
      fileWriter.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertDoesNotThrow(() -> {
      HtmlSiteBuilder builder = new HtmlSiteBuilder(input.toString(), output.toString())
          .setRebuild(false);
      builder.build();
    });

    File staticFile1Output = new File(output.resolve("static/testcopy1").toString());
    assertTrue(staticFile1Output.exists());

    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(staticFile1Input))) {
      for (String line; (line = br.readLine()) != null; ) {
        sb.append(line);
      }
    }
    assertEquals(content, sb.toString());

    byte[] inputCopy1 = Files.readAllBytes(staticFile1Input.toPath());
    byte[] outputCopy1 = Files.readAllBytes(staticFile1Output.toPath());
    assertTrue(Arrays.equals(inputCopy1, outputCopy1));

  }


}
