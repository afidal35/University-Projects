package gla.files.markdown;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import gla.exceptions.templates.NoMedataWithKeyException;
import org.junit.jupiter.api.Test;

public class MetaDataTest {

  @Test
  public void testSimpleMetadata() throws NoMedataWithKeyException {
    String metaString =
        "+++\n"
            + "title = \"gla\"\n"
            + "+++\n";
    String content = metaString + "# TEST SIMPLE";

    MetaData metaData = new MetaData(content);
    assertNotNull(metaData.toString());

    assertEquals("gla", metaData.toTomlObject().get("title"));
    assertEquals(metaString.length(), metaData.end());

  }

  @Test
  public void testLargeMetadata() throws NoMedataWithKeyException {
    String metaString =
        "+++\n"
            + "title = \"Souvenirs et aventures de ma vie\"\n"
            + "date = 1903-02-13\n"
            + "author = \"Louise Michel\"\n"
            + "draft = false\n"
            + "+++\n";
    String content = metaString + "Lorem *ipsum* dolor sit amet.";

    MetaData metaData = new MetaData(content);

    assertEquals(metaString.length(), metaData.end());
    assertEquals("Souvenirs et aventures de ma vie", metaData.toTomlObject().get("title"));
    assertEquals("Louise Michel", metaData.toTomlObject().get("author"));
    assertFalse(metaData.toTomlObject().toTable().getBoolean("draft", () -> true));
    assertEquals("1903-02-13", metaData.toTomlObject().get("date"));
    assertEquals("Louise Michel", metaData.toTomlObject().toMap().get("author"));
  }

  @Test
  public void testNoContentMetadata() {
    String content = "Lorem *ipsum* dolor sit amet.";

    MetaData metaData = new MetaData(content);

    assertEquals(0, metaData.toTomlObject().size());
    assertEquals(0, metaData.end());
  }

  @Test
  public void testNotEnd() {
    String metaString =
        "+++\n"
            + "title = \"Souvenirs et aventures de ma vie\"\n"
            + "date = \"1903-02-13\"\n"
            + "author = \"Louise Michel\"\n"
            + "draft = false\n"
            + "\n";
    String content = metaString + "Lorem *ipsum* dolor sit amet.";

    MetaData metaData = new MetaData(content);
    assertEquals(0, metaData.toTomlObject().size());
    assertEquals(0, metaData.end());
  }

  @Test
  public void testMetadataEmpty() {
    String metaString =
        "+++\n+++\n";
    String content = metaString + "Lorem *ipsum* dolor sit amet.";

    MetaData metaData = new MetaData(content);
    assertEquals(0, metaData.toTomlObject().size());
    assertEquals(metaString.length(), metaData.end());
  }

  @Test
  public void testMetadataNoInStart() {
    String content =
        "aaaaa"
            + "+++\n"
            + "title = \"Souvenirs et aventures de ma vie\"\n"
            + "date = \"1903-02-13\"\n"
            + "author = \"Louise Michel\"\n"
            + "draft = false\n"
            + "+++\n";
    MetaData metaData = new MetaData(content);
    assertEquals(0, metaData.toTomlObject().size());
    assertEquals(0, metaData.end());
  }
}