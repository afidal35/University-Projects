package gla.files.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;

public class HtmlObjTest {


  @Test
  void testHtmlObjEquals() {
    String content1 = "<p><em>The best group is group H.</em></p>\n";
    HtmlObj htmlObj1 = new HtmlObj(content1);

    String content2 = "<p>  <em>The best group is group H.</em>  </p>";
    HtmlObj htmlObj2 = new HtmlObj(content2);

    assertEquals(htmlObj1, htmlObj2);
    assertEquals(htmlObj1.hashCode(), htmlObj2.hashCode());
  }


  @Test
  void testHtmlObjNotEquals() {
    String content1 = "<p><em>The best group is group H.</em></p>\\n";
    HtmlObj htmlObj1 = new HtmlObj(content1);

    String content2 = "<p><em>The best group is NOT group H.</em></p>";
    HtmlObj htmlObj2 = new HtmlObj(content2);

    assertNotEquals(htmlObj1, htmlObj2);
    assertNotEquals(htmlObj1.hashCode(), htmlObj2.hashCode());
  }

  @Test
  void testHtmlContainDoctype() {
    String content1 = "<p><em>The best group is group H.</em></p>\\n";
    HtmlObj htmlObj1 = new HtmlObj(content1);

    // start with doc type
    assertTrue(htmlObj1.toString().startsWith(HtmlObj.DOCTYPE));
  }


  @Test
  void testHtmlDoubleDoctype() {
    String content = "<p><em>The best group is group H.</em></p>\\n";
    HtmlObj htmlObj1 = new HtmlObj(content);

    HtmlObj htmlObj2 = new HtmlObj(HtmlObj.DOCTYPE + content);

    assertEquals(htmlObj1, htmlObj2);
    assertEquals(htmlObj1.hashCode(), htmlObj2.hashCode());
  }
}
