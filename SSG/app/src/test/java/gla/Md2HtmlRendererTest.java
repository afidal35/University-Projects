package gla;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.files.WrongFileNameException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class Md2HtmlRendererTest {

  @TempDir
  Path dir_tmp;

  private Path create_dir(String dirname) {
    Path path = Path.of(dir_tmp.toString(), dirname);
    assertDoesNotThrow(() -> Files.createDirectories(path));
    return path;
  }

  // Exception throwing tests on renderHtmlFile()

  @ParameterizedTest
  @ValueSource(strings = {"index", ""})
  public void testRenderHtmlFile_ShouldThrowWrongFileNameException(String fileName)
      throws IOException {
    Path contentPath = create_dir("content");
    Path outputPath = create_dir("_output");
    String pathHtml = Paths.get(contentPath.toString(), fileName).toString();
    Files.createFile(Paths.get(contentPath.toString(), "toto.txt"));
    Md2HtmlRenderer renderer = new Md2HtmlRenderer(outputPath);
    assertThrows(WrongFileNameException.class, () -> renderer.renderHtmlFile(pathHtml));
  }

  @ParameterizedTest
  @ValueSource(strings = {"index.txt", "index.m", "index.t"})
  public void testRenderHtmlFile_ShouldThrowWrongFileExtensionException(String fileName)
      throws IOException {
    Path contentPath = create_dir("content");
    Path outputPath = create_dir("_output");
    String pathHtml = Paths.get(contentPath.toString(), fileName).toString();
    Files.createFile(Paths.get(contentPath.toString(), "toto.md"));
    Md2HtmlRenderer renderer = new Md2HtmlRenderer(outputPath);
    assertThrows(WrongFileExtensionException.class, () -> renderer.renderHtmlFile(pathHtml));
  }

  @Test
  public void testRenderHtmlFile_ShouldThrowFileNotFoundException() throws IOException {
    Path contentPath = create_dir("content");
    Path outputPath = create_dir("_output");
    String pathHtml = Paths.get(contentPath.toString(), "index.html").toString();
    Files.createFile(Paths.get(contentPath.toString(), "index1.md"));
    Md2HtmlRenderer renderer = new Md2HtmlRenderer(outputPath);
    assertThrows(FileNotFoundException.class, () -> renderer.renderHtmlFile(pathHtml));
  }


  @Test
  public void testRenderHtmlFile_Output()
      throws IOException, WrongFileExtensionException {
    Path contentPath = create_dir("content");
    Path outputPath = create_dir("_output");
    String pathHtml = Paths.get(contentPath.toString(), "index.html").toString();
    Path pathOutputHtml = Paths.get(outputPath.toString(), "index.html");
    Files.createFile(Paths.get(contentPath.toString(), "index.md"));
    Md2HtmlRenderer renderer = new Md2HtmlRenderer(outputPath);
    renderer.renderHtmlFile(pathHtml);
    //assertTrue(Files.exists(pathOutputHtml));
  }

}