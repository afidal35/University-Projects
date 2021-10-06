package gla.files.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListFileReplacerTest {
  ListFileReplacer listFileReplacer;
  String htmlWithListFiles = "</head>\n<body>\n" +
      "<h1> Hello </h1>\n" +
      "{% for item in list_files(\"blog\", false) %}\n" +
      "<li>{{ post }}</li>\n{% endfor %}\n" +
      "<h2> {{ metadata.title }} </h2>\n" +
      "</body>\n</html>\n";

  @BeforeEach
  void setup() {
    listFileReplacer = new ListFileReplacer(null, htmlWithListFiles);
  }

  @Test
  void testTabFunction() {
    String[] tab = {"test1", "test2", "test3"};
    String expected = "[\"test1\", \"test2\", \"test3\"]";
    String got = listFileReplacer.getTabString(tab);

    assertEquals(expected, got);
  }

  @Test
  void testParsingListFile() {
    Matcher matcher = listFileReplacer.getMatcher();

    assertTrue(matcher.find(), "Pattern found");
    assertEquals(matcher.group(1), "blog");
    assertFalse(Boolean.parseBoolean(matcher.group(2)));
  }

  @Test
  void testHtmlContainsListFileAttribute() throws FileNotFoundException {
    ListFileReplacer listFileReplacer =
        new ListFileReplacer(null, "list_files(\"template\\listFilesInTemplate\")");
    listFileReplacer.replaceListFile();
    assertFalse(listFileReplacer.hasListFiles());
  }

  @Test
  void testHtmlNotContainsListFileAttribute() throws FileNotFoundException {
    ListFileReplacer listFileReplacer = new ListFileReplacer(null,"No list files here");
    listFileReplacer.replaceListFile();
    assertFalse(listFileReplacer.hasListFiles());
  }
}
