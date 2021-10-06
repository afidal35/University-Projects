package gla.files.template;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gla.ResourcesHelper;
import gla.folders.contentfolder.ContentFolder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ListFileTest {

  private ContentFolder getContentNoRecursive(){
    Path path = ResourcesHelper.getResourcePath("/listFiles/NoRecursive/");
    return new ContentFolder(path);
  }

  private ContentFolder getContentRecursive(){
    Path path = ResourcesHelper.getResourcePath("/listFiles/Recursive/");
    return new ContentFolder(path);
  }

  @Test
  public void testListFilesNonRecursiveOnRoot() throws IOException {
    ContentFolder contentFolder = getContentNoRecursive();
    List<String> expected = new ArrayList<>(Arrays.asList("Bar.md", "Foo.md"));
    List<String> got = contentFolder.listFilesName(".", false);

    assertEquals(expected, got);
  }

  @Test
  public void testListFilesRecursiveOnRoot() throws IOException {
    ContentFolder contentFolder = getContentRecursive();
    ArrayList<String> expected = new ArrayList<>(Arrays
        .asList(
            "Bar.md",
            "Foo.md",
            "dossier/Baz.md",
            "dossier/dossier1/Baz.md",
            "dossier/dossier1/dossier2/Baz.md",
            "dossier/dossier1/dossier2/dossier3/Baz.md"
        ));
    List<String> got = contentFolder.listFilesName(".", true);

    assertEquals(expected, got);
  }

  @Test
  public void testListFilesRecursiveDossierOnRoot() throws IOException {
    ContentFolder contentFolder = getContentRecursive();
    ArrayList<String> expected = new ArrayList<>(Arrays
        .asList(
            "Baz.md",
            "dossier1/Baz.md",
            "dossier1/dossier2/Baz.md",
            "dossier1/dossier2/dossier3/Baz.md"
        ));
    List<String> got = contentFolder.listFilesName("dossier", true);

    assertEquals(expected, got);
  }


  @Test
  public void testListFilesOnFile() throws IOException {
    ContentFolder contentFolder = getContentNoRecursive();
    List<String> expected = new ArrayList<>(Arrays.asList("Bar.md"));
    List<String> got = contentFolder.listFilesName("Bar.md", false);

    assertEquals(expected, got);

  }
}
