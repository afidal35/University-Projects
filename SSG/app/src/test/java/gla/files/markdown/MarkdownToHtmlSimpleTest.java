package gla.files.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;


import gla.files.html.HtmlObj;
import org.junit.jupiter.api.Test;

public class MarkdownToHtmlSimpleTest {

  void testHelper(String mdString, String htmlString) {
    MarkdownObj markdownObj = new MarkdownObj(mdString);
    HtmlObj htmlObjActual = markdownObj.toHtmlObjSimple();
    assertEquals(htmlObjActual.getRawContent(), htmlString);
    assertEquals(htmlObjActual, new HtmlObj(htmlString));
  }

  @Test
  public void testGoodParseLine_italic() {
    testHelper("*The best group is group H.*\n",
        "<p><em>The best group is group H.</em></p>\n");
  }

  @Test
  public void testGoodParseLine_bold() {
    testHelper("**The best group is still group H.**\n",
        "<p><strong>The best group is still group H.</strong></p>\n");
  }

  @Test
  public void testGoodParseLine_paragraph() {
    testHelper("The best group is group H.\n",
        "<p>The best group is group H.</p>\n");
  }

  @Test
  public void testGoodParseLine_title1() {
    testHelper("# The best group is group H",
        "<h1>The best group is group H</h1>\n");
  }

  @Test
  public void testGoodParseLine_title2() {

    testHelper("## The best group is group H",
        "<h2>The best group is group H</h2>\n");
  }

}