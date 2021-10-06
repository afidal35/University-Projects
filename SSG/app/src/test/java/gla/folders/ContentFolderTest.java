package gla.folders;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gla.exceptions.files.IndexFileNotFoundException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.markdown.MarkdownFile;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContentFolderTest {

  @TempDir
  Path tempDir;

  ContentFolder helperTestMarkdown() throws IOException {
    ContentFolder contentFolder = new ContentFolder(tempDir);
    contentFolder.create();
    Files.createFile(contentFolder.toPath().resolve("index1.md"));
    Files.createFile(contentFolder.toPath().resolve("index2.md"));
    Files.createFile(tempDir.resolve("index.blob"));
    return contentFolder;

  }

  @Test
  void testContentFolderListMarkdownPath() throws IOException {
    ContentFolder contentFolder = helperTestMarkdown();

    List<String> expectedFilename = Arrays.asList("index1.md", "index2.md");
    HashSet<Path> expectedPath = expectedFilename.stream()
        .map((fName) -> contentFolder.toPath().resolve(fName))
        .collect(Collectors.toCollection(HashSet::new));

    assertEquals(expectedPath, new HashSet<>(contentFolder.listMarkdownPath()));
  }

  @Test
  void testContentFolderListMarkdownNameFile() throws IOException {
    ContentFolder contentFolder = helperTestMarkdown();

    HashSet<String> expectedFilename = new HashSet<>(Arrays.asList("index1.md", "index2.md"));
    assertEquals(expectedFilename, new HashSet<>(contentFolder.listMarkdownFileName()));
  }


  @Test
  void testContentFolderGetMarkdowns() throws IOException, WrongFileExtensionException {
    ContentFolder contentFolder = helperTestMarkdown();

    HashSet<String> expectedFilename = new HashSet<>(Arrays.asList("index1.md", "index2.md"));

    MarkdownFile md1obj = contentFolder.getMarkdown("index1.md");
    assertEquals("index1.md", md1obj.getFileName());

    List<MarkdownFile> markdownFiles = contentFolder.listMarkdownFile();
    assertEquals(markdownFiles.size(), 2);

    HashSet<String> filesNames = markdownFiles.stream()
        .map(MarkdownFile::getFileName)
        .collect(Collectors.toCollection(HashSet::new));

    assertEquals(expectedFilename, filesNames);

  }

  @Test
  void testGetMarkdownWatchable() throws IOException, WrongFileExtensionException {
    OutputFolder outputFolder = new OutputFolder(tempDir.resolve("_output"));
    outputFolder.create();
    TemplateFolder templateFolder = new TemplateFolder(tempDir);
    templateFolder.create();
    ContentFolder contentFolder = new ContentFolder(tempDir);
    contentFolder.create();

    Path mdPath = Files.createFile(contentFolder.toPath().resolve("index.md"));
    MarkdownFile markdownFile = contentFolder.getMarkdownMemo("index.md");

    assertEquals(mdPath, markdownFile.toPath());

    assertNotNull(contentFolder.getWatcher(templateFolder, outputFolder));

    assertNotNull(contentFolder.getMarkdownWatchable("index.md", templateFolder, outputFolder));
    assertNull(contentFolder.getMarkdownWatchable("notExist.md", templateFolder, outputFolder));
  }
  @Test
  void testContentNoContainIndexThrow() throws IOException {
    ContentFolder contentFolder = helperTestMarkdown();
    assertThrows(IndexFileNotFoundException.class, contentFolder::throwIfIndexNotExist);
  }

}
